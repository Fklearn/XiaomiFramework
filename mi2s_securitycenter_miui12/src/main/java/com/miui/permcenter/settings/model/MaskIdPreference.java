package com.miui.permcenter.settings.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;

public class MaskIdPreference extends Preference {
    private Context mContext;

    public MaskIdPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public MaskIdPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public MaskIdPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        Drawable drawable = this.mContext.getDrawable(R.drawable.pm_setting_icon_mask);
        if (drawable != null) {
            drawable.setAutoMirrored(true);
        }
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_44);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_40), dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_20));
        a2.itemView.setBackground(drawable);
    }
}
