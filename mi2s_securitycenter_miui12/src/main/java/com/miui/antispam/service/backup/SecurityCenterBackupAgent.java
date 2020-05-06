package com.miui.antispam.service.backup;

import miui.app.backup.FullBackupAgent;

public class SecurityCenterBackupAgent extends FullBackupAgent {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2437a = "SecurityCenterBackupAgent";

    /* renamed from: b  reason: collision with root package name */
    private C0197a f2438b;

    /* access modifiers changed from: protected */
    public int getVersion(int i) {
        return 1;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x010e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onDataRestore(miui.app.backup.BackupMeta r4, android.os.ParcelFileDescriptor r5) {
        /*
            r3 = this;
            java.lang.String r0 = f2437a
            java.lang.String r1 = "feature"
            java.lang.Object r0 = b.b.o.g.d.a((java.lang.String) r0, (java.lang.Object) r4, (java.lang.String) r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = -1
            if (r0 != r1) goto L_0x0022
            com.miui.antispam.service.backup.SecurityCenterBackupAgent.super.onDataRestore(r4, r5)
            android.content.Context r4 = r3.getApplicationContext()
            com.miui.securitycenter.cloudbackup.k r0 = new com.miui.securitycenter.cloudbackup.k
            r0.<init>()
            com.xiaomi.settingsdk.backup.SettingsBackupHelper.restoreSettings(r4, r5, r0)
            goto L_0x0112
        L_0x0022:
            r4 = 2
            if (r0 != r4) goto L_0x0112
            com.miui.antispam.service.backup.a r4 = new com.miui.antispam.service.backup.a
            android.content.Context r0 = r3.getApplicationContext()
            r4.<init>(r0)
            r3.f2438b = r4
            r4 = 0
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0109 }
            java.io.FileDescriptor r5 = r5.getFileDescriptor()     // Catch:{ all -> 0x0109 }
            r0.<init>(r5)     // Catch:{ all -> 0x0109 }
            com.miui.antispam.service.backup.I r4 = com.miui.antispam.service.backup.I.a((java.io.InputStream) r0)     // Catch:{ all -> 0x0107 }
            r0.close()     // Catch:{ all -> 0x0107 }
            if (r4 != 0) goto L_0x0048
            r4 = 6
            r0.close()
            return r4
        L_0x0048:
            com.miui.antispam.service.backup.d r4 = r4.a()     // Catch:{ all -> 0x0107 }
            java.util.List r5 = r4.f()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x0054:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x0066
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.s r1 = (com.miui.antispam.service.backup.s) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.s) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x0054
        L_0x0066:
            java.util.List r5 = r4.b()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x006e:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x0080
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.j r1 = (com.miui.antispam.service.backup.C0206j) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.C0206j) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x006e
        L_0x0080:
            java.util.List r5 = r4.j()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x0088:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x009a
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.E r1 = (com.miui.antispam.service.backup.E) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.E) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x0088
        L_0x009a:
            java.util.List r5 = r4.d()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x00a2:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x00b4
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.m r1 = (com.miui.antispam.service.backup.m) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.m) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x00a2
        L_0x00b4:
            java.util.List r5 = r4.i()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x00bc:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x00ce
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.B r1 = (com.miui.antispam.service.backup.B) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.B) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x00bc
        L_0x00ce:
            java.util.List r5 = r4.e()     // Catch:{ all -> 0x0107 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0107 }
        L_0x00d6:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0107 }
            if (r1 == 0) goto L_0x00e8
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.p r1 = (com.miui.antispam.service.backup.p) r1     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r2 = r3.f2438b     // Catch:{ all -> 0x0107 }
            r2.a((com.miui.antispam.service.backup.p) r1)     // Catch:{ all -> 0x0107 }
            goto L_0x00d6
        L_0x00e8:
            com.miui.antispam.service.backup.a r5 = r3.f2438b     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.g r1 = r4.a()     // Catch:{ all -> 0x0107 }
            r5.a((com.miui.antispam.service.backup.C0203g) r1)     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r5 = r3.f2438b     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.y r1 = r4.h()     // Catch:{ all -> 0x0107 }
            r5.a((com.miui.antispam.service.backup.y) r1)     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.a r5 = r3.f2438b     // Catch:{ all -> 0x0107 }
            com.miui.antispam.service.backup.v r4 = r4.g()     // Catch:{ all -> 0x0107 }
            r5.a((com.miui.antispam.service.backup.v) r4)     // Catch:{ all -> 0x0107 }
            r0.close()
            goto L_0x0112
        L_0x0107:
            r4 = move-exception
            goto L_0x010c
        L_0x0109:
            r5 = move-exception
            r0 = r4
            r4 = r5
        L_0x010c:
            if (r0 == 0) goto L_0x0111
            r0.close()
        L_0x0111:
            throw r4
        L_0x0112:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.SecurityCenterBackupAgent.onDataRestore(miui.app.backup.BackupMeta, android.os.ParcelFileDescriptor):int");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0112 A[Catch:{ FileNotFoundException -> 0x0113, IOException -> 0x010a, all -> 0x0108 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onFullBackup(android.os.ParcelFileDescriptor r9, int r10) {
        /*
            r8 = this;
            java.lang.String r0 = "Cannot export blacklist and whitelist "
            r1 = -1
            if (r10 != r1) goto L_0x0016
            com.miui.antispam.service.backup.SecurityCenterBackupAgent.super.onFullBackup(r9, r10)
            android.content.Context r10 = r8.getApplicationContext()
            com.miui.securitycenter.cloudbackup.k r0 = new com.miui.securitycenter.cloudbackup.k
            r0.<init>()
            com.xiaomi.settingsdk.backup.SettingsBackupHelper.backupSettings(r10, r9, r0)
            goto L_0x0125
        L_0x0016:
            r1 = 2
            if (r10 != r1) goto L_0x0125
            com.miui.antispam.service.backup.a r10 = new com.miui.antispam.service.backup.a
            android.content.Context r1 = r8.getApplicationContext()
            r10.<init>(r1)
            r8.f2438b = r10
            com.miui.antispam.service.backup.d$a r10 = com.miui.antispam.service.backup.C0200d.n()
            com.miui.antispam.service.backup.a r1 = r8.f2438b
            java.util.Vector r1 = r1.d()
            com.miui.antispam.service.backup.a r2 = r8.f2438b
            java.util.Vector r2 = r2.a()
            com.miui.antispam.service.backup.a r3 = r8.f2438b
            java.util.Vector r3 = r3.f()
            com.miui.antispam.service.backup.a r4 = r8.f2438b
            java.util.Vector r4 = r4.b()
            com.miui.antispam.service.backup.a r5 = r8.f2438b
            java.util.Vector r5 = r5.e()
            com.miui.antispam.service.backup.a r6 = r8.f2438b
            java.util.Vector r6 = r6.c()
            java.util.Iterator r1 = r1.iterator()
        L_0x0050:
            boolean r7 = r1.hasNext()
            if (r7 == 0) goto L_0x0060
            java.lang.Object r7 = r1.next()
            com.miui.antispam.service.backup.s r7 = (com.miui.antispam.service.backup.s) r7
            r10.a((com.miui.antispam.service.backup.s) r7)
            goto L_0x0050
        L_0x0060:
            java.util.Iterator r1 = r2.iterator()
        L_0x0064:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0074
            java.lang.Object r2 = r1.next()
            com.miui.antispam.service.backup.j r2 = (com.miui.antispam.service.backup.C0206j) r2
            r10.a((com.miui.antispam.service.backup.C0206j) r2)
            goto L_0x0064
        L_0x0074:
            java.util.Iterator r1 = r3.iterator()
        L_0x0078:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0088
            java.lang.Object r2 = r1.next()
            com.miui.antispam.service.backup.E r2 = (com.miui.antispam.service.backup.E) r2
            r10.a((com.miui.antispam.service.backup.E) r2)
            goto L_0x0078
        L_0x0088:
            java.util.Iterator r1 = r4.iterator()
        L_0x008c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x009c
            java.lang.Object r2 = r1.next()
            com.miui.antispam.service.backup.m r2 = (com.miui.antispam.service.backup.m) r2
            r10.a((com.miui.antispam.service.backup.m) r2)
            goto L_0x008c
        L_0x009c:
            java.util.Iterator r1 = r5.iterator()
        L_0x00a0:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00b0
            java.lang.Object r2 = r1.next()
            com.miui.antispam.service.backup.B r2 = (com.miui.antispam.service.backup.B) r2
            r10.a((com.miui.antispam.service.backup.B) r2)
            goto L_0x00a0
        L_0x00b0:
            java.util.Iterator r1 = r6.iterator()
        L_0x00b4:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00c4
            java.lang.Object r2 = r1.next()
            com.miui.antispam.service.backup.p r2 = (com.miui.antispam.service.backup.p) r2
            r10.a((com.miui.antispam.service.backup.p) r2)
            goto L_0x00b4
        L_0x00c4:
            com.miui.antispam.service.backup.a r1 = r8.f2438b
            com.miui.antispam.service.backup.g r1 = r1.g()
            r10.b((com.miui.antispam.service.backup.C0203g) r1)
            com.miui.antispam.service.backup.a r1 = r8.f2438b
            com.miui.antispam.service.backup.y r1 = r1.i()
            r10.b((com.miui.antispam.service.backup.y) r1)
            com.miui.antispam.service.backup.a r1 = r8.f2438b
            com.miui.antispam.service.backup.v r1 = r1.h()
            r10.b((com.miui.antispam.service.backup.v) r1)
            com.miui.antispam.service.backup.I$a r1 = com.miui.antispam.service.backup.I.d()
            com.miui.antispam.service.backup.d r10 = r10.build()
            r1.b(r10)
            r10 = 0
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x0113, IOException -> 0x010a }
            java.io.FileDescriptor r9 = r9.getFileDescriptor()     // Catch:{ FileNotFoundException -> 0x0113, IOException -> 0x010a }
            r2.<init>(r9)     // Catch:{ FileNotFoundException -> 0x0113, IOException -> 0x010a }
            com.miui.antispam.service.backup.I r9 = r1.build()     // Catch:{ FileNotFoundException -> 0x0105, IOException -> 0x0102, all -> 0x00ff }
            r9.writeTo(r2)     // Catch:{ FileNotFoundException -> 0x0105, IOException -> 0x0102, all -> 0x00ff }
            r2.close()
            goto L_0x0125
        L_0x00ff:
            r9 = move-exception
            r10 = r2
            goto L_0x011f
        L_0x0102:
            r9 = move-exception
            r10 = r2
            goto L_0x010b
        L_0x0105:
            r9 = move-exception
            r10 = r2
            goto L_0x0114
        L_0x0108:
            r9 = move-exception
            goto L_0x011f
        L_0x010a:
            r9 = move-exception
        L_0x010b:
            java.lang.String r1 = f2437a     // Catch:{ all -> 0x0108 }
            android.util.Log.e(r1, r0, r9)     // Catch:{ all -> 0x0108 }
            if (r10 == 0) goto L_0x0125
            goto L_0x011b
        L_0x0113:
            r9 = move-exception
        L_0x0114:
            java.lang.String r1 = f2437a     // Catch:{ all -> 0x0108 }
            android.util.Log.e(r1, r0, r9)     // Catch:{ all -> 0x0108 }
            if (r10 == 0) goto L_0x0125
        L_0x011b:
            r10.close()
            goto L_0x0125
        L_0x011f:
            if (r10 == 0) goto L_0x0124
            r10.close()
        L_0x0124:
            throw r9
        L_0x0125:
            r9 = 0
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.SecurityCenterBackupAgent.onFullBackup(android.os.ParcelFileDescriptor, int):int");
    }
}
