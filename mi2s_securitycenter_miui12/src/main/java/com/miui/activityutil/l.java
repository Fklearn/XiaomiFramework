package com.miui.activityutil;

import android.telephony.TelephonyManager;
import java.util.concurrent.CountDownLatch;

final class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StringBuilder f2300a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CountDownLatch f2301b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ h f2302c;

    l(h hVar, StringBuilder sb, CountDownLatch countDownLatch) {
        this.f2302c = hVar;
        this.f2300a = sb;
        this.f2301b = countDownLatch;
    }

    public final void run() {
        TelephonyManager telephonyManager = (TelephonyManager) this.f2302c.g.getSystemService("phone");
        telephonyManager.listen(new m(this, telephonyManager), 256);
    }
}
