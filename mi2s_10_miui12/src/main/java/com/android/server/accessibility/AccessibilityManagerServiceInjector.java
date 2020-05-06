package com.android.server.accessibility;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import java.util.ArrayList;

class AccessibilityManagerServiceInjector {
    private static ContentResolver mContentResolver;
    private static ArrayList<String> mExceptedPakcages;
    private static ContentObserver mObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean selfChange) {
            AccessibilityManagerServiceInjector.onChanged();
        }
    };

    AccessibilityManagerServiceInjector() {
    }

    public static void init(ContentResolver contentResolver) {
        mExceptedPakcages = new ArrayList<>();
        mContentResolver = contentResolver;
        mContentResolver.registerContentObserver(Settings.Secure.getUriFor("package_accessibillity_service_ignored"), true, mObserver);
        onChanged();
    }

    public static void destroy() {
        ContentResolver contentResolver = mContentResolver;
        if (contentResolver != null) {
            contentResolver.unregisterContentObserver(mObserver);
            mContentResolver = null;
        }
    }

    public static boolean isExcepted(CharSequence pkgName) {
        if (pkgName == null) {
            return false;
        }
        return isExcepted(pkgName.toString());
    }

    public static boolean isExcepted(String pkgName) {
        ArrayList<String> arrayList;
        if (pkgName == null || (arrayList = mExceptedPakcages) == null || !arrayList.contains(pkgName)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void onChanged() {
        mExceptedPakcages.clear();
        String pkgName = Settings.Secure.getString(mContentResolver, "package_accessibillity_service_ignored");
        if (pkgName != null && !pkgName.isEmpty()) {
            mExceptedPakcages.add(pkgName);
        }
    }
}
