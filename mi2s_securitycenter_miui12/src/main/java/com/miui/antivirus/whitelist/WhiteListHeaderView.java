package com.miui.antivirus.whitelist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import b.b.c.i.b;
import com.miui.securitycenter.R;

public class WhiteListHeaderView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f3027a;

    public WhiteListHeaderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WhiteListHeaderView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WhiteListHeaderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f3027a = (TextView) findViewById(R.id.header_title);
    }

    public void setEventHandler(b bVar) {
    }
}
