package com.miui.gamebooster.model;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.Toast;
import com.miui.gamebooster.a.F;
import com.miui.gamebooster.d.c;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.m.C0383n;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.p.r;
import com.miui.securitycenter.R;
import miui.os.Build;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private ResolveInfo f4568a;

    /* renamed from: b  reason: collision with root package name */
    private c f4569b;

    /* renamed from: c  reason: collision with root package name */
    private g f4570c;

    /* renamed from: d  reason: collision with root package name */
    private int f4571d;
    private boolean e;
    private int f;
    private String g;
    private int h;
    private int i;

    public j(c cVar, ResolveInfo resolveInfo, g gVar, int i2) {
        this.i = 0;
        this.f4569b = cVar;
        this.f4568a = resolveInfo;
        this.f4570c = gVar;
        this.f4571d = i2;
        this.f = 0;
    }

    public j(c cVar, ResolveInfo resolveInfo, g gVar, int i2, int i3) {
        this(cVar, resolveInfo, gVar, i2);
        this.f = i3;
    }

    private int a(Context context) {
        int a2 = D.a(context, this.g, this.h);
        if (a2 != -1) {
            this.i = a2;
        }
        return this.i;
    }

    private void b(Context context) {
        C0391w.a(context.getApplicationContext(), this.g, this.h, "settings_hdr", this.i);
    }

    public c a() {
        return this.f4569b;
    }

    public void a(int i2) {
        this.f = i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:102:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01c7, code lost:
        r8.setImageResource(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01e9, code lost:
        r9.setImageResource(r1);
        r8 = r8.f4014b;
        r9 = r10.getResources().getColor(com.miui.securitycenter.R.color.gamebox_func_text_light);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01fc, code lost:
        r9.setImageResource(r0);
        r8 = r8.f4014b;
        r9 = r10.getResources().getColor(com.miui.securitycenter.R.color.gamebox_func_text);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0209, code lost:
        r8.setTextColor(r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r8, android.view.View r9, android.content.Context r10) {
        /*
            r7 = this;
            java.lang.Object r8 = r9.getTag()
            if (r8 != 0) goto L_0x000e
            java.lang.String r8 = "GameBoxModel"
            java.lang.String r9 = "bindView: tag can not be null!!!"
            android.util.Log.e(r8, r9)
            return
        L_0x000e:
            int[] r8 = com.miui.gamebooster.model.i.f4567b
            com.miui.gamebooster.d.c r0 = r7.f4569b
            int r0 = r0.ordinal()
            r8 = r8[r0]
            r0 = 4
            r1 = 1
            r2 = 0
            if (r8 == r1) goto L_0x020d
            r3 = 2
            if (r8 == r3) goto L_0x0022
            goto L_0x027c
        L_0x0022:
            java.lang.Object r8 = r9.getTag()
            boolean r8 = r8 instanceof com.miui.gamebooster.a.F.a
            if (r8 != 0) goto L_0x002c
            goto L_0x027c
        L_0x002c:
            java.lang.Object r8 = r9.getTag()
            com.miui.gamebooster.a.F$a r8 = (com.miui.gamebooster.a.F.a) r8
            android.widget.ImageView r9 = r8.f4013a
            if (r9 == 0) goto L_0x0041
            com.miui.gamebooster.model.g r4 = r7.b()
            int r4 = r4.b()
            r9.setImageResource(r4)
        L_0x0041:
            android.widget.TextView r9 = r8.f4014b
            r4 = 2131099984(0x7f060150, float:1.7812337E38)
            if (r9 == 0) goto L_0x007a
            boolean r5 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r5 != 0) goto L_0x006a
            com.miui.gamebooster.model.g r0 = r7.b()
            int r0 = r0.a()
            r9.setText(r0)
            android.widget.TextView r9 = r8.f4014b
            android.content.res.Resources r0 = r10.getResources()
            int r0 = r0.getColor(r4)
            r9.setTextColor(r0)
            android.widget.TextView r9 = r8.f4014b
            r9.setVisibility(r2)
            goto L_0x007a
        L_0x006a:
            r9.setVisibility(r0)
            android.widget.TextView r9 = r8.f4014b
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r9.height = r2
            android.widget.TextView r0 = r8.f4014b
            r0.setLayoutParams(r9)
        L_0x007a:
            int[] r9 = com.miui.gamebooster.model.i.f4566a
            com.miui.gamebooster.model.g r0 = r7.b()
            com.miui.gamebooster.d.d r0 = r0.c()
            int r0 = r0.ordinal()
            r9 = r9[r0]
            r0 = 2131099985(0x7f060151, float:1.7812339E38)
            switch(r9) {
                case 1: goto L_0x01de;
                case 2: goto L_0x01cc;
                case 3: goto L_0x0182;
                case 4: goto L_0x0148;
                case 5: goto L_0x00dd;
                case 6: goto L_0x00ac;
                case 7: goto L_0x0092;
                default: goto L_0x0090;
            }
        L_0x0090:
            goto L_0x027c
        L_0x0092:
            java.lang.String r9 = com.miui.gamebooster.m.ma.d()
            java.lang.String r1 = "original"
            boolean r9 = r1.equals(r9)
            if (r9 == 0) goto L_0x00a5
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231380(0x7f080294, float:1.807884E38)
            goto L_0x01fc
        L_0x00a5:
            android.widget.ImageView r9 = r8.f4013a
            r1 = 2131231384(0x7f080298, float:1.8078847E38)
            goto L_0x01e9
        L_0x00ac:
            android.content.Context r9 = r10.getApplicationContext()
            com.miui.gamebooster.m.N r9 = com.miui.gamebooster.m.N.a((android.content.Context) r9)
            boolean r1 = r9.d()
            com.miui.gamebooster.model.h r2 = new com.miui.gamebooster.model.h
            r2.<init>(r7, r8, r10)
            r9.a((com.miui.gamebooster.d) r2)
            if (r1 == 0) goto L_0x00c9
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231326(0x7f08025e, float:1.807873E38)
            goto L_0x01fc
        L_0x00c9:
            boolean r9 = r9.b()
            if (r9 == 0) goto L_0x00d6
            android.widget.ImageView r9 = r8.f4013a
            r1 = 2131231328(0x7f080260, float:1.8078734E38)
            goto L_0x01e9
        L_0x00d6:
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231325(0x7f08025d, float:1.8078728E38)
            goto L_0x01fc
        L_0x00dd:
            r9 = 0
            java.lang.String r0 = "key_currentbooster_pkg_uid"
            java.lang.String r9 = com.miui.common.persistence.b.a((java.lang.String) r0, (java.lang.String) r9)
            java.lang.String r0 = ","
            java.lang.String[] r9 = r9.split(r0)
            r0 = r9[r2]
            r7.g = r0
            r9 = r9[r1]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            int r9 = r9.intValue()
            r7.h = r9
            int r9 = r7.a((android.content.Context) r10)
            if (r9 == 0) goto L_0x012d
            if (r9 == r1) goto L_0x0121
            if (r9 == r3) goto L_0x0115
            r10 = 3
            if (r9 == r10) goto L_0x0109
            r9 = r2
            goto L_0x0138
        L_0x0109:
            android.widget.ImageView r9 = r8.f4013a
            r10 = 2131231334(0x7f080266, float:1.8078746E38)
            r9.setImageResource(r10)
            r9 = 2131756354(0x7f100542, float:1.9143613E38)
            goto L_0x0138
        L_0x0115:
            android.widget.ImageView r9 = r8.f4013a
            r10 = 2131231331(0x7f080263, float:1.807874E38)
            r9.setImageResource(r10)
            r9 = 2131756353(0x7f100541, float:1.9143611E38)
            goto L_0x0138
        L_0x0121:
            android.widget.ImageView r9 = r8.f4013a
            r10 = 2131231399(0x7f0802a7, float:1.8078878E38)
            r9.setImageResource(r10)
            r9 = 2131756352(0x7f100540, float:1.914361E38)
            goto L_0x0138
        L_0x012d:
            android.widget.ImageView r9 = r8.f4013a
            r10 = 2131231402(0x7f0802aa, float:1.8078884E38)
            r9.setImageResource(r10)
            r9 = 2131756351(0x7f10053f, float:1.9143607E38)
        L_0x0138:
            boolean r10 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r10 != 0) goto L_0x027c
            android.widget.TextView r10 = r8.f4014b
            r10.setVisibility(r2)
            android.widget.TextView r8 = r8.f4014b
            r8.setText(r9)
            goto L_0x027c
        L_0x0148:
            miui.telephony.SubscriptionManager r9 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r9 = r9.getSubscriptionInfoForSlot(r2)
            miui.telephony.SubscriptionManager r3 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r1 = r3.getSubscriptionInfoForSlot(r1)
            if (r9 == 0) goto L_0x0160
            boolean r9 = r9.isActivated()
            if (r9 != 0) goto L_0x0168
        L_0x0160:
            if (r1 == 0) goto L_0x017c
            boolean r9 = r1.isActivated()
            if (r9 == 0) goto L_0x017c
        L_0x0168:
            boolean r9 = com.miui.gamebooster.c.a.f(r2)
            if (r9 == 0) goto L_0x0175
            android.widget.ImageView r9 = r8.f4013a
            r1 = 2131231314(0x7f080252, float:1.8078706E38)
            goto L_0x01e9
        L_0x0175:
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231312(0x7f080250, float:1.8078701E38)
            goto L_0x01fc
        L_0x017c:
            android.widget.ImageView r8 = r8.f4013a
            r9 = 2131231316(0x7f080254, float:1.807871E38)
            goto L_0x01c7
        L_0x0182:
            miui.telephony.SubscriptionManager r9 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r9 = r9.getSubscriptionInfoForSlot(r2)
            miui.telephony.SubscriptionManager r10 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r10 = r10.getSubscriptionInfoForSlot(r1)
            boolean r0 = com.miui.gamebooster.m.C0384o.a()
            if (r0 == 0) goto L_0x01c2
            if (r9 == 0) goto L_0x01c2
            if (r10 == 0) goto L_0x01c2
            boolean r9 = r9.isActivated()
            if (r9 == 0) goto L_0x01c2
            boolean r9 = r10.isActivated()
            if (r9 == 0) goto L_0x01c2
            miui.telephony.SubscriptionManager r9 = miui.telephony.SubscriptionManager.getDefault()
            int r9 = r9.getDefaultDataSlotId()
            if (r9 == 0) goto L_0x01bc
            if (r9 == r1) goto L_0x01b6
            goto L_0x027c
        L_0x01b6:
            android.widget.ImageView r8 = r8.f4013a
            r9 = 2131231373(0x7f08028d, float:1.8078825E38)
            goto L_0x01c7
        L_0x01bc:
            android.widget.ImageView r8 = r8.f4013a
            r9 = 2131231370(0x7f08028a, float:1.807882E38)
            goto L_0x01c7
        L_0x01c2:
            android.widget.ImageView r8 = r8.f4013a
            r9 = 2131231369(0x7f080289, float:1.8078817E38)
        L_0x01c7:
            r8.setImageResource(r9)
            goto L_0x027c
        L_0x01cc:
            boolean r9 = com.miui.gamebooster.m.C0393y.a((android.content.Context) r10)
            if (r9 == 0) goto L_0x01d8
            android.widget.ImageView r9 = r8.f4013a
            r1 = 2131231395(0x7f0802a3, float:1.807887E38)
            goto L_0x01e9
        L_0x01d8:
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231393(0x7f0802a1, float:1.8078866E38)
            goto L_0x01fc
        L_0x01de:
            boolean r9 = com.miui.gamebooster.m.D.a(r10)
            if (r9 == 0) goto L_0x01f7
            android.widget.ImageView r9 = r8.f4013a
            r1 = 2131231295(0x7f08023f, float:1.8078667E38)
        L_0x01e9:
            r9.setImageResource(r1)
            android.widget.TextView r8 = r8.f4014b
            android.content.res.Resources r9 = r10.getResources()
            int r9 = r9.getColor(r0)
            goto L_0x0209
        L_0x01f7:
            android.widget.ImageView r9 = r8.f4013a
            r0 = 2131231292(0x7f08023c, float:1.807866E38)
        L_0x01fc:
            r9.setImageResource(r0)
            android.widget.TextView r8 = r8.f4014b
            android.content.res.Resources r9 = r10.getResources()
            int r9 = r9.getColor(r4)
        L_0x0209:
            r8.setTextColor(r9)
            goto L_0x027c
        L_0x020d:
            java.lang.Object r8 = r9.getTag()
            boolean r8 = r8 instanceof com.miui.gamebooster.a.E.a
            if (r8 != 0) goto L_0x0216
            goto L_0x027c
        L_0x0216:
            java.lang.Object r8 = r9.getTag()
            com.miui.gamebooster.a.E$a r8 = (com.miui.gamebooster.a.E.a) r8
            android.content.res.Resources r9 = r10.getResources()
            android.content.pm.ResolveInfo r1 = r7.f4568a
            android.content.pm.ActivityInfo r1 = r1.activityInfo
            android.content.pm.ApplicationInfo r1 = r1.applicationInfo
            java.lang.String r1 = r1.packageName
            android.widget.ImageView r3 = r8.f4009a
            if (r3 == 0) goto L_0x0253
            android.content.pm.ResolveInfo r3 = r7.f4568a
            android.content.pm.ActivityInfo r3 = r3.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            int r3 = b.b.c.j.B.c(r3)
            r4 = 999(0x3e7, float:1.4E-42)
            r5 = 2131231439(0x7f0802cf, float:1.807896E38)
            if (r3 != r4) goto L_0x0242
            java.lang.String r3 = "pkg_icon_xspace://"
            goto L_0x0244
        L_0x0242:
            java.lang.String r3 = "pkg_icon://"
        L_0x0244:
            java.lang.String r3 = r3.concat(r1)
            android.widget.ImageView r4 = r8.f4009a
            b.c.a.b.d r6 = b.b.c.j.r.f
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r5)
            b.b.c.j.r.a((java.lang.String) r3, (android.widget.ImageView) r4, (b.c.a.b.d) r6, (android.graphics.drawable.Drawable) r9)
        L_0x0253:
            android.widget.TextView r9 = r8.f4010b
            if (r9 == 0) goto L_0x027c
            boolean r3 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r3 != 0) goto L_0x026c
            r9.setVisibility(r2)
            android.widget.TextView r8 = r8.f4010b
            java.lang.CharSequence r9 = b.b.c.j.x.j(r10, r1)
            java.lang.String r9 = r9.toString()
            r8.setText(r9)
            goto L_0x027c
        L_0x026c:
            r9.setVisibility(r0)
            android.widget.TextView r9 = r8.f4010b
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r9.height = r2
            android.widget.TextView r8 = r8.f4010b
            r8.setLayoutParams(r9)
        L_0x027c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.model.j.a(int, android.view.View, android.content.Context):void");
    }

    public void a(r rVar, View view) {
        int i2;
        int i3;
        int i4 = i.f4567b[this.f4569b.ordinal()];
        if (i4 == 1) {
            C0383n.a(view.getContext(), this.f4568a.activityInfo.applicationInfo.packageName, this.f4568a.activityInfo.name, R.string.gamebox_app_not_find);
        } else if (i4 == 2) {
            F.a aVar = null;
            if (view.getTag() instanceof F.a) {
                aVar = (F.a) view.getTag();
            }
            if (aVar != null) {
                Context context = view.getContext();
                if (d.DISPLAY.equals(b().c())) {
                    int i5 = this.i;
                    if (i5 >= 3) {
                        i2 = 0;
                    } else {
                        i2 = i5 + 1;
                        this.i = i2;
                    }
                    this.i = i2;
                    int i6 = this.i;
                    if (i6 == 0) {
                        aVar.f4013a.setImageResource(R.drawable.gamebox_yuanse_button);
                        i3 = R.string.gamebox_display_1;
                    } else if (i6 == 1) {
                        aVar.f4013a.setImageResource(R.drawable.gamebox_xianyan_button);
                        i3 = R.string.gamebox_display_2;
                    } else if (i6 == 2) {
                        aVar.f4013a.setImageResource(R.drawable.gamebox_mingliang_button);
                        i3 = R.string.gamebox_display_3;
                    } else if (i6 != 3) {
                        i3 = 0;
                    } else {
                        aVar.f4013a.setImageResource(R.drawable.gamebox_mingyan_button);
                        i3 = R.string.gamebox_display_4;
                    }
                    if (!Build.IS_INTERNATIONAL_BUILD) {
                        aVar.f4014b.setVisibility(0);
                        aVar.f4014b.setText(i3);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.gamebox_display_set).concat(context.getResources().getString(i3)), 0).show();
                    }
                    b(context);
                }
                D.a(rVar, b(), context, view);
            }
        }
    }

    public void a(boolean z) {
        this.e = z;
    }

    public g b() {
        return this.f4570c;
    }

    public int c() {
        return this.f;
    }

    public ResolveInfo d() {
        return this.f4568a;
    }
}
