package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import com.xiaomi.stat.MiStat;

public class BatteryVariableUpdater extends NotifierVariableUpdater {
    public static final String USE_TAG = "Battery";
    private IndexedVariable mBatteryLevel = new IndexedVariable(VariableNames.BATTERY_LEVEL, getRoot().getContext().mVariables, true);
    private int mLevel;

    public BatteryVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        super(variableUpdaterManager, "android.intent.action.BATTERY_CHANGED");
    }

    public void onNotify(Context context, Intent intent, Object obj) {
        int intExtra;
        if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED") && (intExtra = intent.getIntExtra(MiStat.Param.LEVEL, -1)) != -1 && this.mLevel != intExtra) {
            this.mBatteryLevel.set(intExtra >= 100 ? 100.0d : (double) intExtra);
            this.mLevel = intExtra;
            getRoot().requestUpdate();
        }
    }
}
