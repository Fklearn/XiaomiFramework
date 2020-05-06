package com.miui.securityscan;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import b.b.j.a.a;
import b.b.j.h;
import b.b.j.i;
import com.miui.common.card.models.BaseCardModel;
import com.miui.phonemanage.view.TabContainerView;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.superpower.b.k;
import java.util.List;
import miui.app.Activity;
import miui.os.Build;

public class MainActivity extends C0534a {
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TabContainerView f7562b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Fragment[] f7563c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f7564d;
    /* access modifiers changed from: private */
    public boolean e = true;

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.securityscan.MainActivity, miui.app.Activity] */
    private void initView() {
        this.f7562b = (TabContainerView) findViewById(R.id.tab_containerview_main);
        this.f7562b.getTabViewPagerAdapter();
        L l = (L) getFragmentManager().findFragmentByTag(a.a(this.f7562b.getContentViewPager().getId(), 0));
        if (l == null) {
            l = new L();
        }
        this.f7562b.getTabViewPagerAdapter();
        h hVar = (h) getFragmentManager().findFragmentByTag(a.a(this.f7562b.getContentViewPager().getId(), 1));
        if (hVar == null) {
            hVar = new h();
        }
        this.f7563c = new Fragment[]{l, hVar};
        this.f7562b.setAdapter(new i(this, this.f7563c, getFragmentManager()));
        this.f7562b.a((ViewPager.OnPageChangeListener) new C0543c(this));
    }

    public void a(BaseCardModel baseCardModel, int i) {
        if (l() == 0) {
            ((L) this.f7563c[0]).a(baseCardModel, i);
        }
    }

    public void a(BaseCardModel baseCardModel, List<BaseCardModel> list) {
        if (l() == 1) {
            ((h) this.f7563c[1]).a(baseCardModel, list);
        }
    }

    public void a(BaseCardModel baseCardModel, List<BaseCardModel> list, int i) {
        if (l() == 0) {
            ((L) this.f7563c[0]).a(baseCardModel, list, i);
        }
    }

    public void a(boolean z, boolean z2) {
        TabContainerView tabContainerView = this.f7562b;
        if (tabContainerView != null) {
            this.e = z;
            tabContainerView.setTabVisible(z);
            this.f7562b.setScrollEnable(z2);
        }
    }

    public int l() {
        TabContainerView tabContainerView = this.f7562b;
        if (tabContainerView != null) {
            return tabContainerView.getCurrentItem();
        }
        return 0;
    }

    public boolean m() {
        return this.e;
    }

    public void n() {
        Fragment[] fragmentArr = this.f7563c;
        if (fragmentArr[0] != null) {
            ((L) fragmentArr[0]).j();
        }
    }

    public void o() {
        if (l() == 0) {
            ((L) this.f7563c[0]).r();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        MainActivity.super.onActivityResult(i, i2, intent);
        if (l() == 0) {
            ((L) this.f7563c[0]).onActivityResult(i, i2, intent);
        }
    }

    public void onBackPressed() {
        if (l() == 0) {
            ((L) this.f7563c[0]).k();
        } else {
            MainActivity.super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.m_activity_main);
        getWindow().setBackgroundDrawable((Drawable) null);
        k.a((Activity) this);
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.f7563c = new Fragment[]{new L()};
            getFragmentManager().beginTransaction().replace(16908290, this.f7563c[0]).commit();
            return;
        }
        initView();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        TabContainerView tabContainerView;
        MainActivity.super.onNewIntent(intent);
        setIntent(intent);
        if (!(l() == 0 || (tabContainerView = this.f7562b) == null)) {
            tabContainerView.setCurrentItem(0);
        }
        ((L) this.f7563c[0]).o();
        ((L) this.f7563c[0]).e();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        TabContainerView tabContainerView = this.f7562b;
        if (tabContainerView != null) {
            tabContainerView.setTabTextSelected(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        MainActivity.super.onRestart();
        if (l() == 0) {
            ((L) this.f7563c[0]).n();
        } else {
            ((L) this.f7563c[0]).a(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        G.c();
    }

    public void p() {
        if (l() == 1) {
            ((h) this.f7563c[1]).b();
        }
    }
}
