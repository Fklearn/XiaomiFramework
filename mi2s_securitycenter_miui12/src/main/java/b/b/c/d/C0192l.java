package b.b.c.d;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

/* renamed from: b.b.c.d.l  reason: case insensitive filesystem */
class C0192l implements AccountManagerCallback<Bundle> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0193m f1688a;

    C0192l(C0193m mVar) {
        this.f1688a = mVar;
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        if (!accountManagerFuture.isCancelled()) {
            boolean unused = this.f1688a.s = true;
            C0193m mVar = this.f1688a;
            mVar.f1674b.a((C0185e) mVar);
        }
    }
}
