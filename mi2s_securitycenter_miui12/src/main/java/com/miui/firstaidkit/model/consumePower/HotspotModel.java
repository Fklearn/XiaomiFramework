package com.miui.firstaidkit.model.consumePower;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import b.b.o.g.c;
import b.b.o.g.d;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class HotspotModel extends AbsModel {
    private static final String TAG = "HotspotModel";
    public static final int WIFI_AP_STATE_ENABLED = 13;
    private WifiManager mWifiManager = ((WifiManager) getContext().getSystemService("wifi"));

    public HotspotModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("hotspot");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_consume_power_button2);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 51;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_consume_power_summary2);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_consume_power_title);
    }

    /* access modifiers changed from: package-private */
    public boolean isWifiApOn() {
        try {
            return ((Integer) d.a(TAG, (Object) this.mWifiManager, Integer.TYPE, "getWifiApState", (Class<?>[]) null, new Object[0])).intValue() == 13;
        } catch (Exception e) {
            Log.e(TAG, "isWifiApOn :", e);
            return false;
        }
    }

    public void optimize(Context context) {
        c.a a2 = c.a.a("miui.app.ToggleManager");
        a2.b("createInstance", new Class[]{Context.class}, getContext());
        a2.e();
        a2.a("performToggle", new Class[]{Integer.TYPE}, 24);
        a2.a();
        Handler firstAidEventHandler = getFirstAidEventHandler();
        if (firstAidEventHandler != null) {
            firstAidEventHandler.sendEmptyMessageDelayed(201, 500);
        }
    }

    public void scan() {
        setSafe(isWifiApOn() ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
