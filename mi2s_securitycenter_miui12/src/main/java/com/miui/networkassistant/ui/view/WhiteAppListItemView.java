package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.j.i;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.ui.adapter.PinnedListAdapter;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class WhiteAppListItemView extends LinearLayout implements BindableView<WhiteListItem>, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    protected PinnedListAdapter.AppSelectionAdapterListener mAdapterListener;
    protected Context mContext;
    protected ImageView mIconView;
    protected WhiteListItem mListItem;
    protected SlidingButton mSlidingButton;
    protected TextView mSummaryView;
    protected TextView mTitleView;

    public WhiteAppListItemView(Context context) {
        this(context, (AttributeSet) null);
        this.mContext = context;
    }

    public WhiteAppListItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        this.mContext = context;
    }

    public WhiteAppListItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    private void setIconView(String str) {
        IconCacheHelper.getInstance().setIconToImageView(this.mIconView, str);
    }

    private void setSlidingButton(boolean z) {
        setOnClickListener((View.OnClickListener) null);
        this.mSlidingButton.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        this.mSlidingButton.setChecked(z);
        setOnClickListener(this);
        this.mSlidingButton.setOnCheckedChangeListener(this);
    }

    private void setTitleView(String str) {
        this.mTitleView.setText(str);
    }

    private void setTitleView(String str, String str2) {
        String str3 = str;
        if (str.toLowerCase().contains(str2.toLowerCase())) {
            int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
            String substring = str3.substring(indexOf, str2.length() + indexOf);
            boolean z = true;
            String format = String.format(getContext().getString(R.string.search_input_txt_na), new Object[]{substring});
            String[] strArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            int length = strArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (format.contains(strArr[i])) {
                    this.mTitleView.setText(Html.fromHtml(str3.replace(substring, format)));
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                this.mTitleView.setText(Html.fromHtml(str3.replaceFirst(substring, format)));
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return getTop() < i.a(getContext(), 20.0f) || super.dispatchTouchEvent(motionEvent);
    }

    public void fillData(WhiteListItem whiteListItem) {
        this.mListItem = whiteListItem;
        setIconView(whiteListItem.getPkgName());
        setTitleView(whiteListItem.getAppLabel());
        setSlidingButton(whiteListItem.isEnabled());
    }

    public void fillData(WhiteListItem whiteListItem, String str) {
        this.mListItem = whiteListItem;
        setIconView(whiteListItem.getPkgName());
        setTitleView(whiteListItem.getAppLabel(), str);
        setSlidingButton(whiteListItem.isEnabled());
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        PinnedListAdapter.AppSelectionAdapterListener appSelectionAdapterListener = this.mAdapterListener;
        if (appSelectionAdapterListener != null) {
            appSelectionAdapterListener.onAppSelected(compoundButton, this.mListItem, z);
        }
    }

    public void onClick(View view) {
        if (this.mAdapterListener != null) {
            this.mSlidingButton.setChecked(!this.mListItem.isEnabled());
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = (ImageView) findViewById(R.id.icon);
        this.mTitleView = (TextView) findViewById(R.id.title);
        this.mSummaryView = (TextView) findViewById(R.id.summary);
        this.mSlidingButton = findViewById(R.id.sliding_button);
    }

    public void setOnSelectionListener(PinnedListAdapter.AppSelectionAdapterListener appSelectionAdapterListener) {
        this.mAdapterListener = appSelectionAdapterListener;
    }
}
