package com.miui.luckymoney.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.h.f;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.luckymoney.webapi.MasterSwitchResult;
import com.miui.luckymoney.webapi.NetworkAsyncTask;
import com.miui.luckymoney.webapi.UploadConfigAsyncTask;
import com.miui.securitycenter.h;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudControlReceiver extends BroadcastReceiver {
    private static final String TAG = "CloudControlReceiver";
    /* access modifiers changed from: private */
    public CommonConfig commonConfig;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /* access modifiers changed from: private */
    public boolean needToCheckUpdate() {
        if (!this.commonConfig.getXiaomiLuckyMoneyEnable()) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long lastTimeCheckUpdateConfig = this.commonConfig.getLastTimeCheckUpdateConfig();
        return (currentTimeMillis < this.commonConfig.getHotStartTime() || currentTimeMillis > this.commonConfig.getHotEndTime()) ? currentTimeMillis - lastTimeCheckUpdateConfig > this.commonConfig.getDefaultUpdateFrequency() : currentTimeMillis - lastTimeCheckUpdateConfig > this.commonConfig.getHotFrequency();
    }

    /* access modifiers changed from: private */
    public void syncConfigFromServer() {
        if (h.i()) {
            new NetworkAsyncTask().execute(new Void[0]);
        }
    }

    public void onReceive(final Context context, Intent intent) {
        this.commonConfig = CommonConfig.getInstance(context);
        this.executor.execute(new Runnable() {
            public void run() {
                if (f.j(context)) {
                    if (CloudControlReceiver.this.needToCheckUpdate()) {
                        Log.d(CloudControlReceiver.TAG, "check CloudControl config");
                        CloudControlReceiver.this.commonConfig.setLastTimeCheckUpdateConfig(System.currentTimeMillis());
                        CloudControlReceiver.this.syncConfigFromServer();
                        ResFileUtils.cleanPNGRes(context);
                    }
                    if (CloudControlReceiver.this.commonConfig.isConfigChanged()) {
                        Log.d(CloudControlReceiver.TAG, "upload settings");
                        new UploadConfigAsyncTask().execute(new Boolean[]{Boolean.valueOf(CloudControlReceiver.this.commonConfig.getXiaomiLuckyMoneyEnable()), Boolean.valueOf(CloudControlReceiver.this.commonConfig.getLuckyAlarmEnable())});
                    }
                }
                new MasterSwitchResult(CloudControlReceiver.this.commonConfig.getMasterSwitchConfig());
            }
        });
    }
}
