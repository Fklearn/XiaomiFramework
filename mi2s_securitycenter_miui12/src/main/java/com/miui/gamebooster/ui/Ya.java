package com.miui.gamebooster.ui;

import android.view.View;

class Ya implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListFragment f5037a;

    Ya(WhiteListFragment whiteListFragment) {
        this.f5037a = whiteListFragment;
    }

    public void onClick(View view) {
        if (view == this.f5037a.f5027d) {
            WhiteListFragment whiteListFragment = this.f5037a;
            whiteListFragment.startSearchMode(whiteListFragment.q);
        }
    }
}
