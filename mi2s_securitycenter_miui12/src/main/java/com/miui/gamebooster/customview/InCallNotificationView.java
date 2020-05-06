package com.miui.gamebooster.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.gamebooster.gbservices.AntiMsgAccessibilityService;
import com.miui.securitycenter.R;

public class InCallNotificationView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f4136a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f4137b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f4138c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public AntiMsgAccessibilityService f4139d;

    public InCallNotificationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void a() {
        if (getVisibility() != 8) {
            setVisibility(8);
        }
    }

    public void a(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4139d = antiMsgAccessibilityService;
    }

    public void a(String str, String str2) {
        TextView textView = this.f4136a;
        if (str == null) {
            str = "";
        }
        textView.setText(str);
    }

    public void b() {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f4136a = (TextView) findViewById(R.id.caller_name);
        this.f4137b = (ImageView) findViewById(R.id.end_call_icon);
        this.f4138c = (ImageView) findViewById(R.id.answer_icon);
        this.f4137b.setOnClickListener(new C0351u(this));
        setOnClickListener(new C0352v(this));
    }
}
