package gachon.termproject.gaja.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

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
        byte[] b = new byte[10000000];
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

    public void FileAppender(File file, String s){
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(s);
            fw.close();
        } catch (IOException e) {

        }
    }
}