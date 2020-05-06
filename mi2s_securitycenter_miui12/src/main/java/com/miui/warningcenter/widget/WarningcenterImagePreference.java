package com.miui.warningcenter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;

public class WarningcenterImagePreference extends Preference {
    private int mDefaultHeight = 449;
    private int mResId;

    public WarningcenterImagePreference(Context context) {
        super(context);
    }

    public WarningcenterImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public WarningcenterImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.view_dimen_40);
        int dimensionPixelSize2 = getContext().getResources().getDimensionPixelSize(R.dimen.view_dimen_80);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize2, dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize);
        ImageView imageView = (ImageView) a2.itemView.findViewById(R.id.image_view);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(-1, this.mDefaultHeight));
        int i = this.mResId;
        if (i != 0) {
            imageView.setImageResource(i);
        }
    }

    public void setResource(int i) {
        this.mResId = i;
    }
}
