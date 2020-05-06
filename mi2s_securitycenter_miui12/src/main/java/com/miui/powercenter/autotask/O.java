package com.miui.powercenter.autotask;

import android.content.Context;
import androidx.preference.Preference;

class O implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ T f6721a;

    O(T t) {
        this.f6721a = t;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (!"brightness".equals(preference.getKey())) {
            return false;
        }
        T t = this.f6721a;
        t.a((Context) ((U) t.f6672c).getActivity());
        return false;
    }
}
