package com.miui.gamebooster.model;

import android.view.View;
import android.widget.Toast;
import com.miui.securitycenter.R;

class A implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ B f4533a;

    A(B b2) {
        this.f4533a = b2;
    }

    public void run() {
        View view = this.f4533a.f4536b;
        if (view != null) {
            view.setVisibility(8);
        }
        Toast.makeText(this.f4533a.f4537c.f4539a, this.f4533a.f4537c.f4539a.getString(R.string.gb_wonderful_video_save_suc), 0).show();
    }
}
