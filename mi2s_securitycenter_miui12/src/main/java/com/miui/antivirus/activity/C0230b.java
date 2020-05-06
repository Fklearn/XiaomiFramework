package com.miui.antivirus.activity;

import android.os.Message;
import com.miui.antivirus.activity.MainActivity;
import com.miui.antivirus.model.a;
import com.miui.antivirus.model.h;
import miui.util.Log;

/* renamed from: com.miui.antivirus.activity.b  reason: case insensitive filesystem */
class C0230b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2713a;

    C0230b(MainActivity mainActivity) {
        this.f2713a = mainActivity;
    }

    public void run() {
        a aVar = new a();
        int i = 0;
        while (true) {
            if (this.f2713a.q != MainActivity.h.SCANNING && this.f2713a.t.size() - 1 < i) {
                Message message = new Message();
                message.what = 1051;
                this.f2713a.w.sendMessage(message);
                return;
            } else if (!this.f2713a.f && !this.f2713a.g && !this.f2713a.isFinishing() && !this.f2713a.isDestroyed()) {
                if (this.f2713a.t.size() - 1 >= i) {
                    int i2 = i + 1;
                    aVar = (a) this.f2713a.t.get(i);
                    Message message2 = new Message();
                    message2.obj = aVar;
                    message2.what = aVar instanceof h ? 1049 : 1050;
                    this.f2713a.w.sendMessage(message2);
                    i = i2;
                }
                try {
                    if (a.C0039a.APP == aVar.a()) {
                        Thread.sleep(40);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Log.e("AntiVirusMainActivity", "InterruptedException when do animation :", e);
                }
            } else {
                return;
            }
        }
    }
}
