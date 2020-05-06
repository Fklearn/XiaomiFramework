package com.android.server.notification;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import java.util.Collections;

public class NotificationShellCmd extends ShellCommand {
    public static final String CHANNEL_ID = "shellcmd";
    public static final int CHANNEL_IMP = 3;
    public static final String CHANNEL_NAME = "Shell command";
    public static final int NOTIFICATION_ID = 1138;
    public static final String NOTIFICATION_PACKAGE = "com.android.shell";
    private static final String NOTIFY_USAGE = "usage: cmd notification post [flags] <tag> <text>\n\nflags:\n  -h|--help\n  -v|--verbose\n  -t|--title <text>\n  -i|--icon <iconspec>\n  -I|--large-icon <iconspec>\n  -S|--style <style> [styleargs]\n  -c|--content-intent <intentspec>\n\nstyles: (default none)\n  bigtext\n  bigpicture --picture <iconspec>\n  inbox --line <text> --line <text> ...\n  messaging --conversation <title> --message <who>:<text> ...\n  media\n\nan <iconspec> is one of\n  file:///data/local/tmp/<img.png>\n  content://<provider>/<path>\n  @[<package>:]drawable/<img>\n  data:base64,<B64DATA==>\n\nan <intentspec> is (broadcast|service|activity) <args>\n  <args> are as described in `am start`";
    private static final String USAGE = "usage: cmd notification SUBCMD [args]\n\nSUBCMDs:\n  allow_listener COMPONENT [user_id (current user if not specified)]\n  disallow_listener COMPONENT [user_id (current user if not specified)]\n  allow_assistant COMPONENT [user_id (current user if not specified)]\n  remove_assistant COMPONENT [user_id (current user if not specified)]\n  allow_dnd PACKAGE [user_id (current user if not specified)]\n  disallow_dnd PACKAGE [user_id (current user if not specified)]\n  suspend_package PACKAGE\n  unsuspend_package PACKAGE\n  reset_assistant_user_set [user_id (current user if not specified)]\n  get_approved_assistant [user_id (current user if not specified)]\n  post [--help | flags] TAG TEXT";
    private final INotificationManager mBinderService;
    private final NotificationManagerService mDirectService;

    public NotificationShellCmd(NotificationManagerService service) {
        this.mDirectService = service;
        this.mBinderService = service.getBinderService();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r8) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0007
            int r0 = r7.handleDefaultCommands(r8)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            r1 = 45
            r2 = 95
            r3 = 0
            java.lang.String r1 = r8.replace(r1, r2)     // Catch:{ Exception -> 0x0211 }
            int r2 = r1.hashCode()     // Catch:{ Exception -> 0x0211 }
            r4 = 1
            r5 = -1
            switch(r2) {
                case -1325770982: goto L_0x00a5;
                case -1039689911: goto L_0x0099;
                case -506770550: goto L_0x008e;
                case -432999190: goto L_0x0084;
                case -429832618: goto L_0x007a;
                case -414550305: goto L_0x006f;
                case 3446944: goto L_0x0063;
                case 372345636: goto L_0x0059;
                case 393969475: goto L_0x004e;
                case 683492127: goto L_0x0041;
                case 1257269496: goto L_0x0036;
                case 1570441869: goto L_0x002a;
                case 2110474600: goto L_0x001f;
                default: goto L_0x001d;
            }     // Catch:{ Exception -> 0x0211 }
        L_0x001d:
            goto L_0x00af
        L_0x001f:
            java.lang.String r2 = "allow_assistant"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 4
            goto L_0x00b0
        L_0x002a:
            java.lang.String r2 = "distract_package"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 8
            goto L_0x00b0
        L_0x0036:
            java.lang.String r2 = "disallow_listener"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 3
            goto L_0x00b0
        L_0x0041:
            java.lang.String r2 = "reset_assistant_user_set"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 9
            goto L_0x00b0
        L_0x004e:
            java.lang.String r2 = "suspend_package"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 6
            goto L_0x00b0
        L_0x0059:
            java.lang.String r2 = "allow_dnd"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = r3
            goto L_0x00b0
        L_0x0063:
            java.lang.String r2 = "post"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 11
            goto L_0x00b0
        L_0x006f:
            java.lang.String r2 = "get_approved_assistant"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 10
            goto L_0x00b0
        L_0x007a:
            java.lang.String r2 = "disallow_dnd"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = r4
            goto L_0x00b0
        L_0x0084:
            java.lang.String r2 = "allow_listener"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 2
            goto L_0x00b0
        L_0x008e:
            java.lang.String r2 = "unsuspend_package"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 7
            goto L_0x00b0
        L_0x0099:
            java.lang.String r2 = "notify"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 12
            goto L_0x00b0
        L_0x00a5:
            java.lang.String r2 = "disallow_assistant"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x0211 }
            if (r1 == 0) goto L_0x001d
            r1 = 5
            goto L_0x00b0
        L_0x00af:
            r1 = r5
        L_0x00b0:
            java.lang.String r2 = "Invalid assistant - must be a ComponentName"
            java.lang.String r6 = "Invalid listener - must be a ComponentName"
            switch(r1) {
                case 0: goto L_0x01f2;
                case 1: goto L_0x01d5;
                case 2: goto L_0x01ae;
                case 3: goto L_0x0187;
                case 4: goto L_0x015f;
                case 5: goto L_0x0137;
                case 6: goto L_0x012c;
                case 7: goto L_0x0121;
                case 8: goto L_0x0108;
                case 9: goto L_0x00ee;
                case 10: goto L_0x00c2;
                case 11: goto L_0x00bd;
                case 12: goto L_0x00bd;
                default: goto L_0x00b7;
            }
        L_0x00b7:
            int r1 = r7.handleDefaultCommands(r8)     // Catch:{ Exception -> 0x0211 }
            goto L_0x0210
        L_0x00bd:
            r7.doNotify(r0)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x00c2:
            int r1 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r2 == 0) goto L_0x00d5
            java.lang.String r2 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0211 }
            r1 = r2
        L_0x00d5:
            com.android.server.notification.NotificationManagerService r2 = r7.mDirectService     // Catch:{ Exception -> 0x0211 }
            android.content.ComponentName r2 = r2.getApprovedAssistant(r1)     // Catch:{ Exception -> 0x0211 }
            if (r2 != 0) goto L_0x00e5
            java.lang.String r4 = "null"
            r0.println(r4)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x00e5:
            java.lang.String r4 = r2.flattenToString()     // Catch:{ Exception -> 0x0211 }
            r0.println(r4)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x00ee:
            int r1 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r2 == 0) goto L_0x0101
            java.lang.String r2 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0211 }
            r1 = r2
        L_0x0101:
            com.android.server.notification.NotificationManagerService r2 = r7.mDirectService     // Catch:{ Exception -> 0x0211 }
            r2.resetAssistantUserSet(r1)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x0108:
            com.android.server.notification.NotificationManagerService r1 = r7.mDirectService     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0211 }
            java.lang.String r4 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r5 = ","
            java.lang.String[] r4 = r4.split(r5)     // Catch:{ Exception -> 0x0211 }
            r1.simulatePackageDistractionBroadcast(r2, r4)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x0121:
            com.android.server.notification.NotificationManagerService r1 = r7.mDirectService     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            r1.simulatePackageSuspendBroadcast(r3, r2)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x012c:
            com.android.server.notification.NotificationManagerService r1 = r7.mDirectService     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            r1.simulatePackageSuspendBroadcast(r4, r2)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x0137:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r1)     // Catch:{ Exception -> 0x0211 }
            if (r1 != 0) goto L_0x0145
            r0.println(r2)     // Catch:{ Exception -> 0x0211 }
            return r5
        L_0x0145:
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r4 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r4 == 0) goto L_0x0158
            java.lang.String r4 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0211 }
            r2 = r4
        L_0x0158:
            android.app.INotificationManager r4 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r4.setNotificationAssistantAccessGrantedForUser(r1, r2, r3)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x015f:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r1)     // Catch:{ Exception -> 0x0211 }
            if (r1 != 0) goto L_0x016d
            r0.println(r2)     // Catch:{ Exception -> 0x0211 }
            return r5
        L_0x016d:
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r5 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r5 == 0) goto L_0x0180
            java.lang.String r5 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0211 }
            r2 = r5
        L_0x0180:
            android.app.INotificationManager r5 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r5.setNotificationAssistantAccessGrantedForUser(r1, r2, r4)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x0187:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r1)     // Catch:{ Exception -> 0x0211 }
            if (r1 != 0) goto L_0x0195
            r0.println(r6)     // Catch:{ Exception -> 0x0211 }
            return r5
        L_0x0195:
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r4 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r4 == 0) goto L_0x01a8
            java.lang.String r4 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0211 }
            r2 = r4
        L_0x01a8:
            android.app.INotificationManager r4 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r4.setNotificationListenerAccessGrantedForUser(r1, r2, r3)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x01ae:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r1)     // Catch:{ Exception -> 0x0211 }
            if (r1 != 0) goto L_0x01bc
            r0.println(r6)     // Catch:{ Exception -> 0x0211 }
            return r5
        L_0x01bc:
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r5 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r5 == 0) goto L_0x01cf
            java.lang.String r5 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0211 }
            r2 = r5
        L_0x01cf:
            android.app.INotificationManager r5 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r5.setNotificationListenerAccessGrantedForUser(r1, r2, r4)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x01d5:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r4 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r4 == 0) goto L_0x01ec
            java.lang.String r4 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0211 }
            r2 = r4
        L_0x01ec:
            android.app.INotificationManager r4 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r4.setNotificationPolicyAccessGrantedForUser(r1, r2, r3)     // Catch:{ Exception -> 0x0211 }
            goto L_0x020f
        L_0x01f2:
            java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r5 = r7.peekNextArg()     // Catch:{ Exception -> 0x0211 }
            if (r5 == 0) goto L_0x0209
            java.lang.String r5 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0211 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0211 }
            r2 = r5
        L_0x0209:
            android.app.INotificationManager r5 = r7.mBinderService     // Catch:{ Exception -> 0x0211 }
            r5.setNotificationPolicyAccessGrantedForUser(r1, r2, r4)     // Catch:{ Exception -> 0x0211 }
        L_0x020f:
            goto L_0x0231
        L_0x0210:
            return r1
        L_0x0211:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Error occurred. Check logcat for details. "
            r2.append(r4)
            java.lang.String r4 = r1.getMessage()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r0.println(r2)
            java.lang.String r2 = "NotificationService"
            java.lang.String r4 = "Error running shell command"
            android.util.Slog.e(r2, r4, r1)
        L_0x0231:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationShellCmd.onCommand(java.lang.String):int");
    }

    /* access modifiers changed from: package-private */
    public void ensureChannel() throws RemoteException {
        int uid = Binder.getCallingUid();
        int userid = UserHandle.getCallingUserId();
        long token = Binder.clearCallingIdentity();
        try {
            if (this.mBinderService.getNotificationChannelForPackage(NOTIFICATION_PACKAGE, uid, CHANNEL_ID, false) == null) {
                NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, 3);
                Slog.v("NotificationService", "creating shell channel for user " + userid + " uid " + uid + ": " + chan);
                this.mBinderService.createNotificationChannelsForPackage(NOTIFICATION_PACKAGE, uid, new ParceledListSlice(Collections.singletonList(chan)));
                StringBuilder sb = new StringBuilder();
                sb.append("created channel: ");
                sb.append(this.mBinderService.getNotificationChannelForPackage(NOTIFICATION_PACKAGE, uid, CHANNEL_ID, false));
                Slog.v("NotificationService", sb.toString());
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: package-private */
    public Icon parseIcon(Resources res, String encoded) throws IllegalArgumentException {
        if (TextUtils.isEmpty(encoded)) {
            return null;
        }
        if (encoded.startsWith(SliceClientPermissions.SliceAuthority.DELIMITER)) {
            encoded = "file://" + encoded;
        }
        if (encoded.startsWith("http:") || encoded.startsWith("https:") || encoded.startsWith("content:") || encoded.startsWith("file:") || encoded.startsWith("android.resource:")) {
            return Icon.createWithContentUri(Uri.parse(encoded));
        }
        if (encoded.startsWith("@")) {
            int resid = res.getIdentifier(encoded.substring(1), "drawable", PackageManagerService.PLATFORM_PACKAGE_NAME);
            if (resid != 0) {
                return Icon.createWithResource(res, resid);
            }
        } else if (encoded.startsWith("data:")) {
            byte[] bits = Base64.decode(encoded.substring(encoded.indexOf(44) + 1), 0);
            return Icon.createWithData(bits, 0, bits.length);
        }
        return null;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x03d3  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x03d6  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0422  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0559  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0561  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int doNotify(java.io.PrintWriter r32) throws android.os.RemoteException, java.net.URISyntaxException {
        /*
            r31 = this;
            r1 = r31
            r2 = r32
            com.android.server.notification.NotificationManagerService r0 = r1.mDirectService
            android.content.Context r9 = r0.getContext()
            android.content.res.Resources r10 = r9.getResources()
            android.app.Notification$Builder r0 = new android.app.Notification$Builder
            java.lang.String r3 = "shellcmd"
            r0.<init>(r9, r3)
            r11 = r0
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r13 = r0
            r14 = r3
            r15 = r4
            r16 = r6
            r12 = r8
            r8 = r5
        L_0x0025:
            java.lang.String r0 = r31.getNextOption()
            r6 = r0
            java.lang.String r3 = "usage: cmd notification post [flags] <tag> <text>\n\nflags:\n  -h|--help\n  -v|--verbose\n  -t|--title <text>\n  -i|--icon <iconspec>\n  -I|--large-icon <iconspec>\n  -S|--style <style> [styleargs]\n  -c|--content-intent <intentspec>\n\nstyles: (default none)\n  bigtext\n  bigpicture --picture <iconspec>\n  inbox --line <text> --line <text> ...\n  messaging --conversation <title> --message <who>:<text> ...\n  media\n\nan <iconspec> is one of\n  file:///data/local/tmp/<img.png>\n  content://<provider>/<path>\n  @[<package>:]drawable/<img>\n  data:base64,<B64DATA==>\n\nan <intentspec> is (broadcast|service|activity) <args>\n  <args> are as described in `am start`"
            if (r0 == 0) goto L_0x04ac
            r0 = 0
            int r17 = r6.hashCode()
            r5 = 2
            switch(r17) {
                case -1954060697: goto L_0x018a;
                case -1613915119: goto L_0x017f;
                case -1613324104: goto L_0x0175;
                case -1210178960: goto L_0x016a;
                case -1183762788: goto L_0x015e;
                case -853380573: goto L_0x0153;
                case -45879957: goto L_0x0149;
                case 1468: goto L_0x013f;
                case 1478: goto L_0x0134;
                case 1494: goto L_0x0129;
                case 1499: goto L_0x011d;
                case 1500: goto L_0x0111;
                case 1511: goto L_0x0106;
                case 1513: goto L_0x00fb;
                case 3226745: goto L_0x00ee;
                case 43017097: goto L_0x00e2;
                case 110371416: goto L_0x00d6;
                case 704999290: goto L_0x00ca;
                case 705941520: goto L_0x00be;
                case 758833716: goto L_0x00b1;
                case 808239966: goto L_0x00a5;
                case 1216250940: goto L_0x0099;
                case 1247228052: goto L_0x008e;
                case 1270815917: goto L_0x0082;
                case 1271769229: goto L_0x0076;
                case 1333069025: goto L_0x006a;
                case 1333096985: goto L_0x005e;
                case 1333192084: goto L_0x0052;
                case 1737088994: goto L_0x0047;
                case 1993764811: goto L_0x003a;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x0195
        L_0x003a:
            java.lang.String r4 = "large-icon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 9
            goto L_0x0196
        L_0x0047:
            java.lang.String r4 = "--verbose"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 1
            goto L_0x0196
        L_0x0052:
            java.lang.String r4 = "--line"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 24
            goto L_0x0196
        L_0x005e:
            java.lang.String r4 = "--icon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 11
            goto L_0x0196
        L_0x006a:
            java.lang.String r4 = "--help"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 28
            goto L_0x0196
        L_0x0076:
            java.lang.String r4 = "--bigtext"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 21
            goto L_0x0196
        L_0x0082:
            java.lang.String r4 = "--bigText"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 20
            goto L_0x0196
        L_0x008e:
            java.lang.String r4 = "--largeicon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 7
            goto L_0x0196
        L_0x0099:
            java.lang.String r4 = "--intent"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 16
            goto L_0x0196
        L_0x00a5:
            java.lang.String r4 = "--picture"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 23
            goto L_0x0196
        L_0x00b1:
            java.lang.String r4 = "largeicon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 8
            goto L_0x0196
        L_0x00be:
            java.lang.String r4 = "--content-intent"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 14
            goto L_0x0196
        L_0x00ca:
            java.lang.String r4 = "--big-text"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 22
            goto L_0x0196
        L_0x00d6:
            java.lang.String r4 = "title"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 4
            goto L_0x0196
        L_0x00e2:
            java.lang.String r4 = "--wtf"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 29
            goto L_0x0196
        L_0x00ee:
            java.lang.String r4 = "icon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 12
            goto L_0x0196
        L_0x00fb:
            java.lang.String r4 = "-v"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 0
            goto L_0x0196
        L_0x0106:
            java.lang.String r4 = "-t"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = r5
            goto L_0x0196
        L_0x0111:
            java.lang.String r4 = "-i"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 10
            goto L_0x0196
        L_0x011d:
            java.lang.String r4 = "-h"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 27
            goto L_0x0196
        L_0x0129:
            java.lang.String r4 = "-c"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 13
            goto L_0x0196
        L_0x0134:
            java.lang.String r4 = "-S"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 18
            goto L_0x0196
        L_0x013f:
            java.lang.String r4 = "-I"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 5
            goto L_0x0196
        L_0x0149:
            java.lang.String r4 = "--large-icon"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 6
            goto L_0x0196
        L_0x0153:
            java.lang.String r4 = "--conversation"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 26
            goto L_0x0196
        L_0x015e:
            java.lang.String r4 = "intent"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 17
            goto L_0x0196
        L_0x016a:
            java.lang.String r4 = "content-intent"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 15
            goto L_0x0196
        L_0x0175:
            java.lang.String r4 = "--title"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 3
            goto L_0x0196
        L_0x017f:
            java.lang.String r4 = "--style"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 19
            goto L_0x0196
        L_0x018a:
            java.lang.String r4 = "--message"
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x0038
            r4 = 25
            goto L_0x0196
        L_0x0195:
            r4 = -1
        L_0x0196:
            switch(r4) {
                case 0: goto L_0x04a0;
                case 1: goto L_0x04a0;
                case 2: goto L_0x048b;
                case 3: goto L_0x048b;
                case 4: goto L_0x048b;
                case 5: goto L_0x0454;
                case 6: goto L_0x0454;
                case 7: goto L_0x0454;
                case 8: goto L_0x0454;
                case 9: goto L_0x0454;
                case 10: goto L_0x044b;
                case 11: goto L_0x044b;
                case 12: goto L_0x044b;
                case 13: goto L_0x038c;
                case 14: goto L_0x038c;
                case 15: goto L_0x038c;
                case 16: goto L_0x038c;
                case 17: goto L_0x038c;
                case 18: goto L_0x02c3;
                case 19: goto L_0x02c3;
                case 20: goto L_0x02a6;
                case 21: goto L_0x02a6;
                case 22: goto L_0x02a6;
                case 23: goto L_0x0241;
                case 24: goto L_0x0224;
                case 25: goto L_0x01b7;
                case 26: goto L_0x019e;
                default: goto L_0x0199;
            }
        L_0x0199:
            r2.println(r3)
            r3 = 0
            return r3
        L_0x019e:
            if (r7 == 0) goto L_0x01af
            java.lang.String r3 = r31.getNextArgRequired()
            r7.setConversationTitle(r3)
            r20 = r6
            r17 = r7
            r19 = r8
            goto L_0x049b
        L_0x01af:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "--conversation requires --style messaging"
            r3.<init>(r4)
            throw r3
        L_0x01b7:
            if (r7 == 0) goto L_0x021a
            java.lang.String r3 = r31.getNextArgRequired()
            java.lang.String r4 = ":"
            java.lang.String[] r4 = r3.split(r4, r5)
            int r5 = r4.length
            r23 = r0
            r0 = 1
            if (r5 <= r0) goto L_0x01e2
            r0 = r4[r0]
            r24 = r6
            long r5 = java.lang.System.currentTimeMillis()
            r19 = r3
            r18 = 0
            r3 = r4[r18]
            r7.addMessage(r0, r5, r3)
            r17 = r7
            r19 = r8
            r20 = r24
            goto L_0x049b
        L_0x01e2:
            r19 = r3
            r24 = r6
            r18 = 0
            r0 = r4[r18]
            long r5 = java.lang.System.currentTimeMillis()
            r20 = r4
            r3 = 2
            java.lang.String[] r4 = new java.lang.String[r3]
            java.lang.CharSequence r21 = r7.getUserDisplayName()
            java.lang.String r21 = r21.toString()
            r4[r18] = r21
            java.lang.String r18 = "Them"
            r17 = 1
            r4[r17] = r18
            java.util.List r17 = r7.getMessages()
            int r17 = r17.size()
            int r17 = r17 % 2
            r3 = r4[r17]
            r7.addMessage(r0, r5, r3)
            r17 = r7
            r19 = r8
            r20 = r24
            goto L_0x049b
        L_0x021a:
            r23 = r0
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "--message requires --style messaging"
            r0.<init>(r3)
            throw r0
        L_0x0224:
            r23 = r0
            r24 = r6
            if (r8 == 0) goto L_0x0239
            java.lang.String r0 = r31.getNextArgRequired()
            r8.addLine(r0)
            r17 = r7
            r19 = r8
            r20 = r24
            goto L_0x049b
        L_0x0239:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "--line requires --style inbox"
            r0.<init>(r3)
            throw r0
        L_0x0241:
            r23 = r0
            r24 = r6
            if (r14 == 0) goto L_0x029e
            java.lang.String r0 = r31.getNextArgRequired()
            android.graphics.drawable.Icon r3 = r1.parseIcon(r10, r0)
            if (r3 == 0) goto L_0x0285
            android.graphics.drawable.Drawable r4 = r3.loadDrawable(r9)
            boolean r5 = r4 instanceof android.graphics.drawable.BitmapDrawable
            if (r5 == 0) goto L_0x026b
            r5 = r4
            android.graphics.drawable.BitmapDrawable r5 = (android.graphics.drawable.BitmapDrawable) r5
            android.graphics.Bitmap r5 = r5.getBitmap()
            r14.bigPicture(r5)
            r17 = r7
            r19 = r8
            r20 = r24
            goto L_0x049b
        L_0x026b:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r17 = r3
            java.lang.String r3 = "not a bitmap: "
            r6.append(r3)
            r6.append(r0)
            java.lang.String r3 = r6.toString()
            r5.<init>(r3)
            throw r5
        L_0x0285:
            r17 = r3
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "bad picture spec: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x029e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "--picture requires --style bigpicture"
            r0.<init>(r3)
            throw r0
        L_0x02a6:
            r23 = r0
            r24 = r6
            if (r15 == 0) goto L_0x02bb
            java.lang.String r0 = r31.getNextArgRequired()
            r15.bigText(r0)
            r17 = r7
            r19 = r8
            r20 = r24
            goto L_0x049b
        L_0x02bb:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "--bigtext requires --style bigtext"
            r0.<init>(r3)
            throw r0
        L_0x02c3:
            r23 = r0
            r24 = r6
            java.lang.String r0 = r31.getNextArgRequired()
            java.lang.String r0 = r0.toLowerCase()
            int r3 = r0.hashCode()
            switch(r3) {
                case -1440008444: goto L_0x0301;
                case -114212307: goto L_0x02f7;
                case -44548098: goto L_0x02ed;
                case 100344454: goto L_0x02e2;
                case 103772132: goto L_0x02d7;
                default: goto L_0x02d6;
            }
        L_0x02d6:
            goto L_0x030c
        L_0x02d7:
            java.lang.String r3 = "media"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x02d6
            r3 = 4
            goto L_0x030d
        L_0x02e2:
            java.lang.String r3 = "inbox"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x02d6
            r3 = 2
            goto L_0x030d
        L_0x02ed:
            java.lang.String r3 = "bigpicture"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x02d6
            r3 = 1
            goto L_0x030d
        L_0x02f7:
            java.lang.String r3 = "bigtext"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x02d6
            r3 = 0
            goto L_0x030d
        L_0x0301:
            java.lang.String r3 = "messaging"
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x02d6
            r3 = 3
            goto L_0x030d
        L_0x030c:
            r3 = -1
        L_0x030d:
            if (r3 == 0) goto L_0x037e
            r4 = 1
            if (r3 == r4) goto L_0x0374
            r4 = 2
            if (r3 == r4) goto L_0x036a
            r4 = 3
            if (r3 == r4) goto L_0x033e
            r4 = 4
            if (r3 != r4) goto L_0x0326
            android.app.Notification$MediaStyle r3 = new android.app.Notification$MediaStyle
            r3.<init>()
            r11.setStyle(r3)
            r16 = r3
            goto L_0x0388
        L_0x0326:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "unrecognized notification style: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x033e:
            java.lang.String r3 = "You"
            java.lang.String r4 = r31.peekNextArg()
            java.lang.String r5 = "--user"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0353
            r31.getNextArg()
            java.lang.String r3 = r31.getNextArgRequired()
        L_0x0353:
            android.app.Notification$MessagingStyle r4 = new android.app.Notification$MessagingStyle
            android.app.Person$Builder r5 = new android.app.Person$Builder
            r5.<init>()
            android.app.Person$Builder r5 = r5.setName(r3)
            android.app.Person r5 = r5.build()
            r4.<init>(r5)
            r7 = r4
            r11.setStyle(r7)
            goto L_0x0388
        L_0x036a:
            android.app.Notification$InboxStyle r3 = new android.app.Notification$InboxStyle
            r3.<init>()
            r8 = r3
            r11.setStyle(r8)
            goto L_0x0388
        L_0x0374:
            android.app.Notification$BigPictureStyle r3 = new android.app.Notification$BigPictureStyle
            r3.<init>()
            r14 = r3
            r11.setStyle(r14)
            goto L_0x0388
        L_0x037e:
            android.app.Notification$BigTextStyle r3 = new android.app.Notification$BigTextStyle
            r3.<init>()
            r15 = r3
            r11.setStyle(r15)
        L_0x0388:
            r20 = r24
            goto L_0x04aa
        L_0x038c:
            r23 = r0
            r24 = r6
            r0 = 0
            java.lang.String r3 = r31.peekNextArg()
            int r4 = r3.hashCode()
            r5 = -1655966961(0xffffffff9d4bf30f, float:-2.6992485E-21)
            java.lang.String r6 = "service"
            r19 = r0
            java.lang.String r0 = "broadcast"
            if (r4 == r5) goto L_0x03c0
            r5 = -1618876223(0xffffffff9f81e8c1, float:-5.5018684E-20)
            if (r4 == r5) goto L_0x03b8
            r5 = 1984153269(0x7643c6b5, float:9.927033E32)
            if (r4 == r5) goto L_0x03b0
        L_0x03af:
            goto L_0x03ca
        L_0x03b0:
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x03af
            r3 = 1
            goto L_0x03cb
        L_0x03b8:
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x03af
            r3 = 0
            goto L_0x03cb
        L_0x03c0:
            java.lang.String r4 = "activity"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x03af
            r3 = 2
            goto L_0x03cb
        L_0x03ca:
            r3 = -1
        L_0x03cb:
            if (r3 == 0) goto L_0x03d6
            r4 = 1
            if (r3 == r4) goto L_0x03d6
            r4 = 2
            if (r3 == r4) goto L_0x03d6
            r5 = r19
            goto L_0x03db
        L_0x03d6:
            java.lang.String r3 = r31.getNextArg()
            r5 = r3
        L_0x03db:
            r3 = 0
            android.content.Intent r4 = android.content.Intent.parseCommandArgs(r1, r3)
            android.net.Uri r3 = r4.getData()
            if (r3 != 0) goto L_0x0408
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r17 = r7
            java.lang.String r7 = "xyz:"
            r3.append(r7)
            r19 = r8
            long r7 = java.lang.System.currentTimeMillis()
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            android.net.Uri r3 = android.net.Uri.parse(r3)
            r4.setData(r3)
            goto L_0x040c
        L_0x0408:
            r17 = r7
            r19 = r8
        L_0x040c:
            boolean r0 = r0.equals(r5)
            r3 = 134217728(0x8000000, float:3.85186E-34)
            if (r0 == 0) goto L_0x0422
            android.os.UserHandle r0 = android.os.UserHandle.CURRENT
            r7 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcastAsUser(r9, r7, r4, r3, r0)
            r3 = r0
            r18 = r4
            r0 = r5
            r20 = r24
            goto L_0x0447
        L_0x0422:
            r7 = 0
            boolean r0 = r6.equals(r5)
            if (r0 == 0) goto L_0x0434
            android.app.PendingIntent r0 = android.app.PendingIntent.getService(r9, r7, r4, r3)
            r3 = r0
            r18 = r4
            r0 = r5
            r20 = r24
            goto L_0x0447
        L_0x0434:
            r0 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 0
            android.os.UserHandle r8 = android.os.UserHandle.CURRENT
            r3 = r9
            r18 = r4
            r4 = r0
            r0 = r5
            r5 = r18
            r20 = r24
            android.app.PendingIntent r3 = android.app.PendingIntent.getActivityAsUser(r3, r4, r5, r6, r7, r8)
        L_0x0447:
            r11.setContentIntent(r3)
            goto L_0x049b
        L_0x044b:
            r23 = r0
            r20 = r6
            r17 = r7
            r19 = r8
            goto L_0x045d
        L_0x0454:
            r23 = r0
            r20 = r6
            r17 = r7
            r19 = r8
            r0 = 1
        L_0x045d:
            java.lang.String r3 = r31.getNextArgRequired()
            android.graphics.drawable.Icon r4 = r1.parseIcon(r10, r3)
            if (r4 != 0) goto L_0x047d
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "error: invalid icon: "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            r2.println(r5)
            r5 = -1
            return r5
        L_0x047d:
            if (r0 == 0) goto L_0x0484
            r11.setLargeIcon(r4)
            r0 = 0
            goto L_0x049b
        L_0x0484:
            r5 = r4
            r12 = r5
            r7 = r17
            r8 = r19
            goto L_0x04aa
        L_0x048b:
            r23 = r0
            r20 = r6
            r17 = r7
            r19 = r8
            java.lang.String r0 = r31.getNextArgRequired()
            r11.setContentTitle(r0)
        L_0x049b:
            r7 = r17
            r8 = r19
            goto L_0x04aa
        L_0x04a0:
            r23 = r0
            r20 = r6
            r17 = r7
            r19 = r8
            r0 = 1
            r13 = r0
        L_0x04aa:
            goto L_0x0025
        L_0x04ac:
            r20 = r6
            r17 = r7
            r19 = r8
            java.lang.String r4 = r31.getNextArg()
            java.lang.String r5 = r31.getNextArg()
            if (r4 == 0) goto L_0x057d
            if (r5 != 0) goto L_0x04c2
            r22 = r5
            goto L_0x057f
        L_0x04c2:
            r11.setContentText(r5)
            if (r12 != 0) goto L_0x04ce
            r0 = 17301623(0x1080077, float:2.4979588E-38)
            r11.setSmallIcon(r0)
            goto L_0x04d1
        L_0x04ce:
            r11.setSmallIcon(r12)
        L_0x04d1:
            r31.ensureChannel()
            android.app.Notification r3 = r11.build()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "posting:\n  "
            r0.append(r6)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            r2.println(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "posting: "
            r0.append(r6)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "NotificationManager"
            android.util.Slog.v(r6, r0)
            int r6 = android.os.UserHandle.getCallingUserId()
            long r7 = android.os.Binder.clearCallingIdentity()
            android.app.INotificationManager r0 = r1.mBinderService     // Catch:{ all -> 0x0574 }
            java.lang.String r25 = "com.android.shell"
            java.lang.String r26 = "android"
            r28 = 1138(0x472, float:1.595E-42)
            r24 = r0
            r27 = r4
            r29 = r3
            r30 = r6
            r24.enqueueNotificationWithTag(r25, r26, r27, r28, r29, r30)     // Catch:{ all -> 0x0574 }
            android.os.Binder.restoreCallingIdentity(r7)
            if (r13 == 0) goto L_0x056e
            com.android.server.notification.NotificationManagerService r0 = r1.mDirectService
            r18 = r3
            r3 = 1138(0x472, float:1.595E-42)
            r22 = r5
            java.lang.String r5 = "com.android.shell"
            com.android.server.notification.NotificationRecord r0 = r0.findNotificationLocked(r5, r4, r3, r6)
            r23 = 3
            r3 = r0
        L_0x0536:
            int r25 = r23 + -1
            if (r23 <= 0) goto L_0x0557
            if (r3 == 0) goto L_0x053d
            goto L_0x0557
        L_0x053d:
            java.lang.String r0 = "waiting for notification to post..."
            r2.println(r0)     // Catch:{ InterruptedException -> 0x0549 }
            r26 = 500(0x1f4, double:2.47E-321)
            java.lang.Thread.sleep(r26)     // Catch:{ InterruptedException -> 0x0549 }
            goto L_0x054a
        L_0x0549:
            r0 = move-exception
        L_0x054a:
            com.android.server.notification.NotificationManagerService r0 = r1.mDirectService
            r1 = 1138(0x472, float:1.595E-42)
            com.android.server.notification.NotificationRecord r3 = r0.findNotificationLocked(r5, r4, r1, r6)
            r1 = r31
            r23 = r25
            goto L_0x0536
        L_0x0557:
            if (r3 != 0) goto L_0x0561
            java.lang.String r0 = "warning: couldn't find notification after enqueueing"
            r2.println(r0)
            r1 = 0
            goto L_0x0573
        L_0x0561:
            java.lang.String r0 = "posted: "
            r2.println(r0)
            java.lang.String r0 = "  "
            r1 = 0
            r3.dump((java.io.PrintWriter) r2, (java.lang.String) r0, (android.content.Context) r9, (boolean) r1)
            goto L_0x0573
        L_0x056e:
            r18 = r3
            r22 = r5
            r1 = 0
        L_0x0573:
            return r1
        L_0x0574:
            r0 = move-exception
            r18 = r3
            r22 = r5
            android.os.Binder.restoreCallingIdentity(r7)
            throw r0
        L_0x057d:
            r22 = r5
        L_0x057f:
            r2.println(r3)
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationShellCmd.doNotify(java.io.PrintWriter):int");
    }

    public void onHelp() {
        getOutPrintWriter().println(USAGE);
    }
}
