package b.b.c.j;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static final char[] f1755a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /* renamed from: b  reason: collision with root package name */
    private static final char[] f1756b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static MessageDigest a() {
        return a("MD5");
    }

    public static MessageDigest a(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static MessageDigest a(MessageDigest messageDigest, InputStream inputStream) {
        byte[] bArr = new byte[2048];
        while (true) {
            int read = inputStream.read(bArr, 0, 2048);
            if (read <= -1) {
                return messageDigest;
            }
            messageDigest.update(bArr, 0, read);
        }
    }

    public static byte[] a(InputStream inputStream) {
        return b(a(), inputStream);
    }

    public static char[] a(byte[] bArr) {
        return a(bArr, true);
    }

    public static char[] a(byte[] bArr, boolean z) {
        return a(bArr, z ? f1755a : f1756b);
    }

    protected static char[] a(byte[] bArr, char[] cArr) {
        int length = bArr.length;
        char[] cArr2 = new char[(length << 1)];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            cArr2[i] = cArr[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr2[i3] = cArr[bArr[i2] & 15];
        }
        return cArr2;
    }

    public static String b(InputStream inputStream) {
        return b(a(inputStream));
    }

    public static String b(byte[] bArr) {
        return new String(a(bArr));
    }

    private static byte[] b(MessageDigest messageDigest, InputStream inputStream) {
        a(messageDigest, inputStream);
        return messageDigest.digest();
    }

    public static byte[] c(byte[] bArr) {
        return a().digest(bArr);
    }

    public static String d(byte[] bArr) {
        return b(c(bArr));
    }
}
