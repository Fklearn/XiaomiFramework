package com.miui.securityscan;

import android.app.Activity;
import android.content.Context;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.cards.b;
import com.miui.securityscan.i.e;
import java.util.ArrayList;

class D extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7546a;

    D(L l) {
        this.f7546a = l;
    }

    public void run() {
        ArrayList<BaseCardModel> arrayList;
        Activity activity = this.f7546a.getActivity();
        if (this.f7546a.a(activity)) {
            long currentTimeMillis = System.currentTimeMillis() - L.f7557b;
            ArrayList<BaseCardModel> arrayList2 = L.f7558c;
            if (!e.a(currentTimeMillis, arrayList2)) {
                e.a(arrayList2);
                arrayList = new ArrayList<>(arrayList2);
            } else {
                arrayList = e.a((Context) activity);
                L.f7558c = arrayList;
            }
            if (arrayList != null) {
                ArrayList unused = this.f7546a.ya = new ArrayList();
                this.f7546a.ya.addAll(b.a());
                this.f7546a.ya.addAll(arrayList);
            } else {
                ArrayList unused2 = this.f7546a.ya = b.b();
            }
            synchronized (this.f7546a.la) {
                boolean unused3 = this.f7546a.Aa = true;
                if (this.f7546a.za) {
                    this.f7546a.m.sendEmptyMessage(108);
                }
            }
        }
    }
}
