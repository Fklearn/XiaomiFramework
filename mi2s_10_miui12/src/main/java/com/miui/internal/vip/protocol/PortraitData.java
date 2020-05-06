package com.miui.internal.vip.protocol;

import java.util.Arrays;

public class PortraitData {
    public Achievement[] badgeList;
    public Banner[] bannerList;
    public int level;

    public String toString() {
        return "PortraitData{level=" + this.level + ", badgeList=" + Arrays.toString(this.badgeList) + ", bannerList=" + Arrays.toString(this.bannerList) + '}';
    }
}
