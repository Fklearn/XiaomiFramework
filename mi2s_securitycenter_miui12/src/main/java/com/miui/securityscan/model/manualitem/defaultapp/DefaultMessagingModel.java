package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import b.b.c.j.i;
import b.b.o.g.d;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import miui.util.OldmanUtil;

public class DefaultMessagingModel extends DefaultAppModel {
    private static final String TAG = "DefaultMessagingModel";

    public DefaultMessagingModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_messaging");
    }

    public int getIndex() {
        return 15;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setDefaultPkgName("com.android.mms");
        setTypeName(getContext().getString(R.string.preferred_app_entries_messaging));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SENDTO");
        intentFilter.addDataScheme("smsto");
        setIntentFilter(intentFilter);
    }

    public void optimize(Context context) {
        try {
            d.a(TAG, Class.forName("com.android.internal.telephony.SmsApplication"), "setDefaultApplication", (Class<?>[]) new Class[]{String.class, Context.class}, getDefaultPkgName(), getContext());
        } catch (Exception e) {
            Log.e(TAG, "optimize exception!", e);
        }
        setSafe(AbsModel.State.SAFE);
    }

    public void scan() {
        if (OldmanUtil.IS_ELDER_MODE || i.f()) {
            setSafe(AbsModel.State.SAFE);
        } else if (i.k(getContext())) {
            super.scan();
        }
    }
}
