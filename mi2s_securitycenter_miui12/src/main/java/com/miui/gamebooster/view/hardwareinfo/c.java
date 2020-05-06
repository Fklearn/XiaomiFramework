package com.miui.gamebooster.view.hardwareinfo;

import android.os.Message;
import android.util.Pair;
import com.miui.gamebooster.globalgame.util.b;
import com.miui.gamebooster.m.C0385p;
import com.miui.gamebooster.m.da;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f5284a;

    c(d dVar) {
        this.f5284a = dVar;
    }

    public void run() {
        if (!this.f5284a.b()) {
            int i = 0;
            try {
                i = Integer.valueOf(da.a()).intValue();
            } catch (Exception e) {
                b.b(e);
            }
            int a2 = this.f5284a.e ? -1 : C0385p.a();
            Message message = new Message();
            message.what = 1;
            message.obj = new Pair(Integer.valueOf(this.f5284a.a(i)), Integer.valueOf(this.f5284a.a(a2)));
            this.f5284a.f5287c.sendMessage(message);
        }
    }
}
