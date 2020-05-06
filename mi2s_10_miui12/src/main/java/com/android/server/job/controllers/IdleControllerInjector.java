package com.android.server.job.controllers;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import miui.os.Build;

public class IdleControllerInjector {
    private static final boolean DEBUG;
    private static final boolean IS_INTERNATIONAL = Build.IS_INTERNATIONAL_BUILD;
    private static final String TAG = "JobScheduler.Idle";
    private static final String[] WHITELISTFORIDLE = {"com.xiaomi.market", "com.miui.cloudbackup"};
    private static volatile IdleControllerInjector mInstance;
    private AlarmManager mAlarm;
    private Context mContext;
    private boolean mDockIdle;
    private boolean mIdleOnlyWhiteList;
    private AlarmManager.OnAlarmListener mIdleOnlyWhiteListAlarmListener = new AlarmManager.OnAlarmListener() {
        public final void onAlarm() {
            IdleControllerInjector.this.lambda$new$0$IdleControllerInjector();
        }
    };
    private long mIdleWindowSlop;
    private long mInactivityIdleOnlyWhiteListThreshold;
    private Object mLock;
    private boolean mScreenOn;
    private StateChangedListener mStateChangedListener;
    private final ArraySet<JobStatus> mTrackedTasksOnlyWhiteList = new ArraySet<>();

    static {
        boolean z = false;
        if (JobSchedulerService.DEBUG || SystemProperties.getBoolean("persist.sys.job_debug_idle", false)) {
            z = true;
        }
        DEBUG = z;
    }

    private IdleControllerInjector() {
    }

    public static IdleControllerInjector getInstance() {
        if (mInstance == null) {
            synchronized (IdleControllerInjector.class) {
                if (mInstance == null) {
                    mInstance = new IdleControllerInjector();
                }
            }
        }
        return mInstance;
    }

    public void initInjector(Context context, StateChangedListener listener, long threshold, long duration, Object lock) {
        if (!IS_INTERNATIONAL) {
            this.mContext = context;
            this.mStateChangedListener = listener;
            this.mInactivityIdleOnlyWhiteListThreshold = threshold / 2;
            this.mIdleWindowSlop = duration;
            this.mLock = lock;
            this.mIdleOnlyWhiteList = false;
            this.mAlarm = (AlarmManager) this.mContext.getSystemService("alarm");
        }
    }

    public void updateIdleTrackerState(boolean screenOn, boolean dockIdle) {
        if (!IS_INTERNATIONAL) {
            this.mScreenOn = screenOn;
            this.mDockIdle = dockIdle;
            if (DEBUG) {
                Slog.d(TAG, "state has changed: mScreenOn=" + this.mScreenOn + ",mDockIdle=" + this.mDockIdle);
            }
            if (!this.mScreenOn || this.mDockIdle) {
                setAlarm(JobSchedulerService.sElapsedRealtimeClock.millis());
                return;
            }
            this.mAlarm.cancel(this.mIdleOnlyWhiteListAlarmListener);
            if (this.mIdleOnlyWhiteList) {
                this.mIdleOnlyWhiteList = false;
                reportNewIdleStateOnlyWhiteList(this.mIdleOnlyWhiteList);
            }
        }
    }

    public boolean isInIdleWhiteListLocked(JobStatus taskStatus) {
        if (IS_INTERNATIONAL) {
            return false;
        }
        for (String equals : WHITELISTFORIDLE) {
            if (taskStatus.getSourcePackageName().equals(equals)) {
                this.mTrackedTasksOnlyWhiteList.add(taskStatus);
                taskStatus.setIdleConstraintSatisfied(this.mIdleOnlyWhiteList);
                return true;
            }
        }
        return false;
    }

    private void setAlarm(long nowElapsed) {
        long j = nowElapsed;
        long duration = 60000;
        long whenForWhiteList = (DEBUG ? 60000 : this.mInactivityIdleOnlyWhiteListThreshold) + j;
        if (DEBUG) {
            Slog.v(TAG, "Scheduling idle only for packages in whitelist:  now:" + j + " when=" + whenForWhiteList);
        }
        if (!DEBUG) {
            duration = this.mIdleWindowSlop;
        }
        this.mAlarm.setWindow(2, whenForWhiteList, duration, "JS in whitelist idleness", this.mIdleOnlyWhiteListAlarmListener, (Handler) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: handleIdleTriggerOnlyWhiteList */
    public void lambda$new$0$IdleControllerInjector() {
        if (!this.mIdleOnlyWhiteList && (!this.mScreenOn || this.mDockIdle)) {
            this.mIdleOnlyWhiteList = true;
            Slog.d(TAG, "Idle only for whitelist trigger fired @ " + JobSchedulerService.sElapsedRealtimeClock.millis());
            reportNewIdleStateOnlyWhiteList(this.mIdleOnlyWhiteList);
        } else if (DEBUG) {
            Slog.v(TAG, "TRIGGER_IDLE received but not changing state; idle=" + this.mIdleOnlyWhiteList + " screen=" + this.mScreenOn);
        }
    }

    private void reportNewIdleStateOnlyWhiteList(boolean isIdle) {
        boolean change = false;
        synchronized (this.mLock) {
            for (int i = this.mTrackedTasksOnlyWhiteList.size() - 1; i >= 0; i--) {
                change |= this.mTrackedTasksOnlyWhiteList.valueAt(i).setIdleConstraintSatisfied(isIdle);
            }
        }
        if (change) {
            this.mStateChangedListener.onControllerStateChanged();
        }
    }

    public void removeTaskLocked(JobStatus taskStatus) {
        if (!IS_INTERNATIONAL) {
            this.mTrackedTasksOnlyWhiteList.remove(taskStatus);
        }
    }
}
