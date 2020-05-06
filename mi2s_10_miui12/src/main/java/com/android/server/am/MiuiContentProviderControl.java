package com.android.server.am;

import android.app.ContentProviderHolder;
import android.app.IApplicationThread;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.server.ServiceThread;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import miui.os.Build;
import miui.util.ReflectionUtils;

public class MiuiContentProviderControl {
    static final int CALLER_TYPE_APP = 100;
    static final int CALLER_TYPE_EXTERNAL = 101;
    static final int CALLER_TYPE_PROVIDER_CONTROL = 102;
    static final boolean DEBUG_CONTROL_PROVIDER_STARTS = false;
    static final long LOW_PRIORITY_DELAY = 150;
    public static final int MAIN_THREAD_ID = 1;
    static final long MAX_TIME_OUT = 1500;
    static final int MSG_NOTIFY_WAIT_THREAD_WHEN_PROVIDER_REMOVED = 102;
    static final int MSG_START_PROVIDER = 101;
    private static final String PROP_MCPC_OPEN_DEFAULT = "persist.sys.mcpc_open";
    static final long SHORT_DELAY = 5;
    static final String TAG = MiuiContentProviderControl.class.getSimpleName();
    static final int THREAD_ID_CALLER_TYPE_EXTERNAL = -1000;
    static MiuiContentProviderControl mInstance;
    private final int MAX_SIZE = 20;
    private boolean canOpen = true;
    private boolean closeCheck = true;
    private boolean enableProviderControl = false;
    private Field fWaitProcessStart;
    private long lasEnforcedTime;
    private StringBuilder logBuilder = new StringBuilder();
    /* access modifiers changed from: private */
    public ActivityManagerService mAms;
    private Method mEnforceGetContentProviderImpl;
    private Method mNotifyProviderReady;
    private final LinkedList<StartProviderInfo> mProviderInfoCache = new LinkedList<>();
    private final LinkedList<StartProviderInfo> mProviderInfoList = new LinkedList<>();
    private WorkHandler mWorkHandler;
    private ServiceThread mWorkThread;
    private int maxSize = 0;
    private boolean sendNoDelayEnforcedMsg = false;

    private MiuiContentProviderControl(ActivityManagerService ams) {
        Class<ActivityManagerService> cls = ActivityManagerService.class;
        try {
            this.mEnforceGetContentProviderImpl = ReflectionUtils.findMethodExact(cls, "getContentProviderImpl", new Class[]{IApplicationThread.class, String.class, IBinder.class, Boolean.TYPE, Integer.TYPE, Long.TYPE, Integer.TYPE});
            this.mNotifyProviderReady = ReflectionUtils.findMethodExact(IApplicationThread.class, "notifyProviderReady", new Class[]{Long.TYPE, ContentProviderHolder.class});
            this.fWaitProcessStart = ReflectionUtils.findField(ContentProviderHolder.class, "waitProcessStart");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.IS_CTS_BUILD || this.mEnforceGetContentProviderImpl == null || this.mNotifyProviderReady == null || this.fWaitProcessStart == null) {
            this.canOpen = false;
            return;
        }
        this.mAms = ams;
        this.mWorkThread = new ServiceThread(TAG, -2, false);
        this.mWorkThread.start();
        this.mWorkHandler = new WorkHandler(this.mWorkThread.getLooper());
    }

    public static void init(ActivityManagerService ams) {
        mInstance = new MiuiContentProviderControl(ams);
    }

    public static MiuiContentProviderControl getInstance() {
        return mInstance;
    }

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 101) {
                long now = SystemClock.elapsedRealtime();
                synchronized (MiuiContentProviderControl.this.mAms) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        MiuiContentProviderControl.this.rescheduleProviderListLocked(now);
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } else if (i == 102 && msg.obj != null) {
                ArrayList<StartProviderInfo> providerInfoList = (ArrayList) msg.obj;
                while (providerInfoList.size() > 0) {
                    MiuiContentProviderControl.this.enforceNotifyProviderReady(providerInfoList.remove(0), (ContentProviderHolder) null);
                }
            }
        }
    }

    public void enableProviderControlLocked() {
        if (this.canOpen) {
            this.enableProviderControl = true;
            if (SystemProperties.getBoolean(PROP_MCPC_OPEN_DEFAULT, false)) {
                this.closeCheck = false;
            }
        }
    }

    public void openProviderControl() {
        synchronized (this.mAms) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (this.canOpen) {
                    if (this.enableProviderControl) {
                        this.closeCheck = false;
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void closeProviderControl() {
        synchronized (this.mAms) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!this.canOpen) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                this.closeCheck = true;
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    private ContentProviderHolder enforceGetContentProviderImpl(StartProviderInfo info) {
        try {
            return (ContentProviderHolder) this.mEnforceGetContentProviderImpl.invoke(this.mAms, new Object[]{info.mCaller, info.mName, info.mToken, Boolean.valueOf(info.mStable), Integer.valueOf(info.mUserId), Long.valueOf(info.mCallerThreadId), 102});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void enforceNotifyProviderReady(StartProviderInfo info, ContentProviderHolder holder) {
        try {
            this.mNotifyProviderReady.invoke(info.mCaller, new Object[]{Long.valueOf(info.mCallerThreadId), holder});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addProviderInfoLocked(IApplicationThread caller, String name, String callerPkg, int callerPid, IBinder token, boolean stable, int userId, long callerThreadId) {
        StartProviderInfo spInfo;
        if (this.closeCheck || Binder.getCallingPid() == ActivityManagerService.MY_PID || callerThreadId == 1) {
            return false;
        }
        long callingIdentity = Binder.clearCallingIdentity();
        Binder.restoreCallingIdentity(callingIdentity);
        if (this.mProviderInfoCache.size() == 0) {
            spInfo = new StartProviderInfo(caller, name, callerPkg, callingIdentity, callerPid, token, stable, userId, callerThreadId);
        } else {
            spInfo = this.mProviderInfoCache.removeLast();
            spInfo.reset(caller, name, callerPkg, callingIdentity, callerPid, token, stable, userId, callerThreadId, LOW_PRIORITY_DELAY);
        }
        this.mProviderInfoList.add(spInfo);
        if (this.mProviderInfoList.size() > this.maxSize) {
            this.maxSize = this.mProviderInfoList.size();
        }
        if (this.mProviderInfoList.size() >= 20) {
            this.lasEnforcedTime = SystemClock.elapsedRealtime();
            if (!this.sendNoDelayEnforcedMsg) {
                this.sendNoDelayEnforcedMsg = true;
                this.mWorkHandler.sendMessage(this.mWorkHandler.obtainMessage(101));
            }
        }
        if (this.mProviderInfoList.size() == 1) {
            long now = SystemClock.elapsedRealtime();
            Message msg = this.mWorkHandler.obtainMessage(101);
            long j = this.lasEnforcedTime;
            if (now >= j) {
                this.mWorkHandler.sendMessage(msg);
            } else {
                this.mWorkHandler.sendMessageDelayed(msg, j - now);
            }
        }
        return true;
    }

    public void removeProviderInfoLocked(String name, int userId) {
        ArrayList<StartProviderInfo> removeInfo = new ArrayList<>();
        Iterator<StartProviderInfo> iterator = this.mProviderInfoList.iterator();
        while (iterator.hasNext()) {
            StartProviderInfo info = iterator.next();
            if (info.mName.equals(name) && info.mUserId == userId) {
                removeInfo.add(info);
                iterator.remove();
            }
        }
        if (removeInfo.size() > 0) {
            Message msg = new Message();
            msg.what = 102;
            msg.obj = removeInfo;
            this.mWorkHandler.sendMessage(msg);
        }
    }

    private boolean isWaitProcessStart(ContentProviderHolder holder) {
        try {
            return this.fWaitProcessStart.getBoolean(holder);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void rescheduleProviderListLocked(long now) {
        if (this.canOpen) {
            if (now >= this.lasEnforcedTime && this.mProviderInfoList.size() > 0) {
                StartProviderInfo spInfo = this.mProviderInfoList.removeFirst();
                StartProviderInfo next = null;
                if (this.mProviderInfoList.size() > 0) {
                    next = this.mProviderInfoList.getFirst();
                }
                if (next == null) {
                    this.lasEnforcedTime = LOW_PRIORITY_DELAY + now;
                } else if (this.mProviderInfoList.size() <= 20) {
                    this.sendNoDelayEnforcedMsg = false;
                    this.lasEnforcedTime = next.mDelay + now;
                } else {
                    this.lasEnforcedTime = SHORT_DELAY + now;
                }
                Binder.restoreCallingIdentity(spInfo.mCallingIdentity);
                this.logBuilder.setLength(0);
                this.logBuilder.append("ProviderControl enforce Provider : callerPkg : ");
                this.logBuilder.append(spInfo.mCallerPkg);
                this.logBuilder.append(" name : ");
                this.logBuilder.append(spInfo.mName);
                this.logBuilder.append(" callerUid : ");
                this.logBuilder.append(Binder.getCallingUid());
                this.logBuilder.append(" callerPid : ");
                this.logBuilder.append(Binder.getCallingPid());
                Slog.d(TAG, this.logBuilder.toString());
                this.mProviderInfoCache.addFirst(spInfo);
                ContentProviderHolder holder = enforceGetContentProviderImpl(spInfo);
                Binder.clearCallingIdentity();
                if (holder == null || !isWaitProcessStart(holder)) {
                    enforceNotifyProviderReady(spInfo, holder);
                }
                this.mWorkHandler.sendMessageDelayed(this.mWorkHandler.obtainMessage(101), this.lasEnforcedTime - now);
            }
            if (now - this.lasEnforcedTime > MAX_TIME_OUT && this.mProviderInfoList.size() > 0) {
                WorkHandler workHandler = this.mWorkHandler;
                workHandler.sendMessage(workHandler.obtainMessage(101));
            }
        }
    }

    public void dumpLocked(PrintWriter pw) {
        pw.println("  MIUI ADD :  MCPC dump start : ");
        pw.print(" canOpen : ");
        pw.println(this.canOpen);
        pw.print(" enableProviderControl : ");
        pw.println(this.enableProviderControl);
        pw.print(" closeCheck : ");
        pw.println(this.closeCheck);
        pw.print(" lasEnforcedTime : ");
        pw.println(this.lasEnforcedTime);
        pw.print("maxSize : ");
        pw.println(this.maxSize);
        pw.println("#  ProviderInfoList : ");
        Iterator<StartProviderInfo> iterator = this.mProviderInfoList.iterator();
        while (iterator.hasNext()) {
            printStartProviderInfo(pw, iterator.next());
        }
        pw.println("#  ProviderInfoObjCache : ");
        Iterator<StartProviderInfo> iterator2 = this.mProviderInfoCache.iterator();
        while (iterator2.hasNext()) {
            printStartProviderInfo(pw, iterator2.next());
        }
        pw.println("  MCPC dump END !!!");
    }

    private void printStartProviderInfo(PrintWriter pw, StartProviderInfo info) {
        pw.print("#SPInfo : ");
        pw.print(info);
        pw.print(" CallerPkg : ");
        pw.print(info.mCallerPkg);
        pw.print(" Name : ");
        pw.print(info.mName);
        pw.print(" CallerThreadId : ");
        pw.print(info.mCallerThreadId);
        pw.print(" Delay : ");
        pw.print(info.mDelay);
        pw.println("");
    }
}
