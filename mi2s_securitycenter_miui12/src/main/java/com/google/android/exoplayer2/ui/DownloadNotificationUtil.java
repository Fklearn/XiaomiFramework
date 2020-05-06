package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

public final class DownloadNotificationUtil {
    @StringRes
    private static final int NULL_STRING_ID = 0;

    private DownloadNotificationUtil() {
    }

    public static Notification buildDownloadCompletedNotification(Context context, @DrawableRes int i, String str, @Nullable PendingIntent pendingIntent, @Nullable String str2) {
        return newNotificationBuilder(context, i, str, pendingIntent, str2, R.string.exo_download_completed).build();
    }

    public static Notification buildDownloadFailedNotification(Context context, @DrawableRes int i, String str, @Nullable PendingIntent pendingIntent, @Nullable String str2) {
        return newNotificationBuilder(context, i, str, pendingIntent, str2, R.string.exo_download_failed).build();
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x005a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Notification buildProgressNotification(android.content.Context r15, @android.support.annotation.DrawableRes int r16, java.lang.String r17, @android.support.annotation.Nullable android.app.PendingIntent r18, @android.support.annotation.Nullable java.lang.String r19, com.google.android.exoplayer2.offline.DownloadManager.TaskState[] r20) {
        /*
            r0 = r20
            int r1 = r0.length
            r2 = 1
            r3 = 0
            r4 = 0
            r8 = r2
            r5 = r3
            r7 = r5
            r6 = r4
            r4 = r7
        L_0x000b:
            if (r4 >= r1) goto L_0x0035
            r9 = r0[r4]
            com.google.android.exoplayer2.offline.DownloadAction r10 = r9.action
            boolean r10 = r10.isRemoveAction
            if (r10 != 0) goto L_0x0032
            int r10 = r9.state
            if (r10 == r2) goto L_0x001a
            goto L_0x0032
        L_0x001a:
            float r10 = r9.downloadPercentage
            r11 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r11 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r11 == 0) goto L_0x0024
            float r6 = r6 + r10
            r8 = r3
        L_0x0024:
            long r9 = r9.downloadedBytes
            r11 = 0
            int r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r9 <= 0) goto L_0x002e
            r9 = r2
            goto L_0x002f
        L_0x002e:
            r9 = r3
        L_0x002f:
            r7 = r7 | r9
            int r5 = r5 + 1
        L_0x0032:
            int r4 = r4 + 1
            goto L_0x000b
        L_0x0035:
            if (r5 <= 0) goto L_0x0039
            r1 = r2
            goto L_0x003a
        L_0x0039:
            r1 = r3
        L_0x003a:
            if (r1 == 0) goto L_0x0040
            int r0 = com.google.android.exoplayer2.ui.R.string.exo_download_downloading
        L_0x003e:
            r14 = r0
            goto L_0x0047
        L_0x0040:
            int r0 = r0.length
            if (r0 <= 0) goto L_0x0046
            int r0 = com.google.android.exoplayer2.ui.R.string.exo_download_removing
            goto L_0x003e
        L_0x0046:
            r14 = r3
        L_0x0047:
            r9 = r15
            r10 = r16
            r11 = r17
            r12 = r18
            r13 = r19
            android.support.v4.app.NotificationCompat$Builder r0 = newNotificationBuilder(r9, r10, r11, r12, r13, r14)
            if (r1 == 0) goto L_0x005a
            float r4 = (float) r5
            float r6 = r6 / r4
            int r4 = (int) r6
            goto L_0x005b
        L_0x005a:
            r4 = r3
        L_0x005b:
            if (r1 == 0) goto L_0x0064
            if (r8 == 0) goto L_0x0062
            if (r7 == 0) goto L_0x0062
            goto L_0x0064
        L_0x0062:
            r1 = r3
            goto L_0x0065
        L_0x0064:
            r1 = r2
        L_0x0065:
            r5 = 100
            r0.setProgress(r5, r4, r1)
            r0.setOngoing(r2)
            r0.setShowWhen(r3)
            android.app.Notification r0 = r0.build()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.DownloadNotificationUtil.buildProgressNotification(android.content.Context, int, java.lang.String, android.app.PendingIntent, java.lang.String, com.google.android.exoplayer2.offline.DownloadManager$TaskState[]):android.app.Notification");
    }

    private static NotificationCompat.Builder newNotificationBuilder(Context context, @DrawableRes int i, String str, @Nullable PendingIntent pendingIntent, @Nullable String str2, @StringRes int i2) {
        NotificationCompat.Builder smallIcon = new NotificationCompat.Builder(context, str).setSmallIcon(i);
        if (i2 != 0) {
            smallIcon.setContentTitle(context.getResources().getString(i2));
        }
        if (pendingIntent != null) {
            smallIcon.setContentIntent(pendingIntent);
        }
        if (str2 != null) {
            smallIcon.setStyle(new NotificationCompat.BigTextStyle().bigText(str2));
        }
        return smallIcon;
    }
}
