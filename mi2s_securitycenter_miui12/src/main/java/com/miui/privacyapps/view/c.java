package com.miui.privacyapps.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.k.b;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class c extends Dialog implements ViewPager.OnPageChangeListener, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f7431a = {R.drawable.pa_dialog_tutorial_page_one, R.drawable.pa_dialog_tutorial_page_two};

    /* renamed from: b  reason: collision with root package name */
    private static final int[] f7432b = {R.string.privacy_apps_tutorial_prompt_pagetwo, R.string.privacy_apps_tutorial_prompt_pagethree};

    /* renamed from: c  reason: collision with root package name */
    private ViewPager f7433c;

    /* renamed from: d  reason: collision with root package name */
    private ViewPagerIndicator f7434d;
    private Button e;
    /* access modifiers changed from: private */
    public LayoutInflater f;
    private Context g;
    private int h;
    /* access modifiers changed from: private */
    public List<b> i = new ArrayList();

    class a extends PagerAdapter {
        a() {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ((ViewPager) viewGroup).removeView((View) obj);
        }

        public int getCount() {
            return 2;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View inflate = c.this.f.inflate(R.layout.privacy_apps_dialog_viewpager_item, (ViewGroup) null);
            b bVar = (b) c.this.i.get(i);
            ((ImageView) inflate.findViewById(R.id.prompt_imageview)).setImageResource(bVar.a());
            ((TextView) inflate.findViewById(R.id.prompt_textview)).setText(bVar.b());
            ViewPager viewPager = (ViewPager) viewGroup;
            if (viewPager.getChildCount() <= i) {
                i = viewPager.getChildCount();
            }
            viewPager.addView(inflate, i);
            return inflate;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    public c(Context context) {
        super(context);
        this.g = context;
    }

    public void onClick(View view) {
        if (view != this.e) {
            return;
        }
        if (this.h == 0) {
            this.f7433c.setCurrentItem(1);
        } else {
            dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f = LayoutInflater.from(this.g);
        setContentView(R.layout.privacy_apps_tutorial_dialog_layout);
        this.f7433c = findViewById(R.id.view_pager);
        this.f7434d = (ViewPagerIndicator) findViewById(R.id.indicator);
        this.e = (Button) findViewById(R.id.pa_dlg_button);
        this.e.setOnClickListener(this);
        for (int i2 = 0; i2 < 2; i2++) {
            b bVar = new b();
            bVar.a(f7431a[i2]);
            bVar.a(this.g.getString(f7432b[i2]));
            this.i.add(bVar);
        }
        this.f7434d.setIndicatorNum(this.i.size());
        this.f7433c.setAdapter(new a());
        this.f7433c.setOnPageChangeListener(this);
    }

    public void onPageScrollStateChanged(int i2) {
    }

    public void onPageScrolled(int i2, float f2, int i3) {
    }

    public void onPageSelected(int i2) {
        int i3;
        Button button;
        this.f7434d.setSelected(i2);
        if (i2 == 0) {
            button = this.e;
            i3 = R.string.privacy_apps_next_stup;
        } else {
            button = this.e;
            i3 = R.string.privacy_apps_button_know;
        }
        button.setText(i3);
        this.h = i2;
    }
}
