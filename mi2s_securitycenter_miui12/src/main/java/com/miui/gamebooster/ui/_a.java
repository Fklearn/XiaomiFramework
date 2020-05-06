package com.miui.gamebooster.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.miui.gamebooster.model.k;
import java.util.List;

class _a implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListFragment f5042a;

    _a(WhiteListFragment whiteListFragment) {
        this.f5042a = whiteListFragment;
    }

    public void afterTextChanged(Editable editable) {
        if (this.f5042a.isSearchMode()) {
            String trim = editable.toString().trim();
            if (this.f5042a.f5025b != null) {
                if (TextUtils.isEmpty(trim)) {
                    WhiteListFragment whiteListFragment = this.f5042a;
                    whiteListFragment.a((List<k>) whiteListFragment.f5025b, true);
                    this.f5042a.f();
                    return;
                }
                this.f5042a.updateSearchResult(trim);
            }
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
