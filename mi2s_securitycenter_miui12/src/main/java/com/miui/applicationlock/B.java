package com.miui.applicationlock;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.G;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.security.SecurityManager;
import miui.widget.SlidingButton;

public class B extends com.miui.common.expandableview.a implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private final LayoutInflater f3099a;

    /* renamed from: b  reason: collision with root package name */
    private final Context f3100b;

    /* renamed from: c  reason: collision with root package name */
    private final Resources f3101c;

    /* renamed from: d  reason: collision with root package name */
    private List<F> f3102d = new ArrayList();

    private class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public TextView f3103a;

        private a() {
        }
    }

    private class b {

        /* renamed from: a  reason: collision with root package name */
        ImageView f3105a;

        /* renamed from: b  reason: collision with root package name */
        TextView f3106b;

        /* renamed from: c  reason: collision with root package name */
        TextView f3107c;

        /* renamed from: d  reason: collision with root package name */
        TextView f3108d;
        SlidingButton e;

        private b() {
        }
    }

    public B(List<F> list, LayoutInflater layoutInflater, Context context) {
        this.f3102d = list;
        this.f3099a = layoutInflater;
        this.f3100b = context;
        this.f3101c = context.getResources();
    }

    private void a() {
        int i;
        Resources resources;
        boolean z = false;
        for (F next : this.f3102d) {
            if (next.c() != null) {
                z = true;
                for (C0257a f : next.a()) {
                    boolean f2 = f.f();
                }
            }
        }
        if (z) {
            for (F next2 : this.f3102d) {
                G c2 = next2.c();
                if (c2 != null) {
                    if (c2 == G.ENABLED) {
                        resources = this.f3101c;
                        i = R.string.privacyapp_number_masked_text;
                    } else {
                        resources = this.f3101c;
                        i = R.string.privacyapp_number_unmasked_text;
                    }
                    next2.a(resources.getString(i));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(C0257a aVar, boolean z) {
        aVar.a(z);
        ((SecurityManager) this.f3100b.getSystemService("security")).setApplicationMaskNotificationEnabledForUser(aVar.e(), z, aVar.d());
        a();
        notifyDataSetChanged();
    }

    public void a(List<F> list, boolean z) {
        this.f3102d.clear();
        this.f3102d.addAll(list);
        if (z) {
            a();
        }
        notifyDataSetChanged();
    }

    public int getCountForSection(int i) {
        return this.f3102d.get(i).a().size();
    }

    public C0257a getItem(int i, int i2) {
        return this.f3102d.get(i).a().get(i2);
    }

    public long getItemId(int i, int i2) {
        return (long) i2;
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        b bVar;
        TextView textView;
        int i3;
        Resources resources;
        String str;
        String str2;
        if (view == null) {
            view = this.f3099a.inflate(R.layout.adapter_list_apps_unlock, (ViewGroup) null);
            bVar = new b();
            bVar.f3105a = (ImageView) view.findViewById(R.id.app_image_lock);
            bVar.f3106b = (TextView) view.findViewById(R.id.app_name_lock);
            bVar.f3107c = (TextView) view.findViewById(R.id.app_type_lock);
            bVar.f3108d = (TextView) view.findViewById(R.id.app_suggest);
            bVar.e = view.findViewById(R.id.switch1);
            bVar.e.setOnPerformCheckedChangeListener(this);
            view.setTag(bVar);
        } else {
            bVar = (b) view.getTag();
        }
        C0257a aVar = this.f3102d.get(i).a().get(i2);
        bVar.f3106b.setText(aVar.a());
        if (aVar.b().intValue() > 0) {
            textView = bVar.f3107c;
            resources = this.f3100b.getResources();
            i3 = R.string.system_application;
        } else {
            textView = bVar.f3107c;
            resources = this.f3100b.getResources();
            i3 = R.string.third_application;
        }
        textView.setText(resources.getString(i3));
        bVar.f3108d.setVisibility(!aVar.c() ? 8 : 0);
        bVar.e.setTag(aVar);
        if (aVar.d() == 999) {
            str = aVar.e();
            str2 = "pkg_icon_xspace://";
        } else {
            str = aVar.e();
            str2 = "pkg_icon://";
        }
        r.a(str2.concat(str), bVar.f3105a, r.f);
        bVar.e.setChecked(aVar.f());
        if (aVar.c()) {
            bVar.f3108d.setText(this.f3100b.getResources().getString(R.string.suggest_app_tolock));
            bVar.f3108d.setBackgroundResource(R.drawable.textview_border_green);
            bVar.f3108d.setTextColor(this.f3100b.getResources().getColor(R.color.auto_start_prog_runing_text_color));
        }
        return view;
    }

    public int getSectionCount() {
        return this.f3102d.size();
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = this.f3099a.inflate(R.layout.pm_auto_start_list_header_view, (ViewGroup) null);
            aVar = new a();
            TextView unused = aVar.f3103a = (TextView) view.findViewById(R.id.header_title);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        aVar.f3103a.setText(this.f3102d.get(i).b());
        return view;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        a((C0257a) compoundButton.getTag(), z);
    }
}
