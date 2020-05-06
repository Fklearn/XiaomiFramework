package com.miui.powercenter.deepsave;

import android.view.View;
import android.widget.AdapterView;
import com.miui.powercenter.deepsave.BatterySaveIdeaActivity;

class a implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatterySaveIdeaActivity f7000a;

    a(BatterySaveIdeaActivity batterySaveIdeaActivity) {
        this.f7000a = batterySaveIdeaActivity;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.f7000a.a(((BatterySaveIdeaActivity.b) view.getTag()).f6999c);
    }
}
