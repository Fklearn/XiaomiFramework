package com.android.server.am;

import android.app.IApplicationThread;
import android.os.IBinder;

/* compiled from: MiuiContentProviderControl */
class StartProviderInfo {
    IApplicationThread mCaller;
    int mCallerPid;
    String mCallerPkg;
    long mCallerThreadId;
    long mCallingIdentity;
    long mDelay;
    String mName;
    boolean mStable;
    IBinder mToken;
    int mUserId;

    public StartProviderInfo(IApplicationThread caller, String name, String callerPkg, long callingIdentity, int callerPid, IBinder token, boolean stable, int userId, long callerThreadId) {
        reset(caller, name, callerPkg, callingIdentity, callerPid, token, stable, userId, callerThreadId, 150);
    }

    public void reset(IApplicationThread caller, String name, String callerPkg, long callingIdentity, int callerPid, IBinder token, boolean stable, int userId, long callerThreadId, long delay) {
        this.mCaller = caller;
        this.mCallerPkg = callerPkg;
        this.mCallingIdentity = callingIdentity;
        this.mCallerPid = callerPid;
        this.mName = name;
        this.mToken = token;
        this.mStable = stable;
        this.mUserId = userId;
        this.mCallerThreadId = callerThreadId;
        this.mDelay = delay;
    }
}
