package com.jsonyao.netty.common.execute;

import com.jsonyao.netty.common.protobuf.MessageModule;
import com.jsonyao.netty.common.scanner.Invoker;
import com.jsonyao.netty.common.scanner.InvokerTable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Netty最佳实践: 包装Response请求成Task任务
 */
public class MessageTask4Response implements Runnable{

    /**
     * Server Channel上线文实例
     */
    private ChannelHandlerContext ctx;

    /**
     * Server接收到的Response实例
     */
    private MessageModule.Message message;

    public MessageTask4Response(ChannelHandlerContext ctx, MessageModule.Message message) {
        this.ctx = ctx;
        this.message = message;
    }

    /**
     * 执行实际业务处理
     */
    @Override
    public void run() {
        try {
            // eg => user-return#save-return
            String module = message.getModule();
            String cmd = message.getCmd();
            MessageModule.ResultType resultType = message.getResultType();
            byte[] data = message.getBody().toByteArray();

            // 获取到Invoker实例后, 调用自定义的方法反射调用相应的方法 => 模拟处理客户端的相应逻辑
            Invoker invoker = InvokerTable.getInvoker(module, cmd);
            invoker.invoke(resultType, data);
        } finally {
            // 释放Buffer资源 => 所以不能把message引用往后传
            ReferenceCountUtil.release(message);
        }
    }
}
