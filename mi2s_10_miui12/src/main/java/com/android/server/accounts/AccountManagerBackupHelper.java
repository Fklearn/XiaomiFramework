package com.android.server.accounts;

import android.accounts.Account;
import android.accounts.AccountManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.PackageUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.XmlUtils;
import com.android.server.accounts.AccountManagerService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class AccountManagerBackupHelper {
    private static final String ATTR_ACCOUNT_SHA_256 = "account-sha-256";
    private static final String ATTR_DIGEST = "digest";
    private static final String ATTR_PACKAGE = "package";
    private static final long PENDING_RESTORE_TIMEOUT_MILLIS = 3600000;
    private static final String TAG = "AccountManagerBackupHelper";
    private static final String TAG_PERMISSION = "permission";
    private static final String TAG_PERMISSIONS = "permissions";
    /* access modifiers changed from: private */
    public final AccountManagerInternal mAccountManagerInternal;
    /* access modifiers changed from: private */
    public final AccountManagerService mAccountManagerService;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public Runnable mRestoreCancelCommand;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public RestorePackageMonitor mRestorePackageMonitor;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public List<PendingAppPermission> mRestorePendingAppPermissions;

    public AccountManagerBackupHelper(AccountManagerService accountManagerService, AccountManagerInternal accountManagerInternal) {
        this.mAccountManagerService = accountManagerService;
        this.mAccountManagerInternal = accountManagerInternal;
    }

    private final class PendingAppPermission {
        private final String accountDigest;
        private final String certDigest;
        /* access modifiers changed from: private */
        public final String packageName;
        private final int userId;

        public PendingAppPermission(String accountDigest2, String packageName2, String certDigest2, int userId2) {
            this.accountDigest = accountDigest2;
            this.packageName = packageName2;
            this.certDigest = certDigest2;
            this.userId = userId2;
        }

        /* Debug info: failed to restart local var, previous not found, register: 12 */
        public boolean apply(PackageManager packageManager) {
            Account account = null;
            AccountManagerService.UserAccounts accounts = AccountManagerBackupHelper.this.mAccountManagerService.getUserAccounts(this.userId);
            synchronized (accounts.dbLock) {
                synchronized (accounts.cacheLock) {
                    Iterator<Account[]> it = accounts.accountCache.values().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        Account[] accountsPerType = it.next();
                        int length = accountsPerType.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            Account accountPerType = accountsPerType[i];
                            if (this.accountDigest.equals(PackageUtils.computeSha256Digest(accountPerType.name.getBytes()))) {
                                account = accountPerType;
                                break;
                            }
                            i++;
                        }
                        if (account != null) {
                            break;
                        }
                    }
                }
            }
            if (account == null) {
                return false;
            }
            try {
                PackageInfo packageInfo = packageManager.getPackageInfoAsUser(this.packageName, 64, this.userId);
                String[] signaturesSha256Digests = PackageUtils.computeSignaturesSha256Digests(packageInfo.signatures);
                if (!this.certDigest.equals(PackageUtils.computeSignaturesSha256Digest(signaturesSha256Digests)) && (packageInfo.signatures.length <= 1 || !this.certDigest.equals(signaturesSha256Digests[0]))) {
                    return false;
                }
                int uid = packageInfo.applicationInfo.uid;
                if (!AccountManagerBackupHelper.this.mAccountManagerInternal.hasAccountAccess(account, uid)) {
                    AccountManagerBackupHelper.this.mAccountManagerService.grantAppPermission(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", uid);
                }
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public byte[] backupAccountAccessPermissions(int r21) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            com.android.server.accounts.AccountManagerService r0 = r1.mAccountManagerService
            com.android.server.accounts.AccountManagerService$UserAccounts r3 = r0.getUserAccounts(r2)
            java.lang.Object r4 = r3.dbLock
            monitor-enter(r4)
            java.lang.Object r5 = r3.cacheLock     // Catch:{ all -> 0x0137 }
            monitor-enter(r5)     // Catch:{ all -> 0x0137 }
            com.android.server.accounts.AccountsDb r0 = r3.accountsDb     // Catch:{ all -> 0x0130 }
            java.util.List r0 = r0.findAllAccountGrants()     // Catch:{ all -> 0x0130 }
            r6 = r0
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x0130 }
            r7 = 0
            if (r0 == 0) goto L_0x002b
            monitor-exit(r5)     // Catch:{ all -> 0x0026 }
            monitor-exit(r4)     // Catch:{ all -> 0x0021 }
            return r7
        L_0x0021:
            r0 = move-exception
            r17 = r3
            goto L_0x013a
        L_0x0026:
            r0 = move-exception
            r17 = r3
            goto L_0x0133
        L_0x002b:
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x0120 }
            r0.<init>()     // Catch:{ IOException -> 0x0120 }
            r8 = r0
            com.android.internal.util.FastXmlSerializer r0 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x0120 }
            r0.<init>()     // Catch:{ IOException -> 0x0120 }
            r9 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x0120 }
            java.lang.String r0 = r0.name()     // Catch:{ IOException -> 0x0120 }
            r9.setOutput(r8, r0)     // Catch:{ IOException -> 0x0120 }
            r0 = 1
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)     // Catch:{ IOException -> 0x0120 }
            r9.startDocument(r7, r0)     // Catch:{ IOException -> 0x0120 }
            java.lang.String r0 = "permissions"
            r9.startTag(r7, r0)     // Catch:{ IOException -> 0x0120 }
            com.android.server.accounts.AccountManagerService r0 = r1.mAccountManagerService     // Catch:{ IOException -> 0x0120 }
            android.content.Context r0 = r0.mContext     // Catch:{ IOException -> 0x0120 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ IOException -> 0x0120 }
            r10 = r0
            java.util.Iterator r11 = r6.iterator()     // Catch:{ IOException -> 0x0120 }
        L_0x005b:
            boolean r0 = r11.hasNext()     // Catch:{ IOException -> 0x0120 }
            if (r0 == 0) goto L_0x0106
            java.lang.Object r0 = r11.next()     // Catch:{ IOException -> 0x0120 }
            android.util.Pair r0 = (android.util.Pair) r0     // Catch:{ IOException -> 0x0120 }
            r12 = r0
            java.lang.Object r0 = r12.first     // Catch:{ IOException -> 0x0120 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ IOException -> 0x0120 }
            r13 = r0
            java.lang.Object r0 = r12.second     // Catch:{ IOException -> 0x0120 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ IOException -> 0x0120 }
            int r0 = r0.intValue()     // Catch:{ IOException -> 0x0120 }
            r14 = r0
            java.lang.String[] r0 = r10.getPackagesForUid(r14)     // Catch:{ IOException -> 0x0120 }
            r15 = r0
            if (r15 != 0) goto L_0x007e
            goto L_0x005b
        L_0x007e:
            int r7 = r15.length     // Catch:{ IOException -> 0x0120 }
            r0 = 0
            r1 = r0
        L_0x0081:
            if (r1 >= r7) goto L_0x00fb
            r0 = r15[r1]     // Catch:{ IOException -> 0x0120 }
            r16 = r0
            r0 = 64
            r17 = r3
            r3 = r16
            android.content.pm.PackageInfo r0 = r10.getPackageInfoAsUser(r3, r0, r2)     // Catch:{ NameNotFoundException -> 0x00d2 }
            android.content.pm.Signature[] r2 = r0.signatures     // Catch:{ IOException -> 0x00ce }
            java.lang.String r2 = android.util.PackageUtils.computeSignaturesSha256Digest(r2)     // Catch:{ IOException -> 0x00ce }
            if (r2 == 0) goto L_0x00c7
            r16 = r0
            java.lang.String r0 = "permission"
            r18 = r6
            r6 = 0
            r9.startTag(r6, r0)     // Catch:{ IOException -> 0x011e }
            java.lang.String r0 = "account-sha-256"
            byte[] r6 = r13.getBytes()     // Catch:{ IOException -> 0x011e }
            java.lang.String r6 = android.util.PackageUtils.computeSha256Digest(r6)     // Catch:{ IOException -> 0x011e }
            r19 = r7
            r7 = 0
            r9.attribute(r7, r0, r6)     // Catch:{ IOException -> 0x011e }
            java.lang.String r0 = "package"
            r9.attribute(r7, r0, r3)     // Catch:{ IOException -> 0x011e }
            java.lang.String r0 = "digest"
            r9.attribute(r7, r0, r2)     // Catch:{ IOException -> 0x011e }
            java.lang.String r0 = "permission"
            r9.endTag(r7, r0)     // Catch:{ IOException -> 0x011e }
            goto L_0x00f0
        L_0x00c7:
            r16 = r0
            r18 = r6
            r19 = r7
            goto L_0x00f0
        L_0x00ce:
            r0 = move-exception
            r18 = r6
            goto L_0x0125
        L_0x00d2:
            r0 = move-exception
            r18 = r6
            r19 = r7
            r2 = r0
            r0 = r2
            java.lang.String r2 = "AccountManagerBackupHelper"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x011e }
            r6.<init>()     // Catch:{ IOException -> 0x011e }
            java.lang.String r7 = "Skipping backup of account access grant for non-existing package: "
            r6.append(r7)     // Catch:{ IOException -> 0x011e }
            r6.append(r3)     // Catch:{ IOException -> 0x011e }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x011e }
            android.util.Slog.i(r2, r6)     // Catch:{ IOException -> 0x011e }
        L_0x00f0:
            int r1 = r1 + 1
            r2 = r21
            r3 = r17
            r6 = r18
            r7 = r19
            goto L_0x0081
        L_0x00fb:
            r17 = r3
            r18 = r6
            r7 = 0
            r1 = r20
            r2 = r21
            goto L_0x005b
        L_0x0106:
            r17 = r3
            r18 = r6
            java.lang.String r0 = "permissions"
            r1 = 0
            r9.endTag(r1, r0)     // Catch:{ IOException -> 0x011e }
            r9.endDocument()     // Catch:{ IOException -> 0x011e }
            r9.flush()     // Catch:{ IOException -> 0x011e }
            byte[] r0 = r8.toByteArray()     // Catch:{ IOException -> 0x011e }
            monitor-exit(r5)     // Catch:{ all -> 0x0135 }
            monitor-exit(r4)     // Catch:{ all -> 0x013c }
            return r0
        L_0x011e:
            r0 = move-exception
            goto L_0x0125
        L_0x0120:
            r0 = move-exception
            r17 = r3
            r18 = r6
        L_0x0125:
            java.lang.String r1 = "AccountManagerBackupHelper"
            java.lang.String r2 = "Error backing up account access grants"
            android.util.Log.e(r1, r2, r0)     // Catch:{ all -> 0x0135 }
            monitor-exit(r5)     // Catch:{ all -> 0x0135 }
            monitor-exit(r4)     // Catch:{ all -> 0x013c }
            r1 = 0
            return r1
        L_0x0130:
            r0 = move-exception
            r17 = r3
        L_0x0133:
            monitor-exit(r5)     // Catch:{ all -> 0x0135 }
            throw r0     // Catch:{ all -> 0x013c }
        L_0x0135:
            r0 = move-exception
            goto L_0x0133
        L_0x0137:
            r0 = move-exception
            r17 = r3
        L_0x013a:
            monitor-exit(r4)     // Catch:{ all -> 0x013c }
            throw r0
        L_0x013c:
            r0 = move-exception
            goto L_0x013a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerBackupHelper.backupAccountAccessPermissions(int):byte[]");
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r13v1, types: [com.android.server.accounts.AccountManagerBackupHelper$1, java.lang.String] */
    /* JADX WARNING: type inference failed for: r13v5 */
    public void restoreAccountAccessPermissions(byte[] data, int userId) {
        boolean z;
        try {
            try {
                ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(dataStream, StandardCharsets.UTF_8.name());
                PackageManager packageManager = this.mAccountManagerService.mContext.getPackageManager();
                int permissionsOuterDepth = parser.getDepth();
                while (true) {
                    ? r13 = 0;
                    if (!XmlUtils.nextElementWithin(parser, permissionsOuterDepth)) {
                        this.mRestoreCancelCommand = new CancelRestoreCommand();
                        this.mAccountManagerService.mHandler.postDelayed(this.mRestoreCancelCommand, 3600000);
                        return;
                    } else if (TAG_PERMISSIONS.equals(parser.getName())) {
                        int permissionOuterDepth = parser.getDepth();
                        while (XmlUtils.nextElementWithin(parser, permissionOuterDepth)) {
                            if (!TAG_PERMISSION.equals(parser.getName())) {
                                z = r13;
                            } else {
                                String accountDigest = parser.getAttributeValue(r13, ATTR_ACCOUNT_SHA_256);
                                if (TextUtils.isEmpty(accountDigest)) {
                                    XmlUtils.skipCurrentTag(parser);
                                }
                                String packageName = parser.getAttributeValue(r13, "package");
                                if (TextUtils.isEmpty(packageName)) {
                                    XmlUtils.skipCurrentTag(parser);
                                }
                                String digest = parser.getAttributeValue(r13, ATTR_DIGEST);
                                if (TextUtils.isEmpty(digest)) {
                                    XmlUtils.skipCurrentTag(parser);
                                }
                                PendingAppPermission pendingAppPermission = new PendingAppPermission(accountDigest, packageName, digest, userId);
                                if (!pendingAppPermission.apply(packageManager)) {
                                    synchronized (this.mLock) {
                                        if (this.mRestorePackageMonitor == null) {
                                            this.mRestorePackageMonitor = new RestorePackageMonitor();
                                            this.mRestorePackageMonitor.register(this.mAccountManagerService.mContext, this.mAccountManagerService.mHandler.getLooper(), true);
                                        }
                                        if (this.mRestorePendingAppPermissions == null) {
                                            this.mRestorePendingAppPermissions = new ArrayList();
                                        }
                                        this.mRestorePendingAppPermissions.add(pendingAppPermission);
                                    }
                                }
                                z = false;
                            }
                            r13 = z;
                        }
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                e = e;
                Log.e(TAG, "Error restoring app permissions", e);
            }
        } catch (IOException | XmlPullParserException e2) {
            e = e2;
            byte[] bArr = data;
            Log.e(TAG, "Error restoring app permissions", e);
        }
    }

    private final class RestorePackageMonitor extends PackageMonitor {
        private RestorePackageMonitor() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0091, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onPackageAdded(java.lang.String r6, int r7) {
            /*
                r5 = this;
                com.android.server.accounts.AccountManagerBackupHelper r0 = com.android.server.accounts.AccountManagerBackupHelper.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.accounts.AccountManagerBackupHelper r1 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.util.List r1 = r1.mRestorePendingAppPermissions     // Catch:{ all -> 0x0092 }
                if (r1 != 0) goto L_0x0011
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x0011:
                int r1 = android.os.UserHandle.getUserId(r7)     // Catch:{ all -> 0x0092 }
                if (r1 == 0) goto L_0x0019
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x0019:
                com.android.server.accounts.AccountManagerBackupHelper r1 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.util.List r1 = r1.mRestorePendingAppPermissions     // Catch:{ all -> 0x0092 }
                int r1 = r1.size()     // Catch:{ all -> 0x0092 }
                int r2 = r1 + -1
            L_0x0025:
                if (r2 < 0) goto L_0x005c
                com.android.server.accounts.AccountManagerBackupHelper r3 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.util.List r3 = r3.mRestorePendingAppPermissions     // Catch:{ all -> 0x0092 }
                java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerBackupHelper$PendingAppPermission r3 = (com.android.server.accounts.AccountManagerBackupHelper.PendingAppPermission) r3     // Catch:{ all -> 0x0092 }
                java.lang.String r4 = r3.packageName     // Catch:{ all -> 0x0092 }
                boolean r4 = r4.equals(r6)     // Catch:{ all -> 0x0092 }
                if (r4 != 0) goto L_0x003e
                goto L_0x0059
            L_0x003e:
                com.android.server.accounts.AccountManagerBackupHelper r4 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerService r4 = r4.mAccountManagerService     // Catch:{ all -> 0x0092 }
                android.content.Context r4 = r4.mContext     // Catch:{ all -> 0x0092 }
                android.content.pm.PackageManager r4 = r4.getPackageManager()     // Catch:{ all -> 0x0092 }
                boolean r4 = r3.apply(r4)     // Catch:{ all -> 0x0092 }
                if (r4 == 0) goto L_0x0059
                com.android.server.accounts.AccountManagerBackupHelper r4 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.util.List r4 = r4.mRestorePendingAppPermissions     // Catch:{ all -> 0x0092 }
                r4.remove(r2)     // Catch:{ all -> 0x0092 }
            L_0x0059:
                int r2 = r2 + -1
                goto L_0x0025
            L_0x005c:
                com.android.server.accounts.AccountManagerBackupHelper r2 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.util.List r2 = r2.mRestorePendingAppPermissions     // Catch:{ all -> 0x0092 }
                boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0092 }
                if (r2 == 0) goto L_0x0090
                com.android.server.accounts.AccountManagerBackupHelper r2 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.lang.Runnable r2 = r2.mRestoreCancelCommand     // Catch:{ all -> 0x0092 }
                if (r2 == 0) goto L_0x0090
                com.android.server.accounts.AccountManagerBackupHelper r2 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerService r2 = r2.mAccountManagerService     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerService$MessageHandler r2 = r2.mHandler     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerBackupHelper r3 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.lang.Runnable r3 = r3.mRestoreCancelCommand     // Catch:{ all -> 0x0092 }
                r2.removeCallbacks(r3)     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerBackupHelper r2 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                java.lang.Runnable r2 = r2.mRestoreCancelCommand     // Catch:{ all -> 0x0092 }
                r2.run()     // Catch:{ all -> 0x0092 }
                com.android.server.accounts.AccountManagerBackupHelper r2 = com.android.server.accounts.AccountManagerBackupHelper.this     // Catch:{ all -> 0x0092 }
                r3 = 0
                java.lang.Runnable unused = r2.mRestoreCancelCommand = r3     // Catch:{ all -> 0x0092 }
            L_0x0090:
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x0092:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerBackupHelper.RestorePackageMonitor.onPackageAdded(java.lang.String, int):void");
        }
    }

    private final class CancelRestoreCommand implements Runnable {
        private CancelRestoreCommand() {
        }

        public void run() {
            synchronized (AccountManagerBackupHelper.this.mLock) {
                List unused = AccountManagerBackupHelper.this.mRestorePendingAppPermissions = null;
                if (AccountManagerBackupHelper.this.mRestorePackageMonitor != null) {
                    AccountManagerBackupHelper.this.mRestorePackageMonitor.unregister();
                    RestorePackageMonitor unused2 = AccountManagerBackupHelper.this.mRestorePackageMonitor = null;
                }
            }
        }
    }
}
