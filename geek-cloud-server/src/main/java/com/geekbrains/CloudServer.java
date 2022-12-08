package com.geekbrains;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public class CloudServer {
    public static void main(String[] args) {
        ThreadFactory servisThreadFactory =  r -> {
                Thread thread = new Thread(r);
                thread.setName("file-handler-thread");
                thread.setDaemon(true);
                return thread;

        };
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true) {
                Socket socket = serverSocket.accept();
                servisThreadFactory.newThread(new FileHandler(socket))
                        .start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}