package com.nowcoder.community.DAO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
//@Primary
public class AlphaDaoHibernateImpl implements AlphaDAO{
    @Override
    public String select() {
        // 这里是具体的数据库查询逻辑
        return "Hibernate";
        // 实际项目中，这里可能会返回一个实体对象或复杂集合
    }
}
