package com.ai.agent.tool;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @author alh
 * @description 终止工具
 * @date 2023/10/26 14:09
 */
public class TerminateTool {
  
    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)  
    public String doTerminate() {  
        return "任务结束";  
    }  
}
