package com.android.server;

import android.content.Context;
import android.net.INetd;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IRecoverySystem;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Slog;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public final class RecoverySystemService extends SystemService {
    private static final boolean DEBUG = false;
    private static final String INIT_SERVICE_CLEAR_BCB = "init.svc.clear-bcb";
    private static final String INIT_SERVICE_SETUP_BCB = "init.svc.setup-bcb";
    private static final String INIT_SERVICE_UNCRYPT = "init.svc.uncrypt";
    private static final int SOCKET_CONNECTION_MAX_RETRY = 30;
    private static final String TAG = "RecoverySystemService";
    private static final String UNCRYPT_SOCKET = "uncrypt";
    /* access modifiers changed from: private */
    public static final Object sRequestLock = new Object();
    /* access modifiers changed from: private */
    public Context mContext;

    public RecoverySystemService(Context context) {
        super(context);
        this.mContext = context;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.RecoverySystemService$BinderService, android.os.IBinder] */
    public void onStart() {
        publishBinderService("recovery", new BinderService());
    }

    private final class BinderService extends IRecoverySystem.Stub {
        private BinderService() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 13 */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x010f, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:0x0118, code lost:
            throw r5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean uncrypt(java.lang.String r14, android.os.IRecoverySystemProgressListener r15) {
            /*
                r13 = this;
                java.lang.Object r0 = com.android.server.RecoverySystemService.sRequestLock
                monitor-enter(r0)
                com.android.server.RecoverySystemService r1 = com.android.server.RecoverySystemService.this     // Catch:{ all -> 0x0139 }
                android.content.Context r1 = r1.mContext     // Catch:{ all -> 0x0139 }
                java.lang.String r2 = "android.permission.RECOVERY"
                r3 = 0
                r1.enforceCallingOrSelfPermission(r2, r3)     // Catch:{ all -> 0x0139 }
                boolean r1 = r13.checkAndWaitForUncryptService()     // Catch:{ all -> 0x0139 }
                r2 = 0
                if (r1 != 0) goto L_0x0022
                java.lang.String r3 = "RecoverySystemService"
                java.lang.String r4 = "uncrypt service is unavailable."
                android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0139 }
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x0022:
                java.io.File r3 = android.os.RecoverySystem.UNCRYPT_PACKAGE_FILE     // Catch:{ all -> 0x0139 }
                r3.delete()     // Catch:{ all -> 0x0139 }
                java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ IOException -> 0x0119 }
                java.io.File r4 = android.os.RecoverySystem.UNCRYPT_PACKAGE_FILE     // Catch:{ IOException -> 0x0119 }
                r3.<init>(r4)     // Catch:{ IOException -> 0x0119 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x010d }
                r4.<init>()     // Catch:{ all -> 0x010d }
                r4.append(r14)     // Catch:{ all -> 0x010d }
                java.lang.String r5 = "\n"
                r4.append(r5)     // Catch:{ all -> 0x010d }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x010d }
                r3.write(r4)     // Catch:{ all -> 0x010d }
                r3.close()     // Catch:{ IOException -> 0x0119 }
                java.lang.String r3 = "ctl.start"
                java.lang.String r4 = "uncrypt"
                android.os.SystemProperties.set(r3, r4)     // Catch:{ all -> 0x0139 }
                android.net.LocalSocket r3 = r13.connectService()     // Catch:{ all -> 0x0139 }
                if (r3 != 0) goto L_0x005d
                java.lang.String r4 = "RecoverySystemService"
                java.lang.String r5 = "Failed to connect to uncrypt socket"
                android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0139 }
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x005d:
                r4 = 0
                r5 = 0
                java.io.DataInputStream r6 = new java.io.DataInputStream     // Catch:{ IOException -> 0x00ef }
                java.io.InputStream r7 = r3.getInputStream()     // Catch:{ IOException -> 0x00ef }
                r6.<init>(r7)     // Catch:{ IOException -> 0x00ef }
                r4 = r6
                java.io.DataOutputStream r6 = new java.io.DataOutputStream     // Catch:{ IOException -> 0x00ef }
                java.io.OutputStream r7 = r3.getOutputStream()     // Catch:{ IOException -> 0x00ef }
                r6.<init>(r7)     // Catch:{ IOException -> 0x00ef }
                r5 = r6
                r6 = -2147483648(0xffffffff80000000, float:-0.0)
                r7 = r6
            L_0x0076:
                int r8 = r4.readInt()     // Catch:{ IOException -> 0x00ef }
                if (r8 != r7) goto L_0x007f
                if (r7 == r6) goto L_0x007f
                goto L_0x0076
            L_0x007f:
                r7 = r8
                if (r8 < 0) goto L_0x00c7
                r9 = 100
                if (r8 > r9) goto L_0x00c7
                java.lang.String r10 = "RecoverySystemService"
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00ef }
                r11.<init>()     // Catch:{ IOException -> 0x00ef }
                java.lang.String r12 = "uncrypt read status: "
                r11.append(r12)     // Catch:{ IOException -> 0x00ef }
                r11.append(r8)     // Catch:{ IOException -> 0x00ef }
                java.lang.String r11 = r11.toString()     // Catch:{ IOException -> 0x00ef }
                android.util.Slog.i(r10, r11)     // Catch:{ IOException -> 0x00ef }
                if (r15 == 0) goto L_0x00ab
                r15.onProgress(r8)     // Catch:{ RemoteException -> 0x00a3 }
                goto L_0x00ab
            L_0x00a3:
                r10 = move-exception
                java.lang.String r11 = "RecoverySystemService"
                java.lang.String r12 = "RemoteException when posting progress"
                android.util.Slog.w(r11, r12)     // Catch:{ IOException -> 0x00ef }
            L_0x00ab:
                if (r8 != r9) goto L_0x00c6
                java.lang.String r6 = "RecoverySystemService"
                java.lang.String r9 = "uncrypt successfully finished."
                android.util.Slog.i(r6, r9)     // Catch:{ IOException -> 0x00ef }
                r5.writeInt(r2)     // Catch:{ IOException -> 0x00ef }
                libcore.io.IoUtils.closeQuietly(r4)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
                r2 = 1
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x00c6:
                goto L_0x0076
            L_0x00c7:
                java.lang.String r6 = "RecoverySystemService"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00ef }
                r9.<init>()     // Catch:{ IOException -> 0x00ef }
                java.lang.String r10 = "uncrypt failed with status: "
                r9.append(r10)     // Catch:{ IOException -> 0x00ef }
                r9.append(r8)     // Catch:{ IOException -> 0x00ef }
                java.lang.String r9 = r9.toString()     // Catch:{ IOException -> 0x00ef }
                android.util.Slog.e(r6, r9)     // Catch:{ IOException -> 0x00ef }
                r5.writeInt(r2)     // Catch:{ IOException -> 0x00ef }
                libcore.io.IoUtils.closeQuietly(r4)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x00ed:
                r2 = move-exception
                goto L_0x0103
            L_0x00ef:
                r6 = move-exception
                java.lang.String r7 = "RecoverySystemService"
                java.lang.String r8 = "IOException when reading status: "
                android.util.Slog.e(r7, r8, r6)     // Catch:{ all -> 0x00ed }
                libcore.io.IoUtils.closeQuietly(r4)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x0103:
                libcore.io.IoUtils.closeQuietly(r4)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ all -> 0x0139 }
                libcore.io.IoUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
                throw r2     // Catch:{ all -> 0x0139 }
            L_0x010d:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x010f }
            L_0x010f:
                r5 = move-exception
                r3.close()     // Catch:{ all -> 0x0114 }
                goto L_0x0118
            L_0x0114:
                r6 = move-exception
                r4.addSuppressed(r6)     // Catch:{ IOException -> 0x0119 }
            L_0x0118:
                throw r5     // Catch:{ IOException -> 0x0119 }
            L_0x0119:
                r3 = move-exception
                java.lang.String r4 = "RecoverySystemService"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0139 }
                r5.<init>()     // Catch:{ all -> 0x0139 }
                java.lang.String r6 = "IOException when writing \""
                r5.append(r6)     // Catch:{ all -> 0x0139 }
                java.io.File r6 = android.os.RecoverySystem.UNCRYPT_PACKAGE_FILE     // Catch:{ all -> 0x0139 }
                r5.append(r6)     // Catch:{ all -> 0x0139 }
                java.lang.String r6 = "\":"
                r5.append(r6)     // Catch:{ all -> 0x0139 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0139 }
                android.util.Slog.e(r4, r5, r3)     // Catch:{ all -> 0x0139 }
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                return r2
            L_0x0139:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0139 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.RecoverySystemService.BinderService.uncrypt(java.lang.String, android.os.IRecoverySystemProgressListener):boolean");
        }

        public boolean clearBcb() {
            boolean z;
            synchronized (RecoverySystemService.sRequestLock) {
                z = setupOrClearBcb(false, (String) null);
            }
            return z;
        }

        public boolean setupBcb(String command) {
            boolean z;
            synchronized (RecoverySystemService.sRequestLock) {
                z = setupOrClearBcb(true, command);
            }
            return z;
        }

        public void rebootRecoveryWithCommand(String command) {
            synchronized (RecoverySystemService.sRequestLock) {
                if (setupOrClearBcb(true, command)) {
                    ((PowerManager) RecoverySystemService.this.mContext.getSystemService("power")).reboot("recovery");
                }
            }
        }

        private boolean checkAndWaitForUncryptService() {
            int retry = 0;
            while (true) {
                boolean busy = false;
                if (retry >= 30) {
                    return false;
                }
                String uncryptService = SystemProperties.get(RecoverySystemService.INIT_SERVICE_UNCRYPT);
                String setupBcbService = SystemProperties.get(RecoverySystemService.INIT_SERVICE_SETUP_BCB);
                String clearBcbService = SystemProperties.get(RecoverySystemService.INIT_SERVICE_CLEAR_BCB);
                if (INetd.IF_FLAG_RUNNING.equals(uncryptService) || INetd.IF_FLAG_RUNNING.equals(setupBcbService) || INetd.IF_FLAG_RUNNING.equals(clearBcbService)) {
                    busy = true;
                }
                if (!busy) {
                    return true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Slog.w(RecoverySystemService.TAG, "Interrupted:", e);
                }
                retry++;
            }
        }

        private LocalSocket connectService() {
            LocalSocket socket = new LocalSocket();
            boolean done = false;
            int retry = 0;
            while (true) {
                if (retry >= 30) {
                    break;
                }
                try {
                    socket.connect(new LocalSocketAddress(RecoverySystemService.UNCRYPT_SOCKET, LocalSocketAddress.Namespace.RESERVED));
                    done = true;
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        Slog.w(RecoverySystemService.TAG, "Interrupted:", e2);
                    }
                    retry++;
                }
            }
            if (done) {
                return socket;
            }
            Slog.e(RecoverySystemService.TAG, "Timed out connecting to uncrypt socket");
            return null;
        }

        private boolean setupOrClearBcb(boolean isSetup, String command) {
            RecoverySystemService.this.mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", (String) null);
            if (!checkAndWaitForUncryptService()) {
                Slog.e(RecoverySystemService.TAG, "uncrypt service is unavailable.");
                return false;
            }
            if (isSetup) {
                SystemProperties.set("ctl.start", "setup-bcb");
            } else {
                SystemProperties.set("ctl.start", "clear-bcb");
            }
            LocalSocket socket = connectService();
            if (socket == null) {
                Slog.e(RecoverySystemService.TAG, "Failed to connect to uncrypt socket");
                return false;
            }
            DataInputStream dis = null;
            DataOutputStream dos = null;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                if (isSetup) {
                    byte[] cmdUtf8 = command.getBytes("UTF-8");
                    dos.writeInt(cmdUtf8.length);
                    dos.write(cmdUtf8, 0, cmdUtf8.length);
                    dos.flush();
                }
                int status = dis.readInt();
                dos.writeInt(0);
                if (status == 100) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("uncrypt ");
                    sb.append(isSetup ? "setup" : "clear");
                    sb.append(" bcb successfully finished.");
                    Slog.i(RecoverySystemService.TAG, sb.toString());
                    IoUtils.closeQuietly(dis);
                    IoUtils.closeQuietly(dos);
                    IoUtils.closeQuietly(socket);
                    return true;
                }
                Slog.e(RecoverySystemService.TAG, "uncrypt failed with status: " + status);
                return false;
            } catch (IOException e) {
                Slog.e(RecoverySystemService.TAG, "IOException when communicating with uncrypt:", e);
                return false;
            } finally {
                IoUtils.closeQuietly(dis);
                IoUtils.closeQuietly(dos);
                IoUtils.closeQuietly(socket);
            }
        }
    }
}
