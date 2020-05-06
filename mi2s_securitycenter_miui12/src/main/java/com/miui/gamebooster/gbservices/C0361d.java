package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.content.Intent;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.p.f;
import com.miui.gamebooster.ui.GameVideoActivity;

/* renamed from: com.miui.gamebooster.gbservices.d  reason: case insensitive filesystem */
class C0361d implements f.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4348a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Intent f4349b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0363f f4350c;

    C0361d(C0363f fVar, Context context, Intent intent) {
        this.f4350c = fVar;
        this.f4348a = context;
        this.f4349b = intent;
    }

    public void a() {
        C0373d.d();
        Intent intent = new Intent(this.f4348a, GameVideoActivity.class);
        intent.putExtra("match_md5", this.f4349b.getStringExtra("match_md5"));
        intent.addFlags(32768);
        C0393y.a(this.f4348a, intent, "00010", true);
    }
}
