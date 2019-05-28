package com.xyc.im.server.init;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.channel.ChannelInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xyc.commons.protobuf.MessageProto;
import com.xyc.im.server.handle.IMServerHandler;


@Component
public class IMServerInit {
	private static Logger LOGGER = LoggerFactory.getLogger(IMServerInit.class);
	@PostConstruct
	public void start() throws Exception {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						ChannelPipeline pipeline = arg0.pipeline();
						//google protobuf 编解码
						pipeline.addLast(new ProtobufVarint32FrameDecoder());
				        pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
				        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
				        pipeline.addLast(new ProtobufEncoder());
				        
						pipeline.addLast(new IMServerHandler());  
					}
				});
			ChannelFuture cf = sb.bind(8088).sync();
			if(cf.isSuccess()){
				LOGGER.info("---服务端启动Netty成功 ["+8088+"]");
			}
		} catch (Exception e) {
			
		}
	}

}
