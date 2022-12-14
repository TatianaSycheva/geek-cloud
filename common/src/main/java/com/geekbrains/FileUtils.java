package com.geekbrains;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    private static final int BATCH_SIZE = 256;
    public static void readFileFromStream(DataInputStream dis, String dstDirectory) throws IOException {
        byte[] batch = new byte[BATCH_SIZE];
        String fileName = dis.readUTF();
        System.out.print("Filename: " + fileName);
        long size = dis.readLong();
        System.out.println(" | Fil size: " + size);

        try (
                FileOutputStream fos = new FileOutputStream(dstDirectory + "/" + fileName)) {
            for (int i = 0; i < (size + BATCH_SIZE - 1) / BATCH_SIZE; i++) {
                int read = dis.read(batch);
                fos.write(batch, 0, read);
            }
        } catch (Exception ignored) {
        }
    }
}
