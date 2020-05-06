package com.android.server.net;

import android.content.Context;
import android.net.NetworkPolicyManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

class NetworkPolicyManagerShellCommand extends ShellCommand {
    private final NetworkPolicyManagerService mInterface;
    private final WifiManager mWifiManager;

    NetworkPolicyManagerShellCommand(Context context, NetworkPolicyManagerService service) {
        this.mInterface = service;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
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
            r1 = -1
            int r2 = r8.hashCode()     // Catch:{ RemoteException -> 0x0076 }
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r2) {
                case -934610812: goto L_0x0042;
                case 96417: goto L_0x0038;
                case 102230: goto L_0x002e;
                case 113762: goto L_0x0023;
                case 3322014: goto L_0x0018;
                default: goto L_0x0017;
            }     // Catch:{ RemoteException -> 0x0076 }
        L_0x0017:
            goto L_0x004d
        L_0x0018:
            java.lang.String r2 = "list"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0017
            r2 = r5
            goto L_0x004e
        L_0x0023:
            java.lang.String r2 = "set"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0017
            r2 = r6
            goto L_0x004e
        L_0x002e:
            java.lang.String r2 = "get"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0017
            r2 = 0
            goto L_0x004e
        L_0x0038:
            java.lang.String r2 = "add"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0017
            r2 = r4
            goto L_0x004e
        L_0x0042:
            java.lang.String r2 = "remove"
            boolean r2 = r8.equals(r2)     // Catch:{ RemoteException -> 0x0076 }
            if (r2 == 0) goto L_0x0017
            r2 = r3
            goto L_0x004e
        L_0x004d:
            r2 = r1
        L_0x004e:
            if (r2 == 0) goto L_0x0071
            if (r2 == r6) goto L_0x006c
            if (r2 == r5) goto L_0x0067
            if (r2 == r4) goto L_0x0062
            if (r2 == r3) goto L_0x005d
            int r1 = r7.handleDefaultCommands(r8)     // Catch:{ RemoteException -> 0x0076 }
            return r1
        L_0x005d:
            int r1 = r7.runRemove()     // Catch:{ RemoteException -> 0x0076 }
            return r1
        L_0x0062:
            int r1 = r7.runAdd()     // Catch:{ RemoteException -> 0x0076 }
            return r1
        L_0x0067:
            int r1 = r7.runList()     // Catch:{ RemoteException -> 0x0076 }
            return r1
        L_0x006c:
            int r1 = r7.runSet()     // Catch:{ RemoteException -> 0x0076 }
            return r1
        L_0x0071:
            int r1 = r7.runGet()     // Catch:{ RemoteException -> 0x0076 }
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
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.onCommand(java.lang.String):int");
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Network policy manager (netpolicy) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("");
        pw.println("  add restrict-background-whitelist UID");
        pw.println("    Adds a UID to the whitelist for restrict background usage.");
        pw.println("  add restrict-background-blacklist UID");
        pw.println("    Adds a UID to the blacklist for restrict background usage.");
        pw.println("  add app-idle-whitelist UID");
        pw.println("    Adds a UID to the temporary app idle whitelist.");
        pw.println("  get restrict-background");
        pw.println("    Gets the global restrict background usage status.");
        pw.println("  list wifi-networks [true|false]");
        pw.println("    Lists all saved wifi networks and whether they are metered or not.");
        pw.println("    If a boolean argument is passed, filters just the metered (or unmetered)");
        pw.println("    networks.");
        pw.println("  list restrict-background-whitelist");
        pw.println("    Lists UIDs that are whitelisted for restrict background usage.");
        pw.println("  list restrict-background-blacklist");
        pw.println("    Lists UIDs that are blacklisted for restrict background usage.");
        pw.println("  remove restrict-background-whitelist UID");
        pw.println("    Removes a UID from the whitelist for restrict background usage.");
        pw.println("  remove restrict-background-blacklist UID");
        pw.println("    Removes a UID from the blacklist for restrict background usage.");
        pw.println("  remove app-idle-whitelist UID");
        pw.println("    Removes a UID from the temporary app idle whitelist.");
        pw.println("  set metered-network ID [undefined|true|false]");
        pw.println("    Toggles whether the given wi-fi network is metered.");
        pw.println("  set restrict-background BOOLEAN");
        pw.println("    Sets the global restrict background usage status.");
        pw.println("  set sub-plan-owner subId [packageName]");
        pw.println("    Sets the data plan owner package for subId.");
    }

    private int runGet() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        String type = getNextArg();
        if (type == null) {
            pw.println("Error: didn't specify type of data to get");
            return -1;
        }
        if (!((type.hashCode() == -747095841 && type.equals("restrict-background")) ? false : true)) {
            return getRestrictBackground();
        }
        pw.println("Error: unknown get type '" + type + "'");
        return -1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runSet() throws android.os.RemoteException {
        /*
            r7 = this;
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            java.lang.String r1 = r7.getNextArg()
            r2 = -1
            if (r1 != 0) goto L_0x0011
            java.lang.String r3 = "Error: didn't specify type of data to set"
            r0.println(r3)
            return r2
        L_0x0011:
            int r3 = r1.hashCode()
            r4 = -983249079(0xffffffffc564cf49, float:-3660.9553)
            r5 = 2
            r6 = 1
            if (r3 == r4) goto L_0x003d
            r4 = -747095841(0xffffffffd37838df, float:-1.06610603E12)
            if (r3 == r4) goto L_0x0032
            r4 = 1846940860(0x6e1614bc, float:1.1611954E28)
            if (r3 == r4) goto L_0x0027
        L_0x0026:
            goto L_0x0048
        L_0x0027:
            java.lang.String r3 = "sub-plan-owner"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r5
            goto L_0x0049
        L_0x0032:
            java.lang.String r3 = "restrict-background"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r6
            goto L_0x0049
        L_0x003d:
            java.lang.String r3 = "metered-network"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = 0
            goto L_0x0049
        L_0x0048:
            r3 = r2
        L_0x0049:
            if (r3 == 0) goto L_0x0073
            if (r3 == r6) goto L_0x006e
            if (r3 == r5) goto L_0x0069
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown set type '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r2
        L_0x0069:
            int r2 = r7.setSubPlanOwner()
            return r2
        L_0x006e:
            int r2 = r7.setRestrictBackground()
            return r2
        L_0x0073:
            int r2 = r7.setMeteredWifiNetwork()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.runSet():int");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runList() throws android.os.RemoteException {
        /*
            r7 = this;
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            java.lang.String r1 = r7.getNextArg()
            r2 = -1
            if (r1 != 0) goto L_0x0011
            java.lang.String r3 = "Error: didn't specify type of data to list"
            r0.println(r3)
            return r2
        L_0x0011:
            int r3 = r1.hashCode()
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r3) {
                case -1683867974: goto L_0x003d;
                case -668534353: goto L_0x0032;
                case -363534403: goto L_0x0027;
                case 639570137: goto L_0x001c;
                default: goto L_0x001b;
            }
        L_0x001b:
            goto L_0x0047
        L_0x001c:
            java.lang.String r3 = "restrict-background-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x001b
            r3 = r5
            goto L_0x0048
        L_0x0027:
            java.lang.String r3 = "wifi-networks"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x001b
            r3 = r6
            goto L_0x0048
        L_0x0032:
            java.lang.String r3 = "restrict-background-blacklist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x001b
            r3 = r4
            goto L_0x0048
        L_0x003d:
            java.lang.String r3 = "app-idle-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x001b
            r3 = 0
            goto L_0x0048
        L_0x0047:
            r3 = r2
        L_0x0048:
            if (r3 == 0) goto L_0x0079
            if (r3 == r6) goto L_0x0074
            if (r3 == r5) goto L_0x006f
            if (r3 == r4) goto L_0x006a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown list type '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r2
        L_0x006a:
            int r2 = r7.listRestrictBackgroundBlacklist()
            return r2
        L_0x006f:
            int r2 = r7.listRestrictBackgroundWhitelist()
            return r2
        L_0x0074:
            int r2 = r7.listWifiNetworks()
            return r2
        L_0x0079:
            int r2 = r7.listAppIdleWhitelist()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.runList():int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0072  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runAdd() throws android.os.RemoteException {
        /*
            r7 = this;
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            java.lang.String r1 = r7.getNextArg()
            r2 = -1
            if (r1 != 0) goto L_0x0011
            java.lang.String r3 = "Error: didn't specify type of data to add"
            r0.println(r3)
            return r2
        L_0x0011:
            int r3 = r1.hashCode()
            r4 = -1683867974(0xffffffff9ba236ba, float:-2.6836018E-22)
            r5 = 2
            r6 = 1
            if (r3 == r4) goto L_0x003d
            r4 = -668534353(0xffffffffd826f9af, float:-7.3436525E14)
            if (r3 == r4) goto L_0x0032
            r4 = 639570137(0x261f10d9, float:5.518704E-16)
            if (r3 == r4) goto L_0x0027
        L_0x0026:
            goto L_0x0047
        L_0x0027:
            java.lang.String r3 = "restrict-background-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = 0
            goto L_0x0048
        L_0x0032:
            java.lang.String r3 = "restrict-background-blacklist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r6
            goto L_0x0048
        L_0x003d:
            java.lang.String r3 = "app-idle-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r5
            goto L_0x0048
        L_0x0047:
            r3 = r2
        L_0x0048:
            if (r3 == 0) goto L_0x0072
            if (r3 == r6) goto L_0x006d
            if (r3 == r5) goto L_0x0068
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown add type '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r2
        L_0x0068:
            int r2 = r7.addAppIdleWhitelist()
            return r2
        L_0x006d:
            int r2 = r7.addRestrictBackgroundBlacklist()
            return r2
        L_0x0072:
            int r2 = r7.addRestrictBackgroundWhitelist()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.runAdd():int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0072  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runRemove() throws android.os.RemoteException {
        /*
            r7 = this;
            java.io.PrintWriter r0 = r7.getOutPrintWriter()
            java.lang.String r1 = r7.getNextArg()
            r2 = -1
            if (r1 != 0) goto L_0x0011
            java.lang.String r3 = "Error: didn't specify type of data to remove"
            r0.println(r3)
            return r2
        L_0x0011:
            int r3 = r1.hashCode()
            r4 = -1683867974(0xffffffff9ba236ba, float:-2.6836018E-22)
            r5 = 2
            r6 = 1
            if (r3 == r4) goto L_0x003d
            r4 = -668534353(0xffffffffd826f9af, float:-7.3436525E14)
            if (r3 == r4) goto L_0x0032
            r4 = 639570137(0x261f10d9, float:5.518704E-16)
            if (r3 == r4) goto L_0x0027
        L_0x0026:
            goto L_0x0047
        L_0x0027:
            java.lang.String r3 = "restrict-background-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = 0
            goto L_0x0048
        L_0x0032:
            java.lang.String r3 = "restrict-background-blacklist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r6
            goto L_0x0048
        L_0x003d:
            java.lang.String r3 = "app-idle-whitelist"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0026
            r3 = r5
            goto L_0x0048
        L_0x0047:
            r3 = r2
        L_0x0048:
            if (r3 == 0) goto L_0x0072
            if (r3 == r6) goto L_0x006d
            if (r3 == r5) goto L_0x0068
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown remove type '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r2
        L_0x0068:
            int r2 = r7.removeAppIdleWhitelist()
            return r2
        L_0x006d:
            int r2 = r7.removeRestrictBackgroundBlacklist()
            return r2
        L_0x0072:
            int r2 = r7.removeRestrictBackgroundWhitelist()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.runRemove():int");
    }

    private int listUidPolicies(String msg, int policy) throws RemoteException {
        return listUidList(msg, this.mInterface.getUidsWithPolicy(policy));
    }

    private int listUidList(String msg, int[] uids) {
        PrintWriter pw = getOutPrintWriter();
        pw.print(msg);
        pw.print(": ");
        if (uids.length == 0) {
            pw.println("none");
        } else {
            for (int uid : uids) {
                pw.print(uid);
                pw.print(' ');
            }
        }
        pw.println();
        return 0;
    }

    private int listRestrictBackgroundWhitelist() throws RemoteException {
        return listUidPolicies("Restrict background whitelisted UIDs", 4);
    }

    private int listRestrictBackgroundBlacklist() throws RemoteException {
        return listUidPolicies("Restrict background blacklisted UIDs", 1);
    }

    private int listAppIdleWhitelist() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        return listUidList("App Idle whitelisted UIDs", this.mInterface.getAppIdleWhitelist());
    }

    private int getRestrictBackground() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        pw.print("Restrict background status: ");
        pw.println(this.mInterface.getRestrictBackground() ? "enabled" : "disabled");
        return 0;
    }

    private int setRestrictBackground() throws RemoteException {
        int enabled = getNextBooleanArg();
        if (enabled < 0) {
            return enabled;
        }
        this.mInterface.setRestrictBackground(enabled > 0);
        return 0;
    }

    private int setSubPlanOwner() throws RemoteException {
        this.mInterface.setSubscriptionPlansOwner(Integer.parseInt(getNextArgRequired()), getNextArg());
        return 0;
    }

    private int setUidPolicy(int policy) throws RemoteException {
        int uid = getUidFromNextArg();
        if (uid < 0) {
            return uid;
        }
        this.mInterface.setUidPolicy(uid, policy);
        return 0;
    }

    private int resetUidPolicy(String errorMessage, int expectedPolicy) throws RemoteException {
        int uid = getUidFromNextArg();
        if (uid < 0) {
            return uid;
        }
        if (this.mInterface.getUidPolicy(uid) != expectedPolicy) {
            PrintWriter pw = getOutPrintWriter();
            pw.print("Error: UID ");
            pw.print(uid);
            pw.print(' ');
            pw.println(errorMessage);
            return -1;
        }
        this.mInterface.setUidPolicy(uid, 0);
        return 0;
    }

    private int addRestrictBackgroundWhitelist() throws RemoteException {
        return setUidPolicy(4);
    }

    private int removeRestrictBackgroundWhitelist() throws RemoteException {
        return resetUidPolicy("not whitelisted", 4);
    }

    private int addRestrictBackgroundBlacklist() throws RemoteException {
        return setUidPolicy(1);
    }

    private int removeRestrictBackgroundBlacklist() throws RemoteException {
        return resetUidPolicy("not blacklisted", 1);
    }

    private int setAppIdleWhitelist(boolean isWhitelisted) {
        int uid = getUidFromNextArg();
        if (uid < 0) {
            return uid;
        }
        this.mInterface.setAppIdleWhitelist(uid, isWhitelisted);
        return 0;
    }

    private int addAppIdleWhitelist() throws RemoteException {
        return setAppIdleWhitelist(true);
    }

    private int removeAppIdleWhitelist() throws RemoteException {
        return setAppIdleWhitelist(false);
    }

    private int listWifiNetworks() {
        int match;
        PrintWriter pw = getOutPrintWriter();
        String arg = getNextArg();
        if (arg == null) {
            match = 0;
        } else if (Boolean.parseBoolean(arg) != 0) {
            match = 1;
        } else {
            match = 2;
        }
        for (WifiConfiguration config : this.mWifiManager.getConfiguredNetworks()) {
            if (arg == null || config.meteredOverride == match) {
                pw.print(NetworkPolicyManager.resolveNetworkId(config));
                pw.print(';');
                pw.println(overrideToString(config.meteredOverride));
            }
        }
        return 0;
    }

    private int setMeteredWifiNetwork() throws RemoteException {
        PrintWriter pw = getOutPrintWriter();
        String networkId = getNextArg();
        if (networkId == null) {
            pw.println("Error: didn't specify networkId");
            return -1;
        }
        String arg = getNextArg();
        if (arg == null) {
            pw.println("Error: didn't specify meteredOverride");
            return -1;
        }
        this.mInterface.setWifiMeteredOverride(NetworkPolicyManager.resolveNetworkId(networkId), stringToOverride(arg));
        return -1;
    }

    private static String overrideToString(int override) {
        if (override == 1) {
            return "true";
        }
        if (override != 2) {
            return "none";
        }
        return "false";
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002e A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int stringToOverride(java.lang.String r4) {
        /*
            int r0 = r4.hashCode()
            r1 = 3569038(0x36758e, float:5.001287E-39)
            r2 = 0
            r3 = 1
            if (r0 == r1) goto L_0x001b
            r1 = 97196323(0x5cb1923, float:1.9099262E-35)
            if (r0 == r1) goto L_0x0011
        L_0x0010:
            goto L_0x0026
        L_0x0011:
            java.lang.String r0 = "false"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = r3
            goto L_0x0027
        L_0x001b:
            java.lang.String r0 = "true"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = r2
            goto L_0x0027
        L_0x0026:
            r0 = -1
        L_0x0027:
            if (r0 == 0) goto L_0x002e
            if (r0 == r3) goto L_0x002c
            return r2
        L_0x002c:
            r0 = 2
            return r0
        L_0x002e:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerShellCommand.stringToOverride(java.lang.String):int");
    }

    private int getNextBooleanArg() {
        PrintWriter pw = getOutPrintWriter();
        String arg = getNextArg();
        if (arg != null) {
            return Boolean.valueOf(arg).booleanValue() ? 1 : 0;
        }
        pw.println("Error: didn't specify BOOLEAN");
        return -1;
    }

    private int getUidFromNextArg() {
        PrintWriter pw = getOutPrintWriter();
        String arg = getNextArg();
        if (arg == null) {
            pw.println("Error: didn't specify UID");
            return -1;
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            pw.println("Error: UID (" + arg + ") should be a number");
            return -2;
        }
    }
}
