package com.miui.gamebooster;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.miui.securitycenter.R;

public class DropDownShowWayAdapter extends ArrayAdapter {

    /* renamed from: a  reason: collision with root package name */
    private String[] f3993a;

    /* renamed from: b  reason: collision with root package name */
    private LayoutInflater f3994b;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f3995a;

        private a() {
        }
    }

    public DropDownShowWayAdapter(@NonNull Context context, AttributeSet attributeSet) {
        super(context, R.layout.miuix_compat_simple_spinner_layout, 16908308);
        this.f3994b = LayoutInflater.from(context);
        this.f3993a = new String[]{context.getString(R.string.gs_show_way_horizontal), context.getString(R.string.gs_show_way_vertical)};
    }

    public int getCount() {
        return this.f3993a.length;
    }

    public View getDropDownView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        if (view == null) {
            view = this.f3994b.inflate(R.layout.dropdown_demo_adapter_layout, viewGroup, false);
            a aVar = new a();
            aVar.f3995a = (TextView) view;
            view.setTag(aVar);
        }
        Object tag = view.getTag();
        if (tag != null) {
            ((a) tag).f3995a.setText((String) getItem(i));
        }
        return view;
    }

    @Nullable
    public Object getItem(int i) {
        return this.f3993a[i];
    }

    public long getItemId(int i) {
        return (long) i;
    }
}
