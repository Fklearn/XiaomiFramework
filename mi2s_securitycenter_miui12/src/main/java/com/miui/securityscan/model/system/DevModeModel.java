package com.miui.securityscan.model.system;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import b.b.c.j.y;
import b.b.o.f.b.a;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import java.util.List;

public class DevModeModel extends AbsModel {
    private static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 16777216;
    private static final int MAX_TASKS = 1001;
    private static final String PREF_FILE = "development";
    private static final String PREF_SHOW = "show";
    private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";

    public DevModeModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
    }

    public String getDesc() {
        return getContext().getString(R.string.item_dev_mode);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_dev);
    }

    public String getTitle() {
        return getContext().getString(isSafe() == AbsModel.State.SAFE ? R.string.title_dev_yes : R.string.title_dev_no);
    }

    public void optimize(Context context) {
        try {
            Context createPackageContext = getContext().createPackageContext(SETTINGS_PACKAGE_NAME, 2);
            if (createPackageContext != null) {
                createPackageContext.getSharedPreferences(PREF_FILE, 0).edit().putBoolean(PREF_SHOW, false).commit();
                Intent intent = new Intent();
                intent.setAction("com.android.settings.action.DEV_CLOSE");
                intent.addFlags(16777216);
                intent.putExtra(PREF_SHOW, false);
                getContext().sendBroadcast(intent);
            }
            PackageManager packageManager = getContext().getPackageManager();
            List<ActivityManager.RecentTaskInfo> recentTasks = ((ActivityManager) getContext().getSystemService("activity")).getRecentTasks(1001, 2);
            int i = 0;
            while (true) {
                if (i >= recentTasks.size()) {
                    break;
                }
                ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(i);
                Intent intent2 = new Intent(recentTaskInfo.baseIntent);
                if (recentTaskInfo.origActivity != null) {
                    intent2.setComponent(recentTaskInfo.origActivity);
                }
                intent2.setFlags((intent2.getFlags() & -2097153) | 268435456);
                ResolveInfo resolveActivity = packageManager.resolveActivity(intent2, 0);
                if (resolveActivity != null && resolveActivity.activityInfo != null && resolveActivity.activityInfo.packageName != null && resolveActivity.activityInfo.packageName.equals(SETTINGS_PACKAGE_NAME)) {
                    a.a(getContext()).a(recentTaskInfo.persistentId);
                    break;
                }
                i++;
            }
            Settings.Secure.putInt(getContext().getContentResolver(), "adb_enabled", 0);
            if (Build.VERSION.SDK_INT >= 28) {
                Settings.Global.putInt(getContext().getContentResolver(), "development_settings_enabled", 0);
            }
            setSafe(AbsModel.State.SAFE);
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getContext().getString(R.string.title_dev_model_off), true));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void scan() {
        if (y.a("ro.debuggable", 0) == 1) {
            setSafe(AbsModel.State.SAFE);
            return;
        }
        try {
            boolean z = getContext().createPackageContext(SETTINGS_PACKAGE_NAME, 2).getSharedPreferences(PREF_FILE, 4).getBoolean(PREF_SHOW, miui.os.Build.IS_DEVELOPMENT_VERSION);
            if (Build.VERSION.SDK_INT >= 28) {
                z = Settings.Global.getInt(getContext().getContentResolver(), "development_settings_enabled", 0) != 0;
            }
            setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
            if (isSafe() == AbsModel.State.SAFE) {
                C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getContext().getString(R.string.title_dev_model_off), false));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
