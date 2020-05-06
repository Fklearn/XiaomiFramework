package com.miui.networkassistant.firewall;

import com.miui.networkassistant.model.FirewallRule;
import java.util.List;
import miui.util.ArrayMap;

public interface IFirewall {
    List<String> getMobileRestrictPackages(int i);

    FirewallRule getMobileRule(String str, int i);

    FirewallRule getRoamingRule(String str);

    boolean getRoamingWhiteListEnable();

    FirewallRule getTempMobileRule(String str, int i);

    String getTempMobileRuleSrcPkgName(String str, int i);

    FirewallRule getTempWifiRule(String str);

    String getTempWifiRuleSrcPkgName(String str);

    List<String> getWifiRestrictPackages();

    FirewallRule getWifiRule(String str);

    boolean isStarted();

    void loadMobileRules(ArrayMap<String, FirewallRule> arrayMap, int i, boolean z, boolean z2);

    void loadRoamingRules(ArrayMap<String, FirewallRule> arrayMap);

    void loadWifiRules(ArrayMap<String, FirewallRule> arrayMap);

    void removePackage(String str, int i);

    void setListener(IFirewallListener iFirewallListener);

    void setMobileRule(String str, FirewallRule firewallRule, int i, boolean z);

    void setRoamingRule(String str, FirewallRule firewallRule);

    void setRoamingWhiteListEnable(boolean z);

    boolean setTempMobileRule(String str, FirewallRule firewallRule, String str2, int i, boolean z);

    boolean setTempWifiRule(String str, FirewallRule firewallRule, String str2);

    void setWifiRule(String str, FirewallRule firewallRule);

    boolean start();

    boolean stop();
}
