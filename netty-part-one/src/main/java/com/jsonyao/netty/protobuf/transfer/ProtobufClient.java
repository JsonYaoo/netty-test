package com.jsonyao.netty.protobuf.transfer;

import com.jsonyao.netty.protobuf.ResponseModule;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Google Protocol Buffers整合Netty: 客户端
 */
public class ProtobufClient {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认值
			}
		}
		new ProtobufClient().connect(port, "127.0.0.1");
	}

    public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
		    Bootstrap b = new Bootstrap();
		    b.group(group).channel(NioSocketChannel.class)
			    .option(ChannelOption.TCP_NODELAY, true)
			    .handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel sc) throws Exception {
						// 1. 对protobuf报文头做解码处理
						sc.pipeline().addLast(new ProtobufVarint32FrameDecoder());

						// 2. 对protobuf报文Response实体做解码处理
						sc.pipeline().addLast(new ProtobufDecoder(ResponseModule.Response.getDefaultInstance()));

						// 3. protobuf报文伪装者 => 用于发送数据时
						sc.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());

						// 4. protobuf报文编码器 => 必须在3.之后
						sc.pipeline().addLast(new ProtobufEncoder());

						// 5. 实际客户端响应业务处理类
						sc.pipeline().addLast(new ProtobufClientHandler());
					}
			    });
	
		    // 发起异步连接操作
		    ChannelFuture f = b.connect(host, port).sync();
		    System.err.println("ProtobufClient Start .. ");
		    // 当代客户端链路关闭
		    f.channel().closeFuture().sync();
		} finally {
		    // 优雅退出，释放NIO线程组
		    group.shutdownGracefully();
		}
    }
}
