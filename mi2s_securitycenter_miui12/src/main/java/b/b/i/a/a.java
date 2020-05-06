package b.b.i.a;

import android.util.Base64;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static SecretKeySpec f1780a;

    /* renamed from: b  reason: collision with root package name */
    private static byte[] f1781b;

    public static String a(String str, String str2) {
        try {
            a(str2);
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            instance.init(2, f1780a);
            return new String(instance.doFinal(Base64.decode(str.getBytes(C.UTF8_NAME), 2)));
        } catch (Exception e) {
            Log.e("AES", "Error while decrypting: ", e);
            return null;
        }
    }

    public static void a(String str) {
        try {
            f1781b = str.getBytes(C.UTF8_NAME);
            f1781b = MessageDigest.getInstance("SHA-1").digest(f1781b);
            f1781b = Arrays.copyOf(f1781b, 16);
            f1780a = new SecretKeySpec(f1781b, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
    }
}
