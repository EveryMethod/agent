package com.ai.agent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/22 17:53
 */
@SpringBootTest
class AnManusTest {

    @Resource
    private AnManus anManus;

    @Test
    void run() {
        String userPrompt = """
                我的另一半住在北京市海淀区，请帮我找到5公里内合适的约会地点，
                并结合一些网络图片，指定一份详细的约会计划，
                并以 PDF 格式输出
                """;
        String answer = anManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}