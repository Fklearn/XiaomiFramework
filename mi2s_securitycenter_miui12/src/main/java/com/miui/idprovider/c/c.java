package com.miui.idprovider.c;

import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.miui.activityutil.o;
import java.security.MessageDigest;
import miui.util.Log;

public class c {
    public static String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(str.getBytes(C.UTF8_NAME));
            return a(instance.digest());
        } catch (Exception e) {
            Log.e("SHA256Utils", "encrypt sha256 exception", e);
            return "";
        }
    }

    private static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            if (hexString.length() == 1) {
                stringBuffer.append(o.f2309a);
            }
            stringBuffer.append(hexString);
        }
        return stringBuffer.toString();
    }
}
