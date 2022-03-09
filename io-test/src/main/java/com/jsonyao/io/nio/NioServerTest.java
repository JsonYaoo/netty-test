package com.jsonyao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * NIO 服务端测试
 */
public class NioServerTest {

    public static void start(int port) throws IOException {
        // non blocking
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // bind & listen
        InetSocketAddress address = new InetSocketAddress(port);
        serverChannel.bind(address);
        System.err.println("bind & listen");

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.err.println("注册事件: OP_ACCEPT");

        while (true) {
            // epoll: blocking
            selector.select();

            // OP_ACCEPT | OP_READ
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();

                // OP_ACCEPT: 三次握手成功, 注册读事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socket = server.accept();
                    System.err.println("Accept !");

                    socket.configureBlocking(false);
                    socket.register(selector, SelectionKey.OP_READ);
                    System.err.println("注册事件: OP_READ");
                } else if (key.isReadable()) {
                    SocketChannel socket = (SocketChannel) key.channel();
                    final ByteBuffer buffer = ByteBuffer.allocate(64);
                    final int bytesRead = socket.read(buffer);
                    if (bytesRead > 0) {
                        System.err.println("read: " + new String(buffer.array()).trim());
                        buffer.flip();
                        int ret = socket.write(buffer);
                        // 尝试写, 没写完则要注册写事件
//                         if (ret <=0) {
//                        	 //register op_write
//                         }
                        buffer.clear();
                    } else if (bytesRead < 0) {
                        key.cancel();
                        socket.close();
                        System.err.println("Client close");
                    }
                }

                // 从readyKeys中移除, 免得下次重新执行
                it.remove();
            }
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        start(8084);
    }
}	
