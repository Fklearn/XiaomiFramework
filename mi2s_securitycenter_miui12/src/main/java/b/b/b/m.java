package b.b.b;

import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.net.Uri;

class m extends IPackageDeleteObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f1558a;

    m(o oVar) {
        this.f1558a = oVar;
    }

    public void packageDeleted(String str, int i) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("mimarket://details?id=" + str + "&startDownload=true&ref=antispam&back=true"));
        intent.setFlags(268435456);
        intent.addCategory("android.intent.category.BROWSABLE");
        this.f1558a.f.startActivity(intent);
    }
}
