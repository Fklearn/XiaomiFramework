package com.android.server.display;

import android.os.Binder;
import android.os.Handler;
import android.os.Parcel;
import android.os.UserHandle;
import android.util.Slog;

public class DisplayManagerServiceInjector {
    static final int MSG_RESET_SHORT_MODEL = 255;
    private static final String TAG = "DisplayManagerService";

    static boolean onTransact(DisplayManagerService displayManagerService, DisplayPowerController displayPowerController, Handler displayControllerHandler, DisplayPowerState displayPowerState, int code, Parcel data, Parcel reply, int flags) {
        if (code == 16777214) {
            return resetAutoBrightnessShortModel(displayControllerHandler, data);
        }
        return false;
    }

    static boolean resetAutoBrightnessShortModel(Handler displayControllerHandler, Parcel data) {
        data.enforceInterface("android.view.android.hardware.display.IDisplayManager");
        resetAutoBrightnessShortModelInternal(displayControllerHandler);
        return true;
    }

    private static void resetAutoBrightnessShortModelInternal(Handler displayControllerHandler) {
        if (UserHandle.getAppId(Binder.getCallingUid()) == 1000) {
            Slog.d(TAG, "reset AutoBrightness ShortModel");
            long token = Binder.clearCallingIdentity();
            if (displayControllerHandler != null) {
                try {
                    displayControllerHandler.obtainMessage(255).sendToTarget();
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(token);
                    throw th;
                }
            }
            Binder.restoreCallingIdentity(token);
            return;
        }
        throw new SecurityException("Only system uid can reset Short Model!");
    }
}
