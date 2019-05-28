package com.xyc.im.route.control;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xyc.commons.constants.BasicConstant;
import com.xyc.commons.pojo.ServerInfo;
import com.xyc.commons.pojo.UserInfo;
import com.xyc.im.route.zk.ZKUtils;

@RestController
@RequestMapping("/")
public class IMRouteController {
	private final static Logger LOGGER = LoggerFactory.getLogger(IMRouteController.class);
	@Autowired
	private ZKUtils zk;
	@Autowired
	private RedisTemplate<String, String>  template;
	private AtomicLong index = new AtomicLong();
	
	/**
	 * 客户端用户发现服务端的接口
	 * 1.获取所有的ZK上的server节点
	 * 2.自己实现一个轮询算法(其他算法) 得到一个Server节点 
	 * 3.保存客户端与server的映射关系（redis）(userId  ->   ip+port)
	 * 4.返回这个Server节点的信息（ip+port）
	 **/
	@RequestMapping("/getRoute")
	public ServerInfo getRoute(@RequestBody UserInfo user){
		String serverStr = "";
		List<String> list = zk.getALlNodes("/route");
		if(CollectionUtils.isEmpty(list)){
			LOGGER.info("获取到的节点数据为空！");
			return null;
		}
		Integer idx = (int) (index.incrementAndGet() % list.size());
		serverStr = list.get(idx);
		String[] strs = serverStr.split("_");
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setIp(strs[0]);
		serverInfo.setNettyPort(Integer.valueOf(strs[1]));
		serverInfo.setServerPort(Integer.valueOf(strs[2]));
		
		Integer userId = user.getUserId();
		template.opsForValue().set(BasicConstant.ROUTE_PREFIX + userId, serverStr);;
		return serverInfo;
	}
}
