package com.miui.superpower.statusbar.button;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import com.miui.securitycenter.R;

public abstract class a extends ImageView implements View.OnClickListener, Checkable {

    /* renamed from: a  reason: collision with root package name */
    private boolean f8159a;

    /* renamed from: b  reason: collision with root package name */
    public Context f8160b;

    /* renamed from: c  reason: collision with root package name */
    public Drawable f8161c;

    /* renamed from: d  reason: collision with root package name */
    public Drawable f8162d;
    public Drawable e;
    public Drawable f;
    public Handler g;

    public a(Context context) {
        this(context, (AttributeSet) null);
    }

    public a(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public a(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f8159a = false;
        this.g = new Handler();
        this.f8160b = context;
        setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        d();
        c();
        setOnClickListener(this);
    }

    private void c() {
        this.f8161c = getResources().getDrawable(R.drawable.launcher_quick_button_background_checked);
        this.f8162d = getResources().getDrawable(R.drawable.launcher_quick_button_background_unchecked);
        setBackground(this.f8162d);
    }

    private void d() {
        this.e = getEnableDrawable();
        this.f = getDisableDrawable();
        setImageDrawable(this.e);
    }

    private Drawable getDisableDrawable() {
        Drawable disableDrawableImpl = getDisableDrawableImpl();
        if (disableDrawableImpl != null && Build.VERSION.SDK_INT >= 21) {
            disableDrawableImpl.setTint(ContextCompat.getColor(this.f8160b, R.color.qs_tile_icon_disabled_color));
        }
        return disableDrawableImpl;
    }

    private Drawable getEnableDrawable() {
        Drawable enableDrawableImpl = getEnableDrawableImpl();
        if (Build.VERSION.SDK_INT >= 21) {
            enableDrawableImpl.setTint(ContextCompat.getColor(this.f8160b, R.color.qs_tile_icon_enabled_color));
        }
        return enableDrawableImpl;
    }

    public void a() {
        if (isEnabled()) {
            setBackground(isChecked() ? this.f8161c : this.f8162d);
        }
    }

    public abstract void b();

    @Nullable
    public Drawable getDisableDrawableImpl() {
        return null;
    }

    public abstract Drawable getEnableDrawableImpl();

    public boolean isChecked() {
        return this.f8159a;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        b();
    }

    public void setChecked(boolean z) {
        if (this.f8159a != z) {
            this.f8159a = z;
            a();
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (z) {
            setImageDrawable(this.e);
            b();
            return;
        }
        setImageDrawable(this.f);
        setBackground(this.f8162d);
    }

    public void toggle() {
        setChecked(!this.f8159a);
    }
}
