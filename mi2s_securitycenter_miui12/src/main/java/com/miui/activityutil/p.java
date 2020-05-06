package com.miui.activityutil;

import android.util.Base64;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public final class p {

    /* renamed from: a  reason: collision with root package name */
    private static final int f2313a = 117;

    /* renamed from: b  reason: collision with root package name */
    private static final int f2314b = 128;

    /* renamed from: c  reason: collision with root package name */
    private static final String f2315c = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/gT0vSqtsv7y4c7qLn1tvJF6Uur067h8pf7VXJNNqYxVWdpR5la5QuLIi90bZntPGsxM7IDSTL7Wia9ox+CbalDC3+ouPMVIjMK9tqWfb8F0XQJi8eHlZ4KpGZxqQDOcisxGQHClig/dD/CkqlX4eTyK7HDRVRS8Jj6SiaWDY2QIDAQAB";

    private static OutputStream a(OutputStream outputStream) {
        PublicKey a2 = a();
        Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        instance.init(1, a2);
        return new d(outputStream, instance);
    }

    private static PublicKey a() {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(f2315c, 2)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] a(String str) {
        return a(str.getBytes());
    }

    public static byte[] a(byte[] bArr) {
        try {
            PublicKey a2 = a();
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, a2);
            int length = ((bArr.length - 1) / 117) + 1;
            byte[] bArr2 = new byte[(length * f2314b)];
            byte[] bArr3 = new byte[117];
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    int i2 = i * 117;
                    int length2 = bArr.length - i2;
                    byte[] bArr4 = new byte[length2];
                    System.arraycopy(bArr, i2, bArr4, 0, length2);
                    bArr3 = bArr4;
                } else {
                    System.arraycopy(bArr, i * 117, bArr3, 0, 117);
                }
                System.arraycopy(instance.doFinal(bArr3), 0, bArr2, i * f2314b, f2314b);
            }
            return bArr2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String b(byte[] bArr) {
        try {
            PublicKey a2 = a();
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(2, a2);
            int length = ((bArr.length - 1) / f2314b) + 1;
            byte[] bArr2 = new byte[(length * 117)];
            byte[] bArr3 = new byte[f2314b];
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < length; i3++) {
                if (i3 == length - 1) {
                    int length2 = bArr.length;
                    int i4 = i3 * f2314b;
                    int i5 = length2 - i4;
                    byte[] bArr4 = new byte[i5];
                    System.arraycopy(bArr, i4, bArr4, 0, i5);
                    bArr3 = bArr4;
                } else {
                    System.arraycopy(bArr, i3 * f2314b, bArr3, 0, f2314b);
                }
                byte[] doFinal = instance.doFinal(bArr3);
                i += doFinal.length;
                System.arraycopy(doFinal, 0, bArr2, i2, doFinal.length);
                i2 += doFinal.length;
            }
            return new String(bArr2, 0, i);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
