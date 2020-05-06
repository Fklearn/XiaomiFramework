package miui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";

    private HashUtils() {
    }

    public static String getMD5(String content) {
        return getHash(content, MD5);
    }

    public static String getMD5(InputStream is) {
        return getHash(is, MD5);
    }

    public static String getMD5(File file) {
        return getHash(file, MD5);
    }

    public static String getSHA1(String content) {
        return getHash(content, SHA1);
    }

    public static String getSHA1(InputStream is) {
        return getHash(is, SHA1);
    }

    public static String getSHA1(File file) {
        return getHash(file, SHA1);
    }

    public static String getHash(String content, String method) {
        try {
            MessageDigest digester = MessageDigest.getInstance(method);
            digester.update(content.getBytes());
            return toHexString(digester.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHash(InputStream is, String method) {
        String hash = null;
        try {
            MessageDigest digester = MessageDigest.getInstance(method);
            byte[] bytes = new byte[8192];
            while (true) {
                int read = is.read(bytes);
                int byteCount = read;
                if (read <= 0) {
                    break;
                }
                digester.update(bytes, 0, byteCount);
            }
            hash = toHexString(digester.digest());
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            if (is != null) {
                is.close();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
            if (is != null) {
                is.close();
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
        return hash;
    }

    public static String getHash(File file, String method) {
        try {
            return getHash((InputStream) new BufferedInputStream(new FileInputStream(file)), method);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            hexString.append(String.format("%02x", new Object[]{Integer.valueOf(bytes[i] & 255)}));
        }
        return hexString.toString();
    }
}
