package androidx.preference;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Deprecated
/* renamed from: androidx.preference.j  reason: case insensitive filesystem */
public class C0156j extends n {
    Set<String> i = new HashSet();
    boolean j;
    CharSequence[] k;
    CharSequence[] l;

    private MultiSelectListPreference b() {
        return (MultiSelectListPreference) a();
    }

    /* access modifiers changed from: protected */
    public void a(AlertDialog.Builder builder) {
        super.a(builder);
        int length = this.l.length;
        boolean[] zArr = new boolean[length];
        for (int i2 = 0; i2 < length; i2++) {
            zArr[i2] = this.i.contains(this.l[i2].toString());
        }
        builder.setMultiChoiceItems(this.k, zArr, new C0155i(this));
    }

    @Deprecated
    public void a(boolean z) {
        MultiSelectListPreference b2 = b();
        if (z && this.j) {
            Set<String> set = this.i;
            if (b2.callChangeListener(set)) {
                b2.a(set);
            }
        }
        this.j = false;
    }

    public void onCreate(Bundle bundle) {
        CharSequence[] charSequenceArr;
        super.onCreate(bundle);
        if (bundle == null) {
            MultiSelectListPreference b2 = b();
            if (b2.g() == null || b2.h() == null) {
                throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
            }
            this.i.clear();
            this.i.addAll(b2.i());
            this.j = false;
            this.k = b2.g();
            charSequenceArr = b2.h();
        } else {
            this.i.clear();
            this.i.addAll(bundle.getStringArrayList("MultiSelectListPreferenceDialogFragment.values"));
            this.j = bundle.getBoolean("MultiSelectListPreferenceDialogFragment.changed", false);
            this.k = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries");
            charSequenceArr = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues");
        }
        this.l = charSequenceArr;
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("MultiSelectListPreferenceDialogFragment.values", new ArrayList(this.i));
        bundle.putBoolean("MultiSelectListPreferenceDialogFragment.changed", this.j);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries", this.k);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues", this.l);
    }
}
