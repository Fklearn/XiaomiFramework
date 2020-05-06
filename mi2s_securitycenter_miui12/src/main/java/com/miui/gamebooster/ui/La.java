package com.miui.gamebooster.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.miui.gamebooster.model.k;
import java.util.List;

class La implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4923a;

    La(SelectGameActivity selectGameActivity) {
        this.f4923a = selectGameActivity;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f4923a.n()) {
            String trim = editable.toString().trim();
            if (this.f4923a.f4984b != null) {
                if (TextUtils.isEmpty(trim)) {
                    SelectGameActivity selectGameActivity = this.f4923a;
                    selectGameActivity.a((List<k>) selectGameActivity.f4984b);
                    this.f4923a.o();
                    return;
                }
                this.f4923a.b(trim);
            }
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
