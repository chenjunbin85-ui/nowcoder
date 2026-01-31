package com.nowcoder.community.DAO;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    public List<DiscussPost> selectDiscussPost(int userId,int offset,int limit);

    public int selectDiscussPostRows(@Param("userId") int userId);
}
