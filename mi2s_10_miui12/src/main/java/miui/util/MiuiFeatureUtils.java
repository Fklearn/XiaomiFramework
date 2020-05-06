package miui.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import miui.os.SystemProperties;
import miui.provider.MiCloudSmsCmd;

public class MiuiFeatureUtils {
    private static final String DEFAULT_CONFIG_FILE_PATH = "/system/etc/miui_feature/default.conf";
    public static final String FEATURE_COMPLETE_ANIMATION = "feature_complete_animation";
    public static final String FEATURE_RUNTIME_BLUR = "feature_runtime_blur";
    public static final String FEATURE_THUMBNAIL = "feature_thumbnail";
    private static final String LITE_CONFIG_FILE_PATH = "/system/etc/miui_feature/lite.conf";
    private static final String MIUISDK_FEATURE_PREFIX = "ro.sys.";
    private static final String MIUISDK_KEY = "miuisdk";
    private static final String PRPPERTY = "persist.sys.miui_feature_config";
    private static final String SYSTEM_KEY = "system";
    private static final String TAG = "MiuiFeatureUtils";
    private static String sConfigFilePath;
    private static HashMap<String, HashMap<String, Boolean>> sConfigResult;
    private static boolean sIsLiteMode = false;
    private static boolean sIsLiteModeSupported;
    private static HashMap<String, Boolean> sMiuisdkConfigResult;
    private static HashMap<String, Boolean> sSystemConfigResult;

    static {
        sIsLiteModeSupported = false;
        try {
            init();
        } catch (Exception e) {
            sIsLiteModeSupported = false;
            Log.e(TAG, "Failed to initialize MiuiFeatureUtils!");
        }
    }

    private MiuiFeatureUtils() {
    }

    public static class Features {
        HashMap<String, Boolean> mFeatures;

        Features(HashMap<String, Boolean> features) {
            this.mFeatures = features;
        }

        public boolean isFeatureSupported(String feature, boolean defaultValue) {
            Boolean result;
            if (TextUtils.isEmpty(feature)) {
                return defaultValue;
            }
            if (this.mFeatures != null && !TextUtils.isEmpty(feature) && (result = this.mFeatures.get(feature.toLowerCase())) != null) {
                return result.booleanValue();
            }
            Log.w(MiuiFeatureUtils.TAG, "Failed to get feature " + feature + " for current package ");
            return defaultValue;
        }
    }

    private static class ConfigReader {
        private HashMap<String, HashMap<String, Boolean>> mConfigResult = null;
        private HashMap<String, Boolean> mCurrentConfig = null;
        private String mCurrentGroupName;
        private String mPath = null;

        public ConfigReader(String path) {
            this.mPath = path;
        }

        public boolean parse() {
            if (!TextUtils.isEmpty(this.mPath)) {
                return parseInternal();
            }
            return false;
        }

        public HashMap<String, HashMap<String, Boolean>> getConfigResult() {
            return this.mConfigResult;
        }

        private boolean parseInternal() {
            BufferedReader reader = null;
            boolean finished = false;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(this.mPath));
                while (true) {
                    String readLine = reader2.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    parseLine(line);
                }
                finished = true;
                try {
                    reader2.close();
                } catch (IOException e) {
                }
            } catch (IOException e2) {
                Log.e(MiuiFeatureUtils.TAG, "Failed to parse feature file " + this.mPath + ", error : " + e2.toString());
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
            return finished;
        }

        private void parseLine(String line) {
            int equalSignPos;
            String line2 = removeComment(line.trim());
            if (!TextUtils.isEmpty(line2)) {
                if (matchGroup(line2)) {
                    this.mCurrentGroupName = line2.substring(1, line2.length() - 1).toLowerCase().trim();
                    if (TextUtils.isEmpty(this.mCurrentGroupName)) {
                        this.mCurrentConfig = null;
                        return;
                    }
                    if (this.mConfigResult == null) {
                        this.mConfigResult = new HashMap<>();
                    }
                    this.mCurrentConfig = this.mConfigResult.get(this.mCurrentGroupName);
                    if (this.mCurrentConfig == null) {
                        this.mCurrentConfig = new HashMap<>();
                        this.mConfigResult.put(this.mCurrentGroupName, this.mCurrentConfig);
                    }
                } else if (this.mCurrentConfig != null && (equalSignPos = findEqualSignPos(line2)) >= 1 && equalSignPos != line2.length() - 1) {
                    boolean result = null;
                    String key = line2.substring(0, equalSignPos).toLowerCase().trim();
                    String value = line2.substring(equalSignPos + 1, line2.length()).toLowerCase().trim();
                    if (value.equals("yes") || value.equals("y") || value.equals("true") || value.equals("t")) {
                        result = true;
                    } else if (value.equals("no") || value.equals(MiCloudSmsCmd.TYPE_NOISE) || value.equals("false") || value.equals("f")) {
                        result = false;
                    }
                    if (result != null) {
                        this.mCurrentConfig.put(key, result);
                    }
                }
            }
        }

        private boolean matchGroup(String target) {
            if (!TextUtils.isEmpty(target) && target.startsWith("[") && target.endsWith("]")) {
                return true;
            }
            return false;
        }

        private int findEqualSignPos(String target) {
            if (TextUtils.isEmpty(target)) {
                return -1;
            }
            return target.indexOf("=");
        }

        private String removeComment(String target) {
            if (TextUtils.isEmpty(target)) {
                return null;
            }
            int pos = target.indexOf("#");
            if (pos < 0) {
                return target;
            }
            return target.substring(0, pos);
        }
    }

    private static void init() {
        if (new File(DEFAULT_CONFIG_FILE_PATH).exists() && new File(LITE_CONFIG_FILE_PATH).exists()) {
            sIsLiteModeSupported = true;
        }
        String path = SystemProperties.get(PRPPERTY);
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            sConfigFilePath = path;
        }
        if (TextUtils.isEmpty(sConfigFilePath) && new File(DEFAULT_CONFIG_FILE_PATH).exists()) {
            sConfigFilePath = DEFAULT_CONFIG_FILE_PATH;
        }
        if (!TextUtils.isEmpty(sConfigFilePath)) {
            ConfigReader reader = new ConfigReader(sConfigFilePath);
            if (reader.parse()) {
                sConfigResult = reader.getConfigResult();
                HashMap<String, HashMap<String, Boolean>> hashMap = sConfigResult;
                if (hashMap != null) {
                    sSystemConfigResult = hashMap.get(SYSTEM_KEY);
                    sMiuisdkConfigResult = sConfigResult.get(MIUISDK_KEY);
                }
                if (LITE_CONFIG_FILE_PATH.equals(sConfigFilePath)) {
                    sIsLiteMode = true;
                }
                Log.v(TAG, "Loaded and parsed feature configure file successfully");
            }
        }
    }

    public static boolean isSystemFeatureSupported(String feature, boolean defaultValue) {
        Boolean result;
        if (TextUtils.isEmpty(feature)) {
            return defaultValue;
        }
        HashMap<String, Boolean> hashMap = sSystemConfigResult;
        if (hashMap != null && (result = hashMap.get(feature.toLowerCase())) != null) {
            return result.booleanValue();
        }
        Log.w(TAG, "Failed to get system feature " + feature);
        return defaultValue;
    }

    public static boolean isLocalFeatureSupported(Context context, String feature, boolean defaultValue) {
        HashMap<String, Boolean> features;
        Boolean result;
        if (TextUtils.isEmpty(feature)) {
            return defaultValue;
        }
        String pkg = null;
        if (context != null) {
            pkg = context.getPackageName();
        }
        if (sConfigResult != null && !TextUtils.isEmpty(pkg) && (features = sConfigResult.get(pkg)) != null && (result = features.get(feature.toLowerCase())) != null) {
            return result.booleanValue();
        }
        Log.w(TAG, "Failed to get feature " + feature + " for package " + pkg);
        return defaultValue;
    }

    public static Features getLocalFeature(Context context) {
        String pkg = null;
        if (context != null) {
            pkg = context.getPackageName();
        }
        if (sConfigResult != null && !TextUtils.isEmpty(pkg)) {
            return new Features(sConfigResult.get(pkg));
        }
        Log.w(TAG, "Failed to get feature set for package " + pkg);
        return null;
    }

    public static boolean isLiteMode() {
        return sIsLiteMode;
    }

    public static boolean isLiteModeSupported() {
        return sIsLiteModeSupported;
    }

    public static void setMiuisdkProperties() {
        HashMap<String, Boolean> hashMap = sMiuisdkConfigResult;
        if (hashMap != null) {
            try {
                for (Map.Entry<String, Boolean> entry : hashMap.entrySet()) {
                    String key = entry.getKey();
                    Boolean value = entry.getValue();
                    if (!TextUtils.isEmpty(key) && value != null) {
                        SystemProperties.set(MIUISDK_FEATURE_PREFIX + key, value.toString());
                    }
                }
            } catch (Exception e) {
                Log.v(TAG, "Failed to set miui sdk features.");
            }
        }
    }
}
