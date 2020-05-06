package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.res.Resources;
import b.b.c.j.x;
import com.miui.securitycenter.R;

public class LabelLoadHelper {
    public static final String ICON_DELETED_APP = "icon_deleted_app";
    public static final String ICON_MANAGED_PROFILE = "magaged_profile_package";
    public static final String ICON_MI_STATS = "com.xiaomi.mistatistic";
    public static final String ICON_OTHERS = "icon_others";
    public static final String ICON_PERSONAL_HOTPOT = "icon_personal_hotpot";
    public static final String ICON_ROOT = "icon_root";
    public static final String ICON_SYSTEM_APP = "icon_system_app";

    private LabelLoadHelper() {
    }

    public static CharSequence getCustomLabel(Context context, CharSequence charSequence) {
        Resources resources;
        int i;
        if ("icon_system_app".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.system_app;
        } else if ("icon_deleted_app".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.deleted_apps;
        } else if ("icon_personal_hotpot".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.person_hotpot;
        } else if ("icon_root".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.root;
        } else if ("icon_others".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.network_speed_for_apps_others;
        } else if ("com.xiaomi.mistatistic".equals(charSequence)) {
            resources = context.getResources();
            i = R.string.label_mi_stats;
        } else if (HybirdServiceUtil.isHybirdService(charSequence)) {
            return HybirdServiceUtil.getHybirdActivityLabel(context);
        } else {
            if (!"magaged_profile_package".equals(charSequence)) {
                return null;
            }
            resources = context.getResources();
            i = R.string.managed_user_title;
        }
        return resources.getString(i);
    }

    public static CharSequence loadLabel(Context context, CharSequence charSequence) {
        String realPackageName = PackageUtil.getRealPackageName(charSequence.toString().trim());
        CharSequence customLabel = getCustomLabel(context, realPackageName);
        return customLabel == null ? x.j(context, realPackageName.toString()) : customLabel;
    }
}
