package com.jsonyao.netty.pkg1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 测试Netty: 客户端, 测试消息定长方式解决TCP拆包/粘包问题
 */
public class Pkg1NettyClient {

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
                        // 2.3.1. 测试消息定长, 空格补位解决TCP拆包/粘包问题 => 定长5个字符
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(5));

                        // 2.3.2. Netty String解码器
                        ch.pipeline().addLast(new StringDecoder());

                        // 2.3.2. 配置自定义具体业务接收和处理的方法
                        ch.pipeline().addLast(new Pkg1NettyClientHandler());
                    }
                });

        // 3. 使用辅助配置工具类绑定要监听的端口, 并启动服务 => 同步阻塞
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8765).syncUninterruptibly();

        // 4. 打破连接的同步阻塞, 则发送一条数据到服务器端 => 5个a, 5个b
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("aaaaabbbbb".getBytes()));

        // 5. 睡眠10秒钟后再发送一条数据到服务端、
        System.err.println("睡10s...");
        Thread.sleep(10000);
//        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("ccccccc   ".getBytes()));// => 7个c + 3个空格 => 后面2个c和3个空格可以发送
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("ccccccc".getBytes()));// => 7个c + 没有空格补位 => 后面2个c不可以发送

        // 5.1. 再睡眠10秒钟后再发送一条数据到服务端
        System.err.println("再睡10s...");
        Thread.sleep(10000);
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("ddd".getBytes()));// => 3个d, 凑够5个字符 => 之前残留的2个c在缓冲区中, 凑了3个d凑够5个字符后, 可以一起发送出去

        // 6. 阻塞的方式等待通道关闭 => 如果两边都不关闭, 则客户端和服务端的通道都会一直开着
        channelFuture.channel().closeFuture().sync();

        // 7. 释放资源
        workGroup.shutdownGracefully();
    }
}
