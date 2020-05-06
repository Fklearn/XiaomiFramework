package com.android.server.am;

import android.app.IInstrumentationWatcher;
import android.content.ComponentName;
import android.os.Bundle;
import java.util.ArrayList;

public class InstrumentationReporter {
    static final boolean DEBUG = false;
    static final int REPORT_TYPE_FINISHED = 1;
    static final int REPORT_TYPE_STATUS = 0;
    static final String TAG = "ActivityManager";
    final Object mLock = new Object();
    ArrayList<Report> mPendingReports;
    Thread mThread;

    final class MyThread extends Thread {
        public MyThread() {
            super("InstrumentationReporter");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001d, code lost:
            r0 = false;
            r1 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0023, code lost:
            if (r1 >= r2.size()) goto L_0x0005;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0025, code lost:
            r3 = r2.get(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x002d, code lost:
            if (r3.mType != 0) goto L_0x003b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
            r3.mWatcher.instrumentationStatus(r3.mName, r3.mResultCode, r3.mResults);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
            r3.mWatcher.instrumentationFinished(r3.mName, r3.mResultCode, r3.mResults);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
            android.util.Slog.i(com.android.server.am.InstrumentationReporter.TAG, "Failure reporting to instrumentation watcher: comp=" + r3.mName + " results=" + r3.mResults);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r8 = this;
                r0 = 0
                android.os.Process.setThreadPriority(r0)
                r0 = 0
            L_0x0005:
                com.android.server.am.InstrumentationReporter r1 = com.android.server.am.InstrumentationReporter.this
                java.lang.Object r1 = r1.mLock
                monitor-enter(r1)
                com.android.server.am.InstrumentationReporter r2 = com.android.server.am.InstrumentationReporter.this     // Catch:{ all -> 0x0084 }
                java.util.ArrayList<com.android.server.am.InstrumentationReporter$Report> r2 = r2.mPendingReports     // Catch:{ all -> 0x0084 }
                com.android.server.am.InstrumentationReporter r3 = com.android.server.am.InstrumentationReporter.this     // Catch:{ all -> 0x0084 }
                r4 = 0
                r3.mPendingReports = r4     // Catch:{ all -> 0x0084 }
                if (r2 == 0) goto L_0x006e
                boolean r3 = r2.isEmpty()     // Catch:{ all -> 0x0084 }
                if (r3 == 0) goto L_0x001c
                goto L_0x006e
            L_0x001c:
                monitor-exit(r1)     // Catch:{ all -> 0x0084 }
                r0 = 0
                r1 = 0
            L_0x001f:
                int r3 = r2.size()
                if (r1 >= r3) goto L_0x006d
                java.lang.Object r3 = r2.get(r1)
                com.android.server.am.InstrumentationReporter$Report r3 = (com.android.server.am.InstrumentationReporter.Report) r3
                int r4 = r3.mType     // Catch:{ RemoteException -> 0x0047 }
                if (r4 != 0) goto L_0x003b
                android.app.IInstrumentationWatcher r4 = r3.mWatcher     // Catch:{ RemoteException -> 0x0047 }
                android.content.ComponentName r5 = r3.mName     // Catch:{ RemoteException -> 0x0047 }
                int r6 = r3.mResultCode     // Catch:{ RemoteException -> 0x0047 }
                android.os.Bundle r7 = r3.mResults     // Catch:{ RemoteException -> 0x0047 }
                r4.instrumentationStatus(r5, r6, r7)     // Catch:{ RemoteException -> 0x0047 }
                goto L_0x0046
            L_0x003b:
                android.app.IInstrumentationWatcher r4 = r3.mWatcher     // Catch:{ RemoteException -> 0x0047 }
                android.content.ComponentName r5 = r3.mName     // Catch:{ RemoteException -> 0x0047 }
                int r6 = r3.mResultCode     // Catch:{ RemoteException -> 0x0047 }
                android.os.Bundle r7 = r3.mResults     // Catch:{ RemoteException -> 0x0047 }
                r4.instrumentationFinished(r5, r6, r7)     // Catch:{ RemoteException -> 0x0047 }
            L_0x0046:
                goto L_0x006a
            L_0x0047:
                r4 = move-exception
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Failure reporting to instrumentation watcher: comp="
                r5.append(r6)
                android.content.ComponentName r6 = r3.mName
                r5.append(r6)
                java.lang.String r6 = " results="
                r5.append(r6)
                android.os.Bundle r6 = r3.mResults
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                java.lang.String r6 = "ActivityManager"
                android.util.Slog.i(r6, r5)
            L_0x006a:
                int r1 = r1 + 1
                goto L_0x001f
            L_0x006d:
                goto L_0x0005
            L_0x006e:
                if (r0 != 0) goto L_0x007e
                com.android.server.am.InstrumentationReporter r3 = com.android.server.am.InstrumentationReporter.this     // Catch:{ InterruptedException -> 0x007a }
                java.lang.Object r3 = r3.mLock     // Catch:{ InterruptedException -> 0x007a }
                r4 = 10000(0x2710, double:4.9407E-320)
                r3.wait(r4)     // Catch:{ InterruptedException -> 0x007a }
                goto L_0x007b
            L_0x007a:
                r3 = move-exception
            L_0x007b:
                r0 = 1
                monitor-exit(r1)     // Catch:{ all -> 0x0084 }
                goto L_0x0005
            L_0x007e:
                com.android.server.am.InstrumentationReporter r3 = com.android.server.am.InstrumentationReporter.this     // Catch:{ all -> 0x0084 }
                r3.mThread = r4     // Catch:{ all -> 0x0084 }
                monitor-exit(r1)     // Catch:{ all -> 0x0084 }
                return
            L_0x0084:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0084 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.InstrumentationReporter.MyThread.run():void");
        }
    }

    final class Report {
        final ComponentName mName;
        final int mResultCode;
        final Bundle mResults;
        final int mType;
        final IInstrumentationWatcher mWatcher;

        Report(int type, IInstrumentationWatcher watcher, ComponentName name, int resultCode, Bundle results) {
            this.mType = type;
            this.mWatcher = watcher;
            this.mName = name;
            this.mResultCode = resultCode;
            this.mResults = results;
        }
    }

    public void reportStatus(IInstrumentationWatcher watcher, ComponentName name, int resultCode, Bundle results) {
        report(new Report(0, watcher, name, resultCode, results));
    }

    public void reportFinished(IInstrumentationWatcher watcher, ComponentName name, int resultCode, Bundle results) {
        report(new Report(1, watcher, name, resultCode, results));
    }

    private void report(Report report) {
        synchronized (this.mLock) {
            if (this.mThread == null) {
                this.mThread = new MyThread();
                this.mThread.start();
            }
            if (this.mPendingReports == null) {
                this.mPendingReports = new ArrayList<>();
            }
            this.mPendingReports.add(report);
            this.mLock.notifyAll();
        }
    }
}
