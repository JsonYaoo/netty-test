package com.jsonyao.netty.protobuf.transfer;

import com.jsonyao.netty.protobuf.RequestModule;
import com.jsonyao.netty.protobuf.ResponseModule;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Google Protocol Buffers整合Netty: 客户端响应业务处理类
 */
public class ProtobufClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public ProtobufClientHandler() {
    }

    /**
     * 通道激活时请求服务端
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.err.println("客户端通道激活");
        for (int i = 0; i < 100; i++) {
            ctx.writeAndFlush(createRequest(i));
        }
    }

    /**
     * 构造RequestModule.Request
     * @param i
     * @return
     */
    private RequestModule.Request createRequest(int i) {
        return RequestModule.Request.newBuilder()
                .setId("主键: " + i)
                .setSequence(i)
                .setData("数据内容: " + i)
                .build();
    }

    /**
     * 客户端响应业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ResponseModule.Response response = (ResponseModule.Response) msg;
            System.err.println("客户端响应业务处理: " + response.getId() + "," + response.getCode() + "," + response.getDesc());
        } finally {
            // 释放buffer
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
    }
}
