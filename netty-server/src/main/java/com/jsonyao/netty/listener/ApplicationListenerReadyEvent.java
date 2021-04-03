package com.jsonyao.netty.listener;

import com.jsonyao.netty.server.Server;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * Netty最佳实践: Spring应用准备完毕后, 进行初始化Netty Server
 */
public class ApplicationListenerReadyEvent implements ApplicationListener<ApplicationReadyEvent>{

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.err.println("@@@@@@@@----应用服务已经启动成功----@@@@@@@@");
		new Server();
	}

}
