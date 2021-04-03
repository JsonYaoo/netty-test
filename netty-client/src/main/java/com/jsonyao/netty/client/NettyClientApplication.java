package com.jsonyao.netty.client;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Netty整合SpringBoot: 客户端应用
 */
@SpringBootApplication
public class NettyClientApplication {

	public static void main(String[] args) {
	    new SpringApplicationBuilder(NettyClientApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
	}

}

