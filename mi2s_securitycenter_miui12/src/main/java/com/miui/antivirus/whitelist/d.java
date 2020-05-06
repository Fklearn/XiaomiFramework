package com.miui.antivirus.whitelist;

import android.widget.CompoundButton;
import com.miui.antivirus.whitelist.WhiteListActivity;

class d implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f3032a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ WhiteListActivity.b f3033b;

    d(WhiteListActivity.b bVar, i iVar) {
        this.f3033b = bVar;
        this.f3032a = iVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.f3033b.f3017b.add(this.f3032a);
        } else {
            this.f3033b.f3017b.remove(this.f3032a);
        }
        this.f3032a.a(z);
        WhiteListActivity.this.f3014d.setEnabled(!this.f3033b.f3017b.isEmpty());
    }
}
