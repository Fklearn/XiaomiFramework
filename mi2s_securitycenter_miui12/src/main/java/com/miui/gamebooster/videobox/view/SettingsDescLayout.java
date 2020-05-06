package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class SettingsDescLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f5219a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f5220b;

    /* renamed from: c  reason: collision with root package name */
    private View f5221c;

    /* renamed from: d  reason: collision with root package name */
    private View f5222d;
    /* access modifiers changed from: private */
    public com.miui.gamebooster.n.c.a e;
    /* access modifiers changed from: private */
    public a f;

    public interface a {
        void b(com.miui.gamebooster.n.c.a aVar);
    }

    public SettingsDescLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SettingsDescLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SettingsDescLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5219a = (TextView) findViewById(R.id.tv_title);
        this.f5220b = (ImageView) findViewById(R.id.iv_back);
        this.f5221c = findViewById(R.id.ll_display_style);
        this.f5222d = findViewById(R.id.ll_frc_vpp);
        View findViewById = findViewById(R.id.iv_desc);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
        this.f5220b.setOnClickListener(new f(this));
        if (this.f5220b.getDrawable() != null) {
            this.f5220b.getDrawable().setAutoMirrored(true);
        }
        setOnClickListener(new g(this));
    }

    public void setFunctionType(com.miui.gamebooster.n.c.a aVar) {
        this.e = aVar;
        this.f5219a.setText(R.string.vtb_func_desc);
        int i = h.f5245a[aVar.ordinal()];
        if (i == 1) {
            this.f5221c.setVisibility(0);
            this.f5222d.setVisibility(8);
        } else if (i == 2) {
            this.f5221c.setVisibility(8);
            this.f5222d.setVisibility(0);
        }
    }

    public void setOnDescBackListener(a aVar) {
        this.f = aVar;
    }
}
