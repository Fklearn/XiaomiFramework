package com.android.server.job;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.ShellCommand;
import java.io.PrintWriter;

public final class JobSchedulerShellCommand extends ShellCommand {
    public static final int CMD_ERR_CONSTRAINTS = -1002;
    public static final int CMD_ERR_NO_JOB = -1001;
    public static final int CMD_ERR_NO_PACKAGE = -1000;
    JobSchedulerService mInternal;
    IPackageManager mPM = AppGlobals.getPackageManager();

    JobSchedulerShellCommand(JobSchedulerService service) {
        this.mInternal = service;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r6) {
        /*
            r5 = this;
            java.io.PrintWriter r0 = r5.getOutPrintWriter()
            r1 = -1
            if (r6 == 0) goto L_0x0009
            r2 = r6
            goto L_0x000b
        L_0x0009:
            java.lang.String r2 = ""
        L_0x000b:
            int r3 = r2.hashCode()     // Catch:{ Exception -> 0x00dd }
            switch(r3) {
                case -1894245460: goto L_0x008b;
                case -1845752298: goto L_0x0081;
                case -1687551032: goto L_0x0077;
                case -1367724422: goto L_0x006d;
                case -1313911455: goto L_0x0062;
                case 113291: goto L_0x0057;
                case 55361425: goto L_0x004c;
                case 200896764: goto L_0x0040;
                case 703160488: goto L_0x0036;
                case 1749711139: goto L_0x002c;
                case 1791471818: goto L_0x0020;
                case 1854493850: goto L_0x0014;
                default: goto L_0x0012;
            }     // Catch:{ Exception -> 0x00dd }
        L_0x0012:
            goto L_0x0097
        L_0x0014:
            java.lang.String r3 = "monitor-battery"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 3
            goto L_0x0098
        L_0x0020:
            java.lang.String r3 = "get-job-state"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 9
            goto L_0x0098
        L_0x002c:
            java.lang.String r3 = "get-battery-not-low"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 6
            goto L_0x0098
        L_0x0036:
            java.lang.String r3 = "get-battery-seq"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 4
            goto L_0x0098
        L_0x0040:
            java.lang.String r3 = "heartbeat"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 10
            goto L_0x0098
        L_0x004c:
            java.lang.String r3 = "get-storage-not-low"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 8
            goto L_0x0098
        L_0x0057:
            java.lang.String r3 = "run"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 0
            goto L_0x0098
        L_0x0062:
            java.lang.String r3 = "timeout"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0098
        L_0x006d:
            java.lang.String r3 = "cancel"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 2
            goto L_0x0098
        L_0x0077:
            java.lang.String r3 = "get-battery-charging"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 5
            goto L_0x0098
        L_0x0081:
            java.lang.String r3 = "get-storage-seq"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 7
            goto L_0x0098
        L_0x008b:
            java.lang.String r3 = "trigger-dock-state"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x00dd }
            if (r2 == 0) goto L_0x0012
            r2 = 11
            goto L_0x0098
        L_0x0097:
            r2 = r1
        L_0x0098:
            switch(r2) {
                case 0: goto L_0x00d7;
                case 1: goto L_0x00d2;
                case 2: goto L_0x00cd;
                case 3: goto L_0x00c8;
                case 4: goto L_0x00c3;
                case 5: goto L_0x00be;
                case 6: goto L_0x00b9;
                case 7: goto L_0x00b4;
                case 8: goto L_0x00af;
                case 9: goto L_0x00aa;
                case 10: goto L_0x00a5;
                case 11: goto L_0x00a0;
                default: goto L_0x009b;
            }     // Catch:{ Exception -> 0x00dd }
        L_0x009b:
            int r1 = r5.handleDefaultCommands(r6)     // Catch:{ Exception -> 0x00dd }
            goto L_0x00dc
        L_0x00a0:
            int r1 = r5.triggerDockState(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00a5:
            int r1 = r5.doHeartbeat(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00aa:
            int r1 = r5.getJobState(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00af:
            int r1 = r5.getStorageNotLow(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00b4:
            int r1 = r5.getStorageSeq(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00b9:
            int r1 = r5.getBatteryNotLow(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00be:
            int r1 = r5.getBatteryCharging(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00c3:
            int r1 = r5.getBatterySeq(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00c8:
            int r1 = r5.monitorBattery(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00cd:
            int r1 = r5.cancelJob(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00d2:
            int r1 = r5.timeout(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00d7:
            int r1 = r5.runJob(r0)     // Catch:{ Exception -> 0x00dd }
            return r1
        L_0x00dc:
            return r1
        L_0x00dd:
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerShellCommand.onCommand(java.lang.String):int");
    }

    private void checkPermission(String operation) throws Exception {
        int uid = Binder.getCallingUid();
        if (uid != 0 && this.mPM.checkUidPermission("android.permission.CHANGE_APP_IDLE_STATE", uid) != 0) {
            throw new SecurityException("Uid " + uid + " not permitted to " + operation);
        }
    }

    private boolean printError(int errCode, String pkgName, int userId, int jobId) {
        switch (errCode) {
            case CMD_ERR_CONSTRAINTS /*-1002*/:
                PrintWriter pw = getErrPrintWriter();
                pw.print("Job ");
                pw.print(jobId);
                pw.print(" in package ");
                pw.print(pkgName);
                pw.print(" / user ");
                pw.print(userId);
                pw.println(" has functional constraints but --force not specified");
                return true;
            case -1001:
                PrintWriter pw2 = getErrPrintWriter();
                pw2.print("Could not find job ");
                pw2.print(jobId);
                pw2.print(" in package ");
                pw2.print(pkgName);
                pw2.print(" / user ");
                pw2.println(userId);
                return true;
            case CMD_ERR_NO_PACKAGE /*-1000*/:
                PrintWriter pw3 = getErrPrintWriter();
                pw3.print("Package not found: ");
                pw3.print(pkgName);
                pw3.print(" / user ");
                pw3.println(userId);
                return true;
            default:
                return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0054 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int runJob(java.io.PrintWriter r10) throws java.lang.Exception {
        /*
            r9 = this;
            java.lang.String r0 = "force scheduled jobs"
            r9.checkPermission(r0)
            r0 = 0
            r1 = 0
        L_0x0007:
            java.lang.String r2 = r9.getNextOption()
            r3 = r2
            if (r2 == 0) goto L_0x0080
            int r2 = r3.hashCode()
            r4 = -1626076853(0xffffffff9f14094b, float:-3.1347906E-20)
            r5 = 3
            r6 = 2
            r7 = 1
            r8 = -1
            if (r2 == r4) goto L_0x0047
            r4 = 1497(0x5d9, float:2.098E-42)
            if (r2 == r4) goto L_0x003d
            r4 = 1512(0x5e8, float:2.119E-42)
            if (r2 == r4) goto L_0x0033
            r4 = 1333469547(0x4f7b216b, float:4.2132713E9)
            if (r2 == r4) goto L_0x0029
        L_0x0028:
            goto L_0x0051
        L_0x0029:
            java.lang.String r2 = "--user"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = r5
            goto L_0x0052
        L_0x0033:
            java.lang.String r2 = "-u"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = r6
            goto L_0x0052
        L_0x003d:
            java.lang.String r2 = "-f"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 0
            goto L_0x0052
        L_0x0047:
            java.lang.String r2 = "--force"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = r7
            goto L_0x0052
        L_0x0051:
            r2 = r8
        L_0x0052:
            if (r2 == 0) goto L_0x007d
            if (r2 == r7) goto L_0x007d
            if (r2 == r6) goto L_0x0074
            if (r2 == r5) goto L_0x0074
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Error: unknown option '"
            r2.append(r4)
            r2.append(r3)
            java.lang.String r4 = "'"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r10.println(r2)
            return r8
        L_0x0074:
            java.lang.String r2 = r9.getNextArgRequired()
            int r1 = java.lang.Integer.parseInt(r2)
            goto L_0x007f
        L_0x007d:
            r0 = 1
        L_0x007f:
            goto L_0x0007
        L_0x0080:
            java.lang.String r2 = r9.getNextArgRequired()
            java.lang.String r4 = r9.getNextArgRequired()
            int r4 = java.lang.Integer.parseInt(r4)
            long r5 = android.os.Binder.clearCallingIdentity()
            com.android.server.job.JobSchedulerService r7 = r9.mInternal     // Catch:{ all -> 0x00b5 }
            int r7 = r7.executeRunCommand(r2, r1, r4, r0)     // Catch:{ all -> 0x00b5 }
            boolean r8 = r9.printError(r7, r2, r1, r4)     // Catch:{ all -> 0x00b5 }
            if (r8 == 0) goto L_0x00a1
            android.os.Binder.restoreCallingIdentity(r5)
            return r7
        L_0x00a1:
            java.lang.String r8 = "Running job"
            r10.print(r8)     // Catch:{ all -> 0x00b5 }
            if (r0 == 0) goto L_0x00ad
            java.lang.String r8 = " [FORCED]"
            r10.print(r8)     // Catch:{ all -> 0x00b5 }
        L_0x00ad:
            r10.println()     // Catch:{ all -> 0x00b5 }
            android.os.Binder.restoreCallingIdentity(r5)
            return r7
        L_0x00b5:
            r7 = move-exception
            android.os.Binder.restoreCallingIdentity(r5)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerShellCommand.runJob(java.io.PrintWriter):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r3.equals("-u") != false) goto L_0x0034;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int timeout(java.io.PrintWriter r19) throws java.lang.Exception {
        /*
            r18 = this;
            r1 = r18
            java.lang.String r0 = "force timeout jobs"
            r1.checkPermission(r0)
            r0 = -1
        L_0x0008:
            java.lang.String r2 = r18.getNextOption()
            r3 = r2
            r4 = 0
            r5 = 1
            r6 = -1
            if (r2 == 0) goto L_0x0060
            int r2 = r3.hashCode()
            r7 = 1512(0x5e8, float:2.119E-42)
            if (r2 == r7) goto L_0x002a
            r4 = 1333469547(0x4f7b216b, float:4.2132713E9)
            if (r2 == r4) goto L_0x0020
        L_0x001f:
            goto L_0x0033
        L_0x0020:
            java.lang.String r2 = "--user"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x001f
            r4 = r5
            goto L_0x0034
        L_0x002a:
            java.lang.String r2 = "-u"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x001f
            goto L_0x0034
        L_0x0033:
            r4 = r6
        L_0x0034:
            if (r4 == 0) goto L_0x0054
            if (r4 == r5) goto L_0x0054
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Error: unknown option '"
            r2.append(r4)
            r2.append(r3)
            java.lang.String r4 = "'"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r13 = r19
            r13.println(r2)
            return r6
        L_0x0054:
            r13 = r19
            java.lang.String r2 = r18.getNextArgRequired()
            int r0 = android.os.UserHandle.parseUserArg(r2)
            goto L_0x0008
        L_0x0060:
            r13 = r19
            r2 = -2
            if (r0 != r2) goto L_0x006b
            int r0 = android.app.ActivityManager.getCurrentUser()
            r2 = r0
            goto L_0x006c
        L_0x006b:
            r2 = r0
        L_0x006c:
            java.lang.String r14 = r18.getNextArg()
            java.lang.String r15 = r18.getNextArg()
            if (r15 == 0) goto L_0x007a
            int r6 = java.lang.Integer.parseInt(r15)
        L_0x007a:
            r12 = r6
            long r16 = android.os.Binder.clearCallingIdentity()
            com.android.server.job.JobSchedulerService r7 = r1.mInternal     // Catch:{ all -> 0x0092 }
            if (r15 == 0) goto L_0x0085
            r11 = r5
            goto L_0x0086
        L_0x0085:
            r11 = r4
        L_0x0086:
            r8 = r19
            r9 = r14
            r10 = r2
            int r0 = r7.executeTimeoutCommand(r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0092 }
            android.os.Binder.restoreCallingIdentity(r16)
            return r0
        L_0x0092:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerShellCommand.timeout(java.io.PrintWriter):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002e, code lost:
        if (r7.equals("-u") != false) goto L_0x0032;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int cancelJob(java.io.PrintWriter r13) throws java.lang.Exception {
        /*
            r12 = this;
            java.lang.String r0 = "cancel jobs"
            r12.checkPermission(r0)
            r0 = 0
        L_0x0006:
            java.lang.String r1 = r12.getNextOption()
            r7 = r1
            r2 = 0
            r3 = 1
            r4 = -1
            if (r1 == 0) goto L_0x005a
            int r1 = r7.hashCode()
            r5 = 1512(0x5e8, float:2.119E-42)
            if (r1 == r5) goto L_0x0028
            r2 = 1333469547(0x4f7b216b, float:4.2132713E9)
            if (r1 == r2) goto L_0x001e
        L_0x001d:
            goto L_0x0031
        L_0x001e:
            java.lang.String r1 = "--user"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x001d
            r2 = r3
            goto L_0x0032
        L_0x0028:
            java.lang.String r1 = "-u"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x001d
            goto L_0x0032
        L_0x0031:
            r2 = r4
        L_0x0032:
            if (r2 == 0) goto L_0x0050
            if (r2 == r3) goto L_0x0050
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error: unknown option '"
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = "'"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r13.println(r1)
            return r4
        L_0x0050:
            java.lang.String r1 = r12.getNextArgRequired()
            int r0 = android.os.UserHandle.parseUserArg(r1)
            goto L_0x0006
        L_0x005a:
            if (r0 >= 0) goto L_0x0062
            java.lang.String r1 = "Error: must specify a concrete user ID"
            r13.println(r1)
            return r4
        L_0x0062:
            java.lang.String r8 = r12.getNextArg()
            java.lang.String r9 = r12.getNextArg()
            if (r9 == 0) goto L_0x0072
            int r1 = java.lang.Integer.parseInt(r9)
            r6 = r1
            goto L_0x0073
        L_0x0072:
            r6 = r4
        L_0x0073:
            long r10 = android.os.Binder.clearCallingIdentity()
            com.android.server.job.JobSchedulerService r1 = r12.mInternal     // Catch:{ all -> 0x0089 }
            if (r9 == 0) goto L_0x007d
            r5 = r3
            goto L_0x007e
        L_0x007d:
            r5 = r2
        L_0x007e:
            r2 = r13
            r3 = r8
            r4 = r0
            int r1 = r1.executeCancelCommand(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0089 }
            android.os.Binder.restoreCallingIdentity(r10)
            return r1
        L_0x0089:
            r1 = move-exception
            android.os.Binder.restoreCallingIdentity(r10)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerShellCommand.cancelJob(java.io.PrintWriter):int");
    }

    /* JADX INFO: finally extract failed */
    private int monitorBattery(PrintWriter pw) throws Exception {
        boolean enabled;
        checkPermission("change battery monitoring");
        String opt = getNextArgRequired();
        if ("on".equals(opt)) {
            enabled = true;
        } else if ("off".equals(opt)) {
            enabled = false;
        } else {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: unknown option " + opt);
            return 1;
        }
        long ident = Binder.clearCallingIdentity();
        try {
            this.mInternal.setMonitorBattery(enabled);
            if (enabled) {
                pw.println("Battery monitoring enabled");
            } else {
                pw.println("Battery monitoring disabled");
            }
            Binder.restoreCallingIdentity(ident);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    private int getBatterySeq(PrintWriter pw) {
        pw.println(this.mInternal.getBatterySeq());
        return 0;
    }

    private int getBatteryCharging(PrintWriter pw) {
        pw.println(this.mInternal.getBatteryCharging());
        return 0;
    }

    private int getBatteryNotLow(PrintWriter pw) {
        pw.println(this.mInternal.getBatteryNotLow());
        return 0;
    }

    private int getStorageSeq(PrintWriter pw) {
        pw.println(this.mInternal.getStorageSeq());
        return 0;
    }

    private int getStorageNotLow(PrintWriter pw) {
        pw.println(this.mInternal.getStorageNotLow());
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0034 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0050 A[ADDED_TO_REGION, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getJobState(java.io.PrintWriter r9) throws java.lang.Exception {
        /*
            r8 = this;
            java.lang.String r0 = "force timeout jobs"
            r8.checkPermission(r0)
            r0 = 0
        L_0x0006:
            java.lang.String r1 = r8.getNextOption()
            r2 = r1
            if (r1 == 0) goto L_0x005a
            int r1 = r2.hashCode()
            r3 = 1512(0x5e8, float:2.119E-42)
            r4 = 1
            r5 = -1
            if (r1 == r3) goto L_0x0027
            r3 = 1333469547(0x4f7b216b, float:4.2132713E9)
            if (r1 == r3) goto L_0x001d
        L_0x001c:
            goto L_0x0031
        L_0x001d:
            java.lang.String r1 = "--user"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x001c
            r1 = r4
            goto L_0x0032
        L_0x0027:
            java.lang.String r1 = "-u"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x001c
            r1 = 0
            goto L_0x0032
        L_0x0031:
            r1 = r5
        L_0x0032:
            if (r1 == 0) goto L_0x0050
            if (r1 == r4) goto L_0x0050
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Error: unknown option '"
            r1.append(r3)
            r1.append(r2)
            java.lang.String r3 = "'"
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            r9.println(r1)
            return r5
        L_0x0050:
            java.lang.String r1 = r8.getNextArgRequired()
            int r0 = android.os.UserHandle.parseUserArg(r1)
            goto L_0x0006
        L_0x005a:
            r1 = -2
            if (r0 != r1) goto L_0x0061
            int r0 = android.app.ActivityManager.getCurrentUser()
        L_0x0061:
            java.lang.String r1 = r8.getNextArgRequired()
            java.lang.String r3 = r8.getNextArgRequired()
            int r4 = java.lang.Integer.parseInt(r3)
            long r5 = android.os.Binder.clearCallingIdentity()
            com.android.server.job.JobSchedulerService r7 = r8.mInternal     // Catch:{ all -> 0x007f }
            int r7 = r7.getJobState(r9, r1, r0, r4)     // Catch:{ all -> 0x007f }
            r8.printError(r7, r1, r0, r4)     // Catch:{ all -> 0x007f }
            android.os.Binder.restoreCallingIdentity(r5)
            return r7
        L_0x007f:
            r7 = move-exception
            android.os.Binder.restoreCallingIdentity(r5)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerShellCommand.getJobState(java.io.PrintWriter):int");
    }

    private int doHeartbeat(PrintWriter pw) throws Exception {
        checkPermission("manipulate scheduler heartbeat");
        String arg = getNextArg();
        int numBeats = arg != null ? Integer.parseInt(arg) : 0;
        long ident = Binder.clearCallingIdentity();
        try {
            return this.mInternal.executeHeartbeatCommand(pw, numBeats);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* JADX INFO: finally extract failed */
    private int triggerDockState(PrintWriter pw) throws Exception {
        boolean idleState;
        checkPermission("trigger wireless charging dock state");
        String opt = getNextArgRequired();
        if ("idle".equals(opt)) {
            idleState = true;
        } else if ("active".equals(opt)) {
            idleState = false;
        } else {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: unknown option " + opt);
            return 1;
        }
        long ident = Binder.clearCallingIdentity();
        try {
            this.mInternal.triggerDockState(idleState);
            Binder.restoreCallingIdentity(ident);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Job scheduler (jobscheduler) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  run [-f | --force] [-u | --user USER_ID] PACKAGE JOB_ID");
        pw.println("    Trigger immediate execution of a specific scheduled job.");
        pw.println("    Options:");
        pw.println("      -f or --force: run the job even if technical constraints such as");
        pw.println("         connectivity are not currently met");
        pw.println("      -u or --user: specify which user's job is to be run; the default is");
        pw.println("         the primary or system user");
        pw.println("  timeout [-u | --user USER_ID] [PACKAGE] [JOB_ID]");
        pw.println("    Trigger immediate timeout of currently executing jobs, as if their.");
        pw.println("    execution timeout had expired.");
        pw.println("    Options:");
        pw.println("      -u or --user: specify which user's job is to be run; the default is");
        pw.println("         all users");
        pw.println("  cancel [-u | --user USER_ID] PACKAGE [JOB_ID]");
        pw.println("    Cancel a scheduled job.  If a job ID is not supplied, all jobs scheduled");
        pw.println("    by that package will be canceled.  USE WITH CAUTION.");
        pw.println("    Options:");
        pw.println("      -u or --user: specify which user's job is to be run; the default is");
        pw.println("         the primary or system user");
        pw.println("  heartbeat [num]");
        pw.println("    With no argument, prints the current standby heartbeat.  With a positive");
        pw.println("    argument, advances the standby heartbeat by that number.");
        pw.println("  monitor-battery [on|off]");
        pw.println("    Control monitoring of all battery changes.  Off by default.  Turning");
        pw.println("    on makes get-battery-seq useful.");
        pw.println("  get-battery-seq");
        pw.println("    Return the last battery update sequence number that was received.");
        pw.println("  get-battery-charging");
        pw.println("    Return whether the battery is currently considered to be charging.");
        pw.println("  get-battery-not-low");
        pw.println("    Return whether the battery is currently considered to not be low.");
        pw.println("  get-storage-seq");
        pw.println("    Return the last storage update sequence number that was received.");
        pw.println("  get-storage-not-low");
        pw.println("    Return whether storage is currently considered to not be low.");
        pw.println("  get-job-state [-u | --user USER_ID] PACKAGE JOB_ID");
        pw.println("    Return the current state of a job, may be any combination of:");
        pw.println("      pending: currently on the pending list, waiting to be active");
        pw.println("      active: job is actively running");
        pw.println("      user-stopped: job can't run because its user is stopped");
        pw.println("      backing-up: job can't run because app is currently backing up its data");
        pw.println("      no-component: job can't run because its component is not available");
        pw.println("      ready: job is ready to run (all constraints satisfied or bypassed)");
        pw.println("      waiting: if nothing else above is printed, job not ready to run");
        pw.println("    Options:");
        pw.println("      -u or --user: specify which user's job is to be run; the default is");
        pw.println("         the primary or system user");
        pw.println("  trigger-dock-state [idle|active]");
        pw.println("    Trigger wireless charging dock state.  Active by default.");
        pw.println();
    }
}
