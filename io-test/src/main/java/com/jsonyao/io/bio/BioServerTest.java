package com.jsonyao.io.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BIO 服务端测试
 */
public class BioServerTest {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public static void start(int port) throws IOException {
        // socket
        ServerSocket server = new ServerSocket();
        // bind & listen
        server.bind(new InetSocketAddress(port));
        System.err.println("bind & listen!");

        while (true) {
            // accept
            final Socket socket = server.accept();// block!
            int count = COUNT.incrementAndGet();
            System.err.println("accept! ip=" + socket.getRemoteSocketAddress() + ", count=" + count);

            // or user thread pool
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String line = in.readLine();

                    while (line != null) {
                        System.err.println("read: " + line);
                        out.println(line);
                        out.flush();
                        line = in.readLine();
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException ee) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    public static void main(String[] args) throws IOException {
        start(8084);
    }
}
