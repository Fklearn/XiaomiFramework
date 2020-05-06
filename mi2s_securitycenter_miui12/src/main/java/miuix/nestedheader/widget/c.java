package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.j;
import androidx.core.view.l;
import androidx.core.view.n;
import androidx.core.view.p;
import d.c.b.b;
import java.util.ArrayList;
import java.util.List;

public class c extends FrameLayout implements n, j {

    /* renamed from: a  reason: collision with root package name */
    private final int[] f8871a;

    /* renamed from: b  reason: collision with root package name */
    private final int[] f8872b;

    /* renamed from: c  reason: collision with root package name */
    private int f8873c;

    /* renamed from: d  reason: collision with root package name */
    protected View f8874d;
    private final int[] e;
    private int f;
    private int g;
    private int h;
    private final p i;
    private final l j;
    private boolean k;
    private boolean l;
    private boolean m;
    private boolean n;
    private long o;
    private long p;
    private boolean q;
    private boolean r;
    private boolean s;
    private List<a> t;

    public interface a {
        void a(int i);

        void b(int i);

        void c(int i);
    }

    public c(Context context) {
        this(context, (AttributeSet) null);
    }

    public c(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public c(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f8871a = new int[2];
        this.f8872b = new int[2];
        this.e = new int[2];
        this.n = true;
        this.o = 0;
        this.p = 0;
        this.q = false;
        this.r = false;
        this.s = false;
        this.t = new ArrayList();
        this.i = new p(this);
        this.j = b.a((View) this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, d.e.c.NestedScrollingLayout);
        this.f8873c = obtainStyledAttributes.getResourceId(d.e.c.NestedScrollingLayout_scrollableView, 16908298);
        obtainStyledAttributes.recycle();
        setNestedScrollingEnabled(true);
    }

    private void a() {
        a(this.f);
    }

    private void d(int i2) {
        for (a c2 : this.t) {
            c2.c(i2);
        }
    }

    private void e(int i2) {
        for (a a2 : this.t) {
            a2.a(i2);
        }
    }

    private void f(int i2) {
        for (a b2 : this.t) {
            b2.b(i2);
        }
    }

    /* access modifiers changed from: protected */
    public void a(int i2) {
    }

    public void a(int i2, int i3, int i4, int i5, @Nullable int[] iArr, int i6, @NonNull int[] iArr2) {
        this.j.a(i2, i3, i4, i5, iArr, i6, iArr2);
    }

    public void a(int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (i2 > i3) {
            Log.w("NestedScrollingLayout", "wrong scrolling range: [%d, %d], making from=to");
            i2 = i3;
        }
        this.g = i2;
        this.h = i3;
        this.r = z;
        this.s = z2;
        int i4 = this.f;
        int i5 = this.g;
        if (i4 < i5) {
            this.f = i5;
        }
        int i6 = this.f;
        int i7 = this.h;
        if (i6 > i7) {
            this.f = i7;
        }
        if (((!z3 || !this.n) && !z4 && !z5) || !this.r) {
            if ((z3 && this.n) || z4) {
                this.f = this.g;
            }
            a();
        }
        this.f = 0;
        this.n = false;
        a();
    }

    public void a(a aVar) {
        this.t.add(aVar);
    }

    public void a(boolean z) {
        if (!this.q && z) {
            this.o = SystemClock.elapsedRealtime();
        }
        this.q = z;
    }

    public boolean a(int i2, int i3, @Nullable int[] iArr, @Nullable int[] iArr2, int i4) {
        return this.j.a(i2, i3, iArr, iArr2, i4);
    }

    public void b(int i2) {
        this.j.c(i2);
    }

    public void c(int i2) {
        this.f = i2;
    }

    public int getScrollingProgress() {
        return this.f;
    }

    public boolean isNestedScrollingEnabled() {
        return this.j.b();
    }

    /* access modifiers changed from: protected */
    @RequiresApi(api = 21)
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f8874d = findViewById(this.f8873c);
        View view = this.f8874d;
        if (view != null) {
            view.setNestedScrollingEnabled(true);
            return;
        }
        throw new IllegalArgumentException("The scrollableView attribute is required and must refer to a valid child.");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
        a();
    }

    public void onNestedPreScroll(View view, int i2, int i3, int[] iArr) {
        onNestedPreScroll(view, i2, i3, iArr, 0);
    }

    public void onNestedPreScroll(@NonNull View view, int i2, int i3, @NonNull int[] iArr, int i4) {
        if (i4 != 0) {
            if (!this.k) {
                this.p = SystemClock.elapsedRealtime();
            }
            this.k = true;
        } else {
            this.l = true;
        }
        int[] iArr2 = this.e;
        if (i3 > 0) {
            int max = Math.max(this.g, Math.min(this.h, this.f - i3));
            int i5 = this.f - max;
            this.f = max;
            a();
            iArr[0] = iArr[0] + 0;
            iArr[1] = iArr[1] + i5;
        }
        if (a(i2 - iArr[0], i3 - iArr[1], iArr2, (int[]) null, i4)) {
            iArr[0] = iArr[0] + iArr2[0];
            iArr[1] = iArr[1] + iArr2[1];
        }
    }

    public void onNestedScroll(View view, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i2, i3, i4, i5, 0);
    }

    public void onNestedScroll(@NonNull View view, int i2, int i3, int i4, int i5, int i6) {
        onNestedScroll(view, i2, i3, i4, i5, 0, this.f8871a);
    }

    public void onNestedScroll(@NonNull View view, int i2, int i3, int i4, int i5, int i6, @NonNull int[] iArr) {
        a(i2, i3, i4, i5, this.f8872b, i6, iArr);
        int i7 = i5 - iArr[1];
        if (i5 < 0 && i7 != 0) {
            int i8 = this.f - i7;
            boolean z = i6 == 0;
            boolean z2 = i8 > this.g;
            int max = Math.max(this.g, Math.min(z || !this.s || (this.s && !this.r && i6 == 1 && !z2) || (this.s && i6 == 1 && this.r && ((!this.q && i8 < 0) || (this.q && (this.o > this.p ? 1 : (this.o == this.p ? 0 : -1)) <= 0))) ? this.h : this.s && !this.r && i6 == 1 && z2 && this.f == this.g ? this.g : 0, i8));
            int i9 = this.f - max;
            this.f = max;
            a();
            iArr[0] = iArr[0] + 0;
            iArr[1] = iArr[1] + i9;
        }
    }

    public void onNestedScrollAccepted(View view, View view2, int i2) {
        this.i.a(view, view2, i2);
        startNestedScroll(i2 & 2);
    }

    public void onNestedScrollAccepted(@NonNull View view, @NonNull View view2, int i2, int i3) {
        onNestedScrollAccepted(view, view2, i2);
        this.m = i3 != 0;
    }

    public boolean onStartNestedScroll(View view, View view2, int i2) {
        boolean z = (i2 & 2) != 0;
        if (!this.j.b(i2)) {
            return isEnabled() && z;
        }
        return true;
    }

    public boolean onStartNestedScroll(@NonNull View view, @NonNull View view2, int i2, int i3) {
        d(i3);
        return this.j.a(i2, i3) || onStartNestedScroll(view, view, i2);
    }

    public void onStopNestedScroll(@NonNull View view, int i2) {
        this.i.a(view, i2);
        e(i2);
        b(i2);
        if (this.l) {
            this.l = false;
            if (this.k || this.m) {
                return;
            }
        } else if (this.k) {
            this.k = false;
        } else {
            return;
        }
        f(i2);
    }

    public void setNestedScrollingEnabled(boolean z) {
        this.j.a(z);
    }

    public boolean startNestedScroll(int i2) {
        return this.j.b(i2);
    }

    public void stopNestedScroll() {
        this.j.c();
    }
}
