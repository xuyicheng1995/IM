package com.xyc.im.route.config;

import com.xyc.im.route.discovery.ServiceDiscovery;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration implements InitializingBean, SmartInitializingSingleton {
	
	@Value("${server.port}")
	private String appPort;
	
	@Value("${zk.server.address}")
	private String zkAddr;
	
	@Value("${zk.server.switch}")
	private String zkSwitch;

	public Configuration(){
		System.out.println("Configuration.........");
	}

	public String getAppPort() {
		return appPort;
	}

	public void setAppPort(String appPort) {
		System.out.println("setAppPort.........");
		this.appPort = appPort;
	}

	public String getZkAddr() {
		return zkAddr;
	}

	public void setZkAddr(String zkAddr) {
		System.out.println("setZkAddr.........");
		this.zkAddr = zkAddr;
	}

	public String getZkSwitch() {
		return zkSwitch;
	}

	public void setZkSwitch(String zkSwitch) {
		System.out.println("setZkSwitch.........");
		this.zkSwitch = zkSwitch;
	}


	@Override
	public void afterPropertiesSet() {
		System.out.println("afterPropertiesSet.........");
	}

	@Override
	public void afterSingletonsInstantiated() {
		System.out.println("afterSingletonsInstantiated.........");
		new ServiceDiscovery();

	}

}
