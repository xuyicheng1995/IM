package com.xyc.im.server.handle;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuyicheng
 * @date 2019/6/12 18:32
 * 保存所有与本服务节点链接的channel
 */
public class ChannelMap {
    /**
     * 1.volatile保证并发可见性
     * 2.禁止指令重排
     */
    private static volatile ChannelMap instance;

    private final static Map<Integer,Channel> CHANNEL_MAP = new ConcurrentHashMap<>();
    //单例模式
    private ChannelMap(){}

    //高并发下单例模式  双重验证
    public static ChannelMap newInstance(){
        if(instance == null){
            synchronized (ChannelMap.class){
                if(instance == null){
                    instance = new ChannelMap();
                }
            }
        }
        return instance;
    }

    public Map<Integer,Channel> getChannelMap(){
        return CHANNEL_MAP;
    }

    public void putChannel(Integer userId,Channel channel){
        CHANNEL_MAP.put(userId,channel);
    }

    public Channel getChannel(Integer userId){
       return CHANNEL_MAP.get(userId);
    }
}
