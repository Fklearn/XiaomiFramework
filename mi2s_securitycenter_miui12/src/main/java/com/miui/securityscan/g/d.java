package com.miui.securityscan.g;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.L;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class d extends AsyncTask<Void, Void, a> {

    /* renamed from: a  reason: collision with root package name */
    private Context f7711a;

    /* renamed from: b  reason: collision with root package name */
    private WeakReference<L> f7712b;

    public d(L l) {
        Activity activity = l.getActivity();
        if (activity != null) {
            this.f7711a = activity.getApplicationContext();
        }
        this.f7712b = new WeakReference<>(l);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b1 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b2 A[RETURN] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.securityscan.g.a doInBackground(java.lang.Void... r8) {
        /*
            r7 = this;
            java.lang.String r8 = "dataVersionScanResult"
            java.lang.String r0 = "initSucessResult"
            java.lang.String r1 = ""
            boolean r2 = r7.isCancelled()
            r3 = 0
            if (r2 != 0) goto L_0x00b3
            android.content.Context r2 = r7.f7711a
            if (r2 != 0) goto L_0x0013
            goto L_0x00b3
        L_0x0013:
            com.miui.securityscan.M.a((java.lang.String) r3)     // Catch:{ Exception -> 0x00a2 }
            android.content.Context r2 = r7.f7711a     // Catch:{ Exception -> 0x00a2 }
            java.lang.String r4 = "data_config"
            com.miui.securityscan.c.e r2 = com.miui.securityscan.c.e.a((android.content.Context) r2, (java.lang.String) r4)     // Catch:{ Exception -> 0x00a2 }
            r4 = 0
            boolean r4 = r2.a((java.lang.String) r0, (boolean) r4)     // Catch:{ Exception -> 0x00a2 }
            java.lang.String[] r5 = new java.lang.String[]{r1, r1}     // Catch:{ Exception -> 0x00a2 }
            com.miui.securityscan.cards.h.a((java.lang.String[]) r5)     // Catch:{ Exception -> 0x00a2 }
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x00a2 }
            r5.<init>()     // Catch:{ Exception -> 0x00a2 }
            java.lang.String r6 = "dataVersion"
            java.lang.String r1 = r2.a((java.lang.String) r8, (java.lang.String) r1)     // Catch:{ Exception -> 0x00a2 }
            r5.put(r6, r1)     // Catch:{ Exception -> 0x00a2 }
            if (r4 != 0) goto L_0x0041
            java.lang.String r1 = "init"
            java.lang.String r4 = "1"
            r5.put(r1, r4)     // Catch:{ Exception -> 0x00a2 }
        L_0x0041:
            java.lang.String r1 = com.miui.securityscan.cards.d.e(r5)     // Catch:{ Exception -> 0x00a2 }
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x00a2 }
            if (r4 != 0) goto L_0x0099
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x00a2 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x00a2 }
            r1 = 2
            com.miui.securityscan.cards.d r1 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r4, (int) r1)     // Catch:{ Exception -> 0x00a2 }
            if (r1 == 0) goto L_0x009a
            java.lang.String r4 = r1.c()     // Catch:{ Exception -> 0x00a2 }
            if (r4 == 0) goto L_0x0064
            java.lang.String r4 = r1.c()     // Catch:{ Exception -> 0x00a2 }
            r2.b((java.lang.String) r8, (java.lang.String) r4)     // Catch:{ Exception -> 0x00a2 }
        L_0x0064:
            int r8 = r1.h()     // Catch:{ Exception -> 0x00a2 }
            r4 = 1
            if (r8 != r4) goto L_0x006e
            r2.b((java.lang.String) r0, (boolean) r4)     // Catch:{ Exception -> 0x00a2 }
        L_0x006e:
            java.lang.ref.WeakReference<com.miui.securityscan.L> r8 = r7.f7712b     // Catch:{ Exception -> 0x00a2 }
            java.lang.Object r8 = r8.get()     // Catch:{ Exception -> 0x00a2 }
            com.miui.securityscan.L r8 = (com.miui.securityscan.L) r8     // Catch:{ Exception -> 0x00a2 }
            if (r8 == 0) goto L_0x0095
            boolean r0 = r1.k()     // Catch:{ Exception -> 0x00a2 }
            if (r0 == 0) goto L_0x0095
            int r0 = r8.O     // Catch:{ Exception -> 0x00a2 }
            if (r0 == r4) goto L_0x0095
            java.lang.String r0 = r1.g()     // Catch:{ Exception -> 0x00a2 }
            com.miui.securityscan.M.a((java.lang.String) r0)     // Catch:{ Exception -> 0x00a2 }
            com.miui.securityscan.scanner.ScoreManager r0 = r8.l     // Catch:{ Exception -> 0x00a2 }
            if (r0 == 0) goto L_0x009a
            com.miui.securityscan.scanner.ScoreManager r8 = r8.l     // Catch:{ Exception -> 0x00a2 }
            r0 = 40
            r8.a((int) r0)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x009a
        L_0x0095:
            com.miui.securityscan.M.a((java.lang.String) r3)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x009a
        L_0x0099:
            r1 = r3
        L_0x009a:
            if (r1 == 0) goto L_0x00aa
            com.miui.securityscan.g.a r8 = new com.miui.securityscan.g.a     // Catch:{ Exception -> 0x00a2 }
            r8.<init>(r1)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x00ab
        L_0x00a2:
            r8 = move-exception
            java.lang.String r0 = "LoadScanResultAdvertisementDataTask"
            java.lang.String r1 = "load scanresult data "
            android.util.Log.e(r0, r1, r8)
        L_0x00aa:
            r8 = r3
        L_0x00ab:
            boolean r0 = r7.isCancelled()
            if (r0 == 0) goto L_0x00b2
            return r3
        L_0x00b2:
            return r8
        L_0x00b3:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.g.d.doInBackground(java.lang.Void[]):com.miui.securityscan.g.a");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(a aVar) {
        if (aVar != null) {
            ArrayList<BaseCardModel> b2 = aVar.b();
            L l = (L) this.f7712b.get();
            if (l != null && !b2.isEmpty() && l.O != 1) {
                l.N = b2;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        L l = (L) this.f7712b.get();
        if (l != null) {
            l.N = null;
        }
    }
}
