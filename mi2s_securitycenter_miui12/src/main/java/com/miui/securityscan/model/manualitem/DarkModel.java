package com.miui.securityscan.model.manualitem;

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;
import b.b.o.g.e;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class DarkModel extends AbsModel {
    private static final String TAG = "DarkModel";
    private final UiModeManager mUiModeManager = ((UiModeManager) getContext().getSystemService("uimode"));

    public DarkModel(String str, Integer num) {
        super(str, num);
        setTrackStr("dark_model");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 59;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_dark_mode);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_dark_mode);
    }

    public void optimize(Context context) {
        UiModeManager uiModeManager = this.mUiModeManager;
        if (uiModeManager != null) {
            uiModeManager.setNightMode(1);
            setSafe(AbsModel.State.SAFE);
            runOnUiThread(new d(this, context));
        }
    }

    public void scan() {
        AbsModel.State state;
        setSafe(AbsModel.State.SAFE);
        UiModeManager uiModeManager = this.mUiModeManager;
        if (uiModeManager == null) {
            Log.d(TAG, "mUiModeManager is null");
            return;
        }
        boolean z = false;
        boolean z2 = 2 == uiModeManager.getNightMode();
        Log.d(TAG, "isNightModelOn: " + z2);
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                z = ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$System"), Boolean.TYPE, "getBooleanForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE, Integer.TYPE}, getContext().getContentResolver(), "is_darkmode_switch_show", false, Integer.valueOf(UserHandle.myUserId()))).booleanValue();
            } catch (Exception e) {
                Log.e(TAG, "ReflectUtil error: ", e);
            }
            Log.d(TAG, "hasDarkModeSwitchInSettings: " + z);
            if (!z2 || z) {
                return;
            }
        } else if (!z2) {
            state = AbsModel.State.SAFE;
            setSafe(state);
        }
        state = AbsModel.State.DANGER;
        setSafe(state);
    }
}
