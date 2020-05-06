package com.miui.antispam.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class AntiSpamEditorTitleView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Button f2630a;

    /* renamed from: b  reason: collision with root package name */
    private Button f2631b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f2632c;

    /* renamed from: d  reason: collision with root package name */
    private View f2633d;
    private Rect e = new Rect();

    public AntiSpamEditorTitleView(Context context) {
        super(context);
    }

    public AntiSpamEditorTitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public Button getCancel() {
        return this.f2631b;
    }

    public Button getOk() {
        return this.f2630a;
    }

    public TextView getTitle() {
        return this.f2632c;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2630a = (Button) findViewById(R.id.ok);
        this.f2631b = (Button) findViewById(R.id.cancel);
        this.f2632c = (TextView) findViewById(R.id.title);
        this.f2633d = findViewById(R.id.title_container);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f2633d.getHitRect(this.e);
            Rect rect = this.e;
            int i = rect.left;
            int i2 = rect.top;
            this.f2632c.getHitRect(rect);
            this.e.offset(i, i2);
            if (!this.e.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
