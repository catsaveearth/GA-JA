package com.se.term;

import java.nio.ByteBuffer;

public class ByteModule {
    public static int btoi(byte[] arr) {
        return (arr[0] & 0xff) << 24 | (arr[1] & 0xff) << 16 | (arr[2] & 0xff) << 8 | (arr[3] & 0xff);
    }

    public static byte[] itob(int port) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((port & 0xFF000000) >> 24);
        bytes[1] = (byte) ((port & 0x00FF0000) >> 16);
        bytes[2] = (byte) ((port & 0x0000FF00) >> 8);
        bytes[3] = (byte) (port & 0x000000FF);
        return bytes;
    }

    public static byte[] ltob(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long btol(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }
}
