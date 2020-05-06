package com.android.server.statusbar;

import android.content.ComponentName;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

public class StatusBarShellCommand extends ShellCommand {
    private static final IBinder sToken = new StatusBarShellCommandToken();
    private final Context mContext;
    private final StatusBarManagerService mInterface;

    public StatusBarShellCommand(StatusBarManagerService service, Context context) {
        this.mInterface = service;
        this.mContext = context;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r6) {
        /*
            r5 = this;
            if (r6 != 0) goto L_0x0007
            int r0 = r5.handleDefaultCommands(r6)
            return r0
        L_0x0007:
            r0 = -1
            int r1 = r6.hashCode()     // Catch:{ RemoteException -> 0x00c1 }
            r2 = 0
            switch(r1) {
                case -1282000806: goto L_0x0070;
                case -1239176554: goto L_0x0066;
                case -1052548778: goto L_0x005a;
                case -823073837: goto L_0x0050;
                case -632085587: goto L_0x0046;
                case -339726761: goto L_0x003b;
                case 901899220: goto L_0x0030;
                case 1612300298: goto L_0x0026;
                case 1629310709: goto L_0x001c;
                case 1672031734: goto L_0x0012;
                default: goto L_0x0010;
            }     // Catch:{ RemoteException -> 0x00c1 }
        L_0x0010:
            goto L_0x007a
        L_0x0012:
            java.lang.String r1 = "expand-settings"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 1
            goto L_0x007b
        L_0x001c:
            java.lang.String r1 = "expand-notifications"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = r2
            goto L_0x007b
        L_0x0026:
            java.lang.String r1 = "check-support"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 6
            goto L_0x007b
        L_0x0030:
            java.lang.String r1 = "disable-for-setup"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 8
            goto L_0x007b
        L_0x003b:
            java.lang.String r1 = "remove-tile"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 4
            goto L_0x007b
        L_0x0046:
            java.lang.String r1 = "collapse"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 2
            goto L_0x007b
        L_0x0050:
            java.lang.String r1 = "click-tile"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 5
            goto L_0x007b
        L_0x005a:
            java.lang.String r1 = "send-disable-flag"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 9
            goto L_0x007b
        L_0x0066:
            java.lang.String r1 = "get-status-icons"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 7
            goto L_0x007b
        L_0x0070:
            java.lang.String r1 = "add-tile"
            boolean r1 = r6.equals(r1)     // Catch:{ RemoteException -> 0x00c1 }
            if (r1 == 0) goto L_0x0010
            r1 = 3
            goto L_0x007b
        L_0x007a:
            r1 = r0
        L_0x007b:
            switch(r1) {
                case 0: goto L_0x00bb;
                case 1: goto L_0x00b6;
                case 2: goto L_0x00b1;
                case 3: goto L_0x00ac;
                case 4: goto L_0x00a7;
                case 5: goto L_0x00a2;
                case 6: goto L_0x0092;
                case 7: goto L_0x008d;
                case 8: goto L_0x0088;
                case 9: goto L_0x0083;
                default: goto L_0x007e;
            }     // Catch:{ RemoteException -> 0x00c1 }
        L_0x007e:
            int r0 = r5.handleDefaultCommands(r6)     // Catch:{ RemoteException -> 0x00c1 }
            goto L_0x00c0
        L_0x0083:
            int r0 = r5.runSendDisableFlag()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x0088:
            int r0 = r5.runDisableForSetup()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x008d:
            int r0 = r5.runGetStatusIcons()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x0092:
            java.io.PrintWriter r1 = r5.getOutPrintWriter()     // Catch:{ RemoteException -> 0x00c1 }
            boolean r3 = android.service.quicksettings.TileService.isQuickSettingsSupported()     // Catch:{ RemoteException -> 0x00c1 }
            java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch:{ RemoteException -> 0x00c1 }
            r1.println(r3)     // Catch:{ RemoteException -> 0x00c1 }
            return r2
        L_0x00a2:
            int r0 = r5.runClickTile()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00a7:
            int r0 = r5.runRemoveTile()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00ac:
            int r0 = r5.runAddTile()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00b1:
            int r0 = r5.runCollapse()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00b6:
            int r0 = r5.runExpandSettings()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00bb:
            int r0 = r5.runExpandNotifications()     // Catch:{ RemoteException -> 0x00c1 }
            return r0
        L_0x00c0:
            return r0
        L_0x00c1:
            r1 = move-exception
            java.io.PrintWriter r2 = r5.getOutPrintWriter()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Remote exception: "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            r2.println(r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.statusbar.StatusBarShellCommand.onCommand(java.lang.String):int");
    }

    private int runAddTile() throws RemoteException {
        this.mInterface.addTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    private int runRemoveTile() throws RemoteException {
        this.mInterface.remTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    private int runClickTile() throws RemoteException {
        this.mInterface.clickTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    private int runCollapse() throws RemoteException {
        this.mInterface.collapsePanels();
        return 0;
    }

    private int runExpandSettings() throws RemoteException {
        this.mInterface.expandSettingsPanel((String) null);
        return 0;
    }

    private int runExpandNotifications() throws RemoteException {
        this.mInterface.expandNotificationsPanel();
        return 0;
    }

    private int runGetStatusIcons() {
        PrintWriter pw = getOutPrintWriter();
        for (String icon : this.mInterface.getStatusBarIcons()) {
            pw.println(icon);
        }
        return 0;
    }

    private int runDisableForSetup() {
        String arg = getNextArgRequired();
        String pkg = this.mContext.getPackageName();
        if (Boolean.parseBoolean(arg)) {
            this.mInterface.disable(61145088, sToken, pkg);
            this.mInterface.disable2(16, sToken, pkg);
        } else {
            this.mInterface.disable(0, sToken, pkg);
            this.mInterface.disable2(0, sToken, pkg);
        }
        return 0;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0054, code lost:
        if (r4.equals("search") != false) goto L_0x0058;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runSendDisableFlag() {
        /*
            r12 = this;
            android.content.Context r0 = r12.mContext
            java.lang.String r0 = r0.getPackageName()
            r1 = 0
            r2 = 0
            android.app.StatusBarManager$DisableInfo r3 = new android.app.StatusBarManager$DisableInfo
            r3.<init>()
            java.lang.String r4 = r12.getNextArg()
        L_0x0011:
            r5 = 0
            if (r4 == 0) goto L_0x007c
            r6 = -1
            int r7 = r4.hashCode()
            r8 = 4
            r9 = 3
            r10 = 2
            r11 = 1
            switch(r7) {
                case -906336856: goto L_0x004d;
                case -755976775: goto L_0x0042;
                case 3208415: goto L_0x0037;
                case 1011652819: goto L_0x002c;
                case 1082295672: goto L_0x0021;
                default: goto L_0x0020;
            }
        L_0x0020:
            goto L_0x0057
        L_0x0021:
            java.lang.String r5 = "recents"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x0020
            r5 = r10
            goto L_0x0058
        L_0x002c:
            java.lang.String r5 = "statusbar-expansion"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x0020
            r5 = r8
            goto L_0x0058
        L_0x0037:
            java.lang.String r5 = "home"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x0020
            r5 = r11
            goto L_0x0058
        L_0x0042:
            java.lang.String r5 = "notification-alerts"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x0020
            r5 = r9
            goto L_0x0058
        L_0x004d:
            java.lang.String r7 = "search"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto L_0x0020
            goto L_0x0058
        L_0x0057:
            r5 = r6
        L_0x0058:
            if (r5 == 0) goto L_0x0073
            if (r5 == r11) goto L_0x006f
            if (r5 == r10) goto L_0x006b
            if (r5 == r9) goto L_0x0067
            if (r5 == r8) goto L_0x0063
            goto L_0x0077
        L_0x0063:
            r3.setStatusBarExpansionDisabled(r11)
            goto L_0x0077
        L_0x0067:
            r3.setNotificationPeekingDisabled(r11)
            goto L_0x0077
        L_0x006b:
            r3.setRecentsDisabled(r11)
            goto L_0x0077
        L_0x006f:
            r3.setNagivationHomeDisabled(r11)
            goto L_0x0077
        L_0x0073:
            r3.setSearchDisabled(r11)
        L_0x0077:
            java.lang.String r4 = r12.getNextArg()
            goto L_0x0011
        L_0x007c:
            android.util.Pair r6 = r3.toFlags()
            com.android.server.statusbar.StatusBarManagerService r7 = r12.mInterface
            java.lang.Object r8 = r6.first
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            android.os.IBinder r9 = sToken
            r7.disable(r8, r9, r0)
            com.android.server.statusbar.StatusBarManagerService r7 = r12.mInterface
            java.lang.Object r8 = r6.second
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            android.os.IBinder r9 = sToken
            r7.disable2(r8, r9, r0)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.statusbar.StatusBarShellCommand.runSendDisableFlag():int");
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Status bar commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        pw.println("  expand-notifications");
        pw.println("    Open the notifications panel.");
        pw.println("");
        pw.println("  expand-settings");
        pw.println("    Open the notifications panel and expand quick settings if present.");
        pw.println("");
        pw.println("  collapse");
        pw.println("    Collapse the notifications and settings panel.");
        pw.println("");
        pw.println("  add-tile COMPONENT");
        pw.println("    Add a TileService of the specified component");
        pw.println("");
        pw.println("  remove-tile COMPONENT");
        pw.println("    Remove a TileService of the specified component");
        pw.println("");
        pw.println("  click-tile COMPONENT");
        pw.println("    Click on a TileService of the specified component");
        pw.println("");
        pw.println("  check-support");
        pw.println("    Check if this device supports QS + APIs");
        pw.println("");
        pw.println("  get-status-icons");
        pw.println("    Print the list of status bar icons and the order they appear in");
        pw.println("");
        pw.println("  disable-for-setup DISABLE");
        pw.println("    If true, disable status bar components unsuitable for device setup");
        pw.println("");
        pw.println("  send-disable-flag FLAG...");
        pw.println("    Send zero or more disable flags (parsed individually) to StatusBarManager");
        pw.println("    Valid options:");
        pw.println("        <blank>             - equivalent to \"none\"");
        pw.println("        none                - re-enables all components");
        pw.println("        search              - disable search");
        pw.println("        home                - disable naviagation home");
        pw.println("        recents             - disable recents/overview");
        pw.println("        notification-peek   - disable notification peeking");
        pw.println("        statusbar-expansion - disable status bar expansion");
        pw.println("");
    }

    private static final class StatusBarShellCommandToken extends Binder {
        private StatusBarShellCommandToken() {
        }
    }
}
