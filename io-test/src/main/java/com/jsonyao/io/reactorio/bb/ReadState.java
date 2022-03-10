package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 读取状态
 */
public class ReadState implements HandlerState {

    private SelectionKey selectionKey;

    @Override
    public void handle(ChannelHandler handler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException {
        this.selectionKey = selectionKey;

        // non-blocking下不可用Readers, 因为Readers不支持non-blocking
        byte[] bytes = new byte[1024];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        // 读取字符串
        int numReadBytes = socketChannel.read(byteBuffer);
        if(numReadBytes == -1) {
            System.out.println("Warning! A client has bean closed...");
            handler.closeChannel();
            return;
        }

        // byte[] => String
        String str = new String(bytes);
        if(!"".equals(str)) {
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + ">" + str);

            // 开始处理业务
            handler.setState(new WorkState(str, handler, selectionKey, socketChannel, pool));
        }
    }
}
