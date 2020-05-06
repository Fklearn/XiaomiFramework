package com.miui.securityscan.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FuncGrid6ImageView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private String f7993a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f7994b;

    public FuncGrid6ImageView(Context context) {
        super(context);
    }

    public FuncGrid6ImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public String getAction() {
        return this.f7993a;
    }

    public void setAction(String str) {
        this.f7993a = str;
    }

    public void setAdShowing(boolean z) {
        this.f7994b = z;
    }
}
