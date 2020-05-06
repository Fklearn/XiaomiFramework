package com.miui.phonemanage.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import b.b.j.a;
import b.b.j.i;
import com.miui.optimizemanage.view.d;
import com.miui.securitycenter.R;

public class TabContainerView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TabViewPager f6615a;

    /* renamed from: b  reason: collision with root package name */
    private TabHost f6616b;

    /* renamed from: c  reason: collision with root package name */
    private Context f6617c;

    /* renamed from: d  reason: collision with root package name */
    private int f6618d;
    private a e;
    private b.b.j.a.a f;

    public TabContainerView(Context context) {
        super(context);
        a(context);
    }

    public TabContainerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public TabContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context);
    }

    private void a(Context context) {
        this.f6617c = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.securityscan_tab_container_layout, (ViewGroup) null);
        addView(inflate);
        this.f6615a = (TabViewPager) inflate.findViewById(R.id.viewpager_tab);
        this.f6616b = (TabHost) inflate.findViewById(R.id.tab_host);
        this.f6616b.setContentViewPager(this.f6615a);
    }

    public void a(int i) {
        Tab b2 = this.f6616b.b(i);
        a aVar = this.e;
        if (!(aVar == null || b2 == null)) {
            aVar.a(b2);
        }
        setCurrentItem(i);
    }

    public void a(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.f6615a.addOnPageChangeListener(onPageChangeListener);
    }

    public void a(i iVar, int i) {
        if (iVar != null) {
            this.f = new b.b.j.a.a(iVar.b(), iVar.a());
            this.f6615a.setAdapter(this.f);
            setCurrentItem(i);
        }
    }

    public TabViewPager getContentViewPager() {
        return this.f6615a;
    }

    public int getCurrentItem() {
        return this.f6618d;
    }

    public b.b.j.a.a getTabViewPagerAdapter() {
        return this.f;
    }

    public void setAdapter(i iVar) {
        a(iVar, 0);
    }

    public void setCurrentItem(int i) {
        this.f6618d = i;
        this.f6616b.a(i);
        this.f6615a.setCurrentItem(i, false);
    }

    public void setOffscreenPageLimit(int i) {
        this.f6615a.setOffscreenPageLimit(i);
    }

    public void setOnTabSelectedListener(a aVar) {
        this.e = aVar;
    }

    public void setScrollEnable(boolean z) {
        this.f6615a.setScrollEnable(z);
    }

    public void setTabTextSelected(boolean z) {
        this.f6616b.setTabTextSelected(z);
    }

    public void setTabVisible(boolean z) {
        ObjectAnimator objectAnimator;
        d dVar;
        int dimensionPixelOffset = this.f6617c.getResources().getDimensionPixelOffset(R.dimen.securityscan_bottom_place_height);
        if (z) {
            objectAnimator = ObjectAnimator.ofFloat(this.f6616b, "translationY", new float[]{(float) dimensionPixelOffset, 0.0f});
            objectAnimator.setDuration(600);
            dVar = new d();
        } else {
            objectAnimator = ObjectAnimator.ofFloat(this.f6616b, "translationY", new float[]{0.0f, (float) dimensionPixelOffset});
            objectAnimator.setDuration(600);
            dVar = new d();
        }
        objectAnimator.setInterpolator(dVar);
        objectAnimator.start();
    }
}
