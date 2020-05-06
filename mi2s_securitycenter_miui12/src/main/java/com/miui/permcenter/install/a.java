package com.miui.permcenter.install;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import com.miui.securitycenter.R;

class a extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AdbInputApplyActivity f6140a;

    a(AdbInputApplyActivity adbInputApplyActivity) {
        this.f6140a = adbInputApplyActivity;
    }

    public void handleMessage(Message message) {
        Button button;
        int i;
        Button button2;
        int i2;
        Object[] objArr;
        AdbInputApplyActivity adbInputApplyActivity;
        AdbInputApplyActivity.b(this.f6140a);
        if (this.f6140a.f6114d == 3 && this.f6140a.e == 0) {
            button = this.f6140a.f6113c;
            i = R.string.button_text_accept;
        } else if (this.f6140a.e == 0) {
            button = this.f6140a.f6113c;
            i = R.string.button_text_next_step;
        } else {
            if (this.f6140a.f6114d == 3) {
                button2 = this.f6140a.f6113c;
                adbInputApplyActivity = this.f6140a;
                i2 = R.string.button_text_accept_timer;
                objArr = new Object[]{Integer.valueOf(adbInputApplyActivity.e)};
            } else {
                button2 = this.f6140a.f6113c;
                adbInputApplyActivity = this.f6140a;
                i2 = R.string.button_text_next_step_timer;
                objArr = new Object[]{Integer.valueOf(adbInputApplyActivity.e)};
            }
            button2.setText(adbInputApplyActivity.getString(i2, objArr));
            this.f6140a.f.removeMessages(100);
            this.f6140a.f.sendEmptyMessageDelayed(100, 1000);
            return;
        }
        button.setText(i);
        this.f6140a.f6113c.setEnabled(true);
    }
}
