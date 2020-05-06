package com.miui.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.job.controllers.JobStatus;

public class GreenGuardManagerService {
    public static final String GREEN_KID_AGENT_PKG_NAME = "com.miui.greenguard";
    public static final String GREEN_KID_SERVICE = "com.miui.greenguard.service.GreenKidService";
    private static final String TAG = "GreenKidManagerService";
    /* access modifiers changed from: private */
    public static long TIME_DELAY = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
    private static ServiceConnection mGreenGuardServiceConnection;
    /* access modifiers changed from: private */
    public static Handler mHandler;

    public static void init(Context context) {
        if (!isGreenKidActive(context) && !isGreenKidNeedWipe(context)) {
            disableAgentProcess(context);
        }
    }

    private static void disableAgentProcess(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            if (packageManager.getApplicationInfo(GREEN_KID_AGENT_PKG_NAME, 8192) != null) {
                packageManager.setApplicationEnabledSetting(GREEN_KID_AGENT_PKG_NAME, 2, 0);
                Slog.i(TAG, "Disable GreenGuard agent : [ com.miui.greenguard] .");
            }
        } catch (Exception e) {
            Slog.e(TAG, "Disable greenGuard agent : [ com.miui.greenguard] failed , package not install", e);
        }
    }

    private static boolean isGreenKidActive(Context context) {
        return MiuiSettings.Secure.isGreenKidActive(context.getContentResolver());
    }

    private static boolean isGreenKidNeedWipe(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_url_green_guard_sdk_need_clear_data", 0) == 1;
    }

    public static void startWatchGreenguardProcess(Context context) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        String callingPackageName = ExtraActivityManagerService.getPackageNameByPid(callingPid);
        if (UserHandle.getAppId(callingUid) == 1000 || GREEN_KID_AGENT_PKG_NAME.equals(callingPackageName)) {
            startWatchGreenguardProcessInner(context);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Permission Denial from pid=");
        sb.append(callingPid);
        sb.append(", uid=");
        sb.append(callingUid);
        sb.append("callingPkg:");
        sb.append(callingPackageName == null ? "" : callingPackageName);
        String msg = sb.toString();
        Slog.w(TAG, msg);
        throw new SecurityException(msg);
    }

    /* access modifiers changed from: private */
    public static synchronized void startWatchGreenguardProcessInner(Context context) {
        synchronized (GreenGuardManagerService.class) {
            if (mHandler == null) {
                HandlerThread handlerThread = new HandlerThread(TAG, 10);
                handlerThread.start();
                mHandler = new Handler(handlerThread.getLooper());
                mGreenGuardServiceConnection = new GreenguardServiceConn(context);
            }
            Log.d(TAG, "startWatchGreenguardProcess");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(GREEN_KID_AGENT_PKG_NAME, GREEN_KID_SERVICE));
            context.bindService(intent, mGreenGuardServiceConnection, 1);
        }
    }

    static class GreenguardServiceConn implements ServiceConnection {
        /* access modifiers changed from: private */
        public Context mContext;

        public GreenguardServiceConn(Context context) {
            this.mContext = context;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(GreenGuardManagerService.TAG, "On GreenKidService Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(GreenGuardManagerService.TAG, "On GreenKidService Disconnected , schedule restart it in 10s.");
            GreenGuardManagerService.mHandler.postDelayed(new Runnable() {
                public void run() {
                    GreenGuardManagerService.startWatchGreenguardProcessInner(GreenguardServiceConn.this.mContext);
                }
            }, GreenGuardManagerService.TIME_DELAY);
        }
    }
}
