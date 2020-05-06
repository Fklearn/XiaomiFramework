package com.miui.warningcenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.miui.warningcenter.analytics.AnalyticHelper;
import com.miui.warningcenter.mijia.MijiaUtils;
import com.miui.warningcenter.mijia.RegistMijiaPushTask;

public class WarningCenterProvider extends ContentProvider {
    public static final String AUTHORITY = "com.miui.warningcenter.provider";
    private static final String KEY_MIJIA_ACCOUNT = "key_mijia_account";
    private static final String KEY_MIJIA_LOGIN_STATUS = "key_mijia_login_status";
    private static final String KEY_MIJIA_SERVER = "key_mijia_server";
    private static final String KEY_STATUS_RETURN = "status_return";
    private static final String METHOD_SET_MIJIA_ACCOUNT = "setMijiaAccount";
    private static final String TAG = "WarningCenterProvider";

    @Nullable
    public Bundle call(@NonNull String str, @Nullable String str2, @Nullable Bundle bundle) {
        String str3;
        if (!METHOD_SET_MIJIA_ACCOUNT.equals(str)) {
            return null;
        }
        Log.i(TAG, "call METHOD_SET_MIJIA_ACCOUNT");
        String callingPackage = getCallingPackage();
        if (TextUtils.isEmpty(callingPackage) || !"com.xiaomi.smarthome".equals(callingPackage)) {
            return null;
        }
        Bundle bundle2 = new Bundle();
        if (bundle != null) {
            int i = bundle.getInt(KEY_MIJIA_LOGIN_STATUS, 0);
            String string = bundle.getString(KEY_MIJIA_ACCOUNT, "");
            String string2 = bundle.getString(KEY_MIJIA_SERVER, "");
            if (i == 1) {
                bundle2.putInt(KEY_STATUS_RETURN, 1);
                new RegistMijiaPushTask((String) null, (String) null, i).execute(new String[0]);
                str3 = AnalyticHelper.MIJIA_UNREGISTER;
            } else if (i == 0) {
                if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
                    bundle2.putInt(KEY_STATUS_RETURN, 2);
                    Log.i(TAG, "no account");
                    str3 = AnalyticHelper.MIJIA_REGISTER_FAILED;
                } else {
                    bundle2.putInt(KEY_STATUS_RETURN, 1);
                    Log.i(TAG, "get account");
                    new RegistMijiaPushTask(string, string2, i).execute(new String[0]);
                    if (MijiaUtils.isFirstUseMijiaWarning()) {
                        MijiaUtils.setFirstUseMijiaWarning(false);
                        MijiaUtils.showFirstUseMijiaNoti(getContext());
                        AnalyticHelper.trackMijiaResultAction(AnalyticHelper.MIJIA_FIRST_REGISTER);
                    }
                    str3 = AnalyticHelper.MIJIA_REGISTER;
                }
            }
            AnalyticHelper.trackMijiaResultAction(str3);
        }
        return bundle2;
    }

    public int delete(@NonNull Uri uri, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] strArr, @Nullable String str, @Nullable String[] strArr2, @Nullable String str2) {
        return null;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }
}
