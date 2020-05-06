package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class MainToolbarItemView extends LinearLayout {
    private TextView mDescView;
    private ImageView mIconImageView;
    private TextView mNameView;
    private View mTipsView;

    public MainToolbarItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainToolbarItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainToolbarItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public String getDesc() {
        return String.valueOf(this.mDescView.getText());
    }

    public String getName() {
        return String.valueOf(this.mNameView.getText());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mNameView = (TextView) findViewById(R.id.name);
        this.mDescView = (TextView) findViewById(R.id.desc);
        this.mIconImageView = (ImageView) findViewById(R.id.icon);
        this.mTipsView = findViewById(R.id.tips);
    }

    public void setDesc(int i) {
        this.mDescView.setText(i);
    }

    public void setDesc(String str) {
        this.mDescView.setText(str);
    }

    public void setDescFromHtml(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mDescView.setText(Html.fromHtml(str));
        } else {
            this.mDescView.setText((CharSequence) null);
        }
    }

    public void setIcon(int i) {
        this.mIconImageView.setImageDrawable(this.mIconImageView.getResources().getDrawable(i));
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

    public void setTipsVisible(boolean z) {
        this.mTipsView.setVisibility(z ? 0 : 8);
    }
}
