package com.jsonyao.netty.marshalling;

import com.jsonyao.netty.utils.GzipUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;
import java.io.FileOutputStream;

/**
 * JBoss Marshalling快速入门: 服务端业务处理类
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收Client请求并进行业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // marshalling自动解码成RequestData对象
        RequestData requestData = (RequestData) msg;
        System.err.println("id: " + requestData.getId() + ", name: " + requestData.getName() + ", requestMessage: " + requestData.getRequestMessage());

        // 处理文件压缩数组
        byte[] attachment = GzipUtils.ungzip(requestData.getAttachment());
        String path = System.getProperty("user.dir") + File.separatorChar + "netty-part-one" + File.separatorChar + "receive" + File.separatorChar + "001.jpg";
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(attachment);
        fos.close();

        // 响应客户端
        ResponseData responseData = new ResponseData();
        responseData.setId("response: " + requestData.getId());
        responseData.setName("response: " + requestData.getName());
        responseData.setResponseMessage("响应信息: " + requestData.getRequestMessage());
        ctx.writeAndFlush(responseData);
    }
}
