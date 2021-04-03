package com.jsonyao.netty.client;

import com.jsonyao.netty.codec.NettyMessageDecoder;
import com.jsonyao.netty.codec.NettyMessageEncoder;
import com.jsonyao.netty.protocol.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自定义Netty客户端
 */
public class NettyClient {

	// 定时单线程线程池 => 用于定时重连
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {

		try {
		    Bootstrap b = new Bootstrap();
		    b.group(group).channel(NioSocketChannel.class)
			    .option(ChannelOption.TCP_NODELAY, true)
			    .handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
					    ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
					    ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
					    ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
					    ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
					    ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
					}
			    });
		    // SocketAddress连接端口, LocalAddress服务端口(Channel响应业务处理时占用的端口, 如果不指定则系统随机分配一个)
		    ChannelFuture future = b.connect(new InetSocketAddress(host, port), new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
		    System.out.println("Client Start.. ");
		    future.channel().closeFuture().sync();
		} finally {
			// 连接完成后, 模拟一秒钟后发起重连
		    executor.execute(new Runnable() {
				@Override
				public void run() {
				    try {
						TimeUnit.SECONDS.sleep(1);
						try {
						    connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
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

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }

}
