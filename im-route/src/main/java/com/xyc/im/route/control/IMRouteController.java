package com.xyc.im.route.control;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSONObject;
import com.xyc.commons.pojo.ChatInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	@Autowired
	private OkHttpClient httpClient;
	private AtomicLong index = new AtomicLong();
	
	/**
	 * 客户端用户发现服务端的接口
	 * 1.获取所有的ZK上的server节点
	 * 2.自己实现一个轮询算法(其他算法) 得到一个Server节点 
	 * 3.保存客户端与server的映射关系（redis）(userId  ->   ip+port)
	 * 4.返回这个Server节点的信息（ip+port）
	 **/
	@RequestMapping(value = "/getRoute",method = RequestMethod.POST)
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
		serverInfo.setServerPort(Integer.valueOf(strs[1]));
		serverInfo.setNettyPort(Integer.valueOf(strs[2]));
		
		Integer userId = user.getUserId();
		template.opsForValue().set(BasicConstant.ROUTE_PREFIX + userId, serverStr);
		LOGGER.info(serverInfo.toString());
		return serverInfo;
	}

    /**
     *
     */
    @RequestMapping(value = "/chat",method = RequestMethod.POST)
	public void chat(@RequestBody ChatInfo chatInfo){
	    //判断userId是否为的登陆状态
		String isLogin = template.opsForValue().get(BasicConstant.ROUTE_PREFIX + chatInfo.getUserId());
		if(StringUtils.isEmpty(isLogin)){
			LOGGER.info("该用户并未登陆......."+chatInfo.getUserId());
			return;
		}

		//拿到所有server节点
		List<String> list = zk.getALlNodes("/route");
		for(String serverStr : list){
			String[] serverStrArray = serverStr.split("_");
			String url = "http://"+serverStrArray[0]+":"+serverStrArray[1]+"/pushMessage";
			//调用服务端api 进行消息推送
			JSONObject json = new JSONObject();
			json.put("command",chatInfo.getCommand());
			json.put("time",chatInfo.getTime());
			json.put("userId",chatInfo.getUserId());
			json.put("content",chatInfo.getContent());
			MediaType mediaType = MediaType.parse("application/json");
			okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
			Request request = new Request.Builder()
					.url(url)
					.post(requestBody)
					.build();
			Response response = null;
			try {
				response = httpClient.newCall(request).execute();
				if(!response.isSuccessful()){
					LOGGER.error("路由端调用server端失败.....");
					throw new IOException("路由端调用server端失败.....");
				}
			} catch (IOException e) {
				LOGGER.error("路由端调用server端失败.....",e);
			}
			finally {

				if(response!=null){
					LOGGER.info("关闭response.....");
					response.close();
				}
			}
		}

    }

	/**
	 *
	 */
	@RequestMapping(value = "/logout",method = RequestMethod.POST)
	public void logout(@RequestBody UserInfo user){

		template.opsForValue().getOperations().delete(BasicConstant.ROUTE_PREFIX +user.getUserId() );
	}
}
