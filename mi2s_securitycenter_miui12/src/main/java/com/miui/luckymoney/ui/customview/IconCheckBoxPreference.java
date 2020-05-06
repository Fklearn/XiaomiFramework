package com.miui.luckymoney.ui.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class IconCheckBoxPreference extends Preference {
    private boolean mChecked;
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private Drawable mIcon;
    private String mTitle;

    public IconCheckBoxPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public IconCheckBoxPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public IconCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.item_lucky_alarm_setting);
    }

    private void initView(View view) {
        String str;
        Drawable drawable;
        ImageView imageView = (ImageView) view.findViewById(R.id.imgItemAlarmSetting);
        TextView textView = (TextView) view.findViewById(R.id.txvItemAlarmSetting);
        SlidingButton findViewById = view.findViewById(R.id.sliding_button_item_alarm_setting);
        if (!(imageView == null || (drawable = this.mIcon) == null)) {
            imageView.setImageDrawable(drawable);
        }
        if (!(textView == null || (str = this.mTitle) == null)) {
            textView.setText(str);
        }
        if (findViewById != null) {
            findViewById.setChecked(this.mChecked);
            findViewById.setOnCheckedChangeListener(this.mCheckedChangeListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        initView(view);
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        notifyChanged();
    }

    public void setSlidingButtonListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckedChangeListener = onCheckedChangeListener;
        notifyChanged();
    }

    public void setTextView(String str) {
        this.mTitle = str;
        notifyChanged();
    }
}
