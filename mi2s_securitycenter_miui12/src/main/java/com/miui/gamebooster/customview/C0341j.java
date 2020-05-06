package com.miui.gamebooster.customview;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.Toast;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.f.c;
import com.miui.gamebooster.m.C0383n;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.p.r;
import com.miui.securitycenter.R;
import miui.os.Build;

/* renamed from: com.miui.gamebooster.customview.j  reason: case insensitive filesystem */
class C0341j implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4209a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f4210b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoxFunctionItemView f4211c;

    C0341j(GameBoxFunctionItemView gameBoxFunctionItemView, Context context, View view) {
        this.f4211c = gameBoxFunctionItemView;
        this.f4209a = context;
        this.f4210b = view;
    }

    public void onClick(View view) {
        int i;
        if (this.f4211c.e != null) {
            int i2 = C0343l.f4213a[this.f4211c.e.a().ordinal()];
            if (i2 == 1) {
                d c2 = this.f4211c.e.b().c();
                if (d.LEFT_ARROW.equals(c2)) {
                    if (c.a().b()) {
                        Context context = this.f4209a;
                        Toast.makeText(context, context.getResources().getString(R.string.gamebox_recent_task_expand_fail), 0).show();
                        return;
                    }
                    GameBoxFunctionItemView gameBoxFunctionItemView = this.f4211c;
                    boolean unused = gameBoxFunctionItemView.f = !gameBoxFunctionItemView.f;
                    r d2 = this.f4211c.h;
                    GameBoxFunctionItemView gameBoxFunctionItemView2 = this.f4211c;
                    d2.b(gameBoxFunctionItemView2.j, gameBoxFunctionItemView2.f, this.f4211c.g);
                    GameBoxFunctionItemView gameBoxFunctionItemView3 = this.f4211c;
                    gameBoxFunctionItemView3.a(gameBoxFunctionItemView3.f, true);
                    GameBoxFunctionItemView gameBoxFunctionItemView4 = this.f4211c;
                    gameBoxFunctionItemView4.j = 0;
                    boolean unused2 = gameBoxFunctionItemView4.g = false;
                } else if (d.RIGHT_ARROW.equals(c2)) {
                    GameBoxFunctionItemView gameBoxFunctionItemView5 = this.f4211c;
                    boolean unused3 = gameBoxFunctionItemView5.g = !gameBoxFunctionItemView5.g;
                    r d3 = this.f4211c.h;
                    GameBoxFunctionItemView gameBoxFunctionItemView6 = this.f4211c;
                    d3.a(gameBoxFunctionItemView6.j, gameBoxFunctionItemView6.g, this.f4211c.f);
                    GameBoxFunctionItemView gameBoxFunctionItemView7 = this.f4211c;
                    gameBoxFunctionItemView7.a(gameBoxFunctionItemView7.g, false);
                    GameBoxFunctionItemView gameBoxFunctionItemView8 = this.f4211c;
                    gameBoxFunctionItemView8.j = 0;
                    boolean unused4 = gameBoxFunctionItemView8.f = false;
                } else {
                    if (d.DISPLAY.equals(c2)) {
                        GameBoxFunctionItemView gameBoxFunctionItemView9 = this.f4211c;
                        int unused5 = gameBoxFunctionItemView9.m = gameBoxFunctionItemView9.m >= 3 ? 0 : GameBoxFunctionItemView.f(this.f4211c);
                        int e = this.f4211c.m;
                        if (e == 0) {
                            this.f4211c.f4126a.setImageResource(R.drawable.gamebox_yuanse_button);
                            i = R.string.gamebox_display_1;
                        } else if (e == 1) {
                            this.f4211c.f4126a.setImageResource(R.drawable.gamebox_xianyan_button);
                            i = R.string.gamebox_display_2;
                        } else if (e == 2) {
                            this.f4211c.f4126a.setImageResource(R.drawable.gamebox_mingliang_button);
                            i = R.string.gamebox_display_3;
                        } else if (e != 3) {
                            i = 0;
                        } else {
                            this.f4211c.f4126a.setImageResource(R.drawable.gamebox_mingyan_button);
                            i = R.string.gamebox_display_4;
                        }
                        if (!Build.IS_INTERNATIONAL_BUILD) {
                            this.f4211c.setTextView(i);
                        } else {
                            Toast.makeText(this.f4209a, this.f4211c.getResources().getString(R.string.gamebox_display_set).concat(this.f4211c.getResources().getString(i)), 0).show();
                        }
                        this.f4211c.c(this.f4209a);
                    } else if (!d.DND.equals(c2) && !d.WIFI.equals(c2) && !d.SIMCARD.equals(c2) && !d.IMMERSION.equals(c2)) {
                        this.f4211c.h.i();
                    }
                    D.a(this.f4211c.h, this.f4211c.e.b(), this.f4211c.i, this.f4210b);
                }
            } else if (i2 == 2) {
                ResolveInfo d4 = this.f4211c.e.d();
                C0383n.a(this.f4211c.i, d4.activityInfo.applicationInfo.packageName, d4.activityInfo.name, R.string.gamebox_app_not_find);
                this.f4211c.h.i();
            }
        }
    }
}
