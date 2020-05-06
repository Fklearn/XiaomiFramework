package androidx.recyclerview.widget;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public abstract class B {

    /* renamed from: a  reason: collision with root package name */
    protected final RecyclerView.g f1079a;

    /* renamed from: b  reason: collision with root package name */
    private int f1080b;

    /* renamed from: c  reason: collision with root package name */
    final Rect f1081c;

    private B(RecyclerView.g gVar) {
        this.f1080b = Integer.MIN_VALUE;
        this.f1081c = new Rect();
        this.f1079a = gVar;
    }

    /* synthetic */ B(RecyclerView.g gVar, z zVar) {
        this(gVar);
    }

    public static B a(RecyclerView.g gVar) {
        return new z(gVar);
    }

    public static B a(RecyclerView.g gVar, int i) {
        if (i == 0) {
            return a(gVar);
        }
        if (i == 1) {
            return b(gVar);
        }
        throw new IllegalArgumentException("invalid orientation");
    }

    public static B b(RecyclerView.g gVar) {
        return new A(gVar);
    }

    public abstract int a();

    public abstract int a(View view);

    public abstract void a(int i);

    public abstract int b();

    public abstract int b(View view);

    public abstract int c();

    public abstract int c(View view);

    public abstract int d();

    public abstract int d(View view);

    public abstract int e();

    public abstract int e(View view);

    public abstract int f();

    public abstract int f(View view);

    public abstract int g();

    public int h() {
        if (Integer.MIN_VALUE == this.f1080b) {
            return 0;
        }
        return g() - this.f1080b;
    }

    public void i() {
        this.f1080b = g();
    }
}
