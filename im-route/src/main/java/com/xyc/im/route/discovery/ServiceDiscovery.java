package com.xyc.im.route.discovery;

import com.xyc.commons.pojo.ServerInfo;
import com.xyc.im.route.cache.ServerInfoCache;
import com.xyc.im.route.config.Configuration;
import com.xyc.im.route.util.SpringFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuyicheng
 * @date 2019/6/13 15:02
 *  服务自动发现
 */
public class ServiceDiscovery{
	private final static Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

	private static CuratorFramework client;

	private static TreeCache treeCache;

	private static final String ROOT = "/route";

	// 保存服务实例
	private static ServerInfoCache serverInfoCache = ServerInfoCache.newInstance();
	static {

		init();
		nodeMonitor();
	}

	public  ServiceDiscovery(){
//		init();
//		nodeMonitor();
	}

//	@Override
//	public void run() {
//		LOGGER.info("discoverClient 初始化...........");
//		init();
//		nodeMonitor();
//	}

	// 初始化ZK连接，初始化缓存数据serverInfoCache
	public static void init() {
		Configuration conf = SpringFactory.getBean(Configuration.class);
		client = CuratorFrameworkFactory.builder().connectString(conf.getZkAddr())
				.connectionTimeoutMs(3000).sessionTimeoutMs(3000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		try {
			List<String> serviceList = client.getChildren().forPath(ROOT);
			LOGGER.info("获取到所有节点信息..........."+serviceList);
			serverInfoCache.addAll(serviceList);
		} catch (Exception e) {
			LOGGER.error("初始化失败: "+e.getMessage());
		}
	}


	public static void nodeMonitor() {
		treeCache = new TreeCache(client, ROOT);

		try {
			treeCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		treeCache.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				String data ;
				// 新的服务上线
				if (event.getType() == Type.NODE_ADDED) {
					data = new String(event.getData().getPath().split("/")[2]);
					LOGGER.info("========ADD"+data);
					addServer(data);
				}

				// 服务信息产生变化，或者更新负载
				if (event.getType() == Type.NODE_UPDATED) {
					data = new String(event.getData().getPath().split("/")[2]);
					LOGGER.info("========UNDATE"+data);
					// serviceMap.put(event.getData().getPath(), data);
				}

				// 服务下线
				if (event.getType() == Type.NODE_REMOVED) {
					data = new String(event.getData().getPath().split("/")[2]);
					LOGGER.info("========DELETE"+data);
					removeServer(event.getData().getPath(), data);
					treeCache.getListenable().removeListener(this);
				}
			}
		});
	}


	public static void addServer(String server) {
		if (!serverInfoCache.containsServerInfo(server))
			serverInfoCache.add(server);
	}

	public  static void removeServer(String serviceName, String server) {
		if (serverInfoCache.containsServerInfo(server))
			serverInfoCache.remove(server);
	}

	// 随机获取一个可用的服务地址，作负载均衡使用
//	public  Optional<String> getServer(String serviceName) {
//		if (serviceMap.size() == 0 || serviceMap.get(serviceName) == null
//				|| serviceMap.get(serviceName).isEmpty()) {
//			System.err.println("does not have available server.");
//			return Optional.ofNullable(null);
//		}
//
//		int size = serviceMap.get(serviceName).size();
//
//		// 这里更改为其他负载均衡算法
//		int rand = new Random().nextInt(size);
//		System.out.println("size=" + size + ",rand=" + rand);
//		String server = (String) serviceMap.get(serviceName).toArray()[rand];
//		return Optional.ofNullable(server);
//	}
}
