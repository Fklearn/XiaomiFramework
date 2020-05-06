package com.miui.earthquakewarning.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;

public class VibratorUtil {
    @SuppressLint({"MissingPermission"})
    public static void cancel(Context context) {
        ((Vibrator) context.getSystemService("vibrator")).cancel();
    }

    @SuppressLint({"MissingPermission"})
    public static void vibrate(Context context, long j) {
        ((Vibrator) context.getSystemService("vibrator")).vibrate(j);
    }

    @SuppressLint({"MissingPermission"})
    public static void vibrate(Context context, long[] jArr, boolean z) {
        ((Vibrator) context.getSystemService("vibrator")).vibrate(jArr, z ? 1 : -1);
    }
}
