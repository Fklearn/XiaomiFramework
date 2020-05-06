package com.miui.powercenter.deepsave;

import android.os.AsyncTask;
import b.b.c.d.C0185e;
import java.util.List;

class f extends AsyncTask<Void, Void, List<C0185e>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7055a;

    f(String str) {
        this.f7055a = str;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0090 A[RETURN] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<b.b.c.d.C0185e> doInBackground(java.lang.Void... r9) {
        /*
            r8 = this;
            r9 = 19
            android.os.Process.setThreadPriority(r9)
            r9 = 0
            r0 = 0
            com.miui.powercenter.deepsave.i r1 = new com.miui.powercenter.deepsave.i     // Catch:{ Exception -> 0x007d }
            com.miui.securitycenter.Application r2 = com.miui.securitycenter.Application.d()     // Catch:{ Exception -> 0x007d }
            r1.<init>(r2)     // Catch:{ Exception -> 0x007d }
            java.lang.String r2 = r1.b()     // Catch:{ Exception -> 0x007d }
            r3 = 2
            b.b.c.d.C0188h.a((int) r3)     // Catch:{ Exception -> 0x007d }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x007d }
            r4 = 1
            if (r3 != 0) goto L_0x0030
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x007d }
            r3.<init>(r2)     // Catch:{ Exception -> 0x007d }
            boolean r2 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ Exception -> 0x007d }
            if (r2 != 0) goto L_0x002a
            r2 = r4
            goto L_0x002b
        L_0x002a:
            r2 = r9
        L_0x002b:
            b.b.c.d.h r2 = b.b.c.d.C0188h.a((org.json.JSONObject) r3, (boolean) r2)     // Catch:{ Exception -> 0x007d }
            goto L_0x0031
        L_0x0030:
            r2 = r0
        L_0x0031:
            boolean r3 = r1.a()     // Catch:{ Exception -> 0x007b }
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x007b }
            r5.<init>()     // Catch:{ Exception -> 0x007b }
            if (r3 != 0) goto L_0x0043
            java.lang.String r3 = "init"
            java.lang.String r6 = "1"
            r5.put(r3, r6)     // Catch:{ Exception -> 0x007b }
        L_0x0043:
            java.lang.String r3 = r8.f7055a     // Catch:{ Exception -> 0x007b }
            java.lang.String r3 = b.b.c.d.C0188h.a((java.lang.String) r3, (java.util.Map<java.lang.String, java.lang.String>) r5)     // Catch:{ Exception -> 0x007b }
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x007b }
            if (r5 != 0) goto L_0x0086
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x007b }
            r5.<init>(r3)     // Catch:{ Exception -> 0x007b }
            b.b.c.d.h r5 = b.b.c.d.C0188h.a((org.json.JSONObject) r5, (boolean) r4)     // Catch:{ Exception -> 0x007b }
            java.lang.String r6 = ""
            if (r5 != 0) goto L_0x0061
        L_0x005c:
            r1.a((java.lang.String) r6)     // Catch:{ Exception -> 0x007b }
            r2 = r0
            goto L_0x0086
        L_0x0061:
            boolean r7 = r5.a()     // Catch:{ Exception -> 0x007b }
            if (r7 == 0) goto L_0x006a
            r1.a((boolean) r4)     // Catch:{ Exception -> 0x007b }
        L_0x006a:
            java.util.List r4 = r5.b()     // Catch:{ Exception -> 0x007b }
            if (r4 == 0) goto L_0x005c
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x007b }
            if (r4 != 0) goto L_0x005c
            r1.a((java.lang.String) r3)     // Catch:{ Exception -> 0x007b }
            r2 = r5
            goto L_0x0086
        L_0x007b:
            r1 = move-exception
            goto L_0x007f
        L_0x007d:
            r1 = move-exception
            r2 = r0
        L_0x007f:
            java.lang.String r3 = "DataModelManager"
            java.lang.String r4 = "preload data"
            android.util.Log.e(r3, r4, r1)
        L_0x0086:
            android.os.Process.setThreadPriority(r9)
            if (r2 == 0) goto L_0x0090
            java.util.List r9 = r2.b()
            return r9
        L_0x0090:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.deepsave.f.doInBackground(java.lang.Void[]):java.util.List");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<C0185e> list) {
        if (list != null) {
            g.f7056a.addAll(list);
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        g.f7056a.clear();
    }
}
