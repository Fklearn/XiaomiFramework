package com.miui.networkassistant.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class FirewallRuleSet implements Parcelable {
    public static final Parcelable.Creator<FirewallRuleSet> CREATOR = new Parcelable.Creator<FirewallRuleSet>() {
        public FirewallRuleSet createFromParcel(Parcel parcel) {
            return FirewallRuleSet.parse(parcel.readString());
        }

        public FirewallRuleSet[] newArray(int i) {
            return new FirewallRuleSet[i];
        }
    };
    private static final String RULE_TAG = "#";
    public FirewallRule mobileRule;
    public FirewallRule mobileRule2;
    public FirewallRule wifiRule;

    public static FirewallRuleSet defaultValue() {
        FirewallRuleSet firewallRuleSet = new FirewallRuleSet();
        FirewallRule firewallRule = FirewallRule.Allow;
        firewallRuleSet.mobileRule = firewallRule;
        firewallRuleSet.wifiRule = firewallRule;
        firewallRuleSet.mobileRule2 = firewallRule;
        return firewallRuleSet;
    }

    public static FirewallRuleSet parse(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            String[] split = str.split(RULE_TAG);
            FirewallRuleSet defaultValue = defaultValue();
            defaultValue.mobileRule = FirewallRule.parse(Integer.parseInt(split[0]));
            defaultValue.wifiRule = FirewallRule.parse(Integer.parseInt(split[1]));
            defaultValue.mobileRule2 = split.length == 3 ? FirewallRule.parse(Integer.parseInt(split[2])) : FirewallRule.Allow;
            return defaultValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.format("%d%s%d%s%d", new Object[]{Integer.valueOf(this.mobileRule.value()), RULE_TAG, Integer.valueOf(this.wifiRule.value()), RULE_TAG, Integer.valueOf(this.mobileRule2.value())});
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(toString());
    }
}
