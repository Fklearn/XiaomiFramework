package com.miui.gamebooster.n.d;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.miui.gamebooster.n.c.b;
import com.miui.gamebooster.n.d.b;
import com.miui.gamebooster.videobox.adapter.f;
import com.miui.gamebooster.videobox.adapter.h;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class j extends b {

    /* renamed from: c  reason: collision with root package name */
    private b f4701c;

    /* renamed from: d  reason: collision with root package name */
    private List<g> f4702d = new ArrayList();

    public j(String str, b bVar) {
        super(str);
        this.f4701c = bVar;
    }

    private void a(ViewPager viewPager) {
        int i;
        if (viewPager != null) {
            Resources resources = viewPager.getContext().getResources();
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
            int i2 = i.f4700a[this.f4701c.ordinal()];
            if (i2 == 1 || i2 == 2) {
                marginLayoutParams.setMarginStart(resources.getDimensionPixelSize(R.dimen.videobox_main_ps1));
                i = resources.getDimensionPixelSize(R.dimen.videobox_main_pe1);
            } else {
                if (i2 == 3) {
                    i = 0;
                    marginLayoutParams.setMarginStart(0);
                }
                viewPager.setLayoutParams(marginLayoutParams);
            }
            marginLayoutParams.setMarginEnd(i);
            viewPager.setLayoutParams(marginLayoutParams);
        }
    }

    private void b(View view) {
        int i;
        int i2;
        if (view != null) {
            boolean b2 = e.b();
            Resources resources = view.getContext().getResources();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int i3 = i.f4700a[this.f4701c.ordinal()];
            if (i3 == 1) {
                i2 = R.dimen.vb_main_item_video_effect_h;
            } else if (i3 != 2) {
                if (i3 == 3) {
                    i2 = R.dimen.vb_main_item_apps_h;
                }
                view.setLayoutParams(layoutParams);
            } else {
                int i4 = R.dimen.vb_main_item_func_h;
                if (!b2) {
                    i4 = this.f4702d.size() <= 3 ? R.dimen.vb_main_item_func_h3 : R.dimen.vb_main_item_func_h2;
                }
                i = resources.getDimensionPixelSize(i4);
                layoutParams.height = i;
                view.setLayoutParams(layoutParams);
            }
            i = resources.getDimensionPixelSize(i2);
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
        }
    }

    private void c(View view) {
        int i;
        if (view != null) {
            int i2 = i.f4700a[this.f4701c.ordinal()];
            if (i2 == 1 || i2 == 2) {
                i = 0;
            } else if (i2 == 3) {
                i = 8;
            } else {
                return;
            }
            view.setVisibility(i);
        }
    }

    public void a(int i, View view, b.a aVar) {
        if (view != null && view.getTag() != null) {
            h.a aVar2 = (h.a) view.getTag();
            b(aVar2.e);
            c(aVar2.f5170b);
            Resources resources = view.getContext().getResources();
            if (aVar2.f5172d != null) {
                int e = e();
                aVar2.f5172d.setVisibility(e > 1 ? 0 : 8);
                aVar2.f5172d.setTotalCount(e);
                if (!e.b()) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) aVar2.f5172d.getLayoutParams();
                    layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.vb_main_indicator_mb_s2);
                    aVar2.f5172d.setLayoutParams(layoutParams);
                }
            }
            a(aVar2.f5171c);
            f adapter = aVar2.f5171c.getAdapter();
            if (adapter == null) {
                aVar2.f5171c.setAdapter(new f(this.f4702d, aVar, this.f4701c));
            } else if (adapter instanceof f) {
                adapter.a();
            }
            aVar2.f5171c.setOnPageChangeListener(new h(this, aVar2));
        }
    }

    public void a(g gVar) {
        if (gVar != null && !this.f4702d.contains(gVar)) {
            this.f4702d.add(gVar);
        }
    }

    public void a(List<g> list) {
        this.f4702d = list;
    }

    public boolean b() {
        return true;
    }

    public int c() {
        return this.f4702d.size();
    }

    public int d() {
        return R.layout.video_box_list_item;
    }

    public int e() {
        int size = this.f4702d.size();
        int i = this.f4701c == com.miui.gamebooster.n.c.b.FLOATING_APPS ? 8 : e.b() ? 3 : 6;
        return (size / i) + (size % i > 0 ? 1 : 0);
    }

    public com.miui.gamebooster.n.c.b f() {
        return this.f4701c;
    }

    public boolean g() {
        List<g> list = this.f4702d;
        return list == null || list.isEmpty();
    }
}
