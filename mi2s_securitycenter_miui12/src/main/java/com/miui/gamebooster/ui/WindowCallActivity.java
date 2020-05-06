package com.miui.gamebooster.ui;

import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.common.persistence.b;

public class WindowCallActivity extends a {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (b.a("gb_show_window", false)) {
            finish();
            return;
        }
        Intent intent = new Intent("miui.intent.action.gb_show_window");
        intent.putExtra("passby_intent", getIntent());
        sendBroadcast(intent);
        finish();
    }
}
