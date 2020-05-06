package com.miui.powercenter.quickoptimize;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class r extends com.miui.common.expandableview.a {
    private static int HEADER_VIEW_TYPE = 0;
    private static int ITEM_VIEW_TYPE = 0;

    /* renamed from: a  reason: collision with root package name */
    private static int f7243a = 1;

    /* renamed from: b  reason: collision with root package name */
    private static int f7244b = 1;

    /* renamed from: c  reason: collision with root package name */
    private List<n> f7245c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private Context f7246d;
    /* access modifiers changed from: private */
    public b.b.c.i.b e;
    /* access modifiers changed from: private */
    public boolean f = true;
    private boolean g = true;
    private CompoundButton.OnCheckedChangeListener h = new p(this);
    private View.OnClickListener i = new q(this);

    static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f7247a;

        /* renamed from: b  reason: collision with root package name */
        LinearLayout f7248b;

        /* renamed from: c  reason: collision with root package name */
        ViewGroup f7249c;

        a() {
        }
    }

    static class b {

        /* renamed from: a  reason: collision with root package name */
        int f7250a;

        /* renamed from: b  reason: collision with root package name */
        int f7251b;

        b() {
        }
    }

    static class c {

        /* renamed from: a  reason: collision with root package name */
        TextView f7252a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7253b;

        /* renamed from: c  reason: collision with root package name */
        CheckBox f7254c;

        /* renamed from: d  reason: collision with root package name */
        ViewGroup f7255d;
        ViewGroup e;
        ImageView f;
        RelativeLayout g;

        c() {
        }
    }

    public r(Context context) {
        this.f7246d = context;
    }

    /* access modifiers changed from: private */
    public void a(CheckBox checkBox) {
        b bVar = (b) checkBox.getTag();
        m a2 = this.f7245c.get(bVar.f7250a).a(bVar.f7251b);
        L.a(Integer.valueOf(a2.f7231a), checkBox.isChecked());
        Object obj = a2.f7233c;
        if (obj != null) {
            for (com.miui.powercenter.f.a aVar : (List) obj) {
                L.a(aVar.f7063b, checkBox.isChecked());
            }
        }
    }

    public void a(b.b.c.i.b bVar) {
        this.e = bVar;
    }

    public void a(boolean z) {
        this.f = z;
    }

    public int getCountForSection(int i2) {
        if (i2 >= this.f7245c.size()) {
            return 0;
        }
        return this.f7245c.get(i2).a();
    }

    public Object getItem(int i2, int i3) {
        if (i2 < this.f7245c.size() && i3 < this.f7245c.get(i2).a()) {
            return this.f7245c.get(i2).a(i3);
        }
        return null;
    }

    public long getItemId(int i2, int i3) {
        return (long) i3;
    }

    public View getItemView(int i2, int i3, View view, ViewGroup viewGroup) {
        m a2 = this.f7245c.get(i2).a(i3);
        if (getItemViewType(i2, i3) == ITEM_VIEW_TYPE) {
            return new View(this.f7246d);
        }
        if (view == null) {
            view = View.inflate(this.f7246d, R.layout.pc_optimize_list_item, (ViewGroup) null);
            c cVar = new c();
            view.setTag(cVar);
            cVar.f7252a = (TextView) view.findViewById(R.id.title);
            cVar.f7253b = (TextView) view.findViewById(R.id.time_flag);
            cVar.f7254c = (CheckBox) view.findViewById(R.id.select);
            cVar.f7255d = (ViewGroup) view.findViewById(R.id.child_list);
            cVar.e = (ViewGroup) view.findViewById(R.id.item_layout);
            cVar.e.setTag(cVar);
            cVar.f = (ImageView) view.findViewById(R.id.status);
            cVar.g = (RelativeLayout) view.findViewById(R.id.root_view);
        }
        c cVar2 = (c) view.getTag();
        cVar2.f7252a.setText(a2.f7232b);
        b bVar = new b();
        bVar.f7250a = i2;
        bVar.f7251b = i3;
        if (!this.f7245c.get(i2).c()) {
            cVar2.f.setVisibility(8);
            cVar2.f7254c.setVisibility(0);
            cVar2.f7254c.setTag(bVar);
            cVar2.f7254c.setOnCheckedChangeListener(this.h);
            long a3 = o.a(this.f7246d, a2);
            if (a3 > 0) {
                String d2 = s.d(this.f7246d, a3);
                TextView textView = cVar2.f7253b;
                textView.setText("+" + d2);
                cVar2.f7253b.setVisibility(0);
            } else {
                cVar2.f7253b.setVisibility(8);
            }
            cVar2.g.setOnClickListener(this.i);
            if (!L.a(Integer.valueOf(a2.f7231a)) || a2.f7231a == 8) {
                int i4 = a2.f7231a;
                if (i4 == 8) {
                    if (this.g) {
                        this.g = false;
                    } else {
                        cVar2.f7254c.setChecked(L.a(Integer.valueOf(i4)));
                    }
                }
                cVar2.f7254c.setChecked(false);
            } else {
                cVar2.f7254c.setChecked(true);
            }
        } else {
            cVar2.f.setVisibility(0);
            cVar2.f7254c.setVisibility(8);
            cVar2.f7254c.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            cVar2.f.setImageResource(R.drawable.scan_state_compelete);
            cVar2.f7253b.setVisibility(8);
            cVar2.g.setImportantForAccessibility(2);
            cVar2.g.setBackgroundColor(this.f7246d.getResources().getColor(R.color.pc_battery_item_normal_color));
        }
        Object obj = a2.f7233c;
        if (obj == null || ((ArrayList) obj).size() == 0) {
            cVar2.f7255d.setVisibility(8);
            ViewGroup viewGroup2 = cVar2.f7255d;
            viewGroup2.setPaddingRelative(viewGroup2.getPaddingStart(), 0, cVar2.f7255d.getPaddingEnd(), 0);
        } else {
            cVar2.f7255d.setVisibility(0);
            ViewGroup viewGroup3 = cVar2.f7255d;
            viewGroup3.setPaddingRelative(viewGroup3.getPaddingStart(), (int) this.f7246d.getResources().getDimension(R.dimen.pc_optimize_list_item_autoheightgridview_margin_top), cVar2.f7255d.getPaddingEnd(), 0);
            C0532k kVar = new C0532k(this.f7246d);
            kVar.a((List) a2.f7233c);
            ((GridView) cVar2.f7255d).setAdapter(kVar);
        }
        return view;
    }

    public int getItemViewType(int i2, int i3) {
        return i2 == 0 ? ITEM_VIEW_TYPE : f7244b;
    }

    public int getItemViewTypeCount() {
        return 2;
    }

    public int getSectionCount() {
        return this.f7245c.size();
    }

    public View getSectionHeaderView(int i2, View view, ViewGroup viewGroup) {
        if (getSectionHeaderViewType(i2) == HEADER_VIEW_TYPE) {
            return View.inflate(this.f7246d, R.layout.pc_scan_result_layout_blank_top, (ViewGroup) null);
        }
        if (view == null) {
            view = View.inflate(this.f7246d, R.layout.pc_optimize_list_header, (ViewGroup) null);
            a aVar = new a();
            view.setTag(aVar);
            view.setImportantForAccessibility(2);
            aVar.f7247a = (TextView) view.findViewById(R.id.header_title);
            aVar.f7249c = (ViewGroup) view.findViewById(R.id.separator_bar);
            aVar.f7248b = (LinearLayout) view.findViewById(R.id.header_corner);
        }
        a aVar2 = (a) view.getTag();
        aVar2.f7247a.setText(this.f7245c.get(i2).b());
        aVar2.f7249c.setVisibility(i2 > 1 ? 0 : 8);
        aVar2.f7248b.setBackgroundColor(this.f7246d.getResources().getColor(R.color.pc_battery_item_normal_color));
        return view;
    }

    public int getSectionHeaderViewType(int i2) {
        return i2 == 0 ? HEADER_VIEW_TYPE : f7243a;
    }

    public int getSectionHeaderViewTypeCount() {
        return 2;
    }

    public void updateData(List<n> list) {
        this.f7245c.clear();
        this.f7245c.addAll(list);
    }
}
