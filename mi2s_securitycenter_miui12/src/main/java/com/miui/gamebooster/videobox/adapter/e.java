package com.miui.gamebooster.videobox.adapter;

import android.view.View;
import android.widget.AdapterView;
import com.miui.gamebooster.n.d.g;
import com.miui.gamebooster.videobox.adapter.f;

class e implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f5148a;

    e(f fVar) {
        this.f5148a = fVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView.getAdapter() != null && (adapterView.getAdapter() instanceof f.b)) {
            f.b bVar = (f.b) adapterView.getAdapter();
            g item = bVar.getItem(i);
            if (!(item == null || !item.b() || this.f5148a.f5150b == null)) {
                this.f5148a.f5150b.a(item, view);
            }
            bVar.notifyDataSetChanged();
        }
    }
}
