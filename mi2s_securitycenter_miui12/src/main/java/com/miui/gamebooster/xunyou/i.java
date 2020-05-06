package com.miui.gamebooster.xunyou;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class i extends Dialog {

    /* renamed from: a  reason: collision with root package name */
    private m f5414a;

    public i(Context context, m mVar, int i) {
        super(context, i);
        this.f5414a = mVar;
    }

    private void a() {
        setContentView(this.f5414a);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        a();
    }
}
