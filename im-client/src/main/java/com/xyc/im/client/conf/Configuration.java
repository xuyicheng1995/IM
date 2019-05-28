package com.xyc.im.client.conf;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {
	@Value("${im.server.port}")
	private String imServerPort;
	
	@Value("${server.port}")
	private String appPort;
	
	@Value("${im.userId}")
	private Integer userId;

	public String getImServerPort() {
		return imServerPort;
	}

	public void setImServerPort(String imServerPort) {
		this.imServerPort = imServerPort;
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
