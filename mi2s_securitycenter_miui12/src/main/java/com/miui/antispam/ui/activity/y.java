package com.miui.antispam.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import b.b.a.d.a.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import miui.provider.ExtraTelephony;

class y extends AsyncTask<Void, Integer, Void> {

    /* renamed from: a  reason: collision with root package name */
    private int f2623a = 0;

    /* renamed from: b  reason: collision with root package name */
    private ProgressDialog f2624b;

    /* renamed from: c  reason: collision with root package name */
    ArrayList<e.a> f2625c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ z f2626d;

    y(z zVar) {
        this.f2626d = zVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Iterator<e.a> it = this.f2625c.iterator();
        while (it.hasNext()) {
            e.a next = it.next();
            int i = next.f1338b;
            if (i == 3 || i == 2) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("sync_dirty", 1);
                this.f2626d.getContentResolver().update(Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(next.f1337a)), contentValues, (String) null, (String[]) null);
            } else {
                this.f2626d.getContentResolver().delete(Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(next.f1337a)), (String) null, (String[]) null);
            }
            int i2 = this.f2623a + 1;
            this.f2623a = i2;
            publishProgress(new Integer[]{Integer.valueOf(i2)});
        }
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        if (this.f2624b.isShowing() && !this.f2626d.isFinishing() && !this.f2626d.isDestroyed()) {
            try {
                this.f2624b.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onProgressUpdate(Integer... numArr) {
        this.f2624b.setProgress(numArr[0].intValue());
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    /* access modifiers changed from: protected */
    public void onPreExecute() {
        SparseBooleanArray g = this.f2626d.f.g();
        for (int i = 0; i < g.size(); i++) {
            if (g.valueAt(i)) {
                this.f2625c.add((e.a) this.f2626d.f.a(g.keyAt(i)));
            }
        }
        this.f2624b = new ProgressDialog(this.f2626d);
        this.f2624b.setProgressStyle(1);
        this.f2624b.setIndeterminate(false);
        this.f2624b.setCancelable(false);
        this.f2624b.setProgressNumberFormat((String) null);
        this.f2624b.setMax(this.f2625c.size());
        this.f2624b.setTitle(this.f2626d.q ? R.string.dlg_remove_blacklist_ing : R.string.dlg_remove_whitelist_ing);
        this.f2624b.show();
    }
}
