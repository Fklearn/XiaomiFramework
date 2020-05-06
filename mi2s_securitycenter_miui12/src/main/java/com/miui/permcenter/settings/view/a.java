package com.miui.permcenter.settings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;
import miui.widget.ArrowPopupWindow;

public class a extends ArrowPopupWindow {

    /* renamed from: a  reason: collision with root package name */
    private View f6586a;

    /* renamed from: b  reason: collision with root package name */
    private int f6587b;

    public a(Context context) {
        this(context, (AttributeSet) null);
    }

    public a(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public a(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View inflate = LayoutInflater.from(context).inflate(R.layout.pm_model_guide_permission_tips, (ViewGroup) null);
        inflate.measure(0, 0);
        setWidth(na.b() - context.getResources().getDimensionPixelSize(R.dimen.view_dimen_80));
        setHeight(inflate.getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void onPrepareWindow() {
        a.super.onPrepareWindow();
        this.f6587b = 5000;
        setFocusable(true);
        this.f6586a = getLayoutInflater().inflate(R.layout.pm_model_guide_permission_tips, (ViewGroup) null, false);
        setContentView(this.f6586a);
    }
}
