package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class ButtonToolbarItemView extends RelativeLayout {
    private Button mButton;
    private ImageView mImageView;
    private ProgressBar mProgress;
    private TextView mSummaryView;
    private TextView mTitleView;

    public ButtonToolbarItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ButtonToolbarItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ButtonToolbarItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View.inflate(context, R.layout.view_fixissue_button_toolbar_item, this);
        this.mTitleView = (TextView) findViewById(R.id.title);
        this.mSummaryView = (TextView) findViewById(R.id.summary);
        this.mButton = (Button) findViewById(R.id.fix_button);
        this.mProgress = (ProgressBar) findViewById(R.id.item_waiting);
        this.mImageView = (ImageView) findViewById(R.id.item_success);
    }

    public Button getFixButton() {
        return this.mButton;
    }

    public ImageView getImageView() {
        return this.mImageView;
    }

    public ProgressBar getProgressBar() {
        return this.mProgress;
    }

    public void setFixButtonText(String str) {
        this.mButton.setText(str);
    }

    public void setSummaryText(String str) {
        this.mSummaryView.setText(Html.fromHtml(str));
    }

    public void setTitleViewText(String str) {
        this.mTitleView.setText(str);
    }
}
