package com.miui.networkassistant.config;

import android.content.Context;
import android.util.Log;
import com.miui.networkassistant.model.FirewallRule;
import java.util.HashMap;
import java.util.Map;
import miui.util.ArrayMap;

public class FireWallConfig extends ConfigFile {
    private static final String TAG = "FireWallConfig";
    private static HashMap<String, FireWallConfig> sInstanceMap;
    private String mFileName;

    private FireWallConfig(Context context, String str) {
        this.mFileName = str;
        init(context);
    }

    public static synchronized FireWallConfig getInstance(Context context, String str) {
        FireWallConfig fireWallConfig;
        synchronized (FireWallConfig.class) {
            if (sInstanceMap == null) {
                sInstanceMap = new HashMap<>();
            }
            fireWallConfig = sInstanceMap.get(str);
            if (fireWallConfig == null) {
                fireWallConfig = new FireWallConfig(context, str);
                sInstanceMap.put(str, fireWallConfig);
            }
        }
        return fireWallConfig;
    }

    /* access modifiers changed from: protected */
    public String getFileName() {
        return this.mFileName;
    }

    public synchronized ArrayMap<String, FirewallRule> getPairMap() {
        ArrayMap<String, FirewallRule> arrayMap;
        arrayMap = new ArrayMap<>();
        for (Map.Entry next : this.mPairValues.entrySet()) {
            String str = (String) next.getKey();
            int i = 1;
            try {
                i = Integer.parseInt((String) next.getValue());
            } catch (NumberFormatException e) {
                Log.i(TAG, "parse rule exception", e);
            }
            FirewallRule parse = FirewallRule.parse(i);
            if (!(str == null || parse == null)) {
                arrayMap.put(str, parse);
            }
        }
        return arrayMap;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:9|10|11|12) */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        return com.miui.networkassistant.model.FirewallRule.Allow;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0017 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.miui.networkassistant.model.FirewallRule getRule(java.lang.String r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = ""
            java.lang.String r2 = r1.get((java.lang.String) r2, (java.lang.String) r0)     // Catch:{ all -> 0x001f }
            boolean r0 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x001f }
            if (r0 != 0) goto L_0x001b
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0017 }
            com.miui.networkassistant.model.FirewallRule r2 = com.miui.networkassistant.model.FirewallRule.parse(r2)     // Catch:{ Exception -> 0x0017 }
            monitor-exit(r1)
            return r2
        L_0x0017:
            com.miui.networkassistant.model.FirewallRule r2 = com.miui.networkassistant.model.FirewallRule.Allow     // Catch:{ all -> 0x001f }
            monitor-exit(r1)
            return r2
        L_0x001b:
            com.miui.networkassistant.model.FirewallRule r2 = com.miui.networkassistant.model.FirewallRule.Allow     // Catch:{ all -> 0x001f }
            monitor-exit(r1)
            return r2
        L_0x001f:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.config.FireWallConfig.getRule(java.lang.String):com.miui.networkassistant.model.FirewallRule");
    }

    public synchronized void set(String str, FirewallRule firewallRule) {
        set(str, firewallRule.toString());
    }
}
