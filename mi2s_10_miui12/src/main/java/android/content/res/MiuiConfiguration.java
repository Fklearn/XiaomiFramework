package android.content.res;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import miui.os.SystemProperties;
import miui.reflect.Field;
import miui.telephony.phonenumber.Prefix;
import miui.util.FeatureParser;
import miui.util.Log;

public class MiuiConfiguration implements Comparable<MiuiConfiguration> {
    private static long BASE_RESTART_FLAG = 25;
    public static final String CONTACTS_PKG_NAME = "com.android.contacts";
    public static final String KEY_VAR_FONT_SCALE = "key_var_font_scale";
    public static final String LAUNCHER_PKG_NAME = "com.miui.home";
    public static final String MMS_PKG_NAME = "com.android.mms";
    public static final String SYSTEMUI_PKG_NAME = "com.android.systemui";
    public static final long SYSTEM_INTEREST_CHANGE_FLAG = 268466329;
    private static final String TAG = "MiuiConfiguration";
    public static final long THEME_FLAG_ALARM = 1024;
    public static final long THEME_FLAG_ALARMSTYLE = 1048576;
    public static final long THEME_FLAG_AUDIO_EFFECT = 32768;
    public static final long THEME_FLAG_BOOT_ANIMATION = 32;
    public static final long THEME_FLAG_BOOT_AUDIO = 64;
    @Deprecated
    public static final long THEME_FLAG_CLOCK = 65536;
    public static final long THEME_FLAG_CLOCK_1x2 = 8388608;
    public static final long THEME_FLAG_CLOCK_2x2 = 16777216;
    public static final long THEME_FLAG_CLOCK_2x4 = 33554432;
    public static final long THEME_FLAG_CONTACT = 2048;
    public static final long THEME_FLAG_FONT = 16;
    public static final long THEME_FLAG_FRAMEWORK = 1;
    public static final long THEME_FLAG_FREE_HOME = 4194304;
    public static final long THEME_FLAG_FREE_HOME_DEPRECATED = 2097152;
    public static final long THEME_FLAG_ICON = 8;
    public static final long THEME_FLAG_LAST = 134217728;
    public static final long THEME_FLAG_LAUNCHER = 16384;
    public static final long THEME_FLAG_LOCKSCREEN = 4;
    public static final long THEME_FLAG_LOCKSTYLE = 4096;
    public static final long THEME_FLAG_MIWALLPAPER = 524288;
    public static final long THEME_FLAG_MMS = 128;
    public static final long THEME_FLAG_NOTIFICATION = 512;
    @Deprecated
    public static final long THEME_FLAG_PHOTO_FRAME = 131072;
    public static final long THEME_FLAG_PHOTO_FRAME_2x2 = 67108864;
    public static final long THEME_FLAG_PHOTO_FRAME_2x4 = 134217728;
    public static final long THEME_FLAG_PHOTO_FRAME_4x4 = 262144;
    public static final long THEME_FLAG_RINGTONE = 256;
    public static final long THEME_FLAG_STATUSBAR = 8192;
    public static final long THEME_FLAG_THIRD_APP = 268435456;
    public static final long THEME_FLAG_VAR_FONT = 536870912;
    public static final long THEME_FLAG_WALLPAPER = 2;
    public static final long THEME_FONT_FLAGS = 16;
    public static final int UI_MODE_TYPE_SCALE_EXTRAL_SMALL = 10;
    public static final int UI_MODE_TYPE_SCALE_GODZILLA = 11;
    public static final int UI_MODE_TYPE_SCALE_HUGE = 15;
    public static final int UI_MODE_TYPE_SCALE_LARGE = 14;
    public static final int UI_MODE_TYPE_SCALE_MEDIUM = 13;
    public static final int UI_MODE_TYPE_SCALE_SMALL = 12;
    private static int sForceScreenLayoutSize = -1;
    private static boolean sHadReadForceScreenLayoutSize = false;
    public Bundle extraData = new Bundle();
    private int lastFontThemeChanged;
    public int themeChanged;
    public long themeChangedFlags;

    public void setTo(MiuiConfiguration o) {
        this.themeChanged = o.themeChanged;
        this.themeChangedFlags = o.themeChangedFlags;
        this.lastFontThemeChanged = o.lastFontThemeChanged;
        setExtraData(o.extraData);
    }

    public String toString() {
        return " themeChanged=" + this.themeChanged + " themeChangedFlags=" + this.themeChangedFlags + " extraData = " + this.extraData;
    }

    public void setToDefaults() {
        this.themeChanged = 0;
        this.themeChangedFlags = 0;
        this.lastFontThemeChanged = 0;
        this.extraData.clear();
    }

    public int updateFrom(MiuiConfiguration delta) {
        int i = this.themeChanged;
        int i2 = delta.themeChanged;
        if (i >= i2) {
            return 0;
        }
        int changed = 0 | Integer.MIN_VALUE;
        this.themeChanged = i2;
        this.themeChangedFlags = delta.themeChangedFlags;
        this.lastFontThemeChanged = delta.lastFontThemeChanged;
        setExtraData(delta.extraData);
        return changed;
    }

    public int diff(MiuiConfiguration delta) {
        if (this.themeChanged < delta.themeChanged) {
            return 0 | Integer.MIN_VALUE;
        }
        return 0;
    }

    public static boolean needNewResources(int configChanges) {
        return (Integer.MIN_VALUE & configChanges) != 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.themeChanged);
        dest.writeInt(this.lastFontThemeChanged);
        dest.writeLong(this.themeChangedFlags);
        dest.writeBundle(this.extraData);
    }

    public void readFromParcel(Parcel source) {
        this.themeChanged = source.readInt();
        this.lastFontThemeChanged = source.readInt();
        this.themeChangedFlags = source.readLong();
        this.extraData = source.readBundle();
    }

    public int compareTo(MiuiConfiguration that) {
        int n = this.themeChanged - that.themeChanged;
        if (n != 0) {
            return n;
        }
        return n;
    }

    public int hashCode() {
        return this.themeChanged + ((int) this.themeChangedFlags);
    }

    public void updateTheme(long changedFlags) {
        this.themeChanged++;
        this.themeChangedFlags = changedFlags;
        if ((this.themeChangedFlags & 16) != 0) {
            this.lastFontThemeChanged = this.themeChanged;
        }
    }

    public boolean checkFontChange(int sinceThemeChanged) {
        return sinceThemeChanged >= 0 && sinceThemeChanged < this.lastFontThemeChanged;
    }

    private void setExtraData(Bundle otherData) {
        this.extraData.clear();
        if (otherData != null) {
            this.extraData.putAll(otherData);
        }
    }

    public static void sendThemeConfigurationChangeMsg(long changeFlag, Bundle data) {
        if (changeFlag != 0) {
            try {
                Configuration curConfig = ActivityManagerNative.getDefault().getConfiguration();
                MiuiConfiguration miuiConfig = getExtraConfig(curConfig);
                miuiConfig.updateTheme(changeFlag);
                if (data != null) {
                    miuiConfig.extraData.putAll(data);
                }
                try {
                    IActivityManager.class.getDeclaredMethod("updateConfiguration", new Class[]{Configuration.class}).invoke(ActivityManagerNative.getDefault(), new Object[]{curConfig});
                } catch (Exception e) {
                    Log.w(TAG, "updateConfiguration failed", e);
                }
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void sendThemeConfigurationChangeMsg(long changeFlag) {
        sendThemeConfigurationChangeMsg(changeFlag, (Bundle) null);
    }

    private static MiuiConfiguration getExtraConfig(Configuration configuration) {
        try {
            return (MiuiConfiguration) Field.of(Configuration.class, "extraConfig", MiuiConfiguration.class).get(configuration);
        } catch (Exception e) {
            Log.w(TAG, "getExtraConfig from Configuration failed", e);
            return null;
        }
    }

    public static boolean needRestartActivity(String packageName, long themeChangeFlags) {
        if (packageName != null) {
            if (packageName.startsWith(LAUNCHER_PKG_NAME)) {
                return needRestartLauncher(themeChangeFlags);
            }
            if (packageName.startsWith(MMS_PKG_NAME)) {
                return needRestartMms(themeChangeFlags);
            }
            if (packageName.startsWith(CONTACTS_PKG_NAME)) {
                return needRestartContacts(themeChangeFlags);
            }
        }
        return needRestart3rdApp(themeChangeFlags);
    }

    public static boolean needRestartLauncher(long themeChangeFlags) {
        return ((BASE_RESTART_FLAG | THEME_FLAG_LAUNCHER) & themeChangeFlags) != 0;
    }

    public static boolean needRestartMms(long themeChangeFlags) {
        return ((BASE_RESTART_FLAG | 128) & themeChangeFlags) != 0;
    }

    public static boolean needRestartContacts(long themeChangeFlags) {
        return ((BASE_RESTART_FLAG | THEME_FLAG_CONTACT) & themeChangeFlags) != 0;
    }

    public static boolean needRestart3rdApp(long themeChangeFlags) {
        return ((BASE_RESTART_FLAG | THEME_FLAG_THIRD_APP) & themeChangeFlags) != 0;
    }

    public static boolean needRestartStatusBar(long themeChangeFlags) {
        return (((BASE_RESTART_FLAG | THEME_FLAG_STATUSBAR) | THEME_FLAG_LOCKSTYLE) & themeChangeFlags) != 0;
    }

    public static int getScaleMode() {
        try {
            Configuration config = ActivityManagerNative.getDefault().getConfiguration();
            if (config == null) {
                return 1;
            }
            int scale = config.uiMode & 15;
            if (scale == 10 || scale == 12 || scale == 13 || scale == 14 || scale == 15 || scale == 11) {
                return scale;
            }
            return 1;
        } catch (RemoteException e) {
            return 1;
        }
    }

    public static int resetScreenLayoutSize(int defaultScreenLayoutSize) {
        if (!sHadReadForceScreenLayoutSize) {
            int i = 1;
            sHadReadForceScreenLayoutSize = true;
            String forceScreenLayoutSizeConfig = SystemProperties.get("ro.miui.screen_layout_size", Prefix.EMPTY);
            if (!forceScreenLayoutSizeConfig.equals("small")) {
                if (forceScreenLayoutSizeConfig.equals("normal")) {
                    i = 2;
                } else if (forceScreenLayoutSizeConfig.equals("large")) {
                    i = 3;
                } else {
                    i = forceScreenLayoutSizeConfig.equals("xlarge") ? 4 : -1;
                }
            }
            sForceScreenLayoutSize = i;
        }
        int i2 = sForceScreenLayoutSize;
        return i2 == -1 ? defaultScreenLayoutSize : i2;
    }

    public static float getFontScale(int fontUiMode) {
        switch (fontUiMode) {
            case 10:
                return FeatureParser.getFloat("extral_smallui_font_scale", 0.81f).floatValue();
            case 11:
                return FeatureParser.getFloat("godzillaui_font_scale", 1.5f).floatValue();
            case 12:
                return FeatureParser.getFloat("smallui_font_scale", 0.92f).floatValue();
            case 13:
                return FeatureParser.getFloat("mediumui_font_scale", 1.0f).floatValue();
            case 14:
                return FeatureParser.getFloat("largeui_font_scale", 1.17f).floatValue();
            case 15:
                return FeatureParser.getFloat("hugeui_font_scale", 1.33f).floatValue();
            default:
                return 1.0f;
        }
    }
}
