package com.miui.appmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AMMainTopFunctionView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    public ImageView f3692a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3693b;

    public AMMainTopFunctionView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AMMainTopFunctionView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AMMainTopFunctionView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_main_top_function_layout, this, true);
        this.f3692a = (ImageView) findViewById(R.id.icon);
        this.f3693b = (TextView) findViewById(R.id.title);
    }

    public void setIcon(int i) {
        this.f3692a.setImageResource(i);
    }

    public void setTitle(int i) {
        this.f3693b.setText(i);
    }
}
