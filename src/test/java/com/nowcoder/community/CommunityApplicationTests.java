package com.nowcoder.community;

import com.nowcoder.community.DAO.AlphaDAO;
import com.nowcoder.community.DAO.AlphaDaoHibernateImpl;
import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
		assertNotNull(applicationContext);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext() {
//		System.out.println(applicationContext);

//		AlphaDAO alphaDAO = applicationContext.getBean(AlphaDAO.class);
////		System.out.println(alphaDAO.select());
////
////		alphaDAO=applicationContext.getBean(AlphaDaoHibernateImpl.class);
////		System.out.println(alphaDAO.select());
	}

	@Test
	public void testBeanManagement() {
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfigure() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(AlphaConfig.class).simpleDateFormat();
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
//	@Qualifier("alphaMybatis")
	@Qualifier("alphaHibernate")
	private AlphaDAO alphaDao;

	@Test
	public void testAlphaDI() {
		System.out.println(alphaDao.select());
	}
}
