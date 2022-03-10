package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ChannelHandler
 */
public class ChannelHandler implements Runnable {

    private static final int DEFAULT_THREAD_COUNT = 4;
    private static final int MAX_THREAD_COUNT = 8;
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            DEFAULT_THREAD_COUNT,
            MAX_THREAD_COUNT,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024)
    );

    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;
    private HandlerState state;

    public ChannelHandler(SelectionKey selectionKey, SocketChannel socketChannel) {
        this.selectionKey = selectionKey;
        this.socketChannel = socketChannel;

        // 初始默认为read状态
        state = new ReadState();
    }

    @Override
    public void run() {
        try {
            state.handle(this, this.selectionKey, this.socketChannel, POOL);
        } catch (IOException e) {
            System.out.println("Waring! A Client has been closed...");
            closeChannel();
        }
    }

    public void closeChannel() {
        selectionKey.cancel();
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setState(HandlerState state) {
        this.state = state;
    }
}
