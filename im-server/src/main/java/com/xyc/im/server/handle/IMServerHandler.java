package com.xyc.im.server.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyc.commons.protobuf.MessageProto;
import com.xyc.commons.protobuf.MessageProto.MessageProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class IMServerHandler extends ChannelInboundHandlerAdapter{
	
	private static Logger LOGGER = LoggerFactory.getLogger(IMServerHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		ByteBuf buf = (ByteBuf)msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req,"UTF-8");
		MessageProto.MessageProtocol reqest = (MessageProto.MessageProtocol)msg;
		
		LOGGER.info("服务端接收到请求"+reqest.toString() +"获取内容" +reqest.getContent());
	}

}
