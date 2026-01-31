package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;
@Component
public class CommunityUtil {
    //随机字符串
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    //Md5加密
    public String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
}
