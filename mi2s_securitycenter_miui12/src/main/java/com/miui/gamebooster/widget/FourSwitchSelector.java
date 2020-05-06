package com.miui.gamebooster.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FourSwitchSelector extends FrameLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f5356a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5357b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f5358c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f5359d;
    private a e;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Option {
    }

    public interface a {
        void a(FourSwitchSelector fourSwitchSelector, int i);
    }

    public FourSwitchSelector(@NonNull Context context) {
        super(context);
        a(context, (AttributeSet) null);
    }

    public FourSwitchSelector(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet);
    }

    public FourSwitchSelector(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context, attributeSet);
    }

    private void a(Context context, @Nullable AttributeSet attributeSet) {
        String str;
        String str2;
        String str3;
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        View.inflate(context, R.layout.gb_settings_four_switch_selector, this);
        this.f5356a = (TextView) findViewById(R.id.optionA);
        this.f5357b = (TextView) findViewById(R.id.optionB);
        this.f5358c = (TextView) findViewById(R.id.optionC);
        this.f5359d = (TextView) findViewById(R.id.optionD);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.GBFourSwitchSelector);
        String str4 = null;
        if (obtainStyledAttributes != null) {
            str4 = obtainStyledAttributes.getString(0);
            str3 = obtainStyledAttributes.getString(1);
            str2 = obtainStyledAttributes.getString(2);
            str = obtainStyledAttributes.getString(3);
            obtainStyledAttributes.recycle();
        } else {
            str3 = null;
            str2 = null;
            str = null;
        }
        if (!(str4 == null || (textView4 = this.f5356a) == null)) {
            textView4.setText(str4);
        }
        if (!(str3 == null || (textView3 = this.f5357b) == null)) {
            textView3.setText(str3);
        }
        if (!(str2 == null || (textView2 = this.f5358c) == null)) {
            textView2.setText(str2);
        }
        if (!(str == null || (textView = this.f5359d) == null)) {
            textView.setText(str);
        }
        for (View view : getAllOptions()) {
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    private View[] getAllOptions() {
        return new View[]{this.f5356a, this.f5357b, this.f5358c, this.f5359d};
    }

    public void onClick(View view) {
        int i = view == this.f5357b ? 1 : view == this.f5358c ? 2 : view == this.f5359d ? 3 : 0;
        for (View view2 : getAllOptions()) {
            if (view2 != null) {
                view2.setSelected(false);
            }
        }
        view.setSelected(true);
        a aVar = this.e;
        if (aVar != null) {
            aVar.a(this, i);
        }
    }

    public void setListener(a aVar) {
        this.e = aVar;
    }

    public void setOption(int i) {
        for (View view : getAllOptions()) {
            if (view != null) {
                view.setSelected(false);
            }
        }
        TextView textView = null;
        if (i == 0) {
            textView = this.f5356a;
        } else if (i == 1) {
            textView = this.f5357b;
        } else if (i == 2) {
            textView = this.f5358c;
        } else if (i == 3) {
            textView = this.f5359d;
        }
        if (textView != null) {
            textView.setSelected(true);
        }
    }
}
