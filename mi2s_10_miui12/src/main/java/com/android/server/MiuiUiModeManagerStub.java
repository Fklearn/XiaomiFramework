package com.android.server;

import android.app.IAppDarkModeObserver;
import android.app.IUiModeManager;
import android.app.UiModeManager;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Slog;

public abstract class MiuiUiModeManagerStub extends IUiModeManager.Stub {
    final RemoteCallbackList<IAppDarkModeObserver> mAppDarkModeObservers = new RemoteCallbackList<>();
    private UiModeManagerService mUiModeManagerService;

    public MiuiUiModeManagerStub(UiModeManagerService uiModeManagerService) {
        this.mUiModeManagerService = uiModeManagerService;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case 16777213:
                data.enforceInterface("android.app.IUiModeManager");
                registAppDarkModeCallback(IAppDarkModeObserver.Stub.asInterface(data.readStrongBinder()), data.readString(), data.readString(), data.readInt());
                data.writeNoException();
                return true;
            case 16777214:
                data.enforceInterface("android.app.IUiModeManager");
                setAppDarkModeEnable(data.readString(), data.readBoolean());
                data.writeNoException();
                return true;
            default:
                boolean result = MiuiUiModeManagerStub.super.onTransact(code, data, reply, flags);
                if (code == 1598311760) {
                    dumpMiuiUiModeManagerStub();
                }
                return result;
        }
    }

    public void setAppDarkModeEnable(String packageName, boolean enable) {
        if (this.mUiModeManagerService.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
            Slog.e(UiModeManager.class.getSimpleName(), "setAppDarkModeEnable failed, requires MODIFY_DAY_NIGHT_MODE permission");
            return;
        }
        synchronized (this.mUiModeManagerService.mLock) {
            int i = this.mAppDarkModeObservers.beginBroadcast();
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    AppDarkModeObserverRegistration registration = (AppDarkModeObserverRegistration) this.mAppDarkModeObservers.getBroadcastCookie(i2);
                    if (!(registration.mPackageName == null || packageName == null || !registration.mPackageName.equals(packageName))) {
                        try {
                            this.mAppDarkModeObservers.getBroadcastItem(i2).onAppDarkModeChanged(enable);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    i = i2;
                } else {
                    this.mAppDarkModeObservers.finishBroadcast();
                }
            }
        }
    }

    public void registAppDarkModeCallback(IAppDarkModeObserver observer, String packageName, String processName, int userId) {
        if (observer == null || packageName == null || processName == null) {
            Slog.e(UiModeManager.class.getSimpleName(), "registAppDarkModeCallback param is null");
            return;
        }
        synchronized (this.mUiModeManagerService.mLock) {
            int i = this.mAppDarkModeObservers.beginBroadcast();
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    AppDarkModeObserverRegistration registration = (AppDarkModeObserverRegistration) this.mAppDarkModeObservers.getBroadcastCookie(i2);
                    if (registration.mPackageName.equals(packageName) && registration.mProcessName.equals(processName) && registration.mUserId == userId) {
                        this.mAppDarkModeObservers.unregister(this.mAppDarkModeObservers.getBroadcastItem(i2));
                    }
                    i = i2;
                } else {
                    this.mAppDarkModeObservers.finishBroadcast();
                    this.mAppDarkModeObservers.register(observer, new AppDarkModeObserverRegistration(packageName, processName, userId));
                }
            }
        }
    }

    static final class AppDarkModeObserverRegistration {
        String mPackageName;
        String mProcessName;
        int mUserId;

        AppDarkModeObserverRegistration(String packageName, String processName, int userId) {
            this.mPackageName = packageName;
            this.mProcessName = processName;
            this.mUserId = userId;
        }
    }

    private void dumpMiuiUiModeManagerStub() {
        if (this.mUiModeManagerService.getContext().checkCallingOrSelfPermission("android.permission.DUMP") == 0) {
            String simpleName = UiModeManager.class.getSimpleName();
            Slog.d(simpleName, "mAppDarkModeObservers callback size = " + this.mAppDarkModeObservers.getRegisteredCallbackCount());
        }
    }
}
