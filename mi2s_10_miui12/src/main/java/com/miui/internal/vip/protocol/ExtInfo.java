package com.miui.internal.vip.protocol;

import miui.telephony.phonenumber.Prefix;

public class ExtInfo {
    public ExtAction action;
    public String app = Prefix.EMPTY;
    public String iconUrl;

    public String toString() {
        return "ExtInfo{iconUrl='" + this.iconUrl + '\'' + ", app='" + this.app + '\'' + ", action=" + this.action + '}';
    }
}
