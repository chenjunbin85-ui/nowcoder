package com.nowcoder.community.service;

import com.nowcoder.community.DAO.LoginTicketMapper;
import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private MailClient mailClient;

    public User findUserById(String id) {
        return userMapper.selectById(Integer.parseInt(id));
    }

    public User findUserById(int id) {
        return userMapper.selectById(id);
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
        user.setStatus(1);
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

    public Map<String,Object> login(String username,String password,int expiredSeconds) {
        Map<String,Object> map = new HashMap<>();
        User user = userMapper.selectByName(username);
        if (StringUtils.isBlank(username)) {
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        //验证账号
        if (user==null){
            map.put("usernameMessage","账号不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMessage", "该账号未激活");
            return map;
        }
        if (!Objects.equals(communityUtil.md5(user.getSalt()+password),user.getPassword())) {
            map.put("passwordMessage", "密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(communityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds* 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public Map<String,Object> modifyPassword(int userId,String inputOldPassword,String inputNewPassword) {
        HashMap<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(userId);
        if (user == null) {
            System.out.println("错误: 用户不存在");
            map.put("passwordMessage", "用户不存在");
            return map;
        }
        // 验证原密码
        String encryptedOldPassword = communityUtil.md5(user.getSalt() + inputOldPassword);
        String storedPassword = user.getPassword();
        if (!Objects.equals(encryptedOldPassword, storedPassword)) {
            System.out.println("错误: 原密码验证失败");
            map.put("passwordMessage", "原密码错误");
            return map;
        }
        // 更新密码
        String encryptedNewPassword = communityUtil.md5(user.getSalt() + inputNewPassword);
        System.out.println("加密后的新密码: " + encryptedNewPassword);
        userMapper.updatePassword(userId, encryptedNewPassword);
        System.out.println("密码更新成功");
        return map; // 空map表示成功
    }

    public void updateUserHeaderUrl(int userId, String headerUrl) {
        userMapper.updateHeadUrl(userId, headerUrl);
    }

    public Map<String, Object> uploadAvatar(User user, MultipartFile file) {
        Map<String, Object> map = new HashMap<>();

        try {
            // 1. 验证参数
            validateUploadParams(user, file, map);
            if (map.containsKey("error")) {
                return map;
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = generateUniqueFilename(suffix);

            // 3. 确定保存路径
            String savePath = determineSavePath();
            System.out.println("图片保存路径："+savePath);

            // 4. 保存文件
            File destFile = saveFile(file, savePath, newFilename);

            // 5. 生成访问URL
            String headerUrl = generateHeaderUrl(newFilename);
            System.out.println("访问Url"+headerUrl);

            // 6. 更新数据库
            int rows = updateUserHeader(user.getId(), headerUrl);
            if (rows <= 0) {
                // 数据库更新失败，删除已保存的文件
                if (destFile.exists()) {
                    destFile.delete();
                }
                map.put("error", "更新用户头像失败");
                return map;
            }

            // 7. 更新用户对象
            user.setHeaderUrl(headerUrl);

            // 8. 返回成功结果
            map.put("success", true);
            map.put("message", "头像上传成功");
            map.put("headerUrl", headerUrl);
            map.put("filename", newFilename);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "上传过程中发生错误: " + e.getMessage());
        }

        return map;
    }

    /**
     * 验证上传参数
     */
    private void validateUploadParams(User user, MultipartFile file, Map<String, Object> map) {
        if (user == null) {
            map.put("error", "用户未登录");
            return;
        }

        if (file == null || file.isEmpty()) {
            map.put("error", "请选择要上传的图片");
            return;
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            map.put("error", "文件名为空");
            return;
        }

        // 检查文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!isValidImageSuffix(suffix)) {
            map.put("error", "不支持的文件格式，请上传图片文件（JPG, JPEG, PNG, GIF, BMP, WebP）");
            return;
        }

        // 检查文件大小（限制为5MB）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            map.put("error", "文件大小不能超过5MB");
            return;
        }

        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            map.put("error", "文件类型不是图片");
            return;
        }
    }

    /**
     * 检查是否是有效的图片后缀
     */
    private boolean isValidImageSuffix(String suffix) {
        return suffix.matches("(?i)\\.(jpg|jpeg|png|gif|bmp|webp)$");
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFilename(String suffix) {
        // 使用UUID + 时间戳确保唯一性
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        return uuid + "_" + timestamp + suffix;
    }

    /**
     * 确定文件保存路径
     */
    private String determineSavePath() {
        // 优先使用配置文件中的路径
        if (uploadPath != null && !uploadPath.trim().isEmpty()) {
            return uploadPath + File.separator + "avatar";
        }

        // 默认使用项目根目录下的 upload/avatar 目录
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "upload" + File.separator + "avatar";
    }

    /**
     * 保存文件到磁盘
     */
    private File saveFile(MultipartFile file, String savePath, String filename) throws IOException {
        // 创建目录（如果不存在）
        File directory = new File(savePath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("创建上传目录失败: " + savePath);
            }
        }

        // 保存文件
        File destFile = new File(savePath + File.separator + filename);
        file.transferTo(destFile);

        return destFile;
    }

    /**
     * 生成头像访问URL
     */
    private String generateHeaderUrl(String filename) {
        // 构建完整的访问URL
        String url = domain + contextPath + "upload/avatar/" + filename;
        return url;
    }

    /**
     * 更新用户头像URL
     */
    private int updateUserHeader(int userId, String headerUrl) {
        return userMapper.updateHeadUrl(userId, headerUrl);
    }

}
