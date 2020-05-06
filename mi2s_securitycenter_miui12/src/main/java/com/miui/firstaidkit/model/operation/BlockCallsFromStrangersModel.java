package com.miui.firstaidkit.model.operation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.a.e.c;
import b.b.a.e.n;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.antispam.db.d;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.TelephonyManager;

public class BlockCallsFromStrangersModel extends AbsModel {
    private static final String TAG = "BlockCallsFromStrangersModel";
    private int currentSimId = 1;

    public BlockCallsFromStrangersModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("block_calls_from_strangers");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
        setOnAbsModelDisplayListener(new b(this));
    }

    private void setMultiSimState() {
        boolean z = false;
        boolean z2 = c.b(getContext(), 1) && (d.a(getContext(), "stranger_call_mode", 1, 1) == 0);
        boolean b2 = c.b(getContext(), 2);
        int a2 = d.a(getContext(), "stranger_call_mode", 2, 1);
        boolean e = c.e(getContext());
        boolean z3 = a2 == 0;
        if (b2 && !e && z3) {
            z = true;
        }
        setSafe((z2 || z) ? AbsModel.State.DANGER : AbsModel.State.SAFE);
        if (!c.e(getContext()) && !z2 && z) {
            this.currentSimId = 2;
        } else {
            this.currentSimId = 1;
        }
    }

    private void setSingleSimState(int i) {
        boolean b2 = c.b(getContext(), i);
        boolean z = false;
        boolean z2 = d.a(getContext(), "stranger_call_mode", i, 1) == 0;
        if (b2 && z2) {
            z = true;
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.button_block_calls_from_strangers);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 44;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_block_calls_from_strangers);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_block_calls_from_strangers);
    }

    public void ignore() {
    }

    public void optimize(Context context) {
        Intent intent = new Intent("miui.intent.action.CALL_FIREWALL");
        intent.putExtra("key_sim_id", this.currentSimId);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean isMultiSimEnabled = TelephonyManager.getDefault().isMultiSimEnabled();
        List<SubscriptionInfo> b2 = n.b();
        int size = b2 != null ? b2.size() : 0;
        Log.d(TAG, "multiSimEnabled: " + isMultiSimEnabled + "   simSize:" + size);
        if (isMultiSimEnabled) {
            if (size == 1) {
                SubscriptionInfo subscriptionInfo = b2.get(0);
                if (subscriptionInfo == null) {
                    return;
                }
                if (!c.e(getContext())) {
                    this.currentSimId = subscriptionInfo.getSlotId() + 1;
                    setSingleSimState(this.currentSimId);
                }
            } else if (size == 2) {
                setMultiSimState();
                return;
            } else {
                return;
            }
        } else if (size == 0) {
            return;
        }
        this.currentSimId = 1;
        setSingleSimState(this.currentSimId);
    }
}
