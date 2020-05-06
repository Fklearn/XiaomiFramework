package com.miui.gamebooster.p;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.ba;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.service.GameBoxWindowManagerService;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;

public class c implements CheckBoxSettingItemView.a {

    /* renamed from: a  reason: collision with root package name */
    private GameBoxWindowManagerService f4711a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f4712b;

    /* renamed from: c  reason: collision with root package name */
    private WindowManager.LayoutParams f4713c;

    /* renamed from: d  reason: collision with root package name */
    private WindowManager f4714d;
    private View e;
    private RelativeLayout f;
    private ImageButton g;
    private CheckBoxSettingItemView h;
    private CheckBoxSettingItemView i;
    private CheckBoxSettingItemView j;
    private CheckBoxSettingItemView k;
    private CheckBoxSettingItemView l;
    private CheckBoxSettingItemView m;
    private Button n;
    private boolean o;
    private View.OnClickListener p = new a(this);

    public c(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4711a = gameBoxWindowManagerService;
        this.f4712b = gameBoxWindowManagerService.getApplicationContext();
        this.f4713c = new WindowManager.LayoutParams();
        this.f4714d = (WindowManager) this.f4712b.getSystemService("window");
    }

    public void a() {
        try {
            Log.i("GameBoosterFirstWindowManager", "try createIcon");
            ba.a(this.f4712b, (Boolean) false);
            a.a(this.f4712b);
            a.I(true);
        } catch (Exception e2) {
            a.a(this.f4712b);
            a.I(false);
            Log.e("GameBoosterFirstWindowManager", e2.toString());
        }
    }

    public void a(boolean z) {
        boolean z2;
        CheckBoxSettingItemView checkBoxSettingItemView;
        boolean z3;
        if (!this.o) {
            Log.i("GameBoosterFirstWindowManager", "isHorizontal:" + z);
            WindowManager.LayoutParams layoutParams = this.f4713c;
            layoutParams.type = 2003;
            layoutParams.format = -3;
            layoutParams.flags = 264;
            layoutParams.gravity = 17;
            layoutParams.width = -1;
            layoutParams.height = -2;
            layoutParams.windowAnimations = R.style.game_first_window_anim;
            this.e = LayoutInflater.from(this.f4712b).inflate(z ? R.layout.gb_first_guide_horizontal : R.layout.gb_first_guide_vertical, (ViewGroup) null);
            this.f = (RelativeLayout) this.e.findViewById(R.id.window_bg);
            this.g = (ImageButton) this.e.findViewById(R.id.settings);
            this.n = (Button) this.e.findViewById(R.id.ok);
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.g.getLayoutParams();
            layoutParams2.addRule(na.c() ? 9 : 11);
            this.g.setLayoutParams(layoutParams2);
            this.h = (CheckBoxSettingItemView) this.e.findViewById(R.id.gamebox);
            this.i = (CheckBoxSettingItemView) this.e.findViewById(R.id.handsfree);
            this.j = (CheckBoxSettingItemView) this.e.findViewById(R.id.autobright);
            this.k = (CheckBoxSettingItemView) this.e.findViewById(R.id.threefinger);
            this.l = (CheckBoxSettingItemView) this.e.findViewById(R.id.forbid_pull_notification);
            this.m = (CheckBoxSettingItemView) this.e.findViewById(R.id.disable_eyeshield);
            this.h.setOnCheckedChangeListener(this);
            this.i.setOnCheckedChangeListener(this);
            this.j.setOnCheckedChangeListener(this);
            this.k.setOnCheckedChangeListener(this);
            this.l.setOnCheckedChangeListener(this);
            this.m.setOnCheckedChangeListener(this);
            a.a(this.f4712b);
            CheckBoxSettingItemView checkBoxSettingItemView2 = this.h;
            if (C0388t.o()) {
                z2 = a.w(true);
            } else {
                a.a(this.f4712b);
                z2 = a.a(true);
            }
            checkBoxSettingItemView2.a(z2, false, false);
            this.i.a(a.l(true), false, false);
            this.j.a(a.r(false), false, false);
            if (C0388t.e()) {
                this.l.setVisibility(8);
                this.k.a(a.u(false), false, false);
                checkBoxSettingItemView = this.m;
                z3 = a.s(false);
            } else {
                this.k.setVisibility(8);
                this.m.setVisibility(8);
                checkBoxSettingItemView = this.l;
                z3 = a.t(false);
            }
            checkBoxSettingItemView.a(z3, false, false);
            this.f4714d.addView(this.e, this.f4713c);
            C0373d.f("show", "time");
            this.o = true;
            this.n.setClickable(true);
            this.n.setOnClickListener(this.p);
            this.g.setOnClickListener(new b(this));
        }
    }

    public void b() {
        WindowManager windowManager = this.f4714d;
        if (windowManager != null && this.o) {
            this.o = false;
            try {
                windowManager.removeView(this.e);
            } catch (Exception e2) {
                Log.i("GameBoosterFirstWindowManager", e2.toString());
            }
        }
    }

    public void c() {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.START_GAMEMODE");
        this.f4712b.sendBroadcast(intent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002d, code lost:
        if (r3 != false) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0033, code lost:
        if (r3 != false) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
        if (r3 != false) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003e, code lost:
        r0 = r0 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0040, code lost:
        com.miui.gamebooster.c.a.b(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0015, code lost:
        if (r3 != false) goto L_0x003b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCheckedChanged(android.view.View r2, boolean r3) {
        /*
            r1 = this;
            android.content.Context r0 = r1.f4712b
            com.miui.gamebooster.c.a.a((android.content.Context) r0)
            r0 = 0
            int r0 = com.miui.gamebooster.c.a.a((int) r0)
            int r2 = r2.getId()
            switch(r2) {
                case 2131296486: goto L_0x0036;
                case 2131296722: goto L_0x0030;
                case 2131296835: goto L_0x002a;
                case 2131296867: goto L_0x001c;
                case 2131296936: goto L_0x0018;
                case 2131297829: goto L_0x0012;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0043
        L_0x0012:
            com.miui.gamebooster.c.a.aa(r3)
            if (r3 == 0) goto L_0x003e
            goto L_0x003b
        L_0x0018:
            com.miui.gamebooster.c.a.N(r3)
            goto L_0x0043
        L_0x001c:
            boolean r2 = com.miui.gamebooster.m.C0388t.o()
            if (r2 == 0) goto L_0x0026
            com.miui.gamebooster.c.a.ca(r3)
            goto L_0x0043
        L_0x0026:
            com.miui.gamebooster.c.a.M(r3)
            goto L_0x0043
        L_0x002a:
            com.miui.gamebooster.c.a.Z(r3)
            if (r3 == 0) goto L_0x003e
            goto L_0x003b
        L_0x0030:
            com.miui.gamebooster.c.a.Y(r3)
            if (r3 == 0) goto L_0x003e
            goto L_0x003b
        L_0x0036:
            com.miui.gamebooster.c.a.X(r3)
            if (r3 == 0) goto L_0x003e
        L_0x003b:
            int r0 = r0 + 1
            goto L_0x0040
        L_0x003e:
            int r0 = r0 + -1
        L_0x0040:
            com.miui.gamebooster.c.a.b((int) r0)
        L_0x0043:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.p.c.onCheckedChanged(android.view.View, boolean):void");
    }
}
