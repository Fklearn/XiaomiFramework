package com.miui.activityutil;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

final class j extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f2294a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CountDownLatch f2295b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ h f2296c;

    j(h hVar, List list, CountDownLatch countDownLatch) {
        this.f2296c = hVar;
        this.f2294a = list;
        this.f2295b = countDownLatch;
    }

    public final void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.device.action.FOUND".equals(intent.getAction())) {
            this.f2294a.add(e.b(((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")).getAddress()));
            if (this.f2294a.size() > 4) {
                this.f2295b.countDown();
            }
        }
    }
}
