package b.b.c.j;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* renamed from: b.b.c.j.a  reason: case insensitive filesystem */
public class C0194a {
    public static String a(String str, String str2) {
        byte[] bArr;
        try {
            Key a2 = a(str);
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            instance.init(1, a2);
            bArr = instance.doFinal(str2.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        return new String(Base64.encodeToString(bArr, 0));
    }

    private static Key a(String str) {
        try {
            return new SecretKeySpec(str.getBytes(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
