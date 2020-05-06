package b.b.c.h;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import b.b.c.a;
import b.b.c.j.i;
import b.b.c.j.x;
import com.google.android.exoplayer2.C;
import com.miui.luckymoney.config.Constants;
import com.miui.permcenter.permissions.C0466c;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.TreeMap;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import org.json.JSONObject;

public class e {
    public static File a(Context context, int i, j jVar) {
        TreeMap<String, String> a2 = a(context, "73da76da-224c-4702-ac60-5cda70139283");
        a2.put("version", String.valueOf(i));
        a2.put("sign", a(a2, "73da76da-224c-4702-ac60-5cda70139283"));
        return f.a(a(a.f1721b, a2), context.getCacheDir().getAbsolutePath(), jVar);
    }

    @Deprecated
    public static String a(Context context, String str, JSONObject jSONObject, String str2, j jVar) {
        return c.a(context, str, jSONObject, str2, jVar);
    }

    @Deprecated
    public static String a(Context context, String str, JSONObject jSONObject, String str2, String str3, j jVar) {
        return h.a(context, str, jSONObject, str2, str3, jVar);
    }

    protected static String a(String str, TreeMap<String, String> treeMap) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String a2 = a(treeMap);
        return str + "?" + a2;
    }

    protected static String a(TreeMap<String, String> treeMap) {
        if (treeMap == null || treeMap.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String next : treeMap.keySet()) {
            if (!z) {
                sb.append("&");
            }
            sb.append(next);
            sb.append("=");
            sb.append(treeMap.get(next));
            z = false;
        }
        return sb.toString();
    }

    public static String a(TreeMap<String, String> treeMap, String str) {
        return b(new String(Base64.encodeToString(a(a(treeMap) + "&" + str), 2)));
    }

    private static TreeMap<String, String> a(Context context, String str) {
        TreeMap<String, String> treeMap = new TreeMap<>(new d());
        treeMap.put("d", a.e);
        treeMap.put(C0466c.f6254a, a.f);
        treeMap.put("r", a.g);
        treeMap.put("v", a.h);
        treeMap.put(Constants.JSON_KEY_T, TextUtils.isEmpty(a.f1595b) ? i.b() : a.f1595b);
        treeMap.put("e", x.a(context));
        treeMap.put("l", Locale.getDefault().toString());
        treeMap.put("a", ExtraTextUtils.toHexReadable(DigestUtils.get(i.b(context) + str, "MD5")));
        return treeMap;
    }

    private static byte[] a(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    @Deprecated
    public static String b(Context context, String str, JSONObject jSONObject, String str2, j jVar) {
        return h.a(context, str, jSONObject, str2, jVar);
    }

    private static String b(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(a(str));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
