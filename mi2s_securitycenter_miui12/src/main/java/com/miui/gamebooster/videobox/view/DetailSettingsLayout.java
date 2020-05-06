package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.miui.gamebooster.videobox.adapter.b;
import com.miui.gamebooster.videobox.adapter.g;
import com.miui.securitycenter.R;

public class DetailSettingsLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f5211a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f5212b;

    /* renamed from: c  reason: collision with root package name */
    private ListView f5213c;

    /* renamed from: d  reason: collision with root package name */
    private View f5214d;
    private View e;
    /* access modifiers changed from: private */
    public com.miui.gamebooster.n.c.a f;
    private b g;
    private g h;
    private com.miui.gamebooster.videobox.adapter.a i;
    /* access modifiers changed from: private */
    public a j;

    public interface a {
        void a(com.miui.gamebooster.n.c.a aVar);

        void c(com.miui.gamebooster.n.c.a aVar);
    }

    public DetailSettingsLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public DetailSettingsLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DetailSettingsLayout(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.g = new b();
        this.h = new g();
        this.i = new com.miui.gamebooster.videobox.adapter.a();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5211a = (TextView) findViewById(R.id.tv_title);
        this.f5212b = (ImageView) findViewById(R.id.iv_back);
        this.f5213c = (ListView) findViewById(R.id.lv_main);
        this.f5214d = findViewById(R.id.tv_tips);
        this.e = findViewById(R.id.iv_desc);
        if (this.e.getBackground() != null) {
            this.e.getBackground().setAutoMirrored(true);
        }
        this.f5212b.setOnClickListener(new a(this));
        Drawable drawable = this.f5212b.getDrawable();
        if (drawable != null) {
            drawable.setAutoMirrored(true);
        }
        this.e.setOnClickListener(new b(this));
        setOnClickListener(new c(this));
    }

    public void setFunctionType(com.miui.gamebooster.n.c.a aVar) {
        ListView listView;
        ListAdapter listAdapter;
        this.f = aVar;
        int i2 = d.f5242a[aVar.ordinal()];
        if (i2 != 1) {
            if (i2 == 2) {
                this.f5211a.setText(R.string.vb_video_effects_srs_premium_sound);
                listView = this.f5213c;
                listAdapter = this.h;
            } else if (i2 == 3) {
                this.f5211a.setText(R.string.vb_advanced_settings);
                listView = this.f5213c;
                listAdapter = this.i;
            } else {
                return;
            }
            listView.setAdapter(listAdapter);
            this.f5214d.setVisibility(8);
            this.e.setVisibility(8);
            return;
        }
        this.f5211a.setText(R.string.vb_video_effects_display_style);
        this.f5213c.setAdapter(this.g);
        this.f5214d.setVisibility(0);
        this.e.setVisibility(0);
    }

    public void setmOnDetailEventListener(a aVar) {
        this.j = aVar;
    }
}
