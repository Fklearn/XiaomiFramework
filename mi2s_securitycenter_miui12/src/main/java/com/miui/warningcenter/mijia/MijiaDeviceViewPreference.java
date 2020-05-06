package com.miui.warningcenter.mijia;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;

public class MijiaDeviceViewPreference extends Preference {
    private Context mContext;

    public MijiaDeviceViewPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public MijiaDeviceViewPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.view_dimen_40);
        int dimensionPixelSize2 = getContext().getResources().getDimensionPixelSize(R.dimen.view_dimen_80);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize2, dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize);
    }
}
