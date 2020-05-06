package androidx.appcompat.widget;

import a.a.a;
import a.a.j;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.ViewCompat;

/* renamed from: androidx.appcompat.widget.a  reason: case insensitive filesystem */
abstract class C0087a extends ViewGroup {

    /* renamed from: a  reason: collision with root package name */
    protected final C0010a f576a;

    /* renamed from: b  reason: collision with root package name */
    protected final Context f577b;

    /* renamed from: c  reason: collision with root package name */
    protected ActionMenuView f578c;

    /* renamed from: d  reason: collision with root package name */
    protected ActionMenuPresenter f579d;
    protected int e;
    protected D f;
    private boolean g;
    private boolean h;

    /* renamed from: androidx.appcompat.widget.a$a  reason: collision with other inner class name */
    protected class C0010a implements E {

        /* renamed from: a  reason: collision with root package name */
        private boolean f580a = false;

        /* renamed from: b  reason: collision with root package name */
        int f581b;

        protected C0010a() {
        }

        public C0010a a(D d2, int i) {
            C0087a.this.f = d2;
            this.f581b = i;
            return this;
        }

        public void onAnimationCancel(View view) {
            this.f580a = true;
        }

        public void onAnimationEnd(View view) {
            if (!this.f580a) {
                C0087a aVar = C0087a.this;
                aVar.f = null;
                C0087a.super.setVisibility(this.f581b);
            }
        }

        public void onAnimationStart(View view) {
            C0087a.super.setVisibility(0);
            this.f580a = false;
        }
    }

    C0087a(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    C0087a(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    C0087a(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2;
        this.f576a = new C0010a();
        TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(a.actionBarPopupTheme, typedValue, true) || (i2 = typedValue.resourceId) == 0) {
            this.f577b = context;
        } else {
            this.f577b = new ContextThemeWrapper(context, i2);
        }
    }

    protected static int a(int i, int i2, boolean z) {
        return z ? i - i2 : i + i2;
    }

    /* access modifiers changed from: protected */
    public int a(View view, int i, int i2, int i3) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), i2);
        return Math.max(0, (i - view.getMeasuredWidth()) - i3);
    }

    /* access modifiers changed from: protected */
    public int a(View view, int i, int i2, int i3, boolean z) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = i2 + ((i3 - measuredHeight) / 2);
        if (z) {
            view.layout(i - measuredWidth, i4, i, measuredHeight + i4);
        } else {
            view.layout(i, i4, i + measuredWidth, measuredHeight + i4);
        }
        return z ? -measuredWidth : measuredWidth;
    }

    public D a(int i, long j) {
        D d2 = this.f;
        if (d2 != null) {
            d2.a();
        }
        if (i == 0) {
            if (getVisibility() != 0) {
                setAlpha(0.0f);
            }
            D a2 = ViewCompat.a(this);
            a2.a(1.0f);
            a2.a(j);
            C0010a aVar = this.f576a;
            aVar.a(a2, i);
            a2.a((E) aVar);
            return a2;
        }
        D a3 = ViewCompat.a(this);
        a3.a(0.0f);
        a3.a(j);
        C0010a aVar2 = this.f576a;
        aVar2.a(a3, i);
        a3.a((E) aVar2);
        return a3;
    }

    public int getAnimatedVisibility() {
        return this.f != null ? this.f576a.f581b : getVisibility();
    }

    public int getContentHeight() {
        return this.e;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes((AttributeSet) null, j.ActionBar, a.actionBarStyle, 0);
        setContentHeight(obtainStyledAttributes.getLayoutDimension(j.ActionBar_height, 0));
        obtainStyledAttributes.recycle();
        ActionMenuPresenter actionMenuPresenter = this.f579d;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.a(configuration);
        }
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 9) {
            this.h = false;
        }
        if (!this.h) {
            boolean onHoverEvent = super.onHoverEvent(motionEvent);
            if (actionMasked == 9 && !onHoverEvent) {
                this.h = true;
            }
        }
        if (actionMasked == 10 || actionMasked == 3) {
            this.h = false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.g = false;
        }
        if (!this.g) {
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (actionMasked == 0 && !onTouchEvent) {
                this.g = true;
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.g = false;
        }
        return true;
    }

    public abstract void setContentHeight(int i);

    public void setVisibility(int i) {
        if (i != getVisibility()) {
            D d2 = this.f;
            if (d2 != null) {
                d2.a();
            }
            super.setVisibility(i);
        }
    }
}
