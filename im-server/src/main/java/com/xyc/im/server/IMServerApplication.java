package com.xyc.im.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xyc.im.server.zk.Register2ZK;

@SpringBootApplication
public class IMServerApplication implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(IMServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Thread thread = new Thread(new Register2ZK());
		thread.setName("im-server-registerZK-thread");
		thread.start();
		
	}

}
