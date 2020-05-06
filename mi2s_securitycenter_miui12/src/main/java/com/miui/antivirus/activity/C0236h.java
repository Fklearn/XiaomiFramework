package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;
import com.miui.antivirus.activity.MainActivity;
import com.miui.securitycenter.R;

/* renamed from: com.miui.antivirus.activity.h  reason: case insensitive filesystem */
class C0236h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2720a;

    C0236h(MainActivity mainActivity) {
        this.f2720a = mainActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        boolean unused = this.f2720a.f = true;
        MainActivity.h unused2 = this.f2720a.q = MainActivity.h.NORMAL;
        this.f2720a.m.setActionButtonText(this.f2720a.getString(R.string.btn_text_quick_scan));
        this.f2720a.m.setContentSummary(this.f2720a.getString(R.string.descx_quick_scan_cancel));
        b.a.e("stop_quit");
        this.f2720a.H();
        this.f2720a.finish();
    }
}
