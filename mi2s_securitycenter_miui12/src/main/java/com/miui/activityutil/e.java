package com.miui.activityutil;

import android.text.TextUtils;
import b.c.a.b.f;
import com.miui.permcenter.permissions.C0466c;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class e {

    /* renamed from: a  reason: collision with root package name */
    private static final String[] f2283a = {o.f2309a, o.f2310b, "2", o.f2312d, o.e, o.f, o.g, o.h, o.i, o.j, "a", "b", C0466c.f6254a, "d", "e", f.f2028a};

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: byte} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r3v0, types: [int, byte] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String a(int r3) {
        /*
            if (r3 >= 0) goto L_0x0004
            int r3 = r3 + 256
        L_0x0004:
            int r0 = r3 / 16
            int r3 = r3 % 16
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String[] r2 = f2283a
            r0 = r2[r0]
            r1.append(r0)
            java.lang.String[] r0 = f2283a
            r3 = r0[r3]
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.e.a(byte):java.lang.String");
    }

    public static final String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            return a(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte a2 : bArr) {
            stringBuffer.append(a(a2));
        }
        return stringBuffer.toString();
    }

    public static final String b(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(str.getBytes());
            return a(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
