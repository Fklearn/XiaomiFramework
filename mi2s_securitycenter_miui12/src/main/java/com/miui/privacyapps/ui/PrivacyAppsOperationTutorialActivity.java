package com.miui.privacyapps.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.e;
import b.b.k.b;
import com.miui.applicationlock.ConfirmAccessControl;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.o;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class PrivacyAppsOperationTutorialActivity extends b.b.c.c.a implements ViewPager.OnPageChangeListener, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static int[] f7389a = {R.drawable.pa_tutorial_page_one, R.drawable.pa_tutorial_page_two, R.drawable.pa_tutorial_page_three};

    /* renamed from: b  reason: collision with root package name */
    private static int[] f7390b = {R.string.privacy_apps_tutorial_prompt_pageone, R.string.privacy_apps_tutorial_prompt_pagetwo, R.string.privacy_apps_tutorial_prompt_pagethree};

    /* renamed from: c  reason: collision with root package name */
    private ViewPager f7391c;

    /* renamed from: d  reason: collision with root package name */
    private ViewPagerIndicator f7392d;
    private Button e;
    /* access modifiers changed from: private */
    public LayoutInflater f;
    private C0259c g;
    private boolean h = true;
    /* access modifiers changed from: private */
    public List<b> i = new ArrayList();

    class a extends PagerAdapter {
        a() {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ((ViewPager) viewGroup).removeView((View) obj);
        }

        public int getCount() {
            return 3;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View inflate = PrivacyAppsOperationTutorialActivity.this.f.inflate(R.layout.privacy_apps_tutorial_viewpager_item, (ViewGroup) null);
            b bVar = (b) PrivacyAppsOperationTutorialActivity.this.i.get(i);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.prompt_imageview);
            imageView.setImageResource(bVar.a());
            imageView.setColorFilter(inflate.getResources().getColor(R.color.pa_instruction_icon_filter));
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

    public static int[] a(int[] iArr) {
        int i2 = 0;
        int[] iArr2 = new int[0];
        if (iArr != null) {
            iArr2 = new int[iArr.length];
            int length = iArr.length - 1;
            while (length >= 0) {
                iArr2[i2] = iArr[length];
                length--;
                i2++;
            }
        }
        return iArr2;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        PrivacyAppsOperationTutorialActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 3) {
            setResult(i3);
            if (i3 == -1) {
                this.h = true;
                return;
            }
            this.h = false;
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsOperationTutorialActivity, miui.app.Activity] */
    public void onClick(View view) {
        if (view == this.e) {
            startActivityForResult(new Intent(this, PrivacyAppsManageActivity.class), 3);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.privacyapps.ui.PrivacyAppsOperationTutorialActivity, android.view.View$OnClickListener, miui.app.Activity, android.support.v4.view.ViewPager$OnPageChangeListener] */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_apps_operation_tutorial);
        if (bundle != null && bundle.containsKey(AdvancedSlider.STATE)) {
            this.h = false;
        }
        if (e.b() >= 10) {
            getActionBar().setExpandState(0);
        }
        this.g = C0259c.b((Context) this);
        this.f7391c = findViewById(R.id.view_pager);
        this.f7392d = (ViewPagerIndicator) findViewById(R.id.indicator);
        if (o.v()) {
            f7389a = a(f7389a);
            f7390b = a(f7390b);
        }
        for (int i2 = 0; i2 < 3; i2++) {
            b bVar = new b();
            bVar.a(f7389a[i2]);
            bVar.a(getString(f7390b[i2]));
            this.i.add(bVar);
        }
        this.f7392d.setIndicatorNum(this.i.size());
        this.f = LayoutInflater.from(this);
        this.f7391c.setAdapter(new a());
        this.f7391c.setOnPageChangeListener(this);
        this.e = (Button) findViewById(R.id.use_privacy_apps);
        this.e.setOnClickListener(this);
        setResult(-1);
        if (o.v()) {
            this.f7391c.setCurrentItem(f7389a.length - 1);
        }
    }

    public void onPageScrollStateChanged(int i2) {
    }

    public void onPageScrolled(int i2, float f2, int i3) {
    }

    public void onPageSelected(int i2) {
        if (o.v()) {
            this.f7392d.setSelected((f7389a.length - 1) - i2);
        } else {
            this.f7392d.setSelected(i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        PrivacyAppsOperationTutorialActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.h);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsOperationTutorialActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        PrivacyAppsOperationTutorialActivity.super.onStart();
        if (!this.g.d() || this.h) {
            this.h = true;
            return;
        }
        Intent intent = new Intent(this, ConfirmAccessControl.class);
        intent.putExtra("extra_data", "HappyCodingMain");
        startActivityForResult(intent, 3);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        PrivacyAppsOperationTutorialActivity.super.onStop();
        if (this.h) {
            this.h = false;
        }
    }
}
