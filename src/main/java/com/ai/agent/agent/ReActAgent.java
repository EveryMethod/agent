package com.ai.agent.agent;

import com.ai.agent.agent.model.AgentStateEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alh
 * @description ReAct (Reasoning and Acting) 模式的代理抽象类
 *              实现类思考-行动的循环模式
 * @date 2026/4/22 15:56
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {

    /**
     * 代理执行步骤
     * @return 执行结果
     */
    @Override
    public String step() {
        try {
            // 先思考
            boolean shouldAct = think();
            if (!shouldAct) {
                setState(AgentStateEnum.FINISHED);
                return "思考完成，无需下一步行动";
            }
            return act();
        } catch (Exception e) {
            log.error("代理执行出错", e);
            return "代理执行出错" + e.getMessage();
        }
    }

    /**
     * 思考方法，由实现类完成具体实现  处理当前状态并决定下一步行动
     * @return 是否继续执行 true 表示继续执行，false 表示不需要执行
     */
    public abstract boolean think();

    /**
     * 行动方法，由实现类完成具体实现  执行具体行动
     * @return 行动结果
     */
    public abstract String act();
}
