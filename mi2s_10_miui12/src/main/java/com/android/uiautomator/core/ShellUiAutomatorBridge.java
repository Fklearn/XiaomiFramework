package com.android.uiautomator.core;

import android.app.UiAutomation;
import android.hardware.display.DisplayManagerGlobal;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;

public class ShellUiAutomatorBridge extends UiAutomatorBridge {
    private static final String LOG_TAG = ShellUiAutomatorBridge.class.getSimpleName();

    public ShellUiAutomatorBridge(UiAutomation uiAutomation) {
        super(uiAutomation);
    }

    public Display getDefaultDisplay() {
        return DisplayManagerGlobal.getInstance().getRealDisplay(0);
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d A[SYNTHETIC, Splitter:B:24:0x006d] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0072 A[Catch:{ RemoteException -> 0x0077 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getSystemLongPressTime() {
        /*
            r15 = this;
            r0 = 0
            r2 = 0
            r3 = 0
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0077 }
            android.net.Uri r5 = android.provider.Settings.Secure.CONTENT_URI     // Catch:{ RemoteException -> 0x0077 }
            java.lang.String r5 = r5.getAuthority()     // Catch:{ RemoteException -> 0x0077 }
            android.os.Binder r6 = new android.os.Binder     // Catch:{ RemoteException -> 0x0077 }
            r6.<init>()     // Catch:{ RemoteException -> 0x0077 }
            r7 = 0
            java.lang.String r8 = "*uiautomator*"
            android.app.ContentProviderHolder r8 = r4.getContentProviderExternal(r5, r7, r6, r8)     // Catch:{ all -> 0x006a }
            if (r8 == 0) goto L_0x0053
            android.content.IContentProvider r9 = r8.provider     // Catch:{ all -> 0x006a }
            r10 = 0
            android.net.Uri r11 = android.provider.Settings.Secure.CONTENT_URI     // Catch:{ all -> 0x004f }
            java.lang.String r2 = "value"
            java.lang.String[] r12 = new java.lang.String[]{r2}     // Catch:{ all -> 0x004f }
            java.lang.String r2 = "name=?"
            java.lang.String r13 = "long_press_timeout"
            java.lang.String[] r13 = new java.lang.String[]{r13}     // Catch:{ all -> 0x004f }
            r14 = 0
            android.os.Bundle r13 = android.content.ContentResolver.createSqlQueryBundle(r2, r13, r14)     // Catch:{ all -> 0x004f }
            r14 = 0
            android.database.Cursor r2 = r9.query(r10, r11, r12, r13, r14)     // Catch:{ all -> 0x004f }
            r3 = r2
            boolean r2 = r3.moveToFirst()     // Catch:{ all -> 0x004f }
            if (r2 == 0) goto L_0x0045
            int r2 = r3.getInt(r7)     // Catch:{ all -> 0x004f }
            long r0 = (long) r2
        L_0x0045:
            r3.close()     // Catch:{ RemoteException -> 0x0077 }
            r4.removeContentProviderExternalAsUser(r5, r6, r7)     // Catch:{ RemoteException -> 0x0077 }
            return r0
        L_0x004f:
            r2 = move-exception
            r8 = r2
            r2 = r9
            goto L_0x006b
        L_0x0053:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch:{ all -> 0x006a }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x006a }
            r10.<init>()     // Catch:{ all -> 0x006a }
            java.lang.String r11 = "Could not find provider: "
            r10.append(r11)     // Catch:{ all -> 0x006a }
            r10.append(r5)     // Catch:{ all -> 0x006a }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x006a }
            r9.<init>(r10)     // Catch:{ all -> 0x006a }
            throw r9     // Catch:{ all -> 0x006a }
        L_0x006a:
            r8 = move-exception
        L_0x006b:
            if (r3 == 0) goto L_0x0070
            r3.close()     // Catch:{ RemoteException -> 0x0077 }
        L_0x0070:
            if (r2 == 0) goto L_0x0075
            r4.removeContentProviderExternalAsUser(r5, r6, r7)     // Catch:{ RemoteException -> 0x0077 }
        L_0x0075:
            throw r8     // Catch:{ RemoteException -> 0x0077 }
        L_0x0077:
            r2 = move-exception
            java.lang.String r3 = "Error reading long press timeout setting."
            java.lang.String r4 = LOG_TAG
            android.util.Log.e(r4, r3, r2)
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            r4.<init>(r3, r2)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.uiautomator.core.ShellUiAutomatorBridge.getSystemLongPressTime():long");
    }

    public int getRotation() {
        try {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window")).getDefaultDisplayRotation();
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting screen rotation", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isScreenOn() {
        try {
            return IPowerManager.Stub.asInterface(ServiceManager.getService("power")).isInteractive();
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting screen status", e);
            throw new RuntimeException(e);
        }
    }
}
