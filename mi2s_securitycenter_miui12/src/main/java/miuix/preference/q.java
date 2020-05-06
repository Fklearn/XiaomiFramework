package miuix.preference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.preference.DialogPreference;
import androidx.preference.n;
import miui.app.AlertDialog;

class q {

    /* renamed from: a  reason: collision with root package name */
    private k f8904a;

    /* renamed from: b  reason: collision with root package name */
    private n f8905b;

    public q(k kVar, n nVar) {
        this.f8904a = kVar;
        this.f8905b = nVar;
    }

    public Dialog a(Bundle bundle) {
        Activity activity = this.f8905b.getActivity();
        DialogPreference a2 = this.f8905b.a();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        C0578a aVar = new C0578a(activity, builder);
        aVar.setTitle(a2.d());
        aVar.setIcon(a2.a());
        aVar.setPositiveButton(a2.f(), (DialogInterface.OnClickListener) this.f8905b);
        aVar.setNegativeButton(a2.e(), (DialogInterface.OnClickListener) this.f8905b);
        View a3 = this.f8904a.a((Context) activity);
        if (a3 != null) {
            this.f8904a.a(a3);
            aVar.setView(a3);
        } else {
            aVar.setMessage(a2.c());
        }
        this.f8904a.a(builder);
        AlertDialog create = builder.create();
        if (this.f8904a.a()) {
            create.getWindow().setSoftInputMode(5);
        }
        return create;
    }
}
