package com.nowcoder.community.DAO;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByName(String name);
    User selectByEmail(String email);
    int create(User user);

    // ❌ 错误的：XML中的updateStatus需要两个参数
    // int updateStatus(User user);

    // ✅ 正确的：需要两个参数
    int updateStatus(@Param("id") int id, @Param("status") int status);
    int updatePassword(@Param("id") int id, @Param("password") String password);
    int updateHeadUrl(@Param("id") int id, @Param("headUrl") String headUrl);
    int deleteByName(String name);
    int deleteById(int id);
}