package com.android.server.wifi;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.ShellCommand;
import com.android.server.wifi.WifiNative;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WifiShellCommand extends ShellCommand {
    private final ClientModeImpl mClientModeImpl;
    private final IPackageManager mPM = AppGlobals.getPackageManager();
    private final WifiConfigManager mWifiConfigManager;
    private final WifiLockManager mWifiLockManager;
    private final WifiNetworkSuggestionsManager mWifiNetworkSuggestionsManager;

    WifiShellCommand(WifiInjector wifiInjector) {
        this.mClientModeImpl = wifiInjector.getClientModeImpl();
        this.mWifiLockManager = wifiInjector.getWifiLockManager();
        this.mWifiNetworkSuggestionsManager = wifiInjector.getWifiNetworkSuggestionsManager();
        this.mWifiConfigManager = wifiInjector.getWifiConfigManager();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r12) {
        /*
            r11 = this;
            java.lang.String r0 = "Invalid argument to 'set-poll-rssi-interval-msecs' - must be a positive integer"
            r11.checkRootPermission()
            java.io.PrintWriter r1 = r11.getOutPrintWriter()
            r2 = -1
            if (r12 == 0) goto L_0x000e
            r3 = r12
            goto L_0x0010
        L_0x000e:
            java.lang.String r3 = ""
        L_0x0010:
            int r4 = r3.hashCode()     // Catch:{ Exception -> 0x01a5 }
            r5 = 1
            r6 = 0
            switch(r4) {
                case -1972405815: goto L_0x0084;
                case -1861126232: goto L_0x007a;
                case -1267819290: goto L_0x0070;
                case -1006001187: goto L_0x0066;
                case -505485935: goto L_0x005c;
                case -29690534: goto L_0x0052;
                case 355024770: goto L_0x0047;
                case 571096281: goto L_0x003c;
                case 918812649: goto L_0x0031;
                case 1120712756: goto L_0x0026;
                case 1201296781: goto L_0x001b;
                default: goto L_0x0019;
            }     // Catch:{ Exception -> 0x01a5 }
        L_0x0019:
            goto L_0x008e
        L_0x001b:
            java.lang.String r4 = "force-low-latency-mode"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 5
            goto L_0x008f
        L_0x0026:
            java.lang.String r4 = "get-ipreach-disconnect"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = r5
            goto L_0x008f
        L_0x0031:
            java.lang.String r4 = "clear-deleted-ephemeral-networks"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 9
            goto L_0x008f
        L_0x003c:
            java.lang.String r4 = "network-requests-remove-user-approved-access-points"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 8
            goto L_0x008f
        L_0x0047:
            java.lang.String r4 = "send-link-probe"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 10
            goto L_0x008f
        L_0x0052:
            java.lang.String r4 = "set-poll-rssi-interval-msecs"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 2
            goto L_0x008f
        L_0x005c:
            java.lang.String r4 = "network-suggestions-set-user-approved"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 6
            goto L_0x008f
        L_0x0066:
            java.lang.String r4 = "force-hi-perf-mode"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 4
            goto L_0x008f
        L_0x0070:
            java.lang.String r4 = "get-poll-rssi-interval-msecs"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 3
            goto L_0x008f
        L_0x007a:
            java.lang.String r4 = "set-ipreach-disconnect"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = r6
            goto L_0x008f
        L_0x0084:
            java.lang.String r4 = "network-suggestions-has-user-approved"
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0019
            r3 = 7
            goto L_0x008f
        L_0x008e:
            r3 = r2
        L_0x008f:
            java.lang.String r4 = "Command execution failed"
            java.lang.String r7 = "no"
            java.lang.String r8 = "yes"
            java.lang.String r9 = "disabled"
            java.lang.String r10 = "enabled"
            switch(r3) {
                case 0: goto L_0x0185;
                case 1: goto L_0x016a;
                case 2: goto L_0x0150;
                case 3: goto L_0x0135;
                case 4: goto L_0x0110;
                case 5: goto L_0x00eb;
                case 6: goto L_0x00c8;
                case 7: goto L_0x00b7;
                case 8: goto L_0x00ad;
                case 9: goto L_0x00a7;
                case 10: goto L_0x00a2;
                default: goto L_0x009c;
            }
        L_0x009c:
            int r0 = r11.handleDefaultCommands(r12)     // Catch:{ Exception -> 0x01a5 }
            goto L_0x01a4
        L_0x00a2:
            int r0 = r11.sendLinkProbe(r1)     // Catch:{ Exception -> 0x01a5 }
            return r0
        L_0x00a7:
            com.android.server.wifi.WifiConfigManager r0 = r11.mWifiConfigManager     // Catch:{ Exception -> 0x01a5 }
            r0.clearDeletedEphemeralNetworks()     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x00ad:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            com.android.server.wifi.ClientModeImpl r3 = r11.mClientModeImpl     // Catch:{ Exception -> 0x01a5 }
            r3.removeNetworkRequestUserApprovedAccessPointsForApp(r0)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x00b7:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            com.android.server.wifi.WifiNetworkSuggestionsManager r3 = r11.mWifiNetworkSuggestionsManager     // Catch:{ Exception -> 0x01a5 }
            boolean r3 = r3.hasUserApprovedForApp(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x00c4
            r7 = r8
        L_0x00c4:
            r1.println(r7)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x00c8:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            java.lang.String r3 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            boolean r4 = r8.equals(r3)     // Catch:{ Exception -> 0x01a5 }
            if (r4 == 0) goto L_0x00d8
            r4 = 1
            goto L_0x00df
        L_0x00d8:
            boolean r4 = r7.equals(r3)     // Catch:{ Exception -> 0x01a5 }
            if (r4 == 0) goto L_0x00e5
            r4 = 0
        L_0x00df:
            com.android.server.wifi.WifiNetworkSuggestionsManager r5 = r11.mWifiNetworkSuggestionsManager     // Catch:{ Exception -> 0x01a5 }
            r5.setHasUserApprovedForApp(r4, r0)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x00e5:
            java.lang.String r4 = "Invalid argument to 'network-suggestions-set-user-approved' - must be 'yes' or 'no'"
            r1.println(r4)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x00eb:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            boolean r3 = r10.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x00f7
            r3 = 1
            goto L_0x00fe
        L_0x00f7:
            boolean r3 = r9.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x010a
            r3 = 0
        L_0x00fe:
            com.android.server.wifi.WifiLockManager r5 = r11.mWifiLockManager     // Catch:{ Exception -> 0x01a5 }
            boolean r5 = r5.forceLowLatencyMode(r3)     // Catch:{ Exception -> 0x01a5 }
            if (r5 != 0) goto L_0x0109
            r1.println(r4)     // Catch:{ Exception -> 0x01a5 }
        L_0x0109:
            return r6
        L_0x010a:
            java.lang.String r3 = "Invalid argument to 'force-low-latency-mode' - must be 'enabled' or 'disabled'"
            r1.println(r3)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x0110:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            boolean r3 = r10.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x011c
            r3 = 1
            goto L_0x0123
        L_0x011c:
            boolean r3 = r9.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x012f
            r3 = 0
        L_0x0123:
            com.android.server.wifi.WifiLockManager r5 = r11.mWifiLockManager     // Catch:{ Exception -> 0x01a5 }
            boolean r5 = r5.forceHiPerfMode(r3)     // Catch:{ Exception -> 0x01a5 }
            if (r5 != 0) goto L_0x012e
            r1.println(r4)     // Catch:{ Exception -> 0x01a5 }
        L_0x012e:
            return r6
        L_0x012f:
            java.lang.String r3 = "Invalid argument to 'force-hi-perf-mode' - must be 'enabled' or 'disabled'"
            r1.println(r3)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x0135:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a5 }
            r0.<init>()     // Catch:{ Exception -> 0x01a5 }
            java.lang.String r3 = "ClientModeImpl.mPollRssiIntervalMsecs = "
            r0.append(r3)     // Catch:{ Exception -> 0x01a5 }
            com.android.server.wifi.ClientModeImpl r3 = r11.mClientModeImpl     // Catch:{ Exception -> 0x01a5 }
            int r3 = r3.getPollRssiIntervalMsecs()     // Catch:{ Exception -> 0x01a5 }
            r0.append(r3)     // Catch:{ Exception -> 0x01a5 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x01a5 }
            r1.println(r0)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x0150:
            java.lang.String r3 = r11.getNextArgRequired()     // Catch:{ NumberFormatException -> 0x0165 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x0165 }
            if (r3 >= r5) goto L_0x015f
            r1.println(r0)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x015f:
            com.android.server.wifi.ClientModeImpl r0 = r11.mClientModeImpl     // Catch:{ Exception -> 0x01a5 }
            r0.setPollRssiIntervalMsecs(r3)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x0165:
            r3 = move-exception
            r1.println(r0)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x016a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a5 }
            r0.<init>()     // Catch:{ Exception -> 0x01a5 }
            java.lang.String r3 = "IPREACH_DISCONNECT state is "
            r0.append(r3)     // Catch:{ Exception -> 0x01a5 }
            com.android.server.wifi.ClientModeImpl r3 = r11.mClientModeImpl     // Catch:{ Exception -> 0x01a5 }
            boolean r3 = r3.getIpReachabilityDisconnectEnabled()     // Catch:{ Exception -> 0x01a5 }
            r0.append(r3)     // Catch:{ Exception -> 0x01a5 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x01a5 }
            r1.println(r0)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x0185:
            java.lang.String r0 = r11.getNextArgRequired()     // Catch:{ Exception -> 0x01a5 }
            boolean r3 = r10.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x0191
            r3 = 1
            goto L_0x0198
        L_0x0191:
            boolean r3 = r9.equals(r0)     // Catch:{ Exception -> 0x01a5 }
            if (r3 == 0) goto L_0x019e
            r3 = 0
        L_0x0198:
            com.android.server.wifi.ClientModeImpl r4 = r11.mClientModeImpl     // Catch:{ Exception -> 0x01a5 }
            r4.setIpReachabilityDisconnectEnabled(r3)     // Catch:{ Exception -> 0x01a5 }
            return r6
        L_0x019e:
            java.lang.String r3 = "Invalid argument to 'set-ipreach-disconnect' - must be 'enabled' or 'disabled'"
            r1.println(r3)     // Catch:{ Exception -> 0x01a5 }
            return r2
        L_0x01a4:
            return r0
        L_0x01a5:
            r0 = move-exception
            java.lang.String r3 = "Exception while executing WifiShellCommand: "
            r1.println(r3)
            r0.printStackTrace(r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiShellCommand.onCommand(java.lang.String):int");
    }

    private int sendLinkProbe(PrintWriter pw) throws InterruptedException {
        final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        this.mClientModeImpl.probeLink(new WifiNative.SendMgmtFrameCallback() {
            public void onAck(int elapsedTimeMs) {
                ArrayBlockingQueue arrayBlockingQueue = queue;
                arrayBlockingQueue.offer("Link probe succeeded after " + elapsedTimeMs + " ms");
            }

            public void onFailure(int reason) {
                ArrayBlockingQueue arrayBlockingQueue = queue;
                arrayBlockingQueue.offer("Link probe failed with reason " + reason);
            }
        }, -1);
        String msg = queue.poll(2000, TimeUnit.MILLISECONDS);
        if (msg == null) {
            pw.println("Link probe timed out");
            return 0;
        }
        pw.println(msg);
        return 0;
    }

    private void checkRootPermission() {
        int uid = Binder.getCallingUid();
        if (uid != 0) {
            throw new SecurityException("Uid " + uid + " does not have access to wifi commands");
        }
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Wi-Fi (wifi) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  set-ipreach-disconnect enabled|disabled");
        pw.println("    Sets whether CMD_IP_REACHABILITY_LOST events should trigger disconnects.");
        pw.println("  get-ipreach-disconnect");
        pw.println("    Gets setting of CMD_IP_REACHABILITY_LOST events triggering disconnects.");
        pw.println("  set-poll-rssi-interval-msecs <int>");
        pw.println("    Sets the interval between RSSI polls to <int> milliseconds.");
        pw.println("  get-poll-rssi-interval-msecs");
        pw.println("    Gets current interval between RSSI polls, in milliseconds.");
        pw.println("  force-hi-perf-mode enabled|disabled");
        pw.println("    Sets whether hi-perf mode is forced or left for normal operation.");
        pw.println("  force-low-latency-mode enabled|disabled");
        pw.println("    Sets whether low latency mode is forced or left for normal operation.");
        pw.println("  network-suggestions-set-user-approved <package name> yes|no");
        pw.println("    Sets whether network suggestions from the app is approved or not.");
        pw.println("  network-suggestions-has-user-approved <package name>");
        pw.println("    Queries whether network suggestions from the app is approved or not.");
        pw.println("  network-requests-remove-user-approved-access-points <package name>");
        pw.println("    Removes all user approved network requests for the app.");
        pw.println("  clear-deleted-ephemeral-networks");
        pw.println("    Clears the deleted ephemeral networks list.");
        pw.println("  send-link-probe");
        pw.println("    Manually triggers a link probe.");
        pw.println();
    }
}
