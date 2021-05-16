package com.se.term;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class AutoLoginProvider {

    FileIO io = new FileIO();
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/logininfo", "login.dat");
    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/logininfo");
    EncryptModule encryptModule = new EncryptModule();

    public void AutoLoginWriter(String email, String password) {
        String string = email + "$" + password;
        byte[] encrypted = encryptModule.encrypt(string.getBytes());
        if (!dir.exists()) {
            dir.mkdir();
        }
        io.FileWriter(file, encrypted);
    }

    public String[] AutoLoginReader() {
        String[] information = new String[2]; //information[0] : email / information[1] : password
        byte[] bytes = io.FileReader(file);
        byte[] decrypted = encryptModule.decrypt(bytes);
        String string = new String(decrypted);
        int index = string.indexOf('$');
        information[0] = string.substring(0, index);
        information[1] = string.substring(index + 1);
        return information;
    }

    public boolean AutoLoginRemover() {
        if (file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    public boolean AutoLoginChecker() {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
