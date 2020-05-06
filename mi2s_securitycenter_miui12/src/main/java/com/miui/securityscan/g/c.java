package com.miui.securityscan.g;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.L;
import com.miui.securityscan.M;
import com.miui.securityscan.i.p;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class c extends AsyncTask<Void, Void, a> {

    /* renamed from: a  reason: collision with root package name */
    private Context f7709a;

    /* renamed from: b  reason: collision with root package name */
    private WeakReference<L> f7710b;

    public c(L l) {
        Activity activity = l.getActivity();
        if (activity != null) {
            this.f7709a = activity.getApplicationContext();
        }
        this.f7710b = new WeakReference<>(l);
    }

    private void a() {
        if (this.f7709a != null && M.a()) {
            Context context = this.f7709a;
            boolean a2 = p.a(context, context.getPackageName());
            boolean z = !M.m();
            if (a2 != z) {
                Context context2 = this.f7709a;
                p.a(context2, context2.getPackageName(), z);
            }
            M.a(true);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b0 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00b1 A[RETURN] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.securityscan.g.a doInBackground(java.lang.Void... r8) {
        /*
            r7 = this;
            java.lang.String r8 = "dataVersionHomePage"
            java.lang.String r0 = "initSucess"
            boolean r1 = r7.isCancelled()
            r2 = 0
            if (r1 != 0) goto L_0x00b2
            android.content.Context r1 = r7.f7709a
            if (r1 != 0) goto L_0x0011
            goto L_0x00b2
        L_0x0011:
            java.lang.String r3 = "data_config"
            com.miui.securityscan.c.e r1 = com.miui.securityscan.c.e.a((android.content.Context) r1, (java.lang.String) r3)     // Catch:{ Exception -> 0x009e }
            r3 = 0
            boolean r3 = r1.a((java.lang.String) r0, (boolean) r3)     // Catch:{ Exception -> 0x009e }
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x009e }
            r4.<init>()     // Catch:{ Exception -> 0x009e }
            java.lang.String r5 = "dataVersion"
            java.lang.String r6 = ""
            java.lang.String r6 = r1.a((java.lang.String) r8, (java.lang.String) r6)     // Catch:{ Exception -> 0x009e }
            r4.put(r5, r6)     // Catch:{ Exception -> 0x009e }
            if (r3 != 0) goto L_0x0035
            java.lang.String r3 = "init"
            java.lang.String r5 = "1"
            r4.put(r3, r5)     // Catch:{ Exception -> 0x009e }
        L_0x0035:
            java.lang.String r3 = com.miui.securityscan.cards.d.c(r4)     // Catch:{ Exception -> 0x009e }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x009e }
            if (r4 != 0) goto L_0x0095
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x009e }
            r4.<init>(r3)     // Catch:{ Exception -> 0x009e }
            r5 = 1
            com.miui.securityscan.cards.d r4 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r4, (int) r5)     // Catch:{ Exception -> 0x009e }
            if (r4 == 0) goto L_0x0096
            java.lang.String r6 = r4.c()     // Catch:{ Exception -> 0x009e }
            if (r6 == 0) goto L_0x0058
            java.lang.String r6 = r4.c()     // Catch:{ Exception -> 0x009e }
            r1.b((java.lang.String) r8, (java.lang.String) r6)     // Catch:{ Exception -> 0x009e }
        L_0x0058:
            int r8 = r4.h()     // Catch:{ Exception -> 0x009e }
            if (r8 != r5) goto L_0x0061
            r1.b((java.lang.String) r0, (boolean) r5)     // Catch:{ Exception -> 0x009e }
        L_0x0061:
            boolean r8 = r4.l()     // Catch:{ Exception -> 0x009e }
            java.lang.String r0 = "securityscan_homelist_cache"
            if (r8 == 0) goto L_0x0071
            android.content.Context r8 = r7.f7709a     // Catch:{ Exception -> 0x009e }
            com.miui.securityscan.i.h.a(r8, r0)     // Catch:{ Exception -> 0x009e }
            com.miui.securityscan.L.f7558c = r2     // Catch:{ Exception -> 0x009e }
            goto L_0x0096
        L_0x0071:
            java.util.ArrayList r8 = r4.f()     // Catch:{ Exception -> 0x009e }
            if (r8 == 0) goto L_0x0096
            boolean r1 = r8.isEmpty()     // Catch:{ Exception -> 0x009e }
            if (r1 != 0) goto L_0x0096
            boolean r1 = r4.a()     // Catch:{ Exception -> 0x009e }
            if (r1 != 0) goto L_0x0089
            boolean r1 = r4.b()     // Catch:{ Exception -> 0x009e }
            if (r1 == 0) goto L_0x0096
        L_0x0089:
            android.content.Context r1 = r7.f7709a     // Catch:{ Exception -> 0x009e }
            com.miui.securityscan.i.h.a(r1, r0, r3)     // Catch:{ Exception -> 0x009e }
            java.util.ArrayList r8 = com.miui.securityscan.cards.d.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r8)     // Catch:{ Exception -> 0x009e }
            com.miui.securityscan.L.f7558c = r8     // Catch:{ Exception -> 0x009e }
            goto L_0x0096
        L_0x0095:
            r4 = r2
        L_0x0096:
            if (r4 == 0) goto L_0x00a6
            com.miui.securityscan.g.a r8 = new com.miui.securityscan.g.a     // Catch:{ Exception -> 0x009e }
            r8.<init>(r4)     // Catch:{ Exception -> 0x009e }
            goto L_0x00a7
        L_0x009e:
            r8 = move-exception
            java.lang.String r0 = "LoadFunctionAndAdvertisementDataTask"
            java.lang.String r1 = "load homepage data "
            android.util.Log.e(r0, r1, r8)
        L_0x00a6:
            r8 = r2
        L_0x00a7:
            r7.a()
            boolean r0 = r7.isCancelled()
            if (r0 == 0) goto L_0x00b1
            return r2
        L_0x00b1:
            return r8
        L_0x00b2:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.g.c.doInBackground(java.lang.Void[]):com.miui.securityscan.g.a");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(a aVar) {
        boolean z;
        ArrayList<BaseCardModel> b2;
        if (aVar == null || (b2 = aVar.b()) == null || b2.isEmpty() || (!aVar.c() && !aVar.d())) {
            z = false;
        } else {
            z = true;
            L l = (L) this.f7710b.get();
            if (l != null) {
                if (aVar.e() || (l.da && !l.ea)) {
                    l.fa = null;
                    l.a(b2);
                } else {
                    l.fa = aVar.a();
                }
            }
        }
        L l2 = (L) this.f7710b.get();
        if (!z && l2 != null) {
            l2.fa = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        L l = (L) this.f7710b.get();
        if (l != null) {
            l.ea = false;
            l.fa = null;
        }
    }
}
