package android.os;

import java.io.FileDescriptor;

public interface IInstalld extends IInterface {
    public static final int FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES = 131072;
    public static final int FLAG_CLEAR_CACHE_ONLY = 16;
    public static final int FLAG_CLEAR_CODE_CACHE_ONLY = 32;
    public static final int FLAG_FORCE = 8192;
    public static final int FLAG_FREE_CACHE_NOOP = 1024;
    public static final int FLAG_FREE_CACHE_V2 = 256;
    public static final int FLAG_FREE_CACHE_V2_DEFY_QUOTA = 512;
    public static final int FLAG_STORAGE_CE = 2;
    public static final int FLAG_STORAGE_DE = 1;
    public static final int FLAG_STORAGE_EXTERNAL = 4;
    public static final int FLAG_USE_QUOTA = 4096;

    void assertFsverityRootHashMatches(String str, byte[] bArr) throws RemoteException;

    void clearAppData(String str, String str2, int i, int i2, long j) throws RemoteException;

    void clearAppProfiles(String str, String str2) throws RemoteException;

    boolean compileLayouts(String str, String str2, String str3, int i) throws RemoteException;

    boolean copySystemProfile(String str, int i, String str2, String str3) throws RemoteException;

    long createAppData(String str, String str2, int i, int i2, int i3, String str3, int i4) throws RemoteException;

    void createOatDir(String str, String str2) throws RemoteException;

    boolean createProfileSnapshot(int i, String str, String str2, String str3) throws RemoteException;

    void createUserData(String str, int i, int i2, int i3) throws RemoteException;

    void deleteOdex(String str, String str2, String str3) throws RemoteException;

    void destroyAppData(String str, String str2, int i, int i2, long j) throws RemoteException;

    void destroyAppDataSnapshot(String str, String str2, int i, long j, int i2, int i3) throws RemoteException;

    void destroyAppProfiles(String str) throws RemoteException;

    void destroyProfileSnapshot(String str, String str2) throws RemoteException;

    void destroyUserData(String str, int i, int i2) throws RemoteException;

    void dexopt(String str, int i, String str2, String str3, int i2, String str4, int i3, String str5, String str6, String str7, String str8, boolean z, int i4, String str9, String str10, String str11) throws RemoteException;

    boolean dumpProfiles(int i, String str, String str2, String str3) throws RemoteException;

    void fixupAppData(String str, int i) throws RemoteException;

    void freeCache(String str, long j, long j2, int i) throws RemoteException;

    long[] getAppSize(String str, String[] strArr, int i, int i2, int i3, long[] jArr, String[] strArr2) throws RemoteException;

    long[] getExternalSize(String str, int i, int i2, int[] iArr) throws RemoteException;

    long[] getUserSize(String str, int i, int i2, int[] iArr) throws RemoteException;

    byte[] hashSecondaryDexFile(String str, String str2, int i, String str3, int i2) throws RemoteException;

    void idmap(String str, String str2, int i) throws RemoteException;

    void installApkVerity(String str, FileDescriptor fileDescriptor, int i) throws RemoteException;

    void invalidateMounts() throws RemoteException;

    boolean isQuotaSupported(String str) throws RemoteException;

    void linkFile(String str, String str2, String str3) throws RemoteException;

    void linkNativeLibraryDirectory(String str, String str2, String str3, int i) throws RemoteException;

    void markBootComplete(String str) throws RemoteException;

    boolean mergeProfiles(int i, String str, String str2) throws RemoteException;

    void migrateAppData(String str, String str2, int i, int i2) throws RemoteException;

    void migrateLegacyObbData() throws RemoteException;

    void moveAb(String str, String str2, String str3) throws RemoteException;

    void moveCompleteApp(String str, String str2, String str3, String str4, int i, String str5, int i2) throws RemoteException;

    boolean prepareAppProfile(String str, int i, int i2, String str2, String str3, String str4) throws RemoteException;

    boolean reconcileSecondaryDexFile(String str, String str2, int i, String[] strArr, String str3, int i2) throws RemoteException;

    void removeIdmap(String str) throws RemoteException;

    void restoreAppDataSnapshot(String str, String str2, int i, String str3, int i2, int i3, int i4) throws RemoteException;

    void restoreconAppData(String str, String str2, int i, int i2, int i3, String str3) throws RemoteException;

    void rmPackageDir(String str) throws RemoteException;

    void rmdex(String str, String str2) throws RemoteException;

    void setAppQuota(String str, int i, int i2, long j) throws RemoteException;

    long snapshotAppData(String str, String str2, int i, int i2, int i3) throws RemoteException;

    public static class Default implements IInstalld {
        public void createUserData(String uuid, int userId, int userSerial, int flags) throws RemoteException {
        }

        public void destroyUserData(String uuid, int userId, int flags) throws RemoteException {
        }

        public long createAppData(String uuid, String packageName, int userId, int flags, int appId, String seInfo, int targetSdkVersion) throws RemoteException {
            return 0;
        }

        public void restoreconAppData(String uuid, String packageName, int userId, int flags, int appId, String seInfo) throws RemoteException {
        }

        public void migrateAppData(String uuid, String packageName, int userId, int flags) throws RemoteException {
        }

        public void clearAppData(String uuid, String packageName, int userId, int flags, long ceDataInode) throws RemoteException {
        }

        public void destroyAppData(String uuid, String packageName, int userId, int flags, long ceDataInode) throws RemoteException {
        }

        public void fixupAppData(String uuid, int flags) throws RemoteException {
        }

        public long[] getAppSize(String uuid, String[] packageNames, int userId, int flags, int appId, long[] ceDataInodes, String[] codePaths) throws RemoteException {
            return null;
        }

        public long[] getUserSize(String uuid, int userId, int flags, int[] appIds) throws RemoteException {
            return null;
        }

        public long[] getExternalSize(String uuid, int userId, int flags, int[] appIds) throws RemoteException {
            return null;
        }

        public void setAppQuota(String uuid, int userId, int appId, long cacheQuota) throws RemoteException {
        }

        public void moveCompleteApp(String fromUuid, String toUuid, String packageName, String dataAppName, int appId, String seInfo, int targetSdkVersion) throws RemoteException {
        }

        public void dexopt(String apkPath, int uid, String packageName, String instructionSet, int dexoptNeeded, String outputPath, int dexFlags, String compilerFilter, String uuid, String sharedLibraries, String seInfo, boolean downgrade, int targetSdkVersion, String profileName, String dexMetadataPath, String compilationReason) throws RemoteException {
        }

        public boolean compileLayouts(String apkPath, String packageName, String outDexFile, int uid) throws RemoteException {
            return false;
        }

        public void rmdex(String codePath, String instructionSet) throws RemoteException {
        }

        public boolean mergeProfiles(int uid, String packageName, String profileName) throws RemoteException {
            return false;
        }

        public boolean dumpProfiles(int uid, String packageName, String profileName, String codePath) throws RemoteException {
            return false;
        }

        public boolean copySystemProfile(String systemProfile, int uid, String packageName, String profileName) throws RemoteException {
            return false;
        }

        public void clearAppProfiles(String packageName, String profileName) throws RemoteException {
        }

        public void destroyAppProfiles(String packageName) throws RemoteException {
        }

        public boolean createProfileSnapshot(int appId, String packageName, String profileName, String classpath) throws RemoteException {
            return false;
        }

        public void destroyProfileSnapshot(String packageName, String profileName) throws RemoteException {
        }

        public void idmap(String targetApkPath, String overlayApkPath, int uid) throws RemoteException {
        }

        public void removeIdmap(String overlayApkPath) throws RemoteException {
        }

        public void rmPackageDir(String packageDir) throws RemoteException {
        }

        public void markBootComplete(String instructionSet) throws RemoteException {
        }

        public void freeCache(String uuid, long targetFreeBytes, long cacheReservedBytes, int flags) throws RemoteException {
        }

        public void linkNativeLibraryDirectory(String uuid, String packageName, String nativeLibPath32, int userId) throws RemoteException {
        }

        public void createOatDir(String oatDir, String instructionSet) throws RemoteException {
        }

        public void linkFile(String relativePath, String fromBase, String toBase) throws RemoteException {
        }

        public void moveAb(String apkPath, String instructionSet, String outputPath) throws RemoteException {
        }

        public void deleteOdex(String apkPath, String instructionSet, String outputPath) throws RemoteException {
        }

        public void installApkVerity(String filePath, FileDescriptor verityInput, int contentSize) throws RemoteException {
        }

        public void assertFsverityRootHashMatches(String filePath, byte[] expectedHash) throws RemoteException {
        }

        public boolean reconcileSecondaryDexFile(String dexPath, String pkgName, int uid, String[] isas, String volume_uuid, int storage_flag) throws RemoteException {
            return false;
        }

        public byte[] hashSecondaryDexFile(String dexPath, String pkgName, int uid, String volumeUuid, int storageFlag) throws RemoteException {
            return null;
        }

        public void invalidateMounts() throws RemoteException {
        }

        public boolean isQuotaSupported(String uuid) throws RemoteException {
            return false;
        }

        public boolean prepareAppProfile(String packageName, int userId, int appId, String profileName, String codePath, String dexMetadata) throws RemoteException {
            return false;
        }

        public long snapshotAppData(String uuid, String packageName, int userId, int snapshotId, int storageFlags) throws RemoteException {
            return 0;
        }

        public void restoreAppDataSnapshot(String uuid, String packageName, int appId, String seInfo, int user, int snapshotId, int storageflags) throws RemoteException {
        }

        public void destroyAppDataSnapshot(String uuid, String packageName, int userId, long ceSnapshotInode, int snapshotId, int storageFlags) throws RemoteException {
        }

        public void migrateLegacyObbData() throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IInstalld {
        private static final String DESCRIPTOR = "android.os.IInstalld";
        static final int TRANSACTION_assertFsverityRootHashMatches = 35;
        static final int TRANSACTION_clearAppData = 6;
        static final int TRANSACTION_clearAppProfiles = 20;
        static final int TRANSACTION_compileLayouts = 15;
        static final int TRANSACTION_copySystemProfile = 19;
        static final int TRANSACTION_createAppData = 3;
        static final int TRANSACTION_createOatDir = 30;
        static final int TRANSACTION_createProfileSnapshot = 22;
        static final int TRANSACTION_createUserData = 1;
        static final int TRANSACTION_deleteOdex = 33;
        static final int TRANSACTION_destroyAppData = 7;
        static final int TRANSACTION_destroyAppDataSnapshot = 43;
        static final int TRANSACTION_destroyAppProfiles = 21;
        static final int TRANSACTION_destroyProfileSnapshot = 23;
        static final int TRANSACTION_destroyUserData = 2;
        static final int TRANSACTION_dexopt = 14;
        static final int TRANSACTION_dumpProfiles = 18;
        static final int TRANSACTION_fixupAppData = 8;
        static final int TRANSACTION_freeCache = 28;
        static final int TRANSACTION_getAppSize = 9;
        static final int TRANSACTION_getExternalSize = 11;
        static final int TRANSACTION_getUserSize = 10;
        static final int TRANSACTION_hashSecondaryDexFile = 37;
        static final int TRANSACTION_idmap = 24;
        static final int TRANSACTION_installApkVerity = 34;
        static final int TRANSACTION_invalidateMounts = 38;
        static final int TRANSACTION_isQuotaSupported = 39;
        static final int TRANSACTION_linkFile = 31;
        static final int TRANSACTION_linkNativeLibraryDirectory = 29;
        static final int TRANSACTION_markBootComplete = 27;
        static final int TRANSACTION_mergeProfiles = 17;
        static final int TRANSACTION_migrateAppData = 5;
        static final int TRANSACTION_migrateLegacyObbData = 44;
        static final int TRANSACTION_moveAb = 32;
        static final int TRANSACTION_moveCompleteApp = 13;
        static final int TRANSACTION_prepareAppProfile = 40;
        static final int TRANSACTION_reconcileSecondaryDexFile = 36;
        static final int TRANSACTION_removeIdmap = 25;
        static final int TRANSACTION_restoreAppDataSnapshot = 42;
        static final int TRANSACTION_restoreconAppData = 4;
        static final int TRANSACTION_rmPackageDir = 26;
        static final int TRANSACTION_rmdex = 16;
        static final int TRANSACTION_setAppQuota = 12;
        static final int TRANSACTION_snapshotAppData = 41;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInstalld asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IInstalld)) {
                return new Proxy(obj);
            }
            return (IInstalld) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
            java.lang.NullPointerException
            	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
            */
        public boolean onTransact(int r35, android.os.Parcel r36, android.os.Parcel r37, int r38) throws android.os.RemoteException {
            /*
                r34 = this;
                r11 = r34
                r10 = r35
                r9 = r36
                r8 = r37
                java.lang.String r15 = "android.os.IInstalld"
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r17 = 1
                if (r10 == r0) goto L_0x0591
                switch(r10) {
                    case 1: goto L_0x0573;
                    case 2: goto L_0x0559;
                    case 3: goto L_0x051f;
                    case 4: goto L_0x04f0;
                    case 5: goto L_0x04d2;
                    case 6: goto L_0x04a9;
                    case 7: goto L_0x0480;
                    case 8: goto L_0x046a;
                    case 9: goto L_0x0430;
                    case 10: goto L_0x040e;
                    case 11: goto L_0x03ea;
                    case 12: goto L_0x03c8;
                    case 13: goto L_0x0392;
                    case 14: goto L_0x031e;
                    case 15: goto L_0x0300;
                    case 16: goto L_0x02ee;
                    case 17: goto L_0x02d4;
                    case 18: goto L_0x02b6;
                    case 19: goto L_0x0298;
                    case 20: goto L_0x0286;
                    case 21: goto L_0x0278;
                    case 22: goto L_0x025a;
                    case 23: goto L_0x0248;
                    case 24: goto L_0x0232;
                    case 25: goto L_0x0224;
                    case 26: goto L_0x0216;
                    case 27: goto L_0x0208;
                    case 28: goto L_0x01e7;
                    case 29: goto L_0x01cd;
                    case 30: goto L_0x01bb;
                    case 31: goto L_0x01a5;
                    case 32: goto L_0x018f;
                    case 33: goto L_0x0179;
                    case 34: goto L_0x0163;
                    case 35: goto L_0x0151;
                    case 36: goto L_0x0121;
                    case 37: goto L_0x00f8;
                    case 38: goto L_0x00ee;
                    case 39: goto L_0x00dc;
                    case 40: goto L_0x00ac;
                    case 41: goto L_0x0083;
                    case 42: goto L_0x0050;
                    case 43: goto L_0x0023;
                    case 44: goto L_0x0019;
                    default: goto L_0x0014;
                }
            L_0x0014:
                boolean r0 = super.onTransact(r35, r36, r37, r38)
                return r0
            L_0x0019:
                r9.enforceInterface(r15)
                r34.migrateLegacyObbData()
                r37.writeNoException()
                return r17
            L_0x0023:
                r9.enforceInterface(r15)
                java.lang.String r12 = r36.readString()
                java.lang.String r13 = r36.readString()
                int r14 = r36.readInt()
                long r18 = r36.readLong()
                int r16 = r36.readInt()
                int r20 = r36.readInt()
                r0 = r34
                r1 = r12
                r2 = r13
                r3 = r14
                r4 = r18
                r6 = r16
                r7 = r20
                r0.destroyAppDataSnapshot(r1, r2, r3, r4, r6, r7)
                r37.writeNoException()
                return r17
            L_0x0050:
                r9.enforceInterface(r15)
                java.lang.String r12 = r36.readString()
                java.lang.String r13 = r36.readString()
                int r14 = r36.readInt()
                java.lang.String r16 = r36.readString()
                int r18 = r36.readInt()
                int r19 = r36.readInt()
                int r20 = r36.readInt()
                r0 = r34
                r1 = r12
                r2 = r13
                r3 = r14
                r4 = r16
                r5 = r18
                r6 = r19
                r7 = r20
                r0.restoreAppDataSnapshot(r1, r2, r3, r4, r5, r6, r7)
                r37.writeNoException()
                return r17
            L_0x0083:
                r9.enforceInterface(r15)
                java.lang.String r6 = r36.readString()
                java.lang.String r7 = r36.readString()
                int r12 = r36.readInt()
                int r13 = r36.readInt()
                int r14 = r36.readInt()
                r0 = r34
                r1 = r6
                r2 = r7
                r3 = r12
                r4 = r13
                r5 = r14
                long r0 = r0.snapshotAppData(r1, r2, r3, r4, r5)
                r37.writeNoException()
                r8.writeLong(r0)
                return r17
            L_0x00ac:
                r9.enforceInterface(r15)
                java.lang.String r7 = r36.readString()
                int r12 = r36.readInt()
                int r13 = r36.readInt()
                java.lang.String r14 = r36.readString()
                java.lang.String r16 = r36.readString()
                java.lang.String r18 = r36.readString()
                r0 = r34
                r1 = r7
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r16
                r6 = r18
                boolean r0 = r0.prepareAppProfile(r1, r2, r3, r4, r5, r6)
                r37.writeNoException()
                r8.writeInt(r0)
                return r17
            L_0x00dc:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                boolean r1 = r11.isQuotaSupported(r0)
                r37.writeNoException()
                r8.writeInt(r1)
                return r17
            L_0x00ee:
                r9.enforceInterface(r15)
                r34.invalidateMounts()
                r37.writeNoException()
                return r17
            L_0x00f8:
                r9.enforceInterface(r15)
                java.lang.String r6 = r36.readString()
                java.lang.String r7 = r36.readString()
                int r12 = r36.readInt()
                java.lang.String r13 = r36.readString()
                int r14 = r36.readInt()
                r0 = r34
                r1 = r6
                r2 = r7
                r3 = r12
                r4 = r13
                r5 = r14
                byte[] r0 = r0.hashSecondaryDexFile(r1, r2, r3, r4, r5)
                r37.writeNoException()
                r8.writeByteArray(r0)
                return r17
            L_0x0121:
                r9.enforceInterface(r15)
                java.lang.String r7 = r36.readString()
                java.lang.String r12 = r36.readString()
                int r13 = r36.readInt()
                java.lang.String[] r14 = r36.createStringArray()
                java.lang.String r16 = r36.readString()
                int r18 = r36.readInt()
                r0 = r34
                r1 = r7
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r16
                r6 = r18
                boolean r0 = r0.reconcileSecondaryDexFile(r1, r2, r3, r4, r5, r6)
                r37.writeNoException()
                r8.writeInt(r0)
                return r17
            L_0x0151:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                byte[] r1 = r36.createByteArray()
                r11.assertFsverityRootHashMatches(r0, r1)
                r37.writeNoException()
                return r17
            L_0x0163:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.io.FileDescriptor r1 = r36.readRawFileDescriptor()
                int r2 = r36.readInt()
                r11.installApkVerity(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x0179:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                r11.deleteOdex(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x018f:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                r11.moveAb(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x01a5:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                r11.linkFile(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x01bb:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                r11.createOatDir(r0, r1)
                r37.writeNoException()
                return r17
            L_0x01cd:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                int r3 = r36.readInt()
                r11.linkNativeLibraryDirectory(r0, r1, r2, r3)
                r37.writeNoException()
                return r17
            L_0x01e7:
                r9.enforceInterface(r15)
                java.lang.String r7 = r36.readString()
                long r12 = r36.readLong()
                long r18 = r36.readLong()
                int r14 = r36.readInt()
                r0 = r34
                r1 = r7
                r2 = r12
                r4 = r18
                r6 = r14
                r0.freeCache(r1, r2, r4, r6)
                r37.writeNoException()
                return r17
            L_0x0208:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                r11.markBootComplete(r0)
                r37.writeNoException()
                return r17
            L_0x0216:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                r11.rmPackageDir(r0)
                r37.writeNoException()
                return r17
            L_0x0224:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                r11.removeIdmap(r0)
                r37.writeNoException()
                return r17
            L_0x0232:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                int r2 = r36.readInt()
                r11.idmap(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x0248:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                r11.destroyProfileSnapshot(r0, r1)
                r37.writeNoException()
                return r17
            L_0x025a:
                r9.enforceInterface(r15)
                int r0 = r36.readInt()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                java.lang.String r3 = r36.readString()
                boolean r4 = r11.createProfileSnapshot(r0, r1, r2, r3)
                r37.writeNoException()
                r8.writeInt(r4)
                return r17
            L_0x0278:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                r11.destroyAppProfiles(r0)
                r37.writeNoException()
                return r17
            L_0x0286:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                r11.clearAppProfiles(r0, r1)
                r37.writeNoException()
                return r17
            L_0x0298:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                java.lang.String r2 = r36.readString()
                java.lang.String r3 = r36.readString()
                boolean r4 = r11.copySystemProfile(r0, r1, r2, r3)
                r37.writeNoException()
                r8.writeInt(r4)
                return r17
            L_0x02b6:
                r9.enforceInterface(r15)
                int r0 = r36.readInt()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                java.lang.String r3 = r36.readString()
                boolean r4 = r11.dumpProfiles(r0, r1, r2, r3)
                r37.writeNoException()
                r8.writeInt(r4)
                return r17
            L_0x02d4:
                r9.enforceInterface(r15)
                int r0 = r36.readInt()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                boolean r3 = r11.mergeProfiles(r0, r1, r2)
                r37.writeNoException()
                r8.writeInt(r3)
                return r17
            L_0x02ee:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                r11.rmdex(r0, r1)
                r37.writeNoException()
                return r17
            L_0x0300:
                r9.enforceInterface(r15)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                java.lang.String r2 = r36.readString()
                int r3 = r36.readInt()
                boolean r4 = r11.compileLayouts(r0, r1, r2, r3)
                r37.writeNoException()
                r8.writeInt(r4)
                return r17
            L_0x031e:
                r9.enforceInterface(r15)
                java.lang.String r18 = r36.readString()
                int r19 = r36.readInt()
                java.lang.String r20 = r36.readString()
                java.lang.String r21 = r36.readString()
                int r22 = r36.readInt()
                java.lang.String r23 = r36.readString()
                int r24 = r36.readInt()
                java.lang.String r25 = r36.readString()
                java.lang.String r26 = r36.readString()
                java.lang.String r27 = r36.readString()
                java.lang.String r28 = r36.readString()
                int r0 = r36.readInt()
                if (r0 == 0) goto L_0x0356
                r12 = r17
                goto L_0x0358
            L_0x0356:
                r0 = 0
                r12 = r0
            L_0x0358:
                int r29 = r36.readInt()
                r13 = r29
                java.lang.String r30 = r36.readString()
                r14 = r30
                java.lang.String r31 = r36.readString()
                r7 = r15
                r15 = r31
                java.lang.String r32 = r36.readString()
                r16 = r32
                r0 = r34
                r1 = r18
                r2 = r19
                r3 = r20
                r4 = r21
                r5 = r22
                r6 = r23
                r33 = r7
                r7 = r24
                r8 = r25
                r9 = r26
                r10 = r27
                r11 = r28
                r0.dexopt(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
                r37.writeNoException()
                return r17
            L_0x0392:
                r33 = r15
                r8 = r36
                r9 = r33
                r8.enforceInterface(r9)
                java.lang.String r10 = r36.readString()
                java.lang.String r11 = r36.readString()
                java.lang.String r12 = r36.readString()
                java.lang.String r13 = r36.readString()
                int r14 = r36.readInt()
                java.lang.String r15 = r36.readString()
                int r16 = r36.readInt()
                r0 = r34
                r1 = r10
                r2 = r11
                r3 = r12
                r4 = r13
                r5 = r14
                r6 = r15
                r7 = r16
                r0.moveCompleteApp(r1, r2, r3, r4, r5, r6, r7)
                r37.writeNoException()
                return r17
            L_0x03c8:
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r6 = r36.readString()
                int r7 = r36.readInt()
                int r10 = r36.readInt()
                long r11 = r36.readLong()
                r0 = r34
                r1 = r6
                r2 = r7
                r3 = r10
                r4 = r11
                r0.setAppQuota(r1, r2, r3, r4)
                r37.writeNoException()
                return r17
            L_0x03ea:
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                int r2 = r36.readInt()
                int[] r3 = r36.createIntArray()
                r10 = r34
                long[] r4 = r10.getExternalSize(r0, r1, r2, r3)
                r37.writeNoException()
                r11 = r37
                r11.writeLongArray(r4)
                return r17
            L_0x040e:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                int r2 = r36.readInt()
                int[] r3 = r36.createIntArray()
                long[] r4 = r10.getUserSize(r0, r1, r2, r3)
                r37.writeNoException()
                r11.writeLongArray(r4)
                return r17
            L_0x0430:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r12 = r36.readString()
                java.lang.String[] r13 = r36.createStringArray()
                int r14 = r36.readInt()
                int r15 = r36.readInt()
                int r16 = r36.readInt()
                long[] r18 = r36.createLongArray()
                java.lang.String[] r19 = r36.createStringArray()
                r0 = r34
                r1 = r12
                r2 = r13
                r3 = r14
                r4 = r15
                r5 = r16
                r6 = r18
                r7 = r19
                long[] r0 = r0.getAppSize(r1, r2, r3, r4, r5, r6, r7)
                r37.writeNoException()
                r11.writeLongArray(r0)
                return r17
            L_0x046a:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                r10.fixupAppData(r0, r1)
                r37.writeNoException()
                return r17
            L_0x0480:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r7 = r36.readString()
                java.lang.String r12 = r36.readString()
                int r13 = r36.readInt()
                int r14 = r36.readInt()
                long r15 = r36.readLong()
                r0 = r34
                r1 = r7
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r15
                r0.destroyAppData(r1, r2, r3, r4, r5)
                r37.writeNoException()
                return r17
            L_0x04a9:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r7 = r36.readString()
                java.lang.String r12 = r36.readString()
                int r13 = r36.readInt()
                int r14 = r36.readInt()
                long r15 = r36.readLong()
                r0 = r34
                r1 = r7
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r15
                r0.clearAppData(r1, r2, r3, r4, r5)
                r37.writeNoException()
                return r17
            L_0x04d2:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                java.lang.String r1 = r36.readString()
                int r2 = r36.readInt()
                int r3 = r36.readInt()
                r10.migrateAppData(r0, r1, r2, r3)
                r37.writeNoException()
                return r17
            L_0x04f0:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r7 = r36.readString()
                java.lang.String r12 = r36.readString()
                int r13 = r36.readInt()
                int r14 = r36.readInt()
                int r15 = r36.readInt()
                java.lang.String r16 = r36.readString()
                r0 = r34
                r1 = r7
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r15
                r6 = r16
                r0.restoreconAppData(r1, r2, r3, r4, r5, r6)
                r37.writeNoException()
                return r17
            L_0x051f:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r12 = r36.readString()
                java.lang.String r13 = r36.readString()
                int r14 = r36.readInt()
                int r15 = r36.readInt()
                int r16 = r36.readInt()
                java.lang.String r18 = r36.readString()
                int r19 = r36.readInt()
                r0 = r34
                r1 = r12
                r2 = r13
                r3 = r14
                r4 = r15
                r5 = r16
                r6 = r18
                r7 = r19
                long r0 = r0.createAppData(r1, r2, r3, r4, r5, r6, r7)
                r37.writeNoException()
                r11.writeLong(r0)
                return r17
            L_0x0559:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                int r2 = r36.readInt()
                r10.destroyUserData(r0, r1, r2)
                r37.writeNoException()
                return r17
            L_0x0573:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r8.enforceInterface(r9)
                java.lang.String r0 = r36.readString()
                int r1 = r36.readInt()
                int r2 = r36.readInt()
                int r3 = r36.readInt()
                r10.createUserData(r0, r1, r2, r3)
                r37.writeNoException()
                return r17
            L_0x0591:
                r10 = r11
                r11 = r8
                r8 = r9
                r9 = r15
                r11.writeString(r9)
                return r17
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.IInstalld.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IInstalld {
            public static IInstalld sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void createUserData(String uuid, int userId, int userSerial, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(userId);
                    _data.writeInt(userSerial);
                    _data.writeInt(flags);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().createUserData(uuid, userId, userSerial, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void destroyUserData(String uuid, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().destroyUserData(uuid, userId, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long createAppData(String uuid, String packageName, int userId, int flags, int appId, String seInfo, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = userId;
                        int i2 = flags;
                        int i3 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th2) {
                        th = th2;
                        int i4 = userId;
                        int i22 = flags;
                        int i32 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeInt(flags);
                        } catch (Throwable th3) {
                            th = th3;
                            int i322 = appId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(appId);
                            _data.writeString(seInfo);
                            _data.writeInt(targetSdkVersion);
                            if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                long _result = _reply.readLong();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            long createAppData = Stub.getDefaultImpl().createAppData(uuid, packageName, userId, flags, appId, seInfo, targetSdkVersion);
                            _reply.recycle();
                            _data.recycle();
                            return createAppData;
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i222 = flags;
                        int i3222 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str2 = uuid;
                    String str3 = packageName;
                    int i42 = userId;
                    int i2222 = flags;
                    int i32222 = appId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void restoreconAppData(String uuid, String packageName, int userId, int flags, int appId, String seInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = userId;
                        int i2 = flags;
                        int i3 = appId;
                        String str2 = seInfo;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                        try {
                            _data.writeInt(userId);
                            try {
                                _data.writeInt(flags);
                            } catch (Throwable th2) {
                                th = th2;
                                int i32 = appId;
                                String str22 = seInfo;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            int i22 = flags;
                            int i322 = appId;
                            String str222 = seInfo;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i4 = userId;
                        int i222 = flags;
                        int i3222 = appId;
                        String str2222 = seInfo;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(appId);
                        try {
                            _data.writeString(seInfo);
                            if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().restoreconAppData(uuid, packageName, userId, flags, appId, seInfo);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        String str22222 = seInfo;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str3 = uuid;
                    String str4 = packageName;
                    int i42 = userId;
                    int i2222 = flags;
                    int i32222 = appId;
                    String str222222 = seInfo;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void migrateAppData(String uuid, String packageName, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().migrateAppData(uuid, packageName, userId, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearAppData(String uuid, String packageName, int userId, int flags, long ceDataInode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = userId;
                        int i2 = flags;
                        long j = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th2) {
                        th = th2;
                        int i3 = userId;
                        int i22 = flags;
                        long j2 = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeInt(flags);
                        } catch (Throwable th3) {
                            th = th3;
                            long j22 = ceDataInode;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeLong(ceDataInode);
                            if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().clearAppData(uuid, packageName, userId, flags, ceDataInode);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i222 = flags;
                        long j222 = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str2 = uuid;
                    String str3 = packageName;
                    int i32 = userId;
                    int i2222 = flags;
                    long j2222 = ceDataInode;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void destroyAppData(String uuid, String packageName, int userId, int flags, long ceDataInode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = userId;
                        int i2 = flags;
                        long j = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th2) {
                        th = th2;
                        int i3 = userId;
                        int i22 = flags;
                        long j2 = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeInt(flags);
                        } catch (Throwable th3) {
                            th = th3;
                            long j22 = ceDataInode;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeLong(ceDataInode);
                            if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().destroyAppData(uuid, packageName, userId, flags, ceDataInode);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i222 = flags;
                        long j222 = ceDataInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str2 = uuid;
                    String str3 = packageName;
                    int i32 = userId;
                    int i2222 = flags;
                    long j2222 = ceDataInode;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void fixupAppData(String uuid, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(flags);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().fixupAppData(uuid, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long[] getAppSize(String uuid, String[] packageNames, int userId, int flags, int appId, long[] ceDataInodes, String[] codePaths) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String[] strArr = packageNames;
                        int i = userId;
                        int i2 = flags;
                        int i3 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeStringArray(packageNames);
                    } catch (Throwable th2) {
                        th = th2;
                        int i4 = userId;
                        int i22 = flags;
                        int i32 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeInt(flags);
                        } catch (Throwable th3) {
                            th = th3;
                            int i322 = appId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(appId);
                            _data.writeLongArray(ceDataInodes);
                            _data.writeStringArray(codePaths);
                            if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                long[] _result = _reply.createLongArray();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            long[] appSize = Stub.getDefaultImpl().getAppSize(uuid, packageNames, userId, flags, appId, ceDataInodes, codePaths);
                            _reply.recycle();
                            _data.recycle();
                            return appSize;
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i222 = flags;
                        int i3222 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str = uuid;
                    String[] strArr2 = packageNames;
                    int i42 = userId;
                    int i2222 = flags;
                    int i32222 = appId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public long[] getUserSize(String uuid, int userId, int flags, int[] appIds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    _data.writeIntArray(appIds);
                    if (!this.mRemote.transact(10, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUserSize(uuid, userId, flags, appIds);
                    }
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long[] getExternalSize(String uuid, int userId, int flags, int[] appIds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    _data.writeIntArray(appIds);
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getExternalSize(uuid, userId, flags, appIds);
                    }
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setAppQuota(String uuid, int userId, int appId, long cacheQuota) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(userId);
                    _data.writeInt(appId);
                    _data.writeLong(cacheQuota);
                    if (this.mRemote.transact(12, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setAppQuota(uuid, userId, appId, cacheQuota);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void moveCompleteApp(String fromUuid, String toUuid, String packageName, String dataAppName, int appId, String seInfo, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(fromUuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = toUuid;
                        String str2 = packageName;
                        String str3 = dataAppName;
                        int i = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(toUuid);
                    } catch (Throwable th2) {
                        th = th2;
                        String str22 = packageName;
                        String str32 = dataAppName;
                        int i2 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                        try {
                            _data.writeString(dataAppName);
                        } catch (Throwable th3) {
                            th = th3;
                            int i22 = appId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(appId);
                            _data.writeString(seInfo);
                            _data.writeInt(targetSdkVersion);
                            if (this.mRemote.transact(13, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().moveCompleteApp(fromUuid, toUuid, packageName, dataAppName, appId, seInfo, targetSdkVersion);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        String str322 = dataAppName;
                        int i222 = appId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str4 = fromUuid;
                    String str5 = toUuid;
                    String str222 = packageName;
                    String str3222 = dataAppName;
                    int i2222 = appId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void dexopt(String apkPath, int uid, String packageName, String instructionSet, int dexoptNeeded, String outputPath, int dexFlags, String compilerFilter, String uuid, String sharedLibraries, String seInfo, boolean downgrade, int targetSdkVersion, String profileName, String dexMetadataPath, String compilationReason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(apkPath);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(instructionSet);
                    _data.writeInt(dexoptNeeded);
                    _data.writeString(outputPath);
                    _data.writeInt(dexFlags);
                    _data.writeString(compilerFilter);
                    _data.writeString(uuid);
                    _data.writeString(sharedLibraries);
                    _data.writeString(seInfo);
                    _data.writeInt(downgrade ? 1 : 0);
                    _data.writeInt(targetSdkVersion);
                    _data.writeString(profileName);
                    _data.writeString(dexMetadataPath);
                    _data.writeString(compilationReason);
                    if (this.mRemote.transact(14, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().dexopt(apkPath, uid, packageName, instructionSet, dexoptNeeded, outputPath, dexFlags, compilerFilter, uuid, sharedLibraries, seInfo, downgrade, targetSdkVersion, profileName, dexMetadataPath, compilationReason);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean compileLayouts(String apkPath, String packageName, String outDexFile, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(apkPath);
                    _data.writeString(packageName);
                    _data.writeString(outDexFile);
                    _data.writeInt(uid);
                    boolean z = false;
                    if (!this.mRemote.transact(15, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().compileLayouts(apkPath, packageName, outDexFile, uid);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void rmdex(String codePath, String instructionSet) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(codePath);
                    _data.writeString(instructionSet);
                    if (this.mRemote.transact(16, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().rmdex(codePath, instructionSet);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean mergeProfiles(int uid, String packageName, String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    boolean z = false;
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().mergeProfiles(uid, packageName, profileName);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean dumpProfiles(int uid, String packageName, String profileName, String codePath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    _data.writeString(codePath);
                    boolean z = false;
                    if (!this.mRemote.transact(18, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().dumpProfiles(uid, packageName, profileName, codePath);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean copySystemProfile(String systemProfile, int uid, String packageName, String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(systemProfile);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    boolean z = false;
                    if (!this.mRemote.transact(19, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().copySystemProfile(systemProfile, uid, packageName, profileName);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearAppProfiles(String packageName, String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    if (this.mRemote.transact(20, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().clearAppProfiles(packageName, profileName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void destroyAppProfiles(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (this.mRemote.transact(21, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().destroyAppProfiles(packageName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean createProfileSnapshot(int appId, String packageName, String profileName, String classpath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appId);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    _data.writeString(classpath);
                    boolean z = false;
                    if (!this.mRemote.transact(22, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().createProfileSnapshot(appId, packageName, profileName, classpath);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void destroyProfileSnapshot(String packageName, String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(profileName);
                    if (this.mRemote.transact(23, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().destroyProfileSnapshot(packageName, profileName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void idmap(String targetApkPath, String overlayApkPath, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetApkPath);
                    _data.writeString(overlayApkPath);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(24, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().idmap(targetApkPath, overlayApkPath, uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeIdmap(String overlayApkPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(overlayApkPath);
                    if (this.mRemote.transact(25, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeIdmap(overlayApkPath);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void rmPackageDir(String packageDir) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageDir);
                    if (this.mRemote.transact(Stub.TRANSACTION_rmPackageDir, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().rmPackageDir(packageDir);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void markBootComplete(String instructionSet) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(instructionSet);
                    if (this.mRemote.transact(Stub.TRANSACTION_markBootComplete, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().markBootComplete(instructionSet);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void freeCache(String uuid, long targetFreeBytes, long cacheReservedBytes, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        long j = targetFreeBytes;
                        long j2 = cacheReservedBytes;
                        int i = flags;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeLong(targetFreeBytes);
                    } catch (Throwable th2) {
                        th = th2;
                        long j22 = cacheReservedBytes;
                        int i2 = flags;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeLong(cacheReservedBytes);
                        try {
                            _data.writeInt(flags);
                            if (this.mRemote.transact(28, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().freeCache(uuid, targetFreeBytes, cacheReservedBytes, flags);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i22 = flags;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    String str = uuid;
                    long j3 = targetFreeBytes;
                    long j222 = cacheReservedBytes;
                    int i222 = flags;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void linkNativeLibraryDirectory(String uuid, String packageName, String nativeLibPath32, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeString(packageName);
                    _data.writeString(nativeLibPath32);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(29, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().linkNativeLibraryDirectory(uuid, packageName, nativeLibPath32, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void createOatDir(String oatDir, String instructionSet) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(oatDir);
                    _data.writeString(instructionSet);
                    if (this.mRemote.transact(30, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().createOatDir(oatDir, instructionSet);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void linkFile(String relativePath, String fromBase, String toBase) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(relativePath);
                    _data.writeString(fromBase);
                    _data.writeString(toBase);
                    if (this.mRemote.transact(31, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().linkFile(relativePath, fromBase, toBase);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void moveAb(String apkPath, String instructionSet, String outputPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(apkPath);
                    _data.writeString(instructionSet);
                    _data.writeString(outputPath);
                    if (this.mRemote.transact(32, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().moveAb(apkPath, instructionSet, outputPath);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deleteOdex(String apkPath, String instructionSet, String outputPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(apkPath);
                    _data.writeString(instructionSet);
                    _data.writeString(outputPath);
                    if (this.mRemote.transact(33, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().deleteOdex(apkPath, instructionSet, outputPath);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void installApkVerity(String filePath, FileDescriptor verityInput, int contentSize) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filePath);
                    _data.writeRawFileDescriptor(verityInput);
                    _data.writeInt(contentSize);
                    if (this.mRemote.transact(34, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().installApkVerity(filePath, verityInput, contentSize);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void assertFsverityRootHashMatches(String filePath, byte[] expectedHash) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filePath);
                    _data.writeByteArray(expectedHash);
                    if (this.mRemote.transact(35, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().assertFsverityRootHashMatches(filePath, expectedHash);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean reconcileSecondaryDexFile(String dexPath, String pkgName, int uid, String[] isas, String volume_uuid, int storage_flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(dexPath);
                    } catch (Throwable th) {
                        th = th;
                        String str = pkgName;
                        int i = uid;
                        String[] strArr = isas;
                        String str2 = volume_uuid;
                        int i2 = storage_flag;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(pkgName);
                        try {
                            _data.writeInt(uid);
                            try {
                                _data.writeStringArray(isas);
                            } catch (Throwable th2) {
                                th = th2;
                                String str22 = volume_uuid;
                                int i22 = storage_flag;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            String[] strArr2 = isas;
                            String str222 = volume_uuid;
                            int i222 = storage_flag;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i3 = uid;
                        String[] strArr22 = isas;
                        String str2222 = volume_uuid;
                        int i2222 = storage_flag;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(volume_uuid);
                        try {
                            _data.writeInt(storage_flag);
                            boolean z = false;
                            if (this.mRemote.transact(36, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                if (_reply.readInt() != 0) {
                                    z = true;
                                }
                                boolean _result = z;
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            boolean reconcileSecondaryDexFile = Stub.getDefaultImpl().reconcileSecondaryDexFile(dexPath, pkgName, uid, isas, volume_uuid, storage_flag);
                            _reply.recycle();
                            _data.recycle();
                            return reconcileSecondaryDexFile;
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        int i22222 = storage_flag;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str3 = dexPath;
                    String str4 = pkgName;
                    int i32 = uid;
                    String[] strArr222 = isas;
                    String str22222 = volume_uuid;
                    int i222222 = storage_flag;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public byte[] hashSecondaryDexFile(String dexPath, String pkgName, int uid, String volumeUuid, int storageFlag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dexPath);
                    _data.writeString(pkgName);
                    _data.writeInt(uid);
                    _data.writeString(volumeUuid);
                    _data.writeInt(storageFlag);
                    if (!this.mRemote.transact(37, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().hashSecondaryDexFile(dexPath, pkgName, uid, volumeUuid, storageFlag);
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void invalidateMounts() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(38, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().invalidateMounts();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isQuotaSupported(String uuid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    boolean z = false;
                    if (!this.mRemote.transact(39, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isQuotaSupported(uuid);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean prepareAppProfile(String packageName, int userId, int appId, String profileName, String codePath, String dexMetadata) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th) {
                        th = th;
                        int i = userId;
                        int i2 = appId;
                        String str = profileName;
                        String str2 = codePath;
                        String str3 = dexMetadata;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeInt(appId);
                            try {
                                _data.writeString(profileName);
                            } catch (Throwable th2) {
                                th = th2;
                                String str22 = codePath;
                                String str32 = dexMetadata;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            String str4 = profileName;
                            String str222 = codePath;
                            String str322 = dexMetadata;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i22 = appId;
                        String str42 = profileName;
                        String str2222 = codePath;
                        String str3222 = dexMetadata;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(codePath);
                        try {
                            _data.writeString(dexMetadata);
                            boolean z = false;
                            if (this.mRemote.transact(40, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                if (_reply.readInt() != 0) {
                                    z = true;
                                }
                                boolean _result = z;
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            boolean prepareAppProfile = Stub.getDefaultImpl().prepareAppProfile(packageName, userId, appId, profileName, codePath, dexMetadata);
                            _reply.recycle();
                            _data.recycle();
                            return prepareAppProfile;
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        String str32222 = dexMetadata;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str5 = packageName;
                    int i3 = userId;
                    int i222 = appId;
                    String str422 = profileName;
                    String str22222 = codePath;
                    String str322222 = dexMetadata;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public long snapshotAppData(String uuid, String packageName, int userId, int snapshotId, int storageFlags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeInt(snapshotId);
                    _data.writeInt(storageFlags);
                    if (!this.mRemote.transact(41, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().snapshotAppData(uuid, packageName, userId, snapshotId, storageFlags);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void restoreAppDataSnapshot(String uuid, String packageName, int appId, String seInfo, int user, int snapshotId, int storageflags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = appId;
                        String str2 = seInfo;
                        int i2 = user;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th2) {
                        th = th2;
                        int i3 = appId;
                        String str22 = seInfo;
                        int i22 = user;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(appId);
                        try {
                            _data.writeString(seInfo);
                        } catch (Throwable th3) {
                            th = th3;
                            int i222 = user;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(user);
                            _data.writeInt(snapshotId);
                            _data.writeInt(storageflags);
                            if (this.mRemote.transact(42, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().restoreAppDataSnapshot(uuid, packageName, appId, seInfo, user, snapshotId, storageflags);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        String str222 = seInfo;
                        int i2222 = user;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str3 = uuid;
                    String str4 = packageName;
                    int i32 = appId;
                    String str2222 = seInfo;
                    int i22222 = user;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void destroyAppDataSnapshot(String uuid, String packageName, int userId, long ceSnapshotInode, int snapshotId, int storageFlags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(uuid);
                    } catch (Throwable th) {
                        th = th;
                        String str = packageName;
                        int i = userId;
                        long j = ceSnapshotInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(packageName);
                    } catch (Throwable th2) {
                        th = th2;
                        int i2 = userId;
                        long j2 = ceSnapshotInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeLong(ceSnapshotInode);
                            _data.writeInt(snapshotId);
                            _data.writeInt(storageFlags);
                            if (this.mRemote.transact(43, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().destroyAppDataSnapshot(uuid, packageName, userId, ceSnapshotInode, snapshotId, storageFlags);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        long j22 = ceSnapshotInode;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    String str2 = uuid;
                    String str3 = packageName;
                    int i22 = userId;
                    long j222 = ceSnapshotInode;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void migrateLegacyObbData() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(44, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().migrateLegacyObbData();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IInstalld impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IInstalld getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
