package com.miui.networkassistant.config;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataUsageIgnoreAppListConfig extends ConfigFile {
    private static final String FILE_NAME = "_datausage_ignore_applist_config.db";
    private static HashMap<String, DataUsageIgnoreAppListConfig> sInstanceMap;
    private String mImsi;

    private DataUsageIgnoreAppListConfig(Context context, String str) {
        this.mImsi = str;
        init(context);
    }

    public static synchronized DataUsageIgnoreAppListConfig getInstance(Context context, String str) {
        DataUsageIgnoreAppListConfig dataUsageIgnoreAppListConfig;
        synchronized (DataUsageIgnoreAppListConfig.class) {
            if (sInstanceMap == null) {
                sInstanceMap = new HashMap<>();
            }
            dataUsageIgnoreAppListConfig = sInstanceMap.get(str);
            if (dataUsageIgnoreAppListConfig == null) {
                dataUsageIgnoreAppListConfig = new DataUsageIgnoreAppListConfig(context, str);
                sInstanceMap.put(str, dataUsageIgnoreAppListConfig);
            }
        }
        return dataUsageIgnoreAppListConfig;
    }

    /* access modifiers changed from: protected */
    public String getFileName() {
        return this.mImsi + FILE_NAME;
    }

    public ArrayList<String> getIgnoreList() {
        ArrayList<String> arrayList = new ArrayList<>();
        String valueOf = String.valueOf(true);
        for (Map.Entry next : this.mPairValues.entrySet()) {
            if (((String) next.getValue()).equals(valueOf)) {
                arrayList.add(next.getKey());
            }
        }
        return arrayList;
    }

    public boolean isDataUsageIgnore(String str) {
        return get(str, false);
    }

    public void setDataUsageIgnore(String str, boolean z) {
        set(str, String.valueOf(z));
    }
}
