package com.jsonyao.netty.client;

import com.jsonyao.netty.listener.ApplicationListenerReadyEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Netty整合SpringBoot: 客户端应用
 */
@SpringBootApplication
public class NettyClientApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(NettyClientApplication.class);
		springApplication.addListeners(new ApplicationListenerReadyEvent());
		springApplication.run(args);
	}

}

