package com.miui.internal.vip.protocol;

import java.util.ArrayList;
import java.util.List;
import miui.vip.VipAchievement;
import miui.vip.VipBanner;
import miui.vip.VipUserInfo;

public class Convertor {
    public static VipUserInfo toVipUserInfo(int level) {
        VipUserInfo user = new VipUserInfo();
        user.level = level;
        return user;
    }

    public static List<VipAchievement> toVipAchievement(Achievement[] list) {
        if (list == null || list.length == 0) {
            return null;
        }
        List<VipAchievement> retList = new ArrayList<>();
        for (Achievement src : list) {
            VipAchievement info = new VipAchievement();
            info.id = src.badgeId;
            info.url = src.clientExtension != null ? src.clientExtension.iconUrl : null;
            boolean z = true;
            if (src.owned != 1) {
                z = false;
            }
            info.isOwned = z;
            retList.add(info);
        }
        return retList;
    }

    public static List<VipBanner> toVipBanner(Banner[] srcList) {
        if (srcList == null || srcList.length == 0) {
            return null;
        }
        List<VipBanner> list = new ArrayList<>();
        for (Banner banner : srcList) {
            VipBanner vipBanner = new VipBanner();
            vipBanner.id = banner.bannerId;
            vipBanner.icon = banner.icon;
            vipBanner.name = banner.name;
            vipBanner.info = banner.info;
            if (!(banner.clientExtension == null || banner.clientExtension.action == null)) {
                vipBanner.action = banner.clientExtension.action.activity;
                vipBanner.extraParams = banner.clientExtension.action.extra;
            }
            list.add(vipBanner);
        }
        return list;
    }
}
