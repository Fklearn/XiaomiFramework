package com.android.server.signedconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignedConfig {
    private static final String CONFIG_KEY_MAX_SDK = "max_sdk";
    private static final String CONFIG_KEY_MIN_SDK = "min_sdk";
    private static final String CONFIG_KEY_VALUES = "values";
    private static final String KEY_CONFIG = "config";
    private static final String KEY_VERSION = "version";
    public final List<PerSdkConfig> perSdkConfig;
    public final int version;

    public static class PerSdkConfig {
        public final int maxSdk;
        public final int minSdk;
        public final Map<String, String> values;

        public PerSdkConfig(int minSdk2, int maxSdk2, Map<String, String> values2) {
            this.minSdk = minSdk2;
            this.maxSdk = maxSdk2;
            this.values = Collections.unmodifiableMap(values2);
        }
    }

    public SignedConfig(int version2, List<PerSdkConfig> perSdkConfig2) {
        this.version = version2;
        this.perSdkConfig = Collections.unmodifiableList(perSdkConfig2);
    }

    public PerSdkConfig getMatchingConfig(int sdkVersion) {
        for (PerSdkConfig config : this.perSdkConfig) {
            if (config.minSdk <= sdkVersion && sdkVersion <= config.maxSdk) {
                return config;
            }
        }
        return null;
    }

    public static SignedConfig parse(String config, Set<String> allowedKeys, Map<String, Map<String, String>> keyValueMappers) throws InvalidConfigException {
        try {
            JSONObject json = new JSONObject(config);
            int version2 = json.getInt(KEY_VERSION);
            JSONArray perSdkConfig2 = json.getJSONArray(KEY_CONFIG);
            List<PerSdkConfig> parsedConfigs = new ArrayList<>();
            for (int i = 0; i < perSdkConfig2.length(); i++) {
                parsedConfigs.add(parsePerSdkConfig(perSdkConfig2.getJSONObject(i), allowedKeys, keyValueMappers));
            }
            return new SignedConfig(version2, parsedConfigs);
        } catch (JSONException e) {
            throw new InvalidConfigException("Could not parse JSON", e);
        }
    }

    private static CharSequence quoted(Object s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s + "\"";
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.signedconfig.SignedConfig.PerSdkConfig parsePerSdkConfig(org.json.JSONObject r11, java.util.Set<java.lang.String> r12, java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r13) throws org.json.JSONException, com.android.server.signedconfig.InvalidConfigException {
        /*
            java.lang.String r0 = "min_sdk"
            int r0 = r11.getInt(r0)
            java.lang.String r1 = "max_sdk"
            int r1 = r11.getInt(r1)
            java.lang.String r2 = "values"
            org.json.JSONObject r2 = r11.getJSONObject(r2)
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            java.util.Set r4 = r2.keySet()
            java.util.Iterator r4 = r4.iterator()
        L_0x0022:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x00a1
            java.lang.Object r5 = r4.next()
            java.lang.String r5 = (java.lang.String) r5
            java.lang.Object r6 = r2.get(r5)
            java.lang.Object r7 = org.json.JSONObject.NULL
            if (r6 == r7) goto L_0x003e
            if (r6 != 0) goto L_0x0039
            goto L_0x003e
        L_0x0039:
            java.lang.String r7 = r6.toString()
            goto L_0x003f
        L_0x003e:
            r7 = 0
        L_0x003f:
            boolean r8 = r12.contains(r5)
            java.lang.String r9 = "Config key "
            if (r8 == 0) goto L_0x0087
            boolean r8 = r13.containsKey(r5)
            if (r8 == 0) goto L_0x0083
            java.lang.Object r8 = r13.get(r5)
            java.util.Map r8 = (java.util.Map) r8
            boolean r10 = r8.containsKey(r7)
            if (r10 == 0) goto L_0x0062
            java.lang.Object r9 = r8.get(r7)
            r7 = r9
            java.lang.String r7 = (java.lang.String) r7
            goto L_0x0083
        L_0x0062:
            com.android.server.signedconfig.InvalidConfigException r4 = new com.android.server.signedconfig.InvalidConfigException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r9)
            r10.append(r5)
            java.lang.String r9 = " contains unsupported value "
            r10.append(r9)
            java.lang.CharSequence r9 = quoted(r7)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r4.<init>(r9)
            throw r4
        L_0x0083:
            r3.put(r5, r7)
            goto L_0x0022
        L_0x0087:
            com.android.server.signedconfig.InvalidConfigException r4 = new com.android.server.signedconfig.InvalidConfigException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r9)
            r8.append(r5)
            java.lang.String r9 = " is not allowed"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r4.<init>(r8)
            throw r4
        L_0x00a1:
            com.android.server.signedconfig.SignedConfig$PerSdkConfig r4 = new com.android.server.signedconfig.SignedConfig$PerSdkConfig
            r4.<init>(r0, r1, r3)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.signedconfig.SignedConfig.parsePerSdkConfig(org.json.JSONObject, java.util.Set, java.util.Map):com.android.server.signedconfig.SignedConfig$PerSdkConfig");
    }
}
