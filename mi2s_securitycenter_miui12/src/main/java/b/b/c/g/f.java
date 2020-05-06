package b.b.c.g;

import android.util.Base64;
import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class f {
    public static String a(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(b(str));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String a(List<c> list, String str) {
        Collections.sort(list, new e());
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (c next : list) {
            if (!z) {
                sb.append("&");
            }
            sb.append(next.a());
            sb.append("=");
            sb.append(next.b());
            z = false;
        }
        sb.append("&");
        sb.append(str);
        return a(new String(Base64.encode(b(sb.toString()), 2)));
    }

    private static byte[] b(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }
}
