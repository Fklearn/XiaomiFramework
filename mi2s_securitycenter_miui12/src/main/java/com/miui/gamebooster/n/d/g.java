package com.miui.gamebooster.n.d;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.B;
import com.miui.gamebooster.d;
import com.miui.gamebooster.m.N;
import com.miui.gamebooster.n.c.a;
import com.miui.gamebooster.videobox.adapter.f;
import com.miui.gamebooster.videobox.utils.MiSoundEffectUtils;
import com.miui.gamebooster.videobox.utils.c;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;

public class g extends b {

    /* renamed from: c  reason: collision with root package name */
    private int f4696c;

    /* renamed from: d  reason: collision with root package name */
    private a f4697d;

    public g(int i, int i2, a aVar) {
        super(i);
        this.f4696c = i2;
        this.f4697d = aVar;
    }

    private void a(f.c cVar) {
        if (cVar != null) {
            N a2 = N.a((Context) Application.d());
            boolean d2 = a2.d();
            LinearLayout linearLayout = cVar.f5159c;
            if (linearLayout != null) {
                linearLayout.setEnabled(!d2);
            }
            TextView textView = cVar.f5160d;
            if (textView != null) {
                textView.setEnabled(!d2);
            }
            ImageView imageView = cVar.f5158b;
            if (imageView != null) {
                imageView.setEnabled(!d2);
                if (!d2) {
                    boolean b2 = a2.b();
                    cVar.f5158b.setSelected(b2);
                    TextView textView2 = cVar.f5160d;
                    if (textView2 != null) {
                        textView2.setSelected(b2);
                    }
                    a2.a((d) new e(this, cVar));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0042, code lost:
        if (com.miui.gamebooster.videobox.settings.f.b() != 0) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0046, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0058, code lost:
        if (com.miui.gamebooster.videobox.settings.f.c() == 0) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0098, code lost:
        if (com.miui.gamebooster.videobox.settings.f.b() != 0) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ae, code lost:
        if (com.miui.gamebooster.videobox.settings.f.c() == 0) goto L_0x009c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r6, android.view.View r7) {
        /*
            r5 = this;
            if (r7 != 0) goto L_0x0003
            return
        L_0x0003:
            boolean r6 = r5.b()
            int[] r0 = com.miui.gamebooster.n.d.f.f4695a
            com.miui.gamebooster.n.c.a r1 = r5.f4697d
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto L_0x0016;
                case 2: goto L_0x0016;
                case 3: goto L_0x0016;
                case 4: goto L_0x0016;
                case 5: goto L_0x0016;
                case 6: goto L_0x0016;
                case 7: goto L_0x0016;
                default: goto L_0x0014;
            }
        L_0x0014:
            goto L_0x00e2
        L_0x0016:
            java.lang.Object r0 = r7.getTag()
            boolean r0 = r0 instanceof com.miui.gamebooster.videobox.adapter.f.c
            if (r0 != 0) goto L_0x0020
            goto L_0x00e2
        L_0x0020:
            java.lang.Object r0 = r7.getTag()
            com.miui.gamebooster.videobox.adapter.f$c r0 = (com.miui.gamebooster.videobox.adapter.f.c) r0
            android.widget.ImageView r1 = r0.f5158b
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0068
            int r4 = r5.f4696c
            r1.setImageResource(r4)
            android.widget.ImageView r1 = r0.f5158b
            r1.setEnabled(r6)
            com.miui.gamebooster.n.c.a r1 = r5.f4697d
            com.miui.gamebooster.n.c.a r4 = com.miui.gamebooster.n.c.a.DISPLAY_STYLE
            if (r1 != r4) goto L_0x0048
            android.widget.ImageView r1 = r0.f5158b
            int r4 = com.miui.gamebooster.videobox.settings.f.b()
            if (r4 == 0) goto L_0x0046
        L_0x0044:
            r4 = r2
            goto L_0x0065
        L_0x0046:
            r4 = r3
            goto L_0x0065
        L_0x0048:
            com.miui.gamebooster.n.c.a r4 = com.miui.gamebooster.n.c.a.SRS_PREMIUM_SOUND
            if (r1 != r4) goto L_0x005b
            android.widget.ImageView r1 = r0.f5158b
            int r4 = com.miui.gamebooster.videobox.settings.f.d()
            if (r4 != 0) goto L_0x0044
            int r4 = com.miui.gamebooster.videobox.settings.f.c()
            if (r4 == 0) goto L_0x0046
            goto L_0x0044
        L_0x005b:
            com.miui.gamebooster.n.c.a r4 = com.miui.gamebooster.n.c.a.ADVANCED_SETTINGS
            if (r1 != r4) goto L_0x0068
            android.widget.ImageView r1 = r0.f5158b
            boolean r4 = com.miui.gamebooster.videobox.settings.f.l()
        L_0x0065:
            r1.setSelected(r4)
        L_0x0068:
            android.widget.TextView r1 = r0.f5160d
            if (r1 == 0) goto L_0x00be
            int r1 = r5.a()
            if (r1 <= 0) goto L_0x0087
            android.widget.TextView r1 = r0.f5160d
            android.content.Context r7 = r7.getContext()
            android.content.res.Resources r7 = r7.getResources()
            int r4 = r5.a()
            java.lang.String r7 = r7.getString(r4)
            r1.setText(r7)
        L_0x0087:
            android.widget.TextView r7 = r0.f5160d
            r7.setEnabled(r6)
            com.miui.gamebooster.n.c.a r7 = r5.f4697d
            com.miui.gamebooster.n.c.a r1 = com.miui.gamebooster.n.c.a.DISPLAY_STYLE
            if (r7 != r1) goto L_0x009e
            android.widget.TextView r7 = r0.f5160d
            int r1 = com.miui.gamebooster.videobox.settings.f.b()
            if (r1 == 0) goto L_0x009c
        L_0x009a:
            r1 = r2
            goto L_0x00bb
        L_0x009c:
            r1 = r3
            goto L_0x00bb
        L_0x009e:
            com.miui.gamebooster.n.c.a r1 = com.miui.gamebooster.n.c.a.SRS_PREMIUM_SOUND
            if (r7 != r1) goto L_0x00b1
            android.widget.TextView r7 = r0.f5160d
            int r1 = com.miui.gamebooster.videobox.settings.f.d()
            if (r1 != 0) goto L_0x009a
            int r1 = com.miui.gamebooster.videobox.settings.f.c()
            if (r1 == 0) goto L_0x009c
            goto L_0x009a
        L_0x00b1:
            com.miui.gamebooster.n.c.a r1 = com.miui.gamebooster.n.c.a.ADVANCED_SETTINGS
            if (r7 != r1) goto L_0x00be
            android.widget.TextView r7 = r0.f5160d
            boolean r1 = com.miui.gamebooster.videobox.settings.f.l()
        L_0x00bb:
            r7.setSelected(r1)
        L_0x00be:
            android.widget.LinearLayout r7 = r0.f5159c
            if (r7 == 0) goto L_0x00c5
            r7.setEnabled(r6)
        L_0x00c5:
            com.miui.gamebooster.n.c.a r6 = r5.f4697d
            com.miui.gamebooster.n.c.a r7 = com.miui.gamebooster.n.c.a.MILINK_SCREENING
            if (r6 != r7) goto L_0x00cf
            r5.a((com.miui.gamebooster.videobox.adapter.f.c) r0)
            goto L_0x00e2
        L_0x00cf:
            com.miui.gamebooster.n.c.a r7 = com.miui.gamebooster.n.c.a.DISPLAY_STYLE
            if (r6 != r7) goto L_0x00e2
            android.widget.ImageView r6 = r0.f5158b
            if (r6 == 0) goto L_0x00e2
            int r7 = com.miui.gamebooster.videobox.settings.f.b()
            if (r7 == 0) goto L_0x00de
            goto L_0x00df
        L_0x00de:
            r2 = r3
        L_0x00df:
            r6.setSelected(r2)
        L_0x00e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.n.d.g.a(int, android.view.View):void");
    }

    public void a(View view) {
        Context context = view.getContext();
        switch (f.f4695a[this.f4697d.ordinal()]) {
            case 1:
                c.d(view.getContext());
                return;
            case 2:
                c.a(view.getContext());
                return;
            case 3:
                if (!c.b(context)) {
                    Toast.makeText(context, context.getResources().getString(R.string.vtb_other_milink_connect), 0).show();
                    return;
                }
                return;
            case 4:
                c.c(context);
                return;
            default:
                return;
        }
    }

    public boolean b() {
        switch (f.f4695a[this.f4697d.ordinal()]) {
            case 1:
            case 4:
                return B.j() == 0;
            case 2:
            case 3:
            case 8:
                return true;
            case 5:
                return com.miui.gamebooster.videobox.utils.a.b();
            case 6:
                return MiSoundEffectUtils.c() || MiSoundEffectUtils.b();
            case 7:
                return com.miui.gamebooster.videobox.utils.f.a() && com.miui.gamebooster.videobox.utils.f.a(com.miui.gamebooster.videobox.settings.f.a());
            default:
                return false;
        }
    }

    public int c() {
        return this.f4696c;
    }

    public a d() {
        return this.f4697d;
    }
}
