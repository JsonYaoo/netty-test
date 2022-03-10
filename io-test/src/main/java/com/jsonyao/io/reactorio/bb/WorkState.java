package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 7、工作状态
 */
public class WorkState implements HandlerState {

    private String readResult;
    private SelectionKey selectionKey;

    public WorkState(String readResult, ChannelHandler handler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) {
        this.readResult = readResult;
        this.selectionKey = selectionKey;

        try {
            this.handle(handler, selectionKey, socketChannel, pool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ChannelHandler handler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException {
        pool.execute(new WorkerThread(handler, this.readResult));
    }

    private synchronized void change2WriteState(ChannelHandler handler, String str) {
        // 读取 -> 返回
        handler.setState(new WriteState());
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);

        // 唤醒一个阻塞的selector
        this.selectionKey.selector().wakeup();
    }

    // 工作线程用于处理业务逻辑
    class WorkerThread implements Runnable {

        private ChannelHandler handler;
        private String str;

        public WorkerThread(ChannelHandler handler, String str) {
            this.handler = handler;
            this.str = str;
        }

        @Override
        public void run() {
            // 执行业务处理
            doBusiness(str);
            change2WriteState(this.handler, this.str);
        }

        private void doBusiness(String str) {
            System.err.println("执行业务处理...");
        }
    }
}
