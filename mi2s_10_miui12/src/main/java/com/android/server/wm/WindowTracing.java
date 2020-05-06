package com.android.server.wm;

import android.os.Build;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.Choreographer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

class WindowTracing {
    private static final int BUFFER_CAPACITY_ALL = 4194304;
    private static final int BUFFER_CAPACITY_CRITICAL = 524288;
    private static final int BUFFER_CAPACITY_TRIM = 2097152;
    private static final String TAG = "WindowTracing";
    private static final String TRACE_FILENAME = "/data/misc/wmtrace/wm_trace.pb";
    private final WindowTraceBuffer mBuffer;
    private final Choreographer mChoreographer;
    private boolean mEnabled;
    private final Object mEnabledLock;
    private volatile boolean mEnabledLockFree;
    private final Choreographer.FrameCallback mFrameCallback;
    private final WindowManagerGlobalLock mGlobalLock;
    private int mLogLevel;
    private boolean mLogOnFrame;
    private boolean mScheduled;
    private final WindowManagerService mService;
    private final File mTraceFile;

    public /* synthetic */ void lambda$new$0$WindowTracing(long frameTimeNanos) {
        log("onFrame");
    }

    static WindowTracing createDefaultAndStartLooper(WindowManagerService service, Choreographer choreographer) {
        return new WindowTracing(new File(TRACE_FILENAME), service, choreographer, 2097152);
    }

    private WindowTracing(File file, WindowManagerService service, Choreographer choreographer, int bufferCapacity) {
        this(file, service, choreographer, service.mGlobalLock, bufferCapacity);
    }

    WindowTracing(File file, WindowManagerService service, Choreographer choreographer, WindowManagerGlobalLock globalLock, int bufferCapacity) {
        this.mEnabledLock = new Object();
        this.mFrameCallback = new Choreographer.FrameCallback() {
            public final void doFrame(long j) {
                WindowTracing.this.lambda$new$0$WindowTracing(j);
            }
        };
        this.mLogLevel = 1;
        this.mLogOnFrame = false;
        this.mChoreographer = choreographer;
        this.mService = service;
        this.mGlobalLock = globalLock;
        this.mTraceFile = file;
        this.mBuffer = new WindowTraceBuffer(bufferCapacity);
        setLogLevel(1, (PrintWriter) null);
    }

    /* access modifiers changed from: package-private */
    public void startTrace(PrintWriter pw) {
        if (Build.IS_USER) {
            logAndPrintln(pw, "Error: Tracing is not supported on user builds.");
            return;
        }
        synchronized (this.mEnabledLock) {
            logAndPrintln(pw, "Start tracing to " + this.mTraceFile + ".");
            this.mBuffer.resetBuffer();
            this.mEnabledLockFree = true;
            this.mEnabled = true;
        }
        log("trace.enable");
    }

    /* access modifiers changed from: package-private */
    public void stopTrace(PrintWriter pw) {
        stopTrace(pw, true);
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: package-private */
    public void stopTrace(PrintWriter pw, boolean writeToFile) {
        if (Build.IS_USER) {
            logAndPrintln(pw, "Error: Tracing is not supported on user builds.");
            return;
        }
        synchronized (this.mEnabledLock) {
            logAndPrintln(pw, "Stop tracing to " + this.mTraceFile + ". Waiting for traces to flush.");
            this.mEnabledLockFree = false;
            this.mEnabled = false;
            if (this.mEnabled) {
                logAndPrintln(pw, "ERROR: tracing was re-enabled while waiting for flush.");
                throw new IllegalStateException("tracing enabled while waiting for flush.");
            } else if (writeToFile) {
                writeTraceToFileLocked();
                logAndPrintln(pw, "Trace written to " + this.mTraceFile + ".");
            }
        }
    }

    private void setLogLevel(int logLevel, PrintWriter pw) {
        logAndPrintln(pw, "Setting window tracing log level to " + logLevel);
        this.mLogLevel = logLevel;
        if (logLevel == 0) {
            setBufferCapacity(4194304, pw);
        } else if (logLevel == 1) {
            setBufferCapacity(2097152, pw);
        } else if (logLevel == 2) {
            setBufferCapacity(524288, pw);
        }
    }

    private void setLogFrequency(boolean onFrame, PrintWriter pw) {
        StringBuilder sb = new StringBuilder();
        sb.append("Setting window tracing log frequency to ");
        sb.append(onFrame ? "frame" : "transaction");
        logAndPrintln(pw, sb.toString());
        this.mLogOnFrame = onFrame;
    }

    private void setBufferCapacity(int capacity, PrintWriter pw) {
        logAndPrintln(pw, "Setting window tracing buffer capacity to " + capacity + "bytes");
        this.mBuffer.setCapacity(capacity);
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabled() {
        return this.mEnabledLockFree;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onShellCommand(android.os.ShellCommand r10) {
        /*
            r9 = this;
            java.io.PrintWriter r0 = r10.getOutPrintWriter()
            java.lang.String r1 = r10.getNextArgRequired()
            int r2 = r1.hashCode()
            r3 = -1
            r4 = 2
            r5 = 1
            r6 = 0
            switch(r2) {
                case -892481550: goto L_0x0050;
                case 3530753: goto L_0x0046;
                case 3540994: goto L_0x003c;
                case 97692013: goto L_0x0032;
                case 102865796: goto L_0x0028;
                case 109757538: goto L_0x001e;
                case 2141246174: goto L_0x0014;
                default: goto L_0x0013;
            }
        L_0x0013:
            goto L_0x005a
        L_0x0014:
            java.lang.String r2 = "transaction"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = 4
            goto L_0x005b
        L_0x001e:
            java.lang.String r2 = "start"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = r6
            goto L_0x005b
        L_0x0028:
            java.lang.String r2 = "level"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = 5
            goto L_0x005b
        L_0x0032:
            java.lang.String r2 = "frame"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = 3
            goto L_0x005b
        L_0x003c:
            java.lang.String r2 = "stop"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = r5
            goto L_0x005b
        L_0x0046:
            java.lang.String r2 = "size"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = 6
            goto L_0x005b
        L_0x0050:
            java.lang.String r2 = "status"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0013
            r2 = r4
            goto L_0x005b
        L_0x005a:
            r2 = r3
        L_0x005b:
            switch(r2) {
                case 0: goto L_0x0135;
                case 1: goto L_0x0131;
                case 2: goto L_0x0129;
                case 3: goto L_0x0120;
                case 4: goto L_0x0117;
                case 5: goto L_0x00c2;
                case 6: goto L_0x00af;
                default: goto L_0x005e;
            }
        L_0x005e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Unknown command: "
            r2.append(r4)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            r0.println(r2)
            java.lang.String r2 = "Window manager trace options:"
            r0.println(r2)
            java.lang.String r2 = "  start: Start logging"
            r0.println(r2)
            java.lang.String r2 = "  stop: Stop logging"
            r0.println(r2)
            java.lang.String r2 = "  frame: Log trace once per frame"
            r0.println(r2)
            java.lang.String r2 = "  transaction: Log each transaction"
            r0.println(r2)
            java.lang.String r2 = "  size: Set the maximum log size (in KB)"
            r0.println(r2)
            java.lang.String r2 = "  status: Print trace status"
            r0.println(r2)
            java.lang.String r2 = "  level [lvl]: Set the log level between"
            r0.println(r2)
            java.lang.String r2 = "    lvl may be one of:"
            r0.println(r2)
            java.lang.String r2 = "      critical: Only visible windows with reduced information"
            r0.println(r2)
            java.lang.String r2 = "      trim: All windows with reduced"
            r0.println(r2)
            java.lang.String r2 = "      all: All window and information"
            r0.println(r2)
            return r3
        L_0x00af:
            java.lang.String r2 = r10.getNextArgRequired()
            int r2 = java.lang.Integer.parseInt(r2)
            int r2 = r2 * 1024
            r9.setBufferCapacity(r2, r0)
            com.android.server.wm.WindowTraceBuffer r2 = r9.mBuffer
            r2.resetBuffer()
            return r6
        L_0x00c2:
            java.lang.String r2 = r10.getNextArgRequired()
            java.lang.String r2 = r2.toLowerCase()
            int r7 = r2.hashCode()
            r8 = 96673(0x179a1, float:1.35468E-40)
            if (r7 == r8) goto L_0x00f2
            r8 = 3568674(0x367422, float:5.000777E-39)
            if (r7 == r8) goto L_0x00e8
            r8 = 1952151455(0x745b779f, float:6.9551954E31)
            if (r7 == r8) goto L_0x00de
        L_0x00dd:
            goto L_0x00fb
        L_0x00de:
            java.lang.String r7 = "critical"
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x00dd
            r3 = r4
            goto L_0x00fb
        L_0x00e8:
            java.lang.String r7 = "trim"
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x00dd
            r3 = r5
            goto L_0x00fb
        L_0x00f2:
            java.lang.String r7 = "all"
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x00dd
            r3 = r6
        L_0x00fb:
            if (r3 == 0) goto L_0x010d
            if (r3 == r5) goto L_0x0109
            if (r3 == r4) goto L_0x0105
            r9.setLogLevel(r5, r0)
            goto L_0x0111
        L_0x0105:
            r9.setLogLevel(r4, r0)
            goto L_0x0111
        L_0x0109:
            r9.setLogLevel(r5, r0)
            goto L_0x0111
        L_0x010d:
            r9.setLogLevel(r6, r0)
        L_0x0111:
            com.android.server.wm.WindowTraceBuffer r3 = r9.mBuffer
            r3.resetBuffer()
            return r6
        L_0x0117:
            r9.setLogFrequency(r6, r0)
            com.android.server.wm.WindowTraceBuffer r2 = r9.mBuffer
            r2.resetBuffer()
            return r6
        L_0x0120:
            r9.setLogFrequency(r5, r0)
            com.android.server.wm.WindowTraceBuffer r2 = r9.mBuffer
            r2.resetBuffer()
            return r6
        L_0x0129:
            java.lang.String r2 = r9.getStatus()
            r9.logAndPrintln(r0, r2)
            return r6
        L_0x0131:
            r9.stopTrace(r0)
            return r6
        L_0x0135:
            r9.startTrace(r0)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowTracing.onShellCommand(android.os.ShellCommand):int");
    }

    /* access modifiers changed from: package-private */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status: ");
        sb.append(isEnabled() ? "Enabled" : "Disabled");
        sb.append("\nLog level: ");
        sb.append(this.mLogLevel);
        sb.append("\n");
        sb.append(this.mBuffer.getStatus());
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public void logState(String where) {
        if (isEnabled()) {
            if (this.mLogOnFrame) {
                schedule();
            } else {
                log(where);
            }
        }
    }

    private void schedule() {
        if (!this.mScheduled) {
            this.mScheduled = true;
            this.mChoreographer.postFrameCallback(this.mFrameCallback);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private void log(String where) {
        Trace.traceBegin(32, "traceStateLocked");
        try {
            ProtoOutputStream os = new ProtoOutputStream();
            long tokenOuter = os.start(2246267895810L);
            os.write(1125281431553L, SystemClock.elapsedRealtimeNanos());
            os.write(1138166333442L, where);
            long tokenInner = os.start(1146756268035L);
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Trace.traceBegin(32, "writeToProtoLocked");
                    this.mService.writeToProtoLocked(os, this.mLogLevel);
                    Trace.traceEnd(32);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            os.end(tokenInner);
            os.end(tokenOuter);
            this.mBuffer.add(os);
            this.mScheduled = false;
        } catch (Exception e) {
            try {
                Log.wtf(TAG, "Exception while tracing state", e);
            } catch (Throwable os2) {
                Trace.traceEnd(32);
                throw os2;
            }
        }
        Trace.traceEnd(32);
    }

    /* access modifiers changed from: package-private */
    public void writeTraceToFile() {
        synchronized (this.mEnabledLock) {
            writeTraceToFileLocked();
        }
    }

    private void logAndPrintln(PrintWriter pw, String msg) {
        Log.i(TAG, msg);
        if (pw != null) {
            pw.println(msg);
            pw.flush();
        }
    }

    private void writeTraceToFileLocked() {
        try {
            Trace.traceBegin(32, "writeTraceToFileLocked");
            this.mBuffer.writeTraceToFile(this.mTraceFile);
        } catch (IOException e) {
            Log.e(TAG, "Unable to write buffer to file", e);
        } catch (Throwable th) {
            Trace.traceEnd(32);
            throw th;
        }
        Trace.traceEnd(32);
    }
}
