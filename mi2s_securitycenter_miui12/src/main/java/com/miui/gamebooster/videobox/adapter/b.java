package com.miui.gamebooster.videobox.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.n.d.c;
import com.miui.gamebooster.videobox.view.DisplayStyleImageView;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class b extends BaseAdapter implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private List<com.miui.gamebooster.n.d.b> f5141a;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public FrameLayout f5142a;

        /* renamed from: b  reason: collision with root package name */
        public TextView f5143b;

        /* renamed from: c  reason: collision with root package name */
        public DisplayStyleImageView f5144c;

        public void a(c cVar, View.OnClickListener onClickListener) {
            this.f5142a.setVisibility(cVar == null ? 4 : 0);
            if (cVar != null) {
                this.f5142a.setTag(cVar);
                this.f5142a.setOnClickListener(onClickListener);
                boolean e = cVar.e();
                TextView textView = this.f5143b;
                if (textView != null) {
                    textView.setText(textView.getContext().getResources().getString(cVar.a()));
                    this.f5143b.setSelected(e);
                }
                if (this.f5144c != null && cVar.d() != 0) {
                    this.f5144c.setDrawBorder(e);
                    this.f5144c.setImageResource(cVar.d());
                }
            }
        }
    }

    /* renamed from: com.miui.gamebooster.videobox.adapter.b$b  reason: collision with other inner class name */
    public static class C0049b {

        /* renamed from: a  reason: collision with root package name */
        public a f5145a = new a();

        /* renamed from: b  reason: collision with root package name */
        public a f5146b = new a();

        public void a(c cVar, c cVar2, View.OnClickListener onClickListener) {
            this.f5145a.a(cVar, onClickListener);
            this.f5146b.a(cVar2, onClickListener);
        }
    }

    public b() {
        this.f5141a = new ArrayList();
        this.f5141a = com.miui.gamebooster.n.b.b.a(Application.d(), com.miui.gamebooster.n.c.a.DISPLAY_STYLE);
    }

    private void a(View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setAutoMirrored(true);
        }
    }

    public int getCount() {
        return (this.f5141a.size() / 2) + (this.f5141a.size() % 2);
    }

    public com.miui.gamebooster.n.d.b getItem(int i) {
        return this.f5141a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videobox_display_style_settings_item_layout, viewGroup, false);
            C0049b bVar = new C0049b();
            bVar.f5145a.f5142a = (FrameLayout) view.findViewById(R.id.item1);
            bVar.f5145a.f5144c = (DisplayStyleImageView) view.findViewById(R.id.img1);
            bVar.f5145a.f5143b = (TextView) view.findViewById(R.id.title1);
            a(bVar.f5145a.f5143b);
            bVar.f5146b.f5142a = (FrameLayout) view.findViewById(R.id.item2);
            bVar.f5146b.f5144c = (DisplayStyleImageView) view.findViewById(R.id.img2);
            bVar.f5146b.f5143b = (TextView) view.findViewById(R.id.title2);
            a(bVar.f5146b.f5143b);
            view.setTag(bVar);
        }
        C0049b bVar2 = (C0049b) view.getTag();
        int i2 = i * 2;
        int i3 = i2 + 1;
        if (bVar2 != null && i2 < this.f5141a.size()) {
            bVar2.a((c) this.f5141a.get(i2), i3 < this.f5141a.size() ? (c) this.f5141a.get(i3) : null, this);
        }
        return view;
    }

    public void onClick(View view) {
        if (view.getTag() == null || !(view.getTag() instanceof c)) {
            Log.e("DisplayStyleSettings", "Display style click failed.");
            return;
        }
        c cVar = (c) view.getTag();
        for (com.miui.gamebooster.n.d.b next : this.f5141a) {
            if (next instanceof c) {
                c cVar2 = (c) next;
                cVar2.a(cVar2.c() == cVar.c());
            }
        }
        cVar.a(view);
        notifyDataSetChanged();
        C0373d.a.a(cVar.c());
    }
}
