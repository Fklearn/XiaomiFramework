package com.miui.internal.vip.protocol;

public class Banner {
    public String action;
    public long bannerId;
    public ExtInfo clientExtension;
    public String icon;
    public String info;
    public String name;

    public String toString() {
        return "Banner{bannerId=" + this.bannerId + ", icon='" + this.icon + '\'' + ", name='" + this.name + '\'' + ", info='" + this.info + '\'' + ", action='" + this.action + '\'' + ", clientExtension=" + this.clientExtension + '}';
    }
}
