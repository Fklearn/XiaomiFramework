package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.i.b.c;
import b.b.i.b.d;
import com.miui.securitycenter.R;

public class StorageActionBar extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private a f5807a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f5808b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f5809c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f5810d;
    private TextView e;
    private TextView f;
    private ViewGroup g;
    private ViewGroup h;
    private ViewGroup i;
    private View j;
    private int k;
    private boolean l;
    private boolean m;
    private AccelerateInterpolator n;

    public interface a {
        void a();
    }

    public StorageActionBar(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public StorageActionBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StorageActionBar(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.m = true;
        this.n = new AccelerateInterpolator();
        this.k = context.getResources().getDimensionPixelSize(R.dimen.storage_actionbar_min_h);
    }

    private void a() {
        int a2 = d.a(getContext(), this.j.getHeight());
        ViewGroup.LayoutParams layoutParams = this.j.getLayoutParams();
        layoutParams.height = a2;
        this.j.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams2 = this.h.getLayoutParams();
        layoutParams2.height = this.k;
        this.h.setLayoutParams(layoutParams2);
    }

    private void b() {
        if (!c.b() || !d.b()) {
            setLargeTitleVisible(false);
            this.l = true;
        }
    }

    public void a(int i2) {
        if (this.m) {
            float f2 = (float) (-Math.abs(i2));
            this.f5810d.setTranslationY(f2);
            this.f.setTranslationY(f2);
            this.g.setAlpha(this.n.getInterpolation(Math.min(((float) Math.abs(i2)) / ((float) this.i.getHeight()), 1.0f)));
        }
    }

    public void onClick(View view) {
        a aVar;
        if (view.getId() == R.id.back_img && (aVar = this.f5807a) != null) {
            aVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5809c = (TextView) findViewById(R.id.title1);
        this.f5810d = (TextView) findViewById(R.id.title2);
        this.e = (TextView) findViewById(R.id.summary1);
        this.f = (TextView) findViewById(R.id.summary2);
        this.h = (ViewGroup) findViewById(R.id.actionbar1);
        this.i = (ViewGroup) findViewById(R.id.actionbar2);
        this.g = (ViewGroup) findViewById(R.id.actionbar_text);
        this.f5808b = (ImageView) findViewById(R.id.back_img);
        this.f5808b.setOnClickListener(this);
        this.j = findViewById(R.id.statusbar_holder);
        this.g.setAlpha(0.0f);
        b();
        a();
    }

    public void setBackClickListener(a aVar) {
        this.f5807a = aVar;
    }

    public void setLargeTitleVisible(boolean z) {
        ViewGroup viewGroup;
        float f2;
        if (!this.l) {
            this.m = z;
            if (!this.m) {
                this.i.setVisibility(8);
                viewGroup = this.g;
                f2 = 1.0f;
            } else {
                this.i.setVisibility(0);
                viewGroup = this.g;
                f2 = 0.0f;
            }
            viewGroup.setAlpha(f2);
        }
    }
}
