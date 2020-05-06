package d.c.b;

import android.view.View;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.l;

public class b extends l {
    private ViewParent f;
    private ViewParent g;
    private final View h;
    private boolean i;
    private int[] j;

    private b(@NonNull View view) {
        super(view);
        this.h = view;
    }

    public static l a(View view) {
        return e() ? new b(view) : new l(view);
    }

    private void b(int i2, ViewParent viewParent) {
        if (i2 == 0) {
            this.f = viewParent;
        } else if (i2 == 1) {
            this.g = viewParent;
        }
    }

    private boolean b(int i2, int i3, int i4, int i5, @Nullable int[] iArr, int i6, @Nullable int[] iArr2) {
        ViewParent d2;
        int i7;
        int i8;
        int[] iArr3;
        int[] iArr4 = iArr;
        if (!b() || (d2 = d(i6)) == null) {
            return false;
        }
        if (i2 == 0 && i3 == 0 && i4 == 0 && i5 == 0) {
            if (iArr4 != null) {
                iArr4[0] = 0;
                iArr4[1] = 0;
            }
            return false;
        }
        if (iArr4 != null) {
            this.h.getLocationInWindow(iArr4);
            i8 = iArr4[0];
            i7 = iArr4[1];
        } else {
            i8 = 0;
            i7 = 0;
        }
        if (iArr2 == null) {
            int[] d3 = d();
            d3[0] = 0;
            d3[1] = 0;
            iArr3 = d3;
        } else {
            iArr3 = iArr2;
        }
        c.a(d2, this.h, i2, i3, i4, i5, i6, iArr3);
        if (iArr4 != null) {
            this.h.getLocationInWindow(iArr4);
            iArr4[0] = iArr4[0] - i8;
            iArr4[1] = iArr4[1] - i7;
        }
        return true;
    }

    private ViewParent d(int i2) {
        if (i2 == 0) {
            return this.f;
        }
        if (i2 != 1) {
            return null;
        }
        return this.g;
    }

    private int[] d() {
        if (this.j == null) {
            this.j = new int[2];
        }
        return this.j;
    }

    private static boolean e() {
        try {
            Class.forName("miui.core.view.NestedScrollingParent3");
            return true;
        } catch (ClassNotFoundException unused) {
            return false;
        }
    }

    public void a(int i2, int i3, int i4, int i5, @Nullable int[] iArr, int i6, @Nullable int[] iArr2) {
        b(i2, i3, i4, i5, iArr, i6, iArr2);
    }

    public void a(boolean z) {
        if (this.i) {
            ViewCompat.w(this.h);
        }
        this.i = z;
    }

    public boolean a() {
        return a(0);
    }

    public boolean a(float f2, float f3) {
        ViewParent d2;
        if (!b() || (d2 = d(0)) == null) {
            return false;
        }
        return c.a(d2, this.h, f2, f3);
    }

    public boolean a(float f2, float f3, boolean z) {
        ViewParent d2;
        if (!b() || (d2 = d(0)) == null) {
            return false;
        }
        return c.a(d2, this.h, f2, f3, z);
    }

    public boolean a(int i2) {
        return d(i2) != null;
    }

    public boolean a(int i2, int i3) {
        if (a(i3)) {
            return true;
        }
        if (!b()) {
            return false;
        }
        View view = this.h;
        for (ViewParent parent = this.h.getParent(); parent != null; parent = parent.getParent()) {
            if (c.b(parent, view, this.h, i2, i3)) {
                b(i3, parent);
                c.a(parent, view, this.h, i2, i3);
                return true;
            }
            if (parent instanceof View) {
                view = (View) parent;
            }
        }
        return false;
    }

    public boolean a(int i2, int i3, int i4, int i5, @Nullable int[] iArr) {
        return b(i2, i3, i4, i5, iArr, 0, (int[]) null);
    }

    public boolean a(int i2, int i3, @Nullable int[] iArr, @Nullable int[] iArr2) {
        return a(i2, i3, iArr, iArr2, 0);
    }

    public boolean a(int i2, int i3, @Nullable int[] iArr, @Nullable int[] iArr2, int i4) {
        ViewParent d2;
        int i5;
        int i6;
        if (!b() || (d2 = d(i4)) == null) {
            return false;
        }
        if (i2 == 0 && i3 == 0) {
            if (iArr2 != null) {
                iArr2[0] = 0;
                iArr2[1] = 0;
            }
            return false;
        }
        if (iArr2 != null) {
            this.h.getLocationInWindow(iArr2);
            i6 = iArr2[0];
            i5 = iArr2[1];
        } else {
            i6 = 0;
            i5 = 0;
        }
        if (iArr == null) {
            iArr = d();
        }
        iArr[0] = 0;
        iArr[1] = 0;
        c.a(d2, this.h, i2, i3, iArr, i4);
        if (iArr2 != null) {
            this.h.getLocationInWindow(iArr2);
            iArr2[0] = iArr2[0] - i6;
            iArr2[1] = iArr2[1] - i5;
        }
        return (iArr[0] == 0 && iArr[1] == 0) ? false : true;
    }

    public boolean b() {
        return this.i;
    }

    public boolean b(int i2) {
        return a(i2, 0);
    }

    public void c() {
        c(0);
    }

    public void c(int i2) {
        ViewParent d2 = d(i2);
        if (d2 != null) {
            c.a(d2, this.h, i2);
            b(i2, (ViewParent) null);
        }
    }
}
