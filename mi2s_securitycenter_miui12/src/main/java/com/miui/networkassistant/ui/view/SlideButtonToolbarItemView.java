package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class SlideButtonToolbarItemView extends RelativeLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ToolbarItemClickListener mListener;
    private TextView mNameView;
    private SlidingButton mSlidingButton;
    private TextView mSummaryView;

    public interface ToolbarItemClickListener {
        void onToolbarItemClick(View view, boolean z);
    }

    public SlideButtonToolbarItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlideButtonToolbarItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlideButtonToolbarItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View.inflate(context, R.layout.view_slidingbutton_toolbar_item, this);
        this.mNameView = (TextView) findViewById(R.id.name);
        this.mSummaryView = (TextView) findViewById(R.id.summary);
        this.mSlidingButton = findViewById(R.id.sliding_button);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        ToolbarItemClickListener toolbarItemClickListener = this.mListener;
        if (toolbarItemClickListener != null) {
            toolbarItemClickListener.onToolbarItemClick(compoundButton, z);
        }
    }

    public void onClick(View view) {
        if (this.mListener != null) {
            this.mSlidingButton.setChecked(!this.mSlidingButton.isChecked());
        }
    }

    public void setChecked(boolean z) {
        setOnClickListener((View.OnClickListener) null);
        this.mSlidingButton.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        this.mSlidingButton.setChecked(z);
        setOnClickListener(this);
        this.mSlidingButton.setOnCheckedChangeListener(this);
    }

    public void setName(int i) {
        this.mNameView.setText(i);
    }

    public void setName(String str) {
        this.mNameView.setText(str);
    }

    public void setSummary(String str) {
        this.mSummaryView.setText(str);
    }

    public void setSummaryVisibility(int i) {
        this.mSummaryView.setVisibility(i);
    }

    public void setToolbarItemClickListener(ToolbarItemClickListener toolbarItemClickListener) {
        this.mListener = toolbarItemClickListener;
    }

    public void setToolbarItemEnable(boolean z) {
        setEnabled(z);
        this.mSlidingButton.setEnabled(z);
    }
}
