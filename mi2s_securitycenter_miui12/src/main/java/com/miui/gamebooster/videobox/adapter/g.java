package com.miui.gamebooster.videobox.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.n.d.d;
import com.miui.gamebooster.videobox.view.SrsLevelSeekBarPro;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class g extends BaseAdapter implements SrsLevelSeekBarPro.a {

    /* renamed from: a  reason: collision with root package name */
    private static List<com.miui.gamebooster.n.d.b> f5161a = new ArrayList();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public ViewGroup f5162a;

        /* renamed from: b  reason: collision with root package name */
        public ImageView f5163b;

        /* renamed from: c  reason: collision with root package name */
        public SrsLevelSeekBarPro f5164c;

        public void a(com.miui.gamebooster.n.d.b bVar, SrsLevelSeekBarPro.a aVar) {
            if (bVar == null || !bVar.b()) {
                ViewGroup viewGroup = this.f5162a;
                if (viewGroup != null) {
                    viewGroup.setVisibility(8);
                    return;
                }
                return;
            }
            ViewGroup viewGroup2 = this.f5162a;
            if (viewGroup2 != null) {
                viewGroup2.setVisibility(0);
            }
            if (this.f5163b != null) {
                d dVar = (d) bVar;
                if (dVar.d() != 0) {
                    this.f5163b.setImageResource(dVar.d());
                }
            }
            SrsLevelSeekBarPro srsLevelSeekBarPro = this.f5164c;
            if (srsLevelSeekBarPro != null) {
                srsLevelSeekBarPro.setLevelChangeListener(aVar);
                this.f5164c.setCurrentLevel(((d) bVar).f());
                this.f5164c.setTag(bVar);
            }
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public a f5165a = new a();

        /* renamed from: b  reason: collision with root package name */
        public a f5166b = new a();
    }

    public g() {
        f5161a = com.miui.gamebooster.n.b.b.a(Application.d(), com.miui.gamebooster.n.c.a.SRS_PREMIUM_SOUND);
    }

    public void a(SrsLevelSeekBarPro srsLevelSeekBarPro, int i) {
        if (srsLevelSeekBarPro.getTag() == null || !(srsLevelSeekBarPro.getTag() instanceof d)) {
            Log.i("SrsSettingsAdapter", "Model can not be null and must instance of SrsSettingsModel");
            return;
        }
        d dVar = (d) srsLevelSeekBarPro.getTag();
        dVar.a(i);
        dVar.a((View) srsLevelSeekBarPro);
        C0373d.a.a(dVar.c(), dVar.e());
    }

    public int getCount() {
        return 1;
    }

    public com.miui.gamebooster.n.d.b getItem(int i) {
        return f5161a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videobox_srs_settings_item_layout, viewGroup, false);
            b bVar = new b();
            bVar.f5165a.f5162a = (ViewGroup) view.findViewById(R.id.item1);
            bVar.f5165a.f5163b = (ImageView) view.findViewById(R.id.img1);
            bVar.f5165a.f5164c = (SrsLevelSeekBarPro) view.findViewById(R.id.seekbar_level1);
            bVar.f5166b.f5162a = (ViewGroup) view.findViewById(R.id.item2);
            bVar.f5166b.f5163b = (ImageView) view.findViewById(R.id.img2);
            bVar.f5166b.f5164c = (SrsLevelSeekBarPro) view.findViewById(R.id.seekbar_level2);
            view.setTag(bVar);
        }
        if (view.getTag() != null && (view.getTag() instanceof b)) {
            ((b) view.getTag()).f5165a.a(f5161a.get(0), this);
            ((b) view.getTag()).f5166b.a(f5161a.get(1), this);
        }
        return view;
    }
}
