package com.xyc.im.server.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.pojo.ServerInfo;
import com.xyc.im.server.config.RouteConfig;
import com.xyc.im.server.util.SpringFactory;
import io.netty.util.AttributeKey;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyc.commons.protobuf.MessageProto;
import com.xyc.commons.protobuf.MessageProto.MessageProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

/**
 *
 */
public class IMServerHandler extends ChannelInboundHandlerAdapter{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerHandler.class);

	private final AttributeKey<Integer> uid = AttributeKey.valueOf("userId");

	private ChannelMap map = ChannelMap.newInstance();

	private OkHttpClient httpClient;
	private RouteConfig routeConfig;

	public IMServerHandler(){
		this.httpClient = SpringFactory.getBean(OkHttpClient.class);
		this.routeConfig = SpringFactory.getBean(RouteConfig.class);
	}

	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		ByteBuf buf = (ByteBuf)msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req,"UTF-8");
//		System.out.println(body);
		MessageProto.MessageProtocol request = (MessageProto.MessageProtocol)msg;
		LOGGER.info("服务端接收到请求"+request.toString() +"获取内容" +request.getContent());
		//登录指令
		if (MessageConstant.LOGIN.equals(request.getCommand()) && MessageConstant.LOGIN.equals(request.getContent())){
			//保存到ChannelMap中
			ctx.channel().attr(uid).set(request.getUserId()); //用户登录时，绑定一个userId属性在Channel
			map.putChannel(request.getUserId(),ctx.channel());
			LOGGER.info("---客户端登录成功。userId:"+request.getUserId());
		}else {
			//聊天
			LOGGER.info("---服务端接收到数据："+request.getContent()+"，发送人："+request.getUserId());

		}

	}

	/**
	 * 服务端感知到连接自己的客户端下线了
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Integer userId = ctx.channel().attr(uid).get();
		map.getChannelMap().remove(userId);//删除客户端对应的channel
		JSONObject json = new JSONObject();
		json.put("userId",userId);

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
				LOGGER.error("调用路由端下线接口logout失败.....");
				throw new IOException("调用路由端下线接口logout失败.....");
			}
		} catch (IOException e) {
			LOGGER.error("调用路由端下线接口logout失败.....");
		}
		finally {
			if(response!=null){
				LOGGER.info("关闭response.....");
				response.close();
			}
		}
		super.channelInactive(ctx);
	}
}
