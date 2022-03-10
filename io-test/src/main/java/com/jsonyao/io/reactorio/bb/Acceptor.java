package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.LockSupport;

/**
 * 2、Acceptor
 */
public class Acceptor implements Runnable {

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private final ServerSocketChannel serverSocketChannel;
    private final Selector[] selectors = new Selector[CORES];
    private int selIndex = 0;
    private SubReactor[] subReactors = new SubReactor[CORES];
    private Thread[] threads = new Thread[CORES];

    public Acceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        this.serverSocketChannel = serverSocketChannel;

        // 创建多个selector, 以及多个SubReactor线程
        for (int i = 0; i < CORES; i++) {
            this.selectors[i] = Selector.open();
            this.subReactors[i] = new SubReactor(this.selectors[i], serverSocketChannel, i);
            this.threads[i] = new Thread(this.subReactors[i]);
            this.threads[i].start();
        }
    }

    @Override
    public void run() {
        try {
            // OP_ACCEPT
            SocketChannel socketChannel = this.serverSocketChannel.accept();
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + " is connected...");

            // non blocking
            socketChannel.configureBlocking(false);

            // 暂停SubReactor线程, 停止轮训
            SubReactor subReactor = this.subReactors[this.selIndex];
            Selector selector = this.selectors[this.selIndex];
            Thread thread = this.threads[this.selIndex];
            subReactor.setRestart(true);

            // 唤醒一个阻塞的selector
            selector.wakeup();

            // ChannelHandler: 绑定OP_READ, 从selector用于处理读取、返回
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(new ChannelHandler(selectionKey, socketChannel));

            // 重启SubReactor线程, 只有为false才会继续轮训
            subReactor.setRestart(false);
            LockSupport.unpark(thread);

            // 轮训重置selIndex
            if(++this.selIndex == this.selectors.length) {
                this.selIndex = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
