package com.miui.applicationlock.widget;

import android.view.SurfaceHolder;

class q implements SurfaceHolder.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3450a;

    q(r rVar) {
        this.f3450a = rVar;
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        int unused = this.f3450a.k = i2;
        int unused2 = this.f3450a.l = i3;
        boolean z = true;
        boolean z2 = this.f3450a.e == 3;
        if (!(this.f3450a.i == i2 && this.f3450a.j == i3)) {
            z = false;
        }
        if (this.f3450a.g != null && z2 && z) {
            if (this.f3450a.s != 0) {
                r rVar = this.f3450a;
                rVar.seekTo(rVar.s);
            }
            this.f3450a.start();
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        SurfaceHolder unused = this.f3450a.f = surfaceHolder;
        this.f3450a.e();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        SurfaceHolder unused = this.f3450a.f = null;
        if (this.f3450a.m != null) {
            this.f3450a.m.hide();
        }
        this.f3450a.a(true);
    }
}
