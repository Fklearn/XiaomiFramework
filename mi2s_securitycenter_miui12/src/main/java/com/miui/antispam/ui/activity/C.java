package com.miui.antispam.ui.activity;

import android.content.Intent;
import androidx.preference.Preference;
import com.miui.antispam.ui.activity.CallInterceptSettingsActivity;
import com.miui.networkassistant.provider.ProviderConstant;
import miui.cloud.common.XSimChangeNotification;

class C implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f2519a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f2520b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f2521c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ CallInterceptSettingsActivity.a f2522d;

    C(CallInterceptSettingsActivity.a aVar, String str, boolean z, int i) {
        this.f2522d = aVar;
        this.f2519a = str;
        this.f2520b = z;
        this.f2521c = i;
    }

    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(this.f2522d.j, BackSoundActivity.class);
        intent.putExtra(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, this.f2519a);
        intent.putExtra(ProviderConstant.DataUsageStatusDetailedColumns.SIM_SLOT, this.f2520b ? this.f2521c : -1);
        this.f2522d.startActivity(intent);
        return false;
    }
}
