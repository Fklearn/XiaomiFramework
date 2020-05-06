package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import androidx.preference.Preference;
import com.miui.antispam.db.d;
import com.miui.antispam.ui.activity.CallInterceptSettingsActivity;

class B implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Preference f2513a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CallInterceptSettingsActivity.a f2514b;

    B(CallInterceptSettingsActivity.a aVar, Preference preference) {
        this.f2514b = aVar;
        this.f2513a = preference;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        d.b(this.f2514b.j, this.f2513a == this.f2514b.e ? "contact_call_mode" : this.f2513a == this.f2514b.f2525c ? "oversea_call_mode" : "stranger_call_mode", this.f2514b.k, 0);
    }
}
