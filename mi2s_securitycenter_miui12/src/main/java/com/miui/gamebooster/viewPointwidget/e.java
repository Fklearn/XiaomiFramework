package com.miui.gamebooster.viewPointwidget;

import android.view.View;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewPointCommentItem f5349a;

    e(ViewPointCommentItem viewPointCommentItem) {
        this.f5349a = viewPointCommentItem;
    }

    public void onClick(View view) {
        ViewPointCommentItem viewPointCommentItem = this.f5349a;
        viewPointCommentItem.a(view, viewPointCommentItem.f);
    }
}
