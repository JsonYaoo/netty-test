package com.jsonyao.netty.marshalling;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * JBoss Marshalling快速入门: 客户端业务响应处理类
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收服务器响应并进行业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ResponseData responseData = (ResponseData) msg;
            System.err.println("输出服务器响应内容: " + responseData.getId());
        } finally {
            // 释放buffer资源 => 客户端需要, 服务端不需要
            ReferenceCountUtil.release(msg);
        }
    }
}
