package com.jsonyao.io.reactorio.aa;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 1、单Reactor单线程
 */
public class AaReactorServer implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public AaReactorServer(int port) throws IOException {
        // non blocking
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);

        // listen & bind
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // Acceptor: 绑定OP_ACCEPT
        this.selector = Selector.open();
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(this.selector, this.serverSocketChannel));
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            System.out.println("Waiting for new event on port: " + this.serverSocketChannel.socket().getLocalPort() + "...");
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

    public static void main(String[] args) {
        try {
            AaReactorServer aaReactorServer = new AaReactorServer(8084);
            aaReactorServer.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}