package com.xyc.im.client.scanner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyc.commons.pojo.ChatInfo;
import com.xyc.commons.pojo.ServerInfo;
import com.xyc.im.client.conf.RouteConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Scanner;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.protobuf.MessageProto;
import com.xyc.commons.utils.StringUtils;
import com.xyc.im.client.conf.Configuration;
import com.xyc.im.client.init.IMClientInit;
import com.xyc.im.client.util.SpringFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * 启动Sanner线程接受客户端的输入
 */
public class Scan implements Runnable{
	private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);
	private IMClientInit imClientInit;
	private Configuration conf;
	private OkHttpClient httpClient;
	private RouteConfig routeConfig;
	
	
	public Scan() {
		this.imClientInit = SpringFactory.getBean(IMClientInit.class);
		this.conf = SpringFactory.getBean(Configuration.class);
		this.httpClient = SpringFactory.getBean(OkHttpClient.class);
		this.routeConfig = SpringFactory.getBean(RouteConfig.class);
	}


	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		while(true){
			String message = scan.nextLine();
			if(StringUtils.isEmpty(message)){
				LOGGER.info("---不允许发送空消息！");
				continue;
			}
			//客户端主动下线
			if(MessageConstant.LOGOUT.equals(message)){
				imClientInit.clear();
				continue;
			}
			//客户端主动上线
			if(MessageConstant.LOGIN.equals(message)){
				imClientInit.start();
				continue;
			}
			//使用ByteBuf发送消息
//			ByteBuf req = Unpooled.buffer(message.getBytes().length);
//			req.writeBytes(message.getBytes());
//			imClientInit.channel.writeAndFlush(req);

			//使用protobuf发送消息
//			MessageProto.MessageProtocol request = MessageProto.MessageProtocol.newBuilder().
//					setCommand(MessageConstant.CHAT).
//					setContent(message).
//					setTime(System.currentTimeMillis()).
//					setUserId(conf.getUserId()).build();
//			imClientInit.channel.writeAndFlush(request);

			//使用路由端api调用
			sendMessage(message);
		}
		
	}

	private void sendMessage(String message){

		ChatInfo chatInfo = new ChatInfo(MessageConstant.CHAT,System.currentTimeMillis(),conf.getUserId(),message);
		JSONObject json = new JSONObject();
		json.put("command",chatInfo.getCommand());
		json.put("time",chatInfo.getTime());
		json.put("userId",chatInfo.getUserId());
		json.put("content",chatInfo.getContent());
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody requestBody = RequestBody.create(mediaType, json.toString());
		Request request = new Request.Builder()
				.url(routeConfig.getChat())
				.post(requestBody)
				.build();
		Response response = null;
		try {
			response = httpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("客户端调用Route Chat失败.....");
				throw new IOException("客户端调用Route Chat失败.....");
			}
		} catch (IOException e) {
			LOGGER.error("客户端调用Route Chat失败.....",e);
		}
		finally {

			if(response!=null){
				LOGGER.info("关闭response.....");
				response.close();
			}
		}
	}

}
