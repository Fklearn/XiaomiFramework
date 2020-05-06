package com.miui.appmanager.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.securitycenter.R;

public class AppDetailTitleView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    public ImageView f3713a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3714b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f3715c;

    /* renamed from: d  reason: collision with root package name */
    private View f3716d;

    public AppDetailTitleView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailTitleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailTitleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_card_layout_top, this, true);
        this.f3713a = (ImageView) findViewById(R.id.app_manager_details_appicon);
        this.f3714b = (TextView) findViewById(R.id.app_manager_details_applabel);
        this.f3715c = (TextView) findViewById(R.id.app_manager_details_appversion);
        this.f3716d = findViewById(R.id.am_top_layout);
        this.f3713a.setColorFilter(context.getResources().getColor(R.color.app_manager_image_bg_color));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setAppIcon(Drawable drawable) {
        this.f3713a.setImageDrawable(drawable);
    }

    public void setAppIcon(String str) {
        r.a(str, this.f3713a, r.f);
    }

    public void setAppLabel(String str) {
        this.f3714b.setText(str);
    }

    public void setAppVersion(String str) {
        this.f3715c.setText(str);
    }

    public void setBackaground(int i) {
        this.f3716d.setBackgroundColor(i);
    }

    public void setTextColor(int i) {
        this.f3714b.setTextColor(i);
        this.f3715c.setTextColor(i);
    }
}
