package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class ToolbarItemView extends RelativeLayout {
    private TextView mDescView;
    private TextView mNameView;

    public ToolbarItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ToolbarItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ToolbarItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View.inflate(context, R.layout.view_toolbar_item, this);
        this.mNameView = (TextView) findViewById(R.id.name);
        this.mDescView = (TextView) findViewById(R.id.desc);
    }

    public String getDesc() {
        return String.valueOf(this.mDescView.getText().toString().trim());
    }

    public String getName() {
        return String.valueOf(this.mNameView.getText().toString().trim());
    }

    public void setDesc(int i) {
        this.mDescView.setText(i);
    }

    public void setDesc(String str) {
        this.mDescView.setText(str);
    }

    public void setDescFromHtml(String str) {
        this.mDescView.setText(Html.fromHtml(str));
    }

    public void setItemEnabled(boolean z) {
        setEnabled(z);
        float f = 1.0f;
        this.mNameView.setAlpha(z ? 1.0f : 0.7f);
        TextView textView = this.mDescView;
        if (!z) {
            f = 0.7f;
        }
        textView.setAlpha(f);
    }

    public void setName(int i) {
        this.mNameView.setText(i);
    }

    public void setName(String str) {
        this.mNameView.setText(str);
    }

    public void setRightArrowGone() {
        this.mDescView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
    }
}
