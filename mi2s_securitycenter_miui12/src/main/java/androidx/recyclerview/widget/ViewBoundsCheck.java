package androidx.recyclerview.widget;

import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class ViewBoundsCheck {

    /* renamed from: a  reason: collision with root package name */
    final b f1171a;

    /* renamed from: b  reason: collision with root package name */
    a f1172b = new a();

    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewBounds {
    }

    static class a {

        /* renamed from: a  reason: collision with root package name */
        int f1173a = 0;

        /* renamed from: b  reason: collision with root package name */
        int f1174b;

        /* renamed from: c  reason: collision with root package name */
        int f1175c;

        /* renamed from: d  reason: collision with root package name */
        int f1176d;
        int e;

        a() {
        }

        /* access modifiers changed from: package-private */
        public int a(int i, int i2) {
            if (i > i2) {
                return 1;
            }
            return i == i2 ? 2 : 4;
        }

        /* access modifiers changed from: package-private */
        public void a(int i) {
            this.f1173a = i | this.f1173a;
        }

        /* access modifiers changed from: package-private */
        public void a(int i, int i2, int i3, int i4) {
            this.f1174b = i;
            this.f1175c = i2;
            this.f1176d = i3;
            this.e = i4;
        }

        /* access modifiers changed from: package-private */
        public boolean a() {
            int i = this.f1173a;
            if ((i & 7) != 0 && (i & (a(this.f1176d, this.f1174b) << 0)) == 0) {
                return false;
            }
            int i2 = this.f1173a;
            if ((i2 & 112) != 0 && (i2 & (a(this.f1176d, this.f1175c) << 4)) == 0) {
                return false;
            }
            int i3 = this.f1173a;
            if ((i3 & 1792) != 0 && (i3 & (a(this.e, this.f1174b) << 8)) == 0) {
                return false;
            }
            int i4 = this.f1173a;
            return (i4 & 28672) == 0 || (i4 & (a(this.e, this.f1175c) << 12)) != 0;
        }

        /* access modifiers changed from: package-private */
        public void b() {
            this.f1173a = 0;
        }
    }

    interface b {
        int a();

        int a(View view);

        View a(int i);

        int b();

        int b(View view);
    }

    ViewBoundsCheck(b bVar) {
        this.f1171a = bVar;
    }

    /* access modifiers changed from: package-private */
    public View a(int i, int i2, int i3, int i4) {
        int a2 = this.f1171a.a();
        int b2 = this.f1171a.b();
        int i5 = i2 > i ? 1 : -1;
        View view = null;
        while (i != i2) {
            View a3 = this.f1171a.a(i);
            this.f1172b.a(a2, b2, this.f1171a.a(a3), this.f1171a.b(a3));
            if (i3 != 0) {
                this.f1172b.b();
                this.f1172b.a(i3);
                if (this.f1172b.a()) {
                    return a3;
                }
            }
            if (i4 != 0) {
                this.f1172b.b();
                this.f1172b.a(i4);
                if (this.f1172b.a()) {
                    view = a3;
                }
            }
            i += i5;
        }
        return view;
    }

    /* access modifiers changed from: package-private */
    public boolean a(View view, int i) {
        this.f1172b.a(this.f1171a.a(), this.f1171a.b(), this.f1171a.a(view), this.f1171a.b(view));
        if (i == 0) {
            return false;
        }
        this.f1172b.b();
        this.f1172b.a(i);
        return this.f1172b.a();
    }
}
