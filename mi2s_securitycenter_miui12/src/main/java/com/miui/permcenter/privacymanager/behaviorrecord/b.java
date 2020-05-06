package com.miui.permcenter.privacymanager.behaviorrecord;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;

class b implements TextWatcher {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6439a;

    b(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6439a = appBehaviorRecordActivity;
    }

    public void afterTextChanged(Editable editable) {
        String trim = editable.toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            this.f6439a.c(trim);
            return;
        }
        this.f6439a.o();
        AppBehaviorRecordActivity appBehaviorRecordActivity = this.f6439a;
        new AppBehaviorRecordActivity.b(appBehaviorRecordActivity, appBehaviorRecordActivity.u).execute(new Void[0]);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
