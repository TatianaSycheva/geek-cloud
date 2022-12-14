package com.geekbrains;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import static com.geekbrains.Command.*;
import static com.geekbrains.FileUtils.readFileFromStream;

public class FileHandler implements Runnable {
    private static final String SERVER_DIR = "server_files";
    private final DataInputStream dis;
    private final DataOutputStream dos;

    private static final Integer BATCH_SIZE = 256;


    public FileHandler(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        byte[] batch = new byte[BATCH_SIZE];
        File file = new File(SERVER_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        sendServerFiles();
        System.out.println("Client accepted...");
    }

    private void sendServerFiles() throws IOException {
        File dir = new File(SERVER_DIR);
        String[] files = dir.list();
        assert files != null;
        dos.writeUTF(GET_FILES_LIST_COMMAND.getSimpleName());
        dos.writeInt(files.length);
        for (String file : files) {
            dos.writeUTF(file);
        }
        dos.flush();
        System.out.println(files.length + " files sent to client");
    }

    @Override
    public void run() {
        try {
            System.out.println("Start listening...");
            while (true) {
                String command = dis.readUTF();
                System.out.println("Received command: " + command);
                if (command.equals(SEND_FILE_COMMAND.getSimpleName())) {
                    readFileFromStream(dis, SERVER_DIR);
                    sendServerFiles();

                } else if (GET_FILE_COMMAND.getSimpleName().equals(command)) {
                    String fileName = dis.readUTF();
                    String filePath = SERVER_DIR + "/" + fileName;
                    File file = new File(filePath);
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (basicFileAttributes.isRegularFile()) {
                        try {
                            System.out.println("File: " + fileName + " sent to client");
                            dos.writeUTF(SEND_FILE_COMMAND.getSimpleName());
                            dos.writeUTF(fileName);
                            dos.writeLong(basicFileAttributes.size());
                            try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] bytes = fis.readAllBytes();
                                dos.write(bytes);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (Exception e) {
                            System.err.println("e" + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Unknown command received: " + command);
                }
            }

        } catch (Exception ignored) {
            System.out.println("Client disconnected...");
        }
    }
}
