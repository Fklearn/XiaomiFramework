package com.android.server.pm;

import android.content.pm.IOtaDexopt;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Locale;

class OtaDexoptShellCommand extends ShellCommand {
    final IOtaDexopt mInterface;

    OtaDexoptShellCommand(OtaDexoptService service) {
        this.mInterface = service;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r9) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0008
            r0 = 0
            int r0 = r8.handleDefaultCommands(r0)
            return r0
        L_0x0008:
            java.io.PrintWriter r0 = r8.getOutPrintWriter()
            r1 = -1
            int r2 = r9.hashCode()     // Catch:{ RemoteException -> 0x008a }
            r3 = 5
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 1
            switch(r2) {
                case -1001078227: goto L_0x004f;
                case -318370553: goto L_0x0044;
                case 3089282: goto L_0x003a;
                case 3377907: goto L_0x002f;
                case 3540684: goto L_0x0024;
                case 856774308: goto L_0x001a;
                default: goto L_0x0019;
            }     // Catch:{ RemoteException -> 0x008a }
        L_0x0019:
            goto L_0x005a
        L_0x001a:
            java.lang.String r2 = "cleanup"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = r7
            goto L_0x005b
        L_0x0024:
            java.lang.String r2 = "step"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = r5
            goto L_0x005b
        L_0x002f:
            java.lang.String r2 = "next"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = r4
            goto L_0x005b
        L_0x003a:
            java.lang.String r2 = "done"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = r6
            goto L_0x005b
        L_0x0044:
            java.lang.String r2 = "prepare"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = 0
            goto L_0x005b
        L_0x004f:
            java.lang.String r2 = "progress"
            boolean r2 = r9.equals(r2)     // Catch:{ RemoteException -> 0x008a }
            if (r2 == 0) goto L_0x0019
            r2 = r3
            goto L_0x005b
        L_0x005a:
            r2 = r1
        L_0x005b:
            if (r2 == 0) goto L_0x0085
            if (r2 == r7) goto L_0x0080
            if (r2 == r6) goto L_0x007b
            if (r2 == r5) goto L_0x0076
            if (r2 == r4) goto L_0x0071
            if (r2 == r3) goto L_0x006c
            int r1 = r8.handleDefaultCommands(r9)     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x006c:
            int r1 = r8.runOtaProgress()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x0071:
            int r1 = r8.runOtaNext()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x0076:
            int r1 = r8.runOtaStep()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x007b:
            int r1 = r8.runOtaDone()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x0080:
            int r1 = r8.runOtaCleanup()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x0085:
            int r1 = r8.runOtaPrepare()     // Catch:{ RemoteException -> 0x008a }
            return r1
        L_0x008a:
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.OtaDexoptShellCommand.onCommand(java.lang.String):int");
    }

    private int runOtaPrepare() throws RemoteException {
        this.mInterface.prepare();
        getOutPrintWriter().println("Success");
        return 0;
    }

    private int runOtaCleanup() throws RemoteException {
        this.mInterface.cleanup();
        return 0;
    }

    private int runOtaDone() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        if (this.mInterface.isDone()) {
            pw.println("OTA complete.");
            return 0;
        }
        pw.println("OTA incomplete.");
        return 0;
    }

    private int runOtaStep() throws RemoteException {
        this.mInterface.dexoptNextPackage();
        return 0;
    }

    private int runOtaNext() throws RemoteException {
        getOutPrintWriter().println(this.mInterface.nextDexoptCommand());
        return 0;
    }

    private int runOtaProgress() throws RemoteException {
        float progress = this.mInterface.getProgress();
        getOutPrintWriter().format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(progress)});
        return 0;
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("OTA Dexopt (ota) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        pw.println("  prepare");
        pw.println("    Prepare an OTA dexopt pass, collecting all packages.");
        pw.println("  done");
        pw.println("    Replies whether the OTA is complete or not.");
        pw.println("  step");
        pw.println("    OTA dexopt the next package.");
        pw.println("  next");
        pw.println("    Get parameters for OTA dexopt of the next package.");
        pw.println("  cleanup");
        pw.println("    Clean up internal states. Ends an OTA session.");
    }
}
