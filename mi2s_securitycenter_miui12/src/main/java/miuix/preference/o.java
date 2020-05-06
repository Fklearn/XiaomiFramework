package miuix.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.preference.C0156j;
import miui.app.AlertDialog;

public class o extends C0156j {
    private q m = new q(this.n, this);
    private k n = new n(this);

    public static o a(String str) {
        o oVar = new o();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        oVar.setArguments(bundle);
        return oVar;
    }

    /* access modifiers changed from: protected */
    public void a(AlertDialog.Builder builder) {
        super.a((AlertDialog.Builder) new C0578a(getActivity(), builder));
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return this.m.a(bundle);
    }
}
