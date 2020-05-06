package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import b.b.c.j.h;
import b.b.c.j.i;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import miui.util.OldmanUtil;

public class DefaultDialModel extends DefaultAppModel {
    private static final String TAG = "DefaultDialModel";

    public DefaultDialModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_dial");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 14;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setTypeName(getContext().getString(R.string.preferred_app_entries_dial));
    }

    public void optimize(Context context) {
        h.a(getContext(), "com.android.contacts");
    }

    public void scan() {
        AbsModel.State state;
        if (!OldmanUtil.IS_ELDER_MODE && !i.f()) {
            if (i.k(getContext())) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.DIAL");
                intentFilter.addAction("android.intent.action.VIEW");
                intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
                intentFilter.addDataScheme("tel");
                boolean b2 = h.b(getContext(), intentFilter, "com.android.contacts");
                Log.d(TAG, "isDefault = " + b2);
                if (!b2) {
                    state = AbsModel.State.DANGER;
                    setSafe(state);
                }
            } else {
                return;
            }
        }
        state = AbsModel.State.SAFE;
        setSafe(state);
    }
}
