package com.jsonyao.io.reactorio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 客户端测试Reactor
 */
public class ReactorClient {

    public static void main(String[] args) {
        String hostName = "127.0.0.1";
        int port = 8084;
        System.out.println("Connecting to " + hostName + ":" + port + "...");

        // connect
        try {
            Socket clientSocket = new Socket(hostName, port);
            System.out.println("Connected to " + hostName + ":" + port + "!");

            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader bufferedReaderSin = new BufferedReader(new InputStreamReader(System.in));
            String input;

            while ((input = bufferedReaderSin.readLine()) != null) {
                printWriter.println(input);
                printWriter.flush();
                if("exit".equals(input)) {
                    break;
                }
                System.out.println("Server: " + bufferedReaderIn.readLine());
            }

            clientSocket.close();
            System.out.println("Client stop...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}