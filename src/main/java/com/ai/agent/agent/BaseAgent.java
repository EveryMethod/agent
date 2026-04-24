package com.ai.agent.agent;

import cn.hutool.core.util.StrUtil;
import com.ai.agent.agent.model.AgentStateEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author alh
 * @description 抽象基础代理类，用于管理代理状态和执行流程
 *              提供状态转换，内存管理和基于步骤的执行循环的基础功能
 * @date 2026/4/22 15:19
 */
@Data
@Slf4j
public abstract class BaseAgent {

    /**
     * 代理名称
     */
    private String name;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 下一步提示词
     */
    private String nextStepPrompt;

    /**
     * 代理状态
     */
    private AgentStateEnum state = AgentStateEnum.IDLE;

    /**
     * 当前步骤
     */
    private int currentStep = 0;

    /**
     * 最大步骤数
     */
    private int maxStep = 10;

    /**
     * 聊天客户端
     */
    private ChatClient chatClient;

    /**
     * Memory 记忆 需要自己维护上下文
     */
    private List<Message> messageList = new ArrayList<>();


    /**
     * 运行代理
     * @param userPrompt 用户提示词
     * @return 代理的响应
     */
    public String run(String userPrompt) {
        // 基础校验
        if (this.state != AgentStateEnum.IDLE) {
            throw new RuntimeException("Cannot run agent when it is not idle" + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("User prompt cannot be blank");
        }
        // 执行，更改状态
        this.state = AgentStateEnum.RUNNING;
        // 记录消息的上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        ArrayList<String> results = new ArrayList<>();
        // 执行循环
        try {
            for (int i = 0; i < maxStep && this.state != AgentStateEnum.FINISHED; i++) {
                currentStep = i + 1;
                log.info("Executing step {}/{}", currentStep, maxStep);
                // 单步执行
                String stepResult = step();
                String result = "Step " + (i + 1) + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxStep) {
                state = AgentStateEnum.FINISHED;
                results.add("Reached maximum steps limit");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentStateEnum.ERROR;
            log.error("error executing agent", e);
            return "执行错误：" + e.getMessage();
        } finally {
            // 清理资源
            cleanUp();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter实例
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        // 5分钟超时
        SseEmitter emitter = new SseEmitter(300000L);

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentStateEnum.IDLE) {
                    emitter.send("错误：无法从状态运行代理: " + this.state);
                    emitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                // 更改状态
                state = AgentStateEnum.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));

                try {
                    for (int i = 0; i < maxStep && state != AgentStateEnum.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxStep);

                        // 单步执行
                        String stepResult = step();
                        String result = "Step " + stepNumber + ": " + stepResult;

                        // 发送每一步的结果
                        emitter.send(result);
                    }
                    // 检查是否超出步骤限制
                    if (currentStep >= maxStep) {
                        state = AgentStateEnum.FINISHED;
                        emitter.send("执行结束: 达到最大步骤 (" + maxStep + ")");
                    }
                    // 正常完成
                    emitter.complete();
                } catch (Exception e) {
                    state = AgentStateEnum.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    // 清理资源
                    this.cleanUp();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            this.state = AgentStateEnum.ERROR;
            this.cleanUp();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentStateEnum.RUNNING) {
                this.state = AgentStateEnum.FINISHED;
            }
            this.cleanUp();
            log.info("SSE connection completed");
        });

        return emitter;
    }


    /**
     * 定义单个步骤
     * @return 代理的响应
     */
    public abstract String step();


    protected void cleanUp() {
        // 清理内存
    }

}
