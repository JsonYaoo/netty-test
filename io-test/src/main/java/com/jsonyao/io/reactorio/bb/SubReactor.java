package com.jsonyao.io.reactorio.bb;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

public class SubReactor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private volatile boolean restart = false;
    private int num;

    public SubReactor(Selector selector, ServerSocketChannel serverSocketChannel, int num) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            if(this.restart) {
                LockSupport.park();
            }

            System.out.println("SubReactor"+ this.num + " waiting for new event on port: " + this.serverSocketChannel.socket().getLocalPort() + "...");
            try {
                // 如果没有事件发生, 则继续自旋
                if(this.selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 有事件发生, 则取出所有发生的事件
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // 根据事件进行派发
                dispatch(iterator.next());

                // 派发完毕, 从selectionKeys中移除, 免得下次重新执行
                iterator.remove();
            }
        }
    }

    // 根据事件进行派发
    private void dispatch(SelectionKey key) {
        // 根据key绑定的对象, 开辟新线程
        Runnable runnable = (Runnable) key.attachment();
        if(runnable != null) {
            runnable.run();
        }
    }

    // 重启SubReactor线程, 只有为false才会继续轮训
    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
