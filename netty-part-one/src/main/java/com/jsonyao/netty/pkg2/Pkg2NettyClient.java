package com.jsonyao.netty.pkg2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 测试Netty: 客户端, 测试特殊字符方式解决TCP拆包/粘包问题
 */
public class Pkg2NettyClient {

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
                // 2.3. 进行初始化ChannelInitializer , 用于构建双向链表 "pipeline" 添加业务handler处理
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 2.3.1. 测试特殊字符方式解决TCP拆包/粘包问题, 以"$_"结尾作为分割符(只要收到$_,就认为到这里就是一个数据包), 最大帧长1024bit
                        ByteBuf delimiterBuf = Unpooled.copiedBuffer("$_".getBytes());
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiterBuf));

                        // 2.3.2. Netty String解码器
                        ch.pipeline().addLast(new StringDecoder());

                        // 2.3.2. 配置自定义具体业务接收和处理的方法
                        ch.pipeline().addLast(new Pkg2NettyClientHandler());
                    }
                });

        // 3. 使用辅助配置工具类绑定要监听的端口, 并启动服务 => 同步阻塞
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8765).syncUninterruptibly();

        // 4. 打破连接的同步阻塞, 则发送一条数据到服务器端 => 循环发送100次带特殊字符的消息
        for (int i = 0; i < 100; i++) {
            channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer(("消息" + i + "$_").getBytes()));
        }

        // 5. 阻塞的方式等待通道关闭 => 如果两边都不关闭, 则客户端和服务端的通道都会一直开着
        channelFuture.channel().closeFuture().sync();

        // 6. 释放资源
        workGroup.shutdownGracefully();
    }
}
