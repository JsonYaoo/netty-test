package com.jsonyao.netty.server;

import com.jsonyao.netty.common.protobuf.MessageModule;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Netty整合SpringBoot: 服务端
 */
@Component
public class Server {

	public Server(){
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		try {
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
			    @Override
			    public void initChannel(SocketChannel sc) throws IOException {
					sc.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					sc.pipeline().addLast(new ProtobufDecoder(MessageModule.Message.getDefaultInstance()));
					sc.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					sc.pipeline().addLast(new ProtobufEncoder());
					sc.pipeline().addLast(new ServerHandler());
			    }
			});
			
			int port = 8888;
			// 	绑定端口，同步等待成功
			ChannelFuture cf = b.bind(port).sync();
			
			System.out.println(" Server startup.. port: " + port);
			cf.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();	
			System.out.println(" Server shutdown.. ");
		}		
	}
}
