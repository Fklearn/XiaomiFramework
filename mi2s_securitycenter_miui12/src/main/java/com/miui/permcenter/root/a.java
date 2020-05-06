package com.miui.permcenter.root;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import com.miui.securitycenter.R;

class a extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RootApplyActivity f6496a;

    a(RootApplyActivity rootApplyActivity) {
        this.f6496a = rootApplyActivity;
    }

    public void handleMessage(Message message) {
        Button button;
        int i;
        Button button2;
        int i2;
        Object[] objArr;
        RootApplyActivity rootApplyActivity;
        RootApplyActivity.b(this.f6496a);
        if (this.f6496a.f == 5 && this.f6496a.g == 0) {
            button = this.f6496a.f6490c;
            i = R.string.button_text_accept;
        } else if (this.f6496a.g == 0) {
            button = this.f6496a.f6490c;
            i = R.string.button_text_next_step;
        } else {
            if (this.f6496a.f == 5) {
                button2 = this.f6496a.f6490c;
                rootApplyActivity = this.f6496a;
                i2 = R.string.button_text_accept_timer;
                objArr = new Object[]{Integer.valueOf(rootApplyActivity.g)};
            } else {
                button2 = this.f6496a.f6490c;
                rootApplyActivity = this.f6496a;
                i2 = R.string.button_text_next_step_timer;
                objArr = new Object[]{Integer.valueOf(rootApplyActivity.g)};
            }
            button2.setText(rootApplyActivity.getString(i2, objArr));
            this.f6496a.h.removeMessages(100);
            this.f6496a.h.sendEmptyMessageDelayed(100, 1000);
            return;
        }
        button.setText(i);
        this.f6496a.f6490c.setEnabled(true);
    }
}
