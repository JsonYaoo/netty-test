package com.jsonyao.netty.quickstart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty快速入门: 服务端业务处理器
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道激活方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Netty server channel active... ");
    }

    /**
     * 通道关闭方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Netty server channel inactive... ");
    }

    /**
     * 读写数据核心方法 => 收到客户端连接则进行处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1. 获取TCP包缓冲区数据
        ByteBuf byteBuf = (ByteBuf) msg;

        // 2. 根据缓冲数据大小构造字节数组
        byte[] bytes = new byte[byteBuf.readableBytes()];

        // 3. 读取到缓冲区数据到字节数组中
        byteBuf.readBytes(bytes);

        // 4. 使用UTF-8编码解码字节数组成字符串
        String body = new String(bytes, "utf-8");
        System.err.println("Netty server: " + body);

        // 5. 构造响应体给客户端 => 测试与客户端的交互
        String response = "Netty server ack: " + body;

        // 6. 使用0拷贝(只拷贝1次)技术响应客户端
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
    }

    /**
     * 读写数据完毕方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Netty server channel read complete... ");
    }

    /**
     * 捕获异常方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
