package com.miui.gamebooster.videobox.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import b.b.o.g.e;
import com.google.android.exoplayer2.util.MimeTypes;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static AudioManager f5201a;

    public static boolean a() {
        try {
            Class<?> cls = Class.forName("android.media.AudioSystem");
            return ((Integer) e.a(cls, Integer.TYPE, "getDeviceConnectionState", (Class<?>[]) new Class[]{Integer.TYPE, String.class}, Integer.valueOf(((Integer) e.a(cls, "DEVICE_OUT_USB_HEADSET", Integer.TYPE)).intValue()), "")).intValue() == ((Integer) e.a(cls, "DEVICE_STATE_AVAILABLE", Integer.TYPE)).intValue();
        } catch (Exception e) {
            Log.e("HeadSetStatusMonitor", "isUsbHeadsetOn: " + e.toString());
            return false;
        }
    }

    public static boolean a(Context context) {
        try {
            if (f5201a == null) {
                f5201a = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            }
            if (f5201a != null) {
                return f5201a.isWiredHeadsetOn();
            }
            return false;
        } catch (Exception e) {
            Log.e("HeadSetStatusMonitor", "isWiredHeadsetOn: " + e.toString());
            return false;
        }
    }
}
