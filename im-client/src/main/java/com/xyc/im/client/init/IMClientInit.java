package com.xyc.im.client.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.pojo.ServerInfo;
import com.xyc.im.client.conf.Configuration;
import com.xyc.im.client.conf.RouteConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.PostConstruct;

import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import okhttp3.*;
import org.apache.catalina.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xyc.commons.protobuf.MessageProto;
import com.xyc.im.client.handler.IMClientHandler;

import java.io.IOException;
import java.io.Serializable;


@Component
public class IMClientInit {
	private static final Logger LOGGER = LoggerFactory.getLogger(IMClientInit.class);
	public Channel channel;
	private ServerInfo serverInfo;

	@Autowired
	private OkHttpClient httpClient;

	@Autowired
	private Configuration conf;

	@Autowired
	private RouteConfig routeConfig;
	@PostConstruct
	public void start(){

		if(serverInfo != null){
			LOGGER.info("--客户端当前已经登录状态！");
			return;
		}
		LOGGER.info("客户端启动初始化......");
		//1.从route 获取server信息
		getServerInfo();
		//2 开启客户端
		startClient();
		//3.登陆到服务器端   就是让服务端保存userId和Channel的关系
		register2Server();
	}

	private void getServerInfo(){
		JSONObject json = new JSONObject();
		json.put("userId",conf.getUserId());
		json.put("userName",conf.getUserName());
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody requestBody = RequestBody.create(mediaType, json.toString());
		Request request = new Request.Builder()
				.url(routeConfig.getGetRoute())
				.post(requestBody)
				.build();
		Response response = null;
		ResponseBody body =null;
		try {
			response = httpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("获取客户端节点失败.....");
				throw new IOException("获取客户端节点失败.....");
			}
			body = response.body();
			String respJson = body.string();
			this.serverInfo = JSON.parseObject(respJson,ServerInfo.class);
		} catch (IOException e) {
			LOGGER.error("获取客户端节点失败.....");
		}
		finally {
			if(body!=null){
				LOGGER.info("关闭body.....");
				body.close();
			}
			if(response!=null){
				LOGGER.info("关闭response.....");
				response.close();
			}
		}
	}

	private void startClient(){
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							ChannelPipeline pipeline = arg0.pipeline();
							//google protobuf 编解码
							pipeline.addLast(new ProtobufVarint32FrameDecoder());
							pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
							pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
							pipeline.addLast(new ProtobufEncoder());
							//自定义处理器
							pipeline.addLast(new IMClientHandler());
						}
					});
			ChannelFuture cf = bs.connect(serverInfo.getIp(),serverInfo.getNettyPort()).sync();
//			channel = cf.channel();
			if(cf.isSuccess()){
				LOGGER.info("客户端启动成功......");
				channel = cf.channel();
			}

		} catch (InterruptedException e) {
			LOGGER.error("客户端启动初始化失败......");
		}
	}

	private void register2Server(){
		MessageProto.MessageProtocol request = MessageProto.MessageProtocol.newBuilder().
				setCommand(MessageConstant.LOGIN).
				setContent(MessageConstant.LOGIN).
				setTime(System.currentTimeMillis()).
				setUserId(conf.getUserId()).build();
		channel.writeAndFlush(request);
	}

	public void clear(){
		//调用路由端清除redis信息
		logoutRoute();
		//调用服务端清除channel数据
		logoutServer();
		serverInfo = null;
		channel = null;
	}

	/**
	 * 调用路由端清理数据
	 */
	private void logoutRoute(){
		JSONObject json = new JSONObject();
		json.put("userId",conf.getUserId());

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody requestBody = RequestBody.create(mediaType, json.toString());
		Request request = new Request.Builder()
				.url(routeConfig.getLogout())
				.post(requestBody)
				.build();
		Response response = null;
		try {
			response = httpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("客户端调用路由端下线接口logout失败.....");
				throw new IOException("客户端调用路由端下线接口logout失败.....");
			}
		} catch (IOException e) {
			LOGGER.error("客户端调用路由端下线接口logout失败.....");
		}
		finally {
			if(response!=null){
				LOGGER.info("关闭response.....");
				response.close();
			}
		}
	}

	/**
	 * 调用服务端清理数据
	 */
	private void logoutServer(){
		String clientLogoutUrl = "http://"+serverInfo.getIp()+":"+serverInfo.getServerPort()+"/clientLogout";
		JSONObject json = new JSONObject();
		json.put("userId",conf.getUserId());

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody requestBody = RequestBody.create(mediaType, json.toString());
		Request request = new Request.Builder()
				.url(clientLogoutUrl)
				.post(requestBody)
				.build();
		Response response = null;
		try {
			response = httpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("客户端调用服务端下线接口clientLogout失败.....");
				throw new IOException("客户端调用服务端下线接口clientLogout失败.....");
			}
		} catch (IOException e) {
			LOGGER.error("客户端调用服务端下线接口clientLogout失败.....");
		}
		finally {
			if(response!=null){
				LOGGER.info("关闭response.....");
				response.close();
			}
		}
	}

	/**
	 * 服务端宕机  客户端重新建立连接
	 */
	public void restart(){
		logoutRoute();
		serverInfo = null;
		channel = null;
		start();
	}
}
