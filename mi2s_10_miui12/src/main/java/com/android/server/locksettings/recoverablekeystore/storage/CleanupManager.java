package com.android.server.locksettings.recoverablekeystore.storage;

import android.content.Context;
import android.os.ServiceSpecificException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CleanupManager {
    private static final String TAG = "CleanupManager";
    private final ApplicationKeyStorage mApplicationKeyStorage;
    private final Context mContext;
    private final RecoverableKeyStoreDb mDatabase;
    private Map<Integer, Long> mSerialNumbers;
    private final RecoverySnapshotStorage mSnapshotStorage;
    private final UserManager mUserManager;

    public static CleanupManager getInstance(Context context, RecoverySnapshotStorage snapshotStorage, RecoverableKeyStoreDb recoverableKeyStoreDb, ApplicationKeyStorage applicationKeyStorage) {
        return new CleanupManager(context, snapshotStorage, recoverableKeyStoreDb, UserManager.get(context), applicationKeyStorage);
    }

    @VisibleForTesting
    CleanupManager(Context context, RecoverySnapshotStorage snapshotStorage, RecoverableKeyStoreDb recoverableKeyStoreDb, UserManager userManager, ApplicationKeyStorage applicationKeyStorage) {
        this.mContext = context;
        this.mSnapshotStorage = snapshotStorage;
        this.mDatabase = recoverableKeyStoreDb;
        this.mUserManager = userManager;
        this.mApplicationKeyStorage = applicationKeyStorage;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void registerRecoveryAgent(int r6, int r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            java.util.Map<java.lang.Integer, java.lang.Long> r0 = r5.mSerialNumbers     // Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x0008
            r5.verifyKnownUsers()     // Catch:{ all -> 0x003a }
        L_0x0008:
            java.util.Map<java.lang.Integer, java.lang.Long> r0 = r5.mSerialNumbers     // Catch:{ all -> 0x003a }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x003a }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x003a }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ all -> 0x003a }
            r1 = -1
            if (r0 != 0) goto L_0x001d
            java.lang.Long r3 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x003a }
            r0 = r3
        L_0x001d:
            long r3 = r0.longValue()     // Catch:{ all -> 0x003a }
            int r3 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0027
            monitor-exit(r5)
            return
        L_0x0027:
            android.os.UserManager r3 = r5.mUserManager     // Catch:{ all -> 0x003a }
            android.os.UserHandle r4 = android.os.UserHandle.of(r6)     // Catch:{ all -> 0x003a }
            long r3 = r3.getSerialNumberForUser(r4)     // Catch:{ all -> 0x003a }
            int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x0038
            r5.storeUserSerialNumber(r6, r3)     // Catch:{ all -> 0x003a }
        L_0x0038:
            monitor-exit(r5)
            return
        L_0x003a:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.CleanupManager.registerRecoveryAgent(int, int):void");
    }

    public synchronized void verifyKnownUsers() {
        this.mSerialNumbers = this.mDatabase.getUserSerialNumbers();
        List<Integer> deletedUserIds = new ArrayList<Integer>() {
        };
        for (Map.Entry<Integer, Long> entry : this.mSerialNumbers.entrySet()) {
            Integer userId = entry.getKey();
            Long storedSerialNumber = entry.getValue();
            if (storedSerialNumber == null) {
                storedSerialNumber = -1L;
            }
            long currentSerialNumber = this.mUserManager.getSerialNumberForUser(UserHandle.of(userId.intValue()));
            if (currentSerialNumber == -1) {
                deletedUserIds.add(userId);
                removeDataForUser(userId.intValue());
            } else if (storedSerialNumber.longValue() == -1) {
                storeUserSerialNumber(userId.intValue(), currentSerialNumber);
            } else if (storedSerialNumber.longValue() != currentSerialNumber) {
                deletedUserIds.add(userId);
                removeDataForUser(userId.intValue());
                storeUserSerialNumber(userId.intValue(), currentSerialNumber);
            }
        }
        for (Integer deletedUser : deletedUserIds) {
            this.mSerialNumbers.remove(deletedUser);
        }
    }

    private void storeUserSerialNumber(int userId, long userSerialNumber) {
        Log.d(TAG, "Storing serial number for user " + userId + ".");
        this.mSerialNumbers.put(Integer.valueOf(userId), Long.valueOf(userSerialNumber));
        this.mDatabase.setUserSerialNumber(userId, userSerialNumber);
    }

    private void removeDataForUser(int userId) {
        Log.d(TAG, "Removing data for user " + userId + ".");
        for (Integer uid : this.mDatabase.getRecoveryAgents(userId)) {
            this.mSnapshotStorage.remove(uid.intValue());
            removeAllKeysForRecoveryAgent(userId, uid.intValue());
        }
        this.mDatabase.removeUserFromAllTables(userId);
    }

    private void removeAllKeysForRecoveryAgent(int userId, int uid) {
        for (String alias : this.mDatabase.getAllKeys(userId, uid, this.mDatabase.getPlatformKeyGenerationId(userId)).keySet()) {
            try {
                this.mApplicationKeyStorage.deleteEntry(userId, uid, alias);
            } catch (ServiceSpecificException e) {
                Log.e(TAG, "Error while removing recoverable key " + alias + " : " + e);
            }
        }
    }
}
