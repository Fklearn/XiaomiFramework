package com.android.server.backup.utils;

import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import com.android.internal.annotations.GuardedBy;

public class RestoreUtils {
    /* JADX WARNING: Removed duplicated region for block: B:112:0x025c A[SYNTHETIC, Splitter:B:112:0x025c] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean installApk(java.io.InputStream r24, android.content.Context r25, com.android.server.backup.restore.RestoreDeleteObserver r26, java.util.HashMap<java.lang.String, android.content.pm.Signature[]> r27, java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r28, com.android.server.backup.FileMetadata r29, java.lang.String r30, com.android.server.backup.utils.BytesReadListener r31, int r32) {
        /*
            r1 = r29
            r2 = 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Installing from backup: "
            r0.append(r3)
            java.lang.String r3 = r1.packageName
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BackupManagerService"
            android.util.Slog.d(r3, r0)
            r4 = 0
            r5 = 0
            com.android.server.backup.utils.RestoreUtils$LocalIntentReceiver r0 = new com.android.server.backup.utils.RestoreUtils$LocalIntentReceiver     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r6 = 0
            r0.<init>()     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r6 = r0
            android.content.pm.PackageManager r0 = r25.getPackageManager()     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r7 = r0
            android.content.pm.PackageInstaller r0 = r7.getPackageInstaller()     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r8 = r0
            android.content.pm.PackageInstaller$SessionParams r0 = new android.content.pm.PackageInstaller$SessionParams     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r9 = 1
            r0.<init>(r9)     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r10 = r0
            r11 = r30
            r10.setInstallerPackageName(r11)     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            int r0 = r8.createSession(r10)     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r12 = r0
            android.content.pm.PackageInstaller$Session r0 = r8.openSession(r12)     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r4 = r0
            java.lang.String r14 = r1.packageName     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r15 = 0
            r20 = r10
            long r9 = r1.size     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r13 = r4
            r17 = r9
            java.io.OutputStream r0 = r13.openWrite(r14, r15, r17)     // Catch:{ IOException -> 0x02b4, all -> 0x02aa }
            r5 = r0
            r9 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r9]     // Catch:{ IOException -> 0x02a0, all -> 0x0296 }
            r10 = r0
            long r13 = r1.size     // Catch:{ IOException -> 0x02a0, all -> 0x0296 }
        L_0x005c:
            r15 = 0
            int r0 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            r15 = 0
            if (r0 <= 0) goto L_0x00d1
            int r0 = r10.length     // Catch:{ IOException -> 0x00c4, all -> 0x00b9 }
            r17 = r10
            long r9 = (long) r0
            int r0 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x0082
            r9 = r17
            int r0 = r9.length     // Catch:{ IOException -> 0x007b, all -> 0x0070 }
            long r10 = (long) r0
            goto L_0x0085
        L_0x0070:
            r0 = move-exception
            r6 = r26
            r15 = r31
            r17 = r2
            r21 = r5
            goto L_0x02cc
        L_0x007b:
            r0 = move-exception
            r6 = r26
            r15 = r31
            goto L_0x02bb
        L_0x0082:
            r9 = r17
            r10 = r13
        L_0x0085:
            int r0 = (int) r10
            r17 = r2
            r2 = r24
            int r0 = r2.read(r9, r15, r0)     // Catch:{ IOException -> 0x00b5, all -> 0x00ac }
            if (r0 < 0) goto L_0x00a7
            r21 = r10
            long r10 = (long) r0
            r15 = r31
            r15.onBytesRead(r10)     // Catch:{ IOException -> 0x00e3, all -> 0x00dc }
            r10 = 0
            r5.write(r9, r10, r0)     // Catch:{ IOException -> 0x00e3, all -> 0x00dc }
            long r10 = (long) r0
            long r13 = r13 - r10
            r11 = r30
            r10 = r9
            r2 = r17
            r9 = 32768(0x8000, float:4.5918E-41)
            goto L_0x005c
        L_0x00a7:
            r15 = r31
            r21 = r10
            goto L_0x00d8
        L_0x00ac:
            r0 = move-exception
            r15 = r31
            r6 = r26
            r21 = r5
            goto L_0x02cc
        L_0x00b5:
            r0 = move-exception
            r15 = r31
            goto L_0x00e4
        L_0x00b9:
            r0 = move-exception
            r15 = r31
            r17 = r2
            r2 = r24
            r6 = r26
            goto L_0x02b1
        L_0x00c4:
            r0 = move-exception
            r15 = r31
            r17 = r2
            r2 = r24
            r6 = r26
            r2 = r17
            goto L_0x02bb
        L_0x00d1:
            r15 = r31
            r17 = r2
            r9 = r10
            r2 = r24
        L_0x00d8:
            r5.close()     // Catch:{ Exception -> 0x00ea }
            goto L_0x00f2
        L_0x00dc:
            r0 = move-exception
            r6 = r26
            r21 = r5
            goto L_0x02cc
        L_0x00e3:
            r0 = move-exception
        L_0x00e4:
            r6 = r26
            r2 = r17
            goto L_0x02bb
        L_0x00ea:
            r0 = move-exception
            r10 = r0
            r0 = r10
            java.lang.String r10 = "apkStream close failed, ignore"
            android.util.Slog.d(r3, r10)     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
        L_0x00f2:
            android.content.IntentSender r0 = r6.getIntentSender()     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            r4.commit(r0)     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            android.content.Intent r0 = r6.getResult()     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            r10 = r0
            java.lang.String r0 = "android.content.pm.extra.STATUS"
            r11 = 1
            int r0 = r10.getIntExtra(r0, r11)     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            r11 = r0
            if (r11 == 0) goto L_0x0130
            java.lang.String r0 = r1.packageName     // Catch:{ IOException -> 0x012c, all -> 0x0123 }
            r2 = r28
            java.lang.Object r0 = r2.get(r0)     // Catch:{ IOException -> 0x00e3, all -> 0x00dc }
            com.android.server.backup.restore.RestorePolicy r3 = com.android.server.backup.restore.RestorePolicy.ACCEPT     // Catch:{ IOException -> 0x00e3, all -> 0x00dc }
            if (r0 == r3) goto L_0x011b
            r0 = 0
            r6 = r26
            r21 = r5
            goto L_0x0281
        L_0x011b:
            r6 = r26
            r21 = r5
            r0 = r17
            goto L_0x0281
        L_0x0123:
            r0 = move-exception
            r2 = r28
            r6 = r26
            r21 = r5
            goto L_0x02cc
        L_0x012c:
            r0 = move-exception
            r2 = r28
            goto L_0x00e4
        L_0x0130:
            r2 = r28
            r19 = 0
            java.lang.String r0 = "android.content.pm.extra.PACKAGE_NAME"
            java.lang.String r0 = r10.getStringExtra(r0)     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            r21 = r0
            java.lang.String r0 = r1.packageName     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            r2 = r21
            boolean r0 = r2.equals(r0)     // Catch:{ IOException -> 0x028e, all -> 0x0288 }
            if (r0 != 0) goto L_0x017c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0175, all -> 0x016e }
            r0.<init>()     // Catch:{ IOException -> 0x0175, all -> 0x016e }
            r21 = r5
            java.lang.String r5 = "Restore stream claimed to include apk for "
            r0.append(r5)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r5 = r1.packageName     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r0.append(r5)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r5 = " but apk was really "
            r0.append(r5)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r0.append(r2)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            android.util.Slog.w(r3, r0)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r0 = 0
            r19 = 1
            r5 = r0
            r22 = r6
            goto L_0x025a
        L_0x016e:
            r0 = move-exception
            r21 = r5
            r6 = r26
            goto L_0x028d
        L_0x0175:
            r0 = move-exception
            r21 = r5
            r6 = r26
            goto L_0x0293
        L_0x017c:
            r21 = r5
            java.lang.String r0 = r1.packageName     // Catch:{ NameNotFoundException -> 0x023b }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r22 = r6
            r6 = r32
            android.content.pm.PackageInfo r0 = r7.getPackageInfoAsUser(r0, r5, r6)     // Catch:{ NameNotFoundException -> 0x022b }
            int r5 = r1.rawFd     // Catch:{ NameNotFoundException -> 0x022b }
            boolean r5 = com.android.server.backup.BackupManagerServiceInjector.isForceAllowBackup(r0, r5)     // Catch:{ NameNotFoundException -> 0x022b }
            if (r5 != 0) goto L_0x01bb
            android.content.pm.ApplicationInfo r5 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x022b }
            int r5 = r5.flags     // Catch:{ NameNotFoundException -> 0x022b }
            r16 = 32768(0x8000, float:4.5918E-41)
            r5 = r5 & r16
            if (r5 != 0) goto L_0x01bb
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x022b }
            r5.<init>()     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r6 = "Restore stream contains apk of package "
            r5.append(r6)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r6 = r1.packageName     // Catch:{ NameNotFoundException -> 0x022b }
            r5.append(r6)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r6 = " but it disallows backup/restore"
            r5.append(r6)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = r5.toString()     // Catch:{ NameNotFoundException -> 0x022b }
            android.util.Slog.w(r3, r5)     // Catch:{ NameNotFoundException -> 0x022b }
            r5 = 0
            goto L_0x022a
        L_0x01bb:
            java.lang.String r5 = r1.packageName     // Catch:{ NameNotFoundException -> 0x022b }
            r6 = r27
            java.lang.Object r5 = r6.get(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            android.content.pm.Signature[] r5 = (android.content.pm.Signature[]) r5     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.Class<android.content.pm.PackageManagerInternal> r16 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r16 = com.android.server.LocalServices.getService(r16)     // Catch:{ NameNotFoundException -> 0x022b }
            android.content.pm.PackageManagerInternal r16 = (android.content.pm.PackageManagerInternal) r16     // Catch:{ NameNotFoundException -> 0x022b }
            r23 = r16
            r6 = r23
            boolean r16 = com.android.server.backup.utils.AppBackupUtils.signaturesMatch(r5, r0, r6)     // Catch:{ NameNotFoundException -> 0x022b }
            r23 = r5
            java.lang.String r5 = "Installed app "
            if (r16 == 0) goto L_0x020b
            r16 = r6
            android.content.pm.ApplicationInfo r6 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x022b }
            int r6 = r6.uid     // Catch:{ NameNotFoundException -> 0x022b }
            boolean r6 = android.os.UserHandle.isCore(r6)     // Catch:{ NameNotFoundException -> 0x022b }
            if (r6 == 0) goto L_0x0208
            android.content.pm.ApplicationInfo r6 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r6 = r6.backupAgentName     // Catch:{ NameNotFoundException -> 0x022b }
            if (r6 != 0) goto L_0x0208
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x022b }
            r6.<init>()     // Catch:{ NameNotFoundException -> 0x022b }
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = r1.packageName     // Catch:{ NameNotFoundException -> 0x022b }
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = " has restricted uid and no agent"
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = r6.toString()     // Catch:{ NameNotFoundException -> 0x022b }
            android.util.Slog.w(r3, r5)     // Catch:{ NameNotFoundException -> 0x022b }
            r5 = 0
            goto L_0x022a
        L_0x0208:
            r5 = r17
            goto L_0x022a
        L_0x020b:
            r16 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x022b }
            r6.<init>()     // Catch:{ NameNotFoundException -> 0x022b }
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = r1.packageName     // Catch:{ NameNotFoundException -> 0x022b }
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = " signatures do not match restore manifest"
            r6.append(r5)     // Catch:{ NameNotFoundException -> 0x022b }
            java.lang.String r5 = r6.toString()     // Catch:{ NameNotFoundException -> 0x022b }
            android.util.Slog.w(r3, r5)     // Catch:{ NameNotFoundException -> 0x022b }
            r5 = 0
            r6 = 1
            r19 = r6
        L_0x022a:
            goto L_0x025a
        L_0x022b:
            r0 = move-exception
            goto L_0x023e
        L_0x022d:
            r0 = move-exception
            r6 = r26
            goto L_0x02cc
        L_0x0232:
            r0 = move-exception
            r6 = r26
            r2 = r17
            r5 = r21
            goto L_0x02bb
        L_0x023b:
            r0 = move-exception
            r22 = r6
        L_0x023e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r5.<init>()     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r6 = "Install of package "
            r5.append(r6)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r6 = r1.packageName     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r5.append(r6)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r6 = " succeeded but now not found"
            r5.append(r6)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            android.util.Slog.w(r3, r5)     // Catch:{ IOException -> 0x0232, all -> 0x022d }
            r5 = 0
        L_0x025a:
            if (r19 == 0) goto L_0x027e
            r26.reset()     // Catch:{ IOException -> 0x0277, all -> 0x0270 }
            r6 = r26
            r1 = 0
            r7.deletePackage(r2, r6, r1)     // Catch:{ IOException -> 0x026b, all -> 0x0269 }
            r26.waitForCompletion()     // Catch:{ IOException -> 0x026b, all -> 0x0269 }
            goto L_0x0280
        L_0x0269:
            r0 = move-exception
            goto L_0x0273
        L_0x026b:
            r0 = move-exception
            r2 = r5
            r5 = r21
            goto L_0x02bb
        L_0x0270:
            r0 = move-exception
            r6 = r26
        L_0x0273:
            r17 = r5
            goto L_0x02cc
        L_0x0277:
            r0 = move-exception
            r6 = r26
            r2 = r5
            r5 = r21
            goto L_0x02bb
        L_0x027e:
            r6 = r26
        L_0x0280:
            r0 = r5
        L_0x0281:
            r4.close()
            r5 = r21
            goto L_0x02c6
        L_0x0288:
            r0 = move-exception
            r6 = r26
            r21 = r5
        L_0x028d:
            goto L_0x02cc
        L_0x028e:
            r0 = move-exception
            r6 = r26
            r21 = r5
        L_0x0293:
            r2 = r17
            goto L_0x02bb
        L_0x0296:
            r0 = move-exception
            r6 = r26
            r15 = r31
            r17 = r2
            r21 = r5
            goto L_0x02cc
        L_0x02a0:
            r0 = move-exception
            r6 = r26
            r15 = r31
            r17 = r2
            r21 = r5
            goto L_0x02bb
        L_0x02aa:
            r0 = move-exception
            r6 = r26
            r15 = r31
            r17 = r2
        L_0x02b1:
            r21 = r5
            goto L_0x02cc
        L_0x02b4:
            r0 = move-exception
            r6 = r26
            r15 = r31
            r17 = r2
        L_0x02bb:
            java.lang.String r1 = "Unable to transcribe restored apk for install"
            android.util.Slog.e(r3, r1)     // Catch:{ all -> 0x02c7 }
            r0 = 0
            if (r4 == 0) goto L_0x02c6
            r4.close()
        L_0x02c6:
            return r0
        L_0x02c7:
            r0 = move-exception
            r17 = r2
            r21 = r5
        L_0x02cc:
            if (r4 == 0) goto L_0x02d1
            r4.close()
        L_0x02d1:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.RestoreUtils.installApk(java.io.InputStream, android.content.Context, com.android.server.backup.restore.RestoreDeleteObserver, java.util.HashMap, java.util.HashMap, com.android.server.backup.FileMetadata, java.lang.String, com.android.server.backup.utils.BytesReadListener, int):boolean");
    }

    private static class LocalIntentReceiver {
        private IIntentSender.Stub mLocalSender;
        /* access modifiers changed from: private */
        public final Object mLock;
        /* access modifiers changed from: private */
        @GuardedBy({"mLock"})
        public Intent mResult;

        private LocalIntentReceiver() {
            this.mLock = new Object();
            this.mResult = null;
            this.mLocalSender = new IIntentSender.Stub() {
                public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                    synchronized (LocalIntentReceiver.this.mLock) {
                        Intent unused = LocalIntentReceiver.this.mResult = intent;
                        LocalIntentReceiver.this.mLock.notifyAll();
                    }
                }
            };
        }

        public IntentSender getIntentSender() {
            return new IntentSender(this.mLocalSender);
        }

        public Intent getResult() {
            Intent intent;
            synchronized (this.mLock) {
                while (this.mResult == null) {
                    try {
                        this.mLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                intent = this.mResult;
            }
            return intent;
        }
    }
}
