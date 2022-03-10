package com.jsonyao.io.reactorio.ab;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 4、状态
 */
public interface HandlerState {

    /**
     * 状态处理事件
     *
     * @param handler
     * @param selectionKey
     * @param socketChannel
     * @param pool
     */
    void handle(ChannelHandler handler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException;
}

