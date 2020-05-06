package com.miui.optimizemanage.c;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.l;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.f;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class k extends d {
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f5910c;

    /* renamed from: d  reason: collision with root package name */
    private String f5911d;
    /* access modifiers changed from: private */
    public String e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public boolean h;
    /* access modifiers changed from: private */
    public boolean i = false;
    /* access modifiers changed from: private */
    public String[] j = new String[3];

    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        ImageView f5912a;

        /* renamed from: b  reason: collision with root package name */
        TextView f5913b;

        /* renamed from: c  reason: collision with root package name */
        private Context f5914c;

        public a(View view) {
            super(view);
            this.f5913b = (TextView) view.findViewById(R.id.title);
            this.f5912a = (ImageView) view.findViewById(R.id.icon);
            this.f5912a.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
            this.f5914c = view.getContext();
            l.a(view, 1.0f);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0098, code lost:
            if (com.miui.optimizemanage.c.k.d(r7) != false) goto L_0x00a7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x00a5, code lost:
            if (com.miui.optimizemanage.c.k.e(r7) != false) goto L_0x00a7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x00da, code lost:
            if (com.miui.optimizemanage.c.k.d(r7) != false) goto L_0x00a7;
         */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x00e6  */
        /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void a(android.view.View r6, com.miui.optimizemanage.c.d r7, int r8) {
            /*
                r5 = this;
                super.a(r6, r7, r8)
                com.miui.optimizemanage.c.k r7 = (com.miui.optimizemanage.c.k) r7
                java.lang.String[] r8 = r7.j
                r0 = 0
                r8 = r8[r0]
                android.widget.ImageView r1 = r5.f5912a
                b.c.a.b.d r2 = b.b.c.j.r.g
                r3 = 2131231069(0x7f08015d, float:1.8078209E38)
                b.b.c.j.r.a((java.lang.String) r8, (android.widget.ImageView) r1, (b.c.a.b.d) r2, (int) r3)
                android.widget.TextView r8 = r5.f5913b
                java.lang.String r1 = r7.f5910c
                r8.setText(r1)
                boolean r8 = r7.h
                r1 = 2131231055(0x7f08014f, float:1.807818E38)
                r2 = 2131231056(0x7f080150, float:1.8078182E38)
                r3 = 2131166859(0x7f07068b, float:1.7947975E38)
                r4 = 2131166833(0x7f070671, float:1.7947923E38)
                if (r8 == 0) goto L_0x009b
                boolean r8 = r7.g
                if (r8 == 0) goto L_0x0066
                boolean r8 = r7.f
                if (r8 == 0) goto L_0x0066
                r8 = 2131231061(0x7f080155, float:1.8078192E38)
                r6.setBackgroundResource(r8)
                android.content.Context r8 = r5.f5914c
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r0 = r5.f5914c
                android.content.res.Resources r0 = r0.getResources()
                int r0 = r0.getDimensionPixelSize(r4)
                android.content.Context r1 = r5.f5914c
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r0, r8, r1, r8)
                goto L_0x00dd
            L_0x0066:
                boolean r8 = r7.f
                if (r8 == 0) goto L_0x0094
                r8 = 2131231063(0x7f080157, float:1.8078196E38)
                r6.setBackgroundResource(r8)
                android.content.Context r8 = r5.f5914c
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r1 = r5.f5914c
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                android.content.Context r2 = r5.f5914c
                android.content.res.Resources r2 = r2.getResources()
                int r2 = r2.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r1, r8, r2, r0)
                goto L_0x00dd
            L_0x0094:
                boolean r8 = r7.g
                if (r8 == 0) goto L_0x00d2
                goto L_0x00a7
            L_0x009b:
                boolean r8 = r7.g
                if (r8 == 0) goto L_0x00cc
                boolean r8 = r7.f
                if (r8 == 0) goto L_0x00cc
            L_0x00a7:
                r6.setBackgroundResource(r1)
                android.content.Context r8 = r5.f5914c
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r1 = r5.f5914c
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                android.content.Context r2 = r5.f5914c
                android.content.res.Resources r2 = r2.getResources()
                int r2 = r2.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r1, r0, r2, r8)
                goto L_0x00dd
            L_0x00cc:
                boolean r8 = r7.f
                if (r8 == 0) goto L_0x00d6
            L_0x00d2:
                r6.setBackgroundResource(r2)
                goto L_0x00dd
            L_0x00d6:
                boolean r8 = r7.g
                if (r8 == 0) goto L_0x00d2
                goto L_0x00a7
            L_0x00dd:
                r6.setOnClickListener(r7)
                boolean r6 = r7.i
                if (r6 != 0) goto L_0x00f1
                java.lang.String r6 = r7.e
                com.miui.optimizemanage.a.a.c(r6)
                r6 = 1
                boolean unused = r7.i = r6
            L_0x00f1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizemanage.c.k.a.a(android.view.View, com.miui.optimizemanage.c.d, int):void");
        }
    }

    public k(JSONObject jSONObject) {
        int i2 = 0;
        JSONArray optJSONArray = jSONObject.optJSONArray("images");
        if (optJSONArray != null) {
            int length = optJSONArray.length();
            while (i2 < 3 && i2 < length) {
                this.j[i2] = optJSONArray.optString(i2);
                i2++;
            }
        }
        this.f5910c = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.f5911d = jSONObject.optString(MijiaAlertModel.KEY_URL);
        this.e = jSONObject.optString("dataId");
        a((int) R.layout.card_layout_news_template_7);
    }

    public e a(View view) {
        return new a(view);
    }

    public void a(boolean z) {
        this.g = z;
    }

    public void b(boolean z) {
        this.h = z;
    }

    public void c(boolean z) {
        this.f = z;
    }

    public void onClick(View view) {
        super.onClick(view);
        if (!f.a()) {
            i.c(view.getContext(), this.f5911d, this.f5910c);
            com.miui.optimizemanage.a.a.b(this.e);
        }
    }
}
