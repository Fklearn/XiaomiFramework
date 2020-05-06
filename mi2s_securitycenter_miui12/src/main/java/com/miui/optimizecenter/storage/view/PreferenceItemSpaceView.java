package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.miui.securitycenter.R;
import miui.text.ExtraTextUtils;

public class PreferenceItemSpaceView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private DeepCleanChartView f5801a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5802b;

    public PreferenceItemSpaceView(Context context) {
        super(context);
    }

    public PreferenceItemSpaceView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PreferenceItemSpaceView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a(long j, long j2) {
        DeepCleanChartView deepCleanChartView = this.f5801a;
        if (deepCleanChartView != null) {
            deepCleanChartView.a(j, j2, 0);
        }
        TextView textView = this.f5802b;
        if (textView != null) {
            textView.setText(getContext().getResources().getString(R.string.memory_title, new Object[]{ExtraTextUtils.formatFileSize(getContext(), j2), ExtraTextUtils.formatFileSize(getContext(), j)}));
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5801a = (DeepCleanChartView) findViewById(R.id.dccv);
        this.f5802b = (TextView) findViewById(R.id.storage_space);
    }
}
