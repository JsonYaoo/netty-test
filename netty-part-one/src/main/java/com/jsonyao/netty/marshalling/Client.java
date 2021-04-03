package com.jsonyao.netty.marshalling;

import com.jsonyao.netty.utils.GzipUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * JBoss Marshalling快速入门: 客户端
 */
public class Client {

    public static void main(String[] args) throws Exception {
        // 客户端只需要一个线程组就可 => 不是Reactor模型
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 客户端配置工具类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        // marshalling编/解码提供者需要配置业务执行之前
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());

                        // 取到管道, 底层是双向链表
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });

        // 使用配置工具类建立连接 => 同步阻塞
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 8765).sync();

        // 使用ChannelFuture对象发送消息 <= 建立连接后
        Channel channel = cf.channel();
        for (int i = 0; i < 100; i++) {
            RequestData requestData = new RequestData();
            requestData.setId("" + i);
            requestData.setName("我是消息" + i);
            requestData.setRequestMessage("内容" + i);

            // 发送图片文件
            String path = System.getProperty("user.dir") + File.separatorChar + "netty-part-one" + File.separatorChar + "source" + File.separatorChar + "001.jpg";
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();
            requestData.setAttachment(GzipUtils.gzip(fileData));

            // 写到缓冲区并刷到网络中
            channel.writeAndFlush(requestData);
        }

        // 同步关闭, 释放资源
        channel.closeFuture().sync();
        workerGroup.shutdownGracefully();
    }
}
