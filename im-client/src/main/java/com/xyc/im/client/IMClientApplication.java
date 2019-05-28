package com.xyc.im.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xyc.im.client.init.IMClientInit;
import com.xyc.im.client.scanner.Scan;


@SpringBootApplication
public class IMClientApplication implements CommandLineRunner{
	public static void main(String[] args) {
		SpringApplication.run(IMClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Thread thread = new Thread(new Scan());
		thread.setName("im-client");
		thread.start();
	}

}
