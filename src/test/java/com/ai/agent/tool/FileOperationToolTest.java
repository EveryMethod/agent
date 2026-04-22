package com.ai.agent.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/20 21:02
 */
class FileOperationToolTest {

    @Test
    void readFile() {
        String result = new FileOperationTool().readFile("test.txt");
        System.out.println(result);
        Assertions.assertNotNull(result, "File read successfully from test.txt");
        Assertions.assertFalse(result.isEmpty(), "File content is not empty");
    }

    @Test
    void writeFile() {
        String result = new FileOperationTool().writeFile("test.txt", "Hello, World!");
        Assertions.assertNotNull(result, "File written successfully to test.txt");
    }
}