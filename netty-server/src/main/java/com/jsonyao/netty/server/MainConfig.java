package com.jsonyao.netty.server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Netty整合SpringBoot: 服务端配置类
 */
@Configuration
// 因为还需要扫描com.jsonyao.netty.common.scanner.NettyProcessBeanScanner
@ComponentScan(basePackages = {"com.jsonyao.netty"})
public class MainConfig {

	
}
