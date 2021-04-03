package com.jsonyao.netty.protobuf.transfer;

import com.jsonyao.netty.protobuf.RequestModule;
import com.jsonyao.netty.protobuf.ResponseModule;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Google Protocol Buffers整合Netty: 服务端接收业务处理类
 */
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 服务端接收业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestModule.Request request = (RequestModule.Request) msg;
        System.err.println("服务端接收业务处理: " + request.getId() + "," + request.getSequence() + "," + request.getData());

        // 响应客户端
        ctx.writeAndFlush(createResponse(request.getId(), request.getSequence()));
    }

    /**
     * 构造ResponseModule.Response
     * @param id
     * @return
     */
    private ResponseModule.Response createResponse(String id, int sequence) {
        return ResponseModule.Response.newBuilder()
                .setId(id)
                .setCode(sequence)
                .setDesc("响应报文")
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
    }
}
