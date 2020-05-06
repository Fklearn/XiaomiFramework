package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class TitleBarItemView extends RelativeLayout {
    private TextView mTitleView;

    public TitleBarItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TitleBarItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TitleBarItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View.inflate(context, R.layout.view_fixissue_title_toolbar_item, this);
        this.mTitleView = (TextView) findViewById(R.id.title);
    }

    public void setTitleViewText(String str) {
        this.mTitleView.setText(str);
    }
}
