package com.miui.applicationlock;

import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import b.b.k.a;
import b.b.o.g.e;
import java.util.List;

class ab extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3255a;

    ab(bb bbVar) {
        this.f3255a = bbVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        for (UserHandle identifier : ((UserManager) this.f3255a.I.getSystemService("user")).getUserProfiles()) {
            int identifier2 = identifier.getIdentifier();
            try {
                for (String str : (List) e.a((Object) this.f3255a.z, "getAllPrivacyApps", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(identifier2))) {
                    if (this.f3255a.z.isPrivacyApp(str, identifier2)) {
                        this.f3255a.z.setPrivacyApp(str, identifier2, false);
                    }
                }
            } catch (Exception e) {
                Log.e("SettingLockActivity", "invoke getAllPrivacyApps error", e);
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
        this.f3255a.I.getContentResolver().notifyChange(a.f1828a, (ContentObserver) null);
    }
}
