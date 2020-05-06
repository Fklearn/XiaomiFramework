package com.miui.antivirus.ui;

import android.os.AsyncTask;
import b.b.b.b;
import com.miui.securitycenter.R;

class m extends AsyncTask<Void, Void, String> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainHandleBar f2973a;

    m(MainHandleBar mainHandleBar) {
        this.f2973a = mainHandleBar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public String doInBackground(Void... voidArr) {
        return b.a(this.f2973a.getContext()).b();
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(String str) {
        this.f2973a.k.setText(this.f2973a.getContext().getString(R.string.ss_activity_main_support_text, new Object[]{str}));
    }
}
