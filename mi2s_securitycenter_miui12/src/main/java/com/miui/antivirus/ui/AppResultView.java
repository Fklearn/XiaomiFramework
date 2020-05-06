package com.miui.antivirus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.b.o;
import com.miui.antivirus.model.e;
import com.miui.antivirus.model.j;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;

public class AppResultView extends e implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: d  reason: collision with root package name */
    private ImageView f2915d;
    private TextView e;
    private TextView f;
    private Button g;
    private Button h;
    private CheckBox i;
    private e j;
    private RelativeLayout k;
    private LinearLayout l;
    private View m;
    private View n;

    public AppResultView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppResultView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0134, code lost:
        r7.g.setText(com.miui.securitycenter.R.string.button_text_open_now);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0197, code lost:
        r8.setText(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.antivirus.model.e r8) {
        /*
            r7 = this;
            r7.j = r8
            android.widget.CheckBox r0 = r7.i
            r1 = 8
            r0.setVisibility(r1)
            android.widget.Button r0 = r7.h
            r0.setVisibility(r1)
            android.widget.RelativeLayout r0 = r7.k
            r2 = 0
            r0.setVisibility(r2)
            android.widget.TextView r0 = r7.f
            r0.setVisibility(r2)
            android.view.View r0 = r7.m
            r0.setVisibility(r1)
            android.view.View r0 = r7.n
            r0.setVisibility(r1)
            int[] r0 = com.miui.antivirus.ui.b.f2954b
            com.miui.antivirus.model.e$a r3 = r8.g()
            int r3 = r3.ordinal()
            r0 = r0[r3]
            r3 = 2131755732(0x7f1002d4, float:1.9142352E38)
            r4 = 1
            java.lang.String r5 = "pkg_icon://"
            switch(r0) {
                case 1: goto L_0x019b;
                case 2: goto L_0x015a;
                case 3: goto L_0x013b;
                case 4: goto L_0x011b;
                case 5: goto L_0x00f1;
                case 6: goto L_0x0084;
                case 7: goto L_0x003a;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x01fe
        L_0x003a:
            android.widget.TextView r0 = r7.e
            java.lang.String r3 = r8.h()
            r0.setText(r3)
            android.widget.TextView r0 = r7.f
            r0.setVisibility(r1)
            android.widget.RelativeLayout r0 = r7.k
            r0.setVisibility(r1)
            android.widget.Button r0 = r7.h
            r0.setVisibility(r2)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            com.miui.antivirus.model.e r3 = r7.j
            java.lang.String r3 = r3.m()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.widget.ImageView r3 = r7.f2915d
            b.c.a.b.d r4 = b.b.c.j.r.f
            b.b.c.j.r.a((java.lang.String) r0, (android.widget.ImageView) r3, (b.c.a.b.d) r4)
            android.widget.Button r0 = r7.h
            r3 = 2131755512(0x7f1001f8, float:1.9141905E38)
            r0.setText(r3)
            android.view.View r0 = r7.n
            boolean r8 = r8.d()
            if (r8 == 0) goto L_0x007f
            r1 = r2
        L_0x007f:
            r0.setVisibility(r1)
            goto L_0x01fe
        L_0x0084:
            android.widget.TextView r0 = r7.e
            java.lang.String r3 = r8.h()
            r0.setText(r3)
            b.b.b.o$f r0 = r8.o()
            int[] r3 = com.miui.antivirus.ui.b.f2953a
            int r0 = r0.ordinal()
            r0 = r3[r0]
            if (r0 == r4) goto L_0x00b8
            r3 = 2
            if (r0 == r3) goto L_0x009f
            goto L_0x00dc
        L_0x009f:
            android.widget.TextView r0 = r7.f
            r3 = 2131755523(0x7f100203, float:1.9141928E38)
            r0.setText(r3)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "apk_icon://"
            r0.append(r3)
            com.miui.antivirus.model.e r3 = r7.j
            java.lang.String r3 = r3.q()
            goto L_0x00ce
        L_0x00b8:
            android.widget.TextView r0 = r7.f
            r3 = 2131755522(0x7f100202, float:1.9141926E38)
            r0.setText(r3)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            com.miui.antivirus.model.e r3 = r7.j
            java.lang.String r3 = r3.m()
        L_0x00ce:
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.widget.ImageView r3 = r7.f2915d
            b.c.a.b.d r4 = b.b.c.j.r.f
            b.b.c.j.r.a((java.lang.String) r0, (android.widget.ImageView) r3, (b.c.a.b.d) r4)
        L_0x00dc:
            android.widget.CheckBox r0 = r7.i
            r0.setVisibility(r2)
            android.widget.CheckBox r0 = r7.i
            boolean r8 = r8.v()
            r0.setChecked(r8)
            android.widget.RelativeLayout r8 = r7.k
            r8.setVisibility(r1)
            goto L_0x01fe
        L_0x00f1:
            android.view.View r8 = r7.m
            r8.setVisibility(r2)
            android.widget.TextView r8 = r7.e
            r0 = 2131757733(0x7f100aa5, float:1.914641E38)
            r8.setText(r0)
            android.widget.TextView r8 = r7.f
            r0 = 2131757734(0x7f100aa6, float:1.9146412E38)
            r8.setText(r0)
            android.content.res.Resources r8 = r7.getResources()
            r0 = 2131232401(0x7f080691, float:1.808091E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r0)
            android.graphics.drawable.BitmapDrawable r8 = miui.content.res.IconCustomizer.generateIconStyleDrawable(r8)
            android.widget.ImageView r0 = r7.f2915d
            r0.setImageDrawable(r8)
            goto L_0x0134
        L_0x011b:
            android.widget.TextView r8 = r7.e
            r0 = 2131757738(0x7f100aaa, float:1.914642E38)
            r8.setText(r0)
            android.widget.TextView r8 = r7.f
            r0 = 2131757736(0x7f100aa8, float:1.9146416E38)
            r8.setText(r0)
            android.widget.ImageView r8 = r7.f2915d
            b.c.a.b.d r0 = b.b.c.j.r.f
            java.lang.String r1 = "drawable://2131230965"
            b.b.c.j.r.a((java.lang.String) r1, (android.widget.ImageView) r8, (b.c.a.b.d) r0)
        L_0x0134:
            android.widget.Button r8 = r7.g
            r8.setText(r3)
            goto L_0x01fe
        L_0x013b:
            android.widget.TextView r8 = r7.e
            r0 = 2131757739(0x7f100aab, float:1.9146422E38)
            r8.setText(r0)
            android.widget.TextView r8 = r7.f
            r0 = 2131757737(0x7f100aa9, float:1.9146418E38)
            r8.setText(r0)
            android.widget.ImageView r8 = r7.f2915d
            b.c.a.b.d r0 = b.b.c.j.r.f
            java.lang.String r1 = "pkg_icon://com.android.mms"
            b.b.c.j.r.a((java.lang.String) r1, (android.widget.ImageView) r8, (b.c.a.b.d) r0)
            android.widget.Button r8 = r7.g
            r0 = 2131755725(0x7f1002cd, float:1.9142337E38)
            goto L_0x0197
        L_0x015a:
            android.widget.TextView r0 = r7.e
            r1 = 2131755515(0x7f1001fb, float:1.9141911E38)
            r0.setText(r1)
            android.widget.TextView r0 = r7.f
            android.content.Context r1 = r7.f2962c
            r3 = 2131755516(0x7f1001fc, float:1.9141913E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r8.h()
            r4[r2] = r6
            java.lang.String r1 = r1.getString(r3, r4)
            r0.setText(r1)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            java.lang.String r8 = r8.m()
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            android.widget.ImageView r0 = r7.f2915d
            b.c.a.b.d r1 = b.b.c.j.r.f
            b.b.c.j.r.a((java.lang.String) r8, (android.widget.ImageView) r0, (b.c.a.b.d) r1)
            android.widget.Button r8 = r7.g
            r0 = 2131755514(0x7f1001fa, float:1.914191E38)
        L_0x0197:
            r8.setText(r0)
            goto L_0x01fe
        L_0x019b:
            android.view.View r0 = r7.m
            r0.setVisibility(r2)
            b.b.c.j.l.a(r7)
            android.widget.LinearLayout r0 = r7.l
            r0.setOnLongClickListener(r7)
            com.miui.antivirus.model.j r8 = (com.miui.antivirus.model.j) r8
            boolean r8 = r8.y()
            if (r8 == 0) goto L_0x01dd
            android.widget.TextView r8 = r7.e
            r0 = 2131756564(0x7f100614, float:1.914404E38)
            r8.setText(r0)
            android.widget.TextView r8 = r7.f
            r0 = 2131755517(0x7f1001fd, float:1.9141916E38)
            r8.setText(r0)
            android.widget.Button r8 = r7.g
            r0 = 2131755724(0x7f1002cc, float:1.9142335E38)
            r8.setText(r0)
            android.content.res.Resources r8 = r7.getResources()
            r0 = 2131232402(0x7f080692, float:1.8080912E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r0)
            android.graphics.drawable.BitmapDrawable r8 = miui.content.res.IconCustomizer.generateIconStyleDrawable(r8)
            android.widget.ImageView r0 = r7.f2915d
            r0.setImageDrawable(r8)
            goto L_0x01fe
        L_0x01dd:
            android.widget.TextView r8 = r7.e
            r0 = 2131755519(0x7f1001ff, float:1.914192E38)
            r8.setText(r0)
            android.widget.TextView r8 = r7.f
            r0 = 2131755518(0x7f1001fe, float:1.9141918E38)
            r8.setText(r0)
            android.widget.Button r8 = r7.g
            r0 = 2131755735(0x7f1002d7, float:1.9142358E38)
            r8.setText(r0)
            android.widget.ImageView r8 = r7.f2915d
            b.c.a.b.d r0 = b.b.c.j.r.f
            java.lang.String r1 = "pkg_icon://com.android.updater"
            b.b.c.j.r.a((java.lang.String) r1, (android.widget.ImageView) r8, (b.c.a.b.d) r0)
        L_0x01fe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.ui.AppResultView.a(com.miui.antivirus.model.e):void");
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.j.g() == e.a.VIRUS) {
            this.j.e(z);
            o.a(this.f2962c).b(this.j);
        }
    }

    public void onClick(View view) {
        if (view == this.h || view == this.g) {
            this.f2961b.a(1012, this.j);
        } else if (view == this && this.j.g() == e.a.VIRUS) {
            new s(this.f2962c, this.j, this.f2961b).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2915d = (ImageView) findViewById(R.id.icon);
        this.e = (TextView) findViewById(R.id.title);
        this.f = (TextView) findViewById(R.id.summary);
        this.g = (Button) findViewById(R.id.button);
        this.h = (Button) findViewById(R.id.item_button);
        this.i = (CheckBox) findViewById(R.id.checkbox);
        this.k = (RelativeLayout) findViewById(R.id.button_layout);
        this.l = (LinearLayout) findViewById(R.id.inner_layout);
        this.m = findViewById(R.id.top_empty_view);
        this.n = findViewById(R.id.bottom_empty_view);
        this.g.setOnClickListener(this);
        this.h.setOnClickListener(this);
        this.i.setOnCheckedChangeListener(this);
        setOnClickListener(this);
    }

    public boolean onLongClick(View view) {
        e eVar;
        int i2;
        Context context;
        String string;
        e eVar2;
        int i3;
        int i4 = b.f2954b[this.j.g().ordinal()];
        if (i4 != 1) {
            if (i4 != 6) {
                if (i4 == 7) {
                    eVar2 = this.j;
                    i3 = 4;
                }
                return true;
            }
            if (p.a() >= 5) {
                eVar2 = this.j;
                i3 = 3;
            }
            return true;
            eVar2.a(i3);
            eVar = this.j;
            string = eVar.h();
        } else {
            if (((j) this.j).y()) {
                this.j.a(1);
                eVar = this.j;
                context = this.f2962c;
                i2 = R.string.sp_settings_check_item_title_root;
            } else {
                this.j.a(2);
                eVar = this.j;
                context = this.f2962c;
                i2 = R.string.sp_settings_check_item_title_update;
            }
            string = context.getString(i2);
        }
        eVar.a(string);
        a(this.j, this.f2962c);
        return true;
    }
}
