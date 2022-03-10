package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 8、返回状态
 */
public class WriteState implements HandlerState {

    @Override
    public void handle(ChannelHandler handler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException {
        // 返回数据
        String str = "Your message has sent to " + socketChannel.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }

        // 返回 -> 读取
        handler.setState(new ReadState());
        selectionKey.interestOps(SelectionKey.OP_READ);

        // 唤醒一个阻塞的selector
        selectionKey.selector().wakeup();
    }
}
