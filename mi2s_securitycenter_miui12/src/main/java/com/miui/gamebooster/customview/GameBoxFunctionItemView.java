package com.miui.gamebooster.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.c.a.b.d;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.model.j;
import com.miui.gamebooster.p.r;
import com.miui.securitycenter.R;

public class GameBoxFunctionItemView extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ImageView f4126a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f4127b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f4128c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f4129d;
    /* access modifiers changed from: private */
    public j e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public r h;
    /* access modifiers changed from: private */
    public Context i;
    public int j;
    private String k;
    private int l;
    /* access modifiers changed from: private */
    public int m = 0;
    private d n;

    public GameBoxFunctionItemView(Context context) {
        super(context);
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(true);
        aVar.b((int) R.drawable.gamebox_game_button);
        aVar.c((int) R.drawable.gamebox_game_button);
        aVar.a(Bitmap.Config.RGB_565);
        aVar.c(true);
        this.n = aVar.a();
        this.i = context;
        b(this.i);
    }

    public GameBoxFunctionItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(true);
        aVar.b((int) R.drawable.gamebox_game_button);
        aVar.c((int) R.drawable.gamebox_game_button);
        aVar.a(Bitmap.Config.RGB_565);
        aVar.c(true);
        this.n = aVar.a();
        this.i = context;
        b(this.i);
    }

    public GameBoxFunctionItemView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(true);
        aVar.b((int) R.drawable.gamebox_game_button);
        aVar.c((int) R.drawable.gamebox_game_button);
        aVar.a(Bitmap.Config.RGB_565);
        aVar.c(true);
        this.n = aVar.a();
        this.i = context;
        b(this.i);
    }

    private int a(Context context) {
        int a2 = D.a(context, this.k, this.l);
        if (a2 != -1) {
            this.m = a2;
        }
        return this.m;
    }

    private void b(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.gamebox_function_item, this, true);
        this.f4126a = (ImageView) findViewById(R.id.item_image);
        this.f4127b = (TextView) findViewById(R.id.label);
        this.f4128c = (ImageView) findViewById(R.id.stick_point);
        this.f4129d = (ImageView) findViewById(R.id.red_point);
        inflate.setOnClickListener(new C0341j(this, context, inflate));
    }

    /* access modifiers changed from: private */
    public void c(Context context) {
        C0391w.a(context.getApplicationContext(), this.k, this.l, "settings_hdr", this.m);
    }

    static /* synthetic */ int f(GameBoxFunctionItemView gameBoxFunctionItemView) {
        int i2 = gameBoxFunctionItemView.m + 1;
        gameBoxFunctionItemView.m = i2;
        return i2;
    }

    private String getPkgName() {
        r rVar = this.h;
        return rVar != null ? rVar.e().c() : "";
    }

    /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0246, code lost:
        r7 = (android.widget.RelativeLayout.LayoutParams) r6.f4127b.getLayoutParams();
        r7.width = getResources().getDimensionPixelOffset(com.miui.securitycenter.R.dimen.view_dimen_160);
        r6.f4127b.setLayoutParams(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x027f, code lost:
        r7.setImageResource(r8);
        r7 = r6.f4127b;
        r8 = getResources().getColor(com.miui.securitycenter.R.color.gamebox_func_text_light);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0292, code lost:
        r7.setImageResource(r8);
        r7 = r6.f4127b;
        r8 = getResources().getColor(com.miui.securitycenter.R.color.gamebox_func_text);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x029f, code lost:
        r7.setTextColor(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.gamebooster.model.j r7, boolean r8) {
        /*
            r6 = this;
            r6.e = r7
            int[] r0 = com.miui.gamebooster.customview.C0343l.f4213a
            com.miui.gamebooster.d.c r1 = r7.a()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            r1 = 2
            r2 = 1
            if (r0 == r2) goto L_0x009d
            if (r0 == r1) goto L_0x0016
            goto L_0x02a2
        L_0x0016:
            android.content.pm.ResolveInfo r7 = r7.d()
            android.content.pm.ActivityInfo r8 = r7.activityInfo
            android.content.pm.ApplicationInfo r8 = r8.applicationInfo
            java.lang.String r8 = r8.packageName
            android.widget.ImageView r0 = r6.f4126a
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.RelativeLayout$LayoutParams r0 = (android.widget.RelativeLayout.LayoutParams) r0
            android.content.res.Resources r1 = r6.getResources()
            r2 = 2131167438(0x7f0708ce, float:1.794915E38)
            int r1 = r1.getDimensionPixelOffset(r2)
            r0.width = r1
            android.content.res.Resources r1 = r6.getResources()
            int r1 = r1.getDimensionPixelOffset(r2)
            r0.height = r1
            android.widget.ImageView r1 = r6.f4126a
            r1.setLayoutParams(r0)
            android.content.pm.ActivityInfo r7 = r7.activityInfo
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo
            int r7 = r7.uid
            int r7 = b.b.c.j.B.c(r7)
            r0 = 999(0x3e7, float:1.4E-42)
            r1 = 2131231439(0x7f0802cf, float:1.807896E38)
            if (r7 != r0) goto L_0x0058
            java.lang.String r7 = "pkg_icon_xspace://"
            goto L_0x005a
        L_0x0058:
            java.lang.String r7 = "pkg_icon://"
        L_0x005a:
            java.lang.String r7 = r7.concat(r8)
            android.widget.ImageView r0 = r6.f4126a
            b.c.a.b.d r2 = b.b.c.j.r.f
            android.content.Context r3 = r6.i
            android.content.res.Resources r3 = r3.getResources()
            android.graphics.drawable.Drawable r1 = r3.getDrawable(r1)
            b.b.c.j.r.a((java.lang.String) r7, (android.widget.ImageView) r0, (b.c.a.b.d) r2, (android.graphics.drawable.Drawable) r1)
            boolean r7 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r7 != 0) goto L_0x0082
            android.content.Context r7 = r6.i
            java.lang.CharSequence r7 = b.b.c.j.x.j(r7, r8)
            java.lang.String r7 = r7.toString()
            r6.setTextView((java.lang.String) r7)
            goto L_0x02a2
        L_0x0082:
            com.miui.gamebooster.p.r r7 = r6.h
            if (r7 == 0) goto L_0x02a2
            com.miui.gamebooster.customview.GameBoxView r7 = r7.d()
            if (r7 == 0) goto L_0x02a2
            android.widget.TextView r8 = r6.f4127b
            boolean r7 = r7.b()
            if (r7 == 0) goto L_0x0097
            r7 = 8
            goto L_0x0098
        L_0x0097:
            r7 = 4
        L_0x0098:
            r8.setVisibility(r7)
            goto L_0x02a2
        L_0x009d:
            android.widget.ImageView r0 = r6.f4126a
            com.miui.gamebooster.model.g r7 = r7.b()
            int r7 = r7.b()
            r0.setImageResource(r7)
            int[] r7 = com.miui.gamebooster.customview.C0343l.f4214b
            com.miui.gamebooster.model.j r0 = r6.e
            com.miui.gamebooster.model.g r0 = r0.b()
            com.miui.gamebooster.d.d r0 = r0.c()
            int r0 = r0.ordinal()
            r7 = r7[r0]
            r0 = 2131167331(0x7f070863, float:1.7948933E38)
            r3 = 2131099985(0x7f060151, float:1.7812339E38)
            r4 = 0
            r5 = 2131099984(0x7f060150, float:1.7812337E38)
            switch(r7) {
                case 1: goto L_0x0272;
                case 2: goto L_0x025e;
                case 3: goto L_0x01fd;
                case 4: goto L_0x01bf;
                case 5: goto L_0x0158;
                case 6: goto L_0x0125;
                case 7: goto L_0x010b;
                case 8: goto L_0x00cb;
                default: goto L_0x00c9;
            }
        L_0x00c9:
            goto L_0x02a2
        L_0x00cb:
            java.lang.String r7 = r6.getPkgName()
            b.b.l.b r8 = b.b.l.b.b()
            boolean r8 = r8.a((java.lang.String) r7)
            if (r8 == 0) goto L_0x02a2
            b.b.l.b r8 = b.b.l.b.b()
            java.lang.String r8 = r8.f(r7)
            boolean r0 = android.text.TextUtils.isEmpty(r8)
            if (r0 != 0) goto L_0x00ef
            android.widget.ImageView r0 = r6.f4126a
            b.c.a.b.d r1 = r6.n
            b.b.c.j.r.a((java.lang.String) r8, (android.widget.ImageView) r0, (b.c.a.b.d) r1)
            goto L_0x00f7
        L_0x00ef:
            android.widget.ImageView r8 = r6.f4126a
            r0 = 2131231307(0x7f08024b, float:1.8078691E38)
            r8.setImageResource(r0)
        L_0x00f7:
            java.lang.String r8 = "active_info"
            com.miui.gamebooster.m.C0373d.j((java.lang.String) r8)
            com.miui.gamebooster.m.s r8 = com.miui.gamebooster.m.C0387s.b()
            java.lang.String r0 = "view"
            com.miui.gamebooster.model.ActiveTrackModel r7 = com.miui.gamebooster.m.C0387s.a((java.lang.String) r7, (java.lang.String) r0)
            r8.a((com.miui.gamebooster.model.ActiveTrackModel) r7)
            goto L_0x02a2
        L_0x010b:
            java.lang.String r7 = com.miui.gamebooster.m.ma.d()
            java.lang.String r8 = "original"
            boolean r7 = r8.equals(r7)
            if (r7 == 0) goto L_0x011e
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231380(0x7f080294, float:1.807884E38)
            goto L_0x0292
        L_0x011e:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231384(0x7f080298, float:1.8078847E38)
            goto L_0x027f
        L_0x0125:
            android.content.Context r7 = r6.i
            android.content.Context r7 = r7.getApplicationContext()
            com.miui.gamebooster.m.N r7 = com.miui.gamebooster.m.N.a((android.content.Context) r7)
            boolean r8 = r7.d()
            com.miui.gamebooster.customview.k r0 = new com.miui.gamebooster.customview.k
            r0.<init>(r6)
            r7.a((com.miui.gamebooster.d) r0)
            if (r8 == 0) goto L_0x0144
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231326(0x7f08025e, float:1.807873E38)
            goto L_0x0292
        L_0x0144:
            boolean r7 = r7.b()
            if (r7 == 0) goto L_0x0151
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231328(0x7f080260, float:1.8078734E38)
            goto L_0x027f
        L_0x0151:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231325(0x7f08025d, float:1.8078728E38)
            goto L_0x0292
        L_0x0158:
            r7 = 0
            java.lang.String r3 = "key_currentbooster_pkg_uid"
            java.lang.String r7 = com.miui.common.persistence.b.a((java.lang.String) r3, (java.lang.String) r7)
            java.lang.String r3 = ","
            java.lang.String[] r7 = r7.split(r3)
            r3 = r7[r4]
            r6.k = r3
            r7 = r7[r2]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            int r7 = r7.intValue()
            r6.l = r7
            android.content.Context r7 = r6.i
            int r7 = r6.a((android.content.Context) r7)
            if (r7 == 0) goto L_0x01a9
            if (r7 == r2) goto L_0x019d
            if (r7 == r1) goto L_0x0191
            r1 = 3
            if (r7 == r1) goto L_0x0185
            goto L_0x01b4
        L_0x0185:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231334(0x7f080266, float:1.8078746E38)
            r7.setImageResource(r1)
            r4 = 2131756354(0x7f100542, float:1.9143613E38)
            goto L_0x01b4
        L_0x0191:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231331(0x7f080263, float:1.807874E38)
            r7.setImageResource(r1)
            r4 = 2131756353(0x7f100541, float:1.9143611E38)
            goto L_0x01b4
        L_0x019d:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231399(0x7f0802a7, float:1.8078878E38)
            r7.setImageResource(r1)
            r4 = 2131756352(0x7f100540, float:1.914361E38)
            goto L_0x01b4
        L_0x01a9:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231402(0x7f0802aa, float:1.8078884E38)
            r7.setImageResource(r1)
            r4 = 2131756351(0x7f10053f, float:1.9143607E38)
        L_0x01b4:
            boolean r7 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r7 != 0) goto L_0x01bb
            r6.setTextView((int) r4)
        L_0x01bb:
            if (r8 != 0) goto L_0x02a2
            goto L_0x0246
        L_0x01bf:
            miui.telephony.SubscriptionManager r7 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r7 = r7.getSubscriptionInfoForSlot(r4)
            miui.telephony.SubscriptionManager r8 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r8 = r8.getSubscriptionInfoForSlot(r2)
            if (r7 == 0) goto L_0x01d7
            boolean r7 = r7.isActivated()
            if (r7 != 0) goto L_0x01df
        L_0x01d7:
            if (r8 == 0) goto L_0x01f3
            boolean r7 = r8.isActivated()
            if (r7 == 0) goto L_0x01f3
        L_0x01df:
            boolean r7 = com.miui.gamebooster.c.a.f(r4)
            if (r7 == 0) goto L_0x01ec
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231314(0x7f080252, float:1.8078706E38)
            goto L_0x027f
        L_0x01ec:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231312(0x7f080250, float:1.8078701E38)
            goto L_0x0292
        L_0x01f3:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231316(0x7f080254, float:1.807871E38)
            r7.setImageResource(r8)
            goto L_0x02a2
        L_0x01fd:
            miui.telephony.SubscriptionManager r7 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r7 = r7.getSubscriptionInfoForSlot(r4)
            miui.telephony.SubscriptionManager r1 = miui.telephony.SubscriptionManager.getDefault()
            miui.telephony.SubscriptionInfo r1 = r1.getSubscriptionInfoForSlot(r2)
            boolean r3 = com.miui.gamebooster.m.C0384o.a()
            if (r3 == 0) goto L_0x023c
            if (r7 == 0) goto L_0x023c
            if (r1 == 0) goto L_0x023c
            boolean r7 = r7.isActivated()
            if (r7 == 0) goto L_0x023c
            boolean r7 = r1.isActivated()
            if (r7 == 0) goto L_0x023c
            miui.telephony.SubscriptionManager r7 = miui.telephony.SubscriptionManager.getDefault()
            int r7 = r7.getDefaultDataSlotId()
            if (r7 == 0) goto L_0x0236
            if (r7 == r2) goto L_0x0230
            goto L_0x0244
        L_0x0230:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231373(0x7f08028d, float:1.8078825E38)
            goto L_0x0241
        L_0x0236:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231370(0x7f08028a, float:1.807882E38)
            goto L_0x0241
        L_0x023c:
            android.widget.ImageView r7 = r6.f4126a
            r1 = 2131231369(0x7f080289, float:1.8078817E38)
        L_0x0241:
            r7.setImageResource(r1)
        L_0x0244:
            if (r8 != 0) goto L_0x02a2
        L_0x0246:
            android.widget.TextView r7 = r6.f4127b
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            android.widget.RelativeLayout$LayoutParams r7 = (android.widget.RelativeLayout.LayoutParams) r7
            android.content.res.Resources r8 = r6.getResources()
            int r8 = r8.getDimensionPixelOffset(r0)
            r7.width = r8
            android.widget.TextView r8 = r6.f4127b
            r8.setLayoutParams(r7)
            goto L_0x02a2
        L_0x025e:
            android.content.Context r7 = r6.i
            boolean r7 = com.miui.gamebooster.m.C0393y.a((android.content.Context) r7)
            if (r7 == 0) goto L_0x026c
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231395(0x7f0802a3, float:1.807887E38)
            goto L_0x027f
        L_0x026c:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231393(0x7f0802a1, float:1.8078866E38)
            goto L_0x0292
        L_0x0272:
            android.content.Context r7 = r6.i
            boolean r7 = com.miui.gamebooster.m.D.a(r7)
            if (r7 == 0) goto L_0x028d
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231295(0x7f08023f, float:1.8078667E38)
        L_0x027f:
            r7.setImageResource(r8)
            android.widget.TextView r7 = r6.f4127b
            android.content.res.Resources r8 = r6.getResources()
            int r8 = r8.getColor(r3)
            goto L_0x029f
        L_0x028d:
            android.widget.ImageView r7 = r6.f4126a
            r8 = 2131231292(0x7f08023c, float:1.807866E38)
        L_0x0292:
            r7.setImageResource(r8)
            android.widget.TextView r7 = r6.f4127b
            android.content.res.Resources r8 = r6.getResources()
            int r8 = r8.getColor(r5)
        L_0x029f:
            r7.setTextColor(r8)
        L_0x02a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.GameBoxFunctionItemView.a(com.miui.gamebooster.model.j, boolean):void");
    }

    public void a(r rVar) {
        this.h = rVar;
    }

    public void a(boolean z, boolean z2) {
        this.f4126a.setImageResource(z ? R.drawable.gamebox_arrow_expand_v : z2 ? R.drawable.gamebox_arrow_shrink_v_disable : R.drawable.gamebox_arrow_shrink_v);
    }

    public ImageView getRedPointView() {
        return this.f4129d;
    }

    public ImageView getStickPointView() {
        return this.f4128c;
    }

    public ImageView getmImageView() {
        return this.f4126a;
    }

    public TextView getmTextView() {
        return this.f4127b;
    }

    public void setTextView(int i2) {
        this.f4127b.setVisibility(0);
        this.f4127b.setText(getResources().getString(i2));
    }

    public void setTextView(String str) {
        this.f4127b.setVisibility(0);
        this.f4127b.setText(str);
    }

    public void setmLeftExpand(boolean z) {
        this.f = z;
    }

    public void setmRightExpand(boolean z) {
        this.g = z;
    }
}
