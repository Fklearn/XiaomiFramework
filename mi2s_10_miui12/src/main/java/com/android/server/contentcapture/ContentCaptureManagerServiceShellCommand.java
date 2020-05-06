package com.android.server.contentcapture;

import android.os.Bundle;
import android.os.ShellCommand;
import android.os.UserHandle;
import com.android.internal.os.IResultReceiver;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class ContentCaptureManagerServiceShellCommand extends ShellCommand {
    private final ContentCaptureManagerService mService;

    public ContentCaptureManagerServiceShellCommand(ContentCaptureManagerService service) {
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
                    c = 2;
                    break;
                }
                break;
            case 113762:
                if (cmd.equals("set")) {
                    c = 3;
                    break;
                }
                break;
            case 3322014:
                if (cmd.equals("list")) {
                    c = 0;
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
            return requestGet(pw);
        }
        if (c != 3) {
            return handleDefaultCommands(cmd);
        }
        return requestSet(pw);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0085, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0086, code lost:
        r0.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0089, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x007e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x007f, code lost:
        if (r1 != null) goto L_0x0081;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHelp() {
        /*
            r4 = this;
            java.lang.String r0 = ""
            java.io.PrintWriter r1 = r4.getOutPrintWriter()
            java.lang.String r2 = "ContentCapture Service (content_capture) commands:"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  help"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Prints this help text."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  get bind-instant-service-allowed"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Gets whether binding to services provided by instant apps is allowed"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  set bind-instant-service-allowed [true | false]"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Sets whether binding to services provided by instant apps is allowed"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  set temporary-service USER_ID [COMPONENT_NAME DURATION]"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Temporarily (for DURATION ms) changes the service implemtation."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    To reset, call with just the USER_ID argument."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  set default-service-enabled USER_ID [true|false]"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Enable / disable the default service for the user."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  get default-service-enabled USER_ID"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Checks whether the default service is enabled for the user."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  list sessions [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Lists all pending sessions."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "  destroy sessions [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = "    Destroys all pending sessions."
            r1.println(r2)     // Catch:{ all -> 0x007c }
            r1.println(r0)     // Catch:{ all -> 0x007c }
            r1.close()
            return
        L_0x007c:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x007e }
        L_0x007e:
            r2 = move-exception
            if (r1 == 0) goto L_0x0089
            r1.close()     // Catch:{ all -> 0x0085 }
            goto L_0x0089
        L_0x0085:
            r3 = move-exception
            r0.addSuppressed(r3)
        L_0x0089:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.onHelp():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0048  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int requestGet(java.io.PrintWriter r6) {
        /*
            r5 = this;
            java.lang.String r0 = r5.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = 529654941(0x1f91e49d, float:6.17881E-20)
            r3 = 1
            r4 = -1
            if (r1 == r2) goto L_0x001f
            r2 = 809633044(0x30420514, float:7.0584005E-10)
            if (r1 == r2) goto L_0x0015
        L_0x0014:
            goto L_0x0029
        L_0x0015:
            java.lang.String r1 = "bind-instant-service-allowed"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0014
            r1 = 0
            goto L_0x002a
        L_0x001f:
            java.lang.String r1 = "default-service-enabled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0014
            r1 = r3
            goto L_0x002a
        L_0x0029:
            r1 = r4
        L_0x002a:
            if (r1 == 0) goto L_0x0048
            if (r1 == r3) goto L_0x0043
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid set: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r6.println(r1)
            return r4
        L_0x0043:
            int r1 = r5.getDefaultServiceEnabled(r6)
            return r1
        L_0x0048:
            int r1 = r5.getBindInstantService(r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.requestGet(java.io.PrintWriter):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int requestSet(java.io.PrintWriter r7) {
        /*
            r6 = this;
            java.lang.String r0 = r6.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = 529654941(0x1f91e49d, float:6.17881E-20)
            r3 = 2
            r4 = 1
            r5 = -1
            if (r1 == r2) goto L_0x0030
            r2 = 809633044(0x30420514, float:7.0584005E-10)
            if (r1 == r2) goto L_0x0026
            r2 = 2003978041(0x77724739, float:4.913986E33)
            if (r1 == r2) goto L_0x001b
        L_0x001a:
            goto L_0x003a
        L_0x001b:
            java.lang.String r1 = "temporary-service"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001a
            r1 = r4
            goto L_0x003b
        L_0x0026:
            java.lang.String r1 = "bind-instant-service-allowed"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001a
            r1 = 0
            goto L_0x003b
        L_0x0030:
            java.lang.String r1 = "default-service-enabled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001a
            r1 = r3
            goto L_0x003b
        L_0x003a:
            r1 = r5
        L_0x003b:
            if (r1 == 0) goto L_0x0060
            if (r1 == r4) goto L_0x005b
            if (r1 == r3) goto L_0x0056
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid set: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r7.println(r1)
            return r5
        L_0x0056:
            int r1 = r6.setDefaultServiceEnabled(r7)
            return r1
        L_0x005b:
            int r1 = r6.setTemporaryService(r7)
            return r1
        L_0x0060:
            int r1 = r6.setBindInstantService(r7)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.requestSet(java.io.PrintWriter):int");
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
            com.android.server.contentcapture.ContentCaptureManagerService r1 = r7.mService
            r1.setAllowInstantService(r6)
            return r6
        L_0x004f:
            com.android.server.contentcapture.ContentCaptureManagerService r1 = r7.mService
            r1.setAllowInstantService(r5)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.setBindInstantService(java.io.PrintWriter):int");
    }

    private int setTemporaryService(PrintWriter pw) {
        int userId = getNextIntArgRequired();
        String serviceName = getNextArg();
        if (serviceName == null) {
            this.mService.resetTemporaryService(userId);
            return 0;
        }
        int duration = getNextIntArgRequired();
        this.mService.setTemporaryService(userId, serviceName, duration);
        pw.println("ContentCaptureService temporarily set to " + serviceName + " for " + duration + "ms");
        return 0;
    }

    private int setDefaultServiceEnabled(PrintWriter pw) {
        int userId = getNextIntArgRequired();
        boolean enabled = Boolean.parseBoolean(getNextArgRequired());
        if (this.mService.setDefaultServiceEnabled(userId, enabled)) {
            return 0;
        }
        pw.println("already " + enabled);
        return 0;
    }

    private int getDefaultServiceEnabled(PrintWriter pw) {
        pw.println(this.mService.isDefaultServiceEnabled(getNextIntArgRequired()));
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
                ContentCaptureManagerServiceShellCommand.this.lambda$requestDestroy$0$ContentCaptureManagerServiceShellCommand(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$requestDestroy$0$ContentCaptureManagerServiceShellCommand(int userId, IResultReceiver receiver) {
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
                ContentCaptureManagerServiceShellCommand.this.lambda$requestList$1$ContentCaptureManagerServiceShellCommand(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$requestList$1$ContentCaptureManagerServiceShellCommand(int userId, IResultReceiver receiver) {
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
