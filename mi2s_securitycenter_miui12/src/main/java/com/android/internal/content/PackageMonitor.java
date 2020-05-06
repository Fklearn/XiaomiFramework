package com.android.internal.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;

public abstract class PackageMonitor extends BroadcastReceiver {
    public static final int PACKAGE_PERMANENT_CHANGE = 3;
    public static final int PACKAGE_TEMPORARY_CHANGE = 2;
    public static final int PACKAGE_UNCHANGED = 0;
    public static final int PACKAGE_UPDATING = 1;
    String[] mAppearingPackages;
    int mChangeType;
    int mChangeUserId;
    String[] mDisappearingPackages;
    Context mRegisteredContext;
    Handler mRegisteredHandler;
    boolean mSomePackagesChanged;

    public boolean anyPackagesAppearing() {
        return this.mAppearingPackages != null;
    }

    public boolean anyPackagesDisappearing() {
        return this.mDisappearingPackages != null;
    }

    public boolean didSomePackagesChange() {
        return this.mSomePackagesChanged;
    }

    public int getChangingUserId() {
        return this.mChangeUserId;
    }

    public String getPackageName(Intent intent) {
        return "";
    }

    public Handler getRegisteredHandler() {
        return this.mRegisteredHandler;
    }

    public int isPackageAppearing(String str) {
        String[] strArr = this.mAppearingPackages;
        if (strArr == null) {
            return 0;
        }
        for (int length = strArr.length - 1; length >= 0; length--) {
            if (str.equals(this.mAppearingPackages[length])) {
                return this.mChangeType;
            }
        }
        return 0;
    }

    public int isPackageDisappearing(String str) {
        return 0;
    }

    public boolean isPackageModified(String str) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isPackageUpdating(String str) {
        return false;
    }

    public boolean isReplacing() {
        return this.mChangeType == 1;
    }

    public void onBeginPackageChanges() {
    }

    public void onFinishPackageChanges() {
    }

    public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
        return false;
    }

    public void onHandleUserStop(Intent intent, int i) {
    }

    public void onPackageAdded(String str, int i) {
    }

    public void onPackageAppeared(String str, int i) {
    }

    public boolean onPackageChanged(String str, int i, String[] strArr) {
        return false;
    }

    public void onPackageDataCleared(String str, int i) {
    }

    public void onPackageDisappeared(String str, int i) {
    }

    public void onPackageModified(String str) {
    }

    public void onPackageRemoved(String str, int i) {
    }

    public void onPackageRemovedAllUsers(String str, int i) {
    }

    public void onPackageUpdateFinished(String str, int i) {
    }

    public void onPackageUpdateStarted(String str, int i) {
    }

    public void onPackagesAvailable(String[] strArr) {
    }

    public void onPackagesSuspended(String[] strArr) {
    }

    public void onPackagesUnavailable(String[] strArr) {
    }

    public void onPackagesUnsuspended(String[] strArr) {
    }

    public void onReceive(Context context, Intent intent) {
    }

    public void onSomePackagesChanged() {
    }

    public void onUidRemoved(int i) {
    }

    public void register(Context context, Looper looper, UserHandle userHandle, boolean z) {
    }

    public void register(Context context, Looper looper, boolean z) {
        register(context, looper, (UserHandle) null, z);
    }

    public void register(Context context, UserHandle userHandle, boolean z, Handler handler) {
    }

    public void unregister() {
        Context context = this.mRegisteredContext;
        if (context != null) {
            context.unregisterReceiver(this);
            this.mRegisteredContext = null;
            return;
        }
        throw new IllegalStateException("Not registered");
    }
}
