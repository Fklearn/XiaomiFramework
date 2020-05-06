package com.miui.applicationlock;

import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import b.b.c.j.B;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;
import java.util.List;

class Sa extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3214a;

    Sa(bb bbVar) {
        this.f3214a = bbVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        List<ApplicationInfo> c2 = o.c();
        boolean isChecked = this.f3214a.o.isChecked();
        for (ApplicationInfo next : c2) {
            this.f3214a.z.setApplicationAccessControlEnabledForUser(next.packageName, isChecked, B.c(next.uid));
        }
        this.f3214a.l.f(this.f3214a.o.isChecked());
        return Boolean.valueOf(isChecked);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        if (!bool.booleanValue()) {
            Toast.makeText(this.f3214a.I, this.f3214a.getResources().getString(R.string.applock_settings_unlock_all_toast), 1).show();
        }
        Log.d("SettingLockActivity", "all apps is locked: " + bool);
    }
}
