package com.miui.gamebooster.xunyou;

import android.os.AsyncTask;
import com.miui.gamebooster.k.b;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class f extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<GameBoosterRealMainActivity> f5408a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f5409b;

    public f(GameBoosterRealMainActivity gameBoosterRealMainActivity, boolean z) {
        this.f5408a = new WeakReference<>(gameBoosterRealMainActivity);
        this.f5409b = z;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) this.f5408a.get();
        if (gameBoosterRealMainActivity == null || isCancelled()) {
            return null;
        }
        return Boolean.valueOf(b.b().a(new HashMap(), gameBoosterRealMainActivity.getApplicationContext(), this.f5409b));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPostExecute(java.lang.Boolean r9) {
        /*
            r8 = this;
            super.onPostExecute(r9)
            java.lang.ref.WeakReference<com.miui.gamebooster.ui.GameBoosterRealMainActivity> r0 = r8.f5408a
            java.lang.Object r0 = r0.get()
            com.miui.gamebooster.ui.GameBoosterRealMainActivity r0 = (com.miui.gamebooster.ui.GameBoosterRealMainActivity) r0
            if (r9 == 0) goto L_0x00b2
            if (r0 == 0) goto L_0x00b2
            boolean r1 = r0.isFinishing()
            if (r1 != 0) goto L_0x00b2
            boolean r1 = r0.isDestroyed()
            if (r1 == 0) goto L_0x001d
            goto L_0x00b2
        L_0x001d:
            java.util.List<android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>> r1 = r0.v
            boolean r1 = r1.contains(r8)
            if (r1 == 0) goto L_0x002a
            java.util.List<android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>> r1 = r0.v
            r1.remove(r8)
        L_0x002a:
            boolean r1 = r8.f5409b
            if (r1 != 0) goto L_0x0034
            boolean r1 = r9.booleanValue()
            r0.n = r1
        L_0x0034:
            boolean r9 = r9.booleanValue()
            if (r9 == 0) goto L_0x00ab
            com.miui.gamebooster.k.b r9 = com.miui.gamebooster.k.b.b()
            r1 = -1
            java.lang.String r3 = "key_gamebooster_red_point_press_day"
            long r1 = com.miui.common.persistence.b.a((java.lang.String) r3, (long) r1)
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS
            long r4 = java.lang.System.currentTimeMillis()
            long r3 = r3.toDays(r4)
            boolean r5 = r9.a()
            java.lang.String r6 = ""
            r7 = 2131231501(0x7f08030d, float:1.8079085E38)
            if (r5 == 0) goto L_0x0096
            boolean r1 = r8.f5409b
            if (r1 == 0) goto L_0x0072
            r1 = 2
            java.text.SimpleDateFormat r1 = com.miui.networkassistant.utils.DateUtil.getDateFormat(r1)
            java.util.Date r2 = new java.util.Date
            r2.<init>()
            java.lang.String r1 = r1.format(r2)
            java.lang.String r2 = "key_gamebooster_signed_day"
            com.miui.common.persistence.b.b((java.lang.String) r2, (java.lang.String) r1)
        L_0x0072:
            int r1 = r9.c()
            if (r1 <= 0) goto L_0x009a
            android.content.res.Resources r1 = r0.getResources()
            r2 = 2131623976(0x7f0e0028, float:1.8875119E38)
            int r3 = r9.c()
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            int r9 = r9.c()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r4[r5] = r9
            java.lang.String r6 = r1.getQuantityString(r2, r3, r4)
            goto L_0x009a
        L_0x0096:
            int r9 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r9 != 0) goto L_0x009e
        L_0x009a:
            r0.a((int) r7, (java.lang.String) r6)
            goto L_0x00a1
        L_0x009e:
            r0.e()
        L_0x00a1:
            boolean r9 = r8.f5409b
            if (r9 == 0) goto L_0x00b2
            com.miui.gamebooster.xunyou.m r9 = r0.f4887c
            r9.d()
            goto L_0x00b2
        L_0x00ab:
            boolean r9 = r8.f5409b
            if (r9 == 0) goto L_0x00b2
            r0.o()
        L_0x00b2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.xunyou.f.onPostExecute(java.lang.Boolean):void");
    }
}
