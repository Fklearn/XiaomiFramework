package androidx.appcompat.app;

import a.a.d.b;
import a.a.j;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class ActionBar {

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayOptions {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {
    }

    public static class a extends ViewGroup.MarginLayoutParams {

        /* renamed from: a  reason: collision with root package name */
        public int f230a;

        public a(int i, int i2) {
            super(i, i2);
            this.f230a = 0;
            this.f230a = 8388627;
        }

        public a(@NonNull Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.f230a = 0;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, j.ActionBarLayout);
            this.f230a = obtainStyledAttributes.getInt(j.ActionBarLayout_android_layout_gravity, 0);
            obtainStyledAttributes.recycle();
        }

        public a(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.f230a = 0;
        }

        public a(a aVar) {
            super(aVar);
            this.f230a = 0;
            this.f230a = aVar.f230a;
        }
    }

    public interface b {
        void onMenuVisibilityChanged(boolean z);
    }

    @Deprecated
    public static abstract class c {
        public abstract CharSequence a();

        public abstract View b();

        public abstract Drawable c();

        public abstract CharSequence d();

        public abstract void e();
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract a.a.d.b a(b.a aVar);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract void a(Configuration configuration);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract void a(CharSequence charSequence);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract boolean a(int i, KeyEvent keyEvent);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean a(KeyEvent keyEvent) {
        return false;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract void b(boolean z);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract void c(boolean z);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract void d(boolean z);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean e() {
        return false;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public abstract boolean f();

    public abstract int g();

    public abstract Context h();

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean i() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void j() {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean k() {
        return false;
    }
}
