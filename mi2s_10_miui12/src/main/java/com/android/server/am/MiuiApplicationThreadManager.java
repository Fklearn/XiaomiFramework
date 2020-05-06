package com.android.server.am;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import miui.process.IMiuiApplicationThread;

class MiuiApplicationThreadManager {
    private static final String TAG = "ProcessManager";
    private ActivityManagerService mActivityManagerService;
    private SparseArray<IMiuiApplicationThread> mMiuiApplicationThreads = new SparseArray<>();

    public MiuiApplicationThreadManager(ActivityManagerService ams) {
        this.mActivityManagerService = ams;
    }

    public synchronized void addMiuiApplicationThread(IMiuiApplicationThread applicationThread, int pid) {
        this.mMiuiApplicationThreads.put(pid, applicationThread);
        try {
            applicationThread.asBinder().linkToDeath(new CallBack(pid, applicationThread), 0);
        } catch (RemoteException e) {
            Log.w("ProcessManager", "process:" + pid + " is dead");
        }
        return;
    }

    public synchronized void removeMiuiApplicationThread(int pid) {
        this.mMiuiApplicationThreads.remove(pid);
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    public synchronized IMiuiApplicationThread getMiuiApplicationThread(int pid) {
        return pid != 0 ? this.mMiuiApplicationThreads.get(pid) : null;
    }

    private final class CallBack implements IBinder.DeathRecipient {
        final IMiuiApplicationThread mMiuiApplicationThread;
        final int mPid;

        CallBack(int pid, IMiuiApplicationThread thread) {
            this.mPid = pid;
            this.mMiuiApplicationThread = thread;
        }

        public void binderDied() {
            MiuiApplicationThreadManager.this.removeMiuiApplicationThread(this.mPid);
        }
    }
}
