package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail(){
        mailClient.sendEmail("1097690400@qq.com","qq mail 测试","hello world!");
    }

    @Test
    public void testSendHtmlMail(){
        //context 的作用 context存储的各变量的值，以便templateEngine.process()处理
        Context context = new Context();
        context.setVariable("name", "1097690400@qq.com");
        //process() 将模板文件和数据结合，生成最终HTML内容的渲染引擎
        // 参数说明：
        //第一个参数 "/demo/mail"：模板文件路径
        //第二个参数 context：包含模板变量的上下文对象
        //返回值 htmlContent：渲染后的HTML字符串
        String htmlContent = templateEngine.process("/demo/mail", context);
        mailClient.sendEmail("1097690400@qq.com","HTML",htmlContent);
    }
}
