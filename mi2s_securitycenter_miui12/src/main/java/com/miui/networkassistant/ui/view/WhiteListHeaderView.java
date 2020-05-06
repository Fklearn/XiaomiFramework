package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.networkassistant.model.WhiteGroupHeader;
import com.miui.securitycenter.R;

public class WhiteListHeaderView extends FrameLayout implements BindableView<WhiteGroupHeader> {
    private TextView mHeaderTitleView;

    public WhiteListHeaderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WhiteListHeaderView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WhiteListHeaderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void fillData(WhiteGroupHeader whiteGroupHeader) {
        this.mHeaderTitleView.setText(whiteGroupHeader.getHeaderTitle());
    }

    public void fillData(WhiteGroupHeader whiteGroupHeader, String str) {
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderTitleView = (TextView) findViewById(R.id.header_title);
    }
}
