package com.miui.idprovider.c;

import android.util.Log;
import b.b.o.g.c;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class b {
    public static String a() {
        try {
            return a("/sys/class/net/wlan0/address").toUpperCase().substring(0, 17);
        } catch (IOException e) {
            Log.e("DeviceUtils", "get mac address error!", e);
            return "";
        }
    }

    public static String a(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getDeviceIdForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        String f = a2.f();
        return f == null ? "" : f;
    }

    private static String a(String str) {
        StringBuffer stringBuffer = new StringBuffer(1000);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
        char[] cArr = new char[1024];
        while (true) {
            int read = bufferedReader.read(cArr);
            if (read != -1) {
                stringBuffer.append(String.valueOf(cArr, 0, read));
            } else {
                bufferedReader.close();
                return stringBuffer.toString();
            }
        }
    }
}
