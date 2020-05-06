package com.android.server.wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.view.IWindowManager;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.server.UiModeManagerService;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowManagerShellCommand extends ShellCommand {
    private final IWindowManager mInterface;
    private final WindowManagerService mInternal;

    public WindowManagerShellCommand(WindowManagerService service) {
        this.mInterface = service;
        this.mInternal = service;
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
            java.io.PrintWriter r0 = r5.getOutPrintWriter()
            r1 = -1
            int r2 = r6.hashCode()     // Catch:{ RemoteException -> 0x00aa }
            switch(r2) {
                case -1999459663: goto L_0x0064;
                case -1316842219: goto L_0x005a;
                case -1067396926: goto L_0x0050;
                case -336752166: goto L_0x0046;
                case -229462135: goto L_0x003c;
                case 3530753: goto L_0x0032;
                case 530020689: goto L_0x0028;
                case 1552717032: goto L_0x001e;
                case 1910897543: goto L_0x0014;
                default: goto L_0x0013;
            }     // Catch:{ RemoteException -> 0x00aa }
        L_0x0013:
            goto L_0x006f
        L_0x0014:
            java.lang.String r2 = "scaling"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 4
            goto L_0x0070
        L_0x001e:
            java.lang.String r2 = "density"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 1
            goto L_0x0070
        L_0x0028:
            java.lang.String r2 = "overscan"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 3
            goto L_0x0070
        L_0x0032:
            java.lang.String r2 = "size"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 0
            goto L_0x0070
        L_0x003c:
            java.lang.String r2 = "dismiss-keyguard"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 5
            goto L_0x0070
        L_0x0046:
            java.lang.String r2 = "folded-area"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 2
            goto L_0x0070
        L_0x0050:
            java.lang.String r2 = "tracing"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 6
            goto L_0x0070
        L_0x005a:
            java.lang.String r2 = "set-user-rotation"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 7
            goto L_0x0070
        L_0x0064:
            java.lang.String r2 = "set-fix-to-user-rotation"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x00aa }
            if (r2 == 0) goto L_0x0013
            r2 = 8
            goto L_0x0070
        L_0x006f:
            r2 = r1
        L_0x0070:
            switch(r2) {
                case 0: goto L_0x00a4;
                case 1: goto L_0x009f;
                case 2: goto L_0x009a;
                case 3: goto L_0x0095;
                case 4: goto L_0x0090;
                case 5: goto L_0x008b;
                case 6: goto L_0x0082;
                case 7: goto L_0x007d;
                case 8: goto L_0x0078;
                default: goto L_0x0073;
            }     // Catch:{ RemoteException -> 0x00aa }
        L_0x0073:
            int r1 = r5.handleDefaultCommands(r6)     // Catch:{ RemoteException -> 0x00aa }
            goto L_0x00a9
        L_0x0078:
            int r1 = r5.runSetFixToUserRotation(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x007d:
            int r1 = r5.runSetDisplayUserRotation(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x0082:
            com.android.server.wm.WindowManagerService r2 = r5.mInternal     // Catch:{ RemoteException -> 0x00aa }
            com.android.server.wm.WindowTracing r2 = r2.mWindowTracing     // Catch:{ RemoteException -> 0x00aa }
            int r1 = r2.onShellCommand(r5)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x008b:
            int r1 = r5.runDismissKeyguard(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x0090:
            int r1 = r5.runDisplayScaling(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x0095:
            int r1 = r5.runDisplayOverscan(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x009a:
            int r1 = r5.runDisplayFoldedArea(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x009f:
            int r1 = r5.runDisplayDensity(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x00a4:
            int r1 = r5.runDisplaySize(r0)     // Catch:{ RemoteException -> 0x00aa }
            return r1
        L_0x00a9:
            return r1
        L_0x00aa:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Remote exception: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerShellCommand.onCommand(java.lang.String):int");
    }

    private int getDisplayId(String opt) {
        String option = "-d".equals(opt) ? opt : getNextOption();
        if (option == null || !"-d".equals(option)) {
            return 0;
        }
        try {
            return Integer.parseInt(getNextArgRequired());
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad number " + e);
            return 0;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: " + e2);
            return 0;
        }
    }

    private void printInitialDisplaySize(PrintWriter pw, int displayId) {
        Point initialSize = new Point();
        Point baseSize = new Point();
        try {
            this.mInterface.getInitialDisplaySize(displayId, initialSize);
            this.mInterface.getBaseDisplaySize(displayId, baseSize);
            pw.println("Physical size: " + initialSize.x + "x" + initialSize.y);
            if (!initialSize.equals(baseSize)) {
                pw.println("Override size: " + baseSize.x + "x" + baseSize.y);
            }
        } catch (RemoteException e) {
            pw.println("Remote exception: " + e);
        }
    }

    private int runDisplaySize(PrintWriter pw) throws RemoteException {
        int div;
        String size = getNextArg();
        int displayId = getDisplayId(size);
        if (size == null) {
            printInitialDisplaySize(pw, displayId);
            return 0;
        } else if ("-d".equals(size)) {
            printInitialDisplaySize(pw, displayId);
            return 0;
        } else {
            int h = -1;
            if ("reset".equals(size)) {
                div = -1;
            } else {
                int div2 = size.indexOf(120);
                if (div2 <= 0 || div2 >= size.length() - 1) {
                    getErrPrintWriter().println("Error: bad size " + size);
                    return -1;
                }
                String wstr = size.substring(0, div2);
                String hstr = size.substring(div2 + 1);
                try {
                    int w = parseDimension(wstr, displayId);
                    div = parseDimension(hstr, displayId);
                    h = w;
                } catch (NumberFormatException e) {
                    getErrPrintWriter().println("Error: bad number " + e);
                    return -1;
                }
            }
            if (h < 0 || div < 0) {
                this.mInterface.clearForcedDisplaySize(displayId);
            } else {
                this.mInterface.setForcedDisplaySize(displayId, h, div);
            }
            return 0;
        }
    }

    private void printInitialDisplayDensity(PrintWriter pw, int displayId) {
        try {
            int initialDensity = this.mInterface.getInitialDisplayDensity(displayId);
            int baseDensity = this.mInterface.getBaseDisplayDensity(displayId);
            pw.println("Physical density: " + initialDensity);
            if (initialDensity != baseDensity) {
                pw.println("Override density: " + baseDensity);
            }
        } catch (RemoteException e) {
            pw.println("Remote exception: " + e);
        }
    }

    private int runDisplayDensity(PrintWriter pw) throws RemoteException {
        int density;
        String densityStr = getNextArg();
        int displayId = getDisplayId(densityStr);
        if (densityStr == null) {
            printInitialDisplayDensity(pw, displayId);
            return 0;
        } else if ("-d".equals(densityStr)) {
            printInitialDisplayDensity(pw, displayId);
            return 0;
        } else {
            if ("reset".equals(densityStr)) {
                density = -1;
            } else {
                try {
                    int density2 = Integer.parseInt(densityStr);
                    if (density2 < 72) {
                        getErrPrintWriter().println("Error: density must be >= 72");
                        return -1;
                    }
                    density = density2;
                } catch (NumberFormatException e) {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Error: bad number " + e);
                    return -1;
                }
            }
            if (density > 0) {
                this.mInterface.setForcedDisplayDensityForUser(displayId, density, -2);
            } else {
                this.mInterface.clearForcedDisplayDensityForUser(displayId, -2);
            }
            return 0;
        }
    }

    private void printFoldedArea(PrintWriter pw) {
        Rect foldedArea = this.mInternal.getFoldedArea();
        if (foldedArea.isEmpty()) {
            pw.println("Folded area: none");
            return;
        }
        pw.println("Folded area: " + foldedArea.left + "," + foldedArea.top + "," + foldedArea.right + "," + foldedArea.bottom);
    }

    private int runDisplayFoldedArea(PrintWriter pw) {
        String areaStr = getNextArg();
        Rect rect = new Rect();
        if (areaStr == null) {
            printFoldedArea(pw);
            return 0;
        }
        if ("reset".equals(areaStr)) {
            rect.setEmpty();
        } else {
            Matcher matcher = Pattern.compile("(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)").matcher(areaStr);
            if (!matcher.matches()) {
                getErrPrintWriter().println("Error: area should be LEFT,TOP,RIGHT,BOTTOM");
                return -1;
            }
            rect.set(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        }
        this.mInternal.setOverrideFoldedArea(rect);
        return 0;
    }

    private int runDisplayOverscan(PrintWriter pw) throws RemoteException {
        String overscanStr = getNextArgRequired();
        Rect rect = new Rect();
        int displayId = getDisplayId(overscanStr);
        if ("reset".equals(overscanStr)) {
            rect.set(0, 0, 0, 0);
        } else {
            Matcher matcher = Pattern.compile("(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)").matcher(overscanStr);
            if (!matcher.matches()) {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: bad rectangle arg: " + overscanStr);
                return -1;
            }
            rect.left = Integer.parseInt(matcher.group(1));
            rect.top = Integer.parseInt(matcher.group(2));
            rect.right = Integer.parseInt(matcher.group(3));
            rect.bottom = Integer.parseInt(matcher.group(4));
        }
        this.mInterface.setOverscan(displayId, rect.left, rect.top, rect.right, rect.bottom);
        return 0;
    }

    private int runDisplayScaling(PrintWriter pw) throws RemoteException {
        String scalingStr = getNextArgRequired();
        if (UiModeManagerService.Shell.NIGHT_MODE_STR_AUTO.equals(scalingStr)) {
            this.mInterface.setForcedDisplayScalingMode(getDisplayId(scalingStr), 0);
        } else if ("off".equals(scalingStr)) {
            this.mInterface.setForcedDisplayScalingMode(getDisplayId(scalingStr), 1);
        } else {
            getErrPrintWriter().println("Error: scaling must be 'auto' or 'off'");
            return -1;
        }
        return 0;
    }

    private int runDismissKeyguard(PrintWriter pw) throws RemoteException {
        this.mInterface.dismissKeyguard((IKeyguardDismissCallback) null, (CharSequence) null);
        return 0;
    }

    private int parseDimension(String s, int displayId) throws NumberFormatException {
        int density;
        if (s.endsWith("px")) {
            return Integer.parseInt(s.substring(0, s.length() - 2));
        }
        if (!s.endsWith("dp")) {
            return Integer.parseInt(s);
        }
        try {
            density = this.mInterface.getBaseDisplayDensity(displayId);
        } catch (RemoteException e) {
            density = 160;
        }
        return (Integer.parseInt(s.substring(0, s.length() - 2)) * density) / 160;
    }

    private int runSetDisplayUserRotation(PrintWriter pw) {
        int rotation;
        String lockMode = getNextArgRequired();
        int displayId = 0;
        String arg = getNextArg();
        if ("-d".equals(arg)) {
            displayId = Integer.parseInt(getNextArgRequired());
            arg = getNextArg();
        }
        if ("free".equals(lockMode)) {
            this.mInternal.thawDisplayRotation(displayId);
            return 0;
        } else if (!lockMode.equals("lock")) {
            getErrPrintWriter().println("Error: lock mode needs to be either free or lock.");
            return -1;
        } else {
            if (arg != null) {
                try {
                    rotation = Integer.parseInt(arg);
                } catch (IllegalArgumentException e) {
                    getErrPrintWriter().println("Error: " + e.getMessage());
                    return -1;
                }
            } else {
                rotation = 0;
            }
            this.mInternal.freezeDisplayRotation(displayId, rotation);
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0073  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runSetFixToUserRotation(java.io.PrintWriter r9) {
        /*
            r8 = this;
            r0 = 0
            java.lang.String r1 = r8.getNextArgRequired()
            java.lang.String r2 = "-d"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0019
            java.lang.String r2 = r8.getNextArgRequired()
            int r0 = java.lang.Integer.parseInt(r2)
            java.lang.String r1 = r8.getNextArgRequired()
        L_0x0019:
            int r2 = r1.hashCode()
            r3 = -1609594047(0xffffffffa00f8b41, float:-1.2158646E-19)
            r4 = 0
            r5 = 2
            r6 = 1
            r7 = -1
            if (r2 == r3) goto L_0x0045
            r3 = 270940796(0x10263a7c, float:3.2782782E-29)
            if (r2 == r3) goto L_0x003b
            r3 = 1544803905(0x5c13d641, float:1.66449585E17)
            if (r2 == r3) goto L_0x0031
        L_0x0030:
            goto L_0x004f
        L_0x0031:
            java.lang.String r2 = "default"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0030
            r2 = r5
            goto L_0x0050
        L_0x003b:
            java.lang.String r2 = "disabled"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0030
            r2 = r6
            goto L_0x0050
        L_0x0045:
            java.lang.String r2 = "enabled"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0030
            r2 = r4
            goto L_0x0050
        L_0x004f:
            r2 = r7
        L_0x0050:
            if (r2 == 0) goto L_0x0073
            if (r2 == r6) goto L_0x0071
            if (r2 == r5) goto L_0x006f
            java.io.PrintWriter r2 = r8.getErrPrintWriter()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: expecting enabled, disabled or default, but we get "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            r2.println(r3)
            return r7
        L_0x006f:
            r2 = 1
            goto L_0x0075
        L_0x0071:
            r2 = 1
            goto L_0x0075
        L_0x0073:
            r2 = 2
        L_0x0075:
            com.android.server.wm.WindowManagerService r3 = r8.mInternal
            r3.setRotateForApp(r0, r2)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerShellCommand.runSetFixToUserRotation(java.io.PrintWriter):int");
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Window manager (window) commands:");
        pw.println("  help");
        pw.println("      Print this help text.");
        pw.println("  size [reset|WxH|WdpxHdp] [-d DISPLAY_ID]");
        pw.println("    Return or override display size.");
        pw.println("    width and height in pixels unless suffixed with 'dp'.");
        pw.println("  density [reset|DENSITY] [-d DISPLAY_ID]");
        pw.println("    Return or override display density.");
        pw.println("  folded-area [reset|LEFT,TOP,RIGHT,BOTTOM]");
        pw.println("    Return or override folded area.");
        pw.println("  overscan [reset|LEFT,TOP,RIGHT,BOTTOM] [-d DISPLAY ID]");
        pw.println("    Set overscan area for display.");
        pw.println("  scaling [off|auto] [-d DISPLAY_ID]");
        pw.println("    Set display scaling mode.");
        pw.println("  dismiss-keyguard");
        pw.println("    Dismiss the keyguard, prompting user for auth if necessary.");
        pw.println("  set-user-rotation [free|lock] [-d DISPLAY_ID] [rotation]");
        pw.println("    Set user rotation mode and user rotation.");
        pw.println("  set-fix-to-user-rotation [-d DISPLAY_ID] [enabled|disabled]");
        pw.println("    Enable or disable rotating display for app requested orientation.");
        if (!Build.IS_USER) {
            pw.println("  tracing (start | stop)");
            pw.println("    Start or stop window tracing.");
        }
    }
}
