package com.nowcoder.community.DAO;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("alphaMybatis")
@Primary
public class AlphaDaoMybatisImpl implements AlphaDAO{
    @Override
    public String select() {
        return "从Mybatis获取到的数据";
    }
}
