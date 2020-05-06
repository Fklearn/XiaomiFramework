package com.miui.gamebooster.videobox.adapter;

import android.view.View;
import android.widget.AdapterView;
import com.miui.gamebooster.n.d.g;
import com.miui.gamebooster.videobox.adapter.f;

class d implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f5147a;

    d(f fVar) {
        this.f5147a = fVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Object adapter = adapterView.getAdapter();
        if (adapter instanceof f.a) {
            f.a aVar = (f.a) adapter;
            int a2 = (aVar.a() * 8) + i;
            if (a2 < this.f5147a.f5149a.size()) {
                g gVar = (g) this.f5147a.f5149a.get(a2);
                if (this.f5147a.f5150b != null) {
                    this.f5147a.f5150b.a(gVar, view);
                }
                aVar.notifyDataSetChanged();
            }
        }
    }
}
