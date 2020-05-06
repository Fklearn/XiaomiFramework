package com.miui.antivirus.activity;

import android.content.DialogInterface;
import com.miui.antivirus.activity.MainActivity;
import com.miui.securitycenter.R;
import miui.os.Build;

/* renamed from: com.miui.antivirus.activity.g  reason: case insensitive filesystem */
class C0235g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2719a;

    C0235g(MainActivity mainActivity) {
        this.f2719a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (!this.f2719a.g) {
            if (!Build.IS_INTERNATIONAL_BUILD && this.f2719a.A != null) {
                this.f2719a.A.a(false);
                this.f2719a.A.b();
            }
            boolean unused = this.f2719a.f = true;
            boolean unused2 = this.f2719a.e = true;
            MainActivity.h unused3 = this.f2719a.q = MainActivity.h.NORMAL;
            this.f2719a.m.setHandleActionButtonEnabled(true);
            this.f2719a.m.setActionButtonText(this.f2719a.getString(R.string.btn_text_quick_scan));
            this.f2719a.m.setContentSummary(this.f2719a.getString(R.string.descx_quick_scan_cancel));
            this.f2719a.H();
            this.f2719a.r();
        }
    }
}
