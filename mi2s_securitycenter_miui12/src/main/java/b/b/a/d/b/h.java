package b.b.a.d.b;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Pair;

class h extends AsyncTask<Void, Void, Pair<String, String>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f1390a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ i f1391b;

    h(i iVar, Context context) {
        this.f1391b = iVar;
        this.f1390a = context;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0109, code lost:
        if (android.text.TextUtils.isEmpty(r1) == false) goto L_0x0140;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0158, code lost:
        if (android.text.TextUtils.isEmpty(r1) == false) goto L_0x015c;
     */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<java.lang.String, java.lang.String> doInBackground(java.lang.Void... r11) {
        /*
            r10 = this;
            b.b.a.d.b.i r11 = r10.f1391b
            java.lang.String r11 = r11.f
            android.content.Context r0 = r10.f1390a
            b.b.a.d.b.i r1 = r10.f1391b
            java.lang.String r1 = r1.f
            java.lang.String r0 = b.b.a.e.n.c((android.content.Context) r0, (java.lang.String) r1)
            android.content.Context r1 = r10.f1390a
            b.b.a.d.b.i r2 = r10.f1391b
            java.lang.String r2 = r2.f
            java.lang.String r1 = miui.telephony.PhoneNumberUtils.parseTelocationString(r1, r2)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r3 = " "
            r4 = 1
            java.lang.String r5 = ""
            if (r2 != 0) goto L_0x0065
            b.b.a.d.b.i r11 = r10.f1391b
            int r11 = r11.g
            if (r11 != r4) goto L_0x0047
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r5)
            b.b.a.d.b.i r2 = r10.f1391b
            java.lang.String r2 = r2.f
            r11.append(r2)
            java.lang.String r11 = r11.toString()
            goto L_0x0048
        L_0x0047:
            r11 = r5
        L_0x0048:
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x0062
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            r2.append(r3)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            goto L_0x015c
        L_0x0062:
            r1 = r11
            goto L_0x015c
        L_0x0065:
            android.content.Context r0 = r10.f1390a
            b.b.a.d.b.i r2 = r10.f1391b
            java.lang.String r2 = r2.f
            r6 = 0
            miui.yellowpage.YellowPagePhone r0 = miui.yellowpage.YellowPageUtils.getPhoneInfo(r0, r2, r6)
            if (r0 == 0) goto L_0x0142
            java.lang.String r2 = r0.getTag()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0089
            b.b.a.d.b.i r2 = r10.f1391b
            java.lang.String r7 = r2.f
            java.lang.String r2 = r2.a((java.lang.String) r7)
            goto L_0x008d
        L_0x0089:
            java.lang.String r2 = r0.getTag()
        L_0x008d:
            boolean r7 = r0.isYellowPage()
            if (r7 == 0) goto L_0x00d6
            java.lang.String r0 = r0.getYellowPageName()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x00d3
            b.b.a.d.b.i r11 = r10.f1391b
            int r11 = r11.g
            if (r11 != r4) goto L_0x00ba
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r5)
            b.b.a.d.b.i r2 = r10.f1391b
            java.lang.String r2 = r2.f
            r11.append(r2)
            java.lang.String r5 = r11.toString()
        L_0x00ba:
            boolean r11 = android.text.TextUtils.isEmpty(r1)
            if (r11 != 0) goto L_0x00d2
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r5)
            r11.append(r3)
            r11.append(r1)
            java.lang.String r5 = r11.toString()
        L_0x00d2:
            r11 = r0
        L_0x00d3:
            r0 = r11
            goto L_0x015b
        L_0x00d6:
            boolean r7 = r0.isUserMarked()
            if (r7 == 0) goto L_0x00e6
            android.content.Context r0 = r10.f1390a
            r4 = 2131756809(0x7f100709, float:1.9144536E38)
            java.lang.String r0 = r0.getString(r4)
            goto L_0x00ff
        L_0x00e6:
            int r0 = r0.getMarkedCount()
            android.content.Context r7 = r10.f1390a
            android.content.res.Resources r7 = r7.getResources()
            r8 = 2131623998(0x7f0e003e, float:1.8875163E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r4[r6] = r9
            java.lang.String r0 = r7.getQuantityString(r8, r0, r4)
        L_0x00ff:
            boolean r4 = android.text.TextUtils.isEmpty(r2)
            if (r4 == 0) goto L_0x010c
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x00d3
            goto L_0x0140
        L_0x010c:
            boolean r4 = android.text.TextUtils.isEmpty(r1)
            if (r4 != 0) goto L_0x012d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r1 = " | "
            r4.append(r1)
            r4.append(r0)
            r4.append(r3)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            goto L_0x013f
        L_0x012d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r3)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x013f:
            r1 = r0
        L_0x0140:
            r0 = r11
            goto L_0x015c
        L_0x0142:
            b.b.a.d.b.i r0 = r10.f1391b
            int r0 = r0.g
            if (r0 == r4) goto L_0x0153
            android.content.Context r11 = r10.f1390a
            r0 = 2131758224(0x7f100c90, float:1.9147406E38)
            java.lang.String r11 = r11.getString(r0)
        L_0x0153:
            r0 = r11
            boolean r11 = android.text.TextUtils.isEmpty(r1)
            if (r11 != 0) goto L_0x015b
            goto L_0x015c
        L_0x015b:
            r1 = r5
        L_0x015c:
            android.util.Pair r11 = new android.util.Pair
            r11.<init>(r0, r1)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.d.b.h.doInBackground(java.lang.Void[]):android.util.Pair");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Pair<String, String> pair) {
        String str = (String) pair.second;
        this.f1391b.f1395d.setTitle((String) pair.first);
        if (!TextUtils.isEmpty(str)) {
            this.f1391b.f1395d.setSubtitle(str);
        } else {
            this.f1391b.f1395d.setSubtitle("");
        }
    }
}
