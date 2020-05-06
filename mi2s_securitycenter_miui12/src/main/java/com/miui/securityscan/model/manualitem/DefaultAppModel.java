package com.miui.securityscan.model.manualitem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.List;

public abstract class DefaultAppModel extends AbsModel {
    public static final int MATCH_ALL = 131072;
    private String defaultPkgName;
    private IntentFilter filter;
    private String typeName;

    public DefaultAppModel(String str, Integer num) {
        super(str, num);
        initModel();
    }

    public static Intent getIntent(IntentFilter intentFilter) {
        Uri uri;
        Intent intent = new Intent(intentFilter.getAction(0));
        if (intentFilter.countCategories() > 0 && !TextUtils.isEmpty(intentFilter.getCategory(0))) {
            intent.addCategory(intentFilter.getCategory(0));
        }
        String str = null;
        if (intentFilter.countDataSchemes() <= 0 || TextUtils.isEmpty(intentFilter.getDataScheme(0))) {
            uri = null;
        } else {
            uri = Uri.parse(intentFilter.getDataScheme(0) + ":");
        }
        if (intentFilter.countDataTypes() > 0 && !TextUtils.isEmpty(intentFilter.getDataType(0))) {
            str = intentFilter.getDataType(0);
            if (!str.contains("\\") && !str.contains("/")) {
                str = str + "/*";
            }
        }
        intent.setDataAndType(uri, str);
        return intent;
    }

    public static boolean isDefaultMiuiApp(Context context, IntentFilter intentFilter, String str) {
        if (intentFilter == null) {
            Log.d("isDefaultMiuiApp", "ri == null ");
            return true;
        }
        try {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(getIntent(intentFilter), 65536);
            if (resolveActivity == null) {
                return true;
            }
            String str2 = resolveActivity.activityInfo.packageName;
            Log.d("isDefaultMiuiApp", "resolved = " + str2 + " ; " + str);
            return str2.equals(str) || str2.equals(Constants.System.ANDROID_PACKAGE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private static void setDefaultMiuiApp(Context context, IntentFilter intentFilter, String str) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(getIntent(intentFilter), 131072);
        ComponentName[] componentNameArr = new ComponentName[(queryIntentActivities.size() + 1)];
        ResolveInfo resolveInfo = null;
        int i = 0;
        for (int i2 = 0; i2 < queryIntentActivities.size(); i2++) {
            ResolveInfo resolveInfo2 = queryIntentActivities.get(i2);
            componentNameArr[i2] = new ComponentName(resolveInfo2.activityInfo.packageName, resolveInfo2.activityInfo.name);
            int i3 = resolveInfo2.match;
            if (i3 > i) {
                i = i3;
            }
            if (resolveInfo2.activityInfo.packageName.equals(str)) {
                resolveInfo = resolveInfo2;
            }
        }
        packageManager.clearPackagePreferredActivities(packageManager.resolveActivity(getIntent(intentFilter), 65536).activityInfo.packageName);
        if (resolveInfo != null) {
            setPreferredApp(intentFilter, packageManager, resolveInfo, componentNameArr, i);
        }
    }

    private static void setPreferredApp(IntentFilter intentFilter, PackageManager packageManager, ResolveInfo resolveInfo, ComponentName[] componentNameArr, int i) {
        IntentFilter intentFilter2 = new IntentFilter(intentFilter);
        intentFilter2.addCategory(Constants.System.CATEGORY_DEFALUT);
        intentFilter2.addCategory("android.intent.category.BROWSABLE");
        packageManager.addPreferredActivity(intentFilter2, i, componentNameArr, new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
    }

    /* access modifiers changed from: protected */
    public String getDefaultPkgName() {
        return this.defaultPkgName;
    }

    public String getDesc() {
        return null;
    }

    /* access modifiers changed from: protected */
    public IntentFilter getIntentFilter() {
        return this.filter;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_default_app, new Object[]{this.typeName});
    }

    public String getTitle() {
        return this.typeName;
    }

    /* access modifiers changed from: protected */
    public String getTypeName() {
        return this.typeName;
    }

    /* access modifiers changed from: protected */
    public abstract void initModel();

    public void optimize(Context context) {
        setDefaultMiuiApp(getContext(), this.filter, getDefaultPkgName());
        setSafe(AbsModel.State.SAFE);
    }

    public void scan() {
        setSafe(isDefaultMiuiApp(getContext(), this.filter, this.defaultPkgName) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }

    /* access modifiers changed from: protected */
    public void setDefaultPkgName(String str) {
        this.defaultPkgName = str;
    }

    /* access modifiers changed from: protected */
    public void setIntentFilter(IntentFilter intentFilter) {
        this.filter = intentFilter;
    }

    /* access modifiers changed from: protected */
    public void setTypeName(String str) {
        this.typeName = str;
    }
}
