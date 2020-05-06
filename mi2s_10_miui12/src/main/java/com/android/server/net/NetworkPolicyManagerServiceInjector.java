package com.android.server.net;

import android.net.NetworkPolicy;
import com.google.android.collect.Maps;
import java.util.Map;

public class NetworkPolicyManagerServiceInjector {
    private static String TAG = "NetworkPolicyManagerServiceInjector";

    public static void updateNetworkRules(Map<NetworkPolicy, String[]> networkRules) {
        boolean findWifi = false;
        boolean findWifiWildCard = false;
        for (NetworkPolicy policy : networkRules.keySet()) {
            if ((policy.limitBytes != -1) || policy.metered) {
                if (policy.template.getMatchRule() == 7) {
                    findWifiWildCard = true;
                } else if (policy.template.getMatchRule() == 4) {
                    findWifi = true;
                }
            }
        }
        if (findWifi && findWifiWildCard) {
            Map<NetworkPolicy, String[]> newNetworkRules = Maps.newHashMap();
            for (NetworkPolicy policy2 : networkRules.keySet()) {
                if (policy2.template.getMatchRule() != 7) {
                    newNetworkRules.put(policy2, networkRules.get(policy2));
                }
            }
            networkRules.clear();
            networkRules.putAll(newNetworkRules);
        }
    }

    public static boolean checkPolicyForNetwork(NetworkPolicy policy) {
        if (policy.template.getMatchRule() != 7) {
            return true;
        }
        return false;
    }
}
