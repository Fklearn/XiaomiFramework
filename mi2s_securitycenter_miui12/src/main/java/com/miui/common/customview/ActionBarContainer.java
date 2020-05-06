package com.miui.common.customview;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.e;
import com.miui.securitycenter.R;

public class ActionBarContainer extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f3761a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3762b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f3763c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f3764d;
    private View e;
    private RelativeLayout f;
    private int g;
    private int h = 0;
    private a i;
    private boolean j = true;

    public interface a {
        void a();

        void b();
    }

    public ActionBarContainer(Context context) {
        super(context);
    }

    public ActionBarContainer(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ActionBarContainer(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
    }

    private void a() {
        int i2;
        Resources resources;
        if (e.b() <= 9) {
            resources = getResources();
            i2 = R.dimen.activity_actionbar_icon_margin_lr_v11;
        } else {
            resources = getResources();
            i2 = R.dimen.activity_actionbar_icon_margin_lr;
        }
        int dimensionPixelSize = resources.getDimensionPixelSize(i2);
        RelativeLayout relativeLayout = this.f;
        relativeLayout.setPaddingRelative(dimensionPixelSize, relativeLayout.getPaddingTop(), dimensionPixelSize, this.f.getPaddingBottom());
    }

    public void a(int i2) {
        if (this.j) {
            this.g = i2;
            int i3 = this.h;
            float f2 = ((float) this.g) / ((float) i3);
            this.f3762b.setTranslationY((float) (-((int) (((float) i3) * f2))));
            this.f3761a.setAlpha(f2 * 1.0f);
        }
    }

    public void onClick(View view) {
        a aVar;
        int id = view.getId();
        if (id == R.id.iv_back) {
            a aVar2 = this.i;
            if (aVar2 != null) {
                aVar2.a();
            }
        } else if (id == R.id.iv_settings && (aVar = this.i) != null) {
            aVar.b();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f3763c = (ImageView) findViewById(R.id.iv_back);
        this.f3764d = (ImageView) findViewById(R.id.iv_settings);
        this.f = (RelativeLayout) findViewById(R.id.first_title_container);
        a();
        this.f3763c.setOnClickListener(this);
        this.f3764d.setOnClickListener(this);
        this.f3761a = (TextView) findViewById(R.id.tv_first_title);
        this.f3762b = (TextView) findViewById(R.id.tv_second_title);
        this.e = findViewById(R.id.second_title_container);
        this.f3761a.setAlpha(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.h = i3 + getContext().getResources().getDimensionPixelSize(R.dimen.activity_actionbar_second_title_margin_top);
    }

    public void setActionBarEventListener(a aVar) {
        this.i = aVar;
    }

    public void setEndIcon(int i2) {
        this.f3764d.setImageResource(i2);
    }

    public void setIsShowSecondTitle(boolean z) {
        this.j = z;
        if (!this.j) {
            this.e.setVisibility(8);
            this.f3761a.setAlpha(1.0f);
        }
    }

    public void setTitle(String str) {
        this.f3761a.setText(str);
        this.f3762b.setText(str);
    }
}
