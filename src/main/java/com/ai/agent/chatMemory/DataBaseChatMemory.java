//package com.ai.agent.chatMemory;
//
//import com.ai.agent.converter.MessageConverter;
//import com.ai.agent.entity.ChatMessage;
//import com.ai.agent.mapper.ChatMessageRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.messages.Message;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @author alh
// * @description 基于数据库的会话存储
// * @date 2026/4/17 17:54
// */
//@Component
//@RequiredArgsConstructor
//public class DataBaseChatMemory implements ChatMemory {
//
//    private final ChatMessageRepository chatMessageRepository;
//
//    @Override
//    public void add(String conversationId, Message message) {
//    }
//
//    @Override
//    public void add(String conversationId, List<Message> messages) {
//        List<ChatMessage> chatMessageList = messages.stream()
//                .map(message -> MessageConverter.convertToChatMessage(message, conversationId)
//                ).toList();
//        chatMessageRepository.saveAll(chatMessageList,chatMessageList.size());
//    }
//
//    @Override
//    public List<Message> get(String conversationId, int lastN) {
//        return List.of();
//    }
//
//    @Override
//    public void clear(String conversationId) {
//
//    }
//}
