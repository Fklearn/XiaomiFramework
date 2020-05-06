package com.android.server.webkit;

import android.os.RemoteException;
import android.os.ShellCommand;
import android.webkit.IWebViewUpdateService;
import java.io.PrintWriter;

class WebViewUpdateServiceShellCommand extends ShellCommand {
    final IWebViewUpdateService mInterface;

    WebViewUpdateServiceShellCommand(IWebViewUpdateService service) {
        this.mInterface = service;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0045 A[Catch:{ RemoteException -> 0x005d }] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0058 A[Catch:{ RemoteException -> 0x005d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r8) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0007
            int r0 = r7.handleDefaultCommands(r8)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            r1 = -1
            int r2 = r8.hashCode()     // Catch:{ RemoteException -> 0x005d }
            r3 = -1857752288(0xffffffff9144f320, float:-1.5536592E-28)
            r4 = 0
            r5 = 2
            r6 = 1
            if (r2 == r3) goto L_0x0038
            r3 = -1381305903(0xffffffffadaaf1d1, float:-1.943415E-11)
            if (r2 == r3) goto L_0x002d
            r3 = 436183515(0x19ffa1db, float:2.6431755E-23)
            if (r2 == r3) goto L_0x0023
        L_0x0022:
            goto L_0x0042
        L_0x0023:
            java.lang.String r2 = "disable-multiprocess"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x005d }
            if (r2 == 0) goto L_0x0022
            r2 = r5
            goto L_0x0043
        L_0x002d:
            java.lang.String r2 = "set-webview-implementation"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x005d }
            if (r2 == 0) goto L_0x0022
            r2 = r4
            goto L_0x0043
        L_0x0038:
            java.lang.String r2 = "enable-multiprocess"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x005d }
            if (r2 == 0) goto L_0x0022
            r2 = r6
            goto L_0x0043
        L_0x0042:
            r2 = r1
        L_0x0043:
            if (r2 == 0) goto L_0x0058
            if (r2 == r6) goto L_0x0053
            if (r2 == r5) goto L_0x004e
            int r1 = r7.handleDefaultCommands(r8)     // Catch:{ RemoteException -> 0x005d }
            return r1
        L_0x004e:
            int r1 = r7.enableMultiProcess(r4)     // Catch:{ RemoteException -> 0x005d }
            return r1
        L_0x0053:
            int r1 = r7.enableMultiProcess(r6)     // Catch:{ RemoteException -> 0x005d }
            return r1
        L_0x0058:
            int r1 = r7.setWebViewImplementation()     // Catch:{ RemoteException -> 0x005d }
            return r1
        L_0x005d:
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.webkit.WebViewUpdateServiceShellCommand.onCommand(java.lang.String):int");
    }

    private int setWebViewImplementation() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        String shellChosenPackage = getNextArg();
        if (shellChosenPackage == null) {
            pw.println("Failed to switch, no PACKAGE provided.");
            pw.println("");
            helpSetWebViewImplementation();
            return 1;
        }
        String newPackage = this.mInterface.changeProviderAndSetting(shellChosenPackage);
        if (shellChosenPackage.equals(newPackage)) {
            pw.println("Success");
            return 0;
        }
        pw.println(String.format("Failed to switch to %s, the WebView implementation is now provided by %s.", new Object[]{shellChosenPackage, newPackage}));
        return 1;
    }

    private int enableMultiProcess(boolean enable) throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        this.mInterface.enableMultiProcess(enable);
        pw.println("Success");
        return 0;
    }

    public void helpSetWebViewImplementation() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("  set-webview-implementation PACKAGE");
        pw.println("    Set the WebView implementation to the specified package.");
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("WebView updater commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        helpSetWebViewImplementation();
        pw.println("  enable-multiprocess");
        pw.println("    Enable multi-process mode for WebView");
        pw.println("  disable-multiprocess");
        pw.println("    Disable multi-process mode for WebView");
        pw.println();
    }
}
