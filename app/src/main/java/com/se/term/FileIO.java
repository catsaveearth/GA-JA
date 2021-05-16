package com.se.term;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

public class FileIO {
    public void FileWriter(File file, byte[] b) {
        //insert code
        try {
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(b);
            fout.close();
        } catch (IOException e) {

        }
    }

    public byte[] FileReader(File file) {
        byte[] error = {0x00};
        byte[] b = new byte[256];
        try {
            FileInputStream fin = new FileInputStream(file);
            if (file.exists()) {
                System.out.println("exist\n");
                int length = fin.read(b);
                byte[] readBytes = new byte[length];
                System.arraycopy(b, 0, readBytes, 0, length);
                return readBytes;
            } else {
                return error;
            }
        } catch (IOException e) {

        }
        return error;
    }
}
