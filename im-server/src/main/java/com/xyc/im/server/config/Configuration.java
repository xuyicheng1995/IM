package com.xyc.im.server.config;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {
	@Value("${im.server.port}")
	private String imServerPort;
	
	@Value("${server.port}")
	private String appPort;
	
	@Value("${zk.server.address}")
	private String zkAddr;
	
	@Value("${zk.server.switch}")
	private String zkSwitch;

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

	public String getZkAddr() {
		return zkAddr;
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public String getZkSwitch() {
		return zkSwitch;
	}

	public void setZkSwitch(String zkSwitch) {
		this.zkSwitch = zkSwitch;
	}

}
