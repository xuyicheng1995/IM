package com.xyc.im.client.scanner;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.protobuf.MessageProto;
import com.xyc.commons.utils.StringUtils;
import com.xyc.im.client.conf.Configuration;
import com.xyc.im.client.init.IMClientInit;
import com.xyc.im.client.util.SpringFactory;

public class Scan implements Runnable{
	private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);
	private IMClientInit imClientInit;
	private Configuration conf;
	
	
	public Scan() {
		this.imClientInit = SpringFactory.getBean(IMClientInit.class);
		this.conf = SpringFactory.getBean(Configuration.class);
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
			//使用ByteBuf发送消息
//			ByteBuf req = Unpooled.buffer(message.getBytes().length);
//			req.writeBytes(message.getBytes());
//			imClientInit.channel.writeAndFlush(req);
			//使用protobuf发送消息
			MessageProto.MessageProtocol request = MessageProto.MessageProtocol.newBuilder().
					setCommand(MessageConstant.CHAT).
					setContent(message).
					setTime(System.currentTimeMillis()).
					setUserId(conf.getUserId()).build();
			imClientInit.channel.writeAndFlush(request);
		}
		
	}

}
