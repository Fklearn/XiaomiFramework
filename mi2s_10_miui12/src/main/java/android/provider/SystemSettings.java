package android.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;
import com.miui.system.internal.R;
import java.util.Iterator;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.telephony.phonenumber.Prefix;
import miui.text.ChinesePinyinConverter;
import miui.util.FeatureParser;
import miui.util.Utf8TextUtils;

public class SystemSettings {
    private static final String TAG = "SystemSettings";
    private static final String UTF8 = "UTF-8";

    public static class System {
        public static final String DARKEN_WALLPAPER_UNDER_DARK_MODE = "darken_wallpaper_under_dark_mode";
        private static final String E10_DEVICE = "beryllium";
        private static final String INDIA = "INDIA";
        public static final String LOCK_WALLPAPER_PROVIDER_AUTHORITY = "lock_wallpaper_provider_authority";
        public static final String PERSIST_SYS_DEVICE_NAME = "persist.sys.device_name";
        public static final String RO_MARKET_NAME = "ro.product.marketname";
        public static final String STATUS_BAR_WINDOW_LOADED = "status_bar_window_loaded";

        public static String getDeviceName(Context context) {
            String defaultName = SystemProperties.get(RO_MARKET_NAME, (String) null);
            if (defaultName == null || defaultName.length() == 0) {
                defaultName = context.getString(getDefaultNameRes());
            }
            return SystemProperties.get(PERSIST_SYS_DEVICE_NAME, defaultName);
        }

        private static int getDefaultNameRes() {
            if (FeatureParser.getBoolean("is_redmi", false)) {
                return R.string.device_redmi;
            }
            if (FeatureParser.getBoolean("is_poco", false)) {
                return R.string.device_poco;
            }
            if (FeatureParser.getBoolean("is_hongmi", false)) {
                return R.string.device_hongmi;
            }
            if (FeatureParser.getBoolean("is_xiaomi", false)) {
                if (!E10_DEVICE.equals(SystemProperties.get("ro.product.device"))) {
                    return R.string.device_xiaomi;
                }
                if (SystemProperties.get("ro.boot.hwc", Prefix.EMPTY).contains(INDIA)) {
                    return R.string.device_poco_india;
                }
                return R.string.device_poco_global;
            } else if (FeatureParser.getBoolean("is_pad", false)) {
                return R.string.device_pad;
            } else {
                return R.string.miui_device_name;
            }
        }

        public static void setDeviceName(Context context, String deviceName) {
            SystemProperties.set(PERSIST_SYS_DEVICE_NAME, deviceName);
            setNetHostName(context);
        }

        public static void setNetHostName(Context context) {
            String hostName;
            String netHostName = SystemProperties.get("net.hostname");
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(Build.MODEL);
            nameBuilder.append("-");
            Iterator<ChinesePinyinConverter.Token> it = ChinesePinyinConverter.getInstance().get(getDeviceName(context)).iterator();
            while (it.hasNext()) {
                nameBuilder.append(it.next().target);
            }
            String newNetHostName = nameBuilder.toString().replace(" ", Prefix.EMPTY);
            if (!newNetHostName.equals(netHostName) && (hostName = Utf8TextUtils.truncateByte(newNetHostName, 20)) != null) {
                SystemProperties.set("net.hostname", hostName);
            }
        }

        public static boolean getBoolean(ContentResolver resolver, String name, boolean defValue) {
            return Settings.System.getInt(resolver, name, defValue) != 0;
        }
    }

    public static class Secure {
        public static final String PRIVACY_MODE_ENABLED = "privacy_mode_enabled";
        public static final String SCREEN_BUTTONS_STATE = "screen_buttons_state";

        public static boolean isCtaSupported(ContentResolver cr) {
            return false;
        }

        public static Cursor checkPrivacyAndReturnCursor(Context context) {
            boolean enabled = false;
            if (1 == Settings.Secure.getInt(context.getContentResolver(), PRIVACY_MODE_ENABLED, 0)) {
                enabled = true;
            }
            if (enabled) {
                return new MatrixCursor(new String[]{"_id"});
            }
            return null;
        }
    }
}
