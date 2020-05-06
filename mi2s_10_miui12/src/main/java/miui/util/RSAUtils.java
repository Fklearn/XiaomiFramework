package miui.util;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAUtils {
    public static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
    private static final int DEFAULT_RADIX = 16;
    public static final int FLAG_CRLF = 4;
    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_NO_CLOSE = 16;
    public static final int FLAG_NO_PADDING = 1;
    public static final int FLAG_NO_WRAP = 2;
    public static final int FLAG_URL_SAFE = 8;
    public static final String KEY_RSA = "RSA";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;
    public static final String SIGNATURE_MD5_WITH_RSA = "MD5withRSA";
    public static final String SIGNATURE_SHA1_WITH_RSA = "SHA1withRSA";

    private RSAUtils() {
    }

    public static PublicKey getPublicKey(KeySpec keySpec) throws Exception {
        return KeyFactory.getInstance(KEY_RSA).generatePublic(keySpec);
    }

    public static PublicKey getPublicKey(String modulus, String exponent, int radix) throws Exception {
        return getPublicKey((KeySpec) new RSAPublicKeySpec(new BigInteger(modulus, radix), new BigInteger(exponent, radix)));
    }

    public static PublicKey getPublicKey(String modulus, String exponent) throws Exception {
        return getPublicKey(modulus, exponent, 16);
    }

    public static PublicKey getPublicKey(String key, int flags) throws Exception {
        return getPublicKey((KeySpec) new X509EncodedKeySpec(Base64.decode(key, flags)));
    }

    public static PublicKey getPublicKey(String key) throws Exception {
        return getPublicKey(key, 0);
    }

    public static PrivateKey getPrivateKey(KeySpec keySpec) throws Exception {
        return KeyFactory.getInstance(KEY_RSA).generatePrivate(keySpec);
    }

    public static PrivateKey getPrivateKey(String modulus, String exponent, int radix) throws Exception {
        return getPrivateKey((KeySpec) new RSAPrivateKeySpec(new BigInteger(modulus, radix), new BigInteger(exponent, radix)));
    }

    public static PrivateKey getPrivateKey(String modulus, String exponent) throws Exception {
        return getPrivateKey(modulus, exponent, 16);
    }

    public static PrivateKey getPrivateKey(String key, int flags) throws Exception {
        return getPrivateKey((KeySpec) new PKCS8EncodedKeySpec(Base64.decode(key, flags)));
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        return getPrivateKey(key, 0);
    }

    private static byte[] doEncryptOrDecrypt(byte[] data, Key key, String algorithm, int mode) throws Exception {
        byte[] cache;
        int maxBlock = getMaxBlock(mode);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(mode, key);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int offset = 0; data.length - offset > 0; offset += maxBlock) {
            if (data.length - offset > maxBlock) {
                cache = cipher.doFinal(data, offset, maxBlock);
            } else {
                cache = cipher.doFinal(data, offset, data.length - offset);
            }
            out.write(cache);
        }
        byte[] deformedData = out.toByteArray();
        out.close();
        return deformedData;
    }

    private static int getMaxBlock(int mode) throws Exception {
        if (mode == 1) {
            return MAX_ENCRYPT_BLOCK;
        }
        if (mode == 2) {
            return 128;
        }
        throw new IllegalArgumentException("wrong operation mode");
    }

    public static byte[] encrypt(byte[] data, Key key, String algorithm) throws Exception {
        return doEncryptOrDecrypt(data, key, algorithm, 1);
    }

    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, CIPHER_RSA);
    }

    public static String encrypt(String content, Key key, String algorithm) throws Exception {
        return new String(encrypt(content.getBytes(), key, algorithm));
    }

    public static String encrypt(String content, Key key) throws Exception {
        return encrypt(content, key, CIPHER_RSA);
    }

    public static byte[] decrypt(byte[] data, Key key, String algorithm) throws Exception {
        return doEncryptOrDecrypt(data, key, algorithm, 2);
    }

    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, CIPHER_RSA);
    }

    public static String decrypt(String content, Key key, String algorithm) throws Exception {
        return new String(decrypt(content.getBytes(), key, algorithm));
    }

    public static String decrypt(String content, Key key) throws Exception {
        return decrypt(content, key, CIPHER_RSA);
    }

    public static byte[] sign(byte[] data, PrivateKey key, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }

    public static byte[] sign(byte[] data, PrivateKey key) throws Exception {
        return sign(data, key, SIGNATURE_SHA1_WITH_RSA);
    }

    public static String sign(String content, PrivateKey key, String algorithm) throws Exception {
        return new String(sign(content.getBytes(), key, algorithm));
    }

    public static String sign(String content, PrivateKey key) throws Exception {
        return sign(content, key, SIGNATURE_SHA1_WITH_RSA);
    }

    public static boolean verify(byte[] data, PublicKey key, byte[] sign, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(key);
        signature.update(data);
        return signature.verify(sign);
    }

    public static boolean verify(byte[] data, PublicKey key, byte[] sign) throws Exception {
        return verify(data, key, sign, SIGNATURE_SHA1_WITH_RSA);
    }

    public static boolean verify(String content, PublicKey key, String sign, String algorithm) throws Exception {
        return verify(content.getBytes(), key, sign.getBytes(), algorithm);
    }

    public static boolean verify(String content, PublicKey key, String sign) throws Exception {
        return verify(content, key, sign, SIGNATURE_SHA1_WITH_RSA);
    }
}
