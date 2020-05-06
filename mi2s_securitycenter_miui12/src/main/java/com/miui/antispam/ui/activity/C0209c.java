package com.miui.antispam.ui.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.miui.securitycenter.R;

/* renamed from: com.miui.antispam.ui.activity.c  reason: case insensitive filesystem */
class C0209c extends AsyncTask<Void, Integer, Void> {

    /* renamed from: a  reason: collision with root package name */
    private int f2585a = 0;

    /* renamed from: b  reason: collision with root package name */
    private ProgressDialog f2586b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String[] f2587c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f2588d;
    final /* synthetic */ int[] e;
    final /* synthetic */ int f;
    final /* synthetic */ int g;
    final /* synthetic */ AddAntiSpamActivity h;

    C0209c(AddAntiSpamActivity addAntiSpamActivity, String[] strArr, int i, int[] iArr, int i2, int i3) {
        this.h = addAntiSpamActivity;
        this.f2587c = strArr;
        this.f2588d = i;
        this.e = iArr;
        this.f = i2;
        this.g = i3;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0019  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0044 A[SYNTHETIC] */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Void doInBackground(java.lang.Void... r10) {
        /*
            r9 = this;
            int r10 = r9.f2588d
            r0 = 1
            if (r10 == 0) goto L_0x000a
            if (r10 == r0) goto L_0x0010
            r1 = 4
            if (r10 == r1) goto L_0x000d
        L_0x000a:
            int r10 = b.b.a.a.c.f1311a
            goto L_0x0012
        L_0x000d:
            int r10 = b.b.a.a.c.f1313c
            goto L_0x0012
        L_0x0010:
            int r10 = b.b.a.a.c.f1312b
        L_0x0012:
            r7 = 0
            r8 = r7
        L_0x0014:
            java.lang.String[] r1 = r9.f2587c
            int r2 = r1.length
            if (r8 >= r2) goto L_0x0044
            com.miui.antispam.ui.activity.AddAntiSpamActivity r2 = r9.h
            r3 = r1[r8]
            int[] r1 = r9.e
            if (r1 != 0) goto L_0x0023
            r1 = -1
            goto L_0x0025
        L_0x0023:
            r1 = r1[r8]
        L_0x0025:
            r4 = r1
            int r5 = r9.f
            int r6 = r9.g
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r10
            r1.a((java.lang.String) r2, (int) r3, (int) r4, (int) r5, (int) r6)
            java.lang.Integer[] r1 = new java.lang.Integer[r0]
            int r2 = r9.f2585a
            int r2 = r2 + r0
            r9.f2585a = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1[r7] = r2
            r9.publishProgress(r1)
            int r8 = r8 + 1
            goto L_0x0014
        L_0x0044:
            r10 = 0
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.C0209c.doInBackground(java.lang.Void[]):java.lang.Void");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        if (this.f2586b.isShowing() && !this.h.isFinishing() && !this.h.isDestroyed()) {
            try {
                this.f2586b.dismiss();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.h.finish();
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onProgressUpdate(Integer... numArr) {
        super.onProgressUpdate(numArr);
        this.f2586b.setProgress(numArr[0].intValue());
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.AddAntiSpamActivity] */
    /* access modifiers changed from: protected */
    public void onPreExecute() {
        this.f2586b = new ProgressDialog(this.h);
        this.f2586b.setProgressStyle(1);
        this.f2586b.setIndeterminate(false);
        this.f2586b.setCancelable(false);
        this.f2586b.setProgressNumberFormat((String) null);
        this.f2586b.setMax(this.f2587c.length);
        this.f2586b.setTitle(this.f2588d == 0 ? R.string.dlg_import_blacklist : R.string.dlg_import_whitelist);
        this.f2586b.show();
    }
}
