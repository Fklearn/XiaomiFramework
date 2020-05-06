package com.miui.optimizemanage.memoryclean;

import android.os.AsyncTask;
import java.util.List;

class g extends AsyncTask<Void, Void, List<c>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LockAppManageActivity f5969a;

    g(LockAppManageActivity lockAppManageActivity) {
        this.f5969a = lockAppManageActivity;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.optimizemanage.memoryclean.LockAppManageActivity] */
    /* access modifiers changed from: protected */
    /* renamed from: a */
    public List<c> doInBackground(Void... voidArr) {
        return b.a(this.f5969a, i.a());
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<c> list) {
        this.f5969a.a(false);
        if (list != null) {
            this.f5969a.a(list);
            this.f5969a.l();
            this.f5969a.f5948c.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        super.onPreExecute();
        this.f5969a.a(true);
    }
}
