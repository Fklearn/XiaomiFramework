package com.miui.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.android.server.job.controllers.JobStatus;
import com.miui.server.ISplashPackageCheckListener;
import com.miui.server.ISplashScreenService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SplashScreenServiceDelegate {
    private static final String ACTION_DEBUG_OFF = "miui.intent.action.ad.DEBUG_OFF";
    private static final String ACTION_DEBUG_ON = "miui.intent.action.ad.DEBUG_ON";
    private static final long DELAY_BIND_AFTER_BOOT_COMPLETE = 120000;
    private static final String KEY_API_VERSION = "apiVersion";
    private static final long MAX_DELAY_TIME = 3600000;
    private static final String MIUI_GENERAL_PERMISSION = "miui.permission.USE_INTERNAL_GENERAL_API";
    private static final int MSG_REBIND = 1;
    public static final String SPLASHSCREEN_ACTIVITY = "com.miui.systemAdSolution.splashscreen.SplashActivity";
    private static final String SPLASHSCREEN_CLASS = "com.miui.systemAdSolution.splashscreen.SplashScreenService";
    public static final String SPLASHSCREEN_GLOBAL_PACKAGE = "com.miui.msa.global";
    public static final String SPLASHSCREEN_PACKAGE = "com.miui.systemAdSolution";
    private static final String TAG = "SplashScreenServiceDelegate";
    private static final int VALUE_API_VERSION = 2;
    /* access modifiers changed from: private */
    public static boolean sDebug;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient() {
        public void binderDied() {
            SplashScreenServiceDelegate.this.logI("SplashScreenService binderDied!");
            SplashScreenServiceDelegate.this.delayToRebindService();
        }
    };
    private long mDelayTime;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                SplashScreenServiceDelegate.this.bindService();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mRebindCount;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                    SplashScreenServiceDelegate.this.logI("Boot completed, delay to bind SplashScreenService", true);
                    SplashScreenServiceDelegate.this.delayToBindServiceAfterBootCompleted();
                } else if (SplashScreenServiceDelegate.ACTION_DEBUG_ON.equals(action)) {
                    SplashScreenServiceDelegate.this.logI("Debug On", true);
                    boolean unused = SplashScreenServiceDelegate.sDebug = true;
                } else if (SplashScreenServiceDelegate.ACTION_DEBUG_OFF.equals(action)) {
                    SplashScreenServiceDelegate.this.logI("Debug Off", true);
                    boolean unused2 = SplashScreenServiceDelegate.sDebug = false;
                }
            }
        }
    };
    private int mSeverity;
    /* access modifiers changed from: private */
    public Map<String, SplashPackageCheckInfo> mSplashPackageCheckInfoMap = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public ISplashPackageCheckListener mSplashPackageCheckListener = new ISplashPackageCheckListener.Stub() {
        public void updateSplashPackageCheckInfoList(List<SplashPackageCheckInfo> splashPackageCheckInfos) throws RemoteException {
            try {
                SplashScreenServiceDelegate.this.logI("updateSplashPackageCheckInfoList");
                SplashScreenServiceDelegate.this.mSplashPackageCheckInfoMap.clear();
                if (splashPackageCheckInfos == null) {
                    return;
                }
                if (!splashPackageCheckInfos.isEmpty()) {
                    for (SplashPackageCheckInfo info : splashPackageCheckInfos) {
                        updateSplashPackageCheckInfo(info);
                    }
                }
            } catch (Exception e) {
                SplashScreenServiceDelegate.this.logE("updateSplashPackageCheckInfoList exception", e);
            }
        }

        public void updateSplashPackageCheckInfo(SplashPackageCheckInfo splashPackageCheckInfo) throws RemoteException {
            try {
                if (SplashScreenServiceDelegate.this.checkSplashPackageCheckInfo(splashPackageCheckInfo)) {
                    SplashScreenServiceDelegate splashScreenServiceDelegate = SplashScreenServiceDelegate.this;
                    splashScreenServiceDelegate.logI("Valid " + splashPackageCheckInfo);
                    SplashScreenServiceDelegate.this.keepSplashPackageCheckInfo(splashPackageCheckInfo);
                    return;
                }
                SplashScreenServiceDelegate splashScreenServiceDelegate2 = SplashScreenServiceDelegate.this;
                splashScreenServiceDelegate2.logI("Invalid " + splashPackageCheckInfo);
            } catch (Exception e) {
                SplashScreenServiceDelegate.this.logE("updateSplashPackageCheckInfo exception", e);
            }
        }
    };
    /* access modifiers changed from: private */
    public final ServiceConnection mSplashScreenConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            SplashScreenServiceDelegate.this.logI("SplashScreenService connected!");
            ISplashScreenService unused = SplashScreenServiceDelegate.this.mSplashScreenService = ISplashScreenService.Stub.asInterface(service);
            long unused2 = SplashScreenServiceDelegate.this.mStartTime = System.currentTimeMillis();
            int unused3 = SplashScreenServiceDelegate.this.mRebindCount = 0;
            SplashScreenServiceDelegate.this.mHandler.removeMessages(1);
            try {
                SplashScreenServiceDelegate.this.mSplashScreenService.asBinder().linkToDeath(SplashScreenServiceDelegate.this.mDeathHandler, 0);
            } catch (Exception e) {
                SplashScreenServiceDelegate.this.logE("linkToDeath exception", e);
            }
            asyncSetSplashPackageCheckListener();
        }

        public void onServiceDisconnected(ComponentName name) {
            SplashScreenServiceDelegate.this.logI("SplashScreenService disconnected!");
            ISplashScreenService unused = SplashScreenServiceDelegate.this.mSplashScreenService = null;
            if (SplashScreenServiceDelegate.this.mContext != null) {
                SplashScreenServiceDelegate.this.mContext.unbindService(SplashScreenServiceDelegate.this.mSplashScreenConnection);
            }
        }

        private void asyncSetSplashPackageCheckListener() {
            BackgroundThread.getHandler().post(new Runnable() {
                public void run() {
                    ISplashScreenService sss = SplashScreenServiceDelegate.this.mSplashScreenService;
                    if (sss != null) {
                        try {
                            SplashScreenServiceDelegate.this.logI("Set splash package check listener");
                            sss.setSplashPackageListener(SplashScreenServiceDelegate.this.mSplashPackageCheckListener);
                        } catch (Exception e) {
                            SplashScreenServiceDelegate.this.logE("asyncSetSplashPackageCheckListener exception", e);
                        }
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public ISplashScreenService mSplashScreenService;
    /* access modifiers changed from: private */
    public long mStartTime;

    public SplashScreenServiceDelegate(Context context) {
        this.mContext = context;
        sDebug = Build.IS_DEBUGGABLE || TextUtils.equals(Build.TYPE, "userdebug");
        logI("Debug " + sDebug);
        registerReceiver();
    }

    private void registerReceiver() {
        logI("Register BOOT_COMPLETED receiver", true);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.OWNER, filter, (String) null, (Handler) null);
        logI("Register debugger receiver", true);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ACTION_DEBUG_ON);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.OWNER, filter2, MIUI_GENERAL_PERMISSION, (Handler) null);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(ACTION_DEBUG_OFF);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.OWNER, filter3, MIUI_GENERAL_PERMISSION, (Handler) null);
        delayToRebindService(600000, false);
    }

    /* access modifiers changed from: private */
    public void bindService() {
        if (this.mContext != null && this.mSplashScreenService == null) {
            try {
                Intent intent = new Intent();
                if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
                    intent.setClassName(SPLASHSCREEN_GLOBAL_PACKAGE, SPLASHSCREEN_CLASS);
                } else {
                    intent.setClassName(SPLASHSCREEN_PACKAGE, SPLASHSCREEN_CLASS);
                }
                intent.putExtra(KEY_API_VERSION, 2);
                if (!this.mContext.bindServiceAsUser(intent, this.mSplashScreenConnection, 5, UserHandle.OWNER)) {
                    logW("Can't bound to SplashScreenService, com.miui.systemAdSolution.splashscreen.SplashScreenService");
                    delayToRebindService();
                    return;
                }
                logI("SplashScreenService started");
            } catch (Exception e) {
                logE("Can not start splash screen service!", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void delayToBindServiceAfterBootCompleted() {
        delayToRebindService(120000, false);
    }

    /* access modifiers changed from: private */
    public void delayToRebindService() {
        delayToRebindService(calcDelayTime(), true);
    }

    private void delayToRebindService(long delayTime, boolean increaseRebindCount) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), delayTime);
        if (increaseRebindCount) {
            this.mRebindCount++;
        }
        logI("SplashScreenService rebind count: " + this.mRebindCount);
    }

    private long calcDelayTime() {
        int severity;
        long aliveTime = System.currentTimeMillis() - this.mStartTime;
        if (aliveTime < 60000) {
            severity = 1;
        } else if (aliveTime < 3600000) {
            severity = 2;
        } else {
            severity = 3;
        }
        if (severity != this.mSeverity) {
            this.mDelayTime = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        } else if (severity == 1) {
            this.mDelayTime *= 2;
        } else if (severity == 2) {
            this.mDelayTime += JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        } else {
            this.mDelayTime = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        }
        long j = this.mDelayTime;
        this.mDelayTime = j + (((long) this.mRebindCount) * j);
        this.mDelayTime = Math.min(this.mDelayTime, 3600000);
        this.mSeverity = severity;
        logI("Restart SplashScreenService delay time " + this.mDelayTime);
        return this.mDelayTime;
    }

    /* access modifiers changed from: private */
    public void keepSplashPackageCheckInfo(SplashPackageCheckInfo splashPackageCheckInfo) {
        this.mSplashPackageCheckInfoMap.put(splashPackageCheckInfo.getSplashPackageName(), splashPackageCheckInfo);
    }

    private boolean isSplashPackage(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        SplashPackageCheckInfo info = this.mSplashPackageCheckInfoMap.get(packageName);
        if (info == null) {
            logI("None for " + packageName);
            return false;
        } else if (info.isExpired()) {
            logI(info + " is expired, remove it");
            this.mSplashPackageCheckInfoMap.remove(packageName);
            return false;
        } else {
            boolean mt = info.matchTime();
            if (!mt) {
                logI("Mismatch time for " + packageName);
            }
            return mt;
        }
    }

    /* access modifiers changed from: private */
    public boolean checkSplashPackageCheckInfo(SplashPackageCheckInfo splashPackageCheckInfo) {
        return splashPackageCheckInfo != null && !TextUtils.isEmpty(splashPackageCheckInfo.getSplashPackageName()) && !splashPackageCheckInfo.isExpired() && isPackageInstalled(splashPackageCheckInfo.getSplashPackageName());
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            PackageInfo pi = this.mContext.getPackageManager().getPackageInfo(packageName, 0);
            if (pi == null || pi.applicationInfo == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Intent requestSplashScreen(Intent intent, ActivityInfo aInfo) {
        ISplashScreenService sss = this.mSplashScreenService;
        if (sss != null && isSplashPackage(getPackageName(aInfo))) {
            long startTime = System.currentTimeMillis();
            try {
                Intent requestSplashScreen = sss.requestSplashScreen(intent, aInfo);
                Intent finalIntent = requestSplashScreen;
                if (requestSplashScreen != null) {
                    logCost("requestSplashScreen ", startTime, aInfo);
                    return finalIntent;
                }
            } catch (Exception e) {
                logE("requestSplashScreen exception", e);
            } catch (Throwable th) {
                logCost("requestSplashScreen ", startTime, aInfo);
                throw th;
            }
            logCost("requestSplashScreen ", startTime, aInfo);
        }
        return intent;
    }

    public void activityIdle(ActivityInfo aInfo) {
    }

    public void destroyActivity(ActivityInfo aInfo) {
        ISplashScreenService sss = this.mSplashScreenService;
        if (sss != null && isSplashPackage(getPackageName(aInfo))) {
            long startTime = System.currentTimeMillis();
            try {
                sss.destroyActivity(aInfo);
            } catch (Exception e) {
                logE("destroyActivity exception", e);
            } catch (Throwable th) {
                logCost("destroyActivity", startTime, aInfo);
                throw th;
            }
            logCost("destroyActivity", startTime, aInfo);
        }
    }

    private String getPackageName(ActivityInfo aInfo) {
        if (aInfo == null || aInfo.applicationInfo == null) {
            return null;
        }
        return aInfo.applicationInfo.packageName;
    }

    private void logCost(String prefix, long startTime, ActivityInfo aInfo) {
        if (sDebug) {
            logI(prefix + " " + (System.currentTimeMillis() - startTime) + "ms, " + getPackageName(aInfo));
        }
    }

    /* access modifiers changed from: private */
    public void logI(String msg) {
        logI(msg, false);
    }

    /* access modifiers changed from: private */
    public void logI(String msg, boolean force) {
        if (sDebug || force) {
            Slog.i(TAG, msg);
        }
    }

    private void logW(String msg) {
        Slog.w(TAG, msg);
    }

    /* access modifiers changed from: private */
    public void logE(String msg, Throwable tr) {
        Slog.e(TAG, msg, tr);
    }
}
