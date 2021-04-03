package com.jsonyao.netty.client;

import com.google.protobuf.GeneratedMessageV3;
import com.jsonyao.netty.common.protobuf.MessageBuilder;
import com.jsonyao.netty.common.protobuf.MessageModule;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty整合SpringBoot: 客户端
 */
// 不再交由Spring管理, 因为如果交由Spring管理, 调用构造方法时会调用connect方法会同步阻塞SpringBoot的主线程, 导致SpringBoot应用起不来
//@Component
public class Client {

	public static final String HOST = "127.0.0.1";
	public static final int PORT = 8888;
	public static final String VIP_HOST = "192.168.1.100";
	public static final int VIP_PORT = 8888;
	private static class SingletonHolder {
		// 单例模式: 懒汉式
		private static final Client INSTANCE = new Client();
	}
	public static final Client getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup(2);
	private Channel channel;

	private Client() {
		try {
//			this.connect(HOST, PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Spring应用准备完毕后, 调用init方法时建立连接: 使用了AtomicBoolean类型, 所以只能建立一次
	 */
	private AtomicBoolean isConnect = new AtomicBoolean(false);
	public synchronized void init() {
		if(!isConnect.get()) {
			try {
				this.connect(VIP_HOST, VIP_PORT);
				isConnect.set(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    private void connect(String host, int port) throws Exception {
    	// 配置客户端NIO线程组
		try {
		    Bootstrap b = new Bootstrap();
		    b.group(group).channel(NioSocketChannel.class)
			    .option(ChannelOption.TCP_NODELAY, true)
			    .handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel sc) throws Exception {
						sc.pipeline().addLast(new ProtobufVarint32FrameDecoder());
						sc.pipeline().addLast(new ProtobufDecoder(MessageModule.Message.getDefaultInstance()));
						sc.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
						sc.pipeline().addLast(new ProtobufEncoder());
						sc.pipeline().addLast(new ClientHandler());
					}
			    });
		    // 发起异步连接操作
		    ChannelFuture future = b.connect(host, port).sync();
		    this.channel = future.channel();
		    System.out.println("Client Start.. ");
		    this.channel.closeFuture().sync();
		} finally {
		    // 	所有资源释放完成之后，清空资源，再次发起重连操作
		    executor.execute(new Runnable() {
				@Override
				public void run() {
				    try {
						TimeUnit.SECONDS.sleep(1);
						try {
						    connect(host, port);// 发起重连操作
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
	 * 	$sendMessage
	 * 发送数据的方法
	 * @param module 模块
	 * @param cmd 指令
	 * @param messageData 数据内容
	 */
	public void sendMessage(String module, String cmd , GeneratedMessageV3 messageData) {
		this.channel.writeAndFlush(MessageBuilder.getRequestMessage(module, cmd, messageData));
	}
}