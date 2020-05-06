package com.miui.gamebooster.customview;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* renamed from: com.miui.gamebooster.customview.i  reason: case insensitive filesystem */
public class C0340i extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private GameAdImageView f4205a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f4206b;

    /* renamed from: c  reason: collision with root package name */
    private Button f4207c;

    /* renamed from: d  reason: collision with root package name */
    private LinearLayout f4208d;

    public LinearLayout getmButtonLayout() {
        return this.f4208d;
    }

    public Button getmButtonView() {
        return this.f4207c;
    }

    public ImageView getmImageView() {
        return this.f4205a;
    }

    public TextView getmTextView() {
        return this.f4206b;
    }

    public void setTabButton(int i) {
        this.f4207c.setText(i);
    }

    public void setTabIcon(int i) {
        this.f4205a.setBackgroundResource(i);
    }

    public void setTabName(int i) {
        this.f4206b.setText(i);
    }
}
