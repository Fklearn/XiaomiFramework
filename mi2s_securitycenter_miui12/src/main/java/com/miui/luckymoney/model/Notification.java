package com.miui.luckymoney.model;

import android.os.Build;

public class Notification {
    private static final String EXTRA_ANDROID_TEXT = "android.text";
    private static final String EXTRA_ANDROID_TITLE = "android.title";
    public final int id;
    public final android.app.Notification notification;
    public final String packageName;
    public final String tag;

    public Notification(String str, int i, String str2, android.app.Notification notification2) {
        this.packageName = str;
        this.id = i;
        this.tag = str2;
        this.notification = notification2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r0 = r3.notification.extras;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getNotificationContent() {
        /*
            r3 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 19
            if (r0 < r2) goto L_0x0014
            android.app.Notification r0 = r3.notification
            android.os.Bundle r0 = r0.extras
            if (r0 == 0) goto L_0x0014
            java.lang.String r2 = "android.text"
            java.lang.CharSequence r0 = r0.getCharSequence(r2)
            goto L_0x0015
        L_0x0014:
            r0 = r1
        L_0x0015:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x001f
            android.app.Notification r0 = r3.notification
            java.lang.CharSequence r0 = r0.tickerText
        L_0x001f:
            if (r0 != 0) goto L_0x0022
            goto L_0x0026
        L_0x0022:
            java.lang.String r1 = r0.toString()
        L_0x0026:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.model.Notification.getNotificationContent():java.lang.String");
    }

    public String getNotificationTitle() {
        CharSequence charSequence = Build.VERSION.SDK_INT >= 19 ? this.notification.extras.getCharSequence(EXTRA_ANDROID_TITLE) : null;
        if (charSequence == null) {
            return null;
        }
        return charSequence.toString();
    }
}
