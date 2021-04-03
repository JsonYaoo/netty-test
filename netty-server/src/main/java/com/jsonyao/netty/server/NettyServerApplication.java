package com.jsonyao.netty.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Netty整合SpringBoot: 服务端应用
 */
@SpringBootApplication
public class NettyServerApplication {

	public static void main(String[] args) {
	    new SpringApplicationBuilder(NettyServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
	}

}

