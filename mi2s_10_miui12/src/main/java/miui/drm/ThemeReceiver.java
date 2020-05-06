package miui.drm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import miui.app.constants.ThemeManagerConstants;
import miui.content.res.ThemeResources;
import miui.content.res.ThemeRuntimeManager;
import miui.drm.DrmManager;
import miui.util.HashUtils;

public class ThemeReceiver extends BroadcastReceiver {
    private static final String TAG = "drm";
    /* access modifiers changed from: private */
    public static Map<String, String> sLocations = new HashMap();
    private static Set<String> sWhiteList = new HashSet();
    /* access modifiers changed from: private */
    public boolean mIsValidating = false;

    static {
        sLocations.put("/data/system/theme/", ThemeResources.THEME_RIGHTS_PATH);
        sLocations.put(ThemeRuntimeManager.RUNTIME_PATH_BOOT_ANIMATION, ThemeResources.THEME_RIGHTS_PATH);
        for (String item : ThemeManagerConstants.DRM_WHITE_LIST) {
            Set<String> set = sWhiteList;
            set.add("/data/system/theme/" + item);
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (DrmBroadcast.ACTION_CHECK_TIME_UP.equals(intent.getAction())) {
            Log.i("drm", "check theme drm event received");
            if (!this.mIsValidating) {
                this.mIsValidating = true;
                new ValidateThemeTask(context).execute(new Void[0]);
                return;
            }
            Log.w("drm", "Validating theme task is running. ");
        }
    }

    private class ValidateThemeTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public ValidateThemeTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voids) {
            try {
                for (Map.Entry<String, String> entry : ThemeReceiver.sLocations.entrySet()) {
                    if (!ThemeReceiver.this.validateTheme(this.mContext, entry.getKey(), entry.getValue())) {
                        Log.w("drm", "restore default theme in " + entry.getKey());
                        new ThemeRuntimeManager(this.mContext).restoreDefault();
                        return null;
                    }
                }
                return null;
            } catch (Exception e) {
                Log.i("drm", "check theme drm occur exception: " + e.toString());
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            boolean unused = ThemeReceiver.this.mIsValidating = false;
        }
    }

    /* access modifiers changed from: private */
    public boolean validateTheme(Context context, String contentPath, String rightsPath) {
        String[] fileList;
        Log.i("drm", "validate theme in " + contentPath);
        File contentFile = new File(contentPath);
        File rightsFolder = new File(rightsPath);
        if (contentFile.exists() && !sWhiteList.contains(contentFile.getAbsolutePath())) {
            if (contentFile.isDirectory()) {
                for (File file : contentFile.listFiles()) {
                    if (!validateTheme(context, file.getAbsolutePath(), rightsPath)) {
                        return false;
                    }
                }
            } else {
                Log.i("drm", "checking component " + contentFile.getAbsolutePath() + " with " + rightsFolder.getAbsolutePath());
                DrmManager.DrmResult ret = DrmManager.isLegal(context, contentFile, rightsFolder);
                if (ret != DrmManager.DrmResult.DRM_SUCCESS) {
                    DrmManager.exportFatalLog("drm", "illegal theme component found: " + contentFile.getAbsolutePath() + " hash:" + HashUtils.getSHA1(contentFile) + " " + ret);
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("checking rightFolder: ");
                    sb2.append(rightsFolder.getAbsolutePath());
                    sb.append(sb2.toString());
                    if (rightsFolder.isDirectory() && (fileList = rightsFolder.list()) != null) {
                        for (String name : fileList) {
                            sb.append(" " + name);
                        }
                    }
                    DrmManager.exportFatalLog("drm", sb.toString());
                    return false;
                }
            }
        }
        return true;
    }
}
