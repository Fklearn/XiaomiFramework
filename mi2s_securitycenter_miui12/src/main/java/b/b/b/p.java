package b.b.b;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import b.b.b.d.m;
import b.b.b.d.n;
import com.miui.common.persistence.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import miui.os.Build;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static ArrayList<String> f1579a;

    /* renamed from: b  reason: collision with root package name */
    private static ArrayList<String> f1580b;

    /* renamed from: c  reason: collision with root package name */
    static final String f1581c = Application.c().getString(R.string.preference_key_open_virus_cloud_scan);

    public static String a() {
        String a2 = b.a("toggle_engine_one", "");
        if (!TextUtils.isEmpty(a2)) {
            return a2;
        }
        String a3 = b.a((Context) Application.d()).a();
        b(a3);
        return a3;
    }

    public static ArrayList<String> a(Context context) {
        if (f1580b == null) {
            f1580b = n.e(context);
        }
        return b.a("key_safepay_monitored_activity_list", f1580b);
    }

    public static void a(int i) {
        b.b("key_last_scan_risk_count", i);
    }

    public static void a(long j) {
        b.b("key_last_cloud_data_update_time", j);
    }

    public static void a(m mVar) {
        b.b("key_pay_safety_status", mVar.b());
    }

    public static void a(String str, boolean z) {
        b.b(str, z);
    }

    public static void a(ArrayList<String> arrayList) {
        b.b("key_safepay_monitored_activity_list", arrayList);
    }

    public static void a(boolean z) {
        b.b("key_first_enter_antivirus_v3", z);
    }

    public static boolean a(String str) {
        return b.a(str, true);
    }

    public static ArrayList<String> b(Context context) {
        if (f1579a == null) {
            f1579a = n.f(context);
        }
        return b.a("key_safepay_monitored_apps_list", f1579a);
    }

    public static void b(int i) {
        b.b("key_last_scan_virus_count", i);
    }

    public static void b(long j) {
        b.b("key_latest_virus_scan_date", j);
        Settings.Secure.putLong(Application.d().getContentResolver(), "key_latest_virus_scan_date", j);
    }

    public static void b(String str) {
        b.b("toggle_engine_one", str);
    }

    public static void b(ArrayList<String> arrayList) {
        b.b("key_safepay_monitored_apps_list", arrayList);
    }

    public static void b(boolean z) {
        b.b("key_free_wifi_alert", z);
    }

    public static boolean b() {
        return b.a("key_free_wifi_alert", true);
    }

    public static long c() {
        return b.a("key_last_cloud_data_update_time", 0);
    }

    public static void c(String str) {
        b.b("key_safepay_wifi_scan_result", str);
    }

    public static void c(ArrayList<String> arrayList) {
        b.b("key_safepay_sign_exception", arrayList);
    }

    public static void c(boolean z) {
        b.b("key_safepay_auto_scan_state", z);
    }

    public static m d() {
        return m.a(b.a("key_pay_safety_status", m.a().b()));
    }

    public static void d(boolean z) {
        b.b("key_check_item_root", z);
    }

    public static int e() {
        return b.a("key_last_scan_risk_count", 0);
    }

    public static void e(boolean z) {
        b.b("key_safepay_input_method_state", z);
    }

    public static int f() {
        return b.a("key_last_scan_virus_count", 0);
    }

    public static void f(boolean z) {
        b.b("key_check_item_update", z);
    }

    public static ArrayList<String> g() {
        return b.a("key_safepay_sign_exception", (ArrayList<String>) new ArrayList());
    }

    public static void g(boolean z) {
        b.b(f1581c, z);
    }

    public static String h() {
        return b.a("key_safepay_wifi_scan_result", "");
    }

    public static void h(boolean z) {
        b.b("PREF_KEY_WHITELIST_HAS_APP", z);
    }

    public static void i(boolean z) {
        b.b("key_check_item_wifi", z);
    }

    public static boolean i() {
        return b.a("key_first_enter_antivirus_v3", true);
    }

    public static boolean j() {
        return b.a("key_safepay_auto_scan_state", true);
    }

    public static boolean k() {
        return !Build.IS_ALPHA_BUILD && b.a("key_check_item_root", true);
    }

    public static boolean l() {
        return b.a("key_safepay_input_method_state", true);
    }

    public static boolean m() {
        return b.a("key_check_item_update", !Build.IS_ALPHA_BUILD);
    }

    public static boolean n() {
        return b.a(f1581c, false);
    }

    public static boolean o() {
        return b.a("PREF_KEY_WHITELIST_HAS_APP", false);
    }

    public static boolean p() {
        return b.a("key_check_item_wifi", true);
    }
}
