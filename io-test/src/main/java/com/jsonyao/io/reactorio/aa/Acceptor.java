package com.jsonyao.io.reactorio.aa;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 2、Acceptor
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            // OP_ACCEPT
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + " is connected...");

            // non blocking
            socketChannel.configureBlocking(false);

            // ChannelHandler: 绑定OP_READ
            SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ);
            selectionKey.attach(new ChannelHandler(selectionKey, socketChannel));

            // 唤醒一个阻塞的selector
            this.selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
