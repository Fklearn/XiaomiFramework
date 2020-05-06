package com.miui.gamebooster.viewPointwidget;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.customview.RoundImageView;
import com.miui.gamebooster.gamead.w;
import com.miui.gamebooster.gamead.x;
import com.miui.securitycenter.R;

public class ViewPointMutiButtomItem extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private RoundImageView f5340a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5341b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f5342c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f5343d;
    private int e;
    private x f;
    private w g;
    private Bundle h;

    public ViewPointMutiButtomItem(Context context) {
        super(context);
    }

    public ViewPointMutiButtomItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onClick(View view) {
        w wVar;
        int id = view.getId();
        if (id == R.id.avatar || id == R.id.nick_name) {
            x xVar = this.f;
            if (xVar != null) {
                xVar.a();
                throw null;
            }
        } else if (id == R.id.reply_count && (wVar = this.g) != null) {
            wVar.a();
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5340a = (RoundImageView) findViewById(R.id.avatar);
        this.f5340a.setOnClickListener(this);
        this.f5341b = (TextView) findViewById(R.id.nick_name);
        this.f5341b.setOnClickListener(this);
        this.f5342c = (TextView) findViewById(R.id.reply_count);
        this.f5342c.setOnClickListener(this);
        this.f5343d = (TextView) findViewById(R.id.read_count);
        this.e = getResources().getDimensionPixelSize(R.dimen.view_dimen_60);
        this.h = new Bundle();
        this.h.putBoolean("report_activity_layer", false);
    }
}
