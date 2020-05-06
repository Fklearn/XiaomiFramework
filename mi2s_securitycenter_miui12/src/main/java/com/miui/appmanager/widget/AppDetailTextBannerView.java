package com.miui.appmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AppDetailTextBannerView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f3711a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3712b;

    public AppDetailTextBannerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailTextBannerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailTextBannerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_card_layout_text_right_banner, this, true);
        this.f3711a = (TextView) findViewById(R.id.tv_title);
        this.f3712b = (TextView) findViewById(R.id.tv_summary);
    }

    public void setSummary(int i) {
        this.f3712b.setText(i);
    }

    public void setSummary(String str) {
        this.f3712b.setText(str);
    }

    public void setTitle(int i) {
        this.f3711a.setText(i);
    }
}
