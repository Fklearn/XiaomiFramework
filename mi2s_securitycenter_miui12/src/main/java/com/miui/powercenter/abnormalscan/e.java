package com.miui.powercenter.abnormalscan;

import android.content.Context;
import android.content.res.Resources;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class e extends com.miui.common.expandableview.a {
    private static int HEADER_VIEW_TYPE = 0;
    private static int ITEM_VIEW_TYPE = 0;

    /* renamed from: a  reason: collision with root package name */
    private static int f6654a = 1;

    /* renamed from: b  reason: collision with root package name */
    private static int f6655b = 1;

    /* renamed from: c  reason: collision with root package name */
    private final LayoutInflater f6656c;

    /* renamed from: d  reason: collision with root package name */
    private final Context f6657d;
    private final Resources e;
    private List<f> f = new ArrayList();
    /* access modifiers changed from: private */
    public boolean g = true;
    /* access modifiers changed from: private */
    public Set<String> h = new ArraySet();
    /* access modifiers changed from: private */
    public a i;
    private CompoundButton.OnCheckedChangeListener j = new c(this);
    private View.OnClickListener k = new d(this);

    public interface a {
        void a(boolean z);
    }

    private class b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public TextView f6658a;

        private b() {
        }

        /* synthetic */ b(e eVar, c cVar) {
            this();
        }
    }

    private class c {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6660a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6661b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6662c;

        /* renamed from: d  reason: collision with root package name */
        CheckBox f6663d;
        ViewGroup e;

        private c() {
        }

        /* synthetic */ c(e eVar, c cVar) {
            this();
        }
    }

    public e(List<f> list, LayoutInflater layoutInflater, Context context) {
        this.f = list;
        this.f6656c = layoutInflater;
        this.f6657d = context;
        this.e = context.getResources();
        b();
    }

    private int a(int i2) {
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? R.string.pc_abnormal_scan_reason_reconnect : R.string.pc_abnormal_scan_reason_wake_system : R.string.pc_abnormal_scan_reason_runtime_exception : R.string.pc_abnormal_scan_reason_use_GPS : R.string.pc_abnormal_scan_reason_scan_wifi : R.string.pc_abnormal_scan_reason_prevent_standby;
    }

    private void b() {
        ListIterator<AbScanModel> listIterator = this.f.get(1).a().listIterator();
        while (listIterator.hasNext()) {
            this.h.add(listIterator.next().getAbnormalPkg());
        }
    }

    public Set<String> a() {
        return this.h;
    }

    public void a(a aVar) {
        this.i = aVar;
    }

    public void a(boolean z) {
        this.g = z;
    }

    public int getCountForSection(int i2) {
        if (i2 >= this.f.size()) {
            return 0;
        }
        return this.f.get(i2).a().size();
    }

    public AbScanModel getItem(int i2, int i3) {
        if (i2 < this.f.size() && i3 < this.f.get(i2).a().size()) {
            return this.f.get(i2).a().get(i3);
        }
        return null;
    }

    public long getItemId(int i2, int i3) {
        return (long) i3;
    }

    public View getItemView(int i2, int i3, View view, ViewGroup viewGroup) {
        c cVar;
        if (getItemViewType(i2, i3) == ITEM_VIEW_TYPE) {
            return new View(this.f6657d);
        }
        if (view == null) {
            view = this.f6656c.inflate(R.layout.abnormal_scan_apps_item, (ViewGroup) null);
            cVar = new c(this, (c) null);
            cVar.f6660a = (ImageView) view.findViewById(R.id.abnormal_app_image);
            cVar.f6661b = (TextView) view.findViewById(R.id.abnormal_app_name);
            cVar.f6662c = (TextView) view.findViewById(R.id.abnormal_reason);
            cVar.f6663d = (CheckBox) view.findViewById(R.id.app_checked);
            cVar.f6663d.setOnCheckedChangeListener(this.j);
            cVar.e = (ViewGroup) view.findViewById(R.id.abnormal_viewGroup);
            cVar.e.setOnClickListener(this.k);
            view.setTag(cVar);
        } else {
            cVar = (c) view.getTag();
        }
        AbScanModel abScanModel = this.f.get(i2).a().get(i3);
        cVar.f6661b.setText(x.j(this.f6657d, abScanModel.getAbnormalPkg()));
        cVar.f6662c.setText(this.f6657d.getResources().getString(a(((Integer) abScanModel.getAbnormalReason().iterator().next()).intValue())));
        cVar.f6663d.setTag(abScanModel);
        r.a("pkg_icon://".concat(abScanModel.getAbnormalPkg()), cVar.f6660a, r.f);
        cVar.f6663d.setChecked(this.h.contains(abScanModel.getAbnormalPkg()));
        return view;
    }

    public int getItemViewType(int i2, int i3) {
        return i2 == 0 ? ITEM_VIEW_TYPE : f6655b;
    }

    public int getItemViewTypeCount() {
        return 2;
    }

    public int getSectionCount() {
        return this.f.size();
    }

    public View getSectionHeaderView(int i2, View view, ViewGroup viewGroup) {
        b bVar;
        if (getSectionHeaderViewType(i2) == HEADER_VIEW_TYPE) {
            return View.inflate(this.f6657d, R.layout.pc_scan_result_layout_blank_top, (ViewGroup) null);
        }
        if (view == null) {
            view = this.f6656c.inflate(R.layout.pc_abnormal_list_header_view, (ViewGroup) null);
            bVar = new b(this, (c) null);
            TextView unused = bVar.f6658a = (TextView) view.findViewById(R.id.header_title);
            view.setTag(bVar);
        } else {
            bVar = (b) view.getTag();
        }
        bVar.f6658a.setText(this.f.get(i2).b());
        return view;
    }

    public int getSectionHeaderViewType(int i2) {
        return i2 == 0 ? HEADER_VIEW_TYPE : f6654a;
    }

    public int getSectionHeaderViewTypeCount() {
        return 2;
    }
}
