package com.jsonyao.io.reactorio.aa;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * ChannelHandler
 */
public class ChannelHandler implements Runnable {

    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;
    int state;

    public ChannelHandler(SelectionKey selectionKey, SocketChannel socketChannel) {
        this.selectionKey = selectionKey;
        this.socketChannel = socketChannel;

        // 初始默认为read状态
        state = 0;
    }

    @Override
    public void run() {
        try {
            // 读取
            if(this.state == 0) {
                read();
            }
            // 返回
            else {
                send();
            }
        } catch (IOException e) {
            System.out.println("Waring! A Client has been closed...");
            closeChannel();
        }
    }

    private synchronized void read() throws IOException {
        // non-blocking下不可用Readers, 因为Readers不支持non-blocking
        byte[] bytes = new byte[1024];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        // 读取字符串
        int numReadBytes = this.socketChannel.read(byteBuffer);
        if(numReadBytes == -1) {
            System.out.println("Warning! A client has bean closed...");
            closeChannel();
            return;
        }

        // byte[] => String
        String str = new String(bytes);
        if(!"".equals(str)) {
            // 执行业务处理
            doBusiness(str);
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + ">" + str);

            // 读取 -> 返回
            this.state = 1;
            this.selectionKey.interestOps(SelectionKey.OP_WRITE);

            // 唤醒一个阻塞的selector
            selectionKey.selector().wakeup();
        }
    }

    private void doBusiness(String str) {
        System.err.println("执行业务处理...");
    }

    private void closeChannel() {
        selectionKey.cancel();
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        // 返回数据
        String str = "Your message has sent to " + socketChannel.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }

        // 返回 -> 读取
        this.state = 0;
        this.selectionKey.interestOps(SelectionKey.OP_READ);

        // 唤醒一个阻塞的selector
        selectionKey.selector().wakeup();
    }
}
