package com.miui.gamebooster.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.h.f;
import com.miui.gamebooster.m.C0377h;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;

public class DataNetVideoPlayBtn extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    ViewGroup f4118a = ((ViewGroup) findViewById(R.id.play_btn_net_data));

    /* renamed from: b  reason: collision with root package name */
    TextView f4119b = ((TextView) findViewById(R.id.net_data_hint));

    /* renamed from: c  reason: collision with root package name */
    View f4120c = findViewById(R.id.play_btn_normal);

    public DataNetVideoPlayBtn(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        RelativeLayout.inflate(context, R.layout.data_net_video_play_btn, this);
    }

    public void a() {
        if (f.i(Application.d())) {
            this.f4120c.setVisibility(8);
            this.f4118a.setVisibility(0);
            return;
        }
        this.f4120c.setVisibility(0);
        this.f4118a.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        a();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setSize(long j) {
        this.f4119b.setText(C0377h.a(j));
    }

    public void setSize(String str) {
        this.f4119b.setText(str);
    }
}
