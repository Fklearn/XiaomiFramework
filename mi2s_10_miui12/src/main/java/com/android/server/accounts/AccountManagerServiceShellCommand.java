package com.android.server.accounts;

import android.os.ShellCommand;
import android.os.UserHandle;
import java.io.PrintWriter;

final class AccountManagerServiceShellCommand extends ShellCommand {
    final AccountManagerService mService;

    AccountManagerServiceShellCommand(AccountManagerService service) {
        this.mService = service;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0030  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0038  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r5) {
        /*
            r4 = this;
            if (r5 != 0) goto L_0x0007
            int r0 = r4.handleDefaultCommands(r5)
            return r0
        L_0x0007:
            int r0 = r5.hashCode()
            r1 = -859068373(0xffffffffcccba82b, float:-1.06774872E8)
            r2 = 1
            r3 = -1
            if (r0 == r1) goto L_0x0023
            r1 = 789489311(0x2f0ea69f, float:1.297402E-10)
            if (r0 == r1) goto L_0x0018
        L_0x0017:
            goto L_0x002d
        L_0x0018:
            java.lang.String r0 = "set-bind-instant-service-allowed"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r2
            goto L_0x002e
        L_0x0023:
            java.lang.String r0 = "get-bind-instant-service-allowed"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = 0
            goto L_0x002e
        L_0x002d:
            r0 = r3
        L_0x002e:
            if (r0 == 0) goto L_0x0038
            if (r0 == r2) goto L_0x0033
            return r3
        L_0x0033:
            int r0 = r4.runSetBindInstantServiceAllowed()
            return r0
        L_0x0038:
            int r0 = r4.runGetBindInstantServiceAllowed()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerServiceShellCommand.onCommand(java.lang.String):int");
    }

    private int runGetBindInstantServiceAllowed() {
        Integer userId = parseUserId();
        if (userId == null) {
            return -1;
        }
        getOutPrintWriter().println(Boolean.toString(this.mService.getBindInstantServiceAllowed(userId.intValue())));
        return 0;
    }

    private int runSetBindInstantServiceAllowed() {
        Integer userId = parseUserId();
        if (userId == null) {
            return -1;
        }
        String allowed = getNextArgRequired();
        if (allowed == null) {
            getErrPrintWriter().println("Error: no true/false specified");
            return -1;
        }
        this.mService.setBindInstantServiceAllowed(userId.intValue(), Boolean.parseBoolean(allowed));
        return 0;
    }

    private Integer parseUserId() {
        String option = getNextOption();
        if (option == null) {
            return 0;
        }
        if (option.equals("--user")) {
            return Integer.valueOf(UserHandle.parseUserArg(getNextArgRequired()));
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Unknown option: " + option);
        return null;
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Account manager service commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  set-bind-instant-service-allowed [--user <USER_ID>] true|false ");
        pw.println("    Set whether binding to services provided by instant apps is allowed.");
        pw.println("  get-bind-instant-service-allowed [--user <USER_ID>]");
        pw.println("    Get whether binding to services provided by instant apps is allowed.");
    }
}
