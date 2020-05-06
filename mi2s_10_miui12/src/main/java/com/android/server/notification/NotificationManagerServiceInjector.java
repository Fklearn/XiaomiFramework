package com.android.server.notification;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.miui.daemon.performance.PerfShielderManager;
import miui.os.Build;

public class NotificationManagerServiceInjector extends NotificationManagerServiceInjectorBase {
    private static final String TAG = "NotificationService";

    static void onNotificationClick(int callingUid, int callingPid, final String postPackage, Notification notification) {
        PendingIntent pi = notification.contentIntent;
        if (pi != null) {
            final Intent intent = pi.getIntent();
            final long time = SystemClock.uptimeMillis();
            BackgroundThread.getHandler().post(new Runnable() {
                public void run() {
                    try {
                        PerfShielderManager.getService().reportNotificationClick(postPackage, intent, time);
                    } catch (RemoteException e) {
                        Log.w(NotificationManagerServiceInjector.TAG, "error when reportNotificationClick");
                    }
                }
            });
        }
    }

    static void calculateAudiblyAlerted(AudioManager mAudioManager, NotificationRecord record) {
        boolean z = true;
        if (!SystemProperties.getBoolean("persist.sys.miui_optimization", true)) {
            boolean buzz = false;
            boolean beep = false;
            boolean aboveThreshold = record.getImportance() >= 3;
            Uri soundUri = record.getSound();
            boolean hasValidVibrate = record.getVibration() != null;
            if (aboveThreshold && mAudioManager != null) {
                if (soundUri != null && !Uri.EMPTY.equals(soundUri)) {
                    beep = true;
                }
                boolean ringerModeSilent = mAudioManager.getRingerModeInternal() == 0;
                if (hasValidVibrate && !ringerModeSilent) {
                    buzz = true;
                }
            }
            if (!buzz && !beep) {
                z = false;
            }
            record.setAudiblyAlerted(z);
        }
    }

    static void checkFullScreenIntent(Notification notification, AppOpsManager appOpsManager, int uid, String packageName) {
        if (!Build.IS_INTERNATIONAL_BUILD && notification.fullScreenIntent != null && appOpsManager.noteOpNoThrow(10021, uid, packageName) != 0) {
            Slog.i(TAG, "MIUILOG- Permission Denied Activity : " + notification.fullScreenIntent + " pkg : " + packageName + " uid : " + uid);
            notification.fullScreenIntent = null;
        }
    }
}
