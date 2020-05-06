package com.miui.firstaidkit.c;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.FirstAidKitActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class a extends AsyncTask<Void, Void, com.miui.securityscan.g.a> {

    /* renamed from: a  reason: collision with root package name */
    private Context f3930a;

    /* renamed from: b  reason: collision with root package name */
    private WeakReference<FirstAidKitActivity> f3931b;

    public a(FirstAidKitActivity firstAidKitActivity) {
        this.f3930a = firstAidKitActivity.getApplicationContext();
        this.f3931b = new WeakReference<>(firstAidKitActivity);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007f A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0080 A[RETURN] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.securityscan.g.a doInBackground(java.lang.Void... r8) {
        /*
            r7 = this;
            java.lang.String r8 = "dataVersion_FirstAidResult"
            java.lang.String r0 = ""
            java.lang.String r1 = "initSucess_FirstAidResult"
            boolean r2 = r7.isCancelled()
            r3 = 0
            if (r2 == 0) goto L_0x000e
            return r3
        L_0x000e:
            android.content.Context r2 = r7.f3930a     // Catch:{ Exception -> 0x0070 }
            java.lang.String r4 = "data_config"
            com.miui.securityscan.c.e r2 = com.miui.securityscan.c.e.a((android.content.Context) r2, (java.lang.String) r4)     // Catch:{ Exception -> 0x0070 }
            r4 = 0
            boolean r4 = r2.a((java.lang.String) r1, (boolean) r4)     // Catch:{ Exception -> 0x0070 }
            java.lang.String[] r5 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x0070 }
            com.miui.securityscan.cards.h.a((java.lang.String[]) r5)     // Catch:{ Exception -> 0x0070 }
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x0070 }
            r5.<init>()     // Catch:{ Exception -> 0x0070 }
            java.lang.String r6 = "dataVersion"
            java.lang.String r0 = r2.a((java.lang.String) r8, (java.lang.String) r0)     // Catch:{ Exception -> 0x0070 }
            r5.put(r6, r0)     // Catch:{ Exception -> 0x0070 }
            if (r4 != 0) goto L_0x0039
            java.lang.String r0 = "init"
            java.lang.String r4 = "1"
            r5.put(r0, r4)     // Catch:{ Exception -> 0x0070 }
        L_0x0039:
            java.lang.String r0 = com.miui.securityscan.cards.d.b(r5)     // Catch:{ Exception -> 0x0070 }
            boolean r4 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0070 }
            if (r4 != 0) goto L_0x0067
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x0070 }
            r4.<init>(r0)     // Catch:{ Exception -> 0x0070 }
            r0 = 3
            com.miui.securityscan.cards.d r0 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r4, (int) r0)     // Catch:{ Exception -> 0x0070 }
            if (r0 == 0) goto L_0x0068
            java.lang.String r4 = r0.c()     // Catch:{ Exception -> 0x0070 }
            if (r4 == 0) goto L_0x005c
            java.lang.String r4 = r0.c()     // Catch:{ Exception -> 0x0070 }
            r2.b((java.lang.String) r8, (java.lang.String) r4)     // Catch:{ Exception -> 0x0070 }
        L_0x005c:
            int r8 = r0.h()     // Catch:{ Exception -> 0x0070 }
            r4 = 1
            if (r8 != r4) goto L_0x0068
            r2.b((java.lang.String) r1, (boolean) r4)     // Catch:{ Exception -> 0x0070 }
            goto L_0x0068
        L_0x0067:
            r0 = r3
        L_0x0068:
            if (r0 == 0) goto L_0x0078
            com.miui.securityscan.g.a r8 = new com.miui.securityscan.g.a     // Catch:{ Exception -> 0x0070 }
            r8.<init>(r0)     // Catch:{ Exception -> 0x0070 }
            goto L_0x0079
        L_0x0070:
            r8 = move-exception
            java.lang.String r0 = "LoadFirstAidScanResultTask"
            java.lang.String r1 = "load scanresult data "
            android.util.Log.e(r0, r1, r8)
        L_0x0078:
            r8 = r3
        L_0x0079:
            boolean r0 = r7.isCancelled()
            if (r0 == 0) goto L_0x0080
            return r3
        L_0x0080:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.firstaidkit.c.a.doInBackground(java.lang.Void[]):com.miui.securityscan.g.a");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(com.miui.securityscan.g.a aVar) {
        if (aVar != null) {
            ArrayList<BaseCardModel> b2 = aVar.b();
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3931b.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed() && !b2.isEmpty()) {
                firstAidKitActivity.t = b2;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3931b.get();
        if (firstAidKitActivity != null || firstAidKitActivity.isFinishing() || firstAidKitActivity.isDestroyed()) {
            firstAidKitActivity.t = null;
        }
    }
}
