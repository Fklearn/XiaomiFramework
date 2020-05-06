package com.miui.gamebooster.videobox.settings;

import android.content.DialogInterface;
import com.miui.gamebooster.m.C0373d;

class g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoBoxSettingsFragment f5195a;

    g(VideoBoxSettingsFragment videoBoxSettingsFragment) {
        this.f5195a = videoBoxSettingsFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z = i != 0;
        this.f5195a.f.a(VideoBoxSettingsFragment.f5187a.get(z ? 1 : 0));
        f.d((int) z);
        C0373d.a.a(!z);
        dialogInterface.dismiss();
    }
}
