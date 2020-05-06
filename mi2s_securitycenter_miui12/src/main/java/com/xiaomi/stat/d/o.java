package com.xiaomi.stat.d;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8553a = "RsaUtils";

    /* renamed from: b  reason: collision with root package name */
    private static final String f8554b = "RSA/ECB/PKCS1Padding";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8555c = "BC";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8556d = "RSA";

    private static RSAPublicKey a(byte[] bArr) {
        return (RSAPublicKey) KeyFactory.getInstance(f8556d).generatePublic(new X509EncodedKeySpec(bArr));
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) {
        try {
            RSAPublicKey a2 = a(bArr);
            Cipher instance = Cipher.getInstance(f8554b);
            instance.init(1, a2);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            k.d(f8553a, "RsaUtils encrypt exception:", e);
            return null;
        }
    }
}
