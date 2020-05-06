package com.miui.networkassistant.traffic.statistic;

import android.content.Context;
import b.b.c.j.B;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.utils.PackageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PreSetGroup {
    private static HashMap<String, Integer> mGroupHeadUidMap = new HashMap<>();
    private static List<String> sPreBgPolicyList = new ArrayList();
    private static List<String> sPreFirewallWhiteList = new ArrayList();

    static {
        mGroupHeadUidMap.put(Constants.System.ANDROID_PACKAGE_NAME, -1);
        mGroupHeadUidMap.put("com.android.providers.telephony", -1);
        mGroupHeadUidMap.put("com.android.contacts", -1);
        mGroupHeadUidMap.put("com.google.android.gsf", -1);
        mGroupHeadUidMap.put("com.android.providers.downloads.ui", -1);
        sPreBgPolicyList.add("com.xiaomi.xmsf");
        sPreBgPolicyList.add("com.miui.guardprovider");
        sPreBgPolicyList.add("com.xiaomi.account");
        sPreBgPolicyList.add("com.lbe.security.miui");
        sPreBgPolicyList.add(com.miui.earthquakewarning.Constants.SECURITY_ADD_PACKAGE);
        sPreFirewallWhiteList.add("com.xiaomi.finddevice");
        sPreFirewallWhiteList.add("com.miui.greenguard");
        sPreFirewallWhiteList.add("com.miui.mishare.connectivity");
    }

    private PreSetGroup() {
    }

    public static List<String> getPreBgPolicyList() {
        return sPreBgPolicyList;
    }

    public static List<String> getPreFirewallWhiteList() {
        return sPreFirewallWhiteList;
    }

    public static void initGroupMap(Context context) {
        initGroupMap(context, mGroupHeadUidMap);
    }

    private static void initGroupMap(Context context, Map<String, Integer> map) {
        for (Map.Entry<String, Integer> key : map.entrySet()) {
            String str = (String) key.getKey();
            if (map.get(str).intValue() == -1) {
                map.put(str, Integer.valueOf(B.a(PackageUtil.getUidByPackageName(context, str))));
            }
        }
    }

    public static boolean isGroupHead(String str) {
        return mGroupHeadUidMap.containsKey(str);
    }

    public static boolean isGroupUid(int i) {
        return mGroupHeadUidMap.containsValue(Integer.valueOf(B.a(i)));
    }

    public static boolean isPreFirewallWhiteListPackage(String str) {
        return sPreFirewallWhiteList.contains(str);
    }

    public static boolean isPrePolicyPackage(String str) {
        return sPreBgPolicyList.contains(str);
    }
}
