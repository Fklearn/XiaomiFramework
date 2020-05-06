package com.miui.optimizemanage.memoryclean;

import android.content.ComponentName;
import java.util.ArrayList;
import java.util.List;

public class j {

    /* renamed from: a  reason: collision with root package name */
    public String f5972a;

    /* renamed from: b  reason: collision with root package name */
    public int f5973b;

    /* renamed from: c  reason: collision with root package name */
    public int[] f5974c;

    /* renamed from: d  reason: collision with root package name */
    public long f5975d;
    public boolean e = false;
    public boolean f = false;
    public boolean g = false;
    public boolean h = false;
    public List<Integer> i = new ArrayList();
    public boolean j = false;
    public long[] k;
    public List<a> l;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f5976a;

        /* renamed from: b  reason: collision with root package name */
        public ComponentName f5977b;

        /* renamed from: c  reason: collision with root package name */
        public long f5978c;
    }

    public static String a(String str, int i2) {
        return str + i2;
    }

    private String b() {
        StringBuilder sb = new StringBuilder();
        if (this.f5974c != null) {
            sb.append(" pid ");
            for (int append : this.f5974c) {
                sb.append(append);
                sb.append(" ");
            }
        } else {
            sb.append(" null ");
        }
        return sb.toString();
    }

    public String a() {
        return a(this.f5972a, this.f5973b);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        j jVar = (j) obj;
        return jVar.f5972a.equals(this.f5972a) && jVar.f5973b == this.f5973b;
    }

    public String toString() {
        int i2;
        StringBuilder sb = new StringBuilder();
        sb.append("packageName ");
        sb.append(this.f5972a);
        sb.append(" uid ");
        sb.append(this.f5973b);
        sb.append(" pids ");
        sb.append(b());
        sb.append(" memorySize ");
        sb.append(this.f5975d);
        sb.append(" isLocked ");
        sb.append(this.e);
        sb.append(" hasActivity ");
        sb.append(this.f);
        sb.append(" isCached ");
        sb.append(this.g);
        sb.append(" taskIds ");
        sb.append(this.i);
        sb.append(" serviceCount ");
        if (this.l == null) {
            i2 = 0;
        } else {
            i2 = this.l.size() + " show " + this.j;
        }
        sb.append(i2);
        return sb.toString();
    }
}
