package com.jsonyao.netty.quickstart;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty快速入门: 客户端
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        // 1. 创建一个线程组: 只需要一个线程组用于实际业务的处理(网络通信的读写)
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        // 2. 创建辅助配置工具类, 进行配置响应的参数 => 用于构造Client
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 2.1. 绑定线程组
                .group(workGroup)
                // 2.2. 指定NIO模式, 因为是Client端, 所以要绑定NioSocketChannel
                .channel(NioSocketChannel.class)
                // 2.3. 指定连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                // 2.4. TCP不允许延迟, 通信不延迟
                .option(ChannelOption.TCP_NODELAY, true)
                // 2.5. 设置TCP缓冲区大小 eg => 32M = sync队列 + accept队列
                .option(ChannelOption.SO_BACKLOG, 32 * 1024)
                // 2.6. 设置接收缓冲区大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                // 2.7. 进行初始化ChannelInitializer , 用于构建双向链表 "pipeline" 添加业务handler处理
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 2.7.1. 配置自定义具体业务接收和处理的方法
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });

        // 3. 使用辅助配置工具类绑定要监听的端口, 并启动服务 => 同步阻塞
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8765).syncUninterruptibly();

        // 4. 打破连接的同步阻塞, 则发送一条数据到服务器端
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("Hello netty!".getBytes()));

        // 5. 睡眠10秒钟后再发送一条数据到服务端
        Thread.sleep(10000);
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("Hello netty again!".getBytes()));

        // 6. 阻塞的方式等待通道关闭 => 如果两边都不关闭, 则客户端和服务端的通道都会一直开着
        channelFuture.channel().closeFuture().sync();

        // 7. 释放资源
        workGroup.shutdownGracefully();
    }
}
