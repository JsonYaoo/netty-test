package com.jsonyao.netty.quickstart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty快速入门: 服务端
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        // 1. 创建两个线程组: 一个是用于处理服务端接收客户端连接, 一个用于进行网络通信(即网络读写)
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        // 2. 创建辅助配置工具类, 用于服务器通道的一系列配置
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                // 2.1. 绑定两个线程组, 父子线程组
                .group(parentGroup, childGroup)
                // 2.2. 指定NIO模式, 因为是Server端, 所以要绑定NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // 2.3. 指定连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                // 2.4. TCP不允许延迟, 通信不延迟
                .option(ChannelOption.TCP_NODELAY, true)
                // 2.5. 设置TCP缓冲区大小 eg => 32M = sync队列 + accept队列
                .option(ChannelOption.SO_BACKLOG, 32 * 1024)
                // 2.6. 设置接收缓冲区大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                // 2.6.1. 设置接收缓冲区自动扩容, 已弃用
//                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                // 2.6.2. 设置发送缓冲区使用对象池, 重用缓冲区
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 2.7. 进行初始化ChannelInitializer , 用于构建双向链表 "pipeline" 添加业务handler处理
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 2.7.1. 配置自定义具体业务接收和处理的方法
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        // 3. 使用辅助配置工具类绑定要监听的端口 => 同步阻塞
        ChannelFuture channelFuture = serverBootstrap.bind(8765).sync();

        // 4. 同步阻塞等待通道关闭 => 如果两边都不关闭, 则客户端和服务端的通道都会一直开着
        channelFuture.channel().closeFuture().sync();

        // 5. 释放资源
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }
}
