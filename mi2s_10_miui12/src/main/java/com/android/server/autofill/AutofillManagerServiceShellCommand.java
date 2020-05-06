package com.android.server.autofill;

import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.service.autofill.AutofillFieldClassificationService;
import com.android.internal.os.IResultReceiver;
import com.android.server.BatteryService;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class AutofillManagerServiceShellCommand extends ShellCommand {
    private final AutofillManagerService mService;

    public AutofillManagerServiceShellCommand(AutofillManagerService service) {
        this.mService = service;
    }

    public int onCommand(String cmd) {
        if (cmd == null) {
            return handleDefaultCommands(cmd);
        }
        PrintWriter pw = getOutPrintWriter();
        char c = 65535;
        switch (cmd.hashCode()) {
            case 102230:
                if (cmd.equals("get")) {
                    c = 3;
                    break;
                }
                break;
            case 113762:
                if (cmd.equals("set")) {
                    c = 4;
                    break;
                }
                break;
            case 3322014:
                if (cmd.equals("list")) {
                    c = 0;
                    break;
                }
                break;
            case 108404047:
                if (cmd.equals("reset")) {
                    c = 2;
                    break;
                }
                break;
            case 1557372922:
                if (cmd.equals("destroy")) {
                    c = 1;
                    break;
                }
                break;
        }
        if (c == 0) {
            return requestList(pw);
        }
        if (c == 1) {
            return requestDestroy(pw);
        }
        if (c == 2) {
            return requestReset();
        }
        if (c == 3) {
            return requestGet(pw);
        }
        if (c != 4) {
            return handleDefaultCommands(cmd);
        }
        return requestSet(pw);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0107, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0108, code lost:
        r0.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x010b, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0100, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0101, code lost:
        if (r1 != null) goto L_0x0103;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHelp() {
        /*
            r4 = this;
            java.lang.String r0 = ""
            java.io.PrintWriter r1 = r4.getOutPrintWriter()
            java.lang.String r2 = "AutoFill Service (autofill) commands:"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  help"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Prints this help text."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get log_level "
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets the Autofill log level (off | debug | verbose)."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get max_partitions"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets the maximum number of partitions per session."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get max_visible_datasets"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets the maximum number of visible datasets in the UI."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get full_screen_mode"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets the Fill UI full screen mode"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get fc_score [--algorithm ALGORITHM] value1 value2"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets the field classification score for 2 fields."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get bind-instant-service-allowed"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Gets whether binding to services provided by instant apps is allowed"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set log_level [off | debug | verbose]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Sets the Autofill log level."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set max_partitions number"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Sets the maximum number of partitions per session."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set max_visible_datasets number"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Sets the maximum number of visible datasets in the UI."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set full_screen_mode [true | false | default]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Sets the Fill UI full screen mode"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set bind-instant-service-allowed [true | false]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Sets whether binding to services provided by instant apps is allowed"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set temporary-augmented-service USER_ID [COMPONENT_NAME DURATION]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Temporarily (for DURATION ms) changes the augmented autofill service implementation."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    To reset, call with just the USER_ID argument."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  set default-augmented-service-enabled USER_ID [true|false]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Enable / disable the default augmented autofill service for the user."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  get default-augmented-service-enabled USER_ID"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Checks whether the default augmented autofill service is enabled for the user."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  list sessions [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Lists all pending sessions."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  destroy sessions [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Destroys all pending sessions."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "  reset"
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = "    Resets all pending sessions and cached service connections."
            r1.println(r2)     // Catch:{ all -> 0x00fe }
            r1.println(r0)     // Catch:{ all -> 0x00fe }
            r1.close()
            return
        L_0x00fe:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0100 }
        L_0x0100:
            r2 = move-exception
            if (r1 == 0) goto L_0x010b
            r1.close()     // Catch:{ all -> 0x0107 }
            goto L_0x010b
        L_0x0107:
            r3 = move-exception
            r0.addSuppressed(r3)
        L_0x010b:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.onHelp():void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int requestGet(java.io.PrintWriter r5) {
        /*
            r4 = this;
            java.lang.String r0 = r4.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = -1
            switch(r1) {
                case -2124387184: goto L_0x004c;
                case -2006901047: goto L_0x0041;
                case -1298810906: goto L_0x0037;
                case 809633044: goto L_0x002d;
                case 852405952: goto L_0x0023;
                case 1393110435: goto L_0x0018;
                case 1772188804: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0056
        L_0x000d:
            java.lang.String r1 = "max_partitions"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 1
            goto L_0x0057
        L_0x0018:
            java.lang.String r1 = "max_visible_datasets"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 2
            goto L_0x0057
        L_0x0023:
            java.lang.String r1 = "default-augmented-service-enabled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 6
            goto L_0x0057
        L_0x002d:
            java.lang.String r1 = "bind-instant-service-allowed"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 5
            goto L_0x0057
        L_0x0037:
            java.lang.String r1 = "full_screen_mode"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 4
            goto L_0x0057
        L_0x0041:
            java.lang.String r1 = "log_level"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 0
            goto L_0x0057
        L_0x004c:
            java.lang.String r1 = "fc_score"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 3
            goto L_0x0057
        L_0x0056:
            r1 = r2
        L_0x0057:
            switch(r1) {
                case 0: goto L_0x008d;
                case 1: goto L_0x0088;
                case 2: goto L_0x0083;
                case 3: goto L_0x007e;
                case 4: goto L_0x0079;
                case 5: goto L_0x0074;
                case 6: goto L_0x006f;
                default: goto L_0x005a;
            }
        L_0x005a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Invalid set: "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r5.println(r1)
            return r2
        L_0x006f:
            int r1 = r4.getDefaultAugmentedServiceEnabled(r5)
            return r1
        L_0x0074:
            int r1 = r4.getBindInstantService(r5)
            return r1
        L_0x0079:
            int r1 = r4.getFullScreenMode(r5)
            return r1
        L_0x007e:
            int r1 = r4.getFieldClassificationScore(r5)
            return r1
        L_0x0083:
            int r1 = r4.getMaxVisibileDatasets(r5)
            return r1
        L_0x0088:
            int r1 = r4.getMaxPartitions(r5)
            return r1
        L_0x008d:
            int r1 = r4.getLogLevel(r5)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.requestGet(java.io.PrintWriter):int");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int requestSet(java.io.PrintWriter r5) {
        /*
            r4 = this;
            java.lang.String r0 = r4.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = -1
            switch(r1) {
                case -2006901047: goto L_0x004c;
                case -1298810906: goto L_0x0042;
                case -571600804: goto L_0x0037;
                case 809633044: goto L_0x002d;
                case 852405952: goto L_0x0023;
                case 1393110435: goto L_0x0018;
                case 1772188804: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0057
        L_0x000d:
            java.lang.String r1 = "max_partitions"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 1
            goto L_0x0058
        L_0x0018:
            java.lang.String r1 = "max_visible_datasets"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 2
            goto L_0x0058
        L_0x0023:
            java.lang.String r1 = "default-augmented-service-enabled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 6
            goto L_0x0058
        L_0x002d:
            java.lang.String r1 = "bind-instant-service-allowed"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 4
            goto L_0x0058
        L_0x0037:
            java.lang.String r1 = "temporary-augmented-service"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 5
            goto L_0x0058
        L_0x0042:
            java.lang.String r1 = "full_screen_mode"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 3
            goto L_0x0058
        L_0x004c:
            java.lang.String r1 = "log_level"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 0
            goto L_0x0058
        L_0x0057:
            r1 = r2
        L_0x0058:
            switch(r1) {
                case 0: goto L_0x008e;
                case 1: goto L_0x0089;
                case 2: goto L_0x0084;
                case 3: goto L_0x007f;
                case 4: goto L_0x007a;
                case 5: goto L_0x0075;
                case 6: goto L_0x0070;
                default: goto L_0x005b;
            }
        L_0x005b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Invalid set: "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r5.println(r1)
            return r2
        L_0x0070:
            int r1 = r4.setDefaultAugmentedServiceEnabled(r5)
            return r1
        L_0x0075:
            int r1 = r4.setTemporaryAugmentedService(r5)
            return r1
        L_0x007a:
            int r1 = r4.setBindInstantService(r5)
            return r1
        L_0x007f:
            int r1 = r4.setFullScreenMode(r5)
            return r1
        L_0x0084:
            int r1 = r4.setMaxVisibileDatasets()
            return r1
        L_0x0089:
            int r1 = r4.setMaxPartitions()
            return r1
        L_0x008e:
            int r1 = r4.setLogLevel(r5)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.requestSet(java.io.PrintWriter):int");
    }

    private int getLogLevel(PrintWriter pw) {
        int logLevel = this.mService.getLogLevel();
        if (logLevel == 0) {
            pw.println("off");
            return 0;
        } else if (logLevel == 2) {
            pw.println("debug");
            return 0;
        } else if (logLevel != 4) {
            pw.println("unknow (" + logLevel + ")");
            return 0;
        } else {
            pw.println("verbose");
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0068  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int setLogLevel(java.io.PrintWriter r9) {
        /*
            r8 = this;
            java.lang.String r0 = r8.getNextArgRequired()
            java.lang.String r1 = r0.toLowerCase()
            int r2 = r1.hashCode()
            r3 = 109935(0x1ad6f, float:1.54052E-40)
            r4 = 1
            r5 = -1
            r6 = 2
            r7 = 0
            if (r2 == r3) goto L_0x0035
            r3 = 95458899(0x5b09653, float:1.6606181E-35)
            if (r2 == r3) goto L_0x002b
            r3 = 351107458(0x14ed7982, float:2.3978811E-26)
            if (r2 == r3) goto L_0x0020
        L_0x001f:
            goto L_0x0040
        L_0x0020:
            java.lang.String r2 = "verbose"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r7
            goto L_0x0041
        L_0x002b:
            java.lang.String r2 = "debug"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r4
            goto L_0x0041
        L_0x0035:
            java.lang.String r2 = "off"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r6
            goto L_0x0041
        L_0x0040:
            r1 = r5
        L_0x0041:
            if (r1 == 0) goto L_0x0068
            if (r1 == r4) goto L_0x0062
            if (r1 == r6) goto L_0x005c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid level: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r9.println(r1)
            return r5
        L_0x005c:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            r1.setLogLevel(r7)
            return r7
        L_0x0062:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            r1.setLogLevel(r6)
            return r7
        L_0x0068:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            r2 = 4
            r1.setLogLevel(r2)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.setLogLevel(java.io.PrintWriter):int");
    }

    private int getMaxPartitions(PrintWriter pw) {
        pw.println(this.mService.getMaxPartitions());
        return 0;
    }

    private int setMaxPartitions() {
        this.mService.setMaxPartitions(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    private int getMaxVisibileDatasets(PrintWriter pw) {
        pw.println(this.mService.getMaxVisibleDatasets());
        return 0;
    }

    private int setMaxVisibileDatasets() {
        this.mService.setMaxVisibleDatasets(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    private int getFieldClassificationScore(PrintWriter pw) {
        String value1;
        String algorithm;
        String nextArg = getNextArgRequired();
        if ("--algorithm".equals(nextArg)) {
            algorithm = getNextArgRequired();
            value1 = getNextArgRequired();
        } else {
            algorithm = null;
            value1 = nextArg;
        }
        String value2 = getNextArgRequired();
        CountDownLatch latch = new CountDownLatch(1);
        this.mService.calculateScore(algorithm, value1, value2, new RemoteCallback(new RemoteCallback.OnResultListener(pw, latch) {
            private final /* synthetic */ PrintWriter f$0;
            private final /* synthetic */ CountDownLatch f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onResult(Bundle bundle) {
                AutofillManagerServiceShellCommand.lambda$getFieldClassificationScore$0(this.f$0, this.f$1, bundle);
            }
        }));
        return waitForLatch(pw, latch);
    }

    static /* synthetic */ void lambda$getFieldClassificationScore$0(PrintWriter pw, CountDownLatch latch, Bundle result) {
        AutofillFieldClassificationService.Scores scores = result.getParcelable("scores");
        if (scores == null) {
            pw.println("no score");
        } else {
            pw.println(scores.scores[0][0]);
        }
        latch.countDown();
    }

    private int getFullScreenMode(PrintWriter pw) {
        Boolean mode = this.mService.getFullScreenMode();
        if (mode == null) {
            pw.println(BatteryService.HealthServiceWrapper.INSTANCE_VENDOR);
            return 0;
        } else if (mode.booleanValue()) {
            pw.println("true");
            return 0;
        } else {
            pw.println("false");
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x006a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int setFullScreenMode(java.io.PrintWriter r9) {
        /*
            r8 = this;
            java.lang.String r0 = r8.getNextArgRequired()
            java.lang.String r1 = r0.toLowerCase()
            int r2 = r1.hashCode()
            r3 = 3569038(0x36758e, float:5.001287E-39)
            r4 = 2
            r5 = 1
            r6 = -1
            r7 = 0
            if (r2 == r3) goto L_0x0034
            r3 = 97196323(0x5cb1923, float:1.9099262E-35)
            if (r2 == r3) goto L_0x002a
            r3 = 1544803905(0x5c13d641, float:1.66449585E17)
            if (r2 == r3) goto L_0x0020
        L_0x001f:
            goto L_0x003f
        L_0x0020:
            java.lang.String r2 = "default"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r4
            goto L_0x0040
        L_0x002a:
            java.lang.String r2 = "false"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r5
            goto L_0x0040
        L_0x0034:
            java.lang.String r2 = "true"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x001f
            r1 = r7
            goto L_0x0040
        L_0x003f:
            r1 = r6
        L_0x0040:
            if (r1 == 0) goto L_0x006a
            if (r1 == r5) goto L_0x0062
            if (r1 == r4) goto L_0x005b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid mode: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r9.println(r1)
            return r6
        L_0x005b:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            r2 = 0
            r1.setFullScreenMode(r2)
            return r7
        L_0x0062:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            java.lang.Boolean r2 = java.lang.Boolean.FALSE
            r1.setFullScreenMode(r2)
            return r7
        L_0x006a:
            com.android.server.autofill.AutofillManagerService r1 = r8.mService
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r1.setFullScreenMode(r2)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.setFullScreenMode(java.io.PrintWriter):int");
    }

    private int getBindInstantService(PrintWriter pw) {
        if (this.mService.getAllowInstantService()) {
            pw.println("true");
            return 0;
        }
        pw.println("false");
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int setBindInstantService(java.io.PrintWriter r8) {
        /*
            r7 = this;
            java.lang.String r0 = r7.getNextArgRequired()
            java.lang.String r1 = r0.toLowerCase()
            int r2 = r1.hashCode()
            r3 = 3569038(0x36758e, float:5.001287E-39)
            r4 = -1
            r5 = 1
            r6 = 0
            if (r2 == r3) goto L_0x0024
            r3 = 97196323(0x5cb1923, float:1.9099262E-35)
            if (r2 == r3) goto L_0x001a
        L_0x0019:
            goto L_0x002f
        L_0x001a:
            java.lang.String r2 = "false"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0019
            r1 = r5
            goto L_0x0030
        L_0x0024:
            java.lang.String r2 = "true"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0019
            r1 = r6
            goto L_0x0030
        L_0x002f:
            r1 = r4
        L_0x0030:
            if (r1 == 0) goto L_0x004f
            if (r1 == r5) goto L_0x0049
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid mode: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r8.println(r1)
            return r4
        L_0x0049:
            com.android.server.autofill.AutofillManagerService r1 = r7.mService
            r1.setAllowInstantService(r6)
            return r6
        L_0x004f:
            com.android.server.autofill.AutofillManagerService r1 = r7.mService
            r1.setAllowInstantService(r5)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceShellCommand.setBindInstantService(java.io.PrintWriter):int");
    }

    private int setTemporaryAugmentedService(PrintWriter pw) {
        int userId = getNextIntArgRequired();
        String serviceName = getNextArg();
        if (serviceName == null) {
            this.mService.resetTemporaryAugmentedAutofillService(userId);
            return 0;
        }
        int duration = getNextIntArgRequired();
        this.mService.setTemporaryAugmentedAutofillService(userId, serviceName, duration);
        pw.println("AugmentedAutofillService temporarily set to " + serviceName + " for " + duration + "ms");
        return 0;
    }

    private int getDefaultAugmentedServiceEnabled(PrintWriter pw) {
        pw.println(this.mService.isDefaultAugmentedServiceEnabled(getNextIntArgRequired()));
        return 0;
    }

    private int setDefaultAugmentedServiceEnabled(PrintWriter pw) {
        int userId = getNextIntArgRequired();
        boolean enabled = Boolean.parseBoolean(getNextArgRequired());
        if (this.mService.setDefaultAugmentedServiceEnabled(userId, enabled)) {
            return 0;
        }
        pw.println("already " + enabled);
        return 0;
    }

    private int requestDestroy(PrintWriter pw) {
        if (!isNextArgSessions(pw)) {
            return -1;
        }
        int userId = getUserIdFromArgsOrAllUsers();
        final CountDownLatch latch = new CountDownLatch(1);
        return requestSessionCommon(pw, latch, new Runnable(userId, new IResultReceiver.Stub() {
            public void send(int resultCode, Bundle resultData) {
                latch.countDown();
            }
        }) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ IResultReceiver f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AutofillManagerServiceShellCommand.this.lambda$requestDestroy$1$AutofillManagerServiceShellCommand(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$requestDestroy$1$AutofillManagerServiceShellCommand(int userId, IResultReceiver receiver) {
        this.mService.destroySessions(userId, receiver);
    }

    private int requestList(final PrintWriter pw) {
        if (!isNextArgSessions(pw)) {
            return -1;
        }
        int userId = getUserIdFromArgsOrAllUsers();
        final CountDownLatch latch = new CountDownLatch(1);
        return requestSessionCommon(pw, latch, new Runnable(userId, new IResultReceiver.Stub() {
            public void send(int resultCode, Bundle resultData) {
                Iterator<String> it = resultData.getStringArrayList("sessions").iterator();
                while (it.hasNext()) {
                    pw.println(it.next());
                }
                latch.countDown();
            }
        }) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ IResultReceiver f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AutofillManagerServiceShellCommand.this.lambda$requestList$2$AutofillManagerServiceShellCommand(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$requestList$2$AutofillManagerServiceShellCommand(int userId, IResultReceiver receiver) {
        this.mService.listSessions(userId, receiver);
    }

    private boolean isNextArgSessions(PrintWriter pw) {
        if (getNextArgRequired().equals("sessions")) {
            return true;
        }
        pw.println("Error: invalid list type");
        return false;
    }

    private int requestSessionCommon(PrintWriter pw, CountDownLatch latch, Runnable command) {
        command.run();
        return waitForLatch(pw, latch);
    }

    private int waitForLatch(PrintWriter pw, CountDownLatch latch) {
        try {
            if (latch.await(5, TimeUnit.SECONDS)) {
                return 0;
            }
            pw.println("Timed out after 5 seconds");
            return -1;
        } catch (InterruptedException e) {
            pw.println("System call interrupted");
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    private int requestReset() {
        this.mService.reset();
        return 0;
    }

    private int getUserIdFromArgsOrAllUsers() {
        if ("--user".equals(getNextArg())) {
            return UserHandle.parseUserArg(getNextArgRequired());
        }
        return -1;
    }

    private int getNextIntArgRequired() {
        return Integer.parseInt(getNextArgRequired());
    }
}
