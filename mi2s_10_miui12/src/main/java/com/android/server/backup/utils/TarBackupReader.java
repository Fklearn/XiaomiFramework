package com.android.server.backup.utils;

import android.app.backup.IBackupManagerMonitor;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.FileMetadata;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarBackupReader {
    private static final int TAR_HEADER_LENGTH_FILESIZE = 12;
    private static final int TAR_HEADER_LENGTH_MODE = 8;
    private static final int TAR_HEADER_LENGTH_MODTIME = 12;
    private static final int TAR_HEADER_LENGTH_PATH = 100;
    private static final int TAR_HEADER_LENGTH_PATH_PREFIX = 155;
    private static final int TAR_HEADER_LONG_RADIX = 8;
    private static final int TAR_HEADER_OFFSET_FILESIZE = 124;
    private static final int TAR_HEADER_OFFSET_MODE = 100;
    private static final int TAR_HEADER_OFFSET_MODTIME = 136;
    private static final int TAR_HEADER_OFFSET_PATH = 0;
    private static final int TAR_HEADER_OFFSET_PATH_PREFIX = 345;
    private static final int TAR_HEADER_OFFSET_TYPE_CHAR = 156;
    private final BytesReadListener mBytesReadListener;
    private final InputStream mInputStream;
    private IBackupManagerMonitor mMonitor;
    private byte[] mWidgetData = null;

    public TarBackupReader(InputStream inputStream, BytesReadListener bytesReadListener, IBackupManagerMonitor monitor) {
        this.mInputStream = inputStream;
        this.mBytesReadListener = bytesReadListener;
        this.mMonitor = monitor;
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r10v0, types: [byte] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.backup.FileMetadata readTarHeaders() throws java.io.IOException {
        /*
            r15 = this;
            java.lang.String r0 = "apps/"
            java.lang.String r1 = "shared/"
            java.lang.String r2 = "BackupManagerService"
            r3 = 512(0x200, float:7.175E-43)
            byte[] r3 = new byte[r3]
            r4 = 0
            boolean r5 = r15.readTarHeader(r3)
            if (r5 == 0) goto L_0x01e8
            com.android.server.backup.FileMetadata r6 = new com.android.server.backup.FileMetadata     // Catch:{ IOException -> 0x01cb }
            r6.<init>()     // Catch:{ IOException -> 0x01cb }
            r4 = r6
            r6 = 124(0x7c, float:1.74E-43)
            r7 = 12
            r8 = 8
            long r9 = extractRadix(r3, r6, r7, r8)     // Catch:{ IOException -> 0x01cb }
            r4.size = r9     // Catch:{ IOException -> 0x01cb }
            r6 = 136(0x88, float:1.9E-43)
            long r6 = extractRadix(r3, r6, r7, r8)     // Catch:{ IOException -> 0x01cb }
            r4.mtime = r6     // Catch:{ IOException -> 0x01cb }
            r6 = 100
            long r7 = extractRadix(r3, r6, r8, r8)     // Catch:{ IOException -> 0x01cb }
            r4.mode = r7     // Catch:{ IOException -> 0x01cb }
            r7 = 345(0x159, float:4.83E-43)
            r8 = 155(0x9b, float:2.17E-43)
            java.lang.String r7 = extractString(r3, r7, r8)     // Catch:{ IOException -> 0x01cb }
            r4.path = r7     // Catch:{ IOException -> 0x01cb }
            r7 = 0
            java.lang.String r6 = extractString(r3, r7, r6)     // Catch:{ IOException -> 0x01cb }
            int r8 = r6.length()     // Catch:{ IOException -> 0x01cb }
            r9 = 47
            if (r8 <= 0) goto L_0x0079
            java.lang.String r8 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r8 = r8.length()     // Catch:{ IOException -> 0x01cb }
            if (r8 <= 0) goto L_0x0066
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r8.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r10 = r4.path     // Catch:{ IOException -> 0x01cb }
            r8.append(r10)     // Catch:{ IOException -> 0x01cb }
            r8.append(r9)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x01cb }
            r4.path = r8     // Catch:{ IOException -> 0x01cb }
        L_0x0066:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r8.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r10 = r4.path     // Catch:{ IOException -> 0x01cb }
            r8.append(r10)     // Catch:{ IOException -> 0x01cb }
            r8.append(r6)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x01cb }
            r4.path = r8     // Catch:{ IOException -> 0x01cb }
        L_0x0079:
            r8 = 156(0x9c, float:2.19E-43)
            byte r10 = r3[r8]     // Catch:{ IOException -> 0x01cb }
            r11 = 120(0x78, float:1.68E-43)
            if (r10 != r11) goto L_0x009b
            boolean r11 = r15.readPaxExtendedHeader(r4)     // Catch:{ IOException -> 0x01cb }
            r5 = r11
            if (r5 == 0) goto L_0x008d
            boolean r11 = r15.readTarHeader(r3)     // Catch:{ IOException -> 0x01cb }
            r5 = r11
        L_0x008d:
            if (r5 == 0) goto L_0x0093
            byte r8 = r3[r8]     // Catch:{ IOException -> 0x01cb }
            r10 = r8
            goto L_0x009b
        L_0x0093:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = "Bad or missing pax header"
            r0.<init>(r1)     // Catch:{ IOException -> 0x01cb }
            throw r0     // Catch:{ IOException -> 0x01cb }
        L_0x009b:
            if (r10 == 0) goto L_0x01b5
            r8 = 48
            if (r10 == r8) goto L_0x00e3
            r8 = 53
            if (r10 != r8) goto L_0x00b8
            r8 = 2
            r4.type = r8     // Catch:{ IOException -> 0x01cb }
            long r11 = r4.size     // Catch:{ IOException -> 0x01cb }
            r13 = 0
            int r8 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r8 == 0) goto L_0x00e7
            java.lang.String r8 = "Directory entry with nonzero size in header"
            android.util.Slog.w(r2, r8)     // Catch:{ IOException -> 0x01cb }
            r4.size = r13     // Catch:{ IOException -> 0x01cb }
            goto L_0x00e7
        L_0x00b8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r0.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = "Unknown tar entity type: "
            r0.append(r1)     // Catch:{ IOException -> 0x01cb }
            r0.append(r10)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01cb }
            android.util.Slog.e(r2, r0)     // Catch:{ IOException -> 0x01cb }
            java.io.IOException r0 = new java.io.IOException     // Catch:{ IOException -> 0x01cb }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r1.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r7 = "Unknown entity type "
            r1.append(r7)     // Catch:{ IOException -> 0x01cb }
            r1.append(r10)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x01cb }
            r0.<init>(r1)     // Catch:{ IOException -> 0x01cb }
            throw r0     // Catch:{ IOException -> 0x01cb }
        L_0x00e3:
            r8 = 1
            r4.type = r8     // Catch:{ IOException -> 0x01cb }
        L_0x00e7:
            java.lang.String r8 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r11 = r1.length()     // Catch:{ IOException -> 0x01cb }
            boolean r8 = r1.regionMatches(r7, r8, r7, r11)     // Catch:{ IOException -> 0x01cb }
            if (r8 == 0) goto L_0x0120
            java.lang.String r0 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r1 = r1.length()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r0.substring(r1)     // Catch:{ IOException -> 0x01cb }
            r4.path = r0     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = "com.android.sharedstoragebackup"
            r4.packageName = r0     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = "shared"
            r4.domain = r0     // Catch:{ IOException -> 0x01cb }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r0.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = "File in shared storage: "
            r0.append(r1)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            r0.append(r1)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01cb }
            android.util.Slog.i(r2, r0)     // Catch:{ IOException -> 0x01cb }
            goto L_0x01b4
        L_0x0120:
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r8 = r0.length()     // Catch:{ IOException -> 0x01cb }
            boolean r1 = r0.regionMatches(r7, r1, r7, r8)     // Catch:{ IOException -> 0x01cb }
            if (r1 == 0) goto L_0x01b4
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r0 = r0.length()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r1.substring(r0)     // Catch:{ IOException -> 0x01cb }
            r4.path = r0     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r0 = r0.indexOf(r9)     // Catch:{ IOException -> 0x01cb }
            if (r0 < 0) goto L_0x019b
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r1.substring(r7, r0)     // Catch:{ IOException -> 0x01cb }
            r4.packageName = r1     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r8 = r0 + 1
            java.lang.String r1 = r1.substring(r8)     // Catch:{ IOException -> 0x01cb }
            r4.path = r1     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = "_manifest"
            boolean r1 = r1.equals(r8)     // Catch:{ IOException -> 0x01cb }
            if (r1 != 0) goto L_0x01b4
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = "_meta"
            boolean r1 = r1.equals(r8)     // Catch:{ IOException -> 0x01cb }
            if (r1 != 0) goto L_0x01b4
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r1 = r1.indexOf(r9)     // Catch:{ IOException -> 0x01cb }
            r0 = r1
            if (r0 < 0) goto L_0x0182
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r1.substring(r7, r0)     // Catch:{ IOException -> 0x01cb }
            r4.domain = r1     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = r4.path     // Catch:{ IOException -> 0x01cb }
            int r7 = r0 + 1
            java.lang.String r1 = r1.substring(r7)     // Catch:{ IOException -> 0x01cb }
            r4.path = r1     // Catch:{ IOException -> 0x01cb }
            goto L_0x01b4
        L_0x0182:
            java.io.IOException r1 = new java.io.IOException     // Catch:{ IOException -> 0x01cb }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r7.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = "Illegal semantic path in non-manifest "
            r7.append(r8)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = r4.path     // Catch:{ IOException -> 0x01cb }
            r7.append(r8)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x01cb }
            r1.<init>(r7)     // Catch:{ IOException -> 0x01cb }
            throw r1     // Catch:{ IOException -> 0x01cb }
        L_0x019b:
            java.io.IOException r1 = new java.io.IOException     // Catch:{ IOException -> 0x01cb }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r7.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = "Illegal semantic path in "
            r7.append(r8)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r8 = r4.path     // Catch:{ IOException -> 0x01cb }
            r7.append(r8)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x01cb }
            r1.<init>(r7)     // Catch:{ IOException -> 0x01cb }
            throw r1     // Catch:{ IOException -> 0x01cb }
        L_0x01b4:
            goto L_0x01e8
        L_0x01b5:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cb }
            r0.<init>()     // Catch:{ IOException -> 0x01cb }
            java.lang.String r1 = "Saw type=0 in tar header block, info="
            r0.append(r1)     // Catch:{ IOException -> 0x01cb }
            r0.append(r4)     // Catch:{ IOException -> 0x01cb }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01cb }
            android.util.Slog.w(r2, r0)     // Catch:{ IOException -> 0x01cb }
            r0 = 0
            return r0
        L_0x01cb:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "Parse error in header: "
            r1.append(r6)
            java.lang.String r6 = r0.getMessage()
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            android.util.Slog.e(r2, r1)
            hexLog(r3)
            throw r0
        L_0x01e8:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.TarBackupReader.readTarHeaders():com.android.server.backup.FileMetadata");
    }

    private static int readExactly(InputStream in, byte[] buffer, int offset, int size) throws IOException {
        if (size > 0) {
            Slog.i(BackupManagerService.TAG, "  ... readExactly(" + size + ") called");
            int soFar = 0;
            while (true) {
                if (soFar >= size) {
                    break;
                }
                int nRead = in.read(buffer, offset + soFar, size - soFar);
                if (nRead <= 0) {
                    Slog.w(BackupManagerService.TAG, "- wanted exactly " + size + " but got only " + soFar);
                    break;
                }
                soFar += nRead;
                Slog.v(BackupManagerService.TAG, "   + got " + nRead + "; now wanting " + (size - soFar));
            }
            return soFar;
        }
        throw new IllegalArgumentException("size must be > 0");
    }

    public Signature[] readAppManifestAndReturnSignatures(FileMetadata info) throws IOException {
        FileMetadata fileMetadata = info;
        if (fileMetadata.size <= 65536) {
            byte[] buffer = new byte[((int) fileMetadata.size)];
            Slog.i(BackupManagerService.TAG, "   readAppManifestAndReturnSignatures() looking for " + fileMetadata.size + " bytes");
            if (((long) readExactly(this.mInputStream, buffer, 0, (int) fileMetadata.size)) == fileMetadata.size) {
                this.mBytesReadListener.onBytesRead(fileMetadata.size);
                String[] str = new String[1];
                try {
                    int offset = extractLine(buffer, 0, str);
                    int version = Integer.parseInt(str[0]);
                    if (version == 1) {
                        int offset2 = extractLine(buffer, offset, str);
                        String manifestPackage = str[0];
                        if (manifestPackage.equals(fileMetadata.packageName)) {
                            int offset3 = extractLine(buffer, offset2, str);
                            fileMetadata.version = (long) Integer.parseInt(str[0]);
                            int offset4 = extractLine(buffer, offset3, str);
                            Integer.parseInt(str[0]);
                            int offset5 = extractLine(buffer, offset4, str);
                            fileMetadata.installerPackageName = str[0].length() > 0 ? str[0] : null;
                            int offset6 = extractLine(buffer, offset5, str);
                            fileMetadata.hasApk = str[0].equals(SplitScreenReporter.ACTION_ENTER_SPLIT);
                            int offset7 = extractLine(buffer, offset6, str);
                            int numSigs = Integer.parseInt(str[0]);
                            if (numSigs > 0) {
                                Signature[] sigs = new Signature[numSigs];
                                for (int i = 0; i < numSigs; i++) {
                                    offset7 = extractLine(buffer, offset7, str);
                                    sigs[i] = new Signature(str[0]);
                                }
                                return sigs;
                            }
                            Slog.i(BackupManagerService.TAG, "Missing signature on backed-up package " + fileMetadata.packageName);
                            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 42, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", fileMetadata.packageName));
                        } else {
                            Slog.i(BackupManagerService.TAG, "Expected package " + fileMetadata.packageName + " but restore manifest claims " + manifestPackage);
                            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 43, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", fileMetadata.packageName), "android.app.backup.extra.LOG_MANIFEST_PACKAGE_NAME", manifestPackage));
                        }
                    } else {
                        Slog.i(BackupManagerService.TAG, "Unknown restore manifest version " + version + " for package " + fileMetadata.packageName);
                        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 44, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", fileMetadata.packageName), "android.app.backup.extra.LOG_EVENT_PACKAGE_VERSION", (long) version));
                    }
                } catch (NumberFormatException e) {
                    Slog.w(BackupManagerService.TAG, "Corrupt restore manifest for package " + fileMetadata.packageName);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 46, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", fileMetadata.packageName));
                } catch (IllegalArgumentException e2) {
                    Slog.w(BackupManagerService.TAG, e2.getMessage());
                }
                return null;
            }
            throw new IOException("Unexpected EOF in manifest");
        }
        throw new IOException("Restore manifest too big; corrupt? size=" + fileMetadata.size);
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01f3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.backup.restore.RestorePolicy chooseRestorePolicy(android.content.pm.PackageManager r18, boolean r19, com.android.server.backup.FileMetadata r20, android.content.pm.Signature[] r21, android.content.pm.PackageManagerInternal r22, int r23) {
        /*
            r17 = this;
            r1 = r17
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r23
            java.lang.String r6 = "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME"
            java.lang.String r7 = "Package "
            java.lang.String r8 = "BackupManagerService"
            if (r4 != 0) goto L_0x0015
            com.android.server.backup.restore.RestorePolicy r0 = com.android.server.backup.restore.RestorePolicy.IGNORE
            return r0
        L_0x0015:
            com.android.server.backup.restore.RestorePolicy r9 = com.android.server.backup.restore.RestorePolicy.IGNORE
            r10 = 3
            r11 = 0
            java.lang.String r0 = r3.packageName     // Catch:{ NameNotFoundException -> 0x0171 }
            r12 = 134217728(0x8000000, float:3.85186E-34)
            r13 = r18
            android.content.pm.PackageInfo r0 = r13.getPackageInfoAsUser(r0, r12, r5)     // Catch:{ NameNotFoundException -> 0x016f }
            android.content.pm.ApplicationInfo r12 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x016f }
            int r12 = r12.flags     // Catch:{ NameNotFoundException -> 0x016f }
            int r14 = r3.rawFd     // Catch:{ NameNotFoundException -> 0x016f }
            boolean r14 = com.android.server.backup.BackupManagerServiceInjector.isForceAllowBackup(r0, r14)     // Catch:{ NameNotFoundException -> 0x016f }
            if (r14 != 0) goto L_0x005f
            r14 = 32768(0x8000, float:4.5918E-41)
            r14 = r14 & r12
            if (r14 == 0) goto L_0x0036
            goto L_0x005f
        L_0x0036:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x016f }
            r14.<init>()     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r15 = "Restore manifest from "
            r14.append(r15)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r15 = r3.packageName     // Catch:{ NameNotFoundException -> 0x016f }
            r14.append(r15)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r15 = " but allowBackup=false"
            r14.append(r15)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r14 = r14.toString()     // Catch:{ NameNotFoundException -> 0x016f }
            android.util.Slog.i(r8, r14)     // Catch:{ NameNotFoundException -> 0x016f }
            android.app.backup.IBackupManagerMonitor r14 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016f }
            r15 = 39
            android.app.backup.IBackupManagerMonitor r14 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r14, r15, r0, r10, r11)     // Catch:{ NameNotFoundException -> 0x016f }
            r1.mMonitor = r14     // Catch:{ NameNotFoundException -> 0x016f }
            r10 = r22
            goto L_0x016c
        L_0x005f:
            android.content.pm.ApplicationInfo r14 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x016f }
            int r14 = r14.uid     // Catch:{ NameNotFoundException -> 0x016f }
            boolean r14 = android.os.UserHandle.isCore(r14)     // Catch:{ NameNotFoundException -> 0x016f }
            if (r14 == 0) goto L_0x0098
            android.content.pm.ApplicationInfo r14 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r14 = r14.backupAgentName     // Catch:{ NameNotFoundException -> 0x016f }
            if (r14 == 0) goto L_0x0070
            goto L_0x0098
        L_0x0070:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x016f }
            r14.<init>()     // Catch:{ NameNotFoundException -> 0x016f }
            r14.append(r7)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r15 = r3.packageName     // Catch:{ NameNotFoundException -> 0x016f }
            r14.append(r15)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r15 = " is system level with no agent"
            r14.append(r15)     // Catch:{ NameNotFoundException -> 0x016f }
            java.lang.String r14 = r14.toString()     // Catch:{ NameNotFoundException -> 0x016f }
            android.util.Slog.w(r8, r14)     // Catch:{ NameNotFoundException -> 0x016f }
            android.app.backup.IBackupManagerMonitor r14 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016f }
            r15 = 38
            r10 = 2
            android.app.backup.IBackupManagerMonitor r10 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r14, r15, r0, r10, r11)     // Catch:{ NameNotFoundException -> 0x016f }
            r1.mMonitor = r10     // Catch:{ NameNotFoundException -> 0x016f }
            r10 = r22
            goto L_0x016c
        L_0x0098:
            r10 = r22
            boolean r14 = com.android.server.backup.utils.AppBackupUtils.signaturesMatch(r4, r0, r10)     // Catch:{ NameNotFoundException -> 0x016d }
            if (r14 == 0) goto L_0x0148
            android.content.pm.ApplicationInfo r14 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x016d }
            int r14 = r14.flags     // Catch:{ NameNotFoundException -> 0x016d }
            r15 = 131072(0x20000, float:1.83671E-40)
            r14 = r14 & r15
            if (r14 == 0) goto L_0x00be
            java.lang.String r14 = "Package has restoreAnyVersion; taking data"
            android.util.Slog.i(r8, r14)     // Catch:{ NameNotFoundException -> 0x016d }
            android.app.backup.IBackupManagerMonitor r14 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016d }
            r15 = 34
            r4 = 3
            android.app.backup.IBackupManagerMonitor r14 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r14, r15, r0, r4, r11)     // Catch:{ NameNotFoundException -> 0x016d }
            r1.mMonitor = r14     // Catch:{ NameNotFoundException -> 0x016d }
            com.android.server.backup.restore.RestorePolicy r4 = com.android.server.backup.restore.RestorePolicy.ACCEPT     // Catch:{ NameNotFoundException -> 0x016d }
            r9 = r4
            goto L_0x016c
        L_0x00be:
            long r14 = r0.getLongVersionCode()     // Catch:{ NameNotFoundException -> 0x016d }
            r16 = r12
            long r11 = r3.version     // Catch:{ NameNotFoundException -> 0x016d }
            int r11 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r11 >= 0) goto L_0x0133
            int r11 = r3.rawFd     // Catch:{ NameNotFoundException -> 0x016d }
            boolean r11 = com.android.server.backup.BackupManagerServiceInjector.isSysAppRunningFromMiui(r0, r11)     // Catch:{ NameNotFoundException -> 0x016d }
            if (r11 == 0) goto L_0x00d3
            goto L_0x0133
        L_0x00d3:
            if (r2 == 0) goto L_0x0100
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x016d }
            r11.<init>()     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r12 = "Data version "
            r11.append(r12)     // Catch:{ NameNotFoundException -> 0x016d }
            long r14 = r3.version     // Catch:{ NameNotFoundException -> 0x016d }
            r11.append(r14)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r12 = " is newer than installed version "
            r11.append(r12)     // Catch:{ NameNotFoundException -> 0x016d }
            long r14 = r0.getLongVersionCode()     // Catch:{ NameNotFoundException -> 0x016d }
            r11.append(r14)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r12 = " - requiring apk"
            r11.append(r12)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r11 = r11.toString()     // Catch:{ NameNotFoundException -> 0x016d }
            android.util.Slog.i(r8, r11)     // Catch:{ NameNotFoundException -> 0x016d }
            com.android.server.backup.restore.RestorePolicy r7 = com.android.server.backup.restore.RestorePolicy.ACCEPT_IF_APK     // Catch:{ NameNotFoundException -> 0x016d }
            r9 = r7
            goto L_0x016c
        L_0x0100:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x016d }
            r11.<init>()     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r12 = "Data requires newer version "
            r11.append(r12)     // Catch:{ NameNotFoundException -> 0x016d }
            long r14 = r3.version     // Catch:{ NameNotFoundException -> 0x016d }
            r11.append(r14)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r12 = "; ignoring"
            r11.append(r12)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r11 = r11.toString()     // Catch:{ NameNotFoundException -> 0x016d }
            android.util.Slog.i(r8, r11)     // Catch:{ NameNotFoundException -> 0x016d }
            android.app.backup.IBackupManagerMonitor r11 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016d }
            r12 = 36
            java.lang.String r14 = "android.app.backup.extra.LOG_OLD_VERSION"
            long r4 = r3.version     // Catch:{ NameNotFoundException -> 0x016d }
            r15 = 0
            android.os.Bundle r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r15, (java.lang.String) r14, (long) r4)     // Catch:{ NameNotFoundException -> 0x016d }
            r14 = 3
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r11, r12, r0, r14, r5)     // Catch:{ NameNotFoundException -> 0x016d }
            r1.mMonitor = r5     // Catch:{ NameNotFoundException -> 0x016d }
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ NameNotFoundException -> 0x016d }
            r9 = r5
            goto L_0x016c
        L_0x0133:
            java.lang.String r5 = "Sig + version match; taking data"
            android.util.Slog.i(r8, r5)     // Catch:{ NameNotFoundException -> 0x016d }
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.ACCEPT     // Catch:{ NameNotFoundException -> 0x016d }
            r9 = r5
            android.app.backup.IBackupManagerMonitor r5 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016d }
            r11 = 35
            r4 = 3
            r12 = 0
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r11, r0, r4, r12)     // Catch:{ NameNotFoundException -> 0x016d }
            r1.mMonitor = r5     // Catch:{ NameNotFoundException -> 0x016d }
            goto L_0x016c
        L_0x0148:
            r16 = r12
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x016d }
            r5.<init>()     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r11 = "Restore manifest signatures do not match installed application for "
            r5.append(r11)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r11 = r3.packageName     // Catch:{ NameNotFoundException -> 0x016d }
            r5.append(r11)     // Catch:{ NameNotFoundException -> 0x016d }
            java.lang.String r5 = r5.toString()     // Catch:{ NameNotFoundException -> 0x016d }
            android.util.Slog.w(r8, r5)     // Catch:{ NameNotFoundException -> 0x016d }
            android.app.backup.IBackupManagerMonitor r5 = r1.mMonitor     // Catch:{ NameNotFoundException -> 0x016d }
            r11 = 37
            r4 = 3
            r12 = 0
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r11, r0, r4, r12)     // Catch:{ NameNotFoundException -> 0x016d }
            r1.mMonitor = r5     // Catch:{ NameNotFoundException -> 0x016d }
        L_0x016c:
            goto L_0x01b0
        L_0x016d:
            r0 = move-exception
            goto L_0x0176
        L_0x016f:
            r0 = move-exception
            goto L_0x0174
        L_0x0171:
            r0 = move-exception
            r13 = r18
        L_0x0174:
            r10 = r22
        L_0x0176:
            if (r2 == 0) goto L_0x0195
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r7)
            java.lang.String r7 = r3.packageName
            r5.append(r7)
            java.lang.String r7 = " not installed; requiring apk in dataset"
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            android.util.Slog.i(r8, r5)
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.ACCEPT_IF_APK
            r9 = r5
            goto L_0x0198
        L_0x0195:
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.IGNORE
            r9 = r5
        L_0x0198:
            java.lang.String r5 = r3.packageName
            r4 = 0
            android.os.Bundle r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r4, (java.lang.String) r6, (java.lang.String) r5)
            java.lang.String r7 = "android.app.backup.extra.LOG_POLICY_ALLOW_APKS"
            android.os.Bundle r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r5, (java.lang.String) r7, (boolean) r2)
            android.app.backup.IBackupManagerMonitor r7 = r1.mMonitor
            r11 = 40
            r12 = 3
            android.app.backup.IBackupManagerMonitor r7 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r7, r11, r4, r12, r5)
            r1.mMonitor = r7
        L_0x01b0:
            com.android.server.backup.restore.RestorePolicy r0 = com.android.server.backup.restore.RestorePolicy.ACCEPT_IF_APK
            if (r9 != r0) goto L_0x01e5
            boolean r0 = r3.hasApk
            if (r0 != 0) goto L_0x01e5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Cannot restore package "
            r0.append(r5)
            java.lang.String r5 = r3.packageName
            r0.append(r5)
            java.lang.String r5 = " without the matching .apk"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Slog.i(r8, r0)
            android.app.backup.IBackupManagerMonitor r0 = r1.mMonitor
            r5 = 41
            java.lang.String r7 = r3.packageName
            r4 = 0
            android.os.Bundle r6 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r4, (java.lang.String) r6, (java.lang.String) r7)
            r7 = 3
            android.app.backup.IBackupManagerMonitor r0 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r0, r5, r4, r7, r6)
            r1.mMonitor = r0
        L_0x01e5:
            int r0 = r3.rawFd
            r4 = r23
            int r0 = com.android.server.backup.BackupManagerServiceInjector.getAppUserId(r0, r4)
            boolean r5 = com.android.server.backup.BackupManagerServiceInjector.isXSpaceUser(r0)
            if (r5 == 0) goto L_0x0218
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.ACCEPT
            if (r9 != r5) goto L_0x01fa
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.ACCEPT_IF_APK
            r9 = r5
        L_0x01fa:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "USER_XSPACE APP chooseRestorePolicy, "
            r5.append(r6)
            java.lang.String r6 = r3.packageName
            r5.append(r6)
            java.lang.String r6 = " userIdX:"
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            android.util.Slog.d(r8, r5)
        L_0x0218:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.TarBackupReader.chooseRestorePolicy(android.content.pm.PackageManager, boolean, com.android.server.backup.FileMetadata, android.content.pm.Signature[], android.content.pm.PackageManagerInternal, int):com.android.server.backup.restore.RestorePolicy");
    }

    public void skipTarPadding(long size) throws IOException {
        long partial = (size + 512) % 512;
        if (partial > 0) {
            int needed = 512 - ((int) partial);
            Slog.i(BackupManagerService.TAG, "Skipping tar padding: " + needed + " bytes");
            if (readExactly(this.mInputStream, new byte[needed], 0, needed) == needed) {
                this.mBytesReadListener.onBytesRead((long) needed);
                return;
            }
            throw new IOException("Unexpected EOF in padding");
        }
    }

    public void readMetadata(FileMetadata info) throws IOException {
        if (info.size <= 65536) {
            byte[] buffer = new byte[((int) info.size)];
            if (((long) readExactly(this.mInputStream, buffer, 0, (int) info.size)) == info.size) {
                this.mBytesReadListener.onBytesRead(info.size);
                String[] str = new String[1];
                int offset = extractLine(buffer, 0, str);
                int version = Integer.parseInt(str[0]);
                if (version == 1) {
                    int offset2 = extractLine(buffer, offset, str);
                    String pkg = str[0];
                    if (info.packageName.equals(pkg)) {
                        ByteArrayInputStream bin = new ByteArrayInputStream(buffer, offset2, buffer.length - offset2);
                        DataInputStream in = new DataInputStream(bin);
                        while (bin.available() > 0) {
                            int token = in.readInt();
                            int size = in.readInt();
                            if (size > 65536) {
                                throw new IOException("Datum " + Integer.toHexString(token) + " too big; corrupt? size=" + info.size);
                            } else if (token != 33549569) {
                                Slog.i(BackupManagerService.TAG, "Ignoring metadata blob " + Integer.toHexString(token) + " for " + info.packageName);
                                in.skipBytes(size);
                            } else {
                                Slog.i(BackupManagerService.TAG, "Got widget metadata for " + info.packageName);
                                this.mWidgetData = new byte[size];
                                in.read(this.mWidgetData);
                            }
                        }
                        return;
                    }
                    Slog.w(BackupManagerService.TAG, "Metadata mismatch: package " + info.packageName + " but widget data for " + pkg);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 47, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", info.packageName), "android.app.backup.extra.LOG_WIDGET_PACKAGE_NAME", pkg));
                    return;
                }
                Slog.w(BackupManagerService.TAG, "Unsupported metadata version " + version);
                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 48, (PackageInfo) null, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EVENT_PACKAGE_NAME", info.packageName), "android.app.backup.extra.LOG_EVENT_PACKAGE_VERSION", (long) version));
                return;
            }
            throw new IOException("Unexpected EOF in widget data");
        }
        throw new IOException("Metadata too big; corrupt? size=" + info.size);
    }

    private static int extractLine(byte[] buffer, int offset, String[] outStr) throws IOException {
        int end = buffer.length;
        if (offset < end) {
            int pos = offset;
            while (pos < end && buffer[pos] != 10) {
                pos++;
            }
            outStr[0] = new String(buffer, offset, pos - offset);
            return pos + 1;
        }
        throw new IOException("Incomplete data");
    }

    private boolean readTarHeader(byte[] block) throws IOException {
        int got = readExactly(this.mInputStream, block, 0, 512);
        if (got == 0) {
            return false;
        }
        if (got >= 512) {
            this.mBytesReadListener.onBytesRead(512);
            return true;
        }
        throw new IOException("Unable to read full block header");
    }

    private boolean readPaxExtendedHeader(FileMetadata info) throws IOException {
        FileMetadata fileMetadata = info;
        if (fileMetadata.size <= 32768) {
            byte[] data = new byte[(((int) ((fileMetadata.size + 511) >> 9)) * 512)];
            if (readExactly(this.mInputStream, data, 0, data.length) >= data.length) {
                this.mBytesReadListener.onBytesRead((long) data.length);
                int contentSize = (int) fileMetadata.size;
                int offset = 0;
                while (true) {
                    int eol = offset + 1;
                    while (eol < contentSize && data[eol] != 32) {
                        eol++;
                    }
                    if (eol < contentSize) {
                        int linelen = (int) extractRadix(data, offset, eol - offset, 10);
                        int key = eol + 1;
                        int eol2 = (offset + linelen) - 1;
                        int value = key + 1;
                        while (data[value] != 61 && value <= eol2) {
                            value++;
                        }
                        if (value <= eol2) {
                            String keyStr = new String(data, key, value - key, "UTF-8");
                            String valStr = new String(data, value + 1, (eol2 - value) - 1, "UTF-8");
                            if ("path".equals(keyStr)) {
                                fileMetadata.path = valStr;
                            } else if ("size".equals(keyStr)) {
                                fileMetadata.size = Long.parseLong(valStr);
                            } else {
                                Slog.i(BackupManagerService.TAG, "Unhandled pax key: " + key);
                            }
                            offset += linelen;
                            if (offset >= contentSize) {
                                return true;
                            }
                        } else {
                            throw new IOException("Invalid pax declaration");
                        }
                    } else {
                        throw new IOException("Invalid pax data");
                    }
                }
            } else {
                throw new IOException("Unable to read full pax header");
            }
        } else {
            Slog.w(BackupManagerService.TAG, "Suspiciously large pax header size " + fileMetadata.size + " - aborting");
            throw new IOException("Sanity failure: pax header size " + fileMetadata.size);
        }
    }

    private static long extractRadix(byte[] data, int offset, int maxChars, int radix) throws IOException {
        long value = 0;
        int end = offset + maxChars;
        int i = offset;
        while (i < end) {
            byte b = data[i];
            if (b == 0 || b == 32) {
                break;
            } else if (b < 48 || b > (radix + 48) - 1) {
                throw new IOException("Invalid number in header: '" + ((char) b) + "' for radix " + radix);
            } else {
                value = (((long) radix) * value) + ((long) (b - 48));
                i++;
            }
        }
        return value;
    }

    private static String extractString(byte[] data, int offset, int maxChars) throws IOException {
        int end = offset + maxChars;
        int eos = offset;
        while (eos < end && data[eos] != 0) {
            eos++;
        }
        return new String(data, offset, eos - offset, "US-ASCII");
    }

    private static void hexLog(byte[] block) {
        int offset = 0;
        int todo = block.length;
        StringBuilder buf = new StringBuilder(64);
        while (todo > 0) {
            buf.append(String.format("%04x   ", new Object[]{Integer.valueOf(offset)}));
            int numThisLine = 16;
            if (todo <= 16) {
                numThisLine = todo;
            }
            for (int i = 0; i < numThisLine; i++) {
                buf.append(String.format("%02x ", new Object[]{Byte.valueOf(block[offset + i])}));
            }
            Slog.i("hexdump", buf.toString());
            buf.setLength(0);
            todo -= numThisLine;
            offset += numThisLine;
        }
    }

    public IBackupManagerMonitor getMonitor() {
        return this.mMonitor;
    }

    public byte[] getWidgetData() {
        return this.mWidgetData;
    }
}
