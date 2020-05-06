package miui.cloud.util;

import android.util.Log;
import java.util.Arrays;

public class DeviceFeatureUtils {
    private static final String TAG = "DeviceFeatureUtils";

    public static String[] getAllDeviceFeaturesOrNull() {
        StringBuilder sb;
        try {
            Class<?> cls = Class.forName("miui.cloud.DeviceFeature");
            return (String[]) cls.getField("features").get(cls);
        } catch (ClassNotFoundException e) {
            e = e;
            sb = new StringBuilder();
            sb.append("failed to find features from miclousdk, ");
            sb.append(e);
            Log.e(TAG, sb.toString());
            return null;
        } catch (IllegalAccessException e2) {
            e = e2;
            sb = new StringBuilder();
            sb.append("failed to find features from miclousdk, ");
            sb.append(e);
            Log.e(TAG, sb.toString());
            return null;
        } catch (NoSuchFieldException e3) {
            e = e3;
            sb = new StringBuilder();
            sb.append("failed to find features from miclousdk, ");
            sb.append(e);
            Log.e(TAG, sb.toString());
            return null;
        }
    }

    public static boolean hasDeviceFeature(String str) {
        String[] allDeviceFeaturesOrNull = getAllDeviceFeaturesOrNull();
        if (allDeviceFeaturesOrNull == null) {
            return false;
        }
        return Arrays.asList(allDeviceFeaturesOrNull).contains(str);
    }
}
