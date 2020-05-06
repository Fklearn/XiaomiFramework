package com.miui.earthquakewarning.utils;

import android.util.Base64;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SHA1WithRSAUtil {
    private static final String CHARSETTING = "UTF-8";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String SIGN_TYPE_RSA = "RSA";
    private static final String TAG = "SHA1WithRSAUtil";

    public static PrivateKey getPrivateKeyFromPKCS8(String str, String str2) {
        if (str == null || "".equals(str) || str2 == null || "".equals(str2)) {
            return null;
        }
        return KeyFactory.getInstance(str).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(str2.getBytes(), 0)));
    }

    public static PublicKey getPublicKeyFromX509(String str, String str2) {
        if (str == null || "".equals(str) || str2 == null || "".equals(str2)) {
            return null;
        }
        return KeyFactory.getInstance(str).generatePublic(new X509EncodedKeySpec(Base64.decode(str2.getBytes(), 0)));
    }

    public static String sign(String str, String str2) {
        if (str == null || "".equals(str) || str2 == null || "".equals(str2)) {
            return null;
        }
        PrivateKey privateKeyFromPKCS8 = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, str2);
        Signature instance = Signature.getInstance(SIGN_ALGORITHMS);
        instance.initSign(privateKeyFromPKCS8);
        instance.update(str.getBytes("UTF-8"));
        return new String(Base64.encode(instance.sign(), 0));
    }

    public static boolean virefy(String str, String str2, String str3) {
        if (str == null || "".equals(str) || str2 == null || "".equals(str2) || str3 == null || "".equals(str3)) {
            return false;
        }
        PublicKey publicKeyFromX509 = getPublicKeyFromX509(SIGN_TYPE_RSA, str3);
        Signature instance = Signature.getInstance(SIGN_ALGORITHMS);
        instance.initVerify(publicKeyFromX509);
        instance.update(str.getBytes("UTF-8"));
        return instance.verify(Base64.decode(str2.getBytes(), 0));
    }
}
