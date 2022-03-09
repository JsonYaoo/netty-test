package com.jsonyao.io.bio;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BIO 客户端测试
 */
public class BioClientTest implements Cloneable {

    public static final String[] commands = new String[]{
            "hi\n",
            "i am client\n",
            "helloworld\n",
            "java and netty\n"
    };

    public static void main(String[] args) throws IOException {
        BioClientTest bioClientTest = new BioClientTest();
        try {
            Object clone = bioClientTest.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        int concurrent = 100;
        Runnable task = () -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8084);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                for (String str : commands) {
                    out.write(str.getBytes());
                }
                out.flush();

                Thread.sleep(100);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (br.ready()) {
                    Thread.sleep(100);
                    System.out.println(br.readLine());
                }

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024));
        for (int i = 0; i < concurrent; i++) {
            executor.execute(task);
        }
        executor.shutdown();
    }
}
