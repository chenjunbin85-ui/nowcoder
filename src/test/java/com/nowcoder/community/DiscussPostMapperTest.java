package com.nowcoder.community;

import com.nowcoder.community.DAO.DiscussPostMapper;
import com.nowcoder.community.DAO.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testDiscussPostMapper() {
        System.out.println(discussPostMapper.selectDiscussPost(0,0,10));
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }
}