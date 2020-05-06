package com.miui.powercenter.legacypowerrank;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import b.b.c.j.C;
import com.miui.powercenter.utils.b;
import com.miui.powercenter.utils.j;
import com.miui.powercenter.utils.t;
import com.miui.powercenter.view.NoScrollListView;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.content.res.IconCustomizer;

public class h extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private boolean f7091a = true;

    /* renamed from: b  reason: collision with root package name */
    List<BatteryData> f7092b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private double f7093c = 0.0d;

    static class a {

        /* renamed from: a  reason: collision with root package name */
        final TextView f7094a;

        /* renamed from: b  reason: collision with root package name */
        final ImageView f7095b;

        /* renamed from: c  reason: collision with root package name */
        final ProgressBar f7096c;

        /* renamed from: d  reason: collision with root package name */
        final TextView f7097d;

        public a(View view) {
            this.f7094a = (TextView) view.findViewById(16908310);
            this.f7095b = (ImageView) view.findViewById(16908294);
            this.f7096c = (ProgressBar) view.findViewById(16908301);
            this.f7097d = (TextView) view.findViewById(16908308);
        }
    }

    private void a(ImageView imageView) {
        imageView.setImageDrawable(IconCustomizer.generateIconStyleDrawable(imageView.getContext().getPackageManager().getDefaultActivityIcon()));
    }

    public double a() {
        return this.f7093c;
    }

    public void a(double d2) {
        this.f7093c = d2;
    }

    public void a(List<BatteryData> list) {
        this.f7092b.clear();
        this.f7092b.addAll(list);
        notifyDataSetChanged();
    }

    public void a(boolean z) {
        this.f7091a = z;
        notifyDataSetChanged();
    }

    public void b() {
        this.f7092b.clear();
    }

    public int getCount() {
        return this.f7092b.size();
    }

    public Object getItem(int i) {
        return this.f7092b.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        String str;
        Context context = viewGroup.getContext();
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.pc_power_consume_list_item, (ViewGroup) null);
            aVar = new a(view);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        if ((viewGroup instanceof NoScrollListView) && ((NoScrollListView) viewGroup).f7345a) {
            return view;
        }
        BatteryData batteryData = (BatteryData) getItem(i);
        double d2 = 0.0d;
        if (this.f7093c != 0.0d) {
            d2 = 100.0d * (batteryData.getValue() / this.f7093c);
        }
        TextView textView = aVar.f7094a;
        textView.setText(b.a(textView.getContext(), batteryData));
        if (k.a() > 8) {
            aVar.f7094a.setTypeface(t.a(), 1);
        }
        aVar.f7096c.setMax(100);
        aVar.f7096c.setProgress((int) Math.round(d2));
        if (this.f7091a) {
            str = context.getResources().getString(R.string.percent_formatted_text, new Object[]{String.format(Locale.getDefault(), "%.2f", new Object[]{Double.valueOf(d2)})});
        } else {
            str = context.getResources().getString(R.string.power_center_list_item_battery_health_model_volume, new Object[]{String.format(Locale.getDefault(), "%.2f", new Object[]{Double.valueOf(batteryData.getValue())})});
        }
        aVar.f7097d.setText(str);
        int a2 = b.a(batteryData);
        if (a2 > 0) {
            b.a(aVar.f7095b, a2);
        } else if (TextUtils.isEmpty(batteryData.getPackageName())) {
            a(aVar.f7095b);
        } else if (C.b(j.a(batteryData.uid))) {
            aVar.f7095b.setImageDrawable(C.a(aVar.f7095b.getContext(), new BitmapDrawable(context.getResources(), b.a(batteryData.getPackageName())), batteryData.uid));
        } else {
            b.a(aVar.f7095b, batteryData.getPackageName());
        }
        return view;
    }
}
