package miuix.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.preference.C0149c;
import miui.app.AlertDialog;

public class i extends C0149c {
    private q k = new q(this.l, this);
    private k l = new h(this);

    public static i a(String str) {
        i iVar = new i();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        iVar.setArguments(bundle);
        return iVar;
    }

    /* access modifiers changed from: protected */
    public void a(AlertDialog.Builder builder) {
        super.a((AlertDialog.Builder) new C0578a(getActivity(), builder));
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return this.k.a(bundle);
    }
}
