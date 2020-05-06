package com.miui.permcenter.install;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import com.miui.securitycenter.R;

class e extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DeviceManagerApplyActivity f6147a;

    e(DeviceManagerApplyActivity deviceManagerApplyActivity) {
        this.f6147a = deviceManagerApplyActivity;
    }

    public void handleMessage(Message message) {
        Button button;
        int i;
        Button button2;
        int i2;
        Object[] objArr;
        DeviceManagerApplyActivity deviceManagerApplyActivity;
        DeviceManagerApplyActivity.b(this.f6147a);
        if (this.f6147a.f6127d == 3 && this.f6147a.e == 0) {
            button = this.f6147a.f6126c;
            i = R.string.button_text_accept;
        } else if (this.f6147a.e == 0) {
            button = this.f6147a.f6126c;
            i = R.string.button_text_next_step;
        } else {
            if (this.f6147a.f6127d == 3) {
                button2 = this.f6147a.f6126c;
                deviceManagerApplyActivity = this.f6147a;
                i2 = R.string.button_text_accept_timer;
                objArr = new Object[]{Integer.valueOf(deviceManagerApplyActivity.e)};
            } else {
                button2 = this.f6147a.f6126c;
                deviceManagerApplyActivity = this.f6147a;
                i2 = R.string.button_text_next_step_timer;
                objArr = new Object[]{Integer.valueOf(deviceManagerApplyActivity.e)};
            }
            button2.setText(deviceManagerApplyActivity.getString(i2, objArr));
            this.f6147a.f.removeMessages(100);
            this.f6147a.f.sendEmptyMessageDelayed(100, 1000);
            return;
        }
        button.setText(i);
        this.f6147a.f6126c.setEnabled(true);
    }
}
