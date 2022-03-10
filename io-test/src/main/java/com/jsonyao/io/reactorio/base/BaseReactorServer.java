package com.jsonyao.io.reactorio.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor 原型
 */
public class BaseReactorServer {

    interface ChannelHandler {
        void onRead(SocketChannel channel) throws Exception;
        void onAccept();
    }

    public static void start(int port) throws Exception {
        // non blocking
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        //bind & listen
        InetSocketAddress address = new InetSocketAddress(port);
        serverChannel.bind(address);

        final Selector selector = Selector.open();
        SelectionKey selectionKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.err.println("注册事件: OP_ACCEPT");

        // Acceptor: 绑定OP_ACCEPT
        selectionKey.attach(new ChannelHandler() {
            public void onRead(SocketChannel channel) {

            }

            // ChannelHandler: 绑定OP_READ
            public void onAccept() {
                try {
                    SocketChannel socket = serverChannel.accept();
                    System.out.println("Accept !");
                    socket.configureBlocking(false);
                    SelectionKey sk = socket.register(selector, SelectionKey.OP_READ);

                    // 绑定
                    sk.attach(new ChannelHandler() {
                        public void onRead(SocketChannel socket) throws IOException {
                            final ByteBuffer buffer = ByteBuffer.allocate(256);
                            final int bytesRead = socket.read(buffer);

                            // Worker
                            if (bytesRead > 0) {
                                // 读
                                String readRes = new String(buffer.array()).trim();
                                System.err.println("read: " + readRes);

                                // 模拟业务执行过久
                                doBusiness(500, readRes);

                                // 写
                                buffer.flip();
                                socket.write(buffer);
                                buffer.clear();
                            } else if (bytesRead < 0) {
                                socket.close();
                                System.out.println("Client close");
                            }
                        }

                        public void onAccept() {

                        }

                        // 模拟业务执行过久
                        private void doBusiness(long time, String readRes) {
                            System.err.println("测试业务处理: time=" + time);
                            try {
                                Thread.sleep(time);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        while (true) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                ChannelHandler handler = (ChannelHandler) key.attachment();
                System.err.println(String.format("handler: %s", handler));

                // OP_ACCEPT
                if (key.isAcceptable()) {
                    handler.onAccept();
                }
                // OP_READ
                else if (key.isReadable()) {
                    handler.onRead((SocketChannel) key.channel());
                }

                // 从readyKeys中移除, 免得下次重新执行
                it.remove();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        start(8084);
    }
}
