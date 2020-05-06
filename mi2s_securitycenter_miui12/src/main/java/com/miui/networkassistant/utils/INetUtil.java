package com.miui.networkassistant.utils;

import java.nio.ByteOrder;

public class INetUtil {
    public static int htonl(int i) {
        return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? i : Integer.reverseBytes(i);
    }

    public static byte[] htonlBytes(int i) {
        return new byte[]{(byte) ((i >>> 24) & 255), (byte) ((i >>> 16) & 255), (byte) ((i >>> 8) & 255), (byte) (i & 255)};
    }

    public static int ntohl(int i) {
        return ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? i : Integer.reverseBytes(i);
    }

    public static int ntohlBytes(byte[] bArr) {
        byte b2 = 0;
        for (int i = 0; i < 4; i++) {
            b2 = (b2 << 8) | (bArr[i] & 255);
        }
        return b2;
    }
}
