package com.miui.securitycenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import com.miui.appmanager.AppManageUtils;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.h;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.util.Log;

public class SecSettingsSearchProvider extends ContentProvider {

    class a {

        /* renamed from: a  reason: collision with root package name */
        String f7504a;

        /* renamed from: b  reason: collision with root package name */
        String f7505b;

        /* renamed from: c  reason: collision with root package name */
        String f7506c;

        /* renamed from: d  reason: collision with root package name */
        String f7507d;

        public a(String str, String str2, String str3, String str4) {
            this.f7504a = str;
            this.f7505b = str2;
            this.f7506c = str3;
            this.f7507d = str4;
        }
    }

    private boolean a(String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(str, 0);
        } catch (Exception e) {
            Log.e("SecSearchProvider", "package not exist!", e);
            packageInfo = null;
        }
        return packageInfo != null;
    }

    public List<a> a() {
        a aVar;
        ArrayList arrayList = new ArrayList();
        a aVar2 = new a(getContext().getString(R.string.title_of_auto_launch_manage), "miui.intent.action.OP_AUTO_START", getContext().getPackageName(), "com.miui.permcenter.autostart.AutoStartManagementActivity");
        a aVar3 = new a(getContext().getString(R.string.pc_install_manager), "", getContext().getPackageName(), "com.miui.permcenter.install.PackageManagerActivity");
        a aVar4 = new a(getContext().getString(R.string.game_booster), "", getContext().getPackageName(), "com.miui.gamebooster.ui.GameBoosterMainActivity");
        a aVar5 = new a(getContext().getString(R.string.power_consume_rank_title), "android.intent.action.POWER_USAGE_SUMMARY", getContext().getPackageName(), "com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity");
        arrayList.add(aVar2);
        arrayList.add(aVar3);
        arrayList.add(aVar4);
        arrayList.add(aVar5);
        if (a("com.xiaomi.market")) {
            a aVar6 = new a(getContext().getString(R.string.app_manager_uninstall), "", "com.xiaomi.market", "com.xiaomi.market.ui.LocalAppsActivity");
            a aVar7 = new a(getContext().getString(R.string.app_manager_app_update), "", "com.xiaomi.market", "com.xiaomi.market.ui.UpdateAppsActivity");
            arrayList.add(aVar6);
            arrayList.add(aVar7);
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            aVar = new a(getContext().getString(R.string.activity_title_permission_manager), "android.intent.action.MANAGE_PERMISSIONS", "com.google.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity");
        } else {
            aVar = new a(getContext().getString(R.string.activity_title_permission_manager), "", getContext().getPackageName(), "com.miui.permcenter.permissions.AppPermissionsTabActivity");
        }
        arrayList.add(aVar);
        if (AppManageUtils.e(getContext())) {
            arrayList.add(new a(getContext().getString(R.string.app_manager_anomaly_analysis), "", getContext().getPackageName(), "com.miui.appmanager.AppManagerSettings"));
        }
        return arrayList;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor matrixCursor = new MatrixCursor(h.f7542a);
        for (a next : a()) {
            matrixCursor.newRow().add(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, next.f7504a).add("intentAction", next.f7505b).add("intentTargetPackage", next.f7506c).add("intentTargetClass", next.f7507d);
        }
        matrixCursor.newRow().add(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, getContext().getString(R.string.activity_title_power_manager)).add("intentAction", "miui.intent.action.POWER_MANAGER").add("intentTargetPackage", getContext().getPackageName()).add("intentTargetClass", "com.miui.powercenter.PowerMainActivity").add("keywords", getContext().getString(R.string.pc_search_keywords_battery) + ";" + getContext().getString(R.string.pc_search_keywords_battery_save) + ";" + getContext().getString(R.string.pc_search_keywords_battery_use) + ";" + getContext().getString(R.string.pc_search_keywords_capacity) + ";" + getContext().getString(R.string.pc_search_keywords_property));
        matrixCursor.newRow().add(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, getContext().getString(R.string.privacy_protect_title)).add("intentAction", "miui.intent.action.PRIVACY_SETTINGS").add("intentTargetPackage", getContext().getPackageName()).add("intentTargetClass", "com.miui.permcenter.settings.PrivacySettingsActivity").add("keywords", getContext().getString(R.string.privacy_search_key_1) + ";" + getContext().getString(R.string.privacy_search_key_2) + ";" + getContext().getString(R.string.privacy_search_key_3) + ";" + getContext().getString(R.string.privacy_search_key_4) + ";" + getContext().getString(R.string.privacy_search_key_5) + ";" + getContext().getString(R.string.privacy_search_key_6) + ";" + getContext().getString(R.string.privacy_search_key_7) + ";" + getContext().getString(R.string.privacy_search_key_8) + ";" + getContext().getString(R.string.privacy_search_key_9) + ";" + getContext().getString(R.string.privacy_search_key_10));
        return matrixCursor;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
