package com.miui.permcenter.permissions;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.miui.securitycenter.R;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f6271a;

    j(k kVar) {
        this.f6271a = kVar;
    }

    public void run() {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this.f6271a.f6273b.f6204b).inflate(R.layout.pm_app_permission_use_ok_preference, (ViewGroup) null);
        this.f6271a.f6272a.addView(relativeLayout);
        ((RelativeLayout.LayoutParams) relativeLayout.getLayoutParams()).addRule(12, -1);
        ((RelativeLayout.LayoutParams) relativeLayout.getLayoutParams()).addRule(14, -1);
        relativeLayout.findViewById(R.id.button_ok).setOnClickListener(new i(this));
    }
}
