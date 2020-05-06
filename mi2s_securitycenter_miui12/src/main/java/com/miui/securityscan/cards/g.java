package com.miui.securityscan.cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static g f7651a;

    /* renamed from: b  reason: collision with root package name */
    private final Handler f7652b;

    /* renamed from: c  reason: collision with root package name */
    private SharedPreferences f7653c;

    /* renamed from: d  reason: collision with root package name */
    private ArrayList<a> f7654d = new ArrayList<>();
    private ArrayList<a> e = new ArrayList<>();
    /* access modifiers changed from: private */
    public Context f;

    public interface a {
        void a(String str, int i, int i2);
    }

    private g(Context context) {
        this.f = context.getApplicationContext();
        this.f7653c = context.getSharedPreferences("install_status", 0);
        this.f7652b = new Handler(Looper.getMainLooper());
    }

    public static synchronized g a(Context context) {
        g gVar;
        synchronized (g.class) {
            if (f7651a == null) {
                f7651a = new g(context);
            }
            gVar = f7651a;
        }
        return gVar;
    }

    public int a(String str) {
        SharedPreferences sharedPreferences = this.f7653c;
        return sharedPreferences.getInt(str + "_download_progress", -1);
    }

    public void a(a aVar) {
        if (aVar != null) {
            this.e.add(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }

    public void a(String str, int i) {
        a(str, i, -1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x007b A[LOOP:0: B:18:0x0075->B:20:0x007b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0091 A[LOOP:1: B:22:0x008b->B:24:0x0091, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(java.lang.String r5, int r6, int r7) {
        /*
            r4 = this;
            r0 = -6
            if (r6 == r0) goto L_0x006c
            r0 = -3
            if (r6 == r0) goto L_0x006c
            r0 = -2
            if (r6 == r0) goto L_0x006c
            r0 = 4
            if (r6 == r0) goto L_0x003c
            r0 = 5
            if (r6 == r0) goto L_0x001d
            android.content.SharedPreferences r0 = r4.f7653c
            android.content.SharedPreferences$Editor r0 = r0.edit()
        L_0x0015:
            android.content.SharedPreferences$Editor r0 = r0.putInt(r5, r6)
            r0.apply()
            goto L_0x006f
        L_0x001d:
            android.content.SharedPreferences r0 = r4.f7653c
            android.content.SharedPreferences$Editor r0 = r0.edit()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            java.lang.String r2 = "_download_progress"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.content.SharedPreferences$Editor r1 = r0.putInt(r1, r7)
            r1.apply()
            goto L_0x0015
        L_0x003c:
            android.content.SharedPreferences r0 = r4.f7653c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            java.lang.String r2 = "_auto_open"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r2 = 0
            boolean r0 = r0.getBoolean(r1, r2)
            if (r0 == 0) goto L_0x006c
            android.os.Handler r0 = r4.f7652b
            com.miui.securityscan.cards.e r1 = new com.miui.securityscan.cards.e
            r1.<init>(r4, r5)
            r0.post(r1)
            android.os.Handler r0 = r4.f7652b
            com.miui.securityscan.cards.f r1 = new com.miui.securityscan.cards.f
            r1.<init>(r4, r5)
            r2 = 3000(0xbb8, double:1.482E-320)
            r0.postDelayed(r1, r2)
        L_0x006c:
            r4.c((java.lang.String) r5)
        L_0x006f:
            java.util.ArrayList<com.miui.securityscan.cards.g$a> r0 = r4.f7654d
            java.util.Iterator r0 = r0.iterator()
        L_0x0075:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0085
            java.lang.Object r1 = r0.next()
            com.miui.securityscan.cards.g$a r1 = (com.miui.securityscan.cards.g.a) r1
            r1.a(r5, r6, r7)
            goto L_0x0075
        L_0x0085:
            java.util.ArrayList<com.miui.securityscan.cards.g$a> r0 = r4.e
            java.util.Iterator r0 = r0.iterator()
        L_0x008b:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x009b
            java.lang.Object r1 = r0.next()
            com.miui.securityscan.cards.g$a r1 = (com.miui.securityscan.cards.g.a) r1
            r1.a(r5, r6, r7)
            goto L_0x008b
        L_0x009b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.g.a(java.lang.String, int, int):void");
    }

    public void a(String str, boolean z) {
        if (str != null) {
            SharedPreferences.Editor edit = this.f7653c.edit();
            edit.putBoolean(str + "_auto_open", z).apply();
        }
    }

    public int b(String str) {
        return this.f7653c.getInt(str, -100);
    }

    public void b(a aVar) {
        if (aVar != null) {
            this.f7654d.add(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }

    public void c(a aVar) {
        if (aVar != null) {
            this.e.remove(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }

    public void c(String str) {
        if (str != null) {
            SharedPreferences.Editor edit = this.f7653c.edit();
            edit.remove(str);
            edit.remove(str + "_download_progress");
            edit.remove(str + "_auto_open");
            edit.apply();
        }
    }

    public void d(a aVar) {
        if (aVar != null) {
            this.f7654d.remove(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }
}
