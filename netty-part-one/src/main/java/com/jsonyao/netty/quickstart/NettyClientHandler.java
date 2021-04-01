package com.jsonyao.netty.quickstart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Netty快速入门: 客户端业务处理器
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道激活方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Netty client channel active... ");
    }

    /**
     * 通道关闭方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Netty client channel inactive... ");
    }

    /**
     * 读写数据核心方法 => 用于处理服务端回写的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 固定模式的 try .. finally
        // 在try代码片段处理逻辑, finally进行释放缓存资源, 也就是 Object msg (buffer)
        try {
            // 1. 获取TCP包缓冲区数据
            ByteBuf byteBuf = (ByteBuf) msg;

            // 2. 根据缓冲数据大小构造字节数组
            byte[] bytes = new byte[byteBuf.readableBytes()];

            // 3. 读取到缓冲区数据到字节数组中
            byteBuf.readBytes(bytes);

            // 4. 使用UTF-8编码解码字节数组成字符串
            String body = new String(bytes, "utf-8");

            // 5. 构造响应体给客户端 => 测试与客户端的交互
            System.err.println("Netty client receive ack: " + body);
        } finally {
            // 6. 释放资源
            ReferenceCountUtil.release(msg);
        }
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
        // 客户端读写异常, 则关闭连接
        ctx.close();
    }
}
