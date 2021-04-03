package com.jsonyao.netty.common.execute;

import com.jsonyao.netty.common.protobuf.MessageBuilder;
import com.jsonyao.netty.common.protobuf.MessageModule;
import com.jsonyao.netty.common.protobuf.Result;
import com.jsonyao.netty.common.scanner.Invoker;
import com.jsonyao.netty.common.scanner.InvokerTable;
import io.netty.channel.ChannelHandlerContext;

/**
 * Netty最佳实践: 包装Request请求成Task任务
 */
public class MessageTask4Request implements Runnable{

    private static final String RETURN_SUFFIX = "-return";

    /**
     * Server Channel上线文实例
     */
    private ChannelHandlerContext ctx;

    /**
     * Server接收到的Request实例
     */
    private MessageModule.Message message;

    public MessageTask4Request(ChannelHandlerContext ctx, MessageModule.Message message) {
        this.ctx = ctx;
        this.message = message;
    }

    /**
     * 执行实际业务处理
     */
    @Override
    public void run() {
        String module = message.getModule();
        String cmd = message.getCmd();
        byte[] data = message.getBody().toByteArray();

        // 获取到Invoker实例后, 调用自定义的方法反射调用相应的方法
        Invoker invoker = InvokerTable.getInvoker(module, cmd);
        Result<?> result = (Result<?>) invoker.invoke(data);

        // 响应客户端 => 必须是MessageModule.Message对象
        ctx.writeAndFlush(MessageBuilder.getResponseMessage(module + RETURN_SUFFIX, cmd + RETURN_SUFFIX, result.getResultType(), result.getContent()));
    }
}
