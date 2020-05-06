package com.miui.appmanager.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AppDetailRightSummaryPointView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f3707a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3708b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f3709c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f3710d;
    private Resources e;

    public AppDetailRightSummaryPointView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailRightSummaryPointView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailRightSummaryPointView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_layout_right_summary_point, this, true);
        this.e = context.getResources();
        this.f3707a = (ImageView) findViewById(R.id.am_arrow_right);
        this.f3708b = (TextView) findViewById(R.id.tv_title);
        this.f3710d = (ImageView) findViewById(R.id.privacy_read_point);
        this.f3709c = (TextView) findViewById(R.id.tv_summary);
    }

    public void setPrivacyEnable(boolean z) {
        this.f3710d.setVisibility(z ? 0 : 8);
    }

    public void setSummary(int i) {
        if (i == 0) {
            this.f3709c.setVisibility(8);
            return;
        }
        this.f3709c.setVisibility(0);
        this.f3709c.setText(i);
    }

    public void setTitle(int i) {
        this.f3708b.setText(i);
    }

    public void setViewEnable(boolean z) {
        TextView textView;
        int i;
        Resources resources;
        setEnabled(z);
        this.f3707a.setEnabled(z);
        if (z) {
            textView = this.f3708b;
            resources = this.e;
            i = R.color.app_manager_detail_title_color;
        } else {
            textView = this.f3708b;
            resources = this.e;
            i = R.color.title_enable_color;
        }
        textView.setTextColor(resources.getColor(i));
    }
}
