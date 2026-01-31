package com.nowcoder.community.service;

import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CommunityUtil communityUtil;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private MailClient mailClient;

    public User findUserById(String id) {
        return userMapper.selectById(Integer.parseInt(id));
    }

    public Map<String,Object> register(User user) {
        //存错误信息
        Map<String,Object> map = new HashMap<String,Object>();
        if(user==null){
            throw new NullPointerException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMessage","账号不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMessage","密码不能为空");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMessage","邮箱不能为空");
        }
        //验证账号
        if (userMapper.selectByName(user.getUsername())!=null){
            map.put("usernameMessage","该账号已存在");
        }
        if (userMapper.selectByEmail(user.getEmail())!=null){
            map.put("emailMessage","该邮箱已被注册");
        }

        //如果存在验证错误，直接返回，不执行后续操作
        if (!map.isEmpty()) {
            return map;
        }

        user.setSalt(communityUtil.generateUUID().substring(0,5));
        user.setPassword(communityUtil.md5(user.getSalt()+user.getPassword()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(communityUtil.generateUUID().substring(0,6));
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        userMapper.create(user);
        //map为空则表单信息没问题

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("code",user.getActivationCode());
        String url=domain+"community/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        String template=templateEngine.process("/mail/activation",context);
        mailClient.sendEmail(user.getEmail(),"激活邮件",template);
        return map;
    }

}
