package com.miui.antispam.ui.activity;

import android.os.AsyncTask;
import android.util.Pair;
import b.b.a.e.n;
import com.miui.antispam.ui.activity.MsgInterceptSettingsActivity;
import com.miui.securitycenter.R;

class Q extends AsyncTask<Void, Void, Pair<Integer, Integer>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MsgInterceptSettingsActivity.a f2569a;

    Q(MsgInterceptSettingsActivity.a aVar) {
        this.f2569a = aVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Pair<Integer, Integer> doInBackground(Void... voidArr) {
        return new Pair<>(Integer.valueOf(n.a(this.f2569a.k, 1, this.f2569a.n)), Integer.valueOf(n.a(this.f2569a.k, 4, this.f2569a.n)));
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Pair<Integer, Integer> pair) {
        this.f2569a.i.a(this.f2569a.k.getResources().getQuantityString(R.plurals.st_show_num_keyword, ((Integer) pair.first).intValue(), new Object[]{pair.first}));
        this.f2569a.j.a(this.f2569a.k.getResources().getQuantityString(R.plurals.st_show_num_keyword, ((Integer) pair.second).intValue(), new Object[]{pair.second}));
    }
}
