package com.miui.powercenter.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class PowerCenterEditorTitleView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Button f7346a;

    /* renamed from: b  reason: collision with root package name */
    private Button f7347b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f7348c;

    /* renamed from: d  reason: collision with root package name */
    private View f7349d;
    private Rect e = new Rect();

    public PowerCenterEditorTitleView(Context context) {
        super(context);
    }

    public PowerCenterEditorTitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public Button getCancel() {
        return this.f7347b;
    }

    public Button getOk() {
        return this.f7346a;
    }

    public TextView getTitle() {
        return this.f7348c;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f7346a = (Button) findViewById(R.id.ok);
        this.f7347b = (Button) findViewById(R.id.cancel);
        this.f7348c = (TextView) findViewById(R.id.title);
        this.f7349d = findViewById(R.id.title_container);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f7349d.getHitRect(this.e);
            Rect rect = this.e;
            int i = rect.left;
            int i2 = rect.top;
            this.f7348c.getHitRect(rect);
            this.e.offset(i, i2);
            if (!this.e.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
