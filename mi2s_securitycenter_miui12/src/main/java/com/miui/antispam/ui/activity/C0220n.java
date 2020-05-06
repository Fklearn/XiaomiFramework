package com.miui.antispam.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import b.b.a.e.n;
import com.miui.antispam.ui.activity.BackSoundActivity;
import miui.app.Activity;
import miui.telephony.TelephonyManager;
import miuix.preference.RadioButtonPreference;

/* renamed from: com.miui.antispam.ui.activity.n  reason: case insensitive filesystem */
class C0220n implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2602a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RadioButtonPreference f2603b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ BackSoundActivity.a f2604c;

    C0220n(BackSoundActivity.a aVar, int i, RadioButtonPreference radioButtonPreference) {
        this.f2604c = aVar;
        this.f2602a = i;
        this.f2603b = radioButtonPreference;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2;
        Activity activity;
        String str;
        String str2 = null;
        BackSoundActivity$BackSoundFragment$2$1 backSoundActivity$BackSoundFragment$2$1 = new BackSoundActivity$BackSoundFragment$2$1(this, (Handler) null);
        if (TelephonyManager.getDefault().getPhoneTypeForSlot(this.f2604c.h) == 2) {
            this.f2604c.a(this.f2602a);
            int i3 = this.f2602a;
            if (i3 == 0) {
                activity = this.f2604c.e;
                i2 = this.f2604c.h;
                str = "*730";
            } else if (i3 == 1) {
                activity = this.f2604c.e;
                i2 = this.f2604c.h;
                str = "*9013800000000";
            } else if (i3 == 2) {
                activity = this.f2604c.e;
                i2 = this.f2604c.h;
                str = "*9013810538911";
            } else if (i3 == 3) {
                activity = this.f2604c.e;
                i2 = this.f2604c.h;
                str = "*9013701110216";
            } else {
                return;
            }
            n.a((Context) activity, str, i2);
            return;
        }
        int i4 = this.f2602a;
        String str3 = i4 != 2 ? i4 != 3 ? "13800000000" : "18710276054" : "13810538911";
        Log.d("TelephonyDebugTool", "setCallForwardingOption " + this.f2604c.h);
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        int i5 = 0;
        int e = this.f2604c.h == -1 ? 0 : this.f2604c.h;
        if (this.f2602a != 0) {
            i5 = 3;
        }
        if (this.f2602a != 0) {
            str2 = str3;
        }
        telephonyManager.setCallForwardingOption(e, i5, 1, str2, backSoundActivity$BackSoundFragment$2$1);
    }
}
