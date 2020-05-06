package androidx.appcompat.view.menu;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.PopupWindow;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.menu.s;
import androidx.core.view.C0125c;
import androidx.core.view.ViewCompat;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class r implements l {

    /* renamed from: a  reason: collision with root package name */
    private final Context f410a;

    /* renamed from: b  reason: collision with root package name */
    private final j f411b;

    /* renamed from: c  reason: collision with root package name */
    private final boolean f412c;

    /* renamed from: d  reason: collision with root package name */
    private final int f413d;
    private final int e;
    private View f;
    private int g;
    private boolean h;
    private s.a i;
    private p j;
    private PopupWindow.OnDismissListener k;
    private final PopupWindow.OnDismissListener l;

    public r(@NonNull Context context, @NonNull j jVar, @NonNull View view, boolean z, @AttrRes int i2) {
        this(context, jVar, view, z, i2, 0);
    }

    public r(@NonNull Context context, @NonNull j jVar, @NonNull View view, boolean z, @AttrRes int i2, @StyleRes int i3) {
        this.g = 8388611;
        this.l = new q(this);
        this.f410a = context;
        this.f411b = jVar;
        this.f = view;
        this.f412c = z;
        this.f413d = i2;
        this.e = i3;
    }

    private void a(int i2, int i3, boolean z, boolean z2) {
        p b2 = b();
        b2.c(z2);
        if (z) {
            if ((C0125c.a(this.g, ViewCompat.j(this.f)) & 7) == 5) {
                i2 -= this.f.getWidth();
            }
            b2.b(i2);
            b2.c(i3);
            int i4 = (int) ((this.f410a.getResources().getDisplayMetrics().density * 48.0f) / 2.0f);
            b2.a(new Rect(i2 - i4, i3 - i4, i2 + i4, i3 + i4));
        }
        b2.b();
    }

    /* JADX WARNING: type inference failed for: r0v7, types: [androidx.appcompat.view.menu.p, androidx.appcompat.view.menu.s] */
    /* JADX WARNING: type inference failed for: r7v1, types: [androidx.appcompat.view.menu.y] */
    /* JADX WARNING: type inference failed for: r1v13, types: [androidx.appcompat.view.menu.CascadingMenuPopup] */
    /* JADX WARNING: Multi-variable type inference failed */
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private androidx.appcompat.view.menu.p g() {
        /*
            r14 = this;
            android.content.Context r0 = r14.f410a
            java.lang.String r1 = "window"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.view.WindowManager r0 = (android.view.WindowManager) r0
            android.view.Display r0 = r0.getDefaultDisplay()
            android.graphics.Point r1 = new android.graphics.Point
            r1.<init>()
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 17
            if (r2 < r3) goto L_0x001d
            r0.getRealSize(r1)
            goto L_0x0020
        L_0x001d:
            r0.getSize(r1)
        L_0x0020:
            int r0 = r1.x
            int r1 = r1.y
            int r0 = java.lang.Math.min(r0, r1)
            android.content.Context r1 = r14.f410a
            android.content.res.Resources r1 = r1.getResources()
            int r2 = a.a.d.abc_cascading_menus_min_smallest_width
            int r1 = r1.getDimensionPixelSize(r2)
            if (r0 < r1) goto L_0x0038
            r0 = 1
            goto L_0x0039
        L_0x0038:
            r0 = 0
        L_0x0039:
            if (r0 == 0) goto L_0x004c
            androidx.appcompat.view.menu.CascadingMenuPopup r0 = new androidx.appcompat.view.menu.CascadingMenuPopup
            android.content.Context r2 = r14.f410a
            android.view.View r3 = r14.f
            int r4 = r14.f413d
            int r5 = r14.e
            boolean r6 = r14.f412c
            r1 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            goto L_0x005e
        L_0x004c:
            androidx.appcompat.view.menu.y r0 = new androidx.appcompat.view.menu.y
            android.content.Context r8 = r14.f410a
            androidx.appcompat.view.menu.j r9 = r14.f411b
            android.view.View r10 = r14.f
            int r11 = r14.f413d
            int r12 = r14.e
            boolean r13 = r14.f412c
            r7 = r0
            r7.<init>(r8, r9, r10, r11, r12, r13)
        L_0x005e:
            androidx.appcompat.view.menu.j r1 = r14.f411b
            r0.a((androidx.appcompat.view.menu.j) r1)
            android.widget.PopupWindow$OnDismissListener r1 = r14.l
            r0.a((android.widget.PopupWindow.OnDismissListener) r1)
            android.view.View r1 = r14.f
            r0.a((android.view.View) r1)
            androidx.appcompat.view.menu.s$a r1 = r14.i
            r0.a((androidx.appcompat.view.menu.s.a) r1)
            boolean r1 = r14.h
            r0.b((boolean) r1)
            int r1 = r14.g
            r0.a((int) r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.view.menu.r.g():androidx.appcompat.view.menu.p");
    }

    public void a() {
        if (c()) {
            this.j.dismiss();
        }
    }

    public void a(int i2) {
        this.g = i2;
    }

    public void a(@NonNull View view) {
        this.f = view;
    }

    public void a(@Nullable PopupWindow.OnDismissListener onDismissListener) {
        this.k = onDismissListener;
    }

    public void a(@Nullable s.a aVar) {
        this.i = aVar;
        p pVar = this.j;
        if (pVar != null) {
            pVar.a(aVar);
        }
    }

    public void a(boolean z) {
        this.h = z;
        p pVar = this.j;
        if (pVar != null) {
            pVar.b(z);
        }
    }

    public boolean a(int i2, int i3) {
        if (c()) {
            return true;
        }
        if (this.f == null) {
            return false;
        }
        a(i2, i3, true, true);
        return true;
    }

    @NonNull
    public p b() {
        if (this.j == null) {
            this.j = g();
        }
        return this.j;
    }

    public boolean c() {
        p pVar = this.j;
        return pVar != null && pVar.isShowing();
    }

    /* access modifiers changed from: protected */
    public void d() {
        this.j = null;
        PopupWindow.OnDismissListener onDismissListener = this.k;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public void e() {
        if (!f()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public boolean f() {
        if (c()) {
            return true;
        }
        if (this.f == null) {
            return false;
        }
        a(0, 0, false, false);
        return true;
    }
}
