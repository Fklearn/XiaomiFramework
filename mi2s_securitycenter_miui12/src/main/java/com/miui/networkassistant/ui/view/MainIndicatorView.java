package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class MainIndicatorView extends LinearLayout {
    private static final int INDICATOR_COUNT = 2;
    private static final int INDICATOR_ONE = 0;
    private static final int INDICATOR_TWO = 1;
    private TextView[] mIndicatorViews;

    public MainIndicatorView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainIndicatorView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainIndicatorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIndicatorViews = new TextView[2];
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIndicatorViews[0] = (TextView) findViewById(R.id.text_indicator1);
        this.mIndicatorViews[1] = (TextView) findViewById(R.id.text_indicator2);
    }

    public void setIndicatorImage(int i, int i2) {
        this.mIndicatorViews[i].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, i2, 0);
    }

    public void setMainIndicatorListener(View.OnClickListener onClickListener) {
        this.mIndicatorViews[0].setOnClickListener(onClickListener);
        this.mIndicatorViews[1].setOnClickListener(onClickListener);
    }

    public void setMainIndicatorTitle(int i, String str) {
        this.mIndicatorViews[i].setText(str);
    }

    public boolean toggleIndicator(int i) {
        for (TextView textColor : this.mIndicatorViews) {
            textColor.setTextColor(getResources().getColor(R.color.na_text_unchecked));
        }
        this.mIndicatorViews[i].setTextColor(getResources().getColor(R.color.na_nd_text));
        return true;
    }
}
