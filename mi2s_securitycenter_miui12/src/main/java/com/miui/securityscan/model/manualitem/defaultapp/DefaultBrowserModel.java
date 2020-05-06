package com.miui.securityscan.model.manualitem.defaultapp;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.h;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import java.lang.reflect.Method;

public class DefaultBrowserModel extends DefaultAppModel {
    private static final String TAG = "DefaultBrowserModel";

    public DefaultBrowserModel(String str, Integer num) {
        super(str, num);
        setTrackStr("default_browser");
    }

    public int getIndex() {
        return 16;
    }

    /* access modifiers changed from: protected */
    public void initModel() {
        setTypeName(getContext().getString(R.string.preferred_app_entries_browser));
    }

    public void optimize(Context context) {
        String str;
        Class[] clsArr;
        PackageManager packageManager = getContext().getPackageManager();
        Class<?> cls = packageManager.getClass();
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                str = "setDefaultBrowserPackageNameAsUser";
                clsArr = new Class[]{String.class, Integer.TYPE};
            } else {
                str = "setDefaultBrowserPackageName";
                clsArr = new Class[]{String.class, Integer.TYPE};
            }
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            if (declaredMethod != null) {
                declaredMethod.invoke(packageManager, new Object[]{"com.android.browser", Integer.valueOf(B.c())});
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void scan() {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            setSafe(AbsModel.State.SAFE);
            return;
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
        intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
        intentFilter.addDataScheme("http");
        intentFilter.addDataScheme("https");
        boolean a2 = h.a(getContext(), intentFilter, "com.android.browser");
        Log.d(TAG, "isDefault = " + a2);
        setSafe(a2 ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
