package com.miui.phonemanage.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import b.b.j.a;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class TabHost extends FrameLayout implements a {

    /* renamed from: a  reason: collision with root package name */
    private Tab f6619a;

    /* renamed from: b  reason: collision with root package name */
    private Tab f6620b;

    /* renamed from: c  reason: collision with root package name */
    private ViewPager f6621c;

    /* renamed from: d  reason: collision with root package name */
    private View f6622d;
    private List<Tab> e;

    public TabHost(Context context) {
        this(context, (AttributeSet) null);
    }

    public TabHost(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TabHost(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.e = new ArrayList();
        this.f6622d = LayoutInflater.from(context).inflate(R.layout.securityscan_tab_host_layout, (ViewGroup) null);
        addView(this.f6622d);
        this.f6619a = (Tab) this.f6622d.findViewById(R.id.securityscan_tab);
        this.f6619a.setText(context.getString(R.string.securitycenter_tab_title));
        int color = context.getResources().getColor(R.color.phone_manage_bm_tab_text_normal_color);
        int color2 = context.getResources().getColor(R.color.phone_manage_bm_tab_text_selected_color);
        this.f6619a.b(color, color2);
        this.f6619a.a(R.drawable.security_tab_normal, R.drawable.security_tab_selected);
        this.f6619a.setTabIndex(0);
        this.f6619a.setOnTabSelectedListener(this);
        this.f6620b = (Tab) this.f6622d.findViewById(R.id.phone_manage_tab);
        this.f6620b.setText(context.getString(R.string.phone_manage));
        this.f6620b.b(color, color2);
        this.f6620b.a(R.drawable.phonemanage_tab_normal, R.drawable.phonemanage_tab_selected);
        this.f6620b.setTabIndex(1);
        this.f6620b.setOnTabSelectedListener(this);
        this.e.add(this.f6619a);
        this.e.add(this.f6620b);
    }

    public void a(int i) {
        int size = this.e.size();
        int i2 = 0;
        while (i2 < size) {
            this.e.get(i2).a(i == i2);
            i2++;
        }
    }

    public void a(Tab tab) {
        this.f6621c.setCurrentItem(tab.getTabIndex(), true);
    }

    public Tab b(int i) {
        if (this.e.size() <= i) {
            return null;
        }
        return this.e.get(i);
    }

    public View getRootView() {
        return this.f6622d;
    }

    public void setContentViewPager(ViewPager viewPager) {
        this.f6621c = viewPager;
    }

    public void setTabTextSelected(boolean z) {
        int size = this.e.size();
        for (int i = 0; i < size; i++) {
            this.e.get(i).setTextSelected(z);
        }
    }
}
