package com.miui.common.persistence;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import b.b.c.j.B;
import com.miui.securitycenter.Application;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final ContentResolver f3835a = Application.d().getContentResolver();

    public static int a(String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("key", str);
        bundle.putInt("default", i);
        Bundle a2 = a("GET", bundle);
        return a2 == null ? i : a2.getInt(str, i);
    }

    public static long a(String str, long j) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 4);
        bundle.putString("key", str);
        bundle.putLong("default", j);
        Bundle a2 = a("GET", bundle);
        return a2 == null ? j : a2.getLong(str, j);
    }

    private static Bundle a(String str, Bundle bundle) {
        Uri uri;
        if (B.i()) {
            Uri parse = Uri.parse("content://com.miui.securitycenter.remoteprovider");
            Uri.Builder buildUpon = parse.buildUpon();
            buildUpon.encodedAuthority("0@" + parse.getEncodedAuthority());
            uri = buildUpon.build();
        } else {
            uri = Uri.parse("content://com.miui.securitycenter.remoteprovider");
        }
        return f3835a.call(uri, "callPreference", str, bundle);
    }

    private static Bundle a(String str, Bundle bundle, int i) {
        Uri parse = Uri.parse("content://com.miui.securitycenter.remoteprovider");
        Uri.Builder buildUpon = parse.buildUpon();
        buildUpon.encodedAuthority("" + i + "@" + parse.getEncodedAuthority());
        return f3835a.call(buildUpon.build(), "callPreference", str, bundle);
    }

    public static String a(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        bundle.putString("key", str);
        bundle.putString("default", str2);
        Bundle a2 = a("GET", bundle);
        return a2 == null ? str2 : a2.getString(str, str2);
    }

    public static ArrayList<String> a(String str, ArrayList<String> arrayList) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 5);
        bundle.putString("key", str);
        bundle.putStringArrayList("default", arrayList);
        Bundle a2 = a("GET", bundle);
        return a2 == null ? arrayList : a2.getStringArrayList(str);
    }

    public static void a(String str, float f) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 3);
        bundle.putString("key", str);
        bundle.putFloat(MiStat.Param.VALUE, f);
        a("SET", bundle);
    }

    public static boolean a(String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("key", str);
        bundle.putBoolean("default", z);
        Bundle a2 = a("GET", bundle);
        return a2 == null ? z : a2.getBoolean(str, z);
    }

    public static boolean a(String str, boolean z, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("key", str);
        bundle.putBoolean("default", z);
        Bundle a2 = Build.VERSION.SDK_INT >= 23 ? a("GET", bundle, i) : a("GET", bundle);
        return a2 == null ? z : a2.getBoolean(str, z);
    }

    public static void b(String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("key", str);
        bundle.putInt(MiStat.Param.VALUE, i);
        a("SET", bundle);
    }

    public static void b(String str, long j) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 4);
        bundle.putString("key", str);
        bundle.putLong(MiStat.Param.VALUE, j);
        a("SET", bundle);
    }

    public static void b(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        bundle.putString("key", str);
        bundle.putString(MiStat.Param.VALUE, str2);
        a("SET", bundle);
        f3835a.notifyChange(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), str), (ContentObserver) null, false);
    }

    public static void b(String str, ArrayList<String> arrayList) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 5);
        bundle.putString("key", str);
        bundle.putStringArrayList(MiStat.Param.VALUE, arrayList);
        a("SET", bundle);
    }

    public static void b(String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("key", str);
        bundle.putBoolean(MiStat.Param.VALUE, z);
        a("SET", bundle);
        f3835a.notifyChange(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), str), (ContentObserver) null, false);
    }
}
