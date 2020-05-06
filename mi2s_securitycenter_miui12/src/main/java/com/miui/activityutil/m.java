package com.miui.activityutil;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

final class m extends PhoneStateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ TelephonyManager f2303a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ l f2304b;

    m(l lVar, TelephonyManager telephonyManager) {
        this.f2304b = lVar;
        this.f2303a = telephonyManager;
    }

    public final void onSignalStrengthsChanged(SignalStrength signalStrength) {
        try {
            Integer num = (Integer) signalStrength.getClass().getMethod("getDbm", new Class[0]).invoke(signalStrength, new Object[0]);
            if (TextUtils.isEmpty(this.f2304b.f2300a)) {
                this.f2304b.f2300a.append(num);
            }
            this.f2304b.f2301b.countDown();
            this.f2303a.listen(this, 0);
        } catch (Exception unused) {
        }
    }
}
