package com.miui.permcenter.install;

import android.content.Context;
import android.os.AsyncTask;
import android.os.IMessenger;

class n extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f6164a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f6165b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f6166c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ IMessenger f6167d;
    final /* synthetic */ PackageVerificationRecevier e;

    n(PackageVerificationRecevier packageVerificationRecevier, Context context, String str, String str2, IMessenger iMessenger) {
        this.e = packageVerificationRecevier;
        this.f6164a = context;
        this.f6165b = str;
        this.f6166c = str2;
        this.f6167d = iMessenger;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0098, code lost:
        if (com.miui.permcenter.install.PackageVerificationRecevier.a(r8.f6164a, r9, r8.f6165b) == false) goto L_0x004f;
     */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Void doInBackground(java.lang.Void... r9) {
        /*
            r8 = this;
            java.lang.String r9 = "parseApk"
            java.lang.String r0 = "PackageVerifyedRecevier"
            r1 = 0
            android.content.Context r2 = r8.f6164a     // Catch:{ Exception -> 0x0018 }
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x0018 }
            java.lang.String r3 = "content://guard"
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0018 }
            java.lang.String r4 = r8.f6165b     // Catch:{ Exception -> 0x0018 }
            android.os.Bundle r9 = r2.call(r3, r9, r4, r1)     // Catch:{ Exception -> 0x0018 }
            goto L_0x0020
        L_0x0018:
            r2 = move-exception
            android.util.Log.e(r0, r9, r2)
            com.miui.analytics.AnalyticsUtil.trackException(r2)
            r9 = r1
        L_0x0020:
            if (r9 == 0) goto L_0x002b
            java.lang.String r2 = "pkgInfo"
            android.os.Parcelable r9 = r9.getParcelable(r2)
            android.content.pm.PackageInfo r9 = (android.content.pm.PackageInfo) r9
            goto L_0x002c
        L_0x002b:
            r9 = r1
        L_0x002c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "verify package "
            r2.append(r3)
            java.lang.String r4 = r8.f6165b
            r2.append(r4)
            java.lang.String r4 = " info:  "
            r2.append(r4)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r0, r2)
            r2 = 1
            r4 = 0
            if (r9 != 0) goto L_0x0051
            r5 = r1
        L_0x004f:
            r2 = r4
            goto L_0x009b
        L_0x0051:
            java.lang.String r5 = r9.packageName
            java.lang.String r6 = "com.google.android.webview"
            boolean r6 = r6.equals(r5)
            if (r6 == 0) goto L_0x0090
            java.lang.String r6 = r8.f6166c
            java.lang.String r7 = "com.miui.packageinstaller"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0079
            java.lang.String r6 = r8.f6166c
            java.lang.String r7 = "com.google.android.packageinstaller"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0079
            java.lang.String r6 = r8.f6166c
            java.lang.String r7 = "com.android.packageinstaller"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0090
        L_0x0079:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "can't install com.google.android.webview installer : "
            r2.append(r6)
            java.lang.String r6 = r8.f6166c
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r0, r2)
            r2 = r4
        L_0x0090:
            android.content.Context r6 = r8.f6164a
            java.lang.String r7 = r8.f6165b
            boolean r9 = com.miui.permcenter.install.PackageVerificationRecevier.a((android.content.Context) r6, (android.content.pm.PackageInfo) r9, (java.lang.String) r7)
            if (r9 != 0) goto L_0x009b
            goto L_0x004f
        L_0x009b:
            android.content.Context r9 = r8.f6164a
            java.lang.String r6 = r8.f6166c
            com.miui.permcenter.install.PackageVerificationRecevier.b(r9, r6, r5)
            android.os.IMessenger r9 = r8.f6167d
            if (r9 == 0) goto L_0x00b5
            if (r2 == 0) goto L_0x00a9
            r4 = -1
        L_0x00a9:
            android.os.Message r9 = new android.os.Message
            r9.<init>()
            r9.what = r4
            android.os.IMessenger r4 = r8.f6167d     // Catch:{ RemoteException -> 0x00b5 }
            r4.send(r9)     // Catch:{ RemoteException -> 0x00b5 }
        L_0x00b5:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r3)
            java.lang.String r3 = r8.f6165b
            r9.append(r3)
            java.lang.String r3 = " finish "
            r9.append(r3)
            r9.append(r2)
            java.lang.String r9 = r9.toString()
            android.util.Log.d(r0, r9)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.install.n.doInBackground(java.lang.Void[]):java.lang.Void");
    }
}
