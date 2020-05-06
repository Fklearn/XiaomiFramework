package com.miui.activityutil;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.text.TextUtils;
import java.util.concurrent.CountDownLatch;

final class k implements SensorEventListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StringBuilder f2297a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CountDownLatch f2298b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ h f2299c;

    k(h hVar, StringBuilder sb, CountDownLatch countDownLatch) {
        this.f2299c = hVar;
        this.f2297a = sb;
        this.f2298b = countDownLatch;
    }

    public final void onAccuracyChanged(Sensor sensor, int i) {
    }

    public final void onSensorChanged(SensorEvent sensorEvent) {
        float f = sensorEvent.values[0];
        if (TextUtils.isEmpty(this.f2297a)) {
            this.f2297a.append(f);
        }
        this.f2298b.countDown();
    }
}
