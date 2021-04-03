package com.jsonyao.netty.server;

import com.jsonyao.netty.listener.ApplicationListenerReadyEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Netty整合SpringBoot: 服务端应用
 */
@SpringBootApplication
public class NettyServerApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(NettyServerApplication.class);
		springApplication.addListeners(new ApplicationListenerReadyEvent());
		springApplication.run(args);
	}

}

