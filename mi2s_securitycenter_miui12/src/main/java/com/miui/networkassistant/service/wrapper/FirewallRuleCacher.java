package com.miui.networkassistant.service.wrapper;

import android.os.RemoteException;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.FirewallRuleSet;
import com.miui.networkassistant.service.IFirewallBinder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FirewallRuleCacher {
    public static final int TYPE_SIM1 = 2;
    public static final int TYPE_SIM2 = 4;
    public static final int TYPE_WLAN = 1;
    private int mDataTypeMasks;
    private IFirewallBinder mFirewallBinder;
    private ConcurrentHashMap<String, FirewallRuleSet> mRules;

    public FirewallRuleCacher(IFirewallBinder iFirewallBinder, int i) {
        this.mDataTypeMasks = i;
        this.mFirewallBinder = iFirewallBinder;
        this.mRules = new ConcurrentHashMap<>();
    }

    public FirewallRuleCacher(IFirewallBinder iFirewallBinder, ConcurrentHashMap<String, FirewallRuleSet> concurrentHashMap, int i) {
        this.mDataTypeMasks = i;
        this.mFirewallBinder = iFirewallBinder;
        this.mRules = concurrentHashMap;
    }

    private void addRestrictsListToRuleMap(Map<String, FirewallRuleSet> map, List<String> list, int i) {
        for (String next : list) {
            FirewallRuleSet firewallRuleSet = map.get(next);
            if (firewallRuleSet == null) {
                firewallRuleSet = FirewallRuleSet.defaultValue();
            }
            if (i == 1) {
                firewallRuleSet.wifiRule = FirewallRule.Restrict;
            } else if (i == 2) {
                firewallRuleSet.mobileRule = FirewallRule.Restrict;
            } else if (i == 4) {
                firewallRuleSet.mobileRule2 = FirewallRule.Restrict;
            }
            map.put(next, firewallRuleSet);
        }
    }

    private void updateData() {
        if (this.mFirewallBinder != null) {
            try {
                ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                if ((this.mDataTypeMasks & 1) != 0) {
                    addRestrictsListToRuleMap(concurrentHashMap, this.mFirewallBinder.getWifiRestrictPackages(), 1);
                }
                if ((this.mDataTypeMasks & 2) != 0) {
                    addRestrictsListToRuleMap(concurrentHashMap, this.mFirewallBinder.getMobileRestrictPackages(0), 2);
                }
                if ((this.mDataTypeMasks & 4) != 0) {
                    addRestrictsListToRuleMap(concurrentHashMap, this.mFirewallBinder.getMobileRestrictPackages(1), 4);
                }
                this.mRules.clear();
                this.mRules.putAll(concurrentHashMap);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public FirewallRuleCacher copy() {
        return new FirewallRuleCacher(this.mFirewallBinder, new ConcurrentHashMap(this.mRules), this.mDataTypeMasks);
    }

    public FirewallRule getMobileRule(String str, int i) {
        FirewallRuleSet rule = getRule(str);
        return i == 1 ? rule.mobileRule2 : rule.mobileRule;
    }

    public FirewallRuleSet getRule(String str) {
        FirewallRuleSet firewallRuleSet = this.mRules.get(str);
        return firewallRuleSet == null ? FirewallRuleSet.defaultValue() : firewallRuleSet;
    }

    public FirewallRule getWifiRule(String str) {
        return getRule(str).wifiRule;
    }

    public void notifyRuleChanged() {
        updateData();
    }

    public void setFirewallBinder(IFirewallBinder iFirewallBinder) {
        this.mFirewallBinder = iFirewallBinder;
        notifyRuleChanged();
    }
}
