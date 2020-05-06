package com.miui.gamebooster.videobox.adapter;

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

public class DropDownLocationAdapter extends ArrayAdapter {

    /* renamed from: a  reason: collision with root package name */
    private String[] f5132a;

    /* renamed from: b  reason: collision with root package name */
    private LayoutInflater f5133b;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f5134a;

        private a() {
        }
    }

    public DropDownLocationAdapter(@NonNull Context context, AttributeSet attributeSet) {
        super(context, R.layout.miuix_compat_simple_spinner_layout, 16908308);
        this.f5133b = LayoutInflater.from(context);
        this.f5132a = new String[]{context.getString(R.string.videobox_settings_line_location_left), context.getString(R.string.videobox_settings_line_location_right)};
    }

    public int getCount() {
        return this.f5132a.length;
    }

    public View getDropDownView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        if (view == null) {
            view = this.f5133b.inflate(R.layout.dropdown_demo_adapter_layout, viewGroup, false);
            a aVar = new a();
            aVar.f5134a = (TextView) view;
            view.setTag(aVar);
        }
        Object tag = view.getTag();
        if (tag != null) {
            ((a) tag).f5134a.setText((String) getItem(i));
        }
        return view;
    }

    @Nullable
    public Object getItem(int i) {
        return this.f5132a[i];
    }

    public long getItemId(int i) {
        return (long) i;
    }
}
