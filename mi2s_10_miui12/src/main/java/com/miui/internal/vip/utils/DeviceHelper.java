package com.miui.internal.vip.utils;

import android.text.TextUtils;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.security.DigestUtils;
import miui.util.DeviceUtils;
import miui.util.HashUtils;

public class DeviceHelper {
    public static final String MIUI_VERSION = SystemProperties.get("ro.build.version.incremental");
    public static final String PRODUCT = SystemProperties.get("ro.product.device");
    private static String TAG = "DeviceHelper";
    private static volatile String sDeviceId = null;

    public static String getDeviceId() {
        if (TextUtils.isEmpty(sDeviceId)) {
            if (Utils.isInMainThread()) {
                Utils.logW("DeviceUtils.getDeviceId shouldn't be invoked in main thread", new Object[0]);
            }
            try {
                synchronized (DeviceHelper.class) {
                    if (TextUtils.isEmpty(sDeviceId)) {
                        String rawId = DeviceUtils.getDeviceId(Utils.getContext());
                        if (!TextUtils.isEmpty(rawId)) {
                            sDeviceId = Utils.bytesToHex(DigestUtils.get(rawId.getBytes(), HashUtils.MD5));
                        }
                    }
                }
            } catch (Exception e) {
                Utils.logW("getDeviceId failed, %s", e);
            }
        }
        return sDeviceId;
    }

    public static boolean isPad() {
        return Build.IS_TABLET;
    }
}
