package com.miui.gamebooster.mutiwindow;

import android.os.AsyncTask;
import com.miui.gamebooster.ui.QuickReplySettingsActivity;
import java.util.List;

class c extends AsyncTask<Void, Void, List<String>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f4625a;

    c(d dVar) {
        this.f4625a = dVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public List<String> doInBackground(Void... voidArr) {
        return QuickReplySettingsActivity.a(this.f4625a.e);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<String> list) {
        super.onPostExecute(list);
        synchronized (this.f4625a.f4629d) {
            if (list != null) {
                if (!list.isEmpty()) {
                    this.f4625a.f4628c.addAll(list);
                }
            }
        }
    }
}
