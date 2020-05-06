package com.miui.appmanager;

import android.view.View;
import com.miui.appmanager.widget.a;
import com.miui.appmanager.widget.d;
import com.miui.securitycenter.R;
import java.util.List;
import miui.cloud.CloudPushConstants;

class y implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3733a;

    y(AppManagerMainActivity appManagerMainActivity) {
        this.f3733a = appManagerMainActivity;
    }

    public void onClick(View view) {
        this.f3733a.l.notifyDataSetChanged();
        this.f3733a.p.a(view.findViewById(R.id.achnor_view));
        this.f3733a.p.a((List<a>) this.f3733a.ja);
        this.f3733a.p.a(this.f3733a.G);
        this.f3733a.p.a((d.b) new x(this));
        this.f3733a.p.b();
        com.miui.appmanager.a.a.b(CloudPushConstants.XML_ITEM);
    }
}
