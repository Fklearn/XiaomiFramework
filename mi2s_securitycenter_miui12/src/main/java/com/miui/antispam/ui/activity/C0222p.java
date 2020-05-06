package com.miui.antispam.ui.activity;

import android.widget.Toast;
import androidx.preference.Preference;
import com.miui.antispam.ui.activity.BackSoundActivity;
import com.miui.securitycenter.R;
import miui.telephony.TelephonyManager;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioSetPreferenceCategory;

/* renamed from: com.miui.antispam.ui.activity.p  reason: case insensitive filesystem */
class C0222p implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String[] f2607a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ BackSoundActivity.a f2608b;

    C0222p(BackSoundActivity.a aVar, String[] strArr) {
        this.f2608b = aVar;
        this.f2607a = strArr;
    }

    public boolean onPreferenceClick(Preference preference) {
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
        RadioSetPreferenceCategory radioSetPreferenceCategory = (RadioSetPreferenceCategory) this.f2608b.f2517c.a(this.f2608b.f());
        if ((this.f2608b.h == -1 ? TelephonyManager.getDefault().getSubscriberId() : TelephonyManager.getDefault().getSubscriberIdForSlot(this.f2608b.h)) == null) {
            Toast.makeText(this.f2608b.e, R.string.sim_not_ready_toast, 0).show();
            radioButtonPreference.setChecked(false);
            radioButtonPreference = (RadioButtonPreference) radioSetPreferenceCategory.a(0);
        } else {
            int i = 0;
            while (i < this.f2607a.length && !radioButtonPreference.getTitle().equals(this.f2607a[i])) {
                i++;
            }
            BackSoundActivity.a aVar = this.f2608b;
            String[] strArr = this.f2607a;
            aVar.a(i, strArr[i], strArr.length, radioButtonPreference);
            ((RadioButtonPreference) radioSetPreferenceCategory.a(0)).setChecked(false);
        }
        radioButtonPreference.setChecked(true);
        return true;
    }
}
