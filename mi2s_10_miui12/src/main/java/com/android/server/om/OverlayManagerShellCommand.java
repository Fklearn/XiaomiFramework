package com.android.server.om;

import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

final class OverlayManagerShellCommand extends ShellCommand {
    private final IOverlayManager mInterface;

    OverlayManagerShellCommand(IOverlayManager iom) {
        this.mInterface = iom;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r9) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0007
            int r0 = r8.handleDefaultCommands(r9)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r8.getErrPrintWriter()
            r1 = -1
            int r2 = r9.hashCode()     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            r3 = 0
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 1
            switch(r2) {
                case -1361113425: goto L_0x0042;
                case -1298848381: goto L_0x0038;
                case -794624300: goto L_0x002e;
                case 3322014: goto L_0x0023;
                case 1671308008: goto L_0x0019;
                default: goto L_0x0018;
            }     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
        L_0x0018:
            goto L_0x004d
        L_0x0019:
            java.lang.String r2 = "disable"
            boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0018
            r2 = r6
            goto L_0x004e
        L_0x0023:
            java.lang.String r2 = "list"
            boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0018
            r2 = r3
            goto L_0x004e
        L_0x002e:
            java.lang.String r2 = "enable-exclusive"
            boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0018
            r2 = r5
            goto L_0x004e
        L_0x0038:
            java.lang.String r2 = "enable"
            boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0018
            r2 = r7
            goto L_0x004e
        L_0x0042:
            java.lang.String r2 = "set-priority"
            boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0018
            r2 = r4
            goto L_0x004e
        L_0x004d:
            r2 = r1
        L_0x004e:
            if (r2 == 0) goto L_0x0071
            if (r2 == r7) goto L_0x006c
            if (r2 == r6) goto L_0x0067
            if (r2 == r5) goto L_0x0062
            if (r2 == r4) goto L_0x005d
            int r1 = r8.handleDefaultCommands(r9)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x005d:
            int r1 = r8.runSetPriority()     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x0062:
            int r1 = r8.runEnableExclusive()     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x0067:
            int r1 = r8.runEnableDisable(r3)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x006c:
            int r1 = r8.runEnableDisable(r7)     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x0071:
            int r1 = r8.runList()     // Catch:{ IllegalArgumentException -> 0x008c, RemoteException -> 0x0076 }
            return r1
        L_0x0076:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Remote exception: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            goto L_0x00a6
        L_0x008c:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: "
            r3.append(r4)
            java.lang.String r4 = r2.getMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
        L_0x00a6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerShellCommand.onCommand(java.lang.String):int");
    }

    public void onHelp() {
        PrintWriter out = getOutPrintWriter();
        out.println("Overlay manager (overlay) commands:");
        out.println("  help");
        out.println("    Print this help text.");
        out.println("  dump [--verbose] [--user USER_ID] [[FIELD] PACKAGE]");
        out.println("    Print debugging information about the overlay manager.");
        out.println("    With optional parameter PACKAGE, limit output to the specified");
        out.println("    package. With optional parameter FIELD, limit output to");
        out.println("    the value of that SettingsItem field. Field names are");
        out.println("    case insensitive and out.println the m prefix can be omitted,");
        out.println("    so the following are equivalent: mState, mstate, State, state.");
        out.println("  list [--user USER_ID] [PACKAGE]");
        out.println("    Print information about target and overlay packages.");
        out.println("    Overlay packages are printed in priority order. With optional");
        out.println("    parameter PACKAGE, limit output to the specified package.");
        out.println("  enable [--user USER_ID] PACKAGE");
        out.println("    Enable overlay package PACKAGE.");
        out.println("  disable [--user USER_ID] PACKAGE");
        out.println("    Disable overlay package PACKAGE.");
        out.println("  enable-exclusive [--user USER_ID] [--category] PACKAGE");
        out.println("    Enable overlay package PACKAGE and disable all other overlays for");
        out.println("    its target package. If the --category option is given, only disables");
        out.println("    other overlays in the same category.");
        out.println("  set-priority [--user USER_ID] PACKAGE PARENT|lowest|highest");
        out.println("    Change the priority of the overlay PACKAGE to be just higher than");
        out.println("    the priority of PACKAGE_PARENT If PARENT is the special keyword");
        out.println("    'lowest', change priority of PACKAGE to the lowest priority.");
        out.println("    If PARENT is the special keyword 'highest', change priority of");
        out.println("    PACKAGE to the highest priority.");
    }

    private int runList() throws RemoteException {
        PrintWriter out = getOutPrintWriter();
        PrintWriter err = getErrPrintWriter();
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                if (opt.hashCode() == 1333469547 && opt.equals("--user")) {
                    c = 0;
                }
                if (c != 0) {
                    err.println("Error: Unknown option: " + opt);
                    return 1;
                }
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                String packageName = getNextArg();
                if (packageName != null) {
                    List<OverlayInfo> overlaysForTarget = this.mInterface.getOverlayInfosForTarget(packageName, userId);
                    if (overlaysForTarget.isEmpty()) {
                        OverlayInfo info = this.mInterface.getOverlayInfo(packageName, userId);
                        if (info != null) {
                            printListOverlay(out, info);
                        }
                        return 0;
                    }
                    out.println(packageName);
                    int n = overlaysForTarget.size();
                    for (int i = 0; i < n; i++) {
                        printListOverlay(out, overlaysForTarget.get(i));
                    }
                    return 0;
                }
                Map<String, List<OverlayInfo>> allOverlays = this.mInterface.getAllOverlays(userId);
                for (String targetPackageName : allOverlays.keySet()) {
                    out.println(targetPackageName);
                    List<OverlayInfo> overlaysForTarget2 = allOverlays.get(targetPackageName);
                    int n2 = overlaysForTarget2.size();
                    for (int i2 = 0; i2 < n2; i2++) {
                        printListOverlay(out, overlaysForTarget2.get(i2));
                    }
                    out.println();
                }
                return 0;
            }
        }
    }

    private void printListOverlay(PrintWriter out, OverlayInfo oi) {
        String status;
        int i = oi.state;
        if (i == 2) {
            status = "[ ]";
        } else if (i == 3 || i == 6) {
            status = "[x]";
        } else {
            status = "---";
        }
        out.println(String.format("%s %s", new Object[]{status, oi.packageName}));
    }

    private int runEnableDisable(boolean enable) throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                if (opt.hashCode() == 1333469547 && opt.equals("--user")) {
                    c = 0;
                }
                if (c != 0) {
                    err.println("Error: Unknown option: " + opt);
                    return 1;
                }
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                return true ^ this.mInterface.setEnabled(getNextArgRequired(), enable, userId) ? 1 : 0;
            }
        }
    }

    private int runEnableExclusive() throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        int userId = 0;
        boolean inCategory = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                int hashCode = opt.hashCode();
                if (hashCode != 66265758) {
                    if (hashCode == 1333469547 && opt.equals("--user")) {
                        c = 0;
                    }
                } else if (opt.equals("--category")) {
                    c = 1;
                }
                if (c == 0) {
                    userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (c != 1) {
                    err.println("Error: Unknown option: " + opt);
                    return 1;
                } else {
                    inCategory = true;
                }
            } else {
                String overlay = getNextArgRequired();
                return inCategory ? true ^ this.mInterface.setEnabledExclusiveInCategory(overlay, userId) ? 1 : 0 : true ^ this.mInterface.setEnabledExclusive(overlay, true, userId) ? 1 : 0;
            }
        }
    }

    private int runSetPriority() throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        int userId = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                char c = 65535;
                if (opt.hashCode() == 1333469547 && opt.equals("--user")) {
                    c = 0;
                }
                if (c != 0) {
                    err.println("Error: Unknown option: " + opt);
                    return 1;
                }
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                String packageName = getNextArgRequired();
                String newParentPackageName = getNextArgRequired();
                return "highest".equals(newParentPackageName) ? true ^ this.mInterface.setHighestPriority(packageName, userId) ? 1 : 0 : "lowest".equals(newParentPackageName) ? true ^ this.mInterface.setLowestPriority(packageName, userId) ? 1 : 0 : true ^ this.mInterface.setPriority(packageName, newParentPackageName, userId) ? 1 : 0;
            }
        }
    }
}
