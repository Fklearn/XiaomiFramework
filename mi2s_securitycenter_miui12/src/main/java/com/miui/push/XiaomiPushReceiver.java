package com.miui.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.j.v;
import com.miui.securitycenter.R;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class XiaomiPushReceiver extends PushMessageReceiver {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f7436a = "XiaomiPushReceiver";

    public static void a(Context context, MiPushMessage miPushMessage) {
        if (context != null && miPushMessage != null) {
            a.a(new c(miPushMessage, context));
        }
    }

    /* access modifiers changed from: private */
    public static void b(Context context, PendingIntent pendingIntent, String str, String str2) {
        Notification.Builder a2 = v.a(context, "securitycenter_xiaomi_push");
        a2.setSmallIcon(R.drawable.security_small_icon);
        a2.setContentTitle(str);
        a2.setContentText(str2);
        a2.setWhen(System.currentTimeMillis());
        a2.setContentIntent(pendingIntent);
        a2.setAutoCancel(true);
        Notification build = a2.build();
        build.tickerText = str + ":" + str2;
        build.flags = build.flags | 16;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "securitycenter_xiaomi_push", context.getResources().getString(R.string.notify_channel_mipush), 3);
        notificationManager.notify(20003, build);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCommandResult(android.content.Context r8, com.xiaomi.mipush.sdk.MiPushCommandMessage r9) {
        /*
            r7 = this;
            java.lang.String r8 = r9.getCommand()
            java.lang.String r0 = "register"
            boolean r0 = r0.equals(r8)
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0042
            long r4 = r9.getResultCode()
            int r8 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x0039
            java.util.List r8 = r9.getCommandArguments()
            java.lang.Object r8 = r8.get(r1)
            java.lang.String r8 = (java.lang.String) r8
            java.lang.String r9 = f7436a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCommandResult register success : "
        L_0x002a:
            r0.append(r1)
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            android.util.Log.d(r9, r8)
            goto L_0x0122
        L_0x0039:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult register failed"
        L_0x003d:
            android.util.Log.d(r8, r9)
            goto L_0x0122
        L_0x0042:
            java.lang.String r0 = "set-alias"
            boolean r0 = r0.equals(r8)
            if (r0 == 0) goto L_0x006b
            long r4 = r9.getResultCode()
            int r8 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x0066
            java.util.List r8 = r9.getCommandArguments()
            java.lang.Object r8 = r8.get(r1)
            java.lang.String r8 = (java.lang.String) r8
            java.lang.String r9 = f7436a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCommandResult alias : "
            goto L_0x002a
        L_0x0066:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult set alias failed"
            goto L_0x003d
        L_0x006b:
            java.lang.String r0 = "unset-alias"
            boolean r0 = r0.equals(r8)
            if (r0 == 0) goto L_0x0094
            long r4 = r9.getResultCode()
            int r8 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x008f
            java.util.List r8 = r9.getCommandArguments()
            java.lang.Object r8 = r8.get(r1)
            java.lang.String r8 = (java.lang.String) r8
            java.lang.String r9 = f7436a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCommandResult unset alias : "
            goto L_0x002a
        L_0x008f:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult unset alias failed"
            goto L_0x003d
        L_0x0094:
            java.lang.String r0 = "subscribe-topic"
            boolean r0 = r0.equals(r8)
            r4 = 0
            if (r0 == 0) goto L_0x00d3
            long r5 = r9.getResultCode()
            int r8 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x00cd
            java.util.List r8 = r9.getCommandArguments()
            if (r8 == 0) goto L_0x00b8
            int r9 = r8.size()
            if (r9 <= 0) goto L_0x00b8
            java.lang.Object r8 = r8.get(r1)
            r4 = r8
            java.lang.String r4 = (java.lang.String) r4
        L_0x00b8:
            java.lang.String r8 = f7436a
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "onCommandResult subscribe topic : "
        L_0x00c1:
            r9.append(r0)
            r9.append(r4)
            java.lang.String r9 = r9.toString()
            goto L_0x003d
        L_0x00cd:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult subscribe topic failed"
            goto L_0x003d
        L_0x00d3:
            java.lang.String r0 = "unsubscibe-topic"
            boolean r0 = r0.equals(r8)
            if (r0 == 0) goto L_0x0106
            long r5 = r9.getResultCode()
            int r8 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x0100
            java.util.List r8 = r9.getCommandArguments()
            if (r8 == 0) goto L_0x00f6
            int r9 = r8.size()
            if (r9 <= 0) goto L_0x00f6
            java.lang.Object r8 = r8.get(r1)
            r4 = r8
            java.lang.String r4 = (java.lang.String) r4
        L_0x00f6:
            java.lang.String r8 = f7436a
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "onCommandResult unsubscribe topic : "
            goto L_0x00c1
        L_0x0100:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult unsubscribe topic failed"
            goto L_0x003d
        L_0x0106:
            java.lang.String r0 = "set-account"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x0122
            long r8 = r9.getResultCode()
            int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x011c
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult SET_ACCOUNT  success"
            goto L_0x003d
        L_0x011c:
            java.lang.String r8 = f7436a
            java.lang.String r9 = "onCommandResult SET_ACCOUNT  failed"
            goto L_0x003d
        L_0x0122:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.push.XiaomiPushReceiver.onCommandResult(android.content.Context, com.xiaomi.mipush.sdk.MiPushCommandMessage):void");
    }

    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        String content = miPushMessage.getContent();
        int passThrough = miPushMessage.getPassThrough();
        Log.i(f7436a, "onReceivePassThroughMessage");
        if (passThrough == 1 && !TextUtils.isEmpty(content)) {
            a(context, miPushMessage);
        }
    }
}
