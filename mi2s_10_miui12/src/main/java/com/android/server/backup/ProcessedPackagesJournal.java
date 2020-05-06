package com.android.server.backup;

import com.android.internal.annotations.GuardedBy;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

final class ProcessedPackagesJournal {
    private static final boolean DEBUG = true;
    private static final String JOURNAL_FILE_NAME = "processed";
    private static final String TAG = "ProcessedPackagesJournal";
    @GuardedBy({"mProcessedPackages"})
    private final Set<String> mProcessedPackages = new HashSet();
    private final File mStateDirectory;

    ProcessedPackagesJournal(File stateDirectory) {
        this.mStateDirectory = stateDirectory;
    }

    /* access modifiers changed from: package-private */
    public void init() {
        synchronized (this.mProcessedPackages) {
            loadFromDisk();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasBeenProcessed(String packageName) {
        boolean contains;
        synchronized (this.mProcessedPackages) {
            contains = this.mProcessedPackages.contains(packageName);
        }
        return contains;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0034, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addPackage(java.lang.String r7) {
        /*
            r6 = this;
            java.util.Set<java.lang.String> r0 = r6.mProcessedPackages
            monitor-enter(r0)
            java.util.Set<java.lang.String> r1 = r6.mProcessedPackages     // Catch:{ all -> 0x0056 }
            boolean r1 = r1.add(r7)     // Catch:{ all -> 0x0056 }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            return
        L_0x000d:
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x0056 }
            java.io.File r2 = r6.mStateDirectory     // Catch:{ all -> 0x0056 }
            java.lang.String r3 = "processed"
            r1.<init>(r2, r3)     // Catch:{ all -> 0x0056 }
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0035 }
            java.lang.String r3 = "rws"
            r2.<init>(r1, r3)     // Catch:{ IOException -> 0x0035 }
            r3 = 0
            long r4 = r2.length()     // Catch:{ all -> 0x002e }
            r2.seek(r4)     // Catch:{ all -> 0x002e }
            r2.writeUTF(r7)     // Catch:{ all -> 0x002e }
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0035 }
            goto L_0x0054
        L_0x002e:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0030 }
        L_0x0030:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ IOException -> 0x0035 }
            throw r4     // Catch:{ IOException -> 0x0035 }
        L_0x0035:
            r2 = move-exception
            java.lang.String r3 = "ProcessedPackagesJournal"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
            r4.<init>()     // Catch:{ all -> 0x0056 }
            java.lang.String r5 = "Can't log backup of "
            r4.append(r5)     // Catch:{ all -> 0x0056 }
            r4.append(r7)     // Catch:{ all -> 0x0056 }
            java.lang.String r5 = " to "
            r4.append(r5)     // Catch:{ all -> 0x0056 }
            r4.append(r1)     // Catch:{ all -> 0x0056 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0056 }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0056 }
        L_0x0054:
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            return
        L_0x0056:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.ProcessedPackagesJournal.addPackage(java.lang.String):void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* access modifiers changed from: package-private */
    public Set<String> getPackagesCopy() {
        HashSet hashSet;
        synchronized (this.mProcessedPackages) {
            hashSet = new HashSet(this.mProcessedPackages);
        }
        return hashSet;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        synchronized (this.mProcessedPackages) {
            this.mProcessedPackages.clear();
            new File(this.mStateDirectory, JOURNAL_FILE_NAME).delete();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0043, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0047, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadFromDisk() {
        /*
            r6 = this;
            java.lang.String r0 = "ProcessedPackagesJournal"
            java.io.File r1 = new java.io.File
            java.io.File r2 = r6.mStateDirectory
            java.lang.String r3 = "processed"
            r1.<init>(r2, r3)
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x0013
            return
        L_0x0013:
            java.io.DataInputStream r2 = new java.io.DataInputStream     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            r4.<init>(r1)     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            r3.<init>(r4)     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            r2.<init>(r3)     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
        L_0x0022:
            java.lang.String r3 = r2.readUTF()     // Catch:{ all -> 0x0041 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0041 }
            r4.<init>()     // Catch:{ all -> 0x0041 }
            java.lang.String r5 = "   + "
            r4.append(r5)     // Catch:{ all -> 0x0041 }
            r4.append(r3)     // Catch:{ all -> 0x0041 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0041 }
            android.util.Slog.v(r0, r4)     // Catch:{ all -> 0x0041 }
            java.util.Set<java.lang.String> r4 = r6.mProcessedPackages     // Catch:{ all -> 0x0041 }
            r4.add(r3)     // Catch:{ all -> 0x0041 }
            goto L_0x0022
        L_0x0041:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0043 }
        L_0x0043:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
            throw r4     // Catch:{ EOFException -> 0x004f, IOException -> 0x0048 }
        L_0x0048:
            r2 = move-exception
            java.lang.String r3 = "Error reading processed packages journal"
            android.util.Slog.e(r0, r3, r2)
            goto L_0x0051
        L_0x004f:
            r0 = move-exception
        L_0x0051:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.ProcessedPackagesJournal.loadFromDisk():void");
    }
}
