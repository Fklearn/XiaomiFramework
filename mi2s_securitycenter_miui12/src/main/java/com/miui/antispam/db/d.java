package com.miui.antispam.db;

import android.content.Context;
import b.b.a.e.c;
import com.miui.common.persistence.b;
import com.miui.securitycenter.h;

public class d {
    public static int a(int i) {
        return b.a(i == 1 ? "backsound_index" : "backsound_index_sim_2", 0);
    }

    public static int a(Context context, String str, int i, int i2) {
        return i == 1 ? c.a(context, str, i2) : b.a(str, i2);
    }

    public static long a() {
        return b.a("clouds_data_version", 0);
    }

    public static void a(int i, int i2) {
        b.b(i == 1 ? "backsound_index" : "backsound_index_sim_2", i2);
    }

    public static void a(int i, boolean z) {
        b.b(i == 1 ? "is_call_transfer_blocked" : "is_call_transfer_blocked_sim_2", z);
    }

    public static void a(long j) {
        b.b("clouds_data_version", j);
    }

    public static void a(Context context, int i, boolean z) {
        c.b(context, i == 1 ? "agent_num_state" : "agent_num_state_sim_2", z ^ true ? 1 : 0);
    }

    public static void a(String str) {
        b.b("update_state", str);
    }

    public static void a(boolean z) {
        b.b("dataMigration", z);
    }

    public static boolean a(Context context, int i) {
        return c.a(context, i == 1 ? "agent_num_state" : "agent_num_state_sim_2", 1) == 0;
    }

    public static int b() {
        return b.a("unread_mms_count", 0);
    }

    public static void b(int i, boolean z) {
        b.b(i == 1 ? "is_repeated_marked_number_permit" : "is_repeated_marked_number_permit_sim_2", z);
    }

    public static void b(Context context, int i, boolean z) {
        c.b(context, i == 1 ? "fraud_num_state" : "fraud_num_state_sim_2", z ^ true ? 1 : 0);
    }

    public static void b(Context context, String str, int i, int i2) {
        if (i == 1) {
            c.b(context, str, i2);
        } else {
            b.b(str, i2);
        }
    }

    public static void b(boolean z) {
        b.b("antispam_data_migration", z);
    }

    public static boolean b(int i) {
        return b.a(i == 1 ? "is_call_transfer_blocked" : "is_call_transfer_blocked_sim_2", false);
    }

    public static boolean b(Context context, int i) {
        return c.a(context, i == 1 ? "fraud_num_state" : "fraud_num_state_sim_2", 1) == 0;
    }

    public static int c() {
        return b.a("unread_phone_count", 0);
    }

    public static void c(Context context, int i, boolean z) {
        c.b(context, i == 1 ? "harass_num_state" : "harass_num_state_sim_2", z ^ true ? 1 : 0);
    }

    public static void c(boolean z) {
        b.b("reported_number_settings_reset", z);
    }

    public static boolean c(int i) {
        return b.a(i == 1 ? "is_repeated_marked_number_permit" : "is_repeated_marked_number_permit_sim_2", true);
    }

    public static boolean c(Context context, int i) {
        return c.a(context, i == 1 ? "harass_num_state" : "harass_num_state_sim_2", 1) == 0;
    }

    public static void d(int i) {
        b.b("unread_mms_count", i);
    }

    public static void d(Context context, int i, boolean z) {
        c.b(context, i == 1 ? "sell_num_state" : "sell_num_state_sim_2", z ^ true ? 1 : 0);
    }

    public static boolean d() {
        return h.i();
    }

    public static boolean d(Context context, int i) {
        return c.a(context, i == 1 ? "sell_num_state" : "sell_num_state_sim_2", 1) == 0;
    }

    public static void e(int i) {
        b.b("unread_phone_count", i);
    }

    public static void e(Context context, int i) {
        c.b(context, i == 1 ? "mark_time_agent" : "mark_time_agent_sim_2", 0);
    }

    public static boolean e() {
        return b.a("antispam_data_migration", false);
    }

    public static void f(Context context, int i) {
        c.b(context, i == 1 ? "mark_time_fraud" : "mark_time_fraud_sim_2", 0);
    }

    public static boolean f() {
        return b.a("reported_number_settings_reset", false);
    }

    public static void g(Context context, int i) {
        c.b(context, i == 1 ? "mark_time_sell" : "mark_time_sell_sim_2", 0);
    }
}
