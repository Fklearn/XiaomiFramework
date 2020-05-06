package com.miui.gamebooster.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.i;
import b.b.c.j.r;
import b.c.a.b.c.b;
import b.c.a.b.d;
import b.c.a.b.d.d;
import com.miui.gamebooster.a.C0328f;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.m.C0382m;
import com.miui.securitycenter.R;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.List;

public class C extends C0399e {

    /* renamed from: d  reason: collision with root package name */
    private List<t> f4538d = new ArrayList();

    public static class a extends C0328f {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f4539a;

        /* renamed from: b  reason: collision with root package name */
        private View[] f4540b;

        /* renamed from: c  reason: collision with root package name */
        private ImageView[] f4541c = new ImageView[3];

        /* renamed from: d  reason: collision with root package name */
        private ImageView[] f4542d = new ImageView[3];
        private ImageView[] e = new ImageView[3];
        /* access modifiers changed from: private */
        public CheckBox[] f = new CheckBox[3];
        private TextView[] g = new TextView[3];
        private TextView[] h = new TextView[3];
        private TextView[] i = new TextView[3];
        private d j;

        public a(View view) {
            super(view);
            this.f4539a = view.getContext();
            d.a aVar = new d.a();
            aVar.a(true);
            aVar.b(false);
            aVar.c((int) R.drawable.gb_wonderful_video_loading);
            aVar.a(Bitmap.Config.RGB_565);
            aVar.c(true);
            aVar.a((b.c.a.b.c.a) new b(i.a(this.f4539a, 11.0f)));
            this.j = aVar.a();
            a(view);
        }

        /* access modifiers changed from: private */
        public String a(t tVar) {
            if (!TextUtils.isEmpty(tVar.c()) && C0382m.a(tVar.c()) > 0) {
                return tVar.c();
            }
            if (TextUtils.isEmpty(tVar.g()) || C0382m.a(tVar.g()) <= 0) {
                return null;
            }
            return tVar.g();
        }

        private void a(int i2, int i3, t tVar, boolean z, I.a aVar) {
            int i4;
            Context context;
            if (tVar != null) {
                boolean h2 = tVar.h();
                r.a(d.a.VIDEO_FILE.c(a(tVar)), this.f4541c[i3], this.j);
                TextView textView = this.g[i3];
                if (h2) {
                    context = this.f4539a;
                    i4 = R.string.gb_game_video_type_ai;
                } else {
                    context = this.f4539a;
                    i4 = R.string.gb_game_video_type_manual;
                }
                textView.setText(context.getString(i4));
                this.g[i3].setBackgroundResource(h2 ? R.drawable.shape_gb_wonderful_video_ai : R.drawable.shape_gb_wonderful_video_manual);
                this.h[i3].setText(tVar.a());
                this.i[i3].setText(tVar.b());
                this.f[i3].setChecked(tVar.i());
                int i5 = 0;
                this.f[i3].setVisibility(z ? 0 : 8);
                this.e[i3].setVisibility(z ? 8 : 0);
                ImageView imageView = this.f4542d[i3];
                if (z || (!TextUtils.isEmpty(tVar.g()) && C0382m.a(tVar.g()) != 0)) {
                    i5 = 8;
                }
                imageView.setVisibility(i5);
                b(i2, i3, tVar, z, aVar);
            }
        }

        private void a(int i2, View view) {
            this.f4541c[i2] = (ImageView) view.findViewById(R.id.iv_bg_img);
            this.f4542d[i2] = (ImageView) view.findViewById(R.id.btn_download);
            this.g[i2] = (TextView) view.findViewById(R.id.iv_tag);
            this.f[i2] = (CheckBox) view.findViewById(R.id.iv_check);
            this.h[i2] = (TextView) view.findViewById(R.id.tv_duration);
            this.i[i2] = (TextView) view.findViewById(R.id.tv_size);
            this.e[i2] = (ImageView) view.findViewById(R.id.iv_play);
        }

        /* access modifiers changed from: private */
        public void a(View view, t tVar) {
            if (!TextUtils.isEmpty(tVar.c())) {
                n.a().b(new B(this, tVar, view));
            }
        }

        private void b(int i2, int i3, t tVar, boolean z, I.a aVar) {
            View[] viewArr = this.f4540b;
            if (i3 < viewArr.length) {
                boolean z2 = z;
                t tVar2 = tVar;
                I.a aVar2 = aVar;
                int i4 = i2;
                viewArr[i3].setOnLongClickListener(new u(this, z2, tVar2, aVar2, i4));
                this.f4542d[i3].setOnLongClickListener(new v(this, z2, tVar2, aVar2, i4));
                this.f4540b[i3].setOnClickListener(new w(this, z2, tVar2, aVar2, i4));
                this.f[i3].setOnClickListener(new x(this, i3, tVar2, aVar2, i4));
                this.e[i3].setOnClickListener(new y(this, z, tVar));
                this.f4542d[i3].setOnClickListener(new z(this, z, tVar));
            }
        }

        public void a(View view) {
            View findViewById = view.findViewById(R.id.cell_left);
            a(0, findViewById);
            View findViewById2 = view.findViewById(R.id.cell_center);
            a(1, findViewById2);
            View findViewById3 = view.findViewById(R.id.cell_right);
            a(2, findViewById3);
            this.f4540b = new View[]{findViewById, findViewById2, findViewById3};
        }

        public void a(View view, int i2, Object obj, I.a aVar) {
            C c2 = (C) obj;
            List<t> g2 = c2.g();
            for (int i3 = 0; i3 < this.f4540b.length; i3++) {
                if (i3 < g2.size()) {
                    this.f4540b[i3].setVisibility(0);
                    a(i2, i3, g2.get(i3), c2.d(), aVar);
                } else {
                    this.f4540b[i3].setVisibility(4);
                }
            }
        }
    }

    public C() {
        super(R.layout.gb_wonderful_moment_video_list_item);
    }

    public C0328f a(View view) {
        return new a(view);
    }

    public void a(t tVar) {
        this.f4538d.add(tVar);
    }

    public t e() {
        if (f() > 0) {
            return this.f4538d.get(f() - 1);
        }
        return null;
    }

    public int f() {
        return this.f4538d.size();
    }

    public List<t> g() {
        return this.f4538d;
    }
}
