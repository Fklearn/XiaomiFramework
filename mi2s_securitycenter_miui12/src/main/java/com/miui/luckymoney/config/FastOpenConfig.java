package com.miui.luckymoney.config;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import miui.util.Log;

public class FastOpenConfig {
    private static final String TAG = "FastOpenConfig";
    private static FastOpenConfig sInstance;
    private CommonConfig mCommonConfig;
    private ArrayList<String> mRestricts = this.mCommonConfig.getFastOpenConfig();

    private FastOpenConfig(Context context) {
        this.mCommonConfig = CommonConfig.getInstance(context);
        if (this.mRestricts == null) {
            this.mRestricts = new ArrayList<>(Arrays.asList(AppConstants.FastOpenRestricts));
            saveConfig();
        }
        String str = TAG;
        Log.d(str, "mRestricts:" + this.mRestricts.toString());
    }

    private boolean add(String str) {
        if (!TextUtils.isEmpty(str) && !this.mRestricts.contains(str)) {
            return this.mRestricts.add(str);
        }
        return false;
    }

    public static synchronized FastOpenConfig getInstance(Context context) {
        FastOpenConfig fastOpenConfig;
        synchronized (FastOpenConfig.class) {
            if (sInstance == null) {
                sInstance = new FastOpenConfig(context.getApplicationContext());
            }
            fastOpenConfig = sInstance;
        }
        return fastOpenConfig;
    }

    private boolean remove(String str) {
        return this.mRestricts.remove(str);
    }

    public boolean contains(String str) {
        return this.mRestricts.contains(str);
    }

    public boolean isRestrict(String str) {
        return this.mCommonConfig.getFastOpenConfig().contains(str);
    }

    public void saveConfig() {
        this.mCommonConfig.setFastOpenConfig(this.mRestricts);
    }

    public boolean set(String str, boolean z) {
        return z ? remove(str) : add(str);
    }
}
