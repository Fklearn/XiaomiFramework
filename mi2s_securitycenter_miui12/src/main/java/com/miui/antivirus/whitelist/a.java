package com.miui.antivirus.whitelist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

class a implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f3028a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ WhiteListActivity f3029b;

    a(WhiteListActivity whiteListActivity, Map map) {
        this.f3029b = whiteListActivity;
        this.f3028a = map;
    }

    public String getGroupName(int i) {
        return (String) this.f3028a.get(Integer.valueOf(i));
    }

    public View getGroupView(int i) {
        View inflate = this.f3029b.getLayoutInflater().inflate(R.layout.v_white_list_header_view, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText((String) this.f3028a.get(Integer.valueOf(i)));
        return inflate;
    }
}
