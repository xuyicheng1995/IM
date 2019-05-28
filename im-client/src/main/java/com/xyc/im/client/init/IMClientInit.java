package com.xyc.im.client.init;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xyc.commons.protobuf.MessageProto;
import com.xyc.im.client.handler.IMClientHandler;


@Component
public class IMClientInit {
	private static Logger LOGGER = LoggerFactory.getLogger(IMClientInit.class);
	public Channel channel;
	@PostConstruct
	public void start(){
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
				        
						pipeline.addLast(new IMClientHandler());
					}
				});
			ChannelFuture cf = bs.connect("127.0.0.1",8088).sync();
			channel = cf.channel();
			if(cf.isSuccess()){
				LOGGER.info("客户端启动了");
			}
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
