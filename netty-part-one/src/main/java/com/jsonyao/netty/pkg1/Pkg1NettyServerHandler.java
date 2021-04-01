package com.jsonyao.netty.pkg1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 测试Netty: 服务端业务处理器, 测试消息定长方式解决TCP拆包/粘包问题
 */
public class Pkg1NettyServerHandler extends ChannelInboundHandlerAdapter {

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
        // 1. 使用了Netty String解码器后, 可以直接转换成String类型
        String body = (String) msg;
        System.err.println("Netty server: " + body);

        // 2. 构造响应体给客户端 => 测试与客户端的交互
        ctx.writeAndFlush(Unpooled.copiedBuffer(body.getBytes()));
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
