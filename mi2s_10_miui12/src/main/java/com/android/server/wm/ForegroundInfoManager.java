package com.android.server.wm;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.am.ProcessManagerService;
import com.android.server.wm.ActivityStack;
import java.io.PrintWriter;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.IActivityChangeListener;
import miui.process.IForegroundInfoListener;
import miui.process.IForegroundWindowListener;

public class ForegroundInfoManager {
    private static final String TAG = "ProcessManager";
    private final RemoteCallbackList<IActivityChangeListener> mActivityChangeListeners = new RemoteCallbackList<>();
    private Object mActivityLock = new Object();
    @GuardedBy({"mForegroundLock"})
    private ForegroundInfo mForegroundInfo;
    private final RemoteCallbackList<IForegroundInfoListener> mForegroundInfoListeners = new RemoteCallbackList<>();
    private Object mForegroundLock = new Object();
    @GuardedBy({"mForegroundLock"})
    private ForegroundInfo mForegroundWindowInfo;
    private final RemoteCallbackList<IForegroundWindowListener> mForegroundWindowListeners = new RemoteCallbackList<>();
    @GuardedBy({"mActivityLock"})
    private ComponentName mLastActivityComponent;
    private ProcessManagerService mProcessManagerService;

    class ActivityChangeInfo {
        int callingPid;
        List<String> targetActivities;
        List<String> targetPackages;

        public ActivityChangeInfo(int callingPid2, List<String> targetPackages2, List<String> targetActivities2) {
            this.callingPid = callingPid2;
            this.targetPackages = targetPackages2;
            this.targetActivities = targetActivities2;
        }

        public String toString() {
            return "ActivityChangeInfo{callingPid=" + this.callingPid + ", targetPackages=" + this.targetPackages + ", targetActivities=" + this.targetActivities + '}';
        }
    }

    public ForegroundInfoManager(ProcessManagerService pms) {
        this.mProcessManagerService = pms;
        this.mForegroundInfo = new ForegroundInfo();
        this.mForegroundWindowInfo = new ForegroundInfo();
    }

    private boolean isMultiWindowChanged(ApplicationInfo multiWindowAppInfo) {
        String lastPkg = this.mForegroundInfo.mMultiWindowForegroundPackageName;
        if (multiWindowAppInfo != null) {
            return true ^ TextUtils.equals(multiWindowAppInfo.packageName, lastPkg);
        }
        if (lastPkg != null) {
            return true;
        }
        return false;
    }

    public void notifyForegroundInfoChanged(ActivityRecord foregroundRecord, ActivityStack.ActivityState state, int pid, ApplicationInfo multiWindowAppInfo) {
        synchronized (this.mForegroundLock) {
            ApplicationInfo foregroundAppInfo = foregroundRecord.appInfo;
            if (foregroundAppInfo != null) {
                if (!TextUtils.equals(this.mForegroundInfo.mForegroundPackageName, foregroundAppInfo.packageName) || isMultiWindowChanged(multiWindowAppInfo)) {
                    this.mForegroundInfo.resetFlags();
                    if (foregroundRecord.isColdStart && state == ActivityStack.ActivityState.INITIALIZING) {
                        this.mForegroundInfo.addFlags(1);
                    }
                    this.mForegroundInfo.mLastForegroundPackageName = this.mForegroundInfo.mForegroundPackageName;
                    this.mForegroundInfo.mLastForegroundUid = this.mForegroundInfo.mForegroundUid;
                    this.mForegroundInfo.mLastForegroundPid = this.mForegroundInfo.mForegroundPid;
                    this.mForegroundInfo.mForegroundPackageName = foregroundAppInfo.packageName;
                    this.mForegroundInfo.mForegroundUid = foregroundAppInfo.uid;
                    this.mForegroundInfo.mForegroundPid = pid;
                    if (multiWindowAppInfo != null) {
                        this.mForegroundInfo.mMultiWindowForegroundPackageName = multiWindowAppInfo.packageName;
                        this.mForegroundInfo.mMultiWindowForegroundUid = multiWindowAppInfo.uid;
                    } else {
                        this.mForegroundInfo.mMultiWindowForegroundPackageName = null;
                        this.mForegroundInfo.mMultiWindowForegroundUid = -1;
                    }
                    notifyForegroundInfoLocked();
                    this.mProcessManagerService.foregroundInfoChanged(this.mForegroundInfo.mForegroundPackageName);
                    ActivityTaskManagerServiceInjector.updateLastStartActivityUid(this.mForegroundInfo.mForegroundPackageName, this.mForegroundInfo.mForegroundUid);
                    return;
                }
            }
            Log.d("ProcessManager", "skip notify foregroundAppInfo:" + foregroundAppInfo);
        }
    }

    public void notifyForegroundWindowChanged(ActivityRecord foregroundRecord, ActivityStack.ActivityState state, int pid, ApplicationInfo multiWindowAppInfo) {
        synchronized (this.mForegroundLock) {
            ApplicationInfo foregroundAppInfo = foregroundRecord.appInfo;
            if (foregroundAppInfo == null) {
                Log.d("ProcessManager", "skip notify foregroundAppInfo:" + foregroundAppInfo);
                return;
            }
            this.mForegroundWindowInfo.resetFlags();
            if (foregroundRecord.isColdStart && state == ActivityStack.ActivityState.INITIALIZING) {
                this.mForegroundWindowInfo.addFlags(1);
            }
            this.mForegroundWindowInfo.mLastForegroundPackageName = this.mForegroundWindowInfo.mForegroundPackageName;
            this.mForegroundWindowInfo.mLastForegroundUid = this.mForegroundWindowInfo.mForegroundUid;
            this.mForegroundWindowInfo.mLastForegroundPid = this.mForegroundWindowInfo.mForegroundPid;
            this.mForegroundWindowInfo.mForegroundPackageName = foregroundAppInfo.packageName;
            this.mForegroundWindowInfo.mForegroundUid = foregroundAppInfo.uid;
            this.mForegroundWindowInfo.mForegroundPid = pid;
            if (multiWindowAppInfo != null) {
                this.mForegroundWindowInfo.mMultiWindowForegroundPackageName = multiWindowAppInfo.packageName;
                this.mForegroundWindowInfo.mMultiWindowForegroundUid = multiWindowAppInfo.uid;
            } else {
                this.mForegroundWindowInfo.mMultiWindowForegroundPackageName = null;
                this.mForegroundWindowInfo.mMultiWindowForegroundUid = -1;
            }
            notifyForegroundWindowLocked();
        }
    }

    private void notifyForegroundInfoLocked() {
        for (int i = this.mForegroundInfoListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                this.mForegroundInfoListeners.getBroadcastItem(i).onForegroundInfoChanged(this.mForegroundInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.mForegroundInfoListeners.finishBroadcast();
    }

    private void notifyForegroundWindowLocked() {
        for (int i = this.mForegroundWindowListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                this.mForegroundWindowListeners.getBroadcastItem(i).onForegroundWindowChanged(this.mForegroundWindowInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.mForegroundWindowListeners.finishBroadcast();
    }

    public ForegroundInfo getForegroundInfo() {
        ForegroundInfo foregroundInfo;
        synchronized (this.mForegroundLock) {
            foregroundInfo = new ForegroundInfo(this.mForegroundInfo);
        }
        return foregroundInfo;
    }

    public void registerForegroundInfoListener(IForegroundInfoListener listener) {
        if (listener != null) {
            synchronized (this.mForegroundLock) {
                this.mForegroundInfoListeners.register(listener);
            }
        }
    }

    public void unregisterForegroundInfoListener(IForegroundInfoListener listener) {
        if (listener != null) {
            synchronized (this.mForegroundLock) {
                this.mForegroundInfoListeners.unregister(listener);
            }
        }
    }

    public void registerForegroundWindowListener(IForegroundWindowListener listener) {
        if (listener != null) {
            synchronized (this.mForegroundLock) {
                this.mForegroundWindowListeners.register(listener);
            }
        }
    }

    public void unregisterForegroundWindowListener(IForegroundWindowListener listener) {
        if (listener != null) {
            synchronized (this.mForegroundLock) {
                this.mForegroundWindowListeners.unregister(listener);
            }
        }
    }

    public void registerActivityChangeListener(List<String> targetPackages, List<String> targetActivities, IActivityChangeListener listener) {
        if (listener != null) {
            synchronized (this.mActivityLock) {
                this.mActivityChangeListeners.register(listener, new ActivityChangeInfo(Binder.getCallingPid(), targetPackages, targetActivities));
            }
        }
    }

    public void unregisterActivityChangeListener(IActivityChangeListener listener) {
        if (listener != null) {
            synchronized (this.mActivityLock) {
                this.mActivityChangeListeners.unregister(listener);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyActivityChanged(android.content.ComponentName r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mActivityLock
            monitor-enter(r0)
            if (r3 == 0) goto L_0x0015
            android.content.ComponentName r1 = r2.mLastActivityComponent     // Catch:{ all -> 0x0017 }
            boolean r1 = r3.equals(r1)     // Catch:{ all -> 0x0017 }
            if (r1 == 0) goto L_0x000e
            goto L_0x0015
        L_0x000e:
            r2.notifyActivitiesChangedLocked(r3)     // Catch:{ all -> 0x0017 }
            r2.mLastActivityComponent = r3     // Catch:{ all -> 0x0017 }
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            return
        L_0x0015:
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            return
        L_0x0017:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ForegroundInfoManager.notifyActivityChanged(android.content.ComponentName):void");
    }

    public void notifyActivitiesChangedLocked(ComponentName curComponent) {
        for (int i = this.mActivityChangeListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                notifyActivityChangedIfNeededLocked(this.mActivityChangeListeners.getBroadcastItem(i), this.mActivityChangeListeners.getBroadcastCookie(i), curComponent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.mActivityChangeListeners.finishBroadcast();
    }

    public void notifyActivityChangedIfNeededLocked(IActivityChangeListener listener, Object cookie, ComponentName curComponent) throws RemoteException {
        if (cookie != null && (cookie instanceof ActivityChangeInfo)) {
            ActivityChangeInfo info = (ActivityChangeInfo) cookie;
            ComponentName componentName = this.mLastActivityComponent;
            String lastActivity = null;
            String lastPackage = componentName != null ? componentName.getPackageName() : null;
            ComponentName componentName2 = this.mLastActivityComponent;
            if (componentName2 != null) {
                lastActivity = componentName2.getClassName();
            }
            if (info.targetPackages != null && info.targetActivities != null) {
                if (info.targetPackages.contains(curComponent.getPackageName()) || info.targetPackages.contains(lastPackage) || info.targetActivities.contains(curComponent.getClassName()) || info.targetActivities.contains(lastActivity)) {
                    listener.onActivityChanged(this.mLastActivityComponent, curComponent);
                }
            }
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println("ForegroundInfo Listener:");
        synchronized (this.mForegroundLock) {
            for (int i = this.mForegroundInfoListeners.beginBroadcast() - 1; i >= 0; i--) {
                pw.print("  #");
                pw.print(i);
                pw.print(": ");
                pw.println(this.mForegroundInfoListeners.getBroadcastItem(i).toString());
            }
            this.mForegroundInfoListeners.finishBroadcast();
        }
        pw.println(prefix + "mForegroundInfo=" + this.mForegroundInfo);
        pw.println("ActivityChange Listener:");
        synchronized (this.mActivityLock) {
            for (int i2 = this.mActivityChangeListeners.beginBroadcast() - 1; i2 >= 0; i2--) {
                pw.print("  #");
                pw.print(i2);
                pw.print(": ");
                pw.println(this.mActivityChangeListeners.getBroadcastCookie(i2));
            }
            this.mActivityChangeListeners.finishBroadcast();
        }
        pw.println(prefix + "mLastActivityComponent=" + this.mLastActivityComponent);
    }
}
