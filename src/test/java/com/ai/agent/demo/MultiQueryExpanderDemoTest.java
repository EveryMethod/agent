package com.ai.agent.demo;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/20 14:54
 */
@SpringBootTest
class MultiQueryExpanderDemoTest {

    @Resource
    private MultiQueryExpanderDemo demo;

    @Test
    void expand() {
        List<Query> expand = demo.expand(new Query("啥是程序员鱼皮啊啊啊啊啊啊，请你回答啊啊啊啊"));
        Assertions.assertNotNull(expand);
    }
}