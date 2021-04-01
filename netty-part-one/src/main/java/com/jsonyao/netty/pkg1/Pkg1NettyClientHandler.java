package com.jsonyao.netty.pkg1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 测试Netty: 客户端业务处理器, 测试消息定长方式解决TCP拆包/粘包问题
 */
public class Pkg1NettyClientHandler extends ChannelInboundHandlerAdapter {

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
            // 1. 使用了Netty String解码器后, 可以直接转换成String类型
            String response = (String) msg;

            // 5. 构造响应体给客户端 => 测试与客户端的交互
            System.err.println("Netty client: " + response);
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
