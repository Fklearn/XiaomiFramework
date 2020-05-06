package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.networkassistant.ui.view.RadioCheckable;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class RadioTextView extends RelativeLayout implements RadioCheckable, View.OnClickListener {
    public static final int DEFAULT_TEXT_COLOR = 0;
    private boolean mChecked;
    private ImageView mIcon;
    private ArrayList<RadioCheckable.OnCheckedChangeListener> mOnCheckedChangeListeners = new ArrayList<>();
    private View.OnClickListener mOnClickListener;
    private int mPressedTextColor;
    private String mSummary;
    private int mSummaryTextColor;
    private TextView mSummaryTextView;
    private String mTitle;
    private int mTitleTextColor;
    private TextView mTitleTextView;

    public RadioTextView(Context context) {
        super(context);
        setupView();
    }

    public RadioTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupView();
    }

    public RadioTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupView();
    }

    public RadioTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setupView();
    }

    private void setupView() {
        inflateView();
        bindView();
    }

    public void addOnCheckChangeListener(RadioCheckable.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListeners.add(onCheckedChangeListener);
    }

    /* access modifiers changed from: protected */
    public void bindView() {
        int i = this.mSummaryTextColor;
        if (i != 0) {
            this.mSummaryTextView.setTextColor(i);
        }
        int i2 = this.mTitleTextColor;
        if (i2 != 0) {
            this.mTitleTextView.setTextColor(i2);
        }
        this.mSummaryTextView.setText(this.mSummary);
        this.mTitleTextView.setText(this.mTitle);
    }

    public String getSummary() {
        return this.mSummary;
    }

    public String getTitle() {
        return this.mTitle;
    }

    /* access modifiers changed from: protected */
    public void inflateView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_radio_text_view, this, true);
        this.mTitleTextView = (TextView) findViewById(R.id.title);
        this.mSummaryTextView = (TextView) findViewById(R.id.summary);
        this.mIcon = (ImageView) findViewById(R.id.icon);
        this.mTitleTextColor = getResources().getColor(R.color.na_nd_text);
        this.mSummaryTextColor = getResources().getColor(R.color.na_nd_text_sub);
        this.mPressedTextColor = getResources().getColor(R.color.na_action_bar_blue);
        super.setOnClickListener(this);
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void onClick(View view) {
        setChecked(true);
        View.OnClickListener onClickListener = this.mOnClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void removeOnCheckChangeListener(RadioCheckable.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListeners.remove(onCheckedChangeListener);
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            if (!this.mOnCheckedChangeListeners.isEmpty()) {
                for (int i = 0; i < this.mOnCheckedChangeListeners.size(); i++) {
                    this.mOnCheckedChangeListeners.get(i).onCheckedChanged(this, this.mChecked);
                }
            }
            if (this.mChecked) {
                setCheckedState();
            } else {
                setNormalState();
            }
        }
    }

    public void setCheckedState() {
        this.mTitleTextView.setTextColor(this.mPressedTextColor);
        this.mIcon.setVisibility(0);
    }

    public void setIcon(int i) {
        this.mIcon.setImageResource(i);
    }

    public void setNormalState() {
        this.mTitleTextView.setTextColor(this.mTitleTextColor);
        this.mSummaryTextView.setTextColor(this.mSummaryTextColor);
        this.mIcon.setVisibility(8);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setSummary(String str) {
        this.mSummary = str;
        this.mSummaryTextView.setText(this.mSummary);
    }

    public void setTitle(String str) {
        this.mTitle = str;
        this.mTitleTextView.setText(this.mTitle);
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }
}
