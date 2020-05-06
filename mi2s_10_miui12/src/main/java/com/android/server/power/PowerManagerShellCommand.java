package com.android.server.power;

import android.content.Intent;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

class PowerManagerShellCommand extends ShellCommand {
    private static final int LOW_POWER_MODE_ON = 1;
    final IPowerManager mInterface;

    PowerManagerShellCommand(IPowerManager service) {
        this.mInterface = service;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0035 A[Catch:{ RemoteException -> 0x0046 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0041 A[Catch:{ RemoteException -> 0x0046 }] */
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
            int r2 = r6.hashCode()     // Catch:{ RemoteException -> 0x0046 }
            r3 = -531688203(0xffffffffe04f14f5, float:-5.9687283E19)
            r4 = 1
            if (r2 == r3) goto L_0x0027
            r3 = 1369181230(0x519c0c2e, float:8.3777405E10)
            if (r2 == r3) goto L_0x001c
        L_0x001b:
            goto L_0x0032
        L_0x001c:
            java.lang.String r2 = "set-mode"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x0046 }
            if (r2 == 0) goto L_0x001b
            r2 = r4
            goto L_0x0033
        L_0x0027:
            java.lang.String r2 = "set-adaptive-power-saver-enabled"
            boolean r2 = r6.equals(r2)     // Catch:{ RemoteException -> 0x0046 }
            if (r2 == 0) goto L_0x001b
            r2 = 0
            goto L_0x0033
        L_0x0032:
            r2 = r1
        L_0x0033:
            if (r2 == 0) goto L_0x0041
            if (r2 == r4) goto L_0x003c
            int r1 = r5.handleDefaultCommands(r6)     // Catch:{ RemoteException -> 0x0046 }
            return r1
        L_0x003c:
            int r1 = r5.runSetMode()     // Catch:{ RemoteException -> 0x0046 }
            return r1
        L_0x0041:
            int r1 = r5.runSetAdaptiveEnabled()     // Catch:{ RemoteException -> 0x0046 }
            return r1
        L_0x0046:
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerShellCommand.onCommand(java.lang.String):int");
    }

    private int runSetAdaptiveEnabled() throws RemoteException {
        this.mInterface.setAdaptivePowerSaveEnabled(Boolean.parseBoolean(getNextArgRequired()));
        return 0;
    }

    private int runSetMode() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        try {
            int mode = Integer.parseInt(getNextArgRequired());
            IPowerManager iPowerManager = this.mInterface;
            boolean z = true;
            if (mode != 1) {
                z = false;
            }
            iPowerManager.setPowerSaveModeEnabled(z);
            return 0;
        } catch (RuntimeException ex) {
            pw.println("Error: " + ex.toString());
            return -1;
        }
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Power manager (power) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        pw.println("  set-adaptive-power-saver-enabled [true|false]");
        pw.println("    enables or disables adaptive power saver.");
        pw.println("  set-mode MODE");
        pw.println("    sets the power mode of the device to MODE.");
        pw.println("    1 turns low power mode on and 0 turns low power mode off.");
        pw.println();
        Intent.printIntentArgsHelp(pw, "");
    }
}
