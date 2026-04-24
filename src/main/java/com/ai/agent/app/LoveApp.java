package com.ai.agent.app;

import com.ai.agent.advisor.MyLoggerAdvisor;
import com.ai.agent.advisor.ReReadingAdvisor;
import com.ai.agent.chatMemory.FileBaseChatMemory;
import com.ai.agent.rag.LoveAppRagCustomAdvisorFactory;
import com.ai.agent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author alh
 * @description 恋爱助手app
 * @date 2026/4/17 11:58
 */
@Slf4j
@Component
public class LoveApp {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT = """
            你将扮演恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。
            围绕单身、恋爱、已婚三种状态提问:单身状态询问社交圈拓展及追求心仪对象的困扰；
            恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题；
            引导用户详述事情经过，对方反应及自身想法，以便给出专属解决方案。
            """;

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private VectorStore pgVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 构造函数
     *
     * @param dashscopeChatModel 模型
     */
    public LoveApp(ChatModel dashscopeChatModel/*, DataBaseChatMemory chatMemory*/) {
        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "tmp/chat-memory";
//        FileBaseChatMemory chatMemory = new FileBaseChatMemory(fileDir);
        // 初始化内存对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        // 创建聊天客户端
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
//                        // 自定义日志拦截器
                        new MyLoggerAdvisor()
//                        // 自定义重读拦截器
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 聊天
     *
     * @param message 消息
     * @param chatId 会话ID
     * @return 回复
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.debug("chatResponse: {}", content);
        return content;
    }

    record LoveReport (String title, List<String> suggestion){}

    /**
     * 聊天并生成恋爱报告
     * @param message 消息
     * @param chatId 会话ID
     * @return 恋爱报告
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    /**
     * rag对话增强
     * @param message 用户消息
     * @param chatId 会话ID
     * @return 回答内容
     */
    public String doChatWithRag(String message, String chatId) {
        // 优化后的用户提示词
        String reMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(reMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                // 应用rag知识库问答(基于内存的向量存储)
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用rag知识库问答(基于pgsql数据库的向量存储)
//                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                // 应用自定义的rag检索增强策略
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                loveAppVectorStore, "单身"
                        )
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("chatResponse: {}", content);
        return content;
    }


    /**
     * 恋爱报告功能(支持调用工具)
     *
     * @param message 消息
     * @param chatId 会话ID
     * @return 回复
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("chatResponse: {}", content);
        return content;
    }

    /**
     * 恋爱报告功能(支持调用mcp)
     *
     * @param message 消息
     * @param chatId 会话ID
     * @return 回复
     */
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("chatResponse: {}", content);
        return content;
    }


    /**
     * 流式聊天
     *
     * @param message 消息
     * @param chatId 会话ID
     * @return 回复
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }

}
