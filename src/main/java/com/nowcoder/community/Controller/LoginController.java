package com.nowcoder.community.Controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.nowcoder.community.DAO.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private DefaultKaptcha producer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    // GET 请求：显示注册页面
    @RequestMapping(path="/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }

    // POST 请求：处理注册表单提交
    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String getRegisterPage(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            //model.addAttribute()实现前后端数据交互
            model.addAttribute("msg","注册成功我们已经向您的邮箱发送一封注册邮件,请尽快完成激活");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMessage"));
            model.addAttribute("passwordMsg",map.get("passwordMessage"));
            model.addAttribute("emailMsg",map.get("emailMessage"));

            return "site/register";
        }
    }

    @RequestMapping(path="/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login";
    }

    // POST 请求：处理登录表单提交
    @RequestMapping(path="/login",method = RequestMethod.POST)
    public String getLoginPage(String username, String password, String code,@RequestParam(value = "rememberme")boolean rememberMe,Model model, HttpServletResponse response, HttpSession session) {
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isEmpty(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确");
            return "site/login";
        }
        int expired = rememberMe ? CommunityConstant.REMEMBER_EXPIRED_SECONDS: CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> check = userService.login(username, password, expired);
        //将cookies发送给客户端
        if (check.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", check.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        else{
            model.addAttribute("usernameMsg",check.get("usernameMessage"));
            model.addAttribute("passwordMsg",check.get("passwordMessage"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(value = "ticket", required = false) String ticket,
                         HttpServletResponse response) {

        // 1. 验证 ticket 是否存在
        if (ticket != null && !ticket.isEmpty()) {
            // 2. 从数据库中获取登录凭证
            LoginTicket loginTicket = loginTicketMapper.selectLoginTicketByTicket(ticket);

            if (loginTicket != null) {
                // 3. 更新数据库中 ticket 的状态为无效（1表示无效）
                loginTicket.setStatus(1);
                loginTicketMapper.updateStatus(loginTicket);

                System.out.println("用户ID: " + loginTicket.getUserId() + " 已登出，ticket: " + ticket);
            } else {
                System.out.println("登出时未找到有效的ticket: " + ticket);
            }

            // 4. 清除客户端 Cookie
            Cookie cookie = new Cookie("ticket", null);
            cookie.setPath(contextPath != null ? contextPath : "/");
            cookie.setMaxAge(0); // 立即过期
            response.addCookie(cookie);

            // 5. 可选：添加一个额外的Cookie清除，确保所有可能的路径都被清除
            Cookie cookie2 = new Cookie("ticket", null);
            cookie2.setPath("/");
            cookie2.setMaxAge(0);
            response.addCookie(cookie2);
        } else {
            System.out.println("登出时未找到ticket Cookie");
        }

        // 6. 重定向到登录页面（或首页）
        return "redirect:/login";
    }

    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha", text);
        //图片输出给浏览器
        response.setContentType("image/png");
        try{
            OutputStream os=response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            System.out.println("响应验证码失败"+e.getMessage());
        }
    }
}
