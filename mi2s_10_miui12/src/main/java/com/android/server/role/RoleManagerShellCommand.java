package com.android.server.role;

import android.app.role.IRoleManager;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.util.Log;
import com.android.server.role.RoleManagerShellCommand;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class RoleManagerShellCommand extends ShellCommand {
    private final IRoleManager mRoleManager;

    RoleManagerShellCommand(IRoleManager roleManager) {
        this.mRoleManager = roleManager;
    }

    private class CallbackFuture extends CompletableFuture<Void> {
        private CallbackFuture() {
        }

        public RemoteCallback createCallback() {
            return new RemoteCallback(new RemoteCallback.OnResultListener() {
                public final void onResult(Bundle bundle) {
                    RoleManagerShellCommand.CallbackFuture.this.lambda$createCallback$0$RoleManagerShellCommand$CallbackFuture(bundle);
                }
            });
        }

        public /* synthetic */ void lambda$createCallback$0$RoleManagerShellCommand$CallbackFuture(Bundle result) {
            if (result != null) {
                complete((Object) null);
            } else {
                completeExceptionally(new RuntimeException("Failed"));
            }
        }

        public int waitForResult() {
            try {
                get(5, TimeUnit.SECONDS);
                return 0;
            } catch (Exception e) {
                PrintWriter errPrintWriter = RoleManagerShellCommand.this.getErrPrintWriter();
                errPrintWriter.println("Error: see logcat for details.\n" + Log.getStackTraceString(e));
                return -1;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0044 A[Catch:{ RemoteException -> 0x005c }] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0057 A[Catch:{ RemoteException -> 0x005c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r7) {
        /*
            r6 = this;
            if (r7 != 0) goto L_0x0007
            int r0 = r6.handleDefaultCommands(r7)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r6.getOutPrintWriter()
            r1 = -1
            int r2 = r7.hashCode()     // Catch:{ RemoteException -> 0x005c }
            r3 = -1831663689(0xffffffff92d307b7, float:-1.3317874E-27)
            r4 = 2
            r5 = 1
            if (r2 == r3) goto L_0x0037
            r3 = -1502066320(0xffffffffa6784970, float:-8.614181E-16)
            if (r2 == r3) goto L_0x002d
            r3 = -1274754278(0xffffffffb404cb1a, float:-1.2367346E-7)
            if (r2 == r3) goto L_0x0022
        L_0x0021:
            goto L_0x0041
        L_0x0022:
            java.lang.String r2 = "remove-role-holder"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x005c }
            if (r2 == 0) goto L_0x0021
            r2 = r5
            goto L_0x0042
        L_0x002d:
            java.lang.String r2 = "clear-role-holders"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x005c }
            if (r2 == 0) goto L_0x0021
            r2 = r4
            goto L_0x0042
        L_0x0037:
            java.lang.String r2 = "add-role-holder"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x005c }
            if (r2 == 0) goto L_0x0021
            r2 = 0
            goto L_0x0042
        L_0x0041:
            r2 = r1
        L_0x0042:
            if (r2 == 0) goto L_0x0057
            if (r2 == r5) goto L_0x0052
            if (r2 == r4) goto L_0x004d
            int r1 = r6.handleDefaultCommands(r7)     // Catch:{ RemoteException -> 0x005c }
            return r1
        L_0x004d:
            int r1 = r6.runClearRoleHolders()     // Catch:{ RemoteException -> 0x005c }
            return r1
        L_0x0052:
            int r1 = r6.runRemoveRoleHolder()     // Catch:{ RemoteException -> 0x005c }
            return r1
        L_0x0057:
            int r1 = r6.runAddRoleHolder()     // Catch:{ RemoteException -> 0x005c }
            return r1
        L_0x005c:
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.RoleManagerShellCommand.onCommand(java.lang.String):int");
    }

    private int getUserIdMaybe() {
        String option = getNextOption();
        if (option == null || !option.equals("--user")) {
            return 0;
        }
        return UserHandle.parseUserArg(getNextArgRequired());
    }

    private int getFlagsMaybe() {
        String flags = getNextArg();
        if (flags == null) {
            return 0;
        }
        return Integer.parseInt(flags);
    }

    private int runAddRoleHolder() throws RemoteException {
        int userId = getUserIdMaybe();
        String roleName = getNextArgRequired();
        String packageName = getNextArgRequired();
        int flags = getFlagsMaybe();
        CallbackFuture future = new CallbackFuture();
        this.mRoleManager.addRoleHolderAsUser(roleName, packageName, flags, userId, future.createCallback());
        return future.waitForResult();
    }

    private int runRemoveRoleHolder() throws RemoteException {
        int userId = getUserIdMaybe();
        String roleName = getNextArgRequired();
        String packageName = getNextArgRequired();
        int flags = getFlagsMaybe();
        CallbackFuture future = new CallbackFuture();
        this.mRoleManager.removeRoleHolderAsUser(roleName, packageName, flags, userId, future.createCallback());
        return future.waitForResult();
    }

    private int runClearRoleHolders() throws RemoteException {
        int userId = getUserIdMaybe();
        String roleName = getNextArgRequired();
        int flags = getFlagsMaybe();
        CallbackFuture future = new CallbackFuture();
        this.mRoleManager.clearRoleHoldersAsUser(roleName, flags, userId, future.createCallback());
        return future.waitForResult();
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Role manager (role) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println();
        pw.println("  add-role-holder [--user USER_ID] ROLE PACKAGE [FLAGS]");
        pw.println("  remove-role-holder [--user USER_ID] ROLE PACKAGE [FLAGS]");
        pw.println("  clear-role-holders [--user USER_ID] ROLE [FLAGS]");
        pw.println();
    }
}
