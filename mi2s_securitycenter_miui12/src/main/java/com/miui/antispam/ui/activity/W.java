package com.miui.antispam.ui.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import b.b.a.e.n;
import com.miui.securitycenter.R;

class W extends AsyncTask<Void, Integer, Void> {

    /* renamed from: a  reason: collision with root package name */
    private int f2577a = 0;

    /* renamed from: b  reason: collision with root package name */
    private ProgressDialog f2578b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ RemoveBlacklistActivity f2579c;

    W(RemoveBlacklistActivity removeBlacklistActivity) {
        this.f2579c = removeBlacklistActivity;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        for (String str : this.f2579c.f2570d) {
            n.b(this.f2579c.getApplicationContext(), str, 0, 1, 1);
            n.b(this.f2579c.getApplicationContext(), str, 0, 1, 2);
            int i = this.f2577a + 1;
            this.f2577a = i;
            publishProgress(new Integer[]{Integer.valueOf(i)});
        }
        Log.d("RemoveBlacklistActivity", "Remove blacklist completed.");
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        if (this.f2578b.isShowing() && !this.f2579c.isFinishing() && !this.f2579c.isDestroyed()) {
            try {
                this.f2578b.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.f2579c.finish();
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onProgressUpdate(Integer... numArr) {
        this.f2578b.setProgress(numArr[0].intValue());
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.RemoveBlacklistActivity] */
    /* access modifiers changed from: protected */
    public void onPreExecute() {
        this.f2578b = new ProgressDialog(this.f2579c);
        this.f2578b.setProgressStyle(1);
        this.f2578b.setIndeterminate(false);
        this.f2578b.setCancelable(false);
        this.f2578b.setProgressNumberFormat((String) null);
        this.f2578b.setMax(this.f2579c.f2570d.length);
        this.f2578b.setTitle(this.f2579c.getString(R.string.dlg_remove_blacklist_ing));
        this.f2578b.show();
    }
}
