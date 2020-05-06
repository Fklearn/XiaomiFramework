package com.miui.superpower.b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import com.miui.powercenter.utils.o;
import java.text.DecimalFormat;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static volatile f f8081a;

    /* renamed from: b  reason: collision with root package name */
    private int f8082b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f8083c;

    /* renamed from: d  reason: collision with root package name */
    private SharedPreferences f8084d;
    /* access modifiers changed from: private */
    public int e;
    /* access modifiers changed from: private */
    public boolean f = false;
    /* access modifiers changed from: private */
    public int g = 0;
    private Handler h;
    private a i;
    private a j;
    private a k;
    private a l;

    private class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Long f8085a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Integer f8086b;

        private a(Long l, Integer num) {
            this.f8085a = l;
            this.f8086b = num;
        }

        /* synthetic */ a(f fVar, Long l, Integer num, c cVar) {
            this(l, num);
        }
    }

    private f(Context context) {
        this.f8083c = context.getApplicationContext();
        PowerManager powerManager = (PowerManager) this.f8083c.getSystemService("power");
        if (powerManager != null) {
            this.f = !powerManager.isScreenOn();
        }
        this.f8082b = o.c(this.f8083c);
        this.f8084d = this.f8083c.getSharedPreferences("pref_battery_statistics", 0);
        this.e = this.f8084d.getInt("pref_battery_statistics_last_type", 0);
        HandlerThread handlerThread = new HandlerThread("PowerStatistics");
        handlerThread.start();
        this.h = new Handler(handlerThread.getLooper());
        int i2 = this.e;
        if (i2 == 2) {
            b(2);
        } else if (i2 == 1) {
            b(1);
        }
    }

    public static f a(Context context) {
        if (f8081a == null) {
            synchronized (f.class) {
                if (f8081a == null) {
                    f8081a = new f(context);
                }
            }
        }
        return f8081a;
    }

    private String a(int i2, boolean z) {
        return i2 == 1 ? z ? "pref_battery_statistics_super_sleep" : "pref_battery_statistics_super" : z ? "pref_battery_statistics_normal_sleep" : "pref_battery_statistics_normal";
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        this.f8084d.edit().putInt("pref_battery_statistics_last_type", i2).apply();
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3) {
        if (i2 < i3) {
            long currentTimeMillis = System.currentTimeMillis();
            if (this.g == 0) {
                this.g = 1;
                this.i = new a(this, Long.valueOf(currentTimeMillis), Integer.valueOf(i2), (c) null);
            } else {
                this.g = 2;
                a aVar = this.j;
                if (aVar == null) {
                    this.j = new a(this, Long.valueOf(currentTimeMillis), Integer.valueOf(i2), (c) null);
                } else {
                    Long unused = aVar.f8085a = Long.valueOf(currentTimeMillis);
                    Integer unused2 = this.j.f8086b = Integer.valueOf(i2);
                }
                a(this.i, this.j, this.e, false);
            }
            if (!this.f) {
                return;
            }
            if (this.k == null) {
                this.k = new a(this, Long.valueOf(currentTimeMillis), Integer.valueOf(i2), (c) null);
                return;
            }
            a aVar2 = this.l;
            if (aVar2 == null) {
                this.l = new a(this, Long.valueOf(currentTimeMillis), Integer.valueOf(i2), (c) null);
            } else {
                Long unused3 = aVar2.f8085a = Long.valueOf(currentTimeMillis);
                Integer unused4 = this.l.f8086b = Integer.valueOf(i2);
            }
            a(this.k, this.l, this.e, true);
        }
    }

    private void a(a aVar, a aVar2, int i2, boolean z) {
        SharedPreferences.Editor edit = this.f8084d.edit();
        edit.putFloat(a(i2, z), ((float) (aVar.f8086b.intValue() - aVar2.f8086b.intValue())) / (((float) (aVar2.f8085a.longValue() - aVar.f8085a.longValue())) / 3600000.0f));
        edit.apply();
    }

    private void a(String str, boolean z) {
        if (z) {
            c();
        } else {
            b();
        }
        SharedPreferences.Editor edit = this.f8084d.edit();
        edit.remove(str);
        edit.apply();
    }

    /* access modifiers changed from: private */
    public void b() {
        this.i = null;
        this.j = null;
        c();
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        try {
            b(i2, true);
            b(i2, false);
        } catch (Exception unused) {
            b();
        }
        this.g = 0;
    }

    /* access modifiers changed from: private */
    public void b(int i2, boolean z) {
        String a2 = a(i2, z);
        if (this.f8084d.contains(a2)) {
            float f2 = this.f8084d.getFloat(a2, 0.0f);
            if (f2 != 0.0f) {
                double doubleValue = Double.valueOf(new DecimalFormat("0.00").format((double) (100.0f / f2))).doubleValue();
                if (doubleValue > 0.0d && doubleValue < 1000.0d) {
                    if (i2 == 1) {
                        h.b(doubleValue, z);
                    } else {
                        h.a(doubleValue, z);
                    }
                }
                a(a2, z);
            }
        }
    }

    private void c() {
        this.k = null;
        this.l = null;
    }

    public void a() {
        this.h.post(new d(this));
    }

    public void a(int i2, int i3, boolean z) {
        this.h.post(new e(this, z, i2, i3));
    }

    public void a(boolean z) {
        this.h.post(new c(this, z));
    }
}
