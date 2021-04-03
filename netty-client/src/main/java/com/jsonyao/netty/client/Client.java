package com.jsonyao.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Netty整合SpringBoot: 客户端
 */
@Component
public class Client {

	public static final String HOST = "127.0.0.1";
	public static final int PORT = 8888;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup(2);
	private Channel channel;

	private Client() throws Exception {
		this.connect(HOST, PORT);
	}

    private void connect(String host, int port) throws Exception {
    	// 配置客户端NIO线程组
		try {
		    Bootstrap b = new Bootstrap();
		    b.group(group).channel(NioSocketChannel.class)
			    .option(ChannelOption.TCP_NODELAY, true)
			    .handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {

					}
			    });
		    // 发起异步连接操作
		    ChannelFuture future = b.connect(host, port).sync();
		    this.channel = future.channel();
		    System.out.println("Client Start.. ");
		    this.channel.closeFuture().sync();
		} finally {
		    // 	所有资源释放完成之后，清空资源，再次发起重连操作
		    executor.execute(new Runnable() {
				@Override
				public void run() {
				    try {
						TimeUnit.SECONDS.sleep(1);
						try {
						    connect(host, port);// 发起重连操作
						} catch (Exception e) {
						    e.printStackTrace();
						}
				    } catch (InterruptedException e) {
				    	e.printStackTrace();
				    }
				}
		    });
		}
	}
}
