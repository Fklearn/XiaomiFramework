package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0375f;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0389u;

public class GameBoosterMainActivity extends a {
    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.gamebooster.ui.GameBoosterMainActivity, android.content.Context, miui.app.Activity] */
    private void a(Boolean bool, boolean z) {
        Intent intent = new Intent(this, GameBoosterRealMainActivity.class);
        if (z) {
            intent.putExtra("track_channel", "channel_luncher");
        }
        intent.putExtra("top", bool.booleanValue());
        String stringExtra = getIntent().getStringExtra("track_gamebooster_enter_way");
        if (stringExtra != null) {
            intent.putExtra("track_gamebooster_enter_way", stringExtra);
        }
        startActivity(intent);
        finish();
    }

    private void l() {
        Intent intent = new Intent("miui.gamebooster.action.GAMEBOX");
        intent.addFlags(268435456);
        startActivity(intent);
        finish();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, com.miui.gamebooster.ui.GameBoosterMainActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (C0375f.a()) {
            C0375f.a(this);
        } else if (C0389u.a(this)) {
            C0389u.b(this);
        } else {
            boolean z = (getIntent().getFlags() & 268435456) != 0;
            if (z) {
                C0373d.a("game_icon_click");
            }
            com.miui.gamebooster.c.a.a((Context) this);
            if (com.miui.gamebooster.c.a.b() != 0 || !C0388t.s()) {
                a(false, z);
                return;
            } else {
                l();
                return;
            }
        }
        finish();
    }
}
