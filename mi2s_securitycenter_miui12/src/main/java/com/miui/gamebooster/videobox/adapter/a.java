package com.miui.gamebooster.videobox.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.videobox.view.SlidingButton;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class a extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private List<com.miui.gamebooster.n.d.b> f5135a;

    /* renamed from: com.miui.gamebooster.videobox.adapter.a$a  reason: collision with other inner class name */
    public static class C0048a {

        /* renamed from: a  reason: collision with root package name */
        public ViewGroup f5136a;

        /* renamed from: b  reason: collision with root package name */
        public SlidingButton f5137b;

        /* renamed from: c  reason: collision with root package name */
        public ImageView f5138c;

        /* renamed from: d  reason: collision with root package name */
        public ImageView f5139d;
        public View e;

        public void a(com.miui.gamebooster.n.d.b bVar, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            if (bVar == null || !bVar.b()) {
                this.f5136a.setVisibility(8);
                return;
            }
            this.f5136a.setVisibility(0);
            SlidingButton slidingButton = this.f5137b;
            if (slidingButton != null) {
                slidingButton.setOnCheckedChangeListener(onCheckedChangeListener);
                this.f5137b.setChecked(((com.miui.gamebooster.n.d.a) bVar).g());
                this.f5137b.setTag(bVar);
            }
            ImageView imageView = this.f5138c;
            if (imageView != null && this.f5139d != null) {
                com.miui.gamebooster.n.d.a aVar = (com.miui.gamebooster.n.d.a) bVar;
                imageView.setImageResource(aVar.e());
                this.f5139d.setImageResource(aVar.d());
            }
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public C0048a f5140a = new C0048a();
    }

    public a() {
        this.f5135a = new ArrayList();
        this.f5135a = com.miui.gamebooster.n.b.b.a(Application.d(), com.miui.gamebooster.n.c.a.ADVANCED_SETTINGS);
    }

    public int getCount() {
        return 1;
    }

    public com.miui.gamebooster.n.d.b getItem(int i) {
        return this.f5135a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videobox_advanced_settings_item_layout, viewGroup, false);
            b bVar = new b();
            bVar.f5140a.f5138c = (ImageView) view.findViewById(R.id.img_left2);
            bVar.f5140a.f5139d = (ImageView) view.findViewById(R.id.img_right2);
            bVar.f5140a.f5137b = (SlidingButton) view.findViewById(R.id.sb_switch2);
            bVar.f5140a.f5136a = (ViewGroup) view.findViewById(R.id.item2);
            bVar.f5140a.e = view.findViewById(R.id.img_wrapper2);
            view.setTag(bVar);
        }
        if (view.getTag() != null && (view.getTag() instanceof b)) {
            ((b) view.getTag()).f5140a.a(this.f5135a.get(0), this);
        }
        return view;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton.getTag() == null || !(compoundButton.getTag() instanceof com.miui.gamebooster.n.d.a)) {
            Log.i("AdvancedSettingsAdapter", "Model can not be null and must be instance of AdvancedSettingsModel");
            return;
        }
        com.miui.gamebooster.n.d.a aVar = (com.miui.gamebooster.n.d.a) compoundButton.getTag();
        aVar.a(z);
        aVar.a((View) compoundButton);
        C0373d.a.a(aVar.c(), aVar.f());
    }
}
