package com.miui.luckymoney.webapi;

import android.os.AsyncTask;

public class NetworkAsyncTask extends AsyncTask<Void, Void, Void> {
    private FloatResourceResult floatResourceResult;
    private LuckyAlarmResult luckyAlarmResult;
    private MasterSwitchResult masterSwitchResult;

    /* access modifiers changed from: protected */
    public Void doInBackground(Void... voidArr) {
        this.masterSwitchResult = WebApiAccessHelper.updateMasterSwitchConfig();
        this.luckyAlarmResult = WebApiAccessHelper.updateLuckyAlarmConfig();
        this.floatResourceResult = WebApiAccessHelper.updateFloatResourceConfig();
        return null;
    }
}
