package com.miui.phonemanage.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.j.a;
import com.miui.securitycenter.R;

public class Tab extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private View f6611a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f6612b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f6613c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public a f6614d;
    private int e;
    protected boolean f;
    private boolean g;
    private int h;
    private int i;
    private int j;
    private int k;

    public Tab(Context context) {
        this(context, (AttributeSet) null);
    }

    public Tab(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Tab(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.g = true;
        a(context, this, R.layout.securityscan_bar_item_layout);
    }

    public void a(int i2, int i3) {
        this.k = i3;
        this.j = i2;
        this.f6612b.setImageResource(i2);
    }

    /* access modifiers changed from: protected */
    public void a(Context context, Tab tab, @LayoutRes int i2) {
        this.f6611a = LayoutInflater.from(context).inflate(i2, (ViewGroup) null);
        addView(this.f6611a);
        this.f6612b = (ImageView) this.f6611a.findViewById(R.id.bar_icon);
        this.f6613c = (TextView) this.f6611a.findViewById(R.id.bar_title);
        this.f6611a.setOnClickListener(new a(this, tab));
    }

    public void a(boolean z) {
        if (this.f != z) {
            this.f6612b.setImageResource(z ? this.k : this.j);
            this.f6613c.setTextColor(z ? this.i : this.h);
            setTextSelected(this.g);
            this.g = false;
            this.f = z;
        }
    }

    public void b(int i2, int i3) {
        this.h = i2;
        this.i = i3;
        this.f6613c.setTextColor(this.h);
    }

    public int getTabIndex() {
        return this.e;
    }

    public void setOnTabSelectedListener(a aVar) {
        this.f6614d = aVar;
    }

    public void setTabIndex(int i2) {
        this.e = i2;
    }

    public void setText(String str) {
        this.f6613c.setText(str);
    }

    public void setTextSelected(boolean z) {
        if (z || (!z && !this.g)) {
            this.f6613c.setSelected(z);
        }
    }
}
