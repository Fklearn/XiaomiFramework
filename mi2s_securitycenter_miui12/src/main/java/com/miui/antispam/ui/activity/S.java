package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import androidx.preference.Preference;
import com.miui.antispam.db.d;
import com.miui.antispam.ui.activity.MsgInterceptSettingsActivity;
import miuix.preference.TextPreference;

class S implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f2571a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Preference f2572b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ MsgInterceptSettingsActivity.a f2573c;

    S(MsgInterceptSettingsActivity.a aVar, String str, Preference preference) {
        this.f2573c = aVar;
        this.f2571a = str;
        this.f2572b = preference;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        TextPreference a2;
        String str;
        d.b(this.f2573c.k, this.f2571a, this.f2573c.n, i);
        if (this.f2572b == this.f2573c.e) {
            a2 = this.f2573c.e;
            str = this.f2573c.l[i];
        } else if (this.f2572b == this.f2573c.f) {
            a2 = this.f2573c.f;
            str = this.f2573c.l[i];
        } else {
            a2 = this.f2573c.g;
            str = this.f2573c.m[i];
        }
        a2.a(str);
        dialogInterface.dismiss();
    }
}
