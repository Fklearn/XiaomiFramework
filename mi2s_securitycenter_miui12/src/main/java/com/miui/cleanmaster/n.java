package com.miui.cleanmaster;

import android.content.Context;
import android.content.SharedPreferences;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private SharedPreferences f3760a;

    public n(Context context) {
        this.f3760a = context.getSharedPreferences("cm_notification_ids", 0);
    }

    private int a() {
        return this.f3760a.getInt("key_max_notification_id", 1001);
    }

    private void a(int i) {
        this.f3760a.edit().putInt("key_max_notification_id", i).commit();
    }

    private void a(String str, int i) {
        this.f3760a.edit().putInt(str, i).commit();
    }

    public int a(String str) {
        int i = this.f3760a.getInt(str, -1);
        if (i != -1) {
            return i;
        }
        int a2 = a() + 1;
        a(a2);
        a(str, a2);
        return a2;
    }
}
