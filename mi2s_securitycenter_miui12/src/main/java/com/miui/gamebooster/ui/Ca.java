package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IFreeformWindow;

class Ca implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ QuickReplySettingsActivity f4864a;

    Ca(QuickReplySettingsActivity quickReplySettingsActivity) {
        this.f4864a = quickReplySettingsActivity;
    }

    public boolean a(IBinder iBinder) {
        IFreeformWindow unused = this.f4864a.i = IFreeformWindow.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("IFreeformWindow :");
        sb.append(this.f4864a.i == null);
        Log.i("QuickReplySettings", sb.toString());
        if (this.f4864a.i != null && !this.f4864a.l && this.f4864a.j && !this.f4864a.g.isEmpty()) {
            try {
                this.f4864a.i.d(this.f4864a.g);
            } catch (Exception e) {
                Log.e("QuickReplySettings", "setQuickReplyApps error", e);
            }
        }
        return false;
    }
}
