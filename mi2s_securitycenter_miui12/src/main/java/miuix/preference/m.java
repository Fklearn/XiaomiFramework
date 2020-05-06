package miuix.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.preference.C0153g;
import miui.app.AlertDialog;

public class m extends C0153g {
    private q l = new q(this.m, this);
    private k m = new l(this);

    public static m a(String str) {
        m mVar = new m();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        mVar.setArguments(bundle);
        return mVar;
    }

    /* access modifiers changed from: protected */
    public void a(AlertDialog.Builder builder) {
        super.a((AlertDialog.Builder) new C0578a(getActivity(), builder));
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return this.l.a(bundle);
    }
}
