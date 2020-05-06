package com.android.server.contentsuggestions;

import android.os.ShellCommand;
import java.io.PrintWriter;

public class ContentSuggestionsManagerServiceShellCommand extends ShellCommand {
    private static final String TAG = ContentSuggestionsManagerServiceShellCommand.class.getSimpleName();
    private final ContentSuggestionsManagerService mService;

    public ContentSuggestionsManagerServiceShellCommand(ContentSuggestionsManagerService service) {
        this.mService = service;
    }

    public int onCommand(String cmd) {
        if (cmd == null) {
            return handleDefaultCommands(cmd);
        }
        PrintWriter pw = getOutPrintWriter();
        char c = 65535;
        int hashCode = cmd.hashCode();
        if (hashCode != 102230) {
            if (hashCode == 113762 && cmd.equals("set")) {
                c = 0;
            }
        } else if (cmd.equals("get")) {
            c = 1;
        }
        if (c == 0) {
            return requestSet(pw);
        }
        if (c != 1) {
            return handleDefaultCommands(cmd);
        }
        return requestGet(pw);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0051, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0052, code lost:
        r0.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0055, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x004a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x004b, code lost:
        if (r1 != null) goto L_0x004d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHelp() {
        /*
            r4 = this;
            java.lang.String r0 = ""
            java.io.PrintWriter r1 = r4.getOutPrintWriter()
            java.lang.String r2 = "ContentSuggestionsManagerService commands:"
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "  help"
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "    Prints this help text."
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            r1.println(r0)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "  set temporary-service USER_ID [COMPONENT_NAME DURATION]"
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "    Temporarily (for DURATION ms) changes the service implementation."
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "    To reset, call with just the USER_ID argument."
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            r1.println(r0)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "  set default-service-enabled USER_ID [true|false]"
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "    Enable / disable the default service for the user."
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            r1.println(r0)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "  get default-service-enabled USER_ID"
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "    Checks whether the default service is enabled for the user."
            r1.println(r2)     // Catch:{ all -> 0x0048 }
            r1.println(r0)     // Catch:{ all -> 0x0048 }
            r1.close()
            return
        L_0x0048:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x004a }
        L_0x004a:
            r2 = move-exception
            if (r1 == 0) goto L_0x0055
            r1.close()     // Catch:{ all -> 0x0051 }
            goto L_0x0055
        L_0x0051:
            r3 = move-exception
            r0.addSuppressed(r3)
        L_0x0055:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentsuggestions.ContentSuggestionsManagerServiceShellCommand.onHelp():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0049  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int requestSet(java.io.PrintWriter r6) {
        /*
            r5 = this;
            java.lang.String r0 = r5.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = 529654941(0x1f91e49d, float:6.17881E-20)
            r3 = 1
            r4 = -1
            if (r1 == r2) goto L_0x0020
            r2 = 2003978041(0x77724739, float:4.913986E33)
            if (r1 == r2) goto L_0x0015
        L_0x0014:
            goto L_0x002a
        L_0x0015:
            java.lang.String r1 = "temporary-service"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0014
            r1 = 0
            goto L_0x002b
        L_0x0020:
            java.lang.String r1 = "default-service-enabled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0014
            r1 = r3
            goto L_0x002b
        L_0x002a:
            r1 = r4
        L_0x002b:
            if (r1 == 0) goto L_0x0049
            if (r1 == r3) goto L_0x0044
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid set: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r6.println(r1)
            return r4
        L_0x0044:
            int r1 = r5.setDefaultServiceEnabled()
            return r1
        L_0x0049:
            int r1 = r5.setTemporaryService(r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentsuggestions.ContentSuggestionsManagerServiceShellCommand.requestSet(java.io.PrintWriter):int");
    }

    private int requestGet(PrintWriter pw) {
        String what = getNextArgRequired();
        if (!((what.hashCode() == 529654941 && what.equals("default-service-enabled")) ? false : true)) {
            return getDefaultServiceEnabled(pw);
        }
        pw.println("Invalid get: " + what);
        return -1;
    }

    private int setTemporaryService(PrintWriter pw) {
        int userId = Integer.parseInt(getNextArgRequired());
        String serviceName = getNextArg();
        if (serviceName == null) {
            this.mService.resetTemporaryService(userId);
            return 0;
        }
        int duration = Integer.parseInt(getNextArgRequired());
        this.mService.setTemporaryService(userId, serviceName, duration);
        pw.println("ContentSuggestionsService temporarily set to " + serviceName + " for " + duration + "ms");
        return 0;
    }

    private int setDefaultServiceEnabled() {
        this.mService.setDefaultServiceEnabled(getNextIntArgRequired(), Boolean.parseBoolean(getNextArg()));
        return 0;
    }

    private int getDefaultServiceEnabled(PrintWriter pw) {
        pw.println(this.mService.isDefaultServiceEnabled(getNextIntArgRequired()));
        return 0;
    }

    private int getNextIntArgRequired() {
        return Integer.parseInt(getNextArgRequired());
    }
}
