package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class TrafficDragView extends LinearLayout {
    private TextView mTextView;

    public TrafficDragView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(1);
        float f = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        this.mTextView = new TextView(context);
        this.mTextView.setGravity(17);
        this.mTextView.setTextColor(-1);
        this.mTextView.setTextSize(14.0f);
        int i = (int) (f * 5.0f);
        this.mTextView.setPadding(0, i, 0, i);
        this.mTextView.setBackgroundResource(R.drawable.na_app_traffic_info_bg);
        layoutParams.gravity = 1;
        addView(this.mTextView, layoutParams);
    }

    public String getText() {
        return this.mTextView.getText().toString();
    }

    public void setText(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
    }
}
