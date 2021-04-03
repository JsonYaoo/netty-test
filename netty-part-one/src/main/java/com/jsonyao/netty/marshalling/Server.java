package com.jsonyao.netty.marshalling;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * JBoss Marshalling快速入门: 服务端
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        // boosGroup一般写1
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // workerGroup不写默认为: NettyRuntime.availableProcessors() * 2
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 配置工具类Builder
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                // 配置线程组, 形成Reactor父子线程组
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // TCP Sync队列与Accept队列之和 => 建立顺接排队队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        // marshalling编/解码提供者需要配置业务执行之前
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());

                        // 取到管道, 底层是双向链表
                        sc.pipeline().addLast(new ServerHandler());
                    }
                });

        // 使用配置工具类来绑定端口监听 => 真正启动服务
        ChannelFuture cf = serverBootstrap.bind(8765).sync();

        // 同步关闭, 释放资源
        cf.channel().closeFuture().sync();
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
