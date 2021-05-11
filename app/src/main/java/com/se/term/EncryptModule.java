package com.se.term;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptModule {

    RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator();

    public byte[] encrypt(byte[] buffer) { // 바이트암호화
        byte[] check_sum = new byte[20];

        check_sum = checksum(buffer);

        byte seed1[] = new byte[4];
        byte seed2[] = new byte[4];
        byte seed3[] = new byte[4];
        byte seed4[] = new byte[4];

        System.arraycopy(check_sum, 0, seed1, 0, 4); // 체크섬기반으로 시드생성
        System.arraycopy(check_sum, 4, seed2, 0, 4);
        System.arraycopy(check_sum, 8, seed3, 0, 4);
        System.arraycopy(check_sum, 12, seed4, 0, 4);

        byte key[] = new byte[16];
        byte IV[] = new byte[16];
        byte tempkey[] = new byte[4];

        tempkey = randomNumberGenerator.MT19937(seed1); // key와 초기화벡터 생성
        System.arraycopy(tempkey, 0, key, 0, 4);
        System.arraycopy(tempkey, 4, IV, 0, 4);
        tempkey = randomNumberGenerator.MT19937(seed2);
        System.arraycopy(tempkey, 0, key, 4, 4);
        System.arraycopy(tempkey, 4, IV, 4, 4);
        tempkey = randomNumberGenerator.MT19937(seed3);
        System.arraycopy(tempkey, 0, key, 8, 4);
        System.arraycopy(tempkey, 4, IV, 8, 4);
        tempkey = randomNumberGenerator.MT19937(seed4);
        System.arraycopy(tempkey, 0, key, 12, 4);
        System.arraycopy(tempkey, 4, IV, 12, 4);

        // AES128 암호화 실행 key = key[]
        Key AES_key = new SecretKeySpec(key, "AES");
        Cipher AES;
        byte[] result = null;

        try {
            AES = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AES.init(Cipher.ENCRYPT_MODE, AES_key, new IvParameterSpec(IV));
            result = AES.doFinal(buffer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        byte[] send = new byte[result.length + 20];
        System.arraycopy(result, 0, send, 0, result.length);
        System.arraycopy(check_sum, 0, send, result.length, 20);

        return send;
    }

    public byte[] decrypt(byte[] b) { // 바이트 복호화
        byte[] check_sum = new byte[20];
        byte[] check_sum_Contrast = new byte[20];
        byte[] s = new byte[b.length - 20];

        byte seed1[] = new byte[4];
        byte seed2[] = new byte[4];
        byte seed3[] = new byte[4];
        byte seed4[] = new byte[4];

        System.arraycopy(b, b.length - 20, check_sum, 0, 20); // 체크섬과 문장분리
        System.arraycopy(b, 0, s, 0, b.length - 20);

        System.arraycopy(check_sum, 0, seed1, 0, 4); // 체크섬기반으로 시드생성
        System.arraycopy(check_sum, 4, seed2, 0, 4);
        System.arraycopy(check_sum, 8, seed3, 0, 4);
        System.arraycopy(check_sum, 12, seed4, 0, 4);

        byte key[] = new byte[16];
        byte IV[] = new byte[16];
        byte tempkey[] = new byte[4];

        tempkey = randomNumberGenerator.MT19937(seed1); // key와 초기화벡터 생성
        System.arraycopy(tempkey, 0, key, 0, 4);
        System.arraycopy(tempkey, 4, IV, 0, 4);
        tempkey = randomNumberGenerator.MT19937(seed2);
        System.arraycopy(tempkey, 0, key, 4, 4);
        System.arraycopy(tempkey, 4, IV, 4, 4);
        tempkey = randomNumberGenerator.MT19937(seed3);
        System.arraycopy(tempkey, 0, key, 8, 4);
        System.arraycopy(tempkey, 4, IV, 8, 4);
        tempkey = randomNumberGenerator.MT19937(seed4);
        System.arraycopy(tempkey, 0, key, 12, 4);
        System.arraycopy(tempkey, 4, IV, 12, 4);

        // AES128 복호화 실행 key = key[]
        Key AES_key = new SecretKeySpec(key, "AES");
        Cipher AES;
        byte[] result = null;

        try {
            AES = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AES.init(Cipher.DECRYPT_MODE, AES_key, new IvParameterSpec(IV));
            result = AES.doFinal(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        check_sum_Contrast = checksum(result);

        if (!Arrays.equals(check_sum, check_sum_Contrast)) { // 해시값 다를경우 (메시지가 변조되었을 경우)
            return null;
        }
        return result;
    }

    private byte[] checksum(byte[] message_bin) { // SHA-1체크섬 생성
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(message_bin);
            byte[] byteData = new byte[20];
            byteData = md.digest();
            return byteData;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return message_bin;
    }
}
