package com.miui.appmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AppDetailListTitleView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f3706a;

    public AppDetailListTitleView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppDetailListTitleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDetailListTitleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.app_manager_card_layout_list_title, this, true);
        this.f3706a = (TextView) findViewById(R.id.tv_title);
    }

    public void setTitle(int i) {
        this.f3706a.setText(i);
    }
}
