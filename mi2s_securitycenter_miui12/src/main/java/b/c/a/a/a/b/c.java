package b.c.a.a.a.b;

import b.c.a.c.d;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class c implements a {
    private byte[] a(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            return instance.digest();
        } catch (NoSuchAlgorithmException e) {
            d.a((Throwable) e);
            return null;
        }
    }

    public String a(String str) {
        return new BigInteger(a(str.getBytes())).abs().toString(36);
    }
}
