package b.b.a.e;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.HashMap;
import miui.provider.ExtraSettings;
import miui.telephony.SubscriptionManager;

public final class c {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1413a = "antispam_enable_for_sim_1";

    /* renamed from: b  reason: collision with root package name */
    public static final String f1414b = "antispam_enable_for_sim_2";

    /* renamed from: c  reason: collision with root package name */
    public static String f1415c = "antispam_action_source";

    /* renamed from: d  reason: collision with root package name */
    public static String f1416d = "action_source_sms";
    public static String e = "action_source_call";
    public static String f = "action_source_other";
    public static final HashMap<Integer, HashMap<Integer, String>> g = new a();
    public static final HashMap<Integer, Integer> h = new b();
    public static final String i = "show_notification_type";
    public static final String j = "show_notification_type_sim_2";

    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f1417a = c.a(Uri.parse("content://antispam/log"), 0);

        /* renamed from: b  reason: collision with root package name */
        public static final Uri f1418b = c.a(Uri.parse("content://antispam/logconversation"), 0);

        /* renamed from: c  reason: collision with root package name */
        public static final Uri f1419c = c.a(Uri.parse("content://antispam/log_sms"), 0);
    }

    public static final class b {

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f1420a = c.a(Uri.parse("content://antispam/phone_list"), 0);

        /* renamed from: b  reason: collision with root package name */
        public static final Uri f1421b = c.a(Uri.parse("content://antispam/unsynced_count"), 0);

        /* renamed from: c  reason: collision with root package name */
        public static final Uri f1422c = c.a(Uri.parse("content://antispam/synced_count"), 0);
    }

    /* renamed from: b.b.a.e.c$c  reason: collision with other inner class name */
    public static final class C0022c {

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f1423a = Uri.parse("content://antispam/phone_list");

        /* renamed from: b  reason: collision with root package name */
        public static final Uri f1424b = Uri.parse("content://mms-sms/blocked");
    }

    public static int a(int i2) {
        return h.get(Integer.valueOf(i2)).intValue();
    }

    public static int a(Context context, int i2) {
        return Settings.Secure.getInt(context.getContentResolver(), i2 == 1 ? i : j, 0);
    }

    public static int a(Context context, String str, int i2) {
        return Settings.Secure.getInt(context.getContentResolver(), str, i2);
    }

    public static long a(Context context) {
        return Settings.Secure.getLong(context.getContentResolver(), "sms_classifier_update_time", 0);
    }

    public static Uri a(Uri uri, int i2) {
        return q.a(uri, i2);
    }

    public static void a(Context context, int i2, int i3) {
        Settings.Secure.putInt(context.getContentResolver(), i3 == 1 ? i : j, i2);
    }

    public static void a(Context context, int i2, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), i2 == 1 ? f1413a : f1414b, z);
    }

    public static void a(Context context, long j2) {
        Settings.Secure.putLong(context.getContentResolver(), "sms_classifier_update_time", j2);
    }

    public static void a(Context context, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "antispam_settings_shared_for_sims", z);
    }

    public static boolean a(Context context, String str) {
        return a(context, str, false);
    }

    public static boolean a(Context context, String str, boolean z) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), str, z);
    }

    public static int[] a(Integer[] numArr) {
        if (numArr == null) {
            return null;
        }
        if (numArr.length == 0) {
            return new int[0];
        }
        int[] iArr = new int[numArr.length];
        for (int i2 = 0; i2 < numArr.length; i2++) {
            iArr[i2] = numArr[i2].intValue();
        }
        return iArr;
    }

    public static int b(Context context) {
        return ExtraSettings.Secure.getInt(context.getContentResolver(), "virtual_sim_slot_id", 0);
    }

    public static String b(int i2) {
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 10 ? "" : "mark_guide_harass" : "mark_guide_sell" : "mark_guide_agent" : "mark_guide_fraud";
    }

    public static void b(Context context, String str, int i2) {
        Settings.Secure.putInt(context.getContentResolver(), str, i2);
    }

    public static void b(Context context, String str, boolean z) {
        c(context, str, z);
    }

    public static void b(Context context, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "has_new_antispam", z);
    }

    public static boolean b(Context context, int i2) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), i2 == 1 ? f1413a : f1414b, true);
    }

    public static void c(Context context, String str, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), str, z);
    }

    public static void c(Context context, boolean z) {
        c(context, "mark_guide_is_set", z);
    }

    public static boolean c(Context context) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), "has_new_antispam", false);
    }

    public static boolean c(Context context, int i2) {
        if (i2 == 1) {
            return a(context, "fraud_num_state", 1) == 0 || a(context, "agent_num_state", 1) == 0 || a(context, "sell_num_state", 1) == 0;
        }
        if (i2 == 2) {
            return a(context, "fraud_num_state_sim_2", 1) == 0 || a(context, "agent_num_state_sim_2", 1) == 0 || a(context, "sell_num_state_sim_2", 1) == 0;
        }
        return false;
    }

    public static void d(Context context, boolean z) {
        ExtraSettings.Secure.putBoolean(context.getContentResolver(), "sms_classifier_auto_update", z);
    }

    public static boolean d(Context context) {
        if (e(context)) {
            return b(context, 1);
        }
        boolean z = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(0) != null;
        boolean z2 = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(1) != null;
        if (!z && !z2) {
            return b(context, 1) || b(context, 2);
        }
        if (!b(context, 1) || !z) {
            return b(context, 2) && z2;
        }
        return true;
    }

    public static boolean e(Context context) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), "antispam_settings_shared_for_sims", true);
    }

    public static boolean f(Context context) {
        return ExtraSettings.Secure.getBoolean(context.getContentResolver(), "sms_classifier_auto_update", true);
    }

    public static boolean g(Context context) {
        return !TextUtils.isEmpty(ExtraSettings.Secure.getString(context.getContentResolver(), "virtual_sim_imsi", ""));
    }

    public static void h(Context context) {
        b(context, "fraud_num_state", 1);
        b(context, "agent_num_state", 1);
        b(context, "sell_num_state", 1);
        b(context, "harass_num_state", 1);
        b(context, "fraud_num_state_sim_2", 1);
        b(context, "agent_num_state_sim_2", 1);
        b(context, "sell_num_state_sim_2", 1);
        b(context, "harass_num_state_sim_2", 1);
    }
}
