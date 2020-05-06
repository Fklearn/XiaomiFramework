package com.miui.appcompatibility;

import android.os.AsyncTask;
import android.widget.TextView;

class g extends AsyncTask<Void, Boolean, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppExcepitonTipsActivity f3082a;

    g(AppExcepitonTipsActivity appExcepitonTipsActivity) {
        this.f3082a = appExcepitonTipsActivity;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        return Boolean.valueOf(this.f3082a.a());
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        AppExcepitonTipsActivity appExcepitonTipsActivity;
        String e;
        super.onPostExecute(bool);
        if (bool.booleanValue()) {
            if (this.f3082a.g.equals("com.miui.appcompatibility.LaunchDialog.launcher")) {
                appExcepitonTipsActivity = this.f3082a;
                e = appExcepitonTipsActivity.e;
            } else {
                if (this.f3082a.g.equals("com.miui.appcompatibility.LaunchDialog.appstore")) {
                    appExcepitonTipsActivity = this.f3082a;
                    e = appExcepitonTipsActivity.f;
                }
                TextView g = this.f3082a.f3063b;
                g.setText(this.f3082a.f3064c + this.f3082a.f3065d);
            }
            String unused = appExcepitonTipsActivity.f3065d = e;
            TextView g2 = this.f3082a.f3063b;
            g2.setText(this.f3082a.f3064c + this.f3082a.f3065d);
        }
    }
}
