package com.miui.earthquakewarning.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    private static char[] sTemp = new char[2];
    private static final char[] strDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: byte} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v0, types: [int, byte] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String byteToArrayString(int r4) {
        /*
            if (r4 >= 0) goto L_0x0004
            int r4 = r4 + 256
        L_0x0004:
            char[] r0 = sTemp
            r1 = 0
            char[] r2 = strDigits
            int r3 = r4 / 16
            char r3 = r2[r3]
            r0[r1] = r3
            r1 = 1
            int r4 = r4 % 16
            char r4 = r2[r4]
            r0[r1] = r4
            java.lang.String r4 = new java.lang.String
            r4.<init>(r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.utils.MD5Util.byteToArrayString(byte):java.lang.String");
    }

    private static String byteToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte byteToArrayString : bArr) {
            stringBuffer.append(byteToArrayString(byteToArrayString));
        }
        return stringBuffer.toString();
    }

    public static String encode(String str) {
        String str2;
        try {
            str2 = new String(str);
            try {
                return byteToString(MessageDigest.getInstance("MD5").digest(str.getBytes()));
            } catch (NoSuchAlgorithmException e) {
                e = e;
                e.printStackTrace();
                return str2;
            }
        } catch (NoSuchAlgorithmException e2) {
            e = e2;
            str2 = null;
            e.printStackTrace();
            return str2;
        }
    }
}
