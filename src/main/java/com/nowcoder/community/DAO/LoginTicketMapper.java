package com.nowcoder.community.DAO;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginTicketMapper {
    public LoginTicket selectLoginTicketByTicket(String ticket);
    public int insertLoginTicket(LoginTicket ticket);
    public int updateStatus(LoginTicket ticket);
}
