package com.miui.permcenter.install;

import android.util.Base64;
import android.util.Log;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class o {
    private PublicKey a() {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/gT0vSqtsv7y4c7qLn1tvJF6Uur067h8pf7VXJNNqYxVWdpR5la5QuLIi90bZntPGsxM7IDSTL7Wia9ox+CbalDC3+ouPMVIjMK9tqWfb8F0XQJi8eHlZ4KpGZxqQDOcisxGQHClig/dD/CkqlX4eTyK7HDRVRS8Jj6SiaWDY2QIDAQAB", 2)));
        } catch (Exception e) {
            Log.d("RSA_Encryption", "get public key error!");
            e.printStackTrace();
            return null;
        }
    }

    public byte[] a(String str) {
        try {
            PublicKey a2 = a();
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, a2);
            byte[] bytes = str.getBytes();
            int length = ((bytes.length - 1) / 117) + 1;
            byte[] bArr = new byte[(length * 128)];
            byte[] bArr2 = new byte[117];
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    int i2 = i * 117;
                    int length2 = bytes.length - i2;
                    if (length2 <= 0) {
                        break;
                    }
                    byte[] bArr3 = new byte[length2];
                    System.arraycopy(bytes, i2, bArr3, 0, length2);
                    bArr2 = bArr3;
                } else {
                    System.arraycopy(bytes, i * 117, bArr2, 0, 117);
                }
                System.arraycopy(instance.doFinal(bArr2), 0, bArr, i * 128, 128);
            }
            return bArr;
        } catch (Exception e) {
            Log.d("RSA_Encryption", "encrypt error!");
            e.printStackTrace();
            return null;
        }
    }
}
