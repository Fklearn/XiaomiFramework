package com.miui.gamebooster.globalgame.view;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class b extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f4426a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f4427b;

    /* renamed from: c  reason: collision with root package name */
    private int f4428c = 0;

    /* renamed from: d  reason: collision with root package name */
    private int f4429d = 0;

    public b(Context context, int i, int i2, int i3, int i4) {
        super(context);
        this.f4428c = i2;
        this.f4429d = i3;
        setTag(Integer.valueOf(i));
        setPadding(i4, i4, i4, i4);
        c();
    }

    private void c() {
        int i = this.f4428c;
        int i2 = -2;
        if (i == 0) {
            i = -2;
        }
        int i3 = this.f4429d;
        if (i3 != 0) {
            i2 = i3;
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(i, i2);
        this.f4426a = new ImageView(getContext());
        this.f4426a.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(this.f4426a, layoutParams);
        this.f4427b = new ImageView(getContext());
        this.f4427b.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(this.f4427b, layoutParams);
        a();
    }

    public void a() {
        this.f4426a.setImageLevel(0);
        this.f4427b.setImageLevel(10000);
    }

    public void b() {
        this.f4426a.setImageLevel(10000);
        this.f4427b.setImageLevel(0);
    }

    public void setEmptyDrawable(Drawable drawable) {
        if (drawable.getConstantState() != null) {
            this.f4427b.setImageDrawable(new ClipDrawable(drawable.getConstantState().newDrawable(), 8388613, 1));
        }
    }

    public void setFilledDrawable(Drawable drawable) {
        if (drawable.getConstantState() != null) {
            this.f4426a.setImageDrawable(new ClipDrawable(drawable.getConstantState().newDrawable(), 8388611, 1));
        }
    }

    public void setPartialFilled(float f) {
        int i = (int) ((f % 1.0f) * 10000.0f);
        if (i == 0) {
            i = 10000;
        }
        this.f4426a.setImageLevel(i);
        this.f4427b.setImageLevel(10000 - i);
    }

    public void setStarHeight(@IntRange(from = 0) int i) {
        this.f4429d = i;
        ViewGroup.LayoutParams layoutParams = this.f4426a.getLayoutParams();
        layoutParams.height = this.f4429d;
        this.f4426a.setLayoutParams(layoutParams);
        this.f4427b.setLayoutParams(layoutParams);
    }

    public void setStarWidth(@IntRange(from = 0) int i) {
        this.f4428c = i;
        ViewGroup.LayoutParams layoutParams = this.f4426a.getLayoutParams();
        layoutParams.width = this.f4428c;
        this.f4426a.setLayoutParams(layoutParams);
        this.f4427b.setLayoutParams(layoutParams);
    }
}
