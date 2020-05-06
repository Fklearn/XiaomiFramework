package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.b;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import java.util.ArrayList;
import java.util.List;

public class DefaultLauncherModel extends DefaultAppModel {
    private static List<String> LAUNCHER_WHITE_LIST = new ArrayList();
    private static final String TAG = "DefaultLauncherModel";

    static {
        LAUNCHER_WHITE_LIST.add("com.mi.android.globallauncher");
        LAUNCHER_WHITE_LIST.add("com.jeejen.family");
        LAUNCHER_WHITE_LIST.add("com.jeejen.family.miui");
    }

    public DefaultLauncherModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_launcher");
    }

    private boolean isSafeLauncher(Context context) {
        try {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
            intentFilter.addCategory("android.intent.category.HOME");
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(DefaultAppModel.getIntent(intentFilter), 65536);
            if (resolveActivity != null) {
                String str = resolveActivity.activityInfo.packageName;
                return !TextUtils.isEmpty(str) && LAUNCHER_WHITE_LIST.contains(str);
            }
        } catch (Exception e) {
            Log.e(TAG, "isSafeLauncher", e);
        }
        return false;
    }

    public int getIndex() {
        return 13;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName(b.a.f1608a);
        setTypeName(getContext().getString(R.string.preferred_app_entries_launcher));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        intentFilter.addCategory("android.intent.category.HOME");
        setIntentFilter(intentFilter);
    }

    public void scan() {
        if (isSafeLauncher(getContext())) {
            setSafe(AbsModel.State.SAFE);
        } else {
            super.scan();
        }
    }
}
