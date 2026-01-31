package com.nowcoder.community.Controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
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
    public String getLoginPage(Model model, User user) {
        return "site/login";
    }
}
