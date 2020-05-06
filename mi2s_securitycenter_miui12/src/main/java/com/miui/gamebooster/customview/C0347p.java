package com.miui.gamebooster.customview;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.miui.securitycenter.R;
import java.util.List;

/* renamed from: com.miui.gamebooster.customview.p  reason: case insensitive filesystem */
class C0347p implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f4220a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4221b;

    C0347p(GameBoxView gameBoxView, List list) {
        this.f4221b = gameBoxView;
        this.f4220a = list;
    }

    public void onGlobalLayout() {
        int i;
        int i2;
        int i3;
        int i4;
        if (this.f4221b.p != null) {
            this.f4221b.p.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        int dimensionPixelOffset = this.f4221b.e.getResources().getDimensionPixelOffset(R.dimen.gb_h_recommend_app_list_item_width);
        float f = 5.5f;
        if (!this.f4220a.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = this.f4221b.q.getLayoutParams();
            int count = this.f4221b.s.getCount();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f4221b.r.getLayoutParams();
            if (!this.f4221b.I) {
                i3 = (this.f4221b.q.getPaddingBottom() * 2) + layoutParams2.topMargin;
                i4 = layoutParams2.bottomMargin;
            } else {
                i3 = this.f4221b.q.getPaddingStart() + layoutParams2.getMarginStart();
                i4 = layoutParams2.getMarginEnd();
            }
            int i5 = i3 + i4;
            float f2 = count > 5 ? 5.5f : (float) count;
            if (this.f4221b.I) {
                layoutParams.width = (int) (((float) i5) + (((float) dimensionPixelOffset) * f2));
            } else {
                layoutParams.height = (int) (((float) i5) + (((float) dimensionPixelOffset) * f2));
            }
            this.f4221b.q.setLayoutParams(layoutParams);
        }
        ViewGroup.LayoutParams layoutParams3 = this.f4221b.m.getLayoutParams();
        int count2 = this.f4221b.o.getCount();
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.f4221b.n.getLayoutParams();
        if (!this.f4221b.I) {
            i = (this.f4221b.m.getPaddingTop() * 2) + layoutParams4.topMargin;
            i2 = layoutParams4.bottomMargin;
        } else {
            i = this.f4221b.m.getPaddingEnd() + layoutParams4.getMarginStart();
            i2 = layoutParams4.getMarginEnd();
        }
        int i6 = i + i2;
        if (count2 <= 5) {
            f = (float) count2;
        }
        if (this.f4221b.I) {
            layoutParams3.width = (int) (((float) i6) + (((float) dimensionPixelOffset) * f));
        } else {
            layoutParams3.height = (int) (((float) i6) + (((float) dimensionPixelOffset) * f));
        }
        this.f4221b.m.setLayoutParams(layoutParams3);
    }
}
