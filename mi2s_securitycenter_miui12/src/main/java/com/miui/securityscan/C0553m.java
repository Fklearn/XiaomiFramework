package com.miui.securityscan;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.PathInterpolator;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.w;
import com.miui.securityscan.scanner.C0557d;

/* renamed from: com.miui.securityscan.m  reason: case insensitive filesystem */
class C0553m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7760a;

    C0553m(L l) {
        this.f7760a = l;
    }

    public void run() {
        C0557d unused = this.f7760a.g = C0557d.NORMAL;
        if (this.f7760a.C != null) {
            this.f7760a.C.setVisibility(8);
        }
        this.f7760a.s.a();
        L l = this.f7760a;
        l.O = 0;
        l.a(l.O == 0, false);
        this.f7760a.B.setText(this.f7760a.getString(R.string.security_center_slogan));
        long unused2 = this.f7760a.Q = SystemClock.elapsedRealtime();
        long unused3 = this.f7760a.R = SystemClock.elapsedRealtime();
        Activity activity = this.f7760a.getActivity();
        if (this.f7760a.a(activity)) {
            Context applicationContext = activity.getApplicationContext();
            L l2 = this.f7760a;
            w.a(applicationContext, l2.s, l2.oa, true);
            PathInterpolator pathInterpolator = new PathInterpolator(0.4f, 0.48f, 0.25f, 1.0f);
            w.b((View) this.f7760a.H, 400, (TimeInterpolator) pathInterpolator);
            w.a(this.f7760a.K, 400, 0.0f, (float) (-this.f7760a.ga), pathInterpolator);
            this.f7760a.I.setText(this.f7760a.getString(R.string.last_check_canceled));
            L l3 = this.f7760a;
            l3.J.setText(l3.getString(R.string.last_check_canceled));
            ((MainActivity) activity).a(true, true);
        }
    }
}
