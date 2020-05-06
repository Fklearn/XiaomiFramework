package com.miui.powercenter.provider;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private List<a> f7169a = new ArrayList();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        int f7170a;

        /* renamed from: b  reason: collision with root package name */
        long f7171b;
    }

    private String a(Context context, a aVar, int i, long j, int i2, long j2) {
        if (aVar.f7170a - i < i2 || j - aVar.f7171b > j2) {
            return "";
        }
        int i3 = (((int) j2) / 1000) / 60;
        return context.getResources().getQuantityString(R.plurals.notification_battery_consume_abnormal_summary, i3, new Object[]{Integer.valueOf(i3), Integer.valueOf(i2)});
    }

    public String a(Context context, int i) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        for (int size = this.f7169a.size() - 1; size >= 0; size--) {
            a aVar = this.f7169a.get(size);
            String a2 = a(context, aVar, i, elapsedRealtime, 20, 600000);
            if (!TextUtils.isEmpty(a2)) {
                return a2;
            }
            String a3 = a(context, aVar, i, elapsedRealtime, 30, 1800000);
            if (!TextUtils.isEmpty(a3)) {
                return a3;
            }
            String a4 = a(context, aVar, i, elapsedRealtime, 50, 3600000);
            if (!TextUtils.isEmpty(a4)) {
                return a4;
            }
        }
        return "";
    }

    public void a() {
        this.f7169a.clear();
    }

    public void a(int i) {
        a aVar = new a();
        aVar.f7171b = SystemClock.elapsedRealtime();
        aVar.f7170a = i;
        a(aVar);
    }

    public void a(a aVar) {
        if (!this.f7169a.isEmpty()) {
            List<a> list = this.f7169a;
            if (list.get(list.size() - 1).f7170a <= aVar.f7170a) {
                return;
            }
        }
        this.f7169a.add(aVar);
    }
}
