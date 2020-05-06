package com.android.server.locksettings.recoverablekeystore.storage;

import android.os.Environment;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.Locale;

public class RecoverySnapshotStorage {
    private static final String ROOT_PATH = "system";
    private static final String STORAGE_PATH = "recoverablekeystore/snapshots/";
    private static final String TAG = "RecoverySnapshotStorage";
    @GuardedBy({"this"})
    private final SparseArray<KeyChainSnapshot> mSnapshotByUid = new SparseArray<>();
    private final File rootDirectory;

    public static RecoverySnapshotStorage newInstance() {
        return new RecoverySnapshotStorage(new File(Environment.getDataDirectory(), ROOT_PATH));
    }

    @VisibleForTesting
    public RecoverySnapshotStorage(File rootDirectory2) {
        this.rootDirectory = rootDirectory2;
    }

    public synchronized void put(int uid, KeyChainSnapshot snapshot) {
        this.mSnapshotByUid.put(uid, snapshot);
        try {
            writeToDisk(uid, snapshot);
        } catch (IOException | CertificateEncodingException e) {
            Log.e(TAG, String.format(Locale.US, "Error persisting snapshot for %d to disk", new Object[]{Integer.valueOf(uid)}), e);
        }
        return;
    }

    public synchronized KeyChainSnapshot get(int uid) {
        KeyChainSnapshot snapshot = this.mSnapshotByUid.get(uid);
        if (snapshot != null) {
            return snapshot;
        }
        try {
            return readFromDisk(uid);
        } catch (KeyChainSnapshotParserException | IOException e) {
            Log.e(TAG, String.format(Locale.US, "Error reading snapshot for %d from disk", new Object[]{Integer.valueOf(uid)}), e);
            return null;
        }
    }

    public synchronized void remove(int uid) {
        this.mSnapshotByUid.remove(uid);
        getSnapshotFile(uid).delete();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0014, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0018, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeToDisk(int r5, android.security.keystore.recovery.KeyChainSnapshot r6) throws java.io.IOException, java.security.cert.CertificateEncodingException {
        /*
            r4 = this;
            java.io.File r0 = r4.getSnapshotFile(r5)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ IOException | CertificateEncodingException -> 0x0019 }
            r1.<init>(r0)     // Catch:{ IOException | CertificateEncodingException -> 0x0019 }
            r2 = 0
            com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotSerializer.serialize(r6, r1)     // Catch:{ all -> 0x0012 }
            $closeResource(r2, r1)     // Catch:{ IOException | CertificateEncodingException -> 0x0019 }
            return
        L_0x0012:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0014 }
        L_0x0014:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ IOException | CertificateEncodingException -> 0x0019 }
            throw r3     // Catch:{ IOException | CertificateEncodingException -> 0x0019 }
        L_0x0019:
            r1 = move-exception
            r0.delete()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverySnapshotStorage.writeToDisk(int, android.security.keystore.recovery.KeyChainSnapshot):void");
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
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0014, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0018, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.security.keystore.recovery.KeyChainSnapshot readFromDisk(int r5) throws java.io.IOException, com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotParserException {
        /*
            r4 = this;
            java.io.File r0 = r4.getSnapshotFile(r5)
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ KeyChainSnapshotParserException | IOException -> 0x0019 }
            r1.<init>(r0)     // Catch:{ KeyChainSnapshotParserException | IOException -> 0x0019 }
            r2 = 0
            android.security.keystore.recovery.KeyChainSnapshot r3 = com.android.server.locksettings.recoverablekeystore.serialization.KeyChainSnapshotDeserializer.deserialize(r1)     // Catch:{ all -> 0x0012 }
            $closeResource(r2, r1)     // Catch:{ KeyChainSnapshotParserException | IOException -> 0x0019 }
            return r3
        L_0x0012:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0014 }
        L_0x0014:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ KeyChainSnapshotParserException | IOException -> 0x0019 }
            throw r3     // Catch:{ KeyChainSnapshotParserException | IOException -> 0x0019 }
        L_0x0019:
            r1 = move-exception
            r0.delete()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverySnapshotStorage.readFromDisk(int):android.security.keystore.recovery.KeyChainSnapshot");
    }

    private File getSnapshotFile(int uid) {
        return new File(getStorageFolder(), getSnapshotFileName(uid));
    }

    private String getSnapshotFileName(int uid) {
        return String.format(Locale.US, "%d.xml", new Object[]{Integer.valueOf(uid)});
    }

    private File getStorageFolder() {
        File folder = new File(this.rootDirectory, STORAGE_PATH);
        folder.mkdirs();
        return folder;
    }
}
