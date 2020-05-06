package com.miui.optimizemanage.settings;

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

public class DropDownLockScreenAdapter extends ArrayAdapter {

    /* renamed from: a  reason: collision with root package name */
    private String[] f5989a;

    /* renamed from: b  reason: collision with root package name */
    private LayoutInflater f5990b;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f5991a;

        private a() {
        }
    }

    public DropDownLockScreenAdapter(@NonNull Context context, AttributeSet attributeSet) {
        super(context, R.layout.miuix_compat_simple_spinner_layout, 16908308);
        this.f5990b = LayoutInflater.from(context);
        int[] intArray = context.getResources().getIntArray(R.array.pc_time_choice_items);
        this.f5989a = new String[intArray.length];
        int i = 0;
        while (true) {
            String[] strArr = this.f5989a;
            if (i < strArr.length) {
                strArr[i] = getMemoryCleanDelayTimeString(intArray[i]);
                i++;
            } else {
                return;
            }
        }
    }

    private String getMemoryCleanDelayTimeString(int i) {
        if (i == 0) {
            return getContext().getResources().getString(R.string.deep_clean_never_memory_clean);
        }
        return getContext().getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, i, new Object[]{Integer.valueOf(i)});
    }

    public int getCount() {
        return this.f5989a.length;
    }

    public View getDropDownView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        if (view == null) {
            view = this.f5990b.inflate(R.layout.dropdown_demo_adapter_layout, viewGroup, false);
            a aVar = new a();
            aVar.f5991a = (TextView) view;
            view.setTag(aVar);
        }
        Object tag = view.getTag();
        if (tag != null) {
            ((a) tag).f5991a.setText((String) getItem(i));
        }
        return view;
    }

    @Nullable
    public Object getItem(int i) {
        return this.f5989a[i];
    }

    public long getItemId(int i) {
        return (long) i;
    }
}
