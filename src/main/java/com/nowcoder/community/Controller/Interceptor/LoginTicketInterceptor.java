package com.nowcoder.community.Controller.Interceptor;

import com.nowcoder.community.DAO.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取登录凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 根据 ticket 查询登录凭证
            LoginTicket loginTicket = loginTicketMapper.selectLoginTicketByTicket(ticket);

            if (loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                // 获取用户信息 - 注意：这里应该传递 int 类型，而不是 String
                User user = userService.findUserById(loginTicket.getUserId());
                // 暂存 hostHolder
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        //将用户信息传递给视图
        if (modelAndView != null) {
            User user = hostHolder.getUser();
            if (user != null) {
                modelAndView.addObject("loginUser", user);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        //清理资源

    }
}