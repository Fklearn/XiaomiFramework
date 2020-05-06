package com.miui.gamebooster.viewPointwidget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.gamead.y;
import com.miui.securitycenter.R;

public class ViewPointPicItem extends RelativeLayout implements d, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private String f5344a = ViewPointPicItem.class.getName();

    /* renamed from: b  reason: collision with root package name */
    private ImageView f5345b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f5346c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f5347d;
    private View e;
    private TextView f;
    private y g;
    private View h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private int n;
    private Bundle o;

    public ViewPointPicItem(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void a(View view, int i2) {
        Log.i(this.f5344a, view.toString());
        y yVar = this.g;
        if (yVar != null) {
            yVar.a();
            throw null;
        }
    }

    public void onClick(View view) {
        a(view, 0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5345b = (ImageView) findViewById(R.id.left_img);
        this.f5345b.setOnClickListener(this);
        this.f5347d = (ImageView) findViewById(R.id.middle_img);
        this.f5347d.setOnClickListener(this);
        this.f5346c = (ImageView) findViewById(R.id.right_img);
        this.h = findViewById(R.id.mask);
        this.e = findViewById(R.id.right_area);
        this.e.setOnClickListener(this);
        this.f = (TextView) findViewById(R.id.pic_count);
        this.l = getResources().getDimensionPixelSize(R.dimen.view_dimen_240);
        this.k = getResources().getDimensionPixelSize(R.dimen.view_dimen_295);
        this.n = getResources().getDimensionPixelSize(R.dimen.view_dimen_360);
        this.m = getResources().getDimensionPixelSize(R.dimen.view_dimen_452);
        this.j = getResources().getDimensionPixelSize(R.dimen.view_dimen_561);
        this.i = getResources().getDimensionPixelSize(R.dimen.view_dimen_924);
        this.o = new Bundle();
        this.o.putBoolean("report_activity_layer", false);
    }
}
