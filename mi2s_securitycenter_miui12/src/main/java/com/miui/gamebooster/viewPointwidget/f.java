package com.miui.gamebooster.viewPointwidget;

import android.view.View;

class f implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewPointCommentItem f5350a;

    f(ViewPointCommentItem viewPointCommentItem) {
        this.f5350a = viewPointCommentItem;
    }

    public void onClick(View view) {
        ViewPointCommentItem viewPointCommentItem = this.f5350a;
        viewPointCommentItem.a(view, viewPointCommentItem.f);
    }
}
