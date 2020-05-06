package androidx.recyclerview.widget;

import androidx.annotation.NonNull;

/* renamed from: androidx.recyclerview.widget.c  reason: case insensitive filesystem */
public class C0162c implements x {

    /* renamed from: a  reason: collision with root package name */
    final x f1186a;

    /* renamed from: b  reason: collision with root package name */
    int f1187b = 0;

    /* renamed from: c  reason: collision with root package name */
    int f1188c = -1;

    /* renamed from: d  reason: collision with root package name */
    int f1189d = -1;
    Object e = null;

    public C0162c(@NonNull x xVar) {
        this.f1186a = xVar;
    }

    public void a() {
        int i = this.f1187b;
        if (i != 0) {
            if (i == 1) {
                this.f1186a.a(this.f1188c, this.f1189d);
            } else if (i == 2) {
                this.f1186a.b(this.f1188c, this.f1189d);
            } else if (i == 3) {
                this.f1186a.a(this.f1188c, this.f1189d, this.e);
            }
            this.e = null;
            this.f1187b = 0;
        }
    }

    public void a(int i, int i2) {
        int i3;
        if (this.f1187b == 1 && i >= (i3 = this.f1188c)) {
            int i4 = this.f1189d;
            if (i <= i3 + i4) {
                this.f1189d = i4 + i2;
                this.f1188c = Math.min(i, i3);
                return;
            }
        }
        a();
        this.f1188c = i;
        this.f1189d = i2;
        this.f1187b = 1;
    }

    public void a(int i, int i2, Object obj) {
        int i3;
        if (this.f1187b == 3) {
            int i4 = this.f1188c;
            int i5 = this.f1189d;
            if (i <= i4 + i5 && (i3 = i + i2) >= i4 && this.e == obj) {
                this.f1188c = Math.min(i, i4);
                this.f1189d = Math.max(i5 + i4, i3) - this.f1188c;
                return;
            }
        }
        a();
        this.f1188c = i;
        this.f1189d = i2;
        this.e = obj;
        this.f1187b = 3;
    }

    public void b(int i, int i2) {
        int i3;
        if (this.f1187b != 2 || (i3 = this.f1188c) < i || i3 > i + i2) {
            a();
            this.f1188c = i;
            this.f1189d = i2;
            this.f1187b = 2;
            return;
        }
        this.f1189d += i2;
        this.f1188c = i;
    }

    public void c(int i, int i2) {
        a();
        this.f1186a.c(i, i2);
    }
}
