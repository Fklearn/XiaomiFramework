package com.miui.common.expandableview;

import com.miui.common.expandableview.PinnedHeaderListView;

class b implements PinnedHeaderListView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WrapPinnedHeaderListView f3829a;

    b(WrapPinnedHeaderListView wrapPinnedHeaderListView) {
        this.f3829a = wrapPinnedHeaderListView;
    }

    public void a(String str, int i, boolean z) {
        this.f3829a.f3828b.getLayoutParams().height = i;
        this.f3829a.setPlaceContentDescription(str);
        this.f3829a.setPlaceViewVisibility(z);
    }
}
