package com.android.server.backup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.server.backup.utils.AppBackupUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PackageManagerBackupAgent extends BackupAgent {
    private static final String ANCESTRAL_RECORD_KEY = "@ancestral_record@";
    private static final int ANCESTRAL_RECORD_VERSION = 1;
    private static final boolean DEBUG = false;
    private static final String DEFAULT_HOME_KEY = "@home@";
    private static final String GLOBAL_METADATA_KEY = "@meta@";
    private static final String STATE_FILE_HEADER = "=state=";
    private static final int STATE_FILE_VERSION = 2;
    private static final String TAG = "PMBA";
    private static final int UNDEFINED_ANCESTRAL_RECORD_VERSION = -1;
    private List<PackageInfo> mAllPackages;
    private final HashSet<String> mExisting = new HashSet<>();
    /* access modifiers changed from: private */
    public boolean mHasMetadata;
    private PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public ComponentName mRestoredHome;
    /* access modifiers changed from: private */
    public String mRestoredHomeInstaller;
    /* access modifiers changed from: private */
    public ArrayList<byte[]> mRestoredHomeSigHashes;
    /* access modifiers changed from: private */
    public long mRestoredHomeVersion;
    /* access modifiers changed from: private */
    public HashMap<String, Metadata> mRestoredSignatures;
    private HashMap<String, Metadata> mStateVersions = new HashMap<>();
    private ComponentName mStoredHomeComponent;
    private ArrayList<byte[]> mStoredHomeSigHashes;
    private long mStoredHomeVersion;
    /* access modifiers changed from: private */
    public String mStoredIncrementalVersion;
    /* access modifiers changed from: private */
    public int mStoredSdkVersion;
    private int mUserId;

    interface RestoreDataConsumer {
        void consumeRestoreData(BackupDataInput backupDataInput) throws IOException;
    }

    public class Metadata {
        public ArrayList<byte[]> sigHashes;
        public long versionCode;

        Metadata(long version, ArrayList<byte[]> hashes) {
            this.versionCode = version;
            this.sigHashes = hashes;
        }
    }

    public PackageManagerBackupAgent(PackageManager packageMgr, List<PackageInfo> packages, int userId) {
        init(packageMgr, packages, userId);
    }

    public PackageManagerBackupAgent(PackageManager packageMgr, int userId) {
        init(packageMgr, (List<PackageInfo>) null, userId);
        evaluateStorablePackages();
    }

    private void init(PackageManager packageMgr, List<PackageInfo> packages, int userId) {
        this.mPackageManager = packageMgr;
        this.mAllPackages = packages;
        this.mRestoredSignatures = null;
        this.mHasMetadata = false;
        this.mStoredSdkVersion = Build.VERSION.SDK_INT;
        this.mStoredIncrementalVersion = Build.VERSION.INCREMENTAL;
        this.mUserId = userId;
    }

    public void evaluateStorablePackages() {
        this.mAllPackages = getStorableApplications(this.mPackageManager, this.mUserId);
    }

    public static List<PackageInfo> getStorableApplications(PackageManager pm, int userId) {
        List<PackageInfo> pkgs = pm.getInstalledPackagesAsUser(134217728, userId);
        for (int a = pkgs.size() - 1; a >= 0; a--) {
            if (!AppBackupUtils.appIsEligibleForBackup(pkgs.get(a).applicationInfo, userId)) {
                pkgs.remove(a);
            }
        }
        return pkgs;
    }

    public boolean hasMetadata() {
        return this.mHasMetadata;
    }

    public Metadata getRestoredMetadata(String packageName) {
        HashMap<String, Metadata> hashMap = this.mRestoredSignatures;
        if (hashMap != null) {
            return hashMap.get(packageName);
        }
        Slog.w(TAG, "getRestoredMetadata() before metadata read!");
        return null;
    }

    public Set<String> getRestoredPackages() {
        HashMap<String, Metadata> hashMap = this.mRestoredSignatures;
        if (hashMap != null) {
            return hashMap.keySet();
        }
        Slog.w(TAG, "getRestoredPackages() before metadata read!");
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d1 A[SYNTHETIC, Splitter:B:35:0x00d1] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0128 A[SYNTHETIC, Splitter:B:59:0x0128] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x013a A[SYNTHETIC, Splitter:B:61:0x013a] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x014b A[SYNTHETIC, Splitter:B:66:0x014b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBackup(android.os.ParcelFileDescriptor r27, android.app.backup.BackupDataOutput r28, android.os.ParcelFileDescriptor r29) {
        /*
            r26 = this;
            r8 = r26
            r9 = r28
            java.lang.String r1 = "Unable to write package backup data file!"
            java.lang.String r2 = "@meta@"
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r0.<init>()
            r10 = r0
            java.io.DataOutputStream r0 = new java.io.DataOutputStream
            r0.<init>(r10)
            r11 = r0
            r26.parseStateFile(r27)
            java.lang.String r0 = r8.mStoredIncrementalVersion
            java.lang.String r3 = "PMBA"
            if (r0 == 0) goto L_0x0025
            java.lang.String r4 = android.os.Build.VERSION.INCREMENTAL
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x004f
        L_0x0025:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Previous metadata "
            r0.append(r4)
            java.lang.String r4 = r8.mStoredIncrementalVersion
            r0.append(r4)
            java.lang.String r4 = " mismatch vs "
            r0.append(r4)
            java.lang.String r4 = android.os.Build.VERSION.INCREMENTAL
            r0.append(r4)
            java.lang.String r4 = " - rewriting"
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Slog.i(r3, r0)
            java.util.HashSet<java.lang.String> r0 = r8.mExisting
            r0.clear()
        L_0x004f:
            r4 = 1
            r11.writeInt(r4)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r0 = "@ancestral_record@"
            byte[] r5 = r10.toByteArray()     // Catch:{ IOException -> 0x0239 }
            writeEntity(r9, r0, r5)     // Catch:{ IOException -> 0x0239 }
            r5 = 0
            r7 = 0
            r12 = 0
            r13 = 0
            android.content.ComponentName r14 = r26.getPreferredHomeComponent()
            if (r14 == 0) goto L_0x00b9
            android.content.pm.PackageManager r0 = r8.mPackageManager     // Catch:{ NameNotFoundException -> 0x00a7 }
            java.lang.String r4 = r14.getPackageName()     // Catch:{ NameNotFoundException -> 0x00a7 }
            int r15 = r8.mUserId     // Catch:{ NameNotFoundException -> 0x00a7 }
            r18 = r5
            r5 = 134217728(0x8000000, float:3.85186E-34)
            android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r4, r5, r15)     // Catch:{ NameNotFoundException -> 0x00a5 }
            r12 = r0
            android.content.pm.PackageManager r0 = r8.mPackageManager     // Catch:{ NameNotFoundException -> 0x00a5 }
            java.lang.String r4 = r14.getPackageName()     // Catch:{ NameNotFoundException -> 0x00a5 }
            java.lang.String r0 = r0.getInstallerPackageName(r4)     // Catch:{ NameNotFoundException -> 0x00a5 }
            r13 = r0
            long r4 = r12.getLongVersionCode()     // Catch:{ NameNotFoundException -> 0x00a5 }
            r5 = r4
            android.content.pm.SigningInfo r0 = r12.signingInfo     // Catch:{ NameNotFoundException -> 0x00a1 }
            if (r0 != 0) goto L_0x0093
            java.lang.String r4 = "Home app has no signing information"
            android.util.Slog.e(r3, r4)     // Catch:{ NameNotFoundException -> 0x00a1 }
            goto L_0x009c
        L_0x0093:
            android.content.pm.Signature[] r4 = r0.getApkContentsSigners()     // Catch:{ NameNotFoundException -> 0x00a1 }
            java.util.ArrayList r15 = com.android.server.backup.BackupUtils.hashSignatureArray((android.content.pm.Signature[]) r4)     // Catch:{ NameNotFoundException -> 0x00a1 }
            r7 = r15
        L_0x009c:
            r15 = r12
            r18 = r13
            r12 = r5
            goto L_0x00c2
        L_0x00a1:
            r0 = move-exception
            r18 = r5
            goto L_0x00aa
        L_0x00a5:
            r0 = move-exception
            goto L_0x00aa
        L_0x00a7:
            r0 = move-exception
            r18 = r5
        L_0x00aa:
            java.lang.String r4 = "Can't access preferred home info"
            android.util.Slog.w(r3, r4)
            r4 = 0
            r14 = r4
            r15 = r12
            r24 = r18
            r18 = r13
            r12 = r24
            goto L_0x00c2
        L_0x00b9:
            r18 = r5
            r15 = r12
            r24 = r18
            r18 = r13
            r12 = r24
        L_0x00c2:
            java.lang.Class<android.content.pm.PackageManagerInternal> r0 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ IOException -> 0x0232 }
            android.content.pm.PackageManagerInternal r0 = (android.content.pm.PackageManagerInternal) r0     // Catch:{ IOException -> 0x0232 }
            r4 = r0
            long r5 = r8.mStoredHomeVersion     // Catch:{ IOException -> 0x0232 }
            int r0 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x00ed
            android.content.ComponentName r0 = r8.mStoredHomeComponent     // Catch:{ IOException -> 0x00e8 }
            boolean r0 = java.util.Objects.equals(r14, r0)     // Catch:{ IOException -> 0x00e8 }
            if (r0 == 0) goto L_0x00ed
            if (r14 == 0) goto L_0x00e4
            java.util.ArrayList<byte[]> r0 = r8.mStoredHomeSigHashes     // Catch:{ IOException -> 0x00e8 }
            boolean r0 = com.android.server.backup.BackupUtils.signaturesMatch(r0, r15, r4)     // Catch:{ IOException -> 0x00e8 }
            if (r0 != 0) goto L_0x00e4
            goto L_0x00ed
        L_0x00e4:
            r0 = 0
            r16 = r0
            goto L_0x00ef
        L_0x00e8:
            r0 = move-exception
            r16 = r7
            goto L_0x0235
        L_0x00ed:
            r16 = 1
        L_0x00ef:
            r5 = r16
            if (r5 == 0) goto L_0x011d
            java.lang.String r0 = "@home@"
            if (r14 == 0) goto L_0x0119
            r10.reset()     // Catch:{ IOException -> 0x00e8 }
            java.lang.String r6 = r14.flattenToString()     // Catch:{ IOException -> 0x00e8 }
            r11.writeUTF(r6)     // Catch:{ IOException -> 0x00e8 }
            r11.writeLong(r12)     // Catch:{ IOException -> 0x00e8 }
            if (r18 == 0) goto L_0x0109
            r6 = r18
            goto L_0x010b
        L_0x0109:
            java.lang.String r6 = ""
        L_0x010b:
            r11.writeUTF(r6)     // Catch:{ IOException -> 0x00e8 }
            writeSignatureHashArray(r11, r7)     // Catch:{ IOException -> 0x00e8 }
            byte[] r6 = r10.toByteArray()     // Catch:{ IOException -> 0x00e8 }
            writeEntity(r9, r0, r6)     // Catch:{ IOException -> 0x00e8 }
            goto L_0x011d
        L_0x0119:
            r6 = -1
            r9.writeEntityHeader(r0, r6)     // Catch:{ IOException -> 0x00e8 }
        L_0x011d:
            r10.reset()     // Catch:{ IOException -> 0x0232 }
            java.util.HashSet<java.lang.String> r0 = r8.mExisting     // Catch:{ IOException -> 0x0232 }
            boolean r0 = r0.contains(r2)     // Catch:{ IOException -> 0x0232 }
            if (r0 != 0) goto L_0x013a
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ IOException -> 0x00e8 }
            r11.writeInt(r0)     // Catch:{ IOException -> 0x00e8 }
            java.lang.String r0 = android.os.Build.VERSION.INCREMENTAL     // Catch:{ IOException -> 0x00e8 }
            r11.writeUTF(r0)     // Catch:{ IOException -> 0x00e8 }
            byte[] r0 = r10.toByteArray()     // Catch:{ IOException -> 0x00e8 }
            writeEntity(r9, r2, r0)     // Catch:{ IOException -> 0x00e8 }
            goto L_0x013f
        L_0x013a:
            java.util.HashSet<java.lang.String> r0 = r8.mExisting     // Catch:{ IOException -> 0x0232 }
            r0.remove(r2)     // Catch:{ IOException -> 0x0232 }
        L_0x013f:
            java.util.List<android.content.pm.PackageInfo> r0 = r8.mAllPackages     // Catch:{ IOException -> 0x0232 }
            java.util.Iterator r6 = r0.iterator()     // Catch:{ IOException -> 0x0232 }
        L_0x0145:
            boolean r0 = r6.hasNext()     // Catch:{ IOException -> 0x0232 }
            if (r0 == 0) goto L_0x021e
            java.lang.Object r0 = r6.next()     // Catch:{ IOException -> 0x00e8 }
            android.content.pm.PackageInfo r0 = (android.content.pm.PackageInfo) r0     // Catch:{ IOException -> 0x00e8 }
            r16 = r0
            r19 = r4
            r4 = r16
            java.lang.String r0 = r4.packageName     // Catch:{ IOException -> 0x00e8 }
            r16 = r0
            r20 = r4
            r4 = r16
            boolean r0 = r4.equals(r2)     // Catch:{ IOException -> 0x00e8 }
            if (r0 == 0) goto L_0x0168
            r4 = r19
            goto L_0x0145
        L_0x0168:
            r16 = 0
            android.content.pm.PackageManager r0 = r8.mPackageManager     // Catch:{ NameNotFoundException -> 0x0208 }
            r21 = r2
            int r2 = r8.mUserId     // Catch:{ NameNotFoundException -> 0x0206 }
            r22 = r5
            r5 = 134217728(0x8000000, float:3.85186E-34)
            android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r4, r5, r2)     // Catch:{ NameNotFoundException -> 0x0202 }
            java.util.HashSet<java.lang.String> r2 = r8.mExisting     // Catch:{ IOException -> 0x00e8 }
            boolean r2 = r2.contains(r4)     // Catch:{ IOException -> 0x00e8 }
            if (r2 == 0) goto L_0x01a3
            java.util.HashSet<java.lang.String> r2 = r8.mExisting     // Catch:{ IOException -> 0x00e8 }
            r2.remove(r4)     // Catch:{ IOException -> 0x00e8 }
            long r16 = r0.getLongVersionCode()     // Catch:{ IOException -> 0x00e8 }
            java.util.HashMap<java.lang.String, com.android.server.backup.PackageManagerBackupAgent$Metadata> r2 = r8.mStateVersions     // Catch:{ IOException -> 0x00e8 }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ IOException -> 0x00e8 }
            com.android.server.backup.PackageManagerBackupAgent$Metadata r2 = (com.android.server.backup.PackageManagerBackupAgent.Metadata) r2     // Catch:{ IOException -> 0x00e8 }
            r23 = r6
            long r5 = r2.versionCode     // Catch:{ IOException -> 0x00e8 }
            int r2 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
            if (r2 != 0) goto L_0x01a5
            r4 = r19
            r2 = r21
            r5 = r22
            r6 = r23
            goto L_0x0145
        L_0x01a3:
            r23 = r6
        L_0x01a5:
            android.content.pm.SigningInfo r2 = r0.signingInfo     // Catch:{ IOException -> 0x00e8 }
            if (r2 != 0) goto L_0x01cc
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00e8 }
            r5.<init>()     // Catch:{ IOException -> 0x00e8 }
            java.lang.String r6 = "Not backing up package "
            r5.append(r6)     // Catch:{ IOException -> 0x00e8 }
            r5.append(r4)     // Catch:{ IOException -> 0x00e8 }
            java.lang.String r6 = " since it appears to have no signatures."
            r5.append(r6)     // Catch:{ IOException -> 0x00e8 }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00e8 }
            android.util.Slog.w(r3, r5)     // Catch:{ IOException -> 0x00e8 }
            r4 = r19
            r2 = r21
            r5 = r22
            r6 = r23
            goto L_0x0145
        L_0x01cc:
            r10.reset()     // Catch:{ IOException -> 0x00e8 }
            int r5 = r0.versionCodeMajor     // Catch:{ IOException -> 0x00e8 }
            if (r5 == 0) goto L_0x01e0
            r5 = -2147483648(0xffffffff80000000, float:-0.0)
            r11.writeInt(r5)     // Catch:{ IOException -> 0x00e8 }
            long r5 = r0.getLongVersionCode()     // Catch:{ IOException -> 0x00e8 }
            r11.writeLong(r5)     // Catch:{ IOException -> 0x00e8 }
            goto L_0x01e5
        L_0x01e0:
            int r5 = r0.versionCode     // Catch:{ IOException -> 0x00e8 }
            r11.writeInt(r5)     // Catch:{ IOException -> 0x00e8 }
        L_0x01e5:
            android.content.pm.Signature[] r5 = r2.getApkContentsSigners()     // Catch:{ IOException -> 0x00e8 }
            java.util.ArrayList r6 = com.android.server.backup.BackupUtils.hashSignatureArray((android.content.pm.Signature[]) r5)     // Catch:{ IOException -> 0x00e8 }
            writeSignatureHashArray(r11, r6)     // Catch:{ IOException -> 0x00e8 }
            byte[] r6 = r10.toByteArray()     // Catch:{ IOException -> 0x00e8 }
            writeEntity(r9, r4, r6)     // Catch:{ IOException -> 0x00e8 }
            r4 = r19
            r2 = r21
            r5 = r22
            r6 = r23
            goto L_0x0145
        L_0x0202:
            r0 = move-exception
            r23 = r6
            goto L_0x020f
        L_0x0206:
            r0 = move-exception
            goto L_0x020b
        L_0x0208:
            r0 = move-exception
            r21 = r2
        L_0x020b:
            r22 = r5
            r23 = r6
        L_0x020f:
            java.util.HashSet<java.lang.String> r2 = r8.mExisting     // Catch:{ IOException -> 0x00e8 }
            r2.add(r4)     // Catch:{ IOException -> 0x00e8 }
            r4 = r19
            r2 = r21
            r5 = r22
            r6 = r23
            goto L_0x0145
        L_0x021e:
            r19 = r4
            r22 = r5
            java.util.List<android.content.pm.PackageInfo> r2 = r8.mAllPackages
            r1 = r26
            r3 = r14
            r4 = r12
            r6 = r7
            r16 = r7
            r7 = r29
            r1.writeStateFile(r2, r3, r4, r6, r7)
            return
        L_0x0232:
            r0 = move-exception
            r16 = r7
        L_0x0235:
            android.util.Slog.e(r3, r1)
            return
        L_0x0239:
            r0 = move-exception
            android.util.Slog.e(r3, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.PackageManagerBackupAgent.onBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor):void");
    }

    private static void writeEntity(BackupDataOutput data, String key, byte[] bytes) throws IOException {
        data.writeEntityHeader(key, bytes.length);
        data.writeEntityData(bytes, bytes.length);
    }

    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        RestoreDataConsumer consumer = getRestoreDataConsumer(getAncestralRecordVersionValue(data));
        if (consumer == null) {
            Slog.w(TAG, "Ancestral restore set version is unknown to this Android version; not restoring");
        } else {
            consumer.consumeRestoreData(data);
        }
    }

    private int getAncestralRecordVersionValue(BackupDataInput data) throws IOException {
        if (!data.readNextHeader()) {
            return -1;
        }
        String key = data.getKey();
        int dataSize = data.getDataSize();
        if (!ANCESTRAL_RECORD_KEY.equals(key)) {
            return -1;
        }
        byte[] inputBytes = new byte[dataSize];
        data.readEntityData(inputBytes, 0, dataSize);
        return new DataInputStream(new ByteArrayInputStream(inputBytes)).readInt();
    }

    private RestoreDataConsumer getRestoreDataConsumer(int ancestralRecordVersion) {
        if (ancestralRecordVersion == -1) {
            return new LegacyRestoreDataConsumer();
        }
        if (ancestralRecordVersion == 1) {
            return new AncestralVersion1RestoreDataConsumer();
        }
        Slog.e(TAG, "Unrecognized ANCESTRAL_RECORD_VERSION: " + ancestralRecordVersion);
        return null;
    }

    private static void writeSignatureHashArray(DataOutputStream out, ArrayList<byte[]> hashes) throws IOException {
        out.writeInt(hashes.size());
        Iterator<byte[]> it = hashes.iterator();
        while (it.hasNext()) {
            byte[] buffer = it.next();
            out.writeInt(buffer.length);
            out.write(buffer);
        }
    }

    /* access modifiers changed from: private */
    public static ArrayList<byte[]> readSignatureHashArray(DataInputStream in) {
        try {
            int num = in.readInt();
            if (num <= 20) {
                boolean nonHashFound = false;
                try {
                    ArrayList<byte[]> sigs = new ArrayList<>(num);
                    for (int i = 0; i < num; i++) {
                        int len = in.readInt();
                        byte[] readHash = new byte[len];
                        in.read(readHash);
                        sigs.add(readHash);
                        if (len != 32) {
                            nonHashFound = true;
                        }
                    }
                    if (nonHashFound) {
                        return BackupUtils.hashSignatureArray((List<byte[]>) sigs);
                    }
                    return sigs;
                } catch (IOException e) {
                    Slog.e(TAG, "Unable to read signatures");
                    return null;
                }
            } else {
                Slog.e(TAG, "Suspiciously large sig count in restore data; aborting");
                throw new IllegalStateException("Bad restore state");
            }
        } catch (EOFException e2) {
            Slog.w(TAG, "Read empty signature block");
            return null;
        }
    }

    private void parseStateFile(ParcelFileDescriptor stateFile) {
        long versionCode;
        this.mExisting.clear();
        this.mStateVersions.clear();
        this.mStoredSdkVersion = 0;
        this.mStoredIncrementalVersion = null;
        this.mStoredHomeComponent = null;
        this.mStoredHomeVersion = 0;
        this.mStoredHomeSigHashes = null;
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(stateFile.getFileDescriptor())));
        boolean ignoreExisting = false;
        try {
            String pkg = in.readUTF();
            if (pkg.equals(STATE_FILE_HEADER)) {
                int stateVersion = in.readInt();
                if (stateVersion > 2) {
                    Slog.w(TAG, "Unsupported state file version " + stateVersion + ", redoing from start");
                    return;
                }
                pkg = in.readUTF();
            } else {
                Slog.i(TAG, "Older version of saved state - rewriting");
                ignoreExisting = true;
            }
            if (pkg.equals(DEFAULT_HOME_KEY)) {
                this.mStoredHomeComponent = ComponentName.unflattenFromString(in.readUTF());
                this.mStoredHomeVersion = in.readLong();
                this.mStoredHomeSigHashes = readSignatureHashArray(in);
                pkg = in.readUTF();
            }
            if (pkg.equals(GLOBAL_METADATA_KEY)) {
                this.mStoredSdkVersion = in.readInt();
                this.mStoredIncrementalVersion = in.readUTF();
                if (!ignoreExisting) {
                    this.mExisting.add(GLOBAL_METADATA_KEY);
                }
                while (true) {
                    String pkg2 = in.readUTF();
                    int versionCodeInt = in.readInt();
                    if (versionCodeInt == Integer.MIN_VALUE) {
                        versionCode = in.readLong();
                    } else {
                        versionCode = (long) versionCodeInt;
                    }
                    if (!ignoreExisting) {
                        this.mExisting.add(pkg2);
                    }
                    this.mStateVersions.put(pkg2, new Metadata(versionCode, (ArrayList<byte[]>) null));
                }
            } else {
                Slog.e(TAG, "No global metadata in state file!");
            }
        } catch (EOFException e) {
        } catch (IOException e2) {
            Slog.e(TAG, "Unable to read Package Manager state file: " + e2);
        }
    }

    private ComponentName getPreferredHomeComponent() {
        return this.mPackageManager.getHomeActivities(new ArrayList());
    }

    private void writeStateFile(List<PackageInfo> pkgs, ComponentName preferredHome, long homeVersion, ArrayList<byte[]> homeSigHashes, ParcelFileDescriptor stateFile) {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(stateFile.getFileDescriptor())));
        try {
            out.writeUTF(STATE_FILE_HEADER);
            out.writeInt(2);
            if (preferredHome != null) {
                out.writeUTF(DEFAULT_HOME_KEY);
                out.writeUTF(preferredHome.flattenToString());
                out.writeLong(homeVersion);
                writeSignatureHashArray(out, homeSigHashes);
            }
            out.writeUTF(GLOBAL_METADATA_KEY);
            out.writeInt(Build.VERSION.SDK_INT);
            out.writeUTF(Build.VERSION.INCREMENTAL);
            for (PackageInfo pkg : pkgs) {
                out.writeUTF(pkg.packageName);
                if (pkg.versionCodeMajor != 0) {
                    out.writeInt(Integer.MIN_VALUE);
                    out.writeLong(pkg.getLongVersionCode());
                } else {
                    out.writeInt(pkg.versionCode);
                }
            }
            out.flush();
        } catch (IOException e) {
            Slog.e(TAG, "Unable to write package manager state file!");
        }
    }

    private class LegacyRestoreDataConsumer implements RestoreDataConsumer {
        private LegacyRestoreDataConsumer() {
        }

        public void consumeRestoreData(BackupDataInput data) throws IOException {
            List<ApplicationInfo> restoredApps;
            long versionCode;
            List<ApplicationInfo> restoredApps2 = new ArrayList<>();
            HashMap<String, Metadata> sigMap = new HashMap<>();
            while (true) {
                String key = data.getKey();
                int dataSize = data.getDataSize();
                byte[] inputBytes = new byte[dataSize];
                data.readEntityData(inputBytes, 0, dataSize);
                DataInputStream inputBufferStream = new DataInputStream(new ByteArrayInputStream(inputBytes));
                if (key.equals(PackageManagerBackupAgent.GLOBAL_METADATA_KEY)) {
                    int unused = PackageManagerBackupAgent.this.mStoredSdkVersion = inputBufferStream.readInt();
                    String unused2 = PackageManagerBackupAgent.this.mStoredIncrementalVersion = inputBufferStream.readUTF();
                    boolean unused3 = PackageManagerBackupAgent.this.mHasMetadata = true;
                    restoredApps = restoredApps2;
                } else if (key.equals(PackageManagerBackupAgent.DEFAULT_HOME_KEY)) {
                    ComponentName unused4 = PackageManagerBackupAgent.this.mRestoredHome = ComponentName.unflattenFromString(inputBufferStream.readUTF());
                    long unused5 = PackageManagerBackupAgent.this.mRestoredHomeVersion = inputBufferStream.readLong();
                    String unused6 = PackageManagerBackupAgent.this.mRestoredHomeInstaller = inputBufferStream.readUTF();
                    ArrayList unused7 = PackageManagerBackupAgent.this.mRestoredHomeSigHashes = PackageManagerBackupAgent.readSignatureHashArray(inputBufferStream);
                    restoredApps = restoredApps2;
                } else {
                    int versionCodeInt = inputBufferStream.readInt();
                    if (versionCodeInt == Integer.MIN_VALUE) {
                        versionCode = inputBufferStream.readLong();
                    } else {
                        versionCode = (long) versionCodeInt;
                    }
                    ArrayList<byte[]> sigs = PackageManagerBackupAgent.readSignatureHashArray(inputBufferStream);
                    if (sigs == null || sigs.size() == 0) {
                        List<ApplicationInfo> restoredApps3 = restoredApps2;
                        Slog.w(PackageManagerBackupAgent.TAG, "Not restoring package " + key + " since it appears to have no signatures.");
                        restoredApps2 = restoredApps3;
                    } else {
                        ApplicationInfo app = new ApplicationInfo();
                        app.packageName = key;
                        restoredApps2.add(app);
                        restoredApps = restoredApps2;
                        sigMap.put(key, new Metadata(versionCode, sigs));
                    }
                }
                if (!data.readNextHeader()) {
                    HashMap unused8 = PackageManagerBackupAgent.this.mRestoredSignatures = sigMap;
                    return;
                }
                restoredApps2 = restoredApps;
            }
        }
    }

    private class AncestralVersion1RestoreDataConsumer implements RestoreDataConsumer {
        private AncestralVersion1RestoreDataConsumer() {
        }

        public void consumeRestoreData(BackupDataInput data) throws IOException {
            List<ApplicationInfo> restoredApps;
            long versionCode;
            List<ApplicationInfo> restoredApps2 = new ArrayList<>();
            HashMap<String, Metadata> sigMap = new HashMap<>();
            while (data.readNextHeader()) {
                String key = data.getKey();
                int dataSize = data.getDataSize();
                byte[] inputBytes = new byte[dataSize];
                data.readEntityData(inputBytes, 0, dataSize);
                DataInputStream inputBufferStream = new DataInputStream(new ByteArrayInputStream(inputBytes));
                if (key.equals(PackageManagerBackupAgent.GLOBAL_METADATA_KEY)) {
                    int unused = PackageManagerBackupAgent.this.mStoredSdkVersion = inputBufferStream.readInt();
                    String unused2 = PackageManagerBackupAgent.this.mStoredIncrementalVersion = inputBufferStream.readUTF();
                    boolean unused3 = PackageManagerBackupAgent.this.mHasMetadata = true;
                    restoredApps = restoredApps2;
                } else if (key.equals(PackageManagerBackupAgent.DEFAULT_HOME_KEY)) {
                    ComponentName unused4 = PackageManagerBackupAgent.this.mRestoredHome = ComponentName.unflattenFromString(inputBufferStream.readUTF());
                    long unused5 = PackageManagerBackupAgent.this.mRestoredHomeVersion = inputBufferStream.readLong();
                    String unused6 = PackageManagerBackupAgent.this.mRestoredHomeInstaller = inputBufferStream.readUTF();
                    ArrayList unused7 = PackageManagerBackupAgent.this.mRestoredHomeSigHashes = PackageManagerBackupAgent.readSignatureHashArray(inputBufferStream);
                    restoredApps = restoredApps2;
                } else {
                    int versionCodeInt = inputBufferStream.readInt();
                    if (versionCodeInt == Integer.MIN_VALUE) {
                        versionCode = inputBufferStream.readLong();
                    } else {
                        versionCode = (long) versionCodeInt;
                    }
                    ArrayList<byte[]> sigs = PackageManagerBackupAgent.readSignatureHashArray(inputBufferStream);
                    if (sigs == null || sigs.size() == 0) {
                        List<ApplicationInfo> restoredApps3 = restoredApps2;
                        Slog.w(PackageManagerBackupAgent.TAG, "Not restoring package " + key + " since it appears to have no signatures.");
                        restoredApps2 = restoredApps3;
                    } else {
                        ApplicationInfo app = new ApplicationInfo();
                        app.packageName = key;
                        restoredApps2.add(app);
                        restoredApps = restoredApps2;
                        sigMap.put(key, new Metadata(versionCode, sigs));
                    }
                }
                restoredApps2 = restoredApps;
            }
            HashMap unused8 = PackageManagerBackupAgent.this.mRestoredSignatures = sigMap;
        }
    }
}
