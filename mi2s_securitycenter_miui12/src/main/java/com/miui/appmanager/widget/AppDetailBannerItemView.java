package com.miui.appmanager.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AppDetailBannerItemView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f3698a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3699b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f3700c;

    /* renamed from: d  reason: collision with root package name */
    private Resources f3701d;

    public AppDetailBannerItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailBannerItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailBannerItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_detail_banner_view, this, true);
        this.f3701d = context.getResources();
        this.f3698a = (ImageView) findViewById(R.id.am_arrow_right);
        this.f3699b = (TextView) findViewById(R.id.am_title);
        this.f3700c = (TextView) findViewById(R.id.am_summary);
    }

    public void setSummary(int i) {
        this.f3700c.setText(i);
    }

    public void setSummary(String str) {
        this.f3700c.setText(str);
    }

    public void setSummaryVisible(boolean z) {
        this.f3700c.setVisibility(z ? 0 : 8);
    }

    public void setTitle(int i) {
        this.f3699b.setText(i);
    }

    public void setViewEnable(boolean z) {
        TextView textView;
        int i;
        Resources resources;
        setEnabled(z);
        this.f3698a.setEnabled(z);
        if (z) {
            this.f3699b.setTextColor(this.f3701d.getColor(R.color.app_manager_detail_title_color));
            textView = this.f3700c;
            resources = this.f3701d;
            i = R.color.app_manager_list_summary_color;
        } else {
            this.f3699b.setTextColor(this.f3701d.getColor(R.color.title_enable_color));
            textView = this.f3700c;
            resources = this.f3701d;
            i = R.color.summary_enable_color;
        }
        textView.setTextColor(resources.getColor(i));
    }
}
