package com.miui.gamebooster.customview.a;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.p.r;
import com.miui.securitycenter.Application;

public class e extends a implements View.OnTouchListener {
    private r f;
    private boolean g;
    private boolean h;
    private boolean i = C0388t.l();

    public e(r rVar, boolean z) {
        this.e = ViewConfiguration.get(Application.d()).getScaledTouchSlop();
        this.f = rVar;
        this.h = z;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        r rVar;
        int action = motionEvent.getAction();
        Log.i("VideoBoxTouchListener", "onTouch: mSupportGameTurbo=" + this.i + "\tisToolBoxAdded=" + this.g + "\tmToolBoxLayoutManager" + this.f);
        if (action != 0) {
            if (action == 1 || (action != 2 && action == 3)) {
                this.g = false;
            }
        } else if (!this.g && (rVar = this.f) != null) {
            this.g = true;
            rVar.a(this.h, false);
        }
        return true;
    }
}
