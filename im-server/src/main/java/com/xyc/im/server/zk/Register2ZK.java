package com.xyc.im.server.zk;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyc.im.server.config.BeanConf;
import com.xyc.im.server.config.Configuration;
import com.xyc.im.server.util.SpringFactory;

public class Register2ZK implements Runnable{

	private static final Logger LOG = LoggerFactory.getLogger(Register2ZK.class);
	private static final String ROUTE = "/route";
	
	private Configuration conf;
	private ZKUtils zkUtils;
	private BeanConf beanConf;
	
	public Register2ZK() {
		this.conf = SpringFactory.getBean(Configuration.class);
		this.zkUtils = SpringFactory.getBean(ZKUtils.class);
		this.beanConf = SpringFactory.getBean(BeanConf.class);
	}
	@Override
	public void run() {
		try {
			if("true".equals(beanConf.get("zk.server.switch"))){
				String path =ROUTE + "/" + InetAddress.getLocalHost().getHostAddress() + "_" + conf.getAppPort() + "_" + conf.getImServerPort();
				LOG.info("--服务端准备注册到Zookeeper中， IP :"+InetAddress.getLocalHost().getHostAddress()+"; AppPort:"+conf.getAppPort()+"; nettyPort:"+conf.getImServerPort());
				zkUtils.creatRouteNode();
				zkUtils.createChildNode(path);
				LOG.info("--服务端注册成功");
				LOG.info(beanConf.get("zk.server.switch"));
			}
		} catch (UnknownHostException e) {
			LOG.error("服务端注册异常",e);
		}
		
	}

}
