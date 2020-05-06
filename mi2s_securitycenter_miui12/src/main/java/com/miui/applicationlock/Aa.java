package com.miui.applicationlock;

import android.view.View;
import android.widget.AdapterView;
import com.miui.applicationlock.c.C0257a;
import com.miui.common.expandableview.PinnedHeaderListView;

class Aa extends PinnedHeaderListView.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MaskNotificationActivity f3098a;

    Aa(MaskNotificationActivity maskNotificationActivity) {
        this.f3098a = maskNotificationActivity;
    }

    public void a(AdapterView<?> adapterView, View view, int i, int i2, long j) {
        C0257a item = ((B) adapterView.getAdapter()).getItem(i, i2);
        if (item != null) {
            this.f3098a.e.a(item, !item.f());
        }
    }

    public void a(AdapterView<?> adapterView, View view, int i, long j) {
    }
}
