package com.xyc.im.server.zk;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZKUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ZKUtils.class);
	
	private static final String ROUTE = "/route";
	@Autowired
	private ZkClient client;
	
	private ZKUtils() {
		
	}
	
	public void creatRouteNode(){
		if(client.exists(ROUTE)){
			LOG.info("route节点已存在无需创建！");
			return;
		}
		client.createPersistent(ROUTE);
	}
	
	public void createChildNode(String path){
		client.createEphemeral(path);
	}
	
	public List<String> getALlNodes(String path){
		List<String> nodes = client.getChildren(path);
		LOG.info("查询所有子节点成功，子节点数量为"+nodes.size());
		return nodes;
	}

}
