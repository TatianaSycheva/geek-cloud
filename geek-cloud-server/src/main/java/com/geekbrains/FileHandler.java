package com.geekbrains;

import java.io.*;
import java.net.Socket;

public class FileHandler implements Runnable {
    private static final String SERVER_DIR = "server_files";
    private static final Integer BATCH_SIZE = 256;
    private final DataInputStream dis;
    private static final String SEND_FILE_COMMAND = "file";
    private final byte[] batch;


    public FileHandler(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());
        //DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        batch = new byte[BATCH_SIZE];
        File file = new File(SERVER_DIR);
        if(!file.exists()) {
            file.mkdir();
        }
        System.out.println("Client accepted...");
     }

    @Override
    public void run() {
        try {
            System.out.println("Start listening...");
            while (true) {
                String command = dis.readUTF();
                if(command.equals(SEND_FILE_COMMAND)) {
                    String fileName = dis.readUTF();
                    long size = dis.readLong();
                    try(FileOutputStream fos = new FileOutputStream(SERVER_DIR + "/" + fileName)) {
                        for (int i = 0; i < (size + BATCH_SIZE - 1) / BATCH_SIZE; i++) {
                            int read = dis.read(batch);
                            fos.write(batch, 0, read);
                        }
                    }catch (Exception ignored){}

                }else {
                    System.out.println("Unknown command received: " + command);
                }
            }

        } catch (Exception ignored) {
            System.out.println("Client disconnected...");
        }
    }
}
