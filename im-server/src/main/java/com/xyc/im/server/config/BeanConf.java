package com.xyc.im.server.config;

import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Configuration
public class BeanConf implements EnvironmentAware{
	@Autowired
	private com.xyc.im.server.config.Configuration conf;
	
	private Environment env;
	@Bean
	public ZkClient getzkClient(){
		System.out.println("===="+env.getProperty("zk.server.address"));
		return new ZkClient(conf.getZkAddr());
	}

	/**
	 * http client
	 * @return okHttp
	 */
	@Bean
	public OkHttpClient okHttpClient() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10,TimeUnit.SECONDS)
				.retryOnConnectionFailure(true);
		return builder.build();
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}
	public String get(String key){
		return env.getProperty(key);
	}


	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger();
		String s1 = "str";
		String s2 = "ing";
		String s3 = s1+s2;
		String s4 = "str" + "ing";
		String s5 = "string";
		String s6 = new String("str") + new String("ing");
		System.out.println(s3 == s4);
		System.out.println(s3 == s5);
		System.out.println(s3 == s6);
		System.out.println(s4==s5);
		System.out.println(s4==s6);
		System.out.println(s6==s5);

	}

}
