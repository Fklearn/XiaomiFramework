package com.miui.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.miui.R;
import android.net.Uri;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.pm.PackageManagerService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class EnableStateManager {
    public static final String HAS_EVER_ENABLED = "com.xiaomi.market.hasEverEnabled_";
    public static final String HAS_UPDATE_APPLICATION_STATE = "has_update_application_state";
    public static final String LAST_REGION = "com.xiaomi.market.lastRegion";
    private static final String TAG = EnableStateManager.class.getSimpleName();
    private static Map<String, List<String>> mCloudEnableSettings = new HashMap();
    /* access modifiers changed from: private */
    public static Context mContext;
    private static boolean receiverRegistered = false;
    /* access modifiers changed from: private */
    public static List<String> sEnableStateControlledPkgList = new ArrayList();
    private static List<String> sShouldKeepStatePackages = new ArrayList();

    private EnableStateManager() {
    }

    public static void updateApplicationEnableState(Context context) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            if (mContext == null) {
                if (context != null) {
                    mContext = context;
                } else {
                    Log.i(TAG, "no context");
                    return;
                }
            }
            Settings.System.putString(mContext.getContentResolver(), HAS_UPDATE_APPLICATION_STATE, "true");
            updateApplicationEnableStateInner(!((PackageManagerService) ServiceManager.getService(com.android.server.pm.Settings.ATTR_PACKAGE)).isFirstBoot());
            registerReceiverIfNeed();
        }
    }

    private static void registerReceiverIfNeed() {
        if (!receiverRegistered) {
            receiverRegistered = true;
            new Thread() {
                public void run() {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.PACKAGE_ADDED");
                    filter.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
                    EnableStateManager.mContext.registerReceiver(new PackageAddedReceiver(), filter);
                }
            }.start();
        }
    }

    private static void updateApplicationEnableStateInner(boolean shouldKeep) {
        Log.i(TAG, "updateConfigFromFile");
        updateConfigFromFile();
        for (String pkgName : sEnableStateControlledPkgList) {
            if (isAppInstalled(pkgName)) {
                updateEnableState(pkgName, shouldKeep);
            }
        }
    }

    private static final class PackageAddedReceiver extends BroadcastReceiver {
        private PackageAddedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (EnableStateManager.sEnableStateControlledPkgList.contains(EnableStateManager.getPackageName(intent))) {
                EnableStateManager.updateApplicationEnableState(context);
            }
        }
    }

    /* access modifiers changed from: private */
    public static String getPackageName(Intent intent) {
        Uri uri;
        if (intent == null || (uri = intent.getData()) == null) {
            return null;
        }
        return uri.getSchemeSpecificPart();
    }

    private static boolean isAppInstalled(String pkgName) {
        try {
            if (mContext.getPackageManager().getApplicationInfo(pkgName, 0) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private static void updateEnableState(String pkgName, boolean shouldKeep) {
        try {
            String region = Build.getRegion();
            String str = TAG;
            Log.d(str, "region: " + region);
            if (!TextUtils.isEmpty(region)) {
                String lastRegion = getString(LAST_REGION);
                if (!TextUtils.isEmpty(lastRegion) && !TextUtils.equals(lastRegion, region)) {
                    shouldKeep = false;
                }
                Set<String> regionList = getEnableSettings(pkgName, shouldKeep);
                String str2 = TAG;
                Log.d(str2, "enable " + pkgName + " in " + regionList.toString());
                if (!regionList.contains(region)) {
                    if (!regionList.contains("all")) {
                        tryDisablePkg(pkgName);
                        setString(LAST_REGION, region);
                    }
                }
                tryEnablePkg(pkgName);
                setString(LAST_REGION, region);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private static void tryDisablePkg(String pkgName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            int state = mContext.getPackageManager().getApplicationEnabledSetting(pkgName);
            if (state == 0 || state == 1) {
                pm.setApplicationEnabledSetting(pkgName, 2, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private static void tryEnablePkg(String pkgName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            if (mContext.getPackageManager().getApplicationEnabledSetting(pkgName) == 2) {
                pm.setApplicationEnabledSetting(pkgName, 1, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private static Set<String> getEnableSettings(String pkgName, boolean shouldKeep) {
        Set<String> result = new HashSet<>();
        String region = Build.getRegion();
        List<String> regions = mCloudEnableSettings.get(pkgName);
        if (regions != null) {
            result.addAll(regions);
        }
        String[] apkPresetRegions = getStringArray(mContext, pkgName, "enable_regions");
        if (apkPresetRegions != null && apkPresetRegions.length > 0) {
            result.addAll(Arrays.asList(apkPresetRegions));
        }
        if (sShouldKeepStatePackages.contains(pkgName)) {
            Set<String> hasEnableRegions = getRegionSetByPkgName(HAS_EVER_ENABLED + pkgName);
            if (hasEnableRegions.size() > 0) {
                result.addAll(hasEnableRegions);
            }
            String str = TAG;
            Log.d(str, "shouldKeep: " + shouldKeep + "\n is " + pkgName + " enable " + isPackageEnabled(mContext, pkgName));
            if (shouldKeep && isPackageEnabled(mContext, pkgName)) {
                String str2 = TAG;
                Log.d(str2, "add " + pkgName + " at " + region);
                result.add(region);
            }
            setRegionSetByPkgName(HAS_EVER_ENABLED + pkgName, result);
        }
        return result;
    }

    private static String[] getStringArray(Context context, String pkgName, String resName) {
        try {
            Context fContext = context.createPackageContext(pkgName, 0);
            return fContext.getResources().getStringArray(fContext.getResources().getIdentifier(resName, "array", pkgName));
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return new String[0];
        }
    }

    private static void updateConfigFromFile() {
        mCloudEnableSettings.clear();
        sEnableStateControlledPkgList.clear();
        sShouldKeepStatePackages.clear();
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(R.raw.enable_list)));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = reader2.readLine();
                String temp = readLine;
                if (readLine == null) {
                    break;
                }
                sb.append(temp);
            }
            JSONObject json = new JSONObject(sb.toString());
            Iterator<String> pkgNameList = json.keys();
            while (pkgNameList.hasNext()) {
                String pkgName = pkgNameList.next();
                JSONObject settingJson = json.getJSONObject(pkgName);
                JSONArray enableRegionArray = settingJson.optJSONArray("enable_list");
                if (settingJson.optBoolean("shouldKeep", false)) {
                    sShouldKeepStatePackages.add(pkgName);
                }
                if (enableRegionArray != null) {
                    List<String> enableRegionList = new ArrayList<>();
                    for (int index = 0; index < enableRegionArray.length(); index++) {
                        enableRegionList.add(enableRegionArray.getString(index));
                    }
                    mCloudEnableSettings.put(pkgName, enableRegionList);
                }
                sEnableStateControlledPkgList.add(pkgName);
            }
            try {
                reader2.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        } catch (Exception e2) {
            Log.e(TAG, e2.toString(), e2);
            reader.close();
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (Exception e3) {
                Log.e(TAG, e3.toString(), e3);
            }
            throw th;
        }
    }

    private static boolean isPackageEnabled(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            int state = pm.getApplicationEnabledSetting(packageName);
            String str = TAG;
            Log.d(str, packageName + " state is " + state);
            if (state != 0) {
                if (state == 1) {
                    return true;
                }
                if (!(state == 2 || state == 3 || state == 4)) {
                    return false;
                }
            }
            return pm.getApplicationInfo(packageName, 0).enabled;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return true;
        }
    }

    private static void setString(String key, String value) {
        Settings.System.putString(mContext.getContentResolver(), key, value);
    }

    private static String getString(String key) {
        return Settings.System.getString(mContext.getContentResolver(), key);
    }

    public static void setRegionSetByPkgName(String packageName, Set<String> regions) {
        String tempResult = "";
        Iterator<String> iter = regions.iterator();
        if (iter != null) {
            while (iter.hasNext()) {
                if (TextUtils.isEmpty(tempResult)) {
                    tempResult = tempResult + iter.next();
                } else {
                    tempResult = tempResult + ";" + iter.next();
                }
            }
            Log.d(TAG, "setRegionSetByPkgName: " + tempResult);
            Settings.System.putString(mContext.getContentResolver(), packageName, tempResult);
        }
    }

    public static Set<String> getRegionSetByPkgName(String packageName) {
        String hasEnableRegions = Settings.System.getString(mContext.getContentResolver(), packageName);
        Log.d(TAG, "getRegionSetByPkgName: " + hasEnableRegions);
        if (TextUtils.isEmpty(hasEnableRegions)) {
            return new HashSet();
        }
        String[] hasEnableRegionList = hasEnableRegions.split(";");
        Set<String> hasEnableRegionSet = new HashSet<>();
        for (String hasEnableRegion : hasEnableRegionList) {
            hasEnableRegionSet.add(hasEnableRegion);
        }
        return hasEnableRegionSet;
    }
}
