package com.miui.gamebooster.globalgame.http;

import android.util.Base64;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.globalgame.util.b;
import com.miui.googlebase.b.c;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.h;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.os.Build;
import miui.util.IOUtils;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f4395a = com.miui.securityscan.c.a.f7625a;

    public static String a(long j, String str) {
        if (str.isEmpty()) {
            return "";
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec("e582db5fbfa01a75".getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec((j + "000").getBytes());
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        instance.init(2, secretKeySpec, ivParameterSpec);
        return new String(instance.doFinal(Base64.decode(str, 0)));
    }

    public static String a(String str) {
        return a((Map<String, String>) null, str);
    }

    public static String a(Map<String, String> map) {
        Map<String, String> e = e(map);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry next : e.entrySet()) {
            if (sb.length() != 0) {
                sb.append("&");
            }
            sb.append((String) next.getKey());
            sb.append("=");
            sb.append((String) next.getValue());
        }
        sb.append("&key=");
        sb.append("11b036c32104bacc485a86538f321d02");
        String sb2 = sb.toString();
        b.c("key :$key");
        String c2 = c(sb2);
        b.c("md5 :$md5");
        return c2;
    }

    public static String a(Map<String, String> map, String str) {
        return a(map, str, false);
    }

    private static String a(Map<String, String> map, String str, boolean z) {
        if (!h.i() && !z) {
            return "";
        }
        try {
            Map<String, String> b2 = b(map);
            if (b2 != null) {
                String d2 = d(b2);
                if (!str.contains("?")) {
                    str = str.concat("?");
                }
                str = str.concat(d2);
            }
            b.c("request start : " + str);
            return c.a(str, new j("gamebooster_globalserver"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            IOUtils.closeQuietly((InputStream) null);
            IOUtils.closeQuietly((OutputStream) null);
        }
    }

    private static Map<String, String> b(Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        c(map);
        map.put("sign", a(map));
        return map;
    }

    private static byte[] b(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    private static String c(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(b(str));
            return String.format("%1$032x", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void c(Map<String, String> map) {
        map.put("miui_version", Utils.g());
        map.put("android_version", Utils.a());
        map.put("r", Utils.f());
        map.put("d", Utils.h().replace(' ', '_'));
        map.put("dc", Build.DEVICE);
        map.put("dm", Build.PRODUCT);
        map.put("l", Utils.e());
        map.put(Constants.JSON_KEY_T, Utils.c());
        map.put("n", Utils.Network.a());
        map.put("uuid", Utils.j());
        map.put("pkg", Utils.b());
        map.put("version_code", Utils.k());
        map.put("version_name", Utils.l());
        map.put("server_code", "100");
    }

    private static String d(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Map.Entry next : map.entrySet()) {
            if (!z) {
                sb.append('&');
            }
            sb.append((String) next.getKey());
            sb.append('=');
            sb.append((String) next.getValue());
            z = false;
        }
        return sb.toString();
    }

    private static Map<String, String> e(Map<String, String> map) {
        if (map != null) {
            return new TreeMap(map);
        }
        throw new IllegalArgumentException("map must not be null.");
    }
}
