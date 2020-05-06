package com.miui.appmanager;

import android.content.Context;
import android.os.AsyncTask;

/* renamed from: com.miui.appmanager.c  reason: case insensitive filesystem */
class C0320c extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3614a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f3615b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f3616c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ boolean f3617d;

    C0320c(String str, Context context, String str2, boolean z) {
        this.f3614a = str;
        this.f3615b = context;
        this.f3616c = str2;
        this.f3617d = z;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0138, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00b3 A[SYNTHETIC, Splitter:B:51:0x00b3] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ba A[Catch:{ IOException -> 0x00ed }] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Void doInBackground(java.lang.Void... r9) {
        /*
            r8 = this;
            java.lang.Object r9 = com.miui.appmanager.C0322e.f3670a
            monitor-enter(r9)
            java.lang.String r0 = r8.f3614a     // Catch:{ all -> 0x0139 }
            if (r0 != 0) goto L_0x0011
            android.content.Context r0 = r8.f3615b     // Catch:{ all -> 0x0139 }
            java.lang.String r1 = r8.f3616c     // Catch:{ all -> 0x0139 }
            java.lang.String r0 = com.miui.appmanager.C0322e.a((android.content.Context) r0, (java.lang.String) r1)     // Catch:{ all -> 0x0139 }
        L_0x0011:
            r1 = 0
            if (r0 == 0) goto L_0x0137
            java.lang.String r2 = "com.miui.packageinstaller"
            boolean r2 = r2.equals(r0)     // Catch:{ all -> 0x0139 }
            if (r2 == 0) goto L_0x001e
            goto L_0x0137
        L_0x001e:
            android.content.Context r2 = r8.f3615b     // Catch:{ all -> 0x0139 }
            org.json.JSONArray r2 = com.miui.appmanager.C0322e.c(r2)     // Catch:{ all -> 0x0139 }
            java.lang.String r3 = r8.f3616c     // Catch:{ all -> 0x0139 }
            int r3 = com.miui.appmanager.C0322e.b((org.json.JSONArray) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0139 }
            r4 = -1
            if (r3 != r4) goto L_0x0055
            boolean r5 = r8.f3617d     // Catch:{ all -> 0x0139 }
            if (r5 != 0) goto L_0x0055
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x004b }
            r3.<init>()     // Catch:{ JSONException -> 0x004b }
            java.lang.String r4 = "pkg_name"
            java.lang.String r5 = r8.f3616c     // Catch:{ JSONException -> 0x0049 }
            r3.put(r4, r5)     // Catch:{ JSONException -> 0x0049 }
            java.lang.String r4 = "installer_pkg_name"
            r3.put(r4, r0)     // Catch:{ JSONException -> 0x0049 }
            java.lang.String r4 = "update_pkg_name"
            r3.put(r4, r0)     // Catch:{ JSONException -> 0x0049 }
            goto L_0x00b1
        L_0x0049:
            r0 = move-exception
            goto L_0x004d
        L_0x004b:
            r0 = move-exception
            r3 = r1
        L_0x004d:
            java.lang.String r4 = "AMInstallerUtils"
            java.lang.String r5 = "JSONExcepiton when addInstallerPkg"
        L_0x0051:
            android.util.Log.e(r4, r5, r0)     // Catch:{ all -> 0x0139 }
            goto L_0x00b1
        L_0x0055:
            if (r3 == r4) goto L_0x008a
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ JSONException -> 0x007f }
            r4.<init>()     // Catch:{ JSONException -> 0x007f }
            java.lang.Object r5 = r2.get(r3)     // Catch:{ JSONException -> 0x007d }
            org.json.JSONObject r5 = (org.json.JSONObject) r5     // Catch:{ JSONException -> 0x007d }
            java.lang.String r6 = "pkg_name"
            java.lang.String r7 = r8.f3616c     // Catch:{ JSONException -> 0x007d }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x007d }
            java.lang.String r6 = "installer_pkg_name"
            java.lang.String r5 = r5.optString(r6)     // Catch:{ JSONException -> 0x007d }
            java.lang.String r6 = "installer_pkg_name"
            r4.put(r6, r5)     // Catch:{ JSONException -> 0x007d }
            java.lang.String r5 = "update_pkg_name"
            r4.put(r5, r0)     // Catch:{ JSONException -> 0x007d }
            r2.remove(r3)     // Catch:{ JSONException -> 0x007d }
            goto L_0x0088
        L_0x007d:
            r0 = move-exception
            goto L_0x0081
        L_0x007f:
            r0 = move-exception
            r4 = r1
        L_0x0081:
            java.lang.String r3 = "AMInstallerUtils"
            java.lang.String r5 = "JSONExcepiton when addInstallerPkg"
            android.util.Log.e(r3, r5, r0)     // Catch:{ all -> 0x0139 }
        L_0x0088:
            r3 = r4
            goto L_0x00b1
        L_0x008a:
            boolean r3 = r8.f3617d     // Catch:{ all -> 0x0139 }
            if (r3 == 0) goto L_0x00b0
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00a9 }
            r3.<init>()     // Catch:{ JSONException -> 0x00a9 }
            java.lang.String r4 = "pkg_name"
            java.lang.String r5 = r8.f3616c     // Catch:{ JSONException -> 0x00a7 }
            r3.put(r4, r5)     // Catch:{ JSONException -> 0x00a7 }
            java.lang.String r4 = "installer_pkg_name"
            java.lang.String r5 = ""
            r3.put(r4, r5)     // Catch:{ JSONException -> 0x00a7 }
            java.lang.String r4 = "update_pkg_name"
            r3.put(r4, r0)     // Catch:{ JSONException -> 0x00a7 }
            goto L_0x00b1
        L_0x00a7:
            r0 = move-exception
            goto L_0x00ab
        L_0x00a9:
            r0 = move-exception
            r3 = r1
        L_0x00ab:
            java.lang.String r4 = "AMInstallerUtils"
            java.lang.String r5 = "JSONExcepiton when addInstallerPkg"
            goto L_0x0051
        L_0x00b0:
            r3 = r1
        L_0x00b1:
            if (r2 != 0) goto L_0x00b8
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ all -> 0x0139 }
            r2.<init>()     // Catch:{ all -> 0x0139 }
        L_0x00b8:
            if (r3 == 0) goto L_0x0135
            r2.put(r3)     // Catch:{ all -> 0x0139 }
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0139 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0139 }
            r3.<init>()     // Catch:{ all -> 0x0139 }
            android.content.Context r4 = r8.f3615b     // Catch:{ all -> 0x0139 }
            java.io.File r4 = r4.getFilesDir()     // Catch:{ all -> 0x0139 }
            r3.append(r4)     // Catch:{ all -> 0x0139 }
            java.lang.String r4 = "/appmanager/"
            r3.append(r4)     // Catch:{ all -> 0x0139 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0139 }
            r0.<init>(r3)     // Catch:{ all -> 0x0139 }
            r0.mkdir()     // Catch:{ all -> 0x0139 }
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0139 }
            java.lang.String r4 = "appmanager_installer_pkg"
            r3.<init>(r0, r4)     // Catch:{ all -> 0x0139 }
            boolean r0 = r3.exists()     // Catch:{ all -> 0x0139 }
            if (r0 != 0) goto L_0x00f5
            r3.createNewFile()     // Catch:{ IOException -> 0x00ed }
            goto L_0x00f5
        L_0x00ed:
            r0 = move-exception
            java.lang.String r4 = "AMInstallerUtils"
            java.lang.String r5 = "IOException openFile"
            android.util.Log.e(r4, r5, r0)     // Catch:{ all -> 0x0139 }
        L_0x00f5:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x011f, all -> 0x011a }
            java.lang.String r3 = r3.getPath()     // Catch:{ FileNotFoundException -> 0x011f, all -> 0x011a }
            r0.<init>(r3)     // Catch:{ FileNotFoundException -> 0x011f, all -> 0x011a }
            java.io.PrintWriter r3 = new java.io.PrintWriter     // Catch:{ FileNotFoundException -> 0x0117, all -> 0x0113 }
            r3.<init>(r0)     // Catch:{ FileNotFoundException -> 0x0117, all -> 0x0113 }
            java.lang.String r2 = r2.toString()     // Catch:{ FileNotFoundException -> 0x0111 }
            r3.write(r2)     // Catch:{ FileNotFoundException -> 0x0111 }
            miui.util.IOUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
        L_0x010d:
            miui.util.IOUtils.closeQuietly(r0)     // Catch:{ all -> 0x0139 }
            goto L_0x0135
        L_0x0111:
            r2 = move-exception
            goto L_0x0122
        L_0x0113:
            r2 = move-exception
            r3 = r1
            r1 = r2
            goto L_0x012e
        L_0x0117:
            r2 = move-exception
            r3 = r1
            goto L_0x0122
        L_0x011a:
            r0 = move-exception
            r3 = r1
            r1 = r0
            r0 = r3
            goto L_0x012e
        L_0x011f:
            r2 = move-exception
            r0 = r1
            r3 = r0
        L_0x0122:
            java.lang.String r4 = "AMInstallerUtils"
            java.lang.String r5 = "FileNotFoundException when addInstallerPkg"
            android.util.Log.e(r4, r5, r2)     // Catch:{ all -> 0x012d }
            miui.util.IOUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
            goto L_0x010d
        L_0x012d:
            r1 = move-exception
        L_0x012e:
            miui.util.IOUtils.closeQuietly(r3)     // Catch:{ all -> 0x0139 }
            miui.util.IOUtils.closeQuietly(r0)     // Catch:{ all -> 0x0139 }
            throw r1     // Catch:{ all -> 0x0139 }
        L_0x0135:
            monitor-exit(r9)     // Catch:{ all -> 0x0139 }
            return r1
        L_0x0137:
            monitor-exit(r9)     // Catch:{ all -> 0x0139 }
            return r1
        L_0x0139:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x0139 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.C0320c.doInBackground(java.lang.Void[]):java.lang.Void");
    }
}
