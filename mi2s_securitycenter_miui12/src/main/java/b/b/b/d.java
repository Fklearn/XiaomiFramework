package b.b.b;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.antivirus.model.DangerousInfo;

class d extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1501a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ DangerousInfo f1502b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ g f1503c;

    d(g gVar, String str, DangerousInfo dangerousInfo) {
        this.f1503c = gVar;
        this.f1501a = str;
        this.f1502b = dangerousInfo;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        PackageInfo packageInfo;
        try {
            packageInfo = this.f1503c.f1547b.getPackageManager().getPackageInfo(this.f1501a, 64);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.i("DangerousService", "getPackageInfo NameNotFoundException pkg :" + this.f1501a);
            packageInfo = null;
        }
        if (packageInfo == null || !this.f1503c.a(this.f1502b, packageInfo)) {
            this.f1503c.a(this.f1501a);
            Log.d("DangerousService", "remove invalid package : " + this.f1501a);
        }
        return null;
    }
}
