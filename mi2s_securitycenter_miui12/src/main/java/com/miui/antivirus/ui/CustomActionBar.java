package com.miui.antivirus.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.b.d.n;
import com.miui.securitycenter.R;

public class CustomActionBar extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f2917a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f2918b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f2919c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f2920d;
    private View e;
    private RelativeLayout f;
    private a g;
    private boolean h = true;

    public interface a {
        void a();

        void b();
    }

    public CustomActionBar(Context context) {
        super(context);
    }

    public CustomActionBar(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomActionBar(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void a() {
        int i;
        Resources resources;
        if (!n.c()) {
            resources = getResources();
            i = R.dimen.antivirus_actionbar_icon_margin_lr_v11;
        } else {
            resources = getResources();
            i = R.dimen.activity_actionbar_icon_margin_lr;
        }
        int dimensionPixelSize = resources.getDimensionPixelSize(i);
        RelativeLayout relativeLayout = this.f;
        relativeLayout.setPaddingRelative(dimensionPixelSize, relativeLayout.getPaddingTop(), dimensionPixelSize, this.f.getPaddingBottom());
    }

    public void a(boolean z) {
        if (this.h) {
            if (z) {
                PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
                this.e.animate().y((float) (-this.e.getHeight())).setDuration(500).setInterpolator(pathInterpolator).start();
                this.f2917a.animate().alpha(1.0f).setDuration(500).setInterpolator(pathInterpolator).start();
                return;
            }
            setIsShowSecondTitle(false);
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = windowInsets.getSystemWindowInsetTop();
        return super.onApplyWindowInsets(windowInsets);
    }

    public void onClick(View view) {
        a aVar;
        int id = view.getId();
        if (id == R.id.iv_back) {
            a aVar2 = this.g;
            if (aVar2 != null) {
                aVar2.a();
            }
        } else if (id == R.id.iv_settings && (aVar = this.g) != null) {
            aVar.b();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2919c = (ImageView) findViewById(R.id.iv_back);
        this.f2920d = (ImageView) findViewById(R.id.iv_settings);
        this.f = (RelativeLayout) findViewById(R.id.first_title_container);
        a();
        this.f2919c.setOnClickListener(this);
        this.f2920d.setOnClickListener(this);
        this.f2917a = (TextView) findViewById(R.id.tv_first_title);
        this.f2918b = (TextView) findViewById(R.id.tv_second_title);
        this.e = findViewById(R.id.second_title_container);
        this.f2917a.setAlpha(0.0f);
    }

    public void setActionBarEventListener(a aVar) {
        this.g = aVar;
    }

    public void setEndIcon(int i) {
        this.f2920d.setImageResource(i);
    }

    public void setIsShowSecondTitle(boolean z) {
        this.h = z;
        if (!this.h) {
            this.e.setVisibility(8);
            this.f2917a.setAlpha(1.0f);
        }
    }

    public void setTitle(String str) {
        this.f2917a.setText(str);
        this.f2918b.setText(str);
    }
}
