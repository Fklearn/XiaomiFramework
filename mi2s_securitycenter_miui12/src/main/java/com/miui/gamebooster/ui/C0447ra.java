package com.miui.gamebooster.ui;

import android.widget.Toast;
import com.miui.securitycenter.R;
import miui.app.Activity;

/* renamed from: com.miui.gamebooster.ui.ra  reason: case insensitive filesystem */
class C0447ra implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0449sa f5102a;

    C0447ra(C0449sa saVar) {
        this.f5102a = saVar;
    }

    public void run() {
        C0449sa saVar = this.f5102a;
        saVar.f5106c.a(saVar.f5105b, false);
        Activity activity = this.f5102a.f5106c;
        Toast.makeText(activity, activity.getString(R.string.gb_wonderful_video_save_suc), 0).show();
    }
}
