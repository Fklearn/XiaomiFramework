package com.miui.activityutil;

import android.net.wifi.ScanResult;
import java.util.Comparator;

final class i implements Comparator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f2293a;

    i(h hVar) {
        this.f2293a = hVar;
    }

    private static int a(ScanResult scanResult, ScanResult scanResult2) {
        return scanResult2.level - scanResult.level;
    }

    public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
        return ((ScanResult) obj2).level - ((ScanResult) obj).level;
    }
}
