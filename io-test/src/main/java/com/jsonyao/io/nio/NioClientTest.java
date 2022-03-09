package com.jsonyao.io.nio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * NIO 客户端1测试
 */
public class NioClientTest {

    public static final String[] commands = new String[]{
            "hi\n",
            "i am client\n",
            "helloworld\n",
            "java and netty\n"
    };

    public static void main(String[] args) throws IOException {
        int concurrent = 100;
        Runnable task = () -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8084);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                for (String str : commands) {
                    out.write(str.getBytes());
                }
                out.flush();

                int count = 0;
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (count < 4) {
                    System.out.println(br.readLine());
                    count++;
                }

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024));
        for (int i = 0; i < concurrent; i++) {
            executor.execute(task);
        }
        executor.shutdown();
    }
}
