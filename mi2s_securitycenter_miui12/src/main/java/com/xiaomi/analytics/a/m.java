package com.xiaomi.analytics.a;

public class m implements Comparable<m> {

    /* renamed from: a  reason: collision with root package name */
    public int f8328a = 1;

    /* renamed from: b  reason: collision with root package name */
    public int f8329b = 0;

    /* renamed from: c  reason: collision with root package name */
    public int f8330c = 0;

    public m(String str) {
        try {
            String[] split = str.split("\\.");
            this.f8328a = Integer.parseInt(split[0]);
            this.f8329b = Integer.parseInt(split[1]);
            this.f8330c = Integer.parseInt(split[2]);
        } catch (Exception unused) {
        }
    }

    /* renamed from: a */
    public int compareTo(m mVar) {
        if (mVar == null) {
            return 1;
        }
        int i = this.f8328a;
        int i2 = mVar.f8328a;
        if (i != i2) {
            return i - i2;
        }
        int i3 = this.f8329b;
        int i4 = mVar.f8329b;
        return i3 != i4 ? i3 - i4 : this.f8330c - mVar.f8330c;
    }

    public String toString() {
        return this.f8328a + "." + this.f8329b + "." + this.f8330c;
    }
}
