package com.miui.gamebooster.videobox.settings;

import android.content.ContentResolver;
import android.content.Context;
import com.miui.activityutil.h;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.C0384o;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class f {

    public static class a {
        public static int a(boolean z) {
            return z ? 1 : 0;
        }
    }

    public static String a() {
        return b.a("pref_current_video_app", h.f2289a);
    }

    public static ArrayList<String> a(ArrayList<String> arrayList) {
        return b.a("pref_video_box_app_list", arrayList);
    }

    public static List<String> a(Context context) {
        ArrayList<String> c2 = C0382m.c("gamebooster", "vtb_net_support_apps", context);
        return (c2 == null || c2.isEmpty()) ? C0382m.a("vtb_default_support_list", context.getApplicationContext()) : c2;
    }

    public static void a(int i) {
        b.b("pref_video_box_dispaly_style", i);
    }

    public static void a(Context context, boolean z) {
        ContentResolver contentResolver = context.getContentResolver();
        a.a(z);
        C0384o.b(contentResolver, "pref_videobox_switch_status", z ? 1 : 0, -2);
    }

    public static void a(String str) {
        b.b("pref_current_video_app", str);
    }

    public static void a(boolean z) {
        b.b("pref_is_first_use_vtb_app_manager", z);
    }

    public static int b() {
        return b.a("pref_video_box_dispaly_style", 0);
    }

    public static void b(int i) {
        b.b("pref_movie_surround_level", i);
    }

    public static void b(ArrayList<String> arrayList) {
        if (arrayList != null) {
            HashSet hashSet = new HashSet();
            hashSet.addAll(arrayList);
            arrayList.clear();
            arrayList.addAll(hashSet);
            b.b("pref_video_box_app_list", arrayList);
        }
    }

    public static void b(boolean z) {
        b.b("key_videobox_hangup_ok", z);
    }

    public static boolean b(Context context) {
        return C0384o.a(context.getContentResolver(), "pref_videobox_switch_status", 1, -2) == 1;
    }

    public static int c() {
        return b.a("pref_movie_surround_level", 0);
    }

    public static void c(int i) {
        b.b("pref_movie_vocal_level", i);
    }

    public static void c(ArrayList<String> arrayList) {
        b.b("pref_vtb_support_vpp_apps", arrayList);
    }

    public static void c(boolean z) {
        b.b("key_videobox_milink_hangup_ok", z);
    }

    public static int d() {
        return b.a("pref_movie_vocal_level", 0);
    }

    public static void d(int i) {
        b.b("pref_videobox_line_location", i);
    }

    public static void d(boolean z) {
        b.b("pref_video_box_hangup_pkg", z);
    }

    public static int e() {
        return b.a("pref_videobox_line_location", 0);
    }

    public static void e(boolean z) {
        b.b("pref_videobox_line_status", z);
    }

    public static void f(boolean z) {
        b.b("pref_video_booster_status", z);
    }

    public static boolean f() {
        return b.a("pref_videobox_line_status", true);
    }

    public static ArrayList<String> g() {
        return b.a("pref_vtb_support_vpp_apps", (ArrayList<String>) new ArrayList());
    }

    public static void g(boolean z) {
        b.b("pref_videobox_vpp_status", z);
    }

    public static boolean h() {
        return b.a("pref_is_first_use_vtb_app_manager", true);
    }

    public static boolean i() {
        return b.a("key_videobox_hangup_ok", false);
    }

    public static boolean j() {
        return b.a("key_videobox_milink_hangup_ok", false);
    }

    public static boolean k() {
        return b.a("pref_video_box_hangup_pkg", false);
    }

    public static boolean l() {
        return b.a("pref_videobox_vpp_status", false);
    }
}
