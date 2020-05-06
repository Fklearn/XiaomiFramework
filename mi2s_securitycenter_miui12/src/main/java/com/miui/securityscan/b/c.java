package com.miui.securityscan.b;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.miui.securityscan.L;
import java.util.List;
import java.util.Random;

class c extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7605a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ L f7606b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Context f7607c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ TextView f7608d;
    final /* synthetic */ TextView e;
    final /* synthetic */ d f;

    c(d dVar, List list, L l, Context context, TextView textView, TextView textView2) {
        this.f = dVar;
        this.f7605a = list;
        this.f7606b = l;
        this.f7607c = context;
        this.f7608d = textView;
        this.e = textView2;
    }

    public void run() {
        super.run();
        try {
            Random random = new Random();
            int i = 0;
            while (i < this.f7605a.size()) {
                if (!this.f.f7610b) {
                    this.f7606b.m.post(new b(this, (Integer) this.f7605a.get(i)));
                    Thread.sleep((((long) random.nextInt(3)) * 1000) + 1000);
                    i++;
                } else {
                    return;
                }
            }
        } catch (Exception e2) {
            Log.e("GroupFinishOptimizeCallBack", "thread interrupt:", e2);
        }
    }
}
