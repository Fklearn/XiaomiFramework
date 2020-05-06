package com.android.server.rollback;

import android.content.rollback.RollbackInfo;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.util.ArrayList;

class RollbackData {
    static final int ROLLBACK_STATE_AVAILABLE = 1;
    static final int ROLLBACK_STATE_COMMITTED = 3;
    static final int ROLLBACK_STATE_ENABLING = 0;
    public int apkSessionId = -1;
    public final File backupDir;
    public final RollbackInfo info;
    public boolean restoreUserDataInProgress = false;
    public final int stagedSessionId;
    public int state;
    public Instant timestamp;

    @Retention(RetentionPolicy.SOURCE)
    @interface RollbackState {
    }

    RollbackData(int rollbackId, File backupDir2, int stagedSessionId2) {
        this.info = new RollbackInfo(rollbackId, new ArrayList(), stagedSessionId2 != -1, new ArrayList(), -1);
        this.backupDir = backupDir2;
        this.stagedSessionId = stagedSessionId2;
        this.state = 0;
        this.timestamp = Instant.now();
    }

    RollbackData(RollbackInfo info2, File backupDir2, Instant timestamp2, int stagedSessionId2, int state2, int apkSessionId2, boolean restoreUserDataInProgress2) {
        this.info = info2;
        this.backupDir = backupDir2;
        this.timestamp = timestamp2;
        this.stagedSessionId = stagedSessionId2;
        this.state = state2;
        this.apkSessionId = apkSessionId2;
        this.restoreUserDataInProgress = restoreUserDataInProgress2;
    }

    public boolean isStaged() {
        return this.info.isStaged();
    }

    static String rollbackStateToString(int state2) {
        if (state2 == 0) {
            return "enabling";
        }
        if (state2 == 1) {
            return "available";
        }
        if (state2 == 3) {
            return "committed";
        }
        throw new AssertionError("Invalid rollback state: " + state2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0056 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int rollbackStateFromString(java.lang.String r5) throws java.text.ParseException {
        /*
            int r0 = r5.hashCode()
            r1 = -1491142788(0xffffffffa71ef77c, float:-2.2061066E-15)
            r2 = 2
            r3 = 0
            r4 = 1
            if (r0 == r1) goto L_0x002b
            r1 = -733902135(0xffffffffd4418ac9, float:-3.32502847E12)
            if (r0 == r1) goto L_0x0021
            r1 = 1642196352(0x61e1ed80, float:5.209539E20)
            if (r0 == r1) goto L_0x0017
        L_0x0016:
            goto L_0x0035
        L_0x0017:
            java.lang.String r0 = "enabling"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r3
            goto L_0x0036
        L_0x0021:
            java.lang.String r0 = "available"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r4
            goto L_0x0036
        L_0x002b:
            java.lang.String r0 = "committed"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r2
            goto L_0x0036
        L_0x0035:
            r0 = -1
        L_0x0036:
            if (r0 == 0) goto L_0x0056
            if (r0 == r4) goto L_0x0055
            if (r0 != r2) goto L_0x003e
            r0 = 3
            return r0
        L_0x003e:
            java.text.ParseException r0 = new java.text.ParseException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid rollback state: "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1, r3)
            throw r0
        L_0x0055:
            return r4
        L_0x0056:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.rollback.RollbackData.rollbackStateFromString(java.lang.String):int");
    }

    public String getStateAsString() {
        return rollbackStateToString(this.state);
    }
}
