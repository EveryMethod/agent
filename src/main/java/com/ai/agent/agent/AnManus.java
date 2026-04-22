package com.ai.agent.agent;

import com.ai.agent.advisor.MyLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * @author alh
 * @description 我的 AI 超级智能体(拥有自主规划能力)
 * @date 2026/4/22 17:36
 */
@Component
@Slf4j
public class AnManus extends ToolCallAgent{

    public AnManus(ToolCallback[] availableTools, ChatModel dashScopeChatmodel) {
        super(availableTools);
        this.setName("AnManus");
        String systemPrompt = """  
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        this.setSystemPrompt(systemPrompt);
        String nextStepPrompt = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        this.setNextStepPrompt(nextStepPrompt);
        this.setMaxStep(20);
        this.setChatClient(ChatClient.builder(dashScopeChatmodel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build());
    }
}
