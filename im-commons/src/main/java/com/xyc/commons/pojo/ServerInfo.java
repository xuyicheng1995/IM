package com.xyc.commons.pojo;

import java.io.Serializable;

public class ServerInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -653331833928487963L;
	
	private String ip;
	
	private Integer nettyPort;
	
	private Integer serverPort;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getNettyPort() {
		return nettyPort;
	}

	public void setNettyPort(Integer nettyPort) {
		this.nettyPort = nettyPort;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
}
