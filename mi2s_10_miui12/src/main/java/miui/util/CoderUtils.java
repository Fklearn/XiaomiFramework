package miui.util;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.provider.ExtraTelephony;
import miui.provider.MiCloudSmsCmd;

public class CoderUtils {
    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String[] hexDigits = {"0", "1", "2", ExtraTelephony.Phonelist.TYPE_VIP, ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_CLOUDS_WHITE, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_WHITE, "8", "9", "a", "b", "c", MiCloudSmsCmd.TYPE_DISCARD_TOKEN, "e", "f"};

    public static final String encodeMD5(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance(HashUtils.MD5);
            digester.update(string.getBytes());
            return byteArrayToString(digester.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String encodeMD5(File file) {
        byte[] buffer = new byte[1024];
        try {
            InputStream fis = new FileInputStream(file);
            try {
                MessageDigest md5 = MessageDigest.getInstance(HashUtils.MD5);
                while (true) {
                    int read = fis.read(buffer);
                    int numRead = read;
                    if (read > 0) {
                        md5.update(buffer, 0, numRead);
                    } else {
                        try {
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                fis.close();
                return byteArrayToString(md5.digest());
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
                try {
                    fis.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                return null;
            } catch (IOException e4) {
                e4.printStackTrace();
                try {
                    fis.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                return null;
            } catch (Throwable th) {
                try {
                    fis.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
                throw th;
            }
        } catch (FileNotFoundException e7) {
            e7.printStackTrace();
            return null;
        }
    }

    private static String byteArrayToString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (byte byteToHexString : b) {
            resultSb.append(byteToHexString(byteToHexString));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        return hexDigits[n / 16] + hexDigits[n % 16];
    }

    public static final String encodeSHA(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA");
            digester.update(string.getBytes());
            return byteArrayToString(digester.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] encodeSHABytes(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA");
            digester.update(string.getBytes());
            return digester.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] encodeBase64(String string) {
        return Base64.encode(string.getBytes(), 2);
    }

    public static final byte[] encodeBase64(byte[] bytes) {
        return Base64.encode(bytes, 2);
    }

    public static final byte[] encodeBase64Bytes(String string) {
        return Base64.encode(string.getBytes(), 2);
    }

    public static final String decodeBase64(String string) {
        return new String(Base64.decode(string.getBytes(), 0));
    }

    public static final byte[] decodeBase64Bytes(String string) {
        return Base64.decode(string.getBytes(), 0);
    }

    public static final String base64AesEncode(String data, String key) {
        byte[] raw;
        if (data == null || data.length() == 0 || (raw = key.getBytes()) == null || raw.length != 16) {
            return null;
        }
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(1, keySpec, new IvParameterSpec("0102030405060708".getBytes()));
            return new String(encodeBase64(cipher.doFinal(data.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e2) {
            return null;
        } catch (InvalidKeyException e3) {
            return null;
        } catch (InvalidAlgorithmParameterException e4) {
            return null;
        } catch (IllegalBlockSizeException e5) {
            return null;
        } catch (BadPaddingException e6) {
            return null;
        }
    }

    public static final String base6AesDecode(String data, String key) {
        byte[] raw;
        if (data == null || data.length() == 0 || (raw = key.getBytes()) == null || raw.length != 16) {
            return null;
        }
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(2, keySpec, new IvParameterSpec("0102030405060708".getBytes()));
            byte[] encryptedByte = decodeBase64Bytes(data);
            if (encryptedByte == null) {
                return null;
            }
            return new String(cipher.doFinal(encryptedByte));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e2) {
            return null;
        } catch (InvalidKeyException e3) {
            return null;
        } catch (InvalidAlgorithmParameterException e4) {
            return null;
        } catch (IllegalBlockSizeException e5) {
            return null;
        } catch (BadPaddingException e6) {
            return null;
        }
    }
}
