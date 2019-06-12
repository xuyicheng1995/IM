package com.xyc.im.server.controller;

import com.xyc.commons.constants.MessageConstant;
import com.xyc.commons.pojo.ChatInfo;
import com.xyc.commons.pojo.UserInfo;
import com.xyc.commons.protobuf.MessageProto;
import com.xyc.im.server.handle.ChannelMap;
import com.xyc.im.server.handle.IMServerHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xuyicheng
 * @date 2019/6/12 19:27
 */
@RequestMapping("/")
@RestController
public class IMServerController {

    private final static Logger LOGGER = LoggerFactory.getLogger(IMServerController.class);

    private ChannelMap channelMap = ChannelMap.newInstance();

    /**
     * 服务端接受消息，推送到指定的客户端
     * @param chatInfo
     */
    @RequestMapping(value="/pushMessage",method = RequestMethod.POST)
    public void pushMessage(@RequestBody ChatInfo chatInfo){
        //1.消息封装为protoBuf
        MessageProto.MessageProtocol request = MessageProto.MessageProtocol.newBuilder().
                setCommand(chatInfo.getCommand()).
                setContent(chatInfo.getContent()).
                setTime(chatInfo.getTime()).
                setUserId(chatInfo.getUserId()).build();
        //2.ChannelMap中所有的channel发送消息
        if(MessageConstant.CHAT.equals(chatInfo.getCommand())){
            for (Map.Entry<Integer,Channel> entry : channelMap.getChannelMap().entrySet()){
                //过滤本身
                if(!chatInfo.getUserId().equals(entry.getKey())){
                    entry.getValue().writeAndFlush(request);
                    LOGGER.info("服务端向客户端【"+entry.getKey()+"】发送了消息，消息来自【"+chatInfo.getUserId()+"】");
                }
            }
        }

    }

    /**
     * 服务端处理客户端下线事件
     * @param userInfo
     */
    @RequestMapping(value="/clientLogout",method = RequestMethod.POST)
    public void clientLogout(@RequestBody UserInfo userInfo){
        channelMap.getChannelMap().remove(userInfo.getUserId());
        LOGGER.info("----服务端处理客户端下线【"+ userInfo.getUserId()+"】");
    }
}
