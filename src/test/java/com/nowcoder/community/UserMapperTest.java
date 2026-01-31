package com.nowcoder.community;

import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper() {
        System.out.println("=== 开始测试 MyBatis CRUD ===");

        // 1. 测试插入
        User user = new User();
        user.setUsername("testuser_" + System.currentTimeMillis()); // 唯一用户名
        user.setPassword("123456");
        user.setSalt("abcde");
        user.setEmail("test_" + System.currentTimeMillis() + "@example.com"); // 唯一邮箱
        user.setType(0);
        user.setStatus(1);
        user.setActivationCode("testcode");
        user.setHeaderUrl("http://test.com/header.jpg");
        user.setCreateTime(new Date());

        System.out.println("插入前用户ID: " + user.getId());

        int insertResult = userMapper.create(user);
        System.out.println("插入结果: " + insertResult);
        System.out.println("插入后的ID: " + user.getId());

        // 2. 测试查询
        User userById = userMapper.selectById(user.getId());
        System.out.println("根据ID查询: " + userById);

        User userByName = userMapper.selectByName(user.getUsername());
        System.out.println("根据用户名查询: " + userByName);

        // 3. 测试更新 - 注意：需要两个参数
        int updateResult = userMapper.updateStatus(user.getId(), 2); // 更新状态为2
        System.out.println("更新状态结果: " + updateResult);

        // 验证更新
        User updatedUser = userMapper.selectById(user.getId());
        System.out.println("更新后的用户: " + updatedUser);

//        // 4. 测试删除
//        int deleteResult = userMapper.deleteById(user.getId());
//        System.out.println("删除结果: " + deleteResult);
//
//        // 验证删除
//        User deletedUser = userMapper.selectById(user.getId());
//        System.out.println("删除后查询结果: " + deletedUser); // 应该是null

        System.out.println("=== 测试结束 ===");
    }
}