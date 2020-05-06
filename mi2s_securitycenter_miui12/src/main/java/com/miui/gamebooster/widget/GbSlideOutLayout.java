package com.miui.gamebooster.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.miui.gamebooster.m.C0373d;
import com.miui.securitycenter.R;

public class GbSlideOutLayout extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Button f5360a;

    /* renamed from: b  reason: collision with root package name */
    private Button f5361b;

    /* renamed from: c  reason: collision with root package name */
    private Rect f5362c;

    /* renamed from: d  reason: collision with root package name */
    private Rect f5363d;
    private Rect e;
    private Point f;

    public enum a {
        EVENT_MOVE,
        EVENT_DOWN,
        EVENT_UP
    }

    public GbSlideOutLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public GbSlideOutLayout(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GbSlideOutLayout(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f5362c = new Rect();
        this.f5363d = new Rect();
        this.e = new Rect();
        this.f5362c = new Rect();
        this.f5363d = new Rect();
        this.f = new Point(-1, -1);
    }

    private void a() {
        Rect rect = this.e;
        Point point = this.f;
        setVisibility(rect.contains(point.x, point.y) ? 0 : 4);
    }

    private void a(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("track_gamebooster_enter_way", "00003");
        bundle.putString("jump_target", "gamebox");
        bundle.putString("caller_channel", "gb_toolbox");
        Intent intent = new Intent("com.miui.gamebooster.action.ACCESS_MAINACTIVITY");
        intent.putExtras(bundle);
        intent.addFlags(268435456);
        try {
            context.startActivity(intent);
        } catch (Exception unused) {
        }
    }

    private void b() {
        Rect rect = this.e;
        Point point = this.f;
        if (rect.contains(point.x, point.y)) {
            setVisibility(0);
            C0373d.d("show");
            return;
        }
        setVisibility(4);
    }

    private void b(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addFlags(268435456);
        try {
            context.startActivity(intent);
        } catch (Exception unused) {
        }
    }

    public boolean a(a aVar, int i, int i2) {
        this.f.set(i - getLeft(), i2 - getTop());
        StringBuilder sb = new StringBuilder();
        sb.append("onGameToolBoxTouchEvent: ");
        sb.append(this.f);
        sb.append("\tcurRect=");
        sb.append(this.e);
        sb.append("\tcontain=");
        Rect rect = this.e;
        Point point = this.f;
        sb.append(rect.contains(point.x, point.y));
        sb.append("\teventType=");
        sb.append(aVar);
        Log.i("GbSlideOutView", sb.toString());
        int i3 = a.f5389a[aVar.ordinal()];
        if (i3 == 1 || i3 == 2) {
            a();
        } else if (i3 == 3) {
            b();
        }
        Rect rect2 = this.e;
        Point point2 = this.f;
        return rect2.contains(point2.x, point2.y);
    }

    public void onClick(View view) {
        String str;
        switch (view.getId()) {
            case R.id.btn_back_gamebox /*2131296542*/:
                a(view.getContext());
                str = "enter_game_toolbox";
                break;
            case R.id.btn_back_home /*2131296543*/:
                b(view.getContext());
                str = "back_to_home";
                break;
            default:
                return;
        }
        C0373d.d(str);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5360a = (Button) findViewById(R.id.btn_back_home);
        this.f5361b = (Button) findViewById(R.id.btn_back_gamebox);
        this.f5360a.setOnClickListener(this);
        this.f5361b.setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.f5362c.set(this.f5360a.getLeft(), this.f5360a.getTop(), this.f5360a.getRight(), this.f5360a.getBottom());
        this.f5363d.set(this.f5361b.getLeft(), this.f5361b.getTop(), this.f5361b.getRight(), this.f5361b.getBottom());
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.e.set(0, 0, i, i2);
    }
}
