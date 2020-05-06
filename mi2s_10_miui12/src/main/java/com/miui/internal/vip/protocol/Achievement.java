package com.miui.internal.vip.protocol;

public class Achievement {
    public long badgeId;
    public ExtInfo clientExtension;
    public int owned;

    public String toString() {
        return "Achievement{owned=" + this.owned + ", badgeId=" + this.badgeId + ", clientExtension=" + this.clientExtension + '}';
    }
}
