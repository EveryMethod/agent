package com.ai.agent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ai.agent.agent.model.AgentStateEnum;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alh
 * @description 工具调用
 * @date 2026/4/22 16:31
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ToolCallAgent extends ReActAgent{

    /**
     * 可用工具列表
     */
    private ToolCallback[] availableTools;

    /**
     * 保存工具调用信息的响应结果(要调用那些工具)
     */
    private ChatResponse chatResponse;

    /**
     * 工具调用管理器
     */
    private final ToolCallingManager toolCallingManager;

    /**
     * 禁用spring ai 内置的工具调用机制，自己维护选项和上下文
     */
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }


    @Override
    public boolean think() {
        // 校验提示词，拼接提示词
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            // 将下一步的提示词添加到上下文中
            getMessageList().add(new UserMessage(getNextStepPrompt()));
        }
        List<Message> messageList = getMessageList();
        // 调用 ai ,获取要调用的工具列表
        try {
            ChatResponse response = getChatClient().prompt(new Prompt(messageList, this.chatOptions))
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于下一步 act
            this.chatResponse = response;
            // 解析结果，获取要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = response.getResult().getOutput();
            // 工具调用列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            // 输出提示信息
            String result = assistantMessage.getText();
            log.info("{}的思考结果: {}", getName(), result);
            log.info("{}选择了{}个工具", getName(), toolCalls.size());
            String toolCallInfo = toolCalls.stream()
                    .map(toolCall -> String.format("工具调用: %s, 参数: %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info("{}的工具调用信息: {}", getName(), toolCallInfo);
            if (CollectionUtil.isNotEmpty(toolCalls)) {
                // 需要调用工具的时候，无需记录助手消息，因为调用工具的时候会自动记录
                return true;
            } else {
                // 不需要调用工具，手动记录助手消息到上下文中
                getMessageList().add(assistantMessage);
                return false;
            }
        } catch (Exception e) {
            log.error("{}调用ai失败，reason: {}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("处理发生错误，" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if (!chatResponse.hasToolCalls()) {
            return "没有工具需要调用";
        }
        // 调用工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(new Prompt(getMessageList(), this.chatOptions), chatResponse);
        // 记录消息上下文
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        boolean match = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (match) {
            // 终止当前对话
            setState(AgentStateEnum.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + "返回的结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info("{}的工具调用结果: {}", getName(), results);
        return results;
    }
}
