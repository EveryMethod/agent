package com.ai.agent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/17 12:24
 */
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String message = "你好啊，我是小明";
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChat(message, chatId);
        // 第二轮
        message = "我想要我的另一半小美更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "我的另一半叫什么来着，我刚刚给你说了，你回忆一下";
        answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String message = "你好啊，我是小明，我想要我的另一半小美更爱我，但是我不知道该怎么办";
        String chatId = UUID.randomUUID().toString();
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String message = "我已经结婚了，但是婚后关系不太亲密，该怎么办？";
        String chatId = UUID.randomUUID().toString();
        System.out.println(loveApp.doChatWithRag(message, chatId));
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
/*        String message = "我的另一半居住在北京市海淀区，请帮我找出5公里内适合约会的3个地点";
        String chatId = UUID.randomUUID().toString();
        String result = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(result);*/

        String message = "帮我找一些哄另一半开心的图片";
        String chatId = UUID.randomUUID().toString();
        String result = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(result);
    }

}