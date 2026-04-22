//package com.ai.agent.converter;
//
//import org.springframework.ai.chat.messages.*;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author alh
// * @description 消息转换
// * @date 2026/4/17 17:45
// */
//public class MessageConverter {
//    /**
//     * 将消息转换为ChatMessage
//     *
//     * @param message 消息
//     * @param conversationId 会话ID
//     * @return ChatMessage
//     */
//    public static ChatMessage convertToChatMessage(Message message, String conversationId) {
//        return ChatMessage.builder()
//                .conversationId(conversationId)
//                .messageType(message.getMessageType())
//                .content(message.getText())
//                .metadata(message.getMetadata())
//                .build();
//    }
//
//    /**
//     * 将ChatMessage转换为消息
//     *
//     * @param chatMessage ChatMessage
//     * @return 消息
//     */
//    public static Message convertToMessage(ChatMessage chatMessage) {
//        MessageType messageType = chatMessage.getMessageType();
//        Map<String, Object> metadata = chatMessage.getMetadata();
//        String content = chatMessage.getContent();
//        return switch (messageType) {
//            case USER -> new UserMessage(content);
//            case SYSTEM -> new SystemMessage(content);
//            case ASSISTANT -> new AssistantMessage(content,metadata);
//            case TOOL -> new ToolResponseMessage(List.of(), metadata);
//        };
//    }
//}
