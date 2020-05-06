package com.miui.gamebooster.ui;

import android.widget.CompoundButton;

class P implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CompetitionDetailFragment f4955a;

    P(CompetitionDetailFragment competitionDetailFragment) {
        this.f4955a = competitionDetailFragment;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f4955a.c(z);
    }
}
