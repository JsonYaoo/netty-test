package com.jsonyao.netty.protobuf.transfer;

import com.jsonyao.netty.protobuf.RequestModule;
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

/**
 * Google Protocol Buffers整合Netty: 服务端
 */
public class ProtobufServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认值
			}
		}
		new ProtobufServer().bind(port);
	}

    public void bind(int port) throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
		    ServerBootstrap b = new ServerBootstrap();
		    b.group(bossGroup, workerGroup)
			    .channel(NioServerSocketChannel.class)
			    .option(ChannelOption.SO_BACKLOG, 100)
			    .handler(new LoggingHandler(LogLevel.INFO))
			    .childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel sc) {
						// 1. 对protobuf报文头做解码处理
						sc.pipeline().addLast(new ProtobufVarint32FrameDecoder());

						// 2. 对protobuf报文Request实体做解码处理
						sc.pipeline().addLast(new ProtobufDecoder(RequestModule.Request.getDefaultInstance()));

						// 3. protobuf报文伪装者 => 用于发送数据时
						sc.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());

						// 4. protobuf报文编码器 => 必须在3.之后
						sc.pipeline().addLast(new ProtobufEncoder());

						// 5. 实际服务端接收业务处理类
						sc.pipeline().addLast(new ProtobufServerHandler());
					}
			    });
	
		    // 绑定端口，同步等待成功
		    ChannelFuture f = b.bind(port).sync();
		    System.err.println("ProtobufServer Start .. ");

		    // 等待服务端监听端口关闭
		    f.channel().closeFuture().sync();
		} finally {
		    // 优雅退出，释放线程池资源
		    bossGroup.shutdownGracefully();
		    workerGroup.shutdownGracefully();
		}
    }
}
