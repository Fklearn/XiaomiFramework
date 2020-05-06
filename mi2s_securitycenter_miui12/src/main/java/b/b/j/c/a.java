package b.b.j.c;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import b.b.j.h;
import com.miui.common.card.models.BaseCardModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class a extends AsyncTask<Void, Void, com.miui.securityscan.g.a> {

    /* renamed from: a  reason: collision with root package name */
    private Context f1814a;

    /* renamed from: b  reason: collision with root package name */
    private WeakReference<h> f1815b;

    public a(h hVar) {
        Activity activity = hVar.getActivity();
        if (activity != null) {
            this.f1814a = activity.getApplicationContext();
        }
        this.f1815b = new WeakReference<>(hVar);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a6 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a7 A[RETURN] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.securityscan.g.a doInBackground(java.lang.Void... r8) {
        /*
            r7 = this;
            java.lang.String r8 = "dataVersion_phoneManage"
            java.lang.String r0 = "initSucess_phoneManage"
            boolean r1 = r7.isCancelled()
            r2 = 0
            if (r1 != 0) goto L_0x00a8
            android.content.Context r1 = r7.f1814a
            if (r1 != 0) goto L_0x0011
            goto L_0x00a8
        L_0x0011:
            java.lang.String r3 = "data_config"
            com.miui.securityscan.c.e r1 = com.miui.securityscan.c.e.a((android.content.Context) r1, (java.lang.String) r3)     // Catch:{ Exception -> 0x0097 }
            r3 = 0
            boolean r3 = r1.a((java.lang.String) r0, (boolean) r3)     // Catch:{ Exception -> 0x0097 }
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x0097 }
            r4.<init>()     // Catch:{ Exception -> 0x0097 }
            java.lang.String r5 = "dataVersion"
            java.lang.String r6 = ""
            java.lang.String r6 = r1.a((java.lang.String) r8, (java.lang.String) r6)     // Catch:{ Exception -> 0x0097 }
            r4.put(r5, r6)     // Catch:{ Exception -> 0x0097 }
            if (r3 != 0) goto L_0x0035
            java.lang.String r3 = "init"
            java.lang.String r5 = "1"
            r4.put(r3, r5)     // Catch:{ Exception -> 0x0097 }
        L_0x0035:
            java.lang.String r3 = com.miui.securityscan.cards.d.d(r4)     // Catch:{ Exception -> 0x0097 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0097 }
            if (r4 != 0) goto L_0x008e
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x0097 }
            r4.<init>(r3)     // Catch:{ Exception -> 0x0097 }
            r5 = 5
            com.miui.securityscan.cards.d r4 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r4, (int) r5)     // Catch:{ Exception -> 0x0097 }
            if (r4 == 0) goto L_0x008f
            java.lang.String r5 = r4.c()     // Catch:{ Exception -> 0x0097 }
            if (r5 == 0) goto L_0x0058
            java.lang.String r5 = r4.c()     // Catch:{ Exception -> 0x0097 }
            r1.b((java.lang.String) r8, (java.lang.String) r5)     // Catch:{ Exception -> 0x0097 }
        L_0x0058:
            int r8 = r4.h()     // Catch:{ Exception -> 0x0097 }
            r5 = 1
            if (r8 != r5) goto L_0x0062
            r1.b((java.lang.String) r0, (boolean) r5)     // Catch:{ Exception -> 0x0097 }
        L_0x0062:
            boolean r8 = r4.l()     // Catch:{ Exception -> 0x0097 }
            java.lang.String r0 = "phonemanage_data_cache"
            if (r8 == 0) goto L_0x0070
            android.content.Context r8 = r7.f1814a     // Catch:{ Exception -> 0x0097 }
            com.miui.securityscan.i.h.a(r8, r0)     // Catch:{ Exception -> 0x0097 }
            goto L_0x008f
        L_0x0070:
            java.util.ArrayList r8 = r4.f()     // Catch:{ Exception -> 0x0097 }
            if (r8 == 0) goto L_0x008f
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0097 }
            if (r8 != 0) goto L_0x008f
            boolean r8 = r4.a()     // Catch:{ Exception -> 0x0097 }
            if (r8 != 0) goto L_0x0088
            boolean r8 = r4.b()     // Catch:{ Exception -> 0x0097 }
            if (r8 == 0) goto L_0x008f
        L_0x0088:
            android.content.Context r8 = r7.f1814a     // Catch:{ Exception -> 0x0097 }
            com.miui.securityscan.i.h.a(r8, r0, r3)     // Catch:{ Exception -> 0x0097 }
            goto L_0x008f
        L_0x008e:
            r4 = r2
        L_0x008f:
            if (r4 == 0) goto L_0x009f
            com.miui.securityscan.g.a r8 = new com.miui.securityscan.g.a     // Catch:{ Exception -> 0x0097 }
            r8.<init>(r4)     // Catch:{ Exception -> 0x0097 }
            goto L_0x00a0
        L_0x0097:
            r8 = move-exception
            java.lang.String r0 = "LoadPhoneManageDataTask"
            java.lang.String r1 = "load phone manage data error"
            android.util.Log.e(r0, r1, r8)
        L_0x009f:
            r8 = r2
        L_0x00a0:
            boolean r0 = r7.isCancelled()
            if (r0 == 0) goto L_0x00a7
            return r2
        L_0x00a7:
            return r8
        L_0x00a8:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.j.c.a.doInBackground(java.lang.Void[]):com.miui.securityscan.g.a");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(com.miui.securityscan.g.a aVar) {
        ArrayList<BaseCardModel> b2;
        super.onPostExecute(aVar);
        h hVar = (h) this.f1815b.get();
        if (aVar == null) {
            return;
        }
        if ((aVar.c() || aVar.d()) && (b2 = aVar.b()) != null && !b2.isEmpty() && hVar != null) {
            hVar.a((List<BaseCardModel>) b2);
        }
    }
}
