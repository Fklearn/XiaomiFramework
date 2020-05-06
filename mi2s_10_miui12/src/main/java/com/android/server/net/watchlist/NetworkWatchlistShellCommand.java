package com.android.server.net.watchlist;

import android.content.Context;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.provider.Settings;
import com.android.server.wm.ActivityTaskManagerService;
import java.io.FileInputStream;
import java.io.PrintWriter;

class NetworkWatchlistShellCommand extends ShellCommand {
    final Context mContext;
    final NetworkWatchlistService mService;

    NetworkWatchlistShellCommand(NetworkWatchlistService service, Context context) {
        this.mContext = context;
        this.mService = service;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0034 A[Catch:{ Exception -> 0x0045 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0040 A[Catch:{ Exception -> 0x0045 }] */
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
            int r2 = r6.hashCode()     // Catch:{ Exception -> 0x0045 }
            r3 = 1757613042(0x68c30bf2, float:7.3686545E24)
            r4 = 1
            if (r2 == r3) goto L_0x0026
            r3 = 1854202282(0x6e84e1aa, float:2.0562416E28)
            if (r2 == r3) goto L_0x001c
        L_0x001b:
            goto L_0x0031
        L_0x001c:
            java.lang.String r2 = "force-generate-report"
            boolean r2 = r6.equals(r2)     // Catch:{ Exception -> 0x0045 }
            if (r2 == 0) goto L_0x001b
            r2 = r4
            goto L_0x0032
        L_0x0026:
            java.lang.String r2 = "set-test-config"
            boolean r2 = r6.equals(r2)     // Catch:{ Exception -> 0x0045 }
            if (r2 == 0) goto L_0x001b
            r2 = 0
            goto L_0x0032
        L_0x0031:
            r2 = r1
        L_0x0032:
            if (r2 == 0) goto L_0x0040
            if (r2 == r4) goto L_0x003b
            int r1 = r5.handleDefaultCommands(r6)     // Catch:{ Exception -> 0x0045 }
            return r1
        L_0x003b:
            int r1 = r5.runForceGenerateReport()     // Catch:{ Exception -> 0x0045 }
            return r1
        L_0x0040:
            int r1 = r5.runSetTestConfig()     // Catch:{ Exception -> 0x0045 }
            return r1
        L_0x0045:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Exception: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.watchlist.NetworkWatchlistShellCommand.onCommand(java.lang.String):int");
    }

    private int runSetTestConfig() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        try {
            ParcelFileDescriptor pfd = openFileForSystem(getNextArgRequired(), ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
            if (pfd != null) {
                WatchlistConfig.getInstance().setTestMode(new FileInputStream(pfd.getFileDescriptor()));
            }
            pw.println("Success!");
            return 0;
        } catch (Exception ex) {
            pw.println("Error: " + ex.toString());
            return -1;
        }
    }

    private int runForceGenerateReport() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        long ident = Binder.clearCallingIdentity();
        try {
            if (WatchlistConfig.getInstance().isConfigSecure()) {
                pw.println("Error: Cannot force generate report under production config");
                return -1;
            }
            Settings.Global.putLong(this.mContext.getContentResolver(), "network_watchlist_last_report_time", 0);
            this.mService.forceReportWatchlistForTest(System.currentTimeMillis());
            pw.println("Success!");
            Binder.restoreCallingIdentity(ident);
            return 0;
        } catch (Exception ex) {
            pw.println("Error: " + ex);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Network watchlist manager commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  set-test-config your_watchlist_config.xml");
        pw.println("    Set network watchlist test config file.");
        pw.println("  force-generate-report");
        pw.println("    Force generate watchlist test report.");
    }
}
