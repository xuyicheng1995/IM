package com.xyc.im.client.conf;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {

	@Value("${server.port}")
	private String appPort;
	
	@Value("${im.userId}")
	private Integer userId;

	@Value("${im.userName}")
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAppPort() {
		return appPort;
	}

	public void setAppPort(String appPort) {
		this.appPort = appPort;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}
