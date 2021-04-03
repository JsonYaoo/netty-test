package com.jsonyao.netty.marshalling;

import com.jsonyao.netty.utils.GzipUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * JBoss Marshalling快速入门: 服务端业务处理类
 */
public class MarshallingServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收Client请求并进行业务处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // marshalling自动解码成RequestData对象
        MarshallingRequestData marshallingRequestData = (MarshallingRequestData) msg;
        System.err.println("id: " + marshallingRequestData.getId() + ", name: " + marshallingRequestData.getName() + ", requestMessage: " + marshallingRequestData.getRequestMessage());

        // 处理文件压缩数组
        byte[] attachment = GzipUtils.ungzip(marshallingRequestData.getAttachment());
        String path = System.getProperty("user.dir") + File.separatorChar + "netty-part-one" + File.separatorChar + "receive" + File.separatorChar + "001.jpg";
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(attachment);
        fos.close();

        // 响应客户端
        MarshallingResponseData marshallingResponseData = new MarshallingResponseData();
        marshallingResponseData.setId("response: " + marshallingRequestData.getId());
        marshallingResponseData.setName("response: " + marshallingRequestData.getName());
        marshallingResponseData.setResponseMessage("响应信息: " + marshallingRequestData.getRequestMessage());
        ctx.writeAndFlush(marshallingResponseData);
    }
}
