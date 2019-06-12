package com.xyc.im.client.handler;

import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.protobuf.MessageProto;
import com.xyc.im.client.conf.Configuration;
import com.xyc.im.client.init.IMClientInit;
import com.xyc.im.client.util.SpringFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  客户端处理器
 */
public class IMClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(IMClientHandler.class);
	private IMClientInit imClientInit;

	public IMClientHandler(){
		LOGGER.info("--IMClientHandler init");
//		imClientInit = SpringFactory.getBean(IMClientInit.class);
	}

	/**
	 * 刚链接服务端时 触发事件
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String message = "hello,xyc";
//		ByteBuf req = Unpooled.buffer(message.getBytes().length);
//		req.writeBytes(message.getBytes());
//		ctx.writeAndFlush(req);

		MessageProto.MessageProtocol request = MessageProto.MessageProtocol.newBuilder().
				setCommand(MessageConstant.CHAT).
				setContent(message).
				setTime(System.currentTimeMillis()).
				setUserId(10001).build();
		ctx.writeAndFlush(request);
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageProto.MessageProtocol request = (MessageProto.MessageProtocol)msg;
		LOGGER.info("客户端接收到请求"+request.toString() +"获取内容" +request.getContent());
		//聊天指令
		if (MessageConstant.CHAT.equals(request.getCommand())){


		}else {
			//

		}

    }

	/**
	 * 客户端感知到自己连接的服务端下线了
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		imClientInit = SpringFactory.getBean(IMClientInit.class);

		imClientInit.restart();
		super.channelInactive(ctx);
	}
}
