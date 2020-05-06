package com.miui.gamebooster.viewPointwidget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.gamead.v;
import com.miui.securitycenter.R;

public class ViewPointCommentItem extends RelativeLayout implements d {

    /* renamed from: a  reason: collision with root package name */
    private String f5330a = ViewPointCommentItem.class.getName();

    /* renamed from: b  reason: collision with root package name */
    protected TextView f5331b;

    /* renamed from: c  reason: collision with root package name */
    protected ShowTextCountTextView f5332c;

    /* renamed from: d  reason: collision with root package name */
    protected v f5333d;
    protected Bundle e;
    protected int f;

    public ViewPointCommentItem(Context context) {
        super(context);
    }

    public ViewPointCommentItem(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void a(View view, int i) {
        Log.i(this.f5330a, view.toString());
        v vVar = this.f5333d;
        if (vVar != null) {
            vVar.a();
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5331b = (TextView) findViewById(R.id.short_comment);
        this.f5331b.setOnClickListener(new e(this));
        this.f5332c = (ShowTextCountTextView) findViewById(R.id.comment);
        this.f5332c.setOnClickListener(new f(this));
        this.e = new Bundle();
        this.e.putBoolean("report_activity_layer", false);
    }
}
