package com.miui.appmanager;

import android.os.UserHandle;
import com.miui.networkassistant.config.Constants;

class p implements h {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3684a;

    p(AppManagerMainActivity appManagerMainActivity) {
        this.f3684a = appManagerMainActivity;
    }

    public void a(String str, UserHandle userHandle) {
        this.f3684a.a(str, userHandle, Constants.System.ACTION_PACKAGE_REMOVED);
    }
}
