package com.miui.gamebooster.m;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.y;
import b.b.o.g.c;
import b.b.o.g.e;
import com.milink.api.v1.MilinkClientManager;
import com.miui.earthquakewarning.Constants;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import com.miui.securitycenter.p;
import com.miui.securityscan.i.c;
import java.util.ArrayList;
import miui.os.Build;

/* renamed from: com.miui.gamebooster.m.t  reason: case insensitive filesystem */
public class C0388t {

    /* renamed from: a  reason: collision with root package name */
    public static final ArrayList<String> f4516a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    public static final ArrayList<String> f4517b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    public static final Long f4518c = 1559059200000L;

    /* renamed from: d  reason: collision with root package name */
    public static final Long f4519d = 1563764980000L;

    static {
        f4516a.add("cepheus");
        f4516a.add("grus");
        f4516a.add("pyxis");
        f4516a.add("raphael");
        f4516a.add("raphaelin");
        f4517b.add("davinci");
        f4517b.add("davinciin");
    }

    public static boolean A() {
        try {
            return ((Boolean) e.a(Class.forName("android.util.MiuiMultiWindowUtils"), Boolean.TYPE, "supportPinFreeFormApp", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("GameBoosterFeatureUtils", "supportPinFreeFormApp!", e);
            return true;
        }
    }

    private static boolean B() {
        String string = SystemPropertiesCompat.getString("ro.board.platform", "default");
        Log.i("GameBoosterFeatureUtils", "notSupportHangupQ8451: platform=" + string + "\tbuildTime=" + Build.TIME + "\tApiLevel=" + Build.VERSION.SDK_INT);
        return "sdm845".equals(string) && miui.os.Build.TIME < 1582711483000L && Build.VERSION.SDK_INT == 29;
    }

    public static String a(String str, String str2) {
        try {
            return (String) e.a(Class.forName(str), String.class, "getString", (Class<?>[]) new Class[]{String.class}, str2);
        } catch (Exception e) {
            Log.e("GameBoosterFeatureUtils", e.toString());
            return null;
        }
    }

    public static boolean a() {
        boolean z = false;
        try {
            z = a("miui.util.FeatureParser", "is_blackshark", false);
            Log.i("GameBoosterFeatureUtils", "isBS: " + z);
            return z;
        } catch (Exception e) {
            Log.e("GameBoosterFeatureUtils", "getBlackSharkFeature Failed", e);
            return z;
        }
    }

    public static boolean a(Context context) {
        return p.a("com.miui.voiceassist") >= 304008000 && !miui.os.Build.IS_INTERNATIONAL_BUILD && b(context);
    }

    public static boolean a(String str, String str2, boolean z) {
        Class[] clsArr = {String.class, Boolean.TYPE};
        try {
            return ((Boolean) e.a(Class.forName(str), Boolean.TYPE, "getBoolean", (Class<?>[]) clsArr, str2, Boolean.valueOf(z))).booleanValue();
        } catch (Exception e) {
            Log.e("GameBoosterFeatureUtils", e.toString());
            return false;
        }
    }

    public static boolean b() {
        return Build.VERSION.SDK_INT >= 26 && "cepheus".equals(miui.os.Build.DEVICE);
    }

    private static boolean b(Context context) {
        int i = -1;
        Cursor cursor = null;
        try {
            Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.voiceassist.xiaoai.manager.provider/mode/voicetrigger"), (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null && query.moveToFirst()) {
                i = Integer.parseInt(query.getString(query.getColumnIndex("voicetrigger_mode")));
            }
            if (query != null) {
                try {
                    query.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            Log.e("GameBoosterFeatureUtils", "getVoiceTriggerAvailable err: " + e2);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        Log.e("GameBoosterFeatureUtils", "voice trigger mode: " + i);
        return i >= 0;
    }

    public static boolean c() {
        return b() || a("android.os.SystemProperties", "ro.vendor.audio.game.effect", false);
    }

    public static boolean d() {
        return h() || q();
    }

    public static boolean e() {
        return b() || (Build.VERSION.SDK_INT > 23 && p.a() >= 12);
    }

    public static boolean f() {
        try {
            MilinkClientManager.class.getDeclaredMethod("disconnectWifiDisplay", (Class[]) null);
            return B.f();
        } catch (NoSuchMethodException unused) {
            return false;
        }
    }

    public static boolean g() {
        if (v()) {
            return false;
        }
        return b() || (C0385p.a() != -1 && "qcom".equals(a("miui.util.FeatureParser", "vendor")) && p.a() >= 12);
    }

    public static boolean h() {
        return b() || a("miui.util.FeatureParser", "support_displayfeature_gamemode", false);
    }

    public static boolean i() {
        if (!"stable".equals(c.a())) {
            return p.a() >= 12 && Build.VERSION.SDK_INT >= 28;
        }
        if (B()) {
            return false;
        }
        return (p.a() >= 13 && Build.VERSION.SDK_INT >= 28) || b() || y();
    }

    public static boolean j() {
        return (b() && Build.VERSION.SDK_INT <= 28) || a("miui.util.FeatureParser", "support_wifi_low_latency_mode", false);
    }

    public static boolean k() {
        if (!a("miui.util.FeatureParser", "support_network_rps_mode", false)) {
            if (Build.VERSION.SDK_INT >= 28) {
                return "cepheus".equals(miui.os.Build.DEVICE) || ("grus".equals(miui.os.Build.DEVICE) && p.a() >= 12);
            }
            return false;
        }
    }

    public static boolean l() {
        boolean z = miui.os.Build.IS_INTERNATIONAL_BUILD && !b() && p.a() < 12;
        boolean z2 = p() && A();
        if (!z && !z2 && !na.c()) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 28) {
                return true;
            }
            if (i >= 26) {
                return p.a() >= 12;
            }
        }
        return false;
    }

    public static boolean m() {
        return (!miui.os.Build.IS_INTERNATIONAL_BUILD || l()) && Build.VERSION.SDK_INT > 23;
    }

    public static boolean n() {
        return c() || r();
    }

    public static boolean o() {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return false;
        }
        try {
            return ((Boolean) e.a(Class.forName("miui.os.DeviceFeature"), Boolean.TYPE, "hasMirihiSupport", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("GameBoosterFeatureUtils", "isSupportSlip!", e);
            return false;
        }
    }

    public static boolean p() {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return false;
        }
        return o() || (Build.VERSION.SDK_INT >= 26 && "lotus".equals(miui.os.Build.DEVICE));
    }

    public static boolean q() {
        return b() || a("miui.util.FeatureParser", "support_touchfeature_gamemode", false);
    }

    public static boolean r() {
        return b() || a("miui.util.FeatureParser", "support_touchfeature_gamemode", false);
    }

    public static boolean s() {
        return (p.a(Constants.SECURITY_ADD_PACKAGE) >= 90321 && Build.VERSION.SDK_INT >= 26) || z();
    }

    public static boolean t() {
        return a("miui.util.FeatureParser", "support_game_mi_time", false);
    }

    public static boolean u() {
        return y.a("ro.vendor.audio.voice.change.support", false);
    }

    public static boolean v() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("pine");
        arrayList.add("laurus");
        return arrayList.contains(miui.os.Build.DEVICE);
    }

    public static boolean w() {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("isGwsdSupport", (Class<?>[]) null, (Object[]) null);
        return a2.a();
    }

    public static boolean x() {
        return a("miui.util.FeatureParser", "support_hangup_while_screen_off", false);
    }

    public static boolean y() {
        if (p.a() < 12 || Build.VERSION.SDK_INT < 28) {
            return false;
        }
        if (f4516a.contains(miui.os.Build.DEVICE)) {
            return true;
        }
        return f4517b.contains(miui.os.Build.DEVICE) ? miui.os.Build.TIME > f4518c.longValue() : miui.os.Build.TIME > f4519d.longValue();
    }

    public static boolean z() {
        return "cepheus".equals(miui.os.Build.DEVICE) || (p.a() >= 12 && Build.VERSION.SDK_INT >= 28 && "grus".equals(miui.os.Build.DEVICE));
    }
}
