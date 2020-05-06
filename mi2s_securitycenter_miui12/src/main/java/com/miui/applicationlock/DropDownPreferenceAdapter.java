package com.miui.applicationlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.h;
import com.miui.securitycenter.R;
import java.util.Arrays;
import miuix.preference.z;

public class DropDownPreferenceAdapter extends ArrayAdapter {

    /* renamed from: a  reason: collision with root package name */
    private CharSequence[] f3156a;

    /* renamed from: b  reason: collision with root package name */
    private LayoutInflater f3157b;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f3158a;

        private a() {
        }
    }

    public DropDownPreferenceAdapter(@NonNull Context context, AttributeSet attributeSet) {
        super(context, R.layout.miuix_compat_simple_spinner_layout, 16908308);
        this.f3157b = LayoutInflater.from(context);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, z.DropDownPreference);
            this.f3156a = h.d(obtainStyledAttributes, 1, 0);
            obtainStyledAttributes.recycle();
            return;
        }
        this.f3156a = new CharSequence[]{"DropDown1", "DropDown2", "DropDown3"};
    }

    public void addAll(Object[] objArr) {
        CharSequence[] charSequenceArr = this.f3156a;
        CharSequence[] charSequenceArr2 = (CharSequence[]) Arrays.copyOf(charSequenceArr, charSequenceArr.length + objArr.length);
        System.arraycopy(objArr, 0, charSequenceArr2, this.f3156a.length, objArr.length);
        this.f3156a = charSequenceArr2;
    }

    public void clear() {
        this.f3156a = new CharSequence[0];
    }

    public int getCount() {
        return this.f3156a.length;
    }

    public View getDropDownView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        if (view == null) {
            view = this.f3157b.inflate(R.layout.dropdown_demo_adapter_layout, viewGroup, false);
            a aVar = new a();
            aVar.f3158a = (TextView) view;
            view.setTag(aVar);
        }
        Object tag = view.getTag();
        if (tag != null) {
            ((a) tag).f3158a.setText((CharSequence) getItem(i));
        }
        return view;
    }

    @Nullable
    public Object getItem(int i) {
        return this.f3156a[i];
    }

    public long getItemId(int i) {
        return (long) i;
    }
}
