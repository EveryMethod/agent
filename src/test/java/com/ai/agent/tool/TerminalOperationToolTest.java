package com.ai.agent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/20 22:18
 */
class TerminalOperationToolTest {

    @Test
    void executeTerminalCommand() {
        String result = new TerminalOperationTool().executeTerminalCommand("dir");
        System.out.println(result);
    }
}