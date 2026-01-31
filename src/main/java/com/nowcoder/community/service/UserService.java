package com.nowcoder.community.service;

import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(String id) {
        return userMapper.selectById(Integer.parseInt(id));
    }
}
