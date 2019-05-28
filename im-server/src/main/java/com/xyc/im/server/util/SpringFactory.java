package com.xyc.im.server.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringFactory implements ApplicationContextAware {

	private static ApplicationContext ctx;
	@Override
	public void setApplicationContext(ApplicationContext appCtx)
			throws BeansException {
		this.ctx = appCtx;
	}
	
	public static <T> T getBean(String beanName){
		return (T)ctx.getBean(beanName);
	}
	
	public static <T> T getBean(Class<T> c){
		return ctx.getBean(c);
	}

}
