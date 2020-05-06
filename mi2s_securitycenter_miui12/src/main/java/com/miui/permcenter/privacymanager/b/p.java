package com.miui.permcenter.privacymanager.b;

import android.os.Build;
import java.util.ArrayList;
import java.util.List;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static List<String> f6390a = new ArrayList();

    static {
        f6390a.add("android.permission.READ_EXTERNAL_STORAGE");
        f6390a.add("android.permission.READ_EXTERNAL_STORAGE");
        f6390a.add("android.permission.WRITE_CALL_LOG");
        f6390a.add("android.permission.READ_CALL_LOG");
        f6390a.add("android.permission.PROCESS_OUTGOING_CALLS");
        f6390a.add("android.permission.CAMERA");
        f6390a.add("android.permission.WRITE_CONTACTS");
        f6390a.add("android.permission.READ_CONTACTS");
        f6390a.add("android.permission.GET_ACCOUNTS");
        f6390a.add("android.permission.READ_CALENDAR");
        f6390a.add("android.permission.WRITE_CALENDAR");
        f6390a.add("android.permission.RECORD_AUDIO");
        f6390a.add("android.permission.ACCESS_COARSE_LOCATION");
        f6390a.add("android.permission.ACCESS_FINE_LOCATION");
        if (Build.VERSION.SDK_INT > 29) {
            f6390a.add("android.permission.ACCESS_MEDIA_LOCATION");
            f6390a.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        }
    }

    public static List<String> a() {
        return f6390a;
    }
}
