package com.nowcoder.community.Controller;

import com.nowcoder.community.DAO.LoginTicketMapper;
import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(@CookieValue(value = "ticket", required = false) String ticket,
                                 Model model) {
        // 先检查ticket是否存在
        if (ticket == null || ticket.isEmpty()) {
            return "redirect:/login";
        }

        // 用ticket获取当前登录用户
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicketByTicket(ticket);
        if (loginTicket == null) {
            return "redirect:/login";
        }

        User user = userService.findUserById(loginTicket.getUserId());
        if (user == null) {
            return "redirect:/login";
        }

        // 将用户信息放入model，供页面显示
        model.addAttribute("user", user);
        return "site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.POST)
    public String handleSettingPost(
            // 获取_method参数，用于区分操作类型
            @RequestParam(value = "_method", required = false) String method,

            // 以下是各种可能的参数，根据method的值决定使用哪些
            @RequestParam(value = "avatar", required = false) MultipartFile file,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,

            @CookieValue(value = "ticket", required = false) String ticket,
            Model model, Principal principal,
            RedirectAttributes redirectAttributes) {  // 添加这个参数

        // 根据_method参数的值决定调用哪个方法
        if ("avatar".equals(method)) {
            // 调用头像上传逻辑，传递redirectAttributes
            return uploadAvatar(file, ticket, model, principal, redirectAttributes);
        } else if ("password".equals(method)) {
            // 调用修改密码逻辑
            return modifyPassword(oldPassword, newPassword, confirmPassword, model, ticket);
        }

        // 如果_method参数不存在或值不正确，重定向到设置页面
        return "redirect:/setting";
    }

    /*
    上传头像
    */
    private String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                @CookieValue(value = "ticket", required = false) String ticket,
                                Model model, Principal principal, RedirectAttributes redirectAttributes) {
        // 先检查ticket是否存在
        if (ticket == null || ticket.isEmpty()) {
            return "redirect:/login";
        }

        // 用ticket获取当前登录用户
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicketByTicket(ticket);
        if (loginTicket == null) {
            return "redirect:/login";
        }

        User loginUser = userService.findUserById(loginTicket.getUserId());
        //用户未登录
        if (loginUser == null) {
            return "redirect:/login";
        }

        System.out.println("当前用户更改前头像Url:"+loginUser.getHeaderUrl());
        // 调用Service上传头像
        Map<String, Object> result = userService.uploadAvatar(loginUser, file);
        System.out.println("当前用户更改后头像Url:"+loginUser.getHeaderUrl());
        if (result.containsKey("success")) {
            String newHeaderUrl = (String) result.get("headerUrl");
            loginUser.setHeaderUrl(newHeaderUrl);

            // 更新数据库中的用户头像
            userService.updateUserHeaderUrl(loginUser.getId(), newHeaderUrl);

            // 添加成功消息到会话中，避免重定向后丢失
            redirectAttributes.addFlashAttribute("avatarSuccessMsg", "头像上传成功！");
            model.addAttribute("avatarSuccessMsg", "头像上传成功！");

            // 重定向到设置页面，让页面重新加载用户数据
            return "redirect:/setting";
        } else {
            // 上传失败
            model.addAttribute("user", loginUser);
            model.addAttribute("avatarErrorMsg", result.get("error"));
            return "site/setting";
        }
    }

    /*
    修改密码
    */
    private String modifyPassword(
            String oldPassword,
            String newPassword,
            String confirmPassword,
            Model model,
            @CookieValue(value = "ticket", required = false) String ticket) {

        // 1. 验证两次输入的密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordMsg", "两次输入的密码不一致！");
            return "site/setting";
        }

        // 2. 验证密码长度
        if (newPassword.length() < 8) {
            model.addAttribute("passwordMsg", "密码长度不能小于8位！");
            return "site/setting";
        }

        // 3. 获取用户ID
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicketByTicket(ticket);
        if (loginTicket == null) {
            model.addAttribute("passwordMsg", "请先登录！");
            return "site/login";
        }

        // 4. 修改密码
        Map<String, Object> map = userService.modifyPassword(loginTicket.getUserId(), oldPassword, newPassword);

        // 5. 处理结果
        if (map == null || map.isEmpty()) {
            // 添加成功消息到会话中
            model.addAttribute("successMsg", "密码修改成功");
            return "redirect:/setting";
        } else {
            model.addAttribute("passwordMsg", map.get("passwordMessage"));
            return "site/setting";
        }
    }
}