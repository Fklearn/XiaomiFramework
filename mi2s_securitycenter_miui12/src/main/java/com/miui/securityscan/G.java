package com.miui.securityscan;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import com.miui.common.card.models.FuncTopBannerScrollCnModel;
import com.miui.common.card.models.FuncTopBannerScrollGlobalModel;

class G implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    private View f7549a;

    /* renamed from: b  reason: collision with root package name */
    private View f7550b;

    /* renamed from: c  reason: collision with root package name */
    private int f7551c;

    /* renamed from: d  reason: collision with root package name */
    private int f7552d;
    private boolean e;
    final /* synthetic */ L f;

    G(L l) {
        this.f = l;
    }

    private void a(View view) {
        Object tag;
        if (view != null && this.f.F != null && (tag = view.getTag()) != null) {
            if ((tag instanceof FuncTopBannerScrollCnModel.FuncTopBannerScrollHolder) || (tag instanceof FuncTopBannerScrollGlobalModel.FuncTopBannerGlobalScrollHolder)) {
                Log.d("com.miui.securityscan.MainActivity", "viewpager stop auto scroll");
                this.f.F.resetViewPager();
            }
        }
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        View view;
        if (this.f.y != null && this.f.y.getChildCount() >= 1) {
            this.f.F.setDefaultStatShow(true);
            View childAt = this.f.y.getChildAt(this.f.y.getChildCount() - 1);
            if (this.f.qa == 0) {
                L l = this.f;
                int unused = l.qa = l.y.getHeight();
            }
            int i4 = i2 + i;
            if (i4 == i3 && childAt != null && childAt.getBottom() == this.f.qa) {
                this.f.oa.setCanRefresh(true);
                com.miui.securityscan.a.G.d();
            } else {
                this.f.oa.setCanRefresh(false);
            }
            if (this.e) {
                if (this.f7551c < i) {
                    view = this.f7549a;
                } else {
                    if (this.f7552d > i4 - 1) {
                        view = this.f7550b;
                    }
                    this.f7551c = i;
                    this.f7552d = i4 - 1;
                    this.f7549a = absListView.getChildAt(0);
                    this.f7550b = absListView.getChildAt(i2 - 1);
                }
                a(view);
                this.f7551c = i;
                this.f7552d = i4 - 1;
                this.f7549a = absListView.getChildAt(0);
                this.f7550b = absListView.getChildAt(i2 - 1);
            }
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        Activity activity = this.f.getActivity();
        if (this.f.a(activity)) {
            if (i == 0) {
                this.e = false;
                MainActivity mainActivity = (MainActivity) activity;
                boolean m = mainActivity.m();
                if (this.f.y.getLastVisiblePosition() == this.f.y.getCount() - 1 && !m) {
                    mainActivity.a(true, true);
                }
            } else if (i == 1 || i == 2) {
                this.e = true;
            }
        }
    }
}
