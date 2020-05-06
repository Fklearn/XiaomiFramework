package com.miui.gamebooster.m;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class ca extends ClickableSpan {

    /* renamed from: a  reason: collision with root package name */
    private long f4477a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4478b;

    public ca(Context context, long j) {
        this.f4477a = j;
        this.f4478b = context;
    }

    public void onClick(View view) {
    }

    public void updateDrawState(TextPaint textPaint) {
        textPaint.setUnderlineText(false);
    }
}
