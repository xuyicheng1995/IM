package com.xyc.im.route.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xuyicheng
 * @date 2019/6/13 15:02
 * serverInfo缓存类（单例）
 */
public class ServerInfoCache {

    private static volatile ServerInfoCache instance;
    private final List<String> serverCache = new CopyOnWriteArrayList<String>();

    private ServerInfoCache(){}

    public static ServerInfoCache newInstance(){
        if(instance == null){
            synchronized (ServerInfoCache.class){
                if(instance == null){
                    instance = new ServerInfoCache();
                }
            }
        }
        return instance;
    }

    /**
     * 添加所有服务信息
     * @param c
     */
    public void addAll(Collection<String> c){
        serverCache.addAll(c);
    }
    /**
     * 添加服务信息
     * @param serverInfo
     */
    public void add(String serverInfo){
        serverCache.add(serverInfo);
    }
    /**
     * 移除服务信息
     * @param serverInfo
     */
    public void remove(String serverInfo){
        serverCache.remove(serverInfo);
    }

  public boolean containsServerInfo(String serverInfo){
        return serverCache.contains(serverInfo);
  }

  public boolean isNotEmpty(){
        return !serverCache.isEmpty();
  }

  public List<String>  getCache(){
        return this.serverCache;
  }
}
