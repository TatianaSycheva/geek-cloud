package com.geekbrains;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public class CloudServer {
    public static void main(String[] args) {
        DaemonThreadFactory servisThreadFactory = new DaemonThreadFactory();


        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true) {
                Socket socket = serverSocket.accept();
                servisThreadFactory.getThread(
                        new FileHandler(socket), "file-handler-thread").start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
