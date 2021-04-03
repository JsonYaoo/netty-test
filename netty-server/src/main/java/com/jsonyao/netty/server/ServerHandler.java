package com.jsonyao.netty.server;

import com.jsonyao.netty.common.execute.MessageTask4Request;
import com.jsonyao.netty.common.protobuf.MessageModule;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Netty整合SpringBoot: 服务端业务处理类
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 将Reactor模型中的worker执行实际业务处理操作, 优化成异步操作:
     *      => 任务判断顺序: 看核心线程数 -> 看任务队列 -> 看最大线程数 -> 看拒绝策略
     */
    ThreadPoolExecutor workerPool = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4000), new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 服务端业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageModule.Message request = (MessageModule.Message) msg;
        workerPool.submit(new MessageTask4Request(ctx, request));
    }
}
