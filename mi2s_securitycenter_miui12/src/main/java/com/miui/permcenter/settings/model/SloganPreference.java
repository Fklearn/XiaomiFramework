package com.miui.permcenter.settings.model;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;

public class SloganPreference extends Preference {
    private Context mContext;

    public SloganPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public SloganPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        a2.itemView.setBackgroundResource(R.drawable.pm_slogan_bg_color);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_44);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_45);
        a2.itemView.setPadding(dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize, dimensionPixelSize2);
        int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_36);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize3, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_40), dimensionPixelSize3, 0);
    }
}
