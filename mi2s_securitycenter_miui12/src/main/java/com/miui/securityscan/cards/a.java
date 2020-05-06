package com.miui.securityscan.cards;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.o;
import b.b.c.j.x;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.functions.CommonFunction;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.luckymoney.config.AppConstants;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import miui.util.OldmanUtil;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final Resources f7636a = Application.c();

    public static ArrayList<FuncTopBannerScrollData> a() {
        ArrayList<FuncTopBannerScrollData> arrayList = new ArrayList<>();
        if (x.h(Application.d(), AppConstants.Package.PACKAGE_NAME_MM)) {
            FuncTopBannerScrollData funcTopBannerScrollData = new FuncTopBannerScrollData();
            funcTopBannerScrollData.setTitle(f7636a.getString(R.string.card_main_deepclean_wechat_title));
            funcTopBannerScrollData.setSummary(f7636a.getString(R.string.card_main_deepclean_wechat_summary));
            funcTopBannerScrollData.setButton(f7636a.getString(R.string.clean_immediately));
            funcTopBannerScrollData.setIcon("drawable://2131232309");
            funcTopBannerScrollData.setImgUrl("drawable://2131232303");
            funcTopBannerScrollData.setStatKey("default_banner_wechat");
            funcTopBannerScrollData.setAction("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end");
            try {
                funcTopBannerScrollData.setCommonFunction(new CommonFunction(Intent.parseUri("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end", 0)));
                arrayList.add(funcTopBannerScrollData);
            } catch (Exception e) {
                Log.e("CardDefaultDataMaker", "parse action error", e);
            }
        }
        FuncTopBannerScrollData funcTopBannerScrollData2 = new FuncTopBannerScrollData();
        funcTopBannerScrollData2.setTitle(f7636a.getString(R.string.menu_text_optimize_manage));
        funcTopBannerScrollData2.setSummary(f7636a.getString(R.string.banner_optimizemanage_summary));
        funcTopBannerScrollData2.setButton(f7636a.getString(R.string.booster_immediately));
        funcTopBannerScrollData2.setIcon("drawable://2131232307");
        funcTopBannerScrollData2.setImgUrl("drawable://2131232301");
        funcTopBannerScrollData2.setStatKey("default_banner_optimizemanage");
        funcTopBannerScrollData2.setAction("#Intent;action=miui.intent.action.OPTIMIZE_MANAGE;end");
        try {
            funcTopBannerScrollData2.setCommonFunction(new CommonFunction(Intent.parseUri("#Intent;action=miui.intent.action.OPTIMIZE_MANAGE;end", 0)));
            arrayList.add(funcTopBannerScrollData2);
        } catch (Exception e2) {
            Log.e("CardDefaultDataMaker", "parse action error", e2);
        }
        FuncTopBannerScrollData funcTopBannerScrollData3 = new FuncTopBannerScrollData();
        funcTopBannerScrollData3.setTitle(f7636a.getString(R.string.card_main_hbassistant_title));
        funcTopBannerScrollData3.setSummary(f7636a.getString(R.string.banner_red_money_assist_summary));
        funcTopBannerScrollData3.setButton(f7636a.getString(R.string.banner_red_money_assist_button));
        funcTopBannerScrollData3.setIcon("drawable://2131232308");
        funcTopBannerScrollData3.setImgUrl("drawable://2131232302");
        funcTopBannerScrollData3.setStatKey("default_banner_lunckymoney");
        funcTopBannerScrollData3.setAction("#Intent;action=miui.intent.action.HB_MAIN_ACTIVITY;end");
        try {
            funcTopBannerScrollData3.setCommonFunction(new CommonFunction(Intent.parseUri("#Intent;action=miui.intent.action.HB_MAIN_ACTIVITY;end", 0)));
            arrayList.add(funcTopBannerScrollData3);
        } catch (Exception e3) {
            Log.e("CardDefaultDataMaker", "parse action error", e3);
        }
        return arrayList;
    }

    public static ArrayList<GridFunctionData> a(Context context) {
        return new ArrayList<>();
    }

    public static ArrayList<GridFunctionData> b() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.menu_text_garbage_cleanup), f7636a.getString(R.string.menu_summary_garbage_cleanup), R.drawable.menu_icon_garbage_selector, "#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end");
        gridFunctionData.setStatKey("clean_master_international");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.menu_text_antivirus), f7636a.getString(R.string.menu_summary_antivirus), R.drawable.menu_icon_virus_safe_selector, "#Intent;action=miui.intent.action.ANTI_VIRUS;end");
        gridFunctionData2.setStatKey("security_scan_international");
        arrayList.add(gridFunctionData2);
        return arrayList;
    }

    public static ArrayList<GridFunctionData> b(Context context) {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.power_save_mode), (String) null, R.drawable.phone_manage_save_mode, "#Intent;action=com.miui.powercenter.POWER_SAVE;end");
        gridFunctionData.setStatKey("phone_manage_power_save");
        arrayList.add(gridFunctionData);
        if (k.o(context)) {
            GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.power_center_super_save_title_text), (String) null, R.drawable.phone_manage_super_mode, "#Intent;action=com.miui.powercenter.SUPERPOWER_SAVE_NEW;end");
            gridFunctionData2.setStatKey("phone_manage_supersave");
            arrayList.add(gridFunctionData2);
        }
        GridFunctionData gridFunctionData3 = new GridFunctionData(f7636a.getString(R.string.power_save_settings), (String) null, R.drawable.phone_manage_save_setting, "#Intent;action=com.miui.securitycenter.action.POWER_SETTINGS;end");
        gridFunctionData3.setStatKey("phone_manage_power_save_setting");
        arrayList.add(gridFunctionData3);
        return arrayList;
    }

    public static ArrayList<GridFunctionData> c() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.menu_text_power_manager), f7636a.getString(R.string.menu_summary_power_manager), R.drawable.menu_icon_power_safe_selector, "#Intent;action=miui.intent.action.POWER_MANAGER;end");
        gridFunctionData.setStatKey("power_manager_international");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.menu_text_networkassistants), f7636a.getString(R.string.menu_summary_networkassistants), R.drawable.menu_icon_net_safe_selector, "#Intent;action=miui.intent.action.NETWORKASSISTANT_ENTRANCE;end");
        gridFunctionData2.setStatKey("network_assistant_international");
        arrayList.add(gridFunctionData2);
        return arrayList;
    }

    public static ArrayList<GridFunctionData> d() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.menu_text_antispam), f7636a.getString(R.string.menu_summary_antispam), R.drawable.grid_circular_anti_spam_selector, "#Intent;action=miui.intent.action.SET_FIREWALL;end");
        gridFunctionData.setStatKey("anti_spam_international");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.app_manager_title), f7636a.getString(R.string.menu_summary_app_manager), R.drawable.menu_icon_appmanager_selector, "#Intent;action=miui.intent.action.APP_MANAGER;end");
        gridFunctionData2.setStatKey("permissions_international");
        arrayList.add(gridFunctionData2);
        return arrayList;
    }

    public static ArrayList<GridFunctionData> e() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        arrayList.add(new GridFunctionData(f7636a.getString(R.string.menu_text_garbage_cleanup), f7636a.getString(R.string.menu_summary_garbage_cleanup), R.drawable.menu_icon_garbage_selector, "#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end"));
        arrayList.add(new GridFunctionData(f7636a.getString(R.string.menu_text_antivirus), f7636a.getString(R.string.menu_summary_antivirus), R.drawable.menu_icon_virus_safe_selector, "#Intent;action=miui.intent.action.ANTI_VIRUS;end"));
        return arrayList;
    }

    public static ArrayList<GridFunctionData> f() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        arrayList.add(new GridFunctionData(f7636a.getString(R.string.menu_text_power_manager), f7636a.getString(R.string.menu_summary_power_manager), R.drawable.menu_icon_power_safe_selector, "#Intent;action=miui.intent.action.POWER_MANAGER;end"));
        arrayList.add(new GridFunctionData(f7636a.getString(R.string.menu_text_networkassistants), f7636a.getString(R.string.menu_summary_networkassistants), R.drawable.menu_icon_net_safe_selector, "#Intent;action=miui.intent.action.NETWORKASSISTANT_ENTRANCE;end"));
        return arrayList;
    }

    public static ArrayList<GridFunctionData> g() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        arrayList.add(o.a() ? new GridFunctionData(f7636a.getString(R.string.privacy_protect_title), f7636a.getString(R.string.privacy_protect_summary), R.drawable.menu_icon_appmanager_privacy_setting, "#Intent;action=miui.intent.action.PRIVACY_SETTINGS;end") : new GridFunctionData(f7636a.getString(R.string.card_main_gamebooster_title), f7636a.getString(R.string.card_main_gamebooster_summary), R.drawable.grid_circular_game_boost_selector, "#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end"));
        arrayList.add(new GridFunctionData(f7636a.getString(R.string.app_manager_title), f7636a.getString(R.string.menu_summary_app_manager), R.drawable.menu_icon_appmanager_selector, "#Intent;action=miui.intent.action.APP_MANAGER;end"));
        return arrayList;
    }

    public static ArrayList<GridFunctionData> h() {
        boolean z = true;
        boolean z2 = B.f() && Build.VERSION.SDK_INT >= 21 && !OldmanUtil.IS_ELDER_MODE;
        if (Build.VERSION.SDK_INT < 21 || OldmanUtil.IS_ELDER_MODE) {
            z = false;
        }
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.card_main_deepclean_title), (String) null, R.drawable.phone_manage_deep_clean, "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN;end");
        gridFunctionData.setStatKey("deep_clean_international");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.card_main_applock_title), (String) null, R.drawable.phone_manage_app_lock, "#Intent;action=com.miui.securitycenter.action.TRANSITION;end");
        gridFunctionData2.setStatKey("app_lock_international");
        arrayList.add(gridFunctionData2);
        if (z2) {
            GridFunctionData gridFunctionData3 = new GridFunctionData(f7636a.getString(R.string.card_main_xspace_title), (String) null, R.drawable.phone_manage_dual_app, "#Intent;action=miui.intent.action.XSPACE_SETTING;end");
            gridFunctionData3.setStatKey("dual_apps_international");
            arrayList.add(gridFunctionData3);
        }
        if (z) {
            GridFunctionData gridFunctionData4 = new GridFunctionData(f7636a.getString(R.string.card_main_private_space_title), (String) null, R.drawable.phone_manage_second_space, "#Intent;action=miui.intent.action.PRIVATE_SPACE_SETTING;end");
            gridFunctionData4.setStatKey("second_space_international");
            arrayList.add(gridFunctionData4);
        }
        GridFunctionData gridFunctionData5 = new GridFunctionData(f7636a.getString(R.string.card_main_netcheck_title), (String) null, R.drawable.phone_manage_network_detection, "#Intent;action=miui.intent.action.NETWORK_DIAGNOSTICS;end");
        gridFunctionData5.setStatKey("network_diagnostics_international");
        arrayList.add(gridFunctionData5);
        GridFunctionData gridFunctionData6 = new GridFunctionData(f7636a.getString(R.string.first_aid_activity_title), (String) null, R.drawable.phone_manage_first_aid, "#Intent;action=com.miui.securitycenter.action.FIRST_AID_KIT;end");
        gridFunctionData6.setStatKey("first_aid_kit_international");
        arrayList.add(gridFunctionData6);
        if (o.a()) {
            GridFunctionData gridFunctionData7 = new GridFunctionData(f7636a.getString(R.string.privacy_protect_title), (String) null, R.drawable.phone_manage_privacy_setting_global, "#Intent;action=miui.intent.action.PRIVACY_SETTINGS;end");
            gridFunctionData7.setStatKey("phone_manage_privacy_setting");
            arrayList.add(gridFunctionData7);
        }
        return arrayList;
    }

    public static ArrayList<GridFunctionData> i() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.app_manager_title), (String) null, R.drawable.phone_manage_appmanager, "#Intent;action=miui.intent.action.APP_MANAGER;end");
        gridFunctionData.setStatKey("phone_manage_app_manage");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.card_main_applock_title), (String) null, R.drawable.phone_manage_app_lock, "#Intent;action=com.miui.securitycenter.action.TRANSITION;end");
        gridFunctionData2.setStatKey("phone_manage_applock");
        arrayList.add(gridFunctionData2);
        if (B.f() && Build.VERSION.SDK_INT >= 21 && !OldmanUtil.IS_ELDER_MODE) {
            GridFunctionData gridFunctionData3 = new GridFunctionData(f7636a.getString(R.string.card_main_xspace_title), (String) null, R.drawable.phone_manage_dual_app, "#Intent;action=miui.intent.action.XSPACE_SETTING;end");
            gridFunctionData3.setStatKey("phone_manage_dual_app");
            arrayList.add(gridFunctionData3);
        }
        return arrayList;
    }

    public static ArrayList<GridFunctionData> j() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.card_main_deepclean_title), (String) null, R.drawable.phone_manage_deep_clean, "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN;end");
        gridFunctionData.setStatKey("phone_manage_deep_clean");
        arrayList.add(gridFunctionData);
        if (x.h(Application.d(), AppConstants.Package.PACKAGE_NAME_MM)) {
            GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.card_main_deepclean_wechat_title), (String) null, R.drawable.phone_manage_wechat_clean, "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end");
            gridFunctionData2.setStatKey("phone_manage_wechat_clean");
            arrayList.add(gridFunctionData2);
        }
        if (x.h(Application.d(), AppConstants.Package.PACKAGE_NAME_QQ)) {
            GridFunctionData gridFunctionData3 = new GridFunctionData(f7636a.getString(R.string.card_main_deepclean_qq_title), (String) null, R.drawable.phone_manage_qq_clean, "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_QQ;end");
            gridFunctionData3.setStatKey("phone_manage_qq_clean");
            arrayList.add(gridFunctionData3);
        }
        GridFunctionData gridFunctionData4 = new GridFunctionData(f7636a.getString(R.string.menu_text_optimize_manage), (String) null, R.drawable.phone_manage_optimizemanage, "#Intent;action=miui.intent.action.OPTIMIZE_MANAGE;end");
        gridFunctionData4.setStatKey("phone_manage_optimizemanage");
        arrayList.add(gridFunctionData4);
        return arrayList;
    }

    public static ArrayList<GridFunctionData> k() {
        ArrayList<GridFunctionData> arrayList = new ArrayList<>();
        GridFunctionData gridFunctionData = new GridFunctionData(f7636a.getString(R.string.card_main_netcheck_title), (String) null, R.drawable.phone_manage_network_detection, "#Intent;action=miui.intent.action.NETWORK_DIAGNOSTICS;end");
        gridFunctionData.setStatKey("phone_manage_network_detection");
        arrayList.add(gridFunctionData);
        GridFunctionData gridFunctionData2 = new GridFunctionData(f7636a.getString(R.string.first_aid_activity_title), (String) null, R.drawable.phone_manage_first_aid, "#Intent;action=com.miui.securitycenter.action.FIRST_AID_KIT;end");
        gridFunctionData2.setStatKey("phone_manage_first_aid_kit");
        arrayList.add(gridFunctionData2);
        GridFunctionData gridFunctionData3 = new GridFunctionData(f7636a.getString(R.string.card_main_hbassistant_title), (String) null, R.drawable.phone_manage_lucky_money, "#Intent;action=miui.intent.action.HB_MAIN_ACTIVITY;end");
        gridFunctionData3.setStatKey("phone_manage_luckey_money");
        arrayList.add(gridFunctionData3);
        if (Build.VERSION.SDK_INT >= 21 && !OldmanUtil.IS_ELDER_MODE) {
            GridFunctionData gridFunctionData4 = new GridFunctionData(f7636a.getString(R.string.card_main_private_space_title), (String) null, R.drawable.phone_manage_second_space, "#Intent;action=miui.intent.action.PRIVATE_SPACE_SETTING;end");
            gridFunctionData4.setStatKey("phone_manage_second_space");
            arrayList.add(gridFunctionData4);
        }
        GridFunctionData gridFunctionData5 = new GridFunctionData(f7636a.getString(R.string.card_main_gamebooster_title), (String) null, R.drawable.phone_manage_gameboost, "#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end");
        gridFunctionData5.setStatKey("phone_manage_gameboost");
        arrayList.add(gridFunctionData5);
        GridFunctionData gridFunctionData6 = new GridFunctionData(f7636a.getString(R.string.system_tool_sos), (String) null, R.drawable.phone_manage_sos, "#Intent;component=com.android.settings/.SubSettings;S.%3Asettings%3Ashow_fragment=com.android.settings.emergency.ui.SosSettings;end");
        gridFunctionData6.setStatKey("phone_manage_sos");
        arrayList.add(gridFunctionData6);
        if (!B.g()) {
            GridFunctionData gridFunctionData7 = new GridFunctionData(f7636a.getString(R.string.title_find_device), (String) null, R.drawable.phone_manage_find_device, "#Intent;component=com.miui.cloudservice/com.miui.cloudservice.ui.MiCloudFindDeviceStatusActivity;end");
            gridFunctionData7.setStatKey("phone_manage_find_device");
            arrayList.add(gridFunctionData7);
        }
        GridFunctionData gridFunctionData8 = new GridFunctionData(f7636a.getString(R.string.menu_text_antispam), (String) null, R.drawable.phone_manage_antispam, "#Intent;action=miui.intent.action.SET_FIREWALL;end");
        gridFunctionData8.setStatKey("phone_manage_antispam");
        arrayList.add(gridFunctionData8);
        return arrayList;
    }
}
