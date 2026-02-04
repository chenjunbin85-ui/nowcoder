package com.nowcoder.community.config;

import com.nowcoder.community.Controller.Interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.Controller.Interceptor.LoginTicketInterceptor;
import com.nowcoder.community.annotation.LoginRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
    //头像上传后需要配置静态资源映射才能正确显示
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传路径配置正确
        System.out.println("配置的上传路径: " + uploadPath);

        // 检查路径是否存在，如果不存在则创建
        File avatarDir = new File(uploadPath + "/avatar");
        if (!avatarDir.exists()) {
            boolean created = avatarDir.mkdirs();
            System.out.println("创建头像目录: " + created + " 路径: " + avatarDir.getAbsolutePath());
        }

        // 映射上传的图片文件
        String resourceLocation = "file:" + uploadPath + "/avatar/";
        System.out.println("资源映射位置: " + resourceLocation);

        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations(resourceLocation);
    }
}
