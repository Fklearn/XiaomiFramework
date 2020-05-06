package com.miui.permcenter.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class t extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private LayoutInflater f6568a;

    /* renamed from: b  reason: collision with root package name */
    private Context f6569b;

    /* renamed from: c  reason: collision with root package name */
    private List<k> f6570c = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public a f6571d;

    public interface a {
        void a(k kVar, int i);

        void b(k kVar, int i);
    }

    static class b {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6572a;

        /* renamed from: b  reason: collision with root package name */
        ImageView f6573b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6574c;

        /* renamed from: d  reason: collision with root package name */
        TextView f6575d;
        Button e;
        LinearLayout f;
        LinearLayout g;
        View h;

        b() {
        }
    }

    public t(Context context) {
        this.f6569b = context;
        this.f6568a = LayoutInflater.from(context);
    }

    public void a(a aVar) {
        this.f6571d = aVar;
    }

    public void a(List<k> list) {
        this.f6570c = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.f6570c.size();
    }

    public Object getItem(int i) {
        return this.f6570c.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0173  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r11, android.view.View r12, android.view.ViewGroup r13) {
        /*
            r10 = this;
            if (r12 != 0) goto L_0x006b
            com.miui.permcenter.settings.t$b r12 = new com.miui.permcenter.settings.t$b
            r12.<init>()
            android.view.LayoutInflater r13 = r10.f6568a
            r0 = 2131493410(0x7f0c0222, float:1.86103E38)
            r1 = 0
            android.view.View r13 = r13.inflate(r0, r1)
            r0 = 2131296981(0x7f0902d5, float:1.8211894E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            r12.f6572a = r0
            r0 = 2131296999(0x7f0902e7, float:1.821193E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            r12.f6573b = r0
            r0 = 2131297942(0x7f090696, float:1.8213843E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r12.f6574c = r0
            r0 = 2131296455(0x7f0900c7, float:1.8210827E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r12.f6575d = r0
            r0 = 2131296622(0x7f09016e, float:1.8211166E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.Button r0 = (android.widget.Button) r0
            r12.e = r0
            r0 = 2131296666(0x7f09019a, float:1.8211255E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r12.f = r0
            r0 = 2131296646(0x7f090186, float:1.8211215E38)
            android.view.View r0 = r13.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r12.g = r0
            r0 = 2131296973(0x7f0902cd, float:1.8211878E38)
            android.view.View r0 = r13.findViewById(r0)
            r12.h = r0
            r13.setTag(r12)
            goto L_0x0074
        L_0x006b:
            java.lang.Object r13 = r12.getTag()
            com.miui.permcenter.settings.t$b r13 = (com.miui.permcenter.settings.t.b) r13
            r9 = r13
            r13 = r12
            r12 = r9
        L_0x0074:
            java.util.List<com.miui.permcenter.settings.k> r0 = r10.f6570c
            java.lang.Object r0 = r0.get(r11)
            com.miui.permcenter.settings.k r0 = (com.miui.permcenter.settings.k) r0
            r1 = 8
            r2 = 0
            r3 = 1
            if (r11 != 0) goto L_0x0088
        L_0x0082:
            android.widget.LinearLayout r4 = r12.f
            r4.setVisibility(r2)
            goto L_0x00a5
        L_0x0088:
            if (r11 < r3) goto L_0x00a5
            java.util.List<com.miui.permcenter.settings.k> r4 = r10.f6570c
            int r5 = r11 + -1
            java.lang.Object r4 = r4.get(r5)
            com.miui.permcenter.settings.k r4 = (com.miui.permcenter.settings.k) r4
            long r5 = r0.a()
            long r7 = r4.a()
            int r4 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r4 != 0) goto L_0x0082
            android.widget.LinearLayout r4 = r12.f
            r4.setVisibility(r1)
        L_0x00a5:
            java.util.List<com.miui.permcenter.settings.k> r4 = r10.f6570c
            int r4 = r4.size()
            int r4 = r4 - r3
            if (r11 != r4) goto L_0x00b4
        L_0x00ae:
            android.view.View r2 = r12.h
            r2.setVisibility(r1)
            goto L_0x00d7
        L_0x00b4:
            int r3 = r11 + 1
            java.util.List<com.miui.permcenter.settings.k> r4 = r10.f6570c
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x00d7
            java.util.List<com.miui.permcenter.settings.k> r4 = r10.f6570c
            java.lang.Object r3 = r4.get(r3)
            com.miui.permcenter.settings.k r3 = (com.miui.permcenter.settings.k) r3
            long r4 = r0.a()
            long r6 = r3.a()
            int r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r3 == 0) goto L_0x00ae
            android.view.View r1 = r12.h
            r1.setVisibility(r2)
        L_0x00d7:
            java.lang.String r1 = r0.b()
            android.content.Context r2 = r10.f6569b
            java.lang.CharSequence r2 = com.miui.securityscan.i.c.a((android.content.Context) r2, (java.lang.String) r1)
            android.widget.TextView r3 = r12.f6575d
            r3.setText(r2)
            long r2 = r0.a()
            r4 = 32
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0113
            android.widget.ImageView r2 = r12.f6573b
            r3 = 2131232197(0x7f0805c5, float:1.8080496E38)
            r2.setImageResource(r3)
            android.widget.TextView r2 = r12.f6574c
            android.content.Context r3 = r10.f6569b
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099792(0x7f060090, float:1.7811947E38)
            int r3 = r3.getColor(r4)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r12.f6574c
            r3 = 2131757658(0x7f100a5a, float:1.9146258E38)
        L_0x010f:
            r2.setText(r3)
            goto L_0x0168
        L_0x0113:
            long r2 = r0.a()
            r4 = 131072(0x20000, double:6.47582E-319)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x013e
            android.widget.ImageView r2 = r12.f6573b
            r3 = 2131232198(0x7f0805c6, float:1.8080498E38)
            r2.setImageResource(r3)
            android.widget.TextView r2 = r12.f6574c
            android.content.Context r3 = r10.f6569b
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099790(0x7f06008e, float:1.7811943E38)
            int r3 = r3.getColor(r4)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r12.f6574c
            r3 = 2131757659(0x7f100a5b, float:1.914626E38)
            goto L_0x010f
        L_0x013e:
            long r2 = r0.a()
            r4 = 4096(0x1000, double:2.0237E-320)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0168
            android.widget.ImageView r2 = r12.f6573b
            r3 = 2131232196(0x7f0805c4, float:1.8080494E38)
            r2.setImageResource(r3)
            android.widget.TextView r2 = r12.f6574c
            android.content.Context r3 = r10.f6569b
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099791(0x7f06008f, float:1.7811945E38)
            int r3 = r3.getColor(r4)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r12.f6574c
            r3 = 2131757657(0x7f100a59, float:1.9146256E38)
            goto L_0x010f
        L_0x0168:
            int r2 = r0.c()
            r3 = 999(0x3e7, float:1.4E-42)
            if (r2 != r3) goto L_0x0173
            java.lang.String r2 = "pkg_icon_xspace://"
            goto L_0x0175
        L_0x0173:
            java.lang.String r2 = "pkg_icon://"
        L_0x0175:
            java.lang.String r1 = r2.concat(r1)
            android.widget.ImageView r2 = r12.f6572a
            b.c.a.b.d r3 = b.b.c.j.r.f
            b.b.c.j.r.a((java.lang.String) r1, (android.widget.ImageView) r2, (b.c.a.b.d) r3)
            android.widget.Button r1 = r12.e
            com.miui.permcenter.settings.r r2 = new com.miui.permcenter.settings.r
            r2.<init>(r10, r0, r11)
            r1.setOnClickListener(r2)
            android.widget.LinearLayout r12 = r12.g
            com.miui.permcenter.settings.s r1 = new com.miui.permcenter.settings.s
            r1.<init>(r10, r0, r11)
            r12.setOnClickListener(r1)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.settings.t.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }
}
