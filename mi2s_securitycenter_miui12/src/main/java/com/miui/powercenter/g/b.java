package com.miui.powercenter.g;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xiaomi.stat.MiStat;

public class b implements a {

    /* renamed from: a  reason: collision with root package name */
    private final Context f7068a;

    public b(Context context) {
        this.f7068a = context;
    }

    private void a(int i) {
        try {
            Intent intent = new Intent("miui.intent.action.ACTIVITY_WIRELESS_CHG_WARNING");
            intent.addFlags(268435456);
            intent.putExtra("plugstatus", i);
            this.f7068a.startActivity(intent);
        } catch (Exception e) {
            Log.e("WirelessCharge", "show Wireless Charging Warning dialog error" + e);
        }
    }

    private void b(boolean z) {
        try {
            Object invoke = Class.forName("miui.util.IWirelessSwitch").getDeclaredMethod("getInstance", new Class[0]).invoke((Object) null, new Object[0]);
            invoke.getClass().getMethod("setWirelessChargingEnabled", new Class[]{Boolean.TYPE}).invoke(invoke, new Object[]{Boolean.valueOf(z)});
        } catch (Exception e) {
            Log.e("WirelessCharge", "changeWirelessReverseChargeStatus error " + e.toString());
        }
    }

    private boolean c() {
        Intent registerReceiver = this.f7068a.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver == null) {
            return false;
        }
        int intExtra = registerReceiver.getIntExtra("plugged", -1);
        int intExtra2 = registerReceiver.getIntExtra(MiStat.Param.LEVEL, -1);
        if (4 != intExtra && (intExtra > 0 || intExtra2 >= 30)) {
            return false;
        }
        a(intExtra);
        return true;
    }

    private void d() {
        try {
            Intent intent = new Intent("miui.intent.action.ACTIVITY_WIRELESS_CHG_CONFIRM");
            intent.addFlags(268435456);
            this.f7068a.startActivity(intent);
        } catch (Exception e) {
            Log.e("WirelessCharge", "show confirm dialog error" + e);
        }
    }

    public void a(boolean z) {
        if (!z) {
            b(false);
        } else if (!c()) {
            d();
        }
    }

    public boolean a() {
        try {
            Object invoke = Class.forName("miui.util.IWirelessSwitch").getDeclaredMethod("getInstance", new Class[0]).invoke((Object) null, new Object[0]);
            return ((Boolean) invoke.getClass().getMethod("isWirelessChargingSupported", new Class[0]).invoke(invoke, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("WirelessCharge", "isWirelessChargingSupported error" + e);
            return false;
        }
    }

    public boolean b() {
        try {
            Object invoke = Class.forName("miui.util.IWirelessSwitch").getDeclaredMethod("getInstance", new Class[0]).invoke((Object) null, new Object[0]);
            return ((Integer) invoke.getClass().getMethod("getWirelessChargingStatus", new Class[0]).invoke(invoke, new Object[0])).intValue() == 0;
        } catch (Exception e) {
            Log.e("WirelessCharge", "isWirelessChargingEnabled error" + e);
            return false;
        }
    }
}
