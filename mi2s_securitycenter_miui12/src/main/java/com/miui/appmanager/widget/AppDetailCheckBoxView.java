package com.miui.appmanager.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class AppDetailCheckBoxView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f3702a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3703b;

    /* renamed from: c  reason: collision with root package name */
    private SlidingButton f3704c;

    /* renamed from: d  reason: collision with root package name */
    private View f3705d;
    private Resources e;

    public AppDetailCheckBoxView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailCheckBoxView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailCheckBoxView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f3705d = LayoutInflater.from(context).inflate(R.layout.app_manager_details_layout_checkbox, this, true);
        this.e = context.getResources();
        this.f3702a = (TextView) findViewById(R.id.tv_title);
        this.f3703b = (TextView) findViewById(R.id.tv_summary);
        this.f3704c = findViewById(R.id.am_switch);
    }

    public boolean a() {
        return this.f3704c.isChecked();
    }

    public void setSlideButtonChecked(boolean z) {
        this.f3704c.setChecked(z);
    }

    public void setSlideButtonOnCheckedListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f3704c.setOnPerformCheckedChangeListener(onCheckedChangeListener);
    }

    public void setSummary(int i) {
        this.f3703b.setText(i);
    }

    public void setSummaryVisible(boolean z) {
        Resources resources;
        int i;
        this.f3703b.setVisibility(z ? 0 : 8);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f3705d.getLayoutParams();
        if (z) {
            resources = this.e;
            i = R.dimen.am_details_item_height_2;
        } else {
            resources = this.e;
            i = R.dimen.am_details_item_height;
        }
        layoutParams.height = resources.getDimensionPixelSize(i);
        this.f3705d.setLayoutParams(layoutParams);
    }

    public void setTitle(int i) {
        this.f3702a.setText(i);
    }

    public void setViewEnable(boolean z) {
        TextView textView;
        int i;
        Resources resources;
        setEnabled(z);
        this.f3704c.setEnabled(z);
        if (z) {
            textView = this.f3702a;
            resources = this.e;
            i = R.color.app_manager_detail_title_color;
        } else {
            textView = this.f3702a;
            resources = this.e;
            i = R.color.title_enable_color;
        }
        textView.setTextColor(resources.getColor(i));
    }
}
