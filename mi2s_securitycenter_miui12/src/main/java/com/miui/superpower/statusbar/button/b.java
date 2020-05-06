package com.miui.superpower.statusbar.button;

import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.miui.networkassistant.config.Constants;

class b extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CellularButton f8163a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(CellularButton cellularButton, Handler handler) {
        super(handler);
        this.f8163a = cellularButton;
    }

    public void onChange(boolean z) {
        CellularButton cellularButton = this.f8163a;
        boolean z2 = true;
        if (Settings.Secure.getInt(cellularButton.j, Constants.System.MOBILE_POLICY, 1) != 1) {
            z2 = false;
        }
        cellularButton.setChecked(z2);
    }
}
