package androidx.appcompat.widget;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;
import androidx.core.view.y;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
class Ga implements View.OnLongClickListener, View.OnHoverListener, View.OnAttachStateChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private static Ga f486a;

    /* renamed from: b  reason: collision with root package name */
    private static Ga f487b;

    /* renamed from: c  reason: collision with root package name */
    private final View f488c;

    /* renamed from: d  reason: collision with root package name */
    private final CharSequence f489d;
    private final int e;
    private final Runnable f = new Ea(this);
    private final Runnable g = new Fa(this);
    private int h;
    private int i;
    private Ha j;
    private boolean k;

    private Ga(View view, CharSequence charSequence) {
        this.f488c = view;
        this.f489d = charSequence;
        this.e = y.a(ViewConfiguration.get(this.f488c.getContext()));
        c();
        this.f488c.setOnLongClickListener(this);
        this.f488c.setOnHoverListener(this);
    }

    public static void a(View view, CharSequence charSequence) {
        Ga ga = f486a;
        if (ga != null && ga.f488c == view) {
            a((Ga) null);
        }
        if (TextUtils.isEmpty(charSequence)) {
            Ga ga2 = f487b;
            if (ga2 != null && ga2.f488c == view) {
                ga2.a();
            }
            view.setOnLongClickListener((View.OnLongClickListener) null);
            view.setLongClickable(false);
            view.setOnHoverListener((View.OnHoverListener) null);
            return;
        }
        new Ga(view, charSequence);
    }

    private static void a(Ga ga) {
        Ga ga2 = f486a;
        if (ga2 != null) {
            ga2.b();
        }
        f486a = ga;
        Ga ga3 = f486a;
        if (ga3 != null) {
            ga3.d();
        }
    }

    private boolean a(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (Math.abs(x - this.h) <= this.e && Math.abs(y - this.i) <= this.e) {
            return false;
        }
        this.h = x;
        this.i = y;
        return true;
    }

    private void b() {
        this.f488c.removeCallbacks(this.f);
    }

    private void c() {
        this.h = Integer.MAX_VALUE;
        this.i = Integer.MAX_VALUE;
    }

    private void d() {
        this.f488c.postDelayed(this.f, (long) ViewConfiguration.getLongPressTimeout());
    }

    /* access modifiers changed from: package-private */
    public void a() {
        if (f487b == this) {
            f487b = null;
            Ha ha = this.j;
            if (ha != null) {
                ha.a();
                this.j = null;
                c();
                this.f488c.removeOnAttachStateChangeListener(this);
            } else {
                Log.e("TooltipCompatHandler", "sActiveHandler.mPopup == null");
            }
        }
        if (f486a == this) {
            a((Ga) null);
        }
        this.f488c.removeCallbacks(this.g);
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        long j2;
        if (ViewCompat.r(this.f488c)) {
            a((Ga) null);
            Ga ga = f487b;
            if (ga != null) {
                ga.a();
            }
            f487b = this;
            this.k = z;
            this.j = new Ha(this.f488c.getContext());
            this.j.a(this.f488c, this.h, this.i, this.k, this.f489d);
            this.f488c.addOnAttachStateChangeListener(this);
            if (this.k) {
                j2 = 2500;
            } else {
                j2 = ((ViewCompat.n(this.f488c) & 1) == 1 ? 3000 : 15000) - ((long) ViewConfiguration.getLongPressTimeout());
            }
            this.f488c.removeCallbacks(this.g);
            this.f488c.postDelayed(this.g, j2);
        }
    }

    public boolean onHover(View view, MotionEvent motionEvent) {
        if (this.j != null && this.k) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.f488c.getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action != 7) {
            if (action == 10) {
                c();
                a();
            }
        } else if (this.f488c.isEnabled() && this.j == null && a(motionEvent)) {
            a(this);
        }
        return false;
    }

    public boolean onLongClick(View view) {
        this.h = view.getWidth() / 2;
        this.i = view.getHeight() / 2;
        a(true);
        return true;
    }

    public void onViewAttachedToWindow(View view) {
    }

    public void onViewDetachedFromWindow(View view) {
        a();
    }
}
