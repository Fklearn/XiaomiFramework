package miui.cloud.common;

import android.util.Base64;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import miui.cloud.common.XLogger;

public class EncryptLogSender implements XLogger.LogSender {
    private static final String ASYM_ENCRYPT_ALGORITHM = "RSA";
    private static final String ASYM_ENCRYPT_ALGORITHM_USAGE = "RSA/ECB/PKCS1Padding";
    private static final String DEFAULT_ASYM_ENCRYPT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCV2gFH5NQcd1hRT5vRTGweHQZhYEtQgF821nVymHNaeSVSTEecTPpAP71djJlR7Fv9Hfaiy3ubkbre0VwFm7gbWkl4RXrEnTcOClXWUSb+lHfpPK0TmVoooYzL9ySVls8Y9U9PfF1RhgaEU0zjyewdYfGolgS/s9VK/TuLCP+YQwIDAQAB";
    private static final String DEFAULT_ASYM_ENCRYPT_PUBLIC_KEY_LABEL = "";
    private static final byte[] DEFAULT_SYM_ENCRYPT_ALGORITHM_IV = "\u0000\u0000\u0000\u0000\u0000\u0000xiaomi.com".getBytes();
    private static final String SYM_ENCRYPT_ALGORITHM = "AES";
    private static final int SYM_ENCRYPT_ALGORITHM_IV_LEN = 16;
    private static final String SYM_ENCRYPT_ALGORITHM_USAGE = "AES/CBC/PKCS5Padding";
    private static final int SYM_ENCRYPT_KEY_LEN_IN_BITS = 256;
    private String mEncryptedSecrectKey;
    private String mKeyLabel;
    private boolean mRandomIv;
    private SecureRandom mRandomIvGenerator = new SecureRandom();
    private SecretKey mSecretKey;
    private XLogger.LogSender mWrappedLogSender;

    public EncryptLogSender(XLogger.LogSender logSender, String str, String str2, boolean z) {
        this.mWrappedLogSender = logSender;
        this.mKeyLabel = str2;
        this.mRandomIv = z;
        byte[] decode = Base64.decode(str, 0);
        try {
            try {
                PublicKey generatePublic = KeyFactory.getInstance(ASYM_ENCRYPT_ALGORITHM).generatePublic(new X509EncodedKeySpec(decode));
                if (generatePublic.getAlgorithm().equals(ASYM_ENCRYPT_ALGORITHM)) {
                    try {
                        KeyGenerator instance = KeyGenerator.getInstance(SYM_ENCRYPT_ALGORITHM);
                        instance.init(SYM_ENCRYPT_KEY_LEN_IN_BITS);
                        this.mSecretKey = instance.generateKey();
                        try {
                            Cipher instance2 = Cipher.getInstance(ASYM_ENCRYPT_ALGORITHM_USAGE);
                            instance2.init(1, generatePublic);
                            try {
                                this.mEncryptedSecrectKey = Base64.encodeToString(instance2.doFinal(this.mSecretKey.getEncoded()), 2);
                            } catch (IllegalBlockSizeException e) {
                                throw new RuntimeException("Should never happen. ", e);
                            } catch (BadPaddingException e2) {
                                throw new RuntimeException("Should never happen. ", e2);
                            }
                        } catch (NoSuchAlgorithmException e3) {
                            throw new RuntimeException(e3);
                        } catch (NoSuchPaddingException e4) {
                            throw new RuntimeException(e4);
                        } catch (InvalidKeyException e5) {
                            throw new IllegalArgumentException("The input publicKey is not valid. ", e5);
                        }
                    } catch (NoSuchAlgorithmException e6) {
                        throw new RuntimeException(e6);
                    }
                } else {
                    throw new IllegalArgumentException(String.format("The input publicKey is not a %s public key. ", new Object[]{ASYM_ENCRYPT_ALGORITHM}));
                }
            } catch (InvalidKeySpecException e7) {
                throw new IllegalArgumentException("The input publicKey is not valid. ", e7);
            }
        } catch (NoSuchAlgorithmException e8) {
            throw new RuntimeException(e8);
        }
    }

    private String encryptString(String str, byte[] bArr) {
        try {
            Cipher instance = Cipher.getInstance(SYM_ENCRYPT_ALGORITHM_USAGE);
            try {
                instance.init(1, this.mSecretKey, new IvParameterSpec(bArr));
                try {
                    return Base64.encodeToString(instance.doFinal(str.getBytes()), 2);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException("Should never happen. ", e);
                } catch (BadPaddingException e2) {
                    throw new RuntimeException("Should never happen. ", e2);
                }
            } catch (InvalidKeyException e3) {
                throw new RuntimeException("Should never happen. ", e3);
            } catch (InvalidAlgorithmParameterException e4) {
                throw new RuntimeException(e4);
            }
        } catch (NoSuchAlgorithmException e5) {
            throw new RuntimeException(e5);
        } catch (NoSuchPaddingException e6) {
            throw new RuntimeException(e6);
        }
    }

    private byte[] generateRandomIv() {
        byte[] bArr = new byte[16];
        this.mRandomIvGenerator.nextBytes(bArr);
        return bArr;
    }

    public static EncryptLogSender getWithDefaultPublicKey(XLogger.LogSender logSender) {
        return new EncryptLogSender(logSender, DEFAULT_ASYM_ENCRYPT_PUBLIC_KEY, "", false);
    }

    public void sendLog(int i, String str, String str2) {
        try {
            if (this.mRandomIv) {
                byte[] generateRandomIv = generateRandomIv();
                str2 = String.format("#&^%s@%s!%s!%s^&#", new Object[]{this.mEncryptedSecrectKey, Base64.encodeToString(generateRandomIv, 2), this.mKeyLabel, encryptString(str2, generateRandomIv)});
            } else {
                str2 = String.format("#&^%s!%s!%s^&#", new Object[]{this.mEncryptedSecrectKey, this.mKeyLabel, encryptString(str2, DEFAULT_SYM_ENCRYPT_ALGORITHM_IV)});
            }
        } catch (Exception e) {
            this.mWrappedLogSender.sendLog(6, EncryptLogSender.class.getName(), String.format("Failed to encrypt the message: %s. ", new Object[]{e}));
        }
        this.mWrappedLogSender.sendLog(i, str, str2);
    }
}
