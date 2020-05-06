package com.miui.gamebooster.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.i.b;
import com.miui.gamebooster.d.f;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.o;
import com.miui.securitycenter.R;
import java.util.concurrent.TimeUnit;

public class MainTopContentFrame extends RelativeLayout implements o {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f4927a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f4928b;

    /* renamed from: c  reason: collision with root package name */
    private b f4929c;

    /* renamed from: d  reason: collision with root package name */
    public View f4930d;
    private Runnable e;
    private ImageView f;

    public MainTopContentFrame(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainTopContentFrame(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainTopContentFrame(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.layout_xunyou, this, true);
    }

    public void a(int i, String str) {
        View view = this.f4930d;
        if (view != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.sign_status);
            if (!TextUtils.isEmpty(str)) {
                ((TextView) this.f4930d.findViewById(R.id.sign_summary)).setText(str);
                Drawable drawable = imageView.getContext().getResources().getDrawable(i);
                drawable.setColorFilter(new PorterDuffColorFilter(-7829368, PorterDuff.Mode.MULTIPLY));
                imageView.setBackground(drawable);
            } else {
                imageView.setBackgroundResource(i);
            }
            this.f.setVisibility(8);
        }
    }

    public /* synthetic */ void a(View view) {
        this.f.setVisibility(8);
        com.miui.common.persistence.b.b("key_gamebooster_red_point_press_day", TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
        Utils.a(this.e);
    }

    public void a(f fVar) {
        String str;
        this.f4927a.setImageResource(fVar == f.OPEN ? R.drawable.gb_speed_on : R.drawable.gb_speed_off);
        int i = C0457wa.f5124a[fVar.ordinal()];
        if (i == 1) {
            str = "not_open";
        } else if (i == 2) {
            str = "activat";
        } else if (i == 3) {
            str = "overdue";
        } else {
            return;
        }
        C0373d.c("show", str);
    }

    public void a(Runnable runnable) {
        this.e = runnable;
    }

    public /* synthetic */ void b(View view) {
        this.f4929c.a(114, new Object());
    }

    public void c() {
        View view = this.f4930d;
        if (view != null) {
            view.findViewById(R.id.sign_gift).setVisibility(8);
        }
    }

    public void e() {
        if (this.f4930d != null) {
            this.f.setVisibility(0);
        }
    }

    public void f() {
        View view = this.f4930d;
        if (view != null) {
            view.findViewById(R.id.sign_gift).setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f4927a = (ImageView) findViewById(R.id.xunyou_netbooster_text);
        this.f4928b = (TextView) findViewById(R.id.xunyou_business);
        findViewById(R.id.xunyou_netbooster).setOnClickListener(new C0423f(this));
        this.f4930d = findViewById(R.id.sign_gift);
        this.f = (ImageView) this.f4930d.findViewById(R.id.sign_red_point);
        this.f4930d.findViewById(R.id.sign_gift).setOnClickListener(new C0421e(this));
        if (com.miui.common.persistence.b.a("key_gamebooster_red_point_press_day", -1) == TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
            this.f.setVisibility(8);
        }
    }

    public void setBusinessText(String str) {
        this.f4928b.setText(str);
        this.f4928b.setVisibility(0);
        C0373d.g("show", "time");
    }

    public void setEventHandler(b bVar) {
        this.f4929c = bVar;
    }
}
