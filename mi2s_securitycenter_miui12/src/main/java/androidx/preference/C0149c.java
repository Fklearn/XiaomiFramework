package androidx.preference;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;

@Deprecated
/* renamed from: androidx.preference.c  reason: case insensitive filesystem */
public class C0149c extends n {
    private EditText i;
    private CharSequence j;

    private EditTextPreference b() {
        return (EditTextPreference) a();
    }

    /* access modifiers changed from: protected */
    public void a(View view) {
        super.a(view);
        this.i = (EditText) view.findViewById(16908291);
        this.i.requestFocus();
        EditText editText = this.i;
        if (editText != null) {
            editText.setText(this.j);
            EditText editText2 = this.i;
            editText2.setSelection(editText2.getText().length());
            return;
        }
        throw new IllegalStateException("Dialog view must contain an EditText with id @android:id/edit");
    }

    @Deprecated
    public void a(boolean z) {
        if (z) {
            String obj = this.i.getText().toString();
            if (b().callChangeListener(obj)) {
                b().a(obj);
            }
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.j = bundle == null ? b().g() : bundle.getCharSequence("EditTextPreferenceDialogFragment.text");
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("EditTextPreferenceDialogFragment.text", this.j);
    }
}
