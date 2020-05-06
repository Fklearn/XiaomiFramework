package com.miui.gamebooster.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class ValueSettingItemView extends FrameLayout implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    private static int f5385a = 20;

    /* renamed from: b  reason: collision with root package name */
    private View f5386b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f5387c;

    /* renamed from: d  reason: collision with root package name */
    private View.OnClickListener f5388d;
    private float e = 0.0f;
    private float f = 0.0f;

    public ValueSettingItemView(@NonNull Context context) {
        super(context);
        a(context, (AttributeSet) null);
    }

    public ValueSettingItemView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet);
    }

    public ValueSettingItemView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context, attributeSet);
    }

    private void a(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        String str;
        String str2;
        View.inflate(context, R.layout.gb_value_setting_item_view, this);
        this.f5386b = findViewById(R.id.rootView);
        this.f5387c = (TextView) findViewById(R.id.value);
        if (attributeSet != null) {
            TextView textView = (TextView) findViewById(R.id.title);
            TextView textView2 = (TextView) findViewById(R.id.subtitle);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.GBSettingItemView);
            String str3 = null;
            if (obtainStyledAttributes != null) {
                str3 = obtainStyledAttributes.getString(4);
                str2 = obtainStyledAttributes.getString(3);
                str = obtainStyledAttributes.getString(5);
                obtainStyledAttributes.recycle();
            } else {
                str2 = null;
                str = null;
            }
            if (str3 != null) {
                textView.setText(str3);
            }
            if (str2 != null) {
                textView2.setText(str2);
            } else {
                textView2.setVisibility(8);
            }
            setValue(str);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.e = motionEvent.getX();
            this.f = motionEvent.getY();
            return false;
        } else if (action != 1) {
            return false;
        } else {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float abs = Math.abs(this.e - x);
            float abs2 = Math.abs(this.f - y);
            View.OnClickListener onClickListener = this.f5388d;
            if (onClickListener == null) {
                return false;
            }
            int i = f5385a;
            if (abs >= ((float) i) || abs2 >= ((float) i)) {
                return false;
            }
            onClickListener.onClick(this);
            return false;
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        setAlpha(z ? 1.0f : 0.2f);
        View view = this.f5386b;
        if (view != null) {
            view.setEnabled(z);
        }
    }

    public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
        this.f5388d = onClickListener;
        this.f5386b.setClickable(true);
        this.f5386b.setOnTouchListener(this);
    }

    public void setValue(String str) {
        TextView textView;
        int i;
        if (this.f5387c != null) {
            if (TextUtils.isEmpty(str)) {
                textView = this.f5387c;
                i = 8;
            } else {
                this.f5387c.setText(str);
                textView = this.f5387c;
                i = 0;
            }
            textView.setVisibility(i);
        }
    }
}
