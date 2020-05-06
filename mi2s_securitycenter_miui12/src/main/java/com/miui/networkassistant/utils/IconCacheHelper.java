package com.miui.networkassistant.utils;

import android.widget.ImageView;
import b.b.c.j.r;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class IconCacheHelper {
    public static final String ICON_DELETED_APP = "icon_deleted_app";
    public static final String ICON_MANAGED_PROFILE = "magaged_profile_package";
    public static final String ICON_MI_STATS = "com.xiaomi.mistatistic";
    public static final String ICON_OTHERS = "icon_others";
    public static final String ICON_PERSONAL_HOTPOT = "icon_personal_hotpot";
    public static final String ICON_ROOT = "icon_root";
    public static final String ICON_SYSTEM_APP = "icon_system_app";
    private static IconCacheHelper sInstance;
    private HashMap<String, Integer> mCustomizedIconMap = new HashMap<>();

    private IconCacheHelper() {
        HashMap<String, Integer> hashMap = this.mCustomizedIconMap;
        Integer valueOf = Integer.valueOf(R.drawable.icon_system_apps);
        hashMap.put("icon_system_app", valueOf);
        this.mCustomizedIconMap.put("icon_deleted_app", Integer.valueOf(R.drawable.icon_deleted_apps));
        this.mCustomizedIconMap.put("icon_personal_hotpot", Integer.valueOf(R.drawable.icon_person_hotpot));
        this.mCustomizedIconMap.put("icon_root", valueOf);
        this.mCustomizedIconMap.put("icon_others", valueOf);
        this.mCustomizedIconMap.put("com.xiaomi.mistatistic", Integer.valueOf(R.drawable.ic_default_launcher));
        this.mCustomizedIconMap.put("magaged_profile_package", Integer.valueOf(R.drawable.ic_corp_icon));
    }

    public static synchronized IconCacheHelper getInstance() {
        IconCacheHelper iconCacheHelper;
        synchronized (IconCacheHelper.class) {
            if (sInstance == null) {
                sInstance = new IconCacheHelper();
            }
            iconCacheHelper = sInstance;
        }
        return iconCacheHelper;
    }

    public void setIconToImageView(ImageView imageView, String str) {
        String str2;
        String str3;
        if (PackageUtil.isXSpaceApp(str)) {
            str = PackageUtil.getRealPackageName(str);
            str3 = "pkg_icon_xspace://";
        } else {
            Integer num = this.mCustomizedIconMap.get(PackageUtil.getRealPackageName(str));
            if (num != null) {
                str2 = "drawable://" + num.intValue();
                r.a(str2, imageView, r.f, (int) R.drawable.icon_app_default);
            } else if (PackageUtil.isManagedProfileApp(str)) {
                str = str.replace(Constants.Default.MANAGED_PROFILE_PACKAGE_SPLIT, "/");
                str3 = "pkg_icon_managed_profile://";
            } else {
                str3 = "pkg_icon://";
            }
        }
        str2 = str3.concat(str);
        r.a(str2, imageView, r.f, (int) R.drawable.icon_app_default);
    }
}
