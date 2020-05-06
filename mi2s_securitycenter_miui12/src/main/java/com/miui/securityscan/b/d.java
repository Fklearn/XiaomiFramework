package com.miui.securityscan.b;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.miui.securityscan.L;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class d implements e {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7609a;

    /* renamed from: b  reason: collision with root package name */
    public volatile boolean f7610b = false;

    public d(L l) {
        this.f7609a = new WeakReference<>(l);
    }

    public void a() {
        Activity activity;
        Log.d("GroupFinishOptimizeCallBack", "GroupFinishOptimizeCallBack onMemoryOptimizeFinished");
        L l = (L) this.f7609a.get();
        if (l != null && (activity = l.getActivity()) != null) {
            Context applicationContext = activity.getApplicationContext();
            TextView textView = l.H;
            TextView textView2 = l.J;
            l.Ja.add(Integer.valueOf(l.l.j()));
            int f = 100 - l.l.f();
            int intValue = ((Integer) Collections.min(l.Ja)).intValue();
            if (intValue == 100 && l.L.getTextScore() < 100) {
                intValue = l.L.getTextScore();
            }
            int i = f - intValue;
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(intValue));
            if (i > 2) {
                arrayList.add(Integer.valueOf(intValue + (i / 2)));
            }
            Collections.sort(arrayList);
            new c(this, arrayList, l, applicationContext, textView, textView2).start();
        }
    }

    public void b() {
        Log.d("GroupFinishOptimizeCallBack", "GroupFinishOptimizeCallBack onSystemAppOptimizeFinished");
        L l = (L) this.f7609a.get();
        if (l != null) {
            this.f7610b = true;
            l.m.post(new a(this, l));
        }
    }
}
