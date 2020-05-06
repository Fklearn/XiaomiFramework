package com.miui.gamebooster.videobox.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.B;
import b.b.c.j.r;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.n.d.b;
import com.miui.gamebooster.n.d.g;
import com.miui.gamebooster.n.d.m;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class f extends PagerAdapter {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public List<g> f5149a = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public b.a f5150b;

    /* renamed from: c  reason: collision with root package name */
    private com.miui.gamebooster.n.c.b f5151c;

    /* renamed from: d  reason: collision with root package name */
    private List<BaseAdapter> f5152d = new ArrayList();
    private int e;

    public class a extends BaseAdapter {

        /* renamed from: a  reason: collision with root package name */
        private int f5153a;

        public a(int i) {
            this.f5153a = i;
        }

        private void a(RelativeLayout relativeLayout, int i) {
            if (!e.b() && relativeLayout != null && i >= 4) {
                relativeLayout.setPaddingRelative(relativeLayout.getPaddingStart(), relativeLayout.getContext().getResources().getDimensionPixelSize(R.dimen.vtb_main_app_mt_style2), relativeLayout.getPaddingEnd(), relativeLayout.getPaddingBottom());
            }
        }

        public int a() {
            return this.f5153a;
        }

        public int getCount() {
            int size = f.this.f5149a.size();
            if ((this.f5153a + 1) * 8 > size) {
                return size % 8;
            }
            return 8;
        }

        public g getItem(int i) {
            int i2 = (this.f5153a * 8) + i;
            if (i2 < f.this.f5149a.size()) {
                return (g) f.this.f5149a.get(i2);
            }
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            String str;
            String str2;
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videobox_main_item_float_apps_layout, viewGroup, false);
                c cVar = new c();
                cVar.f5158b = (ImageView) view.findViewById(R.id.icon);
                cVar.f5157a = (RelativeLayout) view.findViewById(R.id.container);
                view.setTag(cVar);
            }
            g item = getItem(i);
            if (!(item == null || !(item instanceof m) || view.getTag() == null)) {
                c cVar2 = (c) view.getTag();
                cVar2.f5158b.setImageResource(item.c());
                m mVar = (m) item;
                if (B.c(mVar.g()) == 999) {
                    str2 = mVar.f();
                    str = "pkg_icon_xspace://";
                } else {
                    str2 = mVar.f();
                    str = "pkg_icon://";
                }
                r.a(str.concat(str2), cVar2.f5158b, r.f, viewGroup.getContext().getResources().getDrawable(R.drawable.gb_def_icon));
                f.this.a(cVar2.f5157a, i);
                a(cVar2.f5157a, i);
            }
            if (A.a()) {
                com.miui.gamebooster.n.a.a.a(view);
            }
            return view;
        }
    }

    public class b extends BaseAdapter {

        /* renamed from: a  reason: collision with root package name */
        private List<g> f5155a = new ArrayList();

        public b(List<g> list) {
            this.f5155a = list;
        }

        private void a(View view, int i) {
            if (!e.b() && view != null && i >= 3) {
                view.setPaddingRelative(view.getPaddingStart(), view.getContext().getResources().getDimensionPixelSize(R.dimen.vtb_main_func_mt_style2), view.getPaddingEnd(), view.getPaddingBottom());
            }
        }

        private void b(View view, int i) {
            int i2;
            LinearLayout linearLayout;
            if (view instanceof LinearLayout) {
                int i3 = i % 3;
                if (i3 == 0) {
                    linearLayout = (LinearLayout) view;
                    i2 = 8388611;
                } else if (i3 == 1) {
                    ((LinearLayout) view).setGravity(1);
                    return;
                } else if (i3 == 2) {
                    linearLayout = (LinearLayout) view;
                    i2 = 8388613;
                } else {
                    return;
                }
                linearLayout.setGravity(i2);
            }
        }

        public int getCount() {
            return this.f5155a.size();
        }

        public g getItem(int i) {
            return this.f5155a.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.video_box_pager_item1, viewGroup, false);
                c cVar = new c();
                cVar.f5160d = (TextView) view.findViewById(R.id.title);
                cVar.f5158b = (ImageView) view.findViewById(R.id.icon);
                cVar.f5159c = (LinearLayout) view;
                view.setTag(cVar);
            }
            g item = getItem(i);
            if (item != null) {
                item.a(i, view);
            }
            b(view, i);
            a(view, i);
            return view;
        }
    }

    public static class c {

        /* renamed from: a  reason: collision with root package name */
        public RelativeLayout f5157a;

        /* renamed from: b  reason: collision with root package name */
        public ImageView f5158b;

        /* renamed from: c  reason: collision with root package name */
        public LinearLayout f5159c;

        /* renamed from: d  reason: collision with root package name */
        public TextView f5160d;
    }

    public f(List<g> list, b.a aVar, com.miui.gamebooster.n.c.b bVar) {
        int i = 3;
        this.e = 3;
        this.f5149a = list;
        this.f5150b = aVar;
        this.f5151c = bVar;
        this.e = !e.b() ? 6 : i;
    }

    private View a(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        GridView gridView = new GridView(context);
        gridView.setNumColumns(4);
        gridView.setAdapter(new a(i));
        gridView.setSelector(new ColorDrawable(0));
        Resources resources = context.getResources();
        gridView.setPaddingRelative(resources.getDimensionPixelSize(R.dimen.videobox_main_ps), resources.getDimensionPixelSize(e.b() ? R.dimen.vb_main_item_apps_grid_pt : R.dimen.vb_main_item_apps_grid_pt2), resources.getDimensionPixelSize(R.dimen.videobox_main_pe), gridView.getPaddingBottom());
        gridView.setOnItemClickListener(new d(this));
        viewGroup.addView(gridView);
        return gridView;
    }

    /* access modifiers changed from: private */
    public void a(RelativeLayout relativeLayout, int i) {
        if (relativeLayout != null) {
            Resources resources = relativeLayout.getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.vb_main_float_app_ms0);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.vb_main_float_app_ms1);
            int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.vb_main_float_app_me2);
            int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.vb_main_float_app_me3);
            int i2 = i % 4;
            if (na.c()) {
                dimensionPixelSize = dimensionPixelSize4;
            }
            if (i2 == 0) {
                relativeLayout.setGravity(8388611);
                relativeLayout.setPaddingRelative(dimensionPixelSize, relativeLayout.getPaddingTop(), relativeLayout.getPaddingEnd(), relativeLayout.getPaddingBottom());
            } else if (i2 == 1) {
                relativeLayout.setGravity(8388611);
                relativeLayout.setPaddingRelative(dimensionPixelSize2, relativeLayout.getPaddingTop(), relativeLayout.getPaddingEnd(), relativeLayout.getPaddingBottom());
            } else if (i2 == 2) {
                relativeLayout.setGravity(8388613);
                relativeLayout.setPaddingRelative(0, relativeLayout.getPaddingTop(), dimensionPixelSize3, relativeLayout.getPaddingBottom());
            } else if (i2 == 3) {
                relativeLayout.setGravity(8388613);
                relativeLayout.setPaddingRelative(relativeLayout.getPaddingStart(), relativeLayout.getPaddingTop(), dimensionPixelSize4, relativeLayout.getPaddingBottom());
            }
        }
    }

    @NonNull
    private View b(ViewGroup viewGroup, int i) {
        GridView gridView = new GridView(viewGroup.getContext());
        gridView.setNumColumns(3);
        int i2 = this.e;
        int i3 = i * i2;
        b bVar = new b(this.f5149a.subList(i3, i2 + i3 > this.f5149a.size() ? this.f5149a.size() : this.e + i3));
        this.f5152d.add(bVar);
        gridView.setAdapter(bVar);
        gridView.setSelector(new ColorDrawable(0));
        gridView.setOnItemClickListener(new e(this));
        viewGroup.addView(gridView);
        return gridView;
    }

    public void a() {
        for (BaseAdapter next : this.f5152d) {
            if (next != null) {
                next.notifyDataSetChanged();
            }
        }
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

    public int getCount() {
        int size = this.f5149a.size();
        int i = 0;
        if (this.f5151c == com.miui.gamebooster.n.c.b.FLOATING_APPS) {
            int i2 = size / 8;
            if (size % 8 != 0) {
                i = 1;
            }
            return i2 + i;
        }
        int i3 = this.e;
        int i4 = size / i3;
        if (size % i3 != 0) {
            i = 1;
        }
        return i4 + i;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        return this.f5151c == com.miui.gamebooster.n.c.b.FLOATING_APPS ? a(viewGroup, i) : b(viewGroup, i);
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view.equals(obj);
    }
}
