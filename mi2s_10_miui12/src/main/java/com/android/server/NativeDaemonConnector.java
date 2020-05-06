package com.android.server;

import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.LocalLog;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.Watchdog;
import com.android.server.power.ShutdownThread;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

final class NativeDaemonConnector implements Runnable, Handler.Callback, Watchdog.Monitor {
    private static final long DEFAULT_TIMEOUT = 60000;
    private static final boolean VDBG = false;
    private static final long WARN_EXECUTE_DELAY_MS = 500;
    private final int BUFFER_SIZE;
    private final String TAG;
    private Handler mCallbackHandler;
    private INativeDaemonConnectorCallbacks mCallbacks;
    private final Object mDaemonLock;
    private volatile boolean mDebug;
    private LocalLog mLocalLog;
    private final Looper mLooper;
    private OutputStream mOutputStream;
    private final ResponseQueue mResponseQueue;
    private AtomicInteger mSequenceNumber;
    private String mSocket;
    private final PowerManager.WakeLock mWakeLock;
    private volatile Object mWarnIfHeld;

    NativeDaemonConnector(INativeDaemonConnectorCallbacks callbacks, String socket, int responseQueueSize, String logTag, int maxLogSize, PowerManager.WakeLock wl) {
        this(callbacks, socket, responseQueueSize, logTag, maxLogSize, wl, FgThread.get().getLooper());
    }

    NativeDaemonConnector(INativeDaemonConnectorCallbacks callbacks, String socket, int responseQueueSize, String logTag, int maxLogSize, PowerManager.WakeLock wl, Looper looper) {
        this.mDebug = false;
        this.mDaemonLock = new Object();
        this.BUFFER_SIZE = 4096;
        this.mCallbacks = callbacks;
        this.mSocket = socket;
        this.mResponseQueue = new ResponseQueue(responseQueueSize);
        this.mWakeLock = wl;
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(true);
        }
        this.mLooper = looper;
        this.mSequenceNumber = new AtomicInteger(0);
        this.TAG = logTag != null ? logTag : "NativeDaemonConnector";
        this.mLocalLog = new LocalLog(maxLogSize);
    }

    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    private int uptimeMillisInt() {
        return ((int) SystemClock.uptimeMillis()) & Integer.MAX_VALUE;
    }

    public void setWarnIfHeld(Object warnIfHeld) {
        Preconditions.checkState(this.mWarnIfHeld == null);
        this.mWarnIfHeld = Preconditions.checkNotNull(warnIfHeld);
    }

    public void run() {
        this.mCallbackHandler = new Handler(this.mLooper, this);
        while (!isShuttingDown()) {
            try {
                listenToSocket();
            } catch (Exception e) {
                loge("Error in NativeDaemonConnector: " + e);
                if (!isShuttingDown()) {
                    SystemClock.sleep(5000);
                } else {
                    return;
                }
            }
        }
    }

    private static boolean isShuttingDown() {
        String shutdownAct = SystemProperties.get(ShutdownThread.SHUTDOWN_ACTION_PROPERTY, "");
        return shutdownAct != null && shutdownAct.length() > 0;
    }

    public boolean handleMessage(Message msg) {
        Object[] objArr;
        PowerManager.WakeLock wakeLock;
        PowerManager.WakeLock wakeLock2;
        PowerManager.WakeLock wakeLock3;
        String event = (String) msg.obj;
        int start = uptimeMillisInt();
        int sent = msg.arg1;
        try {
            if (!this.mCallbacks.onEvent(msg.what, event, NativeDaemonEvent.unescapeArgs(event))) {
                log(String.format("Unhandled event '%s'", new Object[]{event}));
            }
            if (this.mCallbacks.onCheckHoldWakeLock(msg.what) && (wakeLock3 = this.mWakeLock) != null) {
                wakeLock3.release();
            }
            int end = uptimeMillisInt();
            if (start > sent && ((long) (start - sent)) > 500) {
                loge(String.format("NDC event {%s} processed too late: %dms", new Object[]{event, Integer.valueOf(start - sent)}));
            }
            if (end > start && ((long) (end - start)) > 500) {
                objArr = new Object[]{event, Integer.valueOf(end - start)};
                loge(String.format("NDC event {%s} took too long: %dms", objArr));
            }
        } catch (Exception e) {
            loge("Error handling '" + event + "': " + e);
            if (this.mCallbacks.onCheckHoldWakeLock(msg.what) && (wakeLock = this.mWakeLock) != null) {
                wakeLock.release();
            }
            int end2 = uptimeMillisInt();
            if (start > sent && ((long) (start - sent)) > 500) {
                loge(String.format("NDC event {%s} processed too late: %dms", new Object[]{event, Integer.valueOf(start - sent)}));
            }
            if (end2 > start && ((long) (end2 - start)) > 500) {
                objArr = new Object[]{event, Integer.valueOf(end2 - start)};
            }
        } catch (Throwable th) {
            if (this.mCallbacks.onCheckHoldWakeLock(msg.what) && (wakeLock2 = this.mWakeLock) != null) {
                wakeLock2.release();
            }
            int end3 = uptimeMillisInt();
            if (start > sent && ((long) (start - sent)) > 500) {
                loge(String.format("NDC event {%s} processed too late: %dms", new Object[]{event, Integer.valueOf(start - sent)}));
            }
            if (end3 > start && ((long) (end3 - start)) > 500) {
                loge(String.format("NDC event {%s} took too long: %dms", new Object[]{event, Integer.valueOf(end3 - start)}));
            }
            throw th;
        }
        return true;
    }

    private LocalSocketAddress determineSocketAddress() {
        if (!this.mSocket.startsWith("__test__") || !Build.IS_DEBUGGABLE) {
            return new LocalSocketAddress(this.mSocket, LocalSocketAddress.Namespace.RESERVED);
        }
        return new LocalSocketAddress(this.mSocket);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private void listenToSocket() throws java.io.IOException {
        /*
            r17 = this;
            r1 = r17
            r2 = 0
            r3 = 0
            android.net.LocalSocket r0 = new android.net.LocalSocket     // Catch:{ IOException -> 0x01ba }
            r0.<init>()     // Catch:{ IOException -> 0x01ba }
            r2 = r0
            android.net.LocalSocketAddress r0 = r17.determineSocketAddress()     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r4 = r0
            r2.connect(r4)     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            java.io.InputStream r0 = r2.getInputStream()     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r5 = r0
            java.lang.Object r6 = r1.mDaemonLock     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            monitor-enter(r6)     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            java.io.OutputStream r0 = r2.getOutputStream()     // Catch:{ all -> 0x019e }
            r1.mOutputStream = r0     // Catch:{ all -> 0x019e }
            monitor-exit(r6)     // Catch:{ all -> 0x019e }
            com.android.server.INativeDaemonConnectorCallbacks r0 = r1.mCallbacks     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r0.onDaemonConnected()     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r0 = 0
            r6 = 4096(0x1000, float:5.74E-42)
            byte[] r7 = new byte[r6]     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r8 = 0
            r9 = r8
        L_0x002d:
            int r10 = 4096 - r9
            int r10 = r5.read(r7, r9, r10)     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            if (r10 >= 0) goto L_0x00ae
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01ba }
            r6.<init>()     // Catch:{ IOException -> 0x01ba }
            java.lang.String r8 = "got "
            r6.append(r8)     // Catch:{ IOException -> 0x01ba }
            r6.append(r10)     // Catch:{ IOException -> 0x01ba }
            java.lang.String r8 = " reading with start = "
            r6.append(r8)     // Catch:{ IOException -> 0x01ba }
            r6.append(r9)     // Catch:{ IOException -> 0x01ba }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x01ba }
            r1.loge(r6)     // Catch:{ IOException -> 0x01ba }
            java.lang.Object r4 = r1.mDaemonLock
            monitor-enter(r4)
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ all -> 0x00ab }
            if (r0 == 0) goto L_0x008c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0075 }
            r0.<init>()     // Catch:{ IOException -> 0x0075 }
            java.lang.String r5 = "closing stream for "
            r0.append(r5)     // Catch:{ IOException -> 0x0075 }
            java.lang.String r5 = r1.mSocket     // Catch:{ IOException -> 0x0075 }
            r0.append(r5)     // Catch:{ IOException -> 0x0075 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0075 }
            r1.loge(r0)     // Catch:{ IOException -> 0x0075 }
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ IOException -> 0x0075 }
            r0.close()     // Catch:{ IOException -> 0x0075 }
            goto L_0x008a
        L_0x0075:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r5.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = "Failed closing output stream: "
            r5.append(r6)     // Catch:{ all -> 0x00ab }
            r5.append(r0)     // Catch:{ all -> 0x00ab }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00ab }
            r1.loge(r5)     // Catch:{ all -> 0x00ab }
        L_0x008a:
            r1.mOutputStream = r3     // Catch:{ all -> 0x00ab }
        L_0x008c:
            monitor-exit(r4)     // Catch:{ all -> 0x00ab }
            r2.close()     // Catch:{ IOException -> 0x0092 }
            goto L_0x00aa
        L_0x0092:
            r0 = move-exception
            r3 = r0
            r0 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed closing socket: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r1.loge(r3)
        L_0x00aa:
            return
        L_0x00ab:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00ab }
            throw r0
        L_0x00ae:
            java.io.FileDescriptor[] r11 = r2.getAncillaryFileDescriptors()     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            int r10 = r10 + r9
            r0 = 0
            r9 = 0
            r12 = r0
        L_0x00b6:
            if (r9 >= r10) goto L_0x017b
            byte r0 = r7[r9]     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            if (r0 != 0) goto L_0x016f
            java.lang.String r0 = new java.lang.String     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            int r13 = r9 - r12
            java.nio.charset.Charset r14 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r0.<init>(r7, r12, r13, r14)     // Catch:{ IOException -> 0x01b1, all -> 0x01ac }
            r13 = r0
            r14 = 0
            com.android.server.NativeDaemonEvent r0 = com.android.server.NativeDaemonEvent.parseRawEvent(r13, r11)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            r15.<init>()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            java.lang.String r3 = "RCV <- {"
            r15.append(r3)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            r15.append(r0)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            java.lang.String r3 = "}"
            r15.append(r3)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            java.lang.String r3 = r15.toString()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            r1.log(r3)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            boolean r3 = r0.isClassUnsolicited()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            if (r3 == 0) goto L_0x012c
            com.android.server.INativeDaemonConnectorCallbacks r3 = r1.mCallbacks     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            int r15 = r0.getCode()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            boolean r3 = r3.onCheckHoldWakeLock(r15)     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            if (r3 == 0) goto L_0x010b
            android.os.PowerManager$WakeLock r3 = r1.mWakeLock     // Catch:{ IllegalArgumentException -> 0x0107, all -> 0x0103 }
            if (r3 == 0) goto L_0x010b
            android.os.PowerManager$WakeLock r3 = r1.mWakeLock     // Catch:{ IllegalArgumentException -> 0x0107, all -> 0x0103 }
            r3.acquire()     // Catch:{ IllegalArgumentException -> 0x0107, all -> 0x0103 }
            r14 = 1
            goto L_0x010b
        L_0x0103:
            r0 = move-exception
            r16 = r2
            goto L_0x0166
        L_0x0107:
            r0 = move-exception
            r16 = r2
            goto L_0x0148
        L_0x010b:
            android.os.Handler r3 = r1.mCallbackHandler     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            int r15 = r0.getCode()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            int r6 = r17.uptimeMillisInt()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            java.lang.String r8 = r0.getRawEvent()     // Catch:{ IllegalArgumentException -> 0x0145, all -> 0x0141 }
            r16 = r2
            r2 = 0
            android.os.Message r3 = r3.obtainMessage(r15, r6, r2, r8)     // Catch:{ IllegalArgumentException -> 0x013f }
            r2 = r3
            android.os.Handler r3 = r1.mCallbackHandler     // Catch:{ IllegalArgumentException -> 0x013f }
            boolean r3 = r3.sendMessage(r2)     // Catch:{ IllegalArgumentException -> 0x013f }
            if (r3 == 0) goto L_0x012b
            r3 = 0
            r14 = r3
        L_0x012b:
            goto L_0x0137
        L_0x012c:
            r16 = r2
            com.android.server.NativeDaemonConnector$ResponseQueue r2 = r1.mResponseQueue     // Catch:{ IllegalArgumentException -> 0x013f }
            int r3 = r0.getCmdNumber()     // Catch:{ IllegalArgumentException -> 0x013f }
            r2.add(r3, r0)     // Catch:{ IllegalArgumentException -> 0x013f }
        L_0x0137:
            if (r14 == 0) goto L_0x0161
            android.os.PowerManager$WakeLock r0 = r1.mWakeLock     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
        L_0x013b:
            r0.release()     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
            goto L_0x0161
        L_0x013f:
            r0 = move-exception
            goto L_0x0148
        L_0x0141:
            r0 = move-exception
            r16 = r2
            goto L_0x0166
        L_0x0145:
            r0 = move-exception
            r16 = r2
        L_0x0148:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0165 }
            r2.<init>()     // Catch:{ all -> 0x0165 }
            java.lang.String r3 = "Problem parsing message "
            r2.append(r3)     // Catch:{ all -> 0x0165 }
            r2.append(r0)     // Catch:{ all -> 0x0165 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0165 }
            r1.log(r2)     // Catch:{ all -> 0x0165 }
            if (r14 == 0) goto L_0x0161
            android.os.PowerManager$WakeLock r0 = r1.mWakeLock     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
            goto L_0x013b
        L_0x0161:
            int r0 = r9 + 1
            r12 = r0
            goto L_0x0171
        L_0x0165:
            r0 = move-exception
        L_0x0166:
            if (r14 == 0) goto L_0x016d
            android.os.PowerManager$WakeLock r2 = r1.mWakeLock     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
            r2.release()     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
        L_0x016d:
            throw r0     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
        L_0x016f:
            r16 = r2
        L_0x0171:
            int r9 = r9 + 1
            r2 = r16
            r3 = 0
            r6 = 4096(0x1000, float:5.74E-42)
            r8 = 0
            goto L_0x00b6
        L_0x017b:
            r16 = r2
            if (r12 != 0) goto L_0x0184
            java.lang.String r0 = "RCV incomplete"
            r1.log(r0)     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
        L_0x0184:
            if (r12 == r10) goto L_0x0191
            r2 = 4096(0x1000, float:5.74E-42)
            int r6 = 4096 - r12
            r3 = 0
            java.lang.System.arraycopy(r7, r12, r7, r3, r6)     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
            r0 = r6
            r9 = r0
            goto L_0x0196
        L_0x0191:
            r2 = 4096(0x1000, float:5.74E-42)
            r3 = 0
            r0 = 0
            r9 = r0
        L_0x0196:
            r6 = r2
            r8 = r3
            r0 = r11
            r2 = r16
            r3 = 0
            goto L_0x002d
        L_0x019e:
            r0 = move-exception
            r16 = r2
        L_0x01a1:
            monitor-exit(r6)     // Catch:{ all -> 0x01aa }
            throw r0     // Catch:{ IOException -> 0x01a6, all -> 0x01a3 }
        L_0x01a3:
            r0 = move-exception
            r2 = r0
            goto L_0x01d1
        L_0x01a6:
            r0 = move-exception
            r2 = r16
            goto L_0x01bb
        L_0x01aa:
            r0 = move-exception
            goto L_0x01a1
        L_0x01ac:
            r0 = move-exception
            r16 = r2
            r2 = r0
            goto L_0x01d1
        L_0x01b1:
            r0 = move-exception
            r16 = r2
            goto L_0x01bb
        L_0x01b5:
            r0 = move-exception
            r16 = r2
            r2 = r0
            goto L_0x01d1
        L_0x01ba:
            r0 = move-exception
        L_0x01bb:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b5 }
            r3.<init>()     // Catch:{ all -> 0x01b5 }
            java.lang.String r4 = "Communications error: "
            r3.append(r4)     // Catch:{ all -> 0x01b5 }
            r3.append(r0)     // Catch:{ all -> 0x01b5 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01b5 }
            r1.loge(r3)     // Catch:{ all -> 0x01b5 }
            throw r0     // Catch:{ all -> 0x01b5 }
        L_0x01d1:
            java.lang.Object r3 = r1.mDaemonLock
            monitor-enter(r3)
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ all -> 0x022d }
            if (r0 == 0) goto L_0x020c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01f4 }
            r0.<init>()     // Catch:{ IOException -> 0x01f4 }
            java.lang.String r4 = "closing stream for "
            r0.append(r4)     // Catch:{ IOException -> 0x01f4 }
            java.lang.String r4 = r1.mSocket     // Catch:{ IOException -> 0x01f4 }
            r0.append(r4)     // Catch:{ IOException -> 0x01f4 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01f4 }
            r1.loge(r0)     // Catch:{ IOException -> 0x01f4 }
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ IOException -> 0x01f4 }
            r0.close()     // Catch:{ IOException -> 0x01f4 }
            goto L_0x0209
        L_0x01f4:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x022d }
            r4.<init>()     // Catch:{ all -> 0x022d }
            java.lang.String r5 = "Failed closing output stream: "
            r4.append(r5)     // Catch:{ all -> 0x022d }
            r4.append(r0)     // Catch:{ all -> 0x022d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x022d }
            r1.loge(r4)     // Catch:{ all -> 0x022d }
        L_0x0209:
            r4 = 0
            r1.mOutputStream = r4     // Catch:{ all -> 0x022d }
        L_0x020c:
            monitor-exit(r3)     // Catch:{ all -> 0x022d }
            if (r16 == 0) goto L_0x022b
            r16.close()     // Catch:{ IOException -> 0x0213 }
            goto L_0x022b
        L_0x0213:
            r0 = move-exception
            r3 = r0
            r0 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed closing socket: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r1.loge(r3)
            goto L_0x022c
        L_0x022b:
        L_0x022c:
            throw r2
        L_0x022d:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x022d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NativeDaemonConnector.listenToSocket():void");
    }

    public static class SensitiveArg {
        private final Object mArg;

        public SensitiveArg(Object arg) {
            this.mArg = arg;
        }

        public String toString() {
            return String.valueOf(this.mArg);
        }
    }

    @VisibleForTesting
    static void makeCommand(StringBuilder rawBuilder, StringBuilder logBuilder, int sequenceNumber, String cmd, Object... args) {
        if (cmd.indexOf(0) >= 0) {
            throw new IllegalArgumentException("Unexpected command: " + cmd);
        } else if (cmd.indexOf(32) < 0) {
            rawBuilder.append(sequenceNumber);
            rawBuilder.append(' ');
            rawBuilder.append(cmd);
            logBuilder.append(sequenceNumber);
            logBuilder.append(' ');
            logBuilder.append(cmd);
            int length = args.length;
            int i = 0;
            while (i < length) {
                Object arg = args[i];
                String argString = String.valueOf(arg);
                if (argString.indexOf(0) < 0) {
                    rawBuilder.append(' ');
                    logBuilder.append(' ');
                    appendEscaped(rawBuilder, argString);
                    if (arg instanceof SensitiveArg) {
                        logBuilder.append("[scrubbed]");
                    } else {
                        appendEscaped(logBuilder, argString);
                    }
                    i++;
                } else {
                    throw new IllegalArgumentException("Unexpected argument: " + arg);
                }
            }
            rawBuilder.append(0);
        } else {
            throw new IllegalArgumentException("Arguments must be separate from command");
        }
    }

    public void waitForCallbacks() {
        if (Thread.currentThread() != this.mLooper.getThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            this.mCallbackHandler.post(new Runnable() {
                public void run() {
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Slog.wtf(this.TAG, "Interrupted while waiting for unsolicited response handling", e);
            }
        } else {
            throw new IllegalStateException("Must not call this method on callback thread");
        }
    }

    public NativeDaemonEvent execute(Command cmd) throws NativeDaemonConnectorException {
        return execute(cmd.mCmd, cmd.mArguments.toArray());
    }

    public NativeDaemonEvent execute(String cmd, Object... args) throws NativeDaemonConnectorException {
        return execute(60000, cmd, args);
    }

    public NativeDaemonEvent execute(long timeoutMs, String cmd, Object... args) throws NativeDaemonConnectorException {
        NativeDaemonEvent[] events = executeForList(timeoutMs, cmd, args);
        if (events.length == 1) {
            return events[0];
        }
        throw new NativeDaemonConnectorException("Expected exactly one response, but received " + events.length);
    }

    public NativeDaemonEvent[] executeForList(Command cmd) throws NativeDaemonConnectorException {
        return executeForList(cmd.mCmd, cmd.mArguments.toArray());
    }

    public NativeDaemonEvent[] executeForList(String cmd, Object... args) throws NativeDaemonConnectorException {
        return executeForList(60000, cmd, args);
    }

    /* Debug info: failed to restart local var, previous not found, register: 21 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x009b, code lost:
        r0 = r1.mResponseQueue.remove(r7, r22, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00a3, code lost:
        if (r0 == null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00a5, code lost:
        r4.add(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00ac, code lost:
        if (r0.isClassContinue() != false) goto L_0x010d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00ae, code lost:
        r15 = android.os.SystemClock.uptimeMillis();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00b8, code lost:
        if ((r15 - r2) <= 500) goto L_0x00e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00ba, code lost:
        r12 = new java.lang.StringBuilder();
        r17 = r5;
        r12.append("NDC Command {");
        r12.append(r11);
        r12.append("} took too long (");
        r18 = r6;
        r12.append(r15 - r2);
        r12.append("ms)");
        loge(r12.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00e4, code lost:
        r17 = r5;
        r18 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00ec, code lost:
        if (r0.isClassClientError() != false) goto L_0x0107;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00f2, code lost:
        if (r0.isClassServerError() != false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0100, code lost:
        return (com.android.server.NativeDaemonEvent[]) r4.toArray(new com.android.server.NativeDaemonEvent[r4.size()]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0106, code lost:
        throw new com.android.server.NativeDaemonConnector.NativeDaemonFailureException(r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x010c, code lost:
        throw new com.android.server.NativeDaemonConnector.NativeDaemonArgumentException(r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x010d, code lost:
        r17 = r5;
        r18 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0112, code lost:
        r17 = r5;
        r18 = r6;
        loge("timed-out waiting for response to " + r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0130, code lost:
        throw new com.android.server.NativeDaemonTimeoutException(r11, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.NativeDaemonEvent[] executeForList(long r22, java.lang.String r24, java.lang.Object... r25) throws com.android.server.NativeDaemonConnectorException {
        /*
            r21 = this;
            r1 = r21
            java.lang.Object r0 = r1.mWarnIfHeld
            if (r0 == 0) goto L_0x0043
            java.lang.Object r0 = r1.mWarnIfHeld
            boolean r0 = java.lang.Thread.holdsLock(r0)
            if (r0 == 0) goto L_0x0043
            java.lang.String r0 = r1.TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Calling thread "
            r2.append(r3)
            java.lang.Thread r3 = java.lang.Thread.currentThread()
            java.lang.String r3 = r3.getName()
            r2.append(r3)
            java.lang.String r3 = " is holding 0x"
            r2.append(r3)
            java.lang.Object r3 = r1.mWarnIfHeld
            int r3 = java.lang.System.identityHashCode(r3)
            java.lang.String r3 = java.lang.Integer.toHexString(r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.Throwable r3 = new java.lang.Throwable
            r3.<init>()
            android.util.Slog.wtf(r0, r2, r3)
        L_0x0043:
            long r2 = android.os.SystemClock.uptimeMillis()
            java.util.ArrayList r4 = com.google.android.collect.Lists.newArrayList()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r5 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r6 = r0
            java.util.concurrent.atomic.AtomicInteger r0 = r1.mSequenceNumber
            int r7 = r0.incrementAndGet()
            r8 = r24
            r9 = r25
            makeCommand(r5, r6, r7, r8, r9)
            java.lang.String r10 = r5.toString()
            java.lang.String r11 = r6.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "SND -> {"
            r0.append(r12)
            r0.append(r11)
            java.lang.String r12 = "}"
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            r1.log(r0)
            java.lang.Object r12 = r1.mDaemonLock
            monitor-enter(r12)
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ all -> 0x0150 }
            if (r0 == 0) goto L_0x0141
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ IOException -> 0x0131 }
            java.nio.charset.Charset r13 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x0131 }
            byte[] r13 = r10.getBytes(r13)     // Catch:{ IOException -> 0x0131 }
            r0.write(r13)     // Catch:{ IOException -> 0x0131 }
            monitor-exit(r12)     // Catch:{ all -> 0x0150 }
            r0 = 0
        L_0x009b:
            com.android.server.NativeDaemonConnector$ResponseQueue r12 = r1.mResponseQueue
            r13 = r22
            com.android.server.NativeDaemonEvent r0 = r12.remove(r7, r13, r11)
            if (r0 == 0) goto L_0x0112
            r4.add(r0)
            boolean r12 = r0.isClassContinue()
            if (r12 != 0) goto L_0x010d
            long r15 = android.os.SystemClock.uptimeMillis()
            long r17 = r15 - r2
            r19 = 500(0x1f4, double:2.47E-321)
            int r12 = (r17 > r19 ? 1 : (r17 == r19 ? 0 : -1))
            if (r12 <= 0) goto L_0x00e4
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r17 = r5
            java.lang.String r5 = "NDC Command {"
            r12.append(r5)
            r12.append(r11)
            java.lang.String r5 = "} took too long ("
            r12.append(r5)
            r18 = r6
            long r5 = r15 - r2
            r12.append(r5)
            java.lang.String r5 = "ms)"
            r12.append(r5)
            java.lang.String r5 = r12.toString()
            r1.loge(r5)
            goto L_0x00e8
        L_0x00e4:
            r17 = r5
            r18 = r6
        L_0x00e8:
            boolean r5 = r0.isClassClientError()
            if (r5 != 0) goto L_0x0107
            boolean r5 = r0.isClassServerError()
            if (r5 != 0) goto L_0x0101
            int r5 = r4.size()
            com.android.server.NativeDaemonEvent[] r5 = new com.android.server.NativeDaemonEvent[r5]
            java.lang.Object[] r5 = r4.toArray(r5)
            com.android.server.NativeDaemonEvent[] r5 = (com.android.server.NativeDaemonEvent[]) r5
            return r5
        L_0x0101:
            com.android.server.NativeDaemonConnector$NativeDaemonFailureException r5 = new com.android.server.NativeDaemonConnector$NativeDaemonFailureException
            r5.<init>(r11, r0)
            throw r5
        L_0x0107:
            com.android.server.NativeDaemonConnector$NativeDaemonArgumentException r5 = new com.android.server.NativeDaemonConnector$NativeDaemonArgumentException
            r5.<init>(r11, r0)
            throw r5
        L_0x010d:
            r17 = r5
            r18 = r6
            goto L_0x009b
        L_0x0112:
            r17 = r5
            r18 = r6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "timed-out waiting for response to "
            r5.append(r6)
            r5.append(r11)
            java.lang.String r5 = r5.toString()
            r1.loge(r5)
            com.android.server.NativeDaemonTimeoutException r5 = new com.android.server.NativeDaemonTimeoutException
            r5.<init>(r11, r0)
            throw r5
        L_0x0131:
            r0 = move-exception
            r13 = r22
            r17 = r5
            r18 = r6
            com.android.server.NativeDaemonConnectorException r5 = new com.android.server.NativeDaemonConnectorException     // Catch:{ all -> 0x0159 }
            java.lang.String r6 = "problem sending command"
            r5.<init>((java.lang.String) r6, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0159 }
            throw r5     // Catch:{ all -> 0x0159 }
        L_0x0141:
            r13 = r22
            r17 = r5
            r18 = r6
            com.android.server.NativeDaemonConnectorException r0 = new com.android.server.NativeDaemonConnectorException     // Catch:{ all -> 0x0159 }
            java.lang.String r5 = "missing output stream"
            r0.<init>(r5)     // Catch:{ all -> 0x0159 }
            throw r0     // Catch:{ all -> 0x0159 }
        L_0x0150:
            r0 = move-exception
            r13 = r22
            r17 = r5
            r18 = r6
        L_0x0157:
            monitor-exit(r12)     // Catch:{ all -> 0x0159 }
            throw r0
        L_0x0159:
            r0 = move-exception
            goto L_0x0157
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NativeDaemonConnector.executeForList(long, java.lang.String, java.lang.Object[]):com.android.server.NativeDaemonEvent[]");
    }

    @VisibleForTesting
    static void appendEscaped(StringBuilder builder, String arg) {
        boolean hasSpaces = arg.indexOf(32) >= 0;
        if (hasSpaces) {
            builder.append('\"');
        }
        int length = arg.length();
        for (int i = 0; i < length; i++) {
            char c = arg.charAt(i);
            if (c == '\"') {
                builder.append("\\\"");
            } else if (c == '\\') {
                builder.append("\\\\");
            } else {
                builder.append(c);
            }
        }
        if (hasSpaces) {
            builder.append('\"');
        }
    }

    private static class NativeDaemonArgumentException extends NativeDaemonConnectorException {
        public NativeDaemonArgumentException(String command, NativeDaemonEvent event) {
            super(command, event);
        }

        public IllegalArgumentException rethrowAsParcelableException() {
            throw new IllegalArgumentException(getMessage(), this);
        }
    }

    private static class NativeDaemonFailureException extends NativeDaemonConnectorException {
        public NativeDaemonFailureException(String command, NativeDaemonEvent event) {
            super(command, event);
        }
    }

    public static class Command {
        /* access modifiers changed from: private */
        public ArrayList<Object> mArguments = Lists.newArrayList();
        /* access modifiers changed from: private */
        public String mCmd;

        public Command(String cmd, Object... args) {
            this.mCmd = cmd;
            for (Object arg : args) {
                appendArg(arg);
            }
        }

        public Command appendArg(Object arg) {
            this.mArguments.add(arg);
            return this;
        }
    }

    public void monitor() {
        synchronized (this.mDaemonLock) {
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mLocalLog.dump(fd, pw, args);
        pw.println();
        this.mResponseQueue.dump(fd, pw, args);
    }

    private void log(String logstring) {
        if (this.mDebug) {
            Slog.d(this.TAG, logstring);
        }
        this.mLocalLog.log(logstring);
    }

    private void loge(String logstring) {
        Slog.e(this.TAG, logstring);
        this.mLocalLog.log(logstring);
    }

    private static class ResponseQueue {
        private int mMaxCount;
        private final LinkedList<PendingCmd> mPendingCmds = new LinkedList<>();

        private static class PendingCmd {
            public int availableResponseCount;
            public final int cmdNum;
            public final String logCmd;
            public BlockingQueue<NativeDaemonEvent> responses = new ArrayBlockingQueue(10);

            public PendingCmd(int cmdNum2, String logCmd2) {
                this.cmdNum = cmdNum2;
                this.logCmd = logCmd2;
            }
        }

        ResponseQueue(int maxCount) {
            this.mMaxCount = maxCount;
        }

        public void add(int cmdNum, NativeDaemonEvent response) {
            PendingCmd found = null;
            synchronized (this.mPendingCmds) {
                Iterator it = this.mPendingCmds.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    PendingCmd pendingCmd = (PendingCmd) it.next();
                    if (pendingCmd.cmdNum == cmdNum) {
                        found = pendingCmd;
                        break;
                    }
                }
                if (found == null) {
                    while (this.mPendingCmds.size() >= this.mMaxCount) {
                        Slog.e("NativeDaemonConnector.ResponseQueue", "more buffered than allowed: " + this.mPendingCmds.size() + " >= " + this.mMaxCount);
                        PendingCmd pendingCmd2 = this.mPendingCmds.remove();
                        Slog.e("NativeDaemonConnector.ResponseQueue", "Removing request: " + pendingCmd2.logCmd + " (" + pendingCmd2.cmdNum + ")");
                    }
                    found = new PendingCmd(cmdNum, (String) null);
                    this.mPendingCmds.add(found);
                }
                found.availableResponseCount++;
                if (found.availableResponseCount == 0) {
                    this.mPendingCmds.remove(found);
                }
            }
            try {
                found.responses.put(response);
            } catch (InterruptedException e) {
            }
        }

        public NativeDaemonEvent remove(int cmdNum, long timeoutMs, String logCmd) {
            PendingCmd found = null;
            synchronized (this.mPendingCmds) {
                Iterator it = this.mPendingCmds.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    PendingCmd pendingCmd = (PendingCmd) it.next();
                    if (pendingCmd.cmdNum == cmdNum) {
                        found = pendingCmd;
                        break;
                    }
                }
                if (found == null) {
                    found = new PendingCmd(cmdNum, logCmd);
                    this.mPendingCmds.add(found);
                }
                found.availableResponseCount--;
                if (found.availableResponseCount == 0) {
                    this.mPendingCmds.remove(found);
                }
            }
            NativeDaemonEvent result = null;
            try {
                result = found.responses.poll(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            if (result == null) {
                Slog.e("NativeDaemonConnector.ResponseQueue", "Timeout waiting for response");
            }
            return result;
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            pw.println("Pending requests:");
            synchronized (this.mPendingCmds) {
                Iterator it = this.mPendingCmds.iterator();
                while (it.hasNext()) {
                    PendingCmd pendingCmd = (PendingCmd) it.next();
                    pw.println("  Cmd " + pendingCmd.cmdNum + " - " + pendingCmd.logCmd);
                }
            }
        }
    }
}
