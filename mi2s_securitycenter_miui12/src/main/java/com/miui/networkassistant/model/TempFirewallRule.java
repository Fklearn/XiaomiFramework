package com.miui.networkassistant.model;

public class TempFirewallRule {
    public FirewallRule rule;
    public String srcPkgName;

    public TempFirewallRule(FirewallRule firewallRule, String str) {
        this.rule = firewallRule;
        this.srcPkgName = str;
    }
}
