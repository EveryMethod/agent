package com.ai.agent.controller;

import com.ai.agent.agent.AnManus;
import com.ai.agent.app.LoveApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @author alh
 * @description ai服务
 * @date 2026/4/24 17:05
 */
@RestController
@RequestMapping("/ai/loveApp")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final ToolCallback[] allTools;

    private final ChatModel dashscopeChatModel;

    /**
     * loveApp
     */
    private final LoveApp loveApp;

    @GetMapping("/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        try {
            log.info("com.ai.agent.controller.doChatWithLoveAppSync message:{} chatId:{}", message, chatId);
            return loveApp.doChat(message, chatId);
        } catch (Exception e) {
            log.error("com.ai.agent.controller.doChatWithLoveAppSync error:{}", e.getMessage());
            return "error";
        }
    }

    /**
     * sse
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * sse
     */
    @GetMapping(value = "/chat/sse/event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * sse
     */
    @GetMapping(value = "/chat/sse/emitter")
    public SseEmitter doChatWithAppServerSseEmitter(String message, String chatId) {
        // 获取Flux响应式数据里并且直接通过订阅模式推送给SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L);
        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (Exception e) {
                        sseEmitter.completeWithError(e);
                        log.error("com.ai.agent.controller.doChatWithAppServerSseEmitter error:{}", e.getMessage());
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message 消息
     * @return SseEmitter
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        return new AnManus(allTools, dashscopeChatModel).runStream(message);
    }
}
