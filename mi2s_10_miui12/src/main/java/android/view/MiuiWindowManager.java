package android.view;

import android.content.Context;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.WindowManager;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class MiuiWindowManager {
    private static final int TYPE_LAYER_MULTIPLIER = 10000;
    private static final int TYPE_LAYER_OFFSET = 1000;
    private static SparseIntArray sTypeLayers;

    public static class LayoutParams extends WindowManager.LayoutParams {
        public static final int EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN = 4194304;
        public static final int EXTRA_FLAG_DISABLE_FOD_ICON = 32768;
        public static final int EXTRA_FLAG_ENABLE_NOTCH_CONFIG = 256;
        public static final int EXTRA_FLAG_FINDDEVICE_KEYGUARD = 2048;
        public static final int EXTRA_FLAG_FULLSCREEN_BLURSURFACE = 67108864;
        public static final int EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION = 16384;
        public static final int EXTRA_FLAG_IS_NO_SCREENSHOT = 8388608;
        public static final int EXTRA_FLAG_IS_PIP_SCREEN_PROJECTION = 33554432;
        public static final int EXTRA_FLAG_IS_SCREEN_PROJECTION = 16777216;
        public static final int EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE = 1024;
        public static final int EXTRA_FLAG_LAYOUT_NOTCH_PORTRAIT = 512;
        public static final int EXTRA_FLAG_NAVIGATION_BAR_DARK_MODE = 64;
        public static final int EXTRA_FLAG_SHOW_ON_FINDDEVICE_KEYGUARD = 4096;
        public static final int EXTRA_FLAG_STATUS_BAR_DARK_MODE = 16;
        public static final int EXTRA_FLAG_STATUS_BAR_HIDE = 32;
        public static final int EXTRA_FLAG_STATUS_BAR_LOW_PROFILE = 2;
        public static final int EXTRA_FLAG_STATUS_BAR_MASK = 59;
        public static final int EXTRA_FLAG_STATUS_BAR_SIMPLE_MODE = 8;
        public static final int EXTRA_FLAG_STATUS_BAR_TRANSPARENT = 1;
        public static final int PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP = 134217728;
    }

    private static void loadTypeLayerIfNeed(Context context) {
        SparseIntArray sparseIntArray = sTypeLayers;
        if (sparseIntArray == null || sparseIntArray.size() <= 0) {
            String strJsonTypeLayer = MiuiSettings.System.getStringForUser(context.getContentResolver(), "window_type_layer", 0);
            if (!TextUtils.isEmpty(strJsonTypeLayer)) {
                try {
                    JSONObject jsonTypeLayer = new JSONObject(strJsonTypeLayer);
                    sTypeLayers = new SparseIntArray();
                    Iterator<String> iterator = jsonTypeLayer.keys();
                    while (iterator.hasNext()) {
                        String strType = iterator.next();
                        sTypeLayers.put(Integer.parseInt(strType), jsonTypeLayer.getInt(strType));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Window type layer has not set to setting.");
            }
        }
    }

    public static int getLayer(Context context, int type) {
        loadTypeLayerIfNeed(context);
        int typeLayer = 2;
        SparseIntArray sparseIntArray = sTypeLayers;
        if (sparseIntArray != null) {
            typeLayer = sparseIntArray.get(type, 2);
        }
        return (typeLayer * 10000) + 1000;
    }
}
