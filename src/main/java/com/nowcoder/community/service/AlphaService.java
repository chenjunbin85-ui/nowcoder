package com.nowcoder.community.service;

import com.nowcoder.community.DAO.AlphaDAO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
//@Scope("prototype")//多例模式 默认singleton
public class AlphaService {

    private AlphaDAO alphaDAO;
    @Autowired
    public AlphaService(AlphaDAO alphaDAO) {
        System.out.println("实例化AlphaService");
        this.alphaDAO = alphaDAO;
    }

    @PostConstruct //表示在该方法构造器之后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String findAData() {
        return alphaDAO.select();
    }

}
