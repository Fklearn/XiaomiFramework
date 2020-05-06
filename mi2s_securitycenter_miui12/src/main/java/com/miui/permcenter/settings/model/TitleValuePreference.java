package com.miui.permcenter.settings.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;

public class TitleValuePreference extends Preference {
    private Context mContext;

    public TitleValuePreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public TitleValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public TitleValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        View view = a2.itemView;
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_80);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_48);
        a2.itemView.setPadding(dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize, dimensionPixelSize2);
        ((TextView) view.findViewById(R.id.title)).setText(getTitle());
        ((TextView) view.findViewById(R.id.value)).setText(getSummary());
    }
}
