package com.miui.powercenter.batteryhistory;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import miui.securitycenter.powercenter.BatteryHistoryHelper;
import miui.securitycenter.powercenter.HistoryItemWrapper;

/* renamed from: com.miui.powercenter.batteryhistory.s  reason: case insensitive filesystem */
public class C0514s {

    /* renamed from: a  reason: collision with root package name */
    private static C0514s f6921a;

    /* renamed from: com.miui.powercenter.batteryhistory.s$a */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public List<aa> f6922a;

        /* renamed from: b  reason: collision with root package name */
        public long f6923b;

        /* renamed from: c  reason: collision with root package name */
        public long f6924c;
    }

    private C0514s() {
    }

    private a a(boolean z) {
        a aVar = new a();
        aVar.f6922a = new ArrayList();
        Log.d("BatteryHistoryLoadMgr", "getHistoryInfo begin");
        try {
            BatteryHistoryHelper batteryHistoryHelper = new BatteryHistoryHelper();
            batteryHistoryHelper.refreshHistory();
            HistoryItemWrapper historyItemWrapper = new HistoryItemWrapper();
            if (batteryHistoryHelper.startIterate()) {
                int i = -1;
                int i2 = -1;
                int i3 = -1;
                while (batteryHistoryHelper.getNextHistoryItem(historyItemWrapper)) {
                    if (historyItemWrapper.isDeltaData()) {
                        if (!z) {
                            int intValue = ((Integer) historyItemWrapper.getObjectValue("batteryLevel")).intValue();
                            int intValue2 = ((Integer) historyItemWrapper.getObjectValue("batteryPlugType")).intValue();
                            int intValue3 = ((Integer) historyItemWrapper.getObjectValue("batteryStatus")).intValue();
                            if (i != intValue || i2 != intValue2 || i3 != intValue3) {
                                i = intValue;
                                i2 = intValue2;
                                i3 = intValue3;
                            }
                        }
                        aVar.f6922a.add(new aa(historyItemWrapper));
                    }
                }
                batteryHistoryHelper.finishIterate();
            }
            aVar.f6923b = batteryHistoryHelper.getBatteryUsageRealtime();
            aVar.f6924c = batteryHistoryHelper.getScreenOnTime();
        } catch (Exception | NullPointerException | OutOfMemoryError e) {
            Log.e("BatteryHistoryLoadMgr", "getHistoryInfo", e);
        }
        Log.d("BatteryHistoryLoadMgr", "getHistoryInfo end");
        return aVar;
    }

    public static synchronized C0514s c() {
        C0514s sVar;
        synchronized (C0514s.class) {
            if (f6921a == null) {
                f6921a = new C0514s();
            }
            sVar = f6921a;
        }
        return sVar;
    }

    public a a() {
        return a(true);
    }

    public List<aa> b() {
        return a(false).f6922a;
    }
}
