package com.miui.gamebooster.p;

import android.util.Log;
import com.miui.gamebooster.customview.AddedRelativeLayout;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f4715a;

    d(f fVar) {
        this.f4715a = fVar;
    }

    public void run() {
        try {
            if (this.f4715a.f != null && this.f4715a.f.a()) {
                this.f4715a.e.removeView(this.f4715a.f);
                this.f4715a.f.setAdded(false);
                AddedRelativeLayout unused = this.f4715a.f = null;
            }
        } catch (Exception e) {
            Log.e("GameToastWindowManager", "remove error", e);
        }
    }
}
