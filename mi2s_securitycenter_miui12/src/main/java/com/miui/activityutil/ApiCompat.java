package com.miui.activityutil;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import miui.telephony.TelephonyManager;

public class ApiCompat {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f2250a = false;

    /* renamed from: b  reason: collision with root package name */
    private static final String f2251b = "ApiCompat";

    public static String a() {
        String str;
        if (TelephonyManager.getDefault().isMultiSimEnabled()) {
            str = TelephonyManager.getDefault().getMeidForSlot(0);
            String meidForSlot = TelephonyManager.getDefault().getMeidForSlot(1);
            if (!(str == null || meidForSlot == null || str.equals(meidForSlot))) {
                String a2 = e.a(str);
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(",");
                sb.append(meidForSlot);
                return a2 + "," + e.a(meidForSlot);
            }
        } else {
            str = null;
        }
        if (str == null) {
            str = TelephonyManager.getDefault().getDeviceId();
        }
        if (str == null || str.length() == 0) {
            str = h.a("ro.ril.oem.meid", "");
        }
        if (str == null || str.length() == 0) {
            return null;
        }
        return e.a(str);
    }

    public static String a(File file) {
        try {
            Class[] clsArr = {File.class, Integer.TYPE};
            Object[] objArr = {file, 0};
            Object a2 = q.a("android.content.pm.PackageParser", (Object[]) null);
            Method declaredMethod = a2.getClass().getDeclaredMethod("parsePackage", clsArr);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(a2, objArr);
            if (invoke != null) {
                Field declaredField = invoke.getClass().getDeclaredField("packageName");
                declaredField.setAccessible(true);
                return (String) declaredField.get(invoke);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static boolean a(Context context, String str) {
        try {
            return ((Boolean) q.b((Object) context.getPackageManager(), "getApplicationHiddenSettingAsUser", new Class[]{String.class, UserHandle.class}, str, UserHandle.class.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{0}))).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static String b() {
        if (!TelephonyManager.getDefault().isMultiSimEnabled()) {
            return null;
        }
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        String subscriberIdForSlot = telephonyManager.getSubscriberIdForSlot(0);
        String subscriberIdForSlot2 = telephonyManager.getSubscriberIdForSlot(1);
        if (subscriberIdForSlot2 == null || subscriberIdForSlot == null) {
            return subscriberIdForSlot;
        }
        return subscriberIdForSlot + "," + subscriberIdForSlot2;
    }

    public static String getIMEI() {
        if (TelephonyManager.getDefault().isMultiSimEnabled()) {
            String imeiForSlot = TelephonyManager.getDefault().getImeiForSlot(0);
            String imeiForSlot2 = TelephonyManager.getDefault().getImeiForSlot(1);
            if (!(imeiForSlot == null || imeiForSlot2 == null || imeiForSlot.equals(imeiForSlot2))) {
                return e.a(imeiForSlot) + "," + e.a(imeiForSlot2);
            }
        }
        String deviceId = TelephonyManager.getDefault().getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = h.a("ro.ril.miui.imei", "");
        }
        return e.a(deviceId);
    }
}
