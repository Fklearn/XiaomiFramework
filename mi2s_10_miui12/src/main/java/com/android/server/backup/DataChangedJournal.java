package com.android.server.backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DataChangedJournal {
    private static final int BUFFER_SIZE_BYTES = 8192;
    private static final String FILE_NAME_PREFIX = "journal";
    private final File mFile;

    DataChangedJournal(File file) {
        this.mFile = file;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001f, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
        $closeResource(r1, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addPackage(java.lang.String r4) throws java.io.IOException {
        /*
            r3 = this;
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile
            java.io.File r1 = r3.mFile
            java.lang.String r2 = "rws"
            r0.<init>(r1, r2)
            long r1 = r0.length()     // Catch:{ all -> 0x0019 }
            r0.seek(r1)     // Catch:{ all -> 0x0019 }
            r0.writeUTF(r4)     // Catch:{ all -> 0x0019 }
            r1 = 0
            $closeResource(r1, r0)
            return
        L_0x0019:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001b }
        L_0x001b:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.DataChangedJournal.addPackage(java.lang.String):void");
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

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002d, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0031, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0034, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0035, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0038, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void forEach(java.util.function.Consumer<java.lang.String> r5) throws java.io.IOException {
        /*
            r4 = this;
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream
            java.io.FileInputStream r1 = new java.io.FileInputStream
            java.io.File r2 = r4.mFile
            r1.<init>(r2)
            r2 = 8192(0x2000, float:1.14794E-41)
            r0.<init>(r1, r2)
            java.io.DataInputStream r1 = new java.io.DataInputStream     // Catch:{ all -> 0x0032 }
            r1.<init>(r0)     // Catch:{ all -> 0x0032 }
        L_0x0015:
            int r2 = r1.available()     // Catch:{ all -> 0x002b }
            if (r2 <= 0) goto L_0x0023
            java.lang.String r2 = r1.readUTF()     // Catch:{ all -> 0x002b }
            r5.accept(r2)     // Catch:{ all -> 0x002b }
            goto L_0x0015
        L_0x0023:
            r2 = 0
            $closeResource(r2, r1)     // Catch:{ all -> 0x0032 }
            $closeResource(r2, r0)
            return
        L_0x002b:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x002d }
        L_0x002d:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ all -> 0x0032 }
            throw r3     // Catch:{ all -> 0x0032 }
        L_0x0032:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0034 }
        L_0x0034:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.DataChangedJournal.forEach(java.util.function.Consumer):void");
    }

    public List<String> getPackages() throws IOException {
        List<String> packages = new ArrayList<>();
        Objects.requireNonNull(packages);
        forEach(new Consumer(packages) {
            private final /* synthetic */ List f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.add((String) obj);
            }
        });
        return packages;
    }

    public boolean delete() {
        return this.mFile.delete();
    }

    public boolean equals(Object object) {
        if (!(object instanceof DataChangedJournal)) {
            return false;
        }
        try {
            return this.mFile.getCanonicalPath().equals(((DataChangedJournal) object).mFile.getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

    public String toString() {
        return this.mFile.toString();
    }

    static DataChangedJournal newJournal(File journalDirectory) throws IOException {
        return new DataChangedJournal(File.createTempFile(FILE_NAME_PREFIX, (String) null, journalDirectory));
    }

    static ArrayList<DataChangedJournal> listJournals(File journalDirectory) {
        ArrayList<DataChangedJournal> journals = new ArrayList<>();
        for (File file : journalDirectory.listFiles()) {
            journals.add(new DataChangedJournal(file));
        }
        return journals;
    }
}
