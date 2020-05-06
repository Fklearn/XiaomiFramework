package com.miui.gamebooster.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class CheckBoxSettingItemView extends FrameLayout implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    private static int f5352a = 20;

    /* renamed from: b  reason: collision with root package name */
    private View f5353b;

    /* renamed from: c  reason: collision with root package name */
    private LinearLayout f5354c;

    /* renamed from: d  reason: collision with root package name */
    private SwitchButton f5355d;
    private a e;
    private float f = 0.0f;
    private float g = 0.0f;

    public interface a {
        void onCheckedChanged(View view, boolean z);
    }

    public CheckBoxSettingItemView(@NonNull Context context) {
        super(context);
        a(context, (AttributeSet) null);
    }

    public CheckBoxSettingItemView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet);
    }

    public CheckBoxSettingItemView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context, attributeSet);
    }

    private void a(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        boolean z;
        boolean z2;
        boolean z3;
        Drawable drawable;
        String str;
        String str2;
        int i;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.GBSettingItemView);
            if (obtainStyledAttributes != null) {
                i = obtainStyledAttributes.getResourceId(2, 0);
                str2 = obtainStyledAttributes.getString(4);
                str = obtainStyledAttributes.getString(3);
                drawable = obtainStyledAttributes.getDrawable(1);
                z3 = obtainStyledAttributes.getBoolean(0, false);
                z2 = obtainStyledAttributes.getBoolean(6, true);
                z = obtainStyledAttributes.getBoolean(7, true);
                obtainStyledAttributes.recycle();
            } else {
                str2 = null;
                str = null;
                drawable = null;
                z2 = true;
                i = 0;
                z3 = false;
                z = false;
            }
            if (i == 0) {
                View.inflate(context, R.layout.gb_checkbox_setting_item_view, this);
            } else {
                View.inflate(context, i, this);
            }
            this.f5353b = findViewById(R.id.rootView);
            if (this.f5353b != null) {
                this.f5354c = (LinearLayout) findViewById(R.id.second_root);
                this.f5353b.setClickable(true);
                this.f5353b.setOnTouchListener(this);
                this.f5355d = (SwitchButton) findViewById(R.id.switchBtn);
                SwitchButton switchButton = this.f5355d;
                if (switchButton != null) {
                    switchButton.setOnCheckedChangeListener(this);
                }
                TextView textView = (TextView) findViewById(R.id.title);
                TextView textView2 = (TextView) findViewById(R.id.subtitle);
                if (!(textView == null || str2 == null)) {
                    textView.setText(str2);
                }
                if (textView2 != null) {
                    if (str != null) {
                        if (z) {
                            textView2.setSingleLine(false);
                        }
                        textView2.setText(str);
                    } else {
                        textView2.setVisibility(8);
                    }
                }
                if (drawable != null) {
                    ImageView imageView = (ImageView) findViewById(R.id.bottomImg);
                    if (imageView != null) {
                        imageView.setImageDrawable(drawable);
                        imageView.setVisibility(0);
                    }
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin - getResources().getDimensionPixelSize(R.dimen.view_dimen_14), layoutParams.rightMargin, layoutParams.bottomMargin);
                }
                if (z3) {
                    this.f5353b.setPadding(0, 0, 0, 0);
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.f5353b.getLayoutParams();
                    layoutParams2.setMargins(0, 0, 0, 0);
                    this.f5353b.setLayoutParams(layoutParams2);
                }
                if (!z2) {
                    this.f5353b.setMinimumHeight(0);
                    LinearLayout linearLayout = this.f5354c;
                    if (linearLayout != null) {
                        linearLayout.setMinimumHeight(0);
                        return;
                    }
                    return;
                }
                return;
            }
            throw new NullPointerException("The view which id is rootView can not be null");
        }
    }

    public void a() {
        SwitchButton switchButton = this.f5355d;
        if (switchButton != null) {
            switchButton.setChecked(!switchButton.isChecked());
        }
    }

    public void a(boolean z, boolean z2, boolean z3) {
        if (z2) {
            if (z3) {
                this.f5355d.setChecked(z);
            } else {
                this.f5355d.setCheckedNoEvent(z);
            }
        } else if (z3) {
            this.f5355d.setCheckedImmediately(z);
        } else {
            this.f5355d.setCheckedImmediatelyNoEvent(z);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        a aVar = this.e;
        if (aVar != null) {
            aVar.onCheckedChanged(this, z);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.f = motionEvent.getX();
            this.g = motionEvent.getY();
            return false;
        } else if (action != 1) {
            return false;
        } else {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float abs = Math.abs(this.f - x);
            float abs2 = Math.abs(this.g - y);
            int i = f5352a;
            if (abs >= ((float) i) || abs2 >= ((float) i)) {
                return false;
            }
            a();
            return false;
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        setAlpha(z ? 1.0f : 0.2f);
        for (View view : new View[]{this.f5353b, this.f5355d}) {
            if (view != null) {
                view.setEnabled(z);
            }
        }
    }

    public void setOnCheckedChangeListener(a aVar) {
        this.e = aVar;
    }
}
