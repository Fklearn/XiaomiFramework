package com.android.server.policy.keyguard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.security.keystore.IKeystoreService;
import android.util.Slog;
import com.android.internal.policy.IKeyguardService;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.widget.LockPatternUtils;
import java.io.PrintWriter;

public class KeyguardStateMonitor extends IKeyguardStateCallback.Stub {
    private static final String TAG = "KeyguardStateMonitor";
    private final StateCallback mCallback;
    private int mCurrentUserId;
    private volatile boolean mHasLockscreenWallpaper = false;
    private volatile boolean mInputRestricted = true;
    private volatile boolean mIsShowing = true;
    IKeystoreService mKeystoreService;
    private final LockPatternUtils mLockPatternUtils;
    private volatile boolean mSimSecure = true;
    private volatile boolean mTrusted = false;

    public interface StateCallback {
        void onShowingChanged();

        void onTrustedChanged();

        void unblockScreenOn();
    }

    public KeyguardStateMonitor(Context context, IKeyguardService service, StateCallback callback) {
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mCallback = callback;
        this.mKeystoreService = IKeystoreService.Stub.asInterface(ServiceManager.getService("android.security.keystore"));
        try {
            service.addStateMonitorCallback(this);
        } catch (RemoteException e) {
            Slog.w(TAG, "Remote Exception", e);
        }
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public boolean isSecure(int userId) {
        return this.mLockPatternUtils.isSecure(userId) || this.mSimSecure;
    }

    public boolean isInputRestricted() {
        return this.mInputRestricted;
    }

    public boolean isTrusted() {
        return this.mTrusted;
    }

    public boolean hasLockscreenWallpaper() {
        return this.mHasLockscreenWallpaper;
    }

    public void onShowingStateChanged(boolean showing) {
        this.mIsShowing = showing;
        this.mCallback.onShowingChanged();
        int retry = 2;
        while (retry > 0) {
            try {
                this.mKeystoreService.onKeyguardVisibilityChanged(showing, this.mCurrentUserId);
                return;
            } catch (RemoteException e) {
                if (retry == 2) {
                    Slog.w(TAG, "Error informing keystore of screen lock. Keystore may have died -> refreshing service token and retrying");
                    this.mKeystoreService = IKeystoreService.Stub.asInterface(ServiceManager.getService("android.security.keystore"));
                } else {
                    Slog.e(TAG, "Error informing keystore of screen lock after retrying once", e);
                }
                retry--;
            }
        }
    }

    public void onSimSecureStateChanged(boolean simSecure) {
        this.mSimSecure = simSecure;
    }

    public synchronized void setCurrentUser(int userId) {
        this.mCurrentUserId = userId;
    }

    private synchronized int getCurrentUser() {
        return this.mCurrentUserId;
    }

    public void onInputRestrictedStateChanged(boolean inputRestricted) {
        this.mInputRestricted = inputRestricted;
    }

    public void onTrustedChanged(boolean trusted) {
        this.mTrusted = trusted;
        this.mCallback.onTrustedChanged();
    }

    public void onHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) {
        this.mHasLockscreenWallpaper = hasLockscreenWallpaper;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code != 255) {
            return KeyguardStateMonitor.super.onTransact(code, data, reply, flags);
        }
        data.enforceInterface("com.android.internal.policy.IKeyguardStateCallback");
        this.mCallback.unblockScreenOn();
        reply.writeNoException();
        return true;
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.println(prefix + TAG);
        String prefix2 = prefix + "  ";
        pw.println(prefix2 + "mIsShowing=" + this.mIsShowing);
        pw.println(prefix2 + "mSimSecure=" + this.mSimSecure);
        pw.println(prefix2 + "mInputRestricted=" + this.mInputRestricted);
        pw.println(prefix2 + "mTrusted=" + this.mTrusted);
        pw.println(prefix2 + "mCurrentUserId=" + this.mCurrentUserId);
    }
}
