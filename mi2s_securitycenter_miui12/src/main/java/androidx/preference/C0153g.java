package androidx.preference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;

@Deprecated
/* renamed from: androidx.preference.g  reason: case insensitive filesystem */
public class C0153g extends n {
    int i;
    private CharSequence[] j;
    private CharSequence[] k;

    private ListPreference b() {
        return (ListPreference) a();
    }

    /* access modifiers changed from: protected */
    public void a(AlertDialog.Builder builder) {
        super.a(builder);
        builder.setSingleChoiceItems(this.j, this.i, new C0152f(this));
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
    }

    @Deprecated
    public void a(boolean z) {
        int i2;
        ListPreference b2 = b();
        if (z && (i2 = this.i) >= 0) {
            String charSequence = this.k[i2].toString();
            if (b2.callChangeListener(charSequence)) {
                b2.b(charSequence);
            }
        }
    }

    public void onCreate(Bundle bundle) {
        CharSequence[] charSequenceArr;
        super.onCreate(bundle);
        if (bundle == null) {
            ListPreference b2 = b();
            if (b2.g() == null || b2.i() == null) {
                throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
            }
            this.i = b2.a(b2.j());
            this.j = b2.g();
            charSequenceArr = b2.i();
        } else {
            this.i = bundle.getInt("ListPreferenceDialogFragment.index", 0);
            this.j = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entries");
            charSequenceArr = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entryValues");
        }
        this.k = charSequenceArr;
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("ListPreferenceDialogFragment.index", this.i);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entries", this.j);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entryValues", this.k);
    }
}
