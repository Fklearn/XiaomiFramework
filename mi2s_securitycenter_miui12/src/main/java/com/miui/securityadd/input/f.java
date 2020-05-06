package com.miui.securityadd.input;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7454a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f7455b;

    f(Context context, String str) {
        this.f7454a = context;
        this.f7455b = str;
    }

    public void run() {
        ClipData clipData;
        ClipboardManager clipboardManager = (ClipboardManager) this.f7454a.getSystemService("clipboard");
        if (InputProvider.f7446c >= 2) {
            clipData = ClipData.newPlainText("miui_input_no_need_show_pop", this.f7455b);
        } else {
            String str = this.f7455b;
            clipData = ClipData.newPlainText(str, str);
        }
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        } else {
            Log.e("InputUtils", "ClipboardManager is null");
        }
    }
}
