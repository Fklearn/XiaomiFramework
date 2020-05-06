package com.miui.common.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AdImageView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private Handler f3765a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f3766b = -100;

    /* renamed from: c  reason: collision with root package name */
    private Handler f3767c = new a(this);

    public AdImageView(Context context) {
        super(context);
    }

    public AdImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AdImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a(Handler handler, int i, Object obj) {
        if (a() && this.f3766b != i) {
            this.f3766b = i;
            this.f3765a = handler;
            this.f3765a.removeMessages(this.f3766b);
            Handler handler2 = this.f3765a;
            handler2.sendMessageDelayed(Message.obtain(handler2, this.f3766b, obj), 1000);
        }
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        return true;
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.f3766b != -100) {
            this.f3767c.removeMessages(500);
        }
    }

    public void onStartTemporaryDetach() {
        int i;
        super.onStartTemporaryDetach();
        Handler handler = this.f3765a;
        if (handler != null && (i = this.f3766b) != -100) {
            handler.removeMessages(i);
            this.f3767c.sendEmptyMessageDelayed(500, 800);
        }
    }
}
