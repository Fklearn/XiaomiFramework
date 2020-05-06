package com.miui.warningcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.miui.warningcenter.mijia.MijiaUtils;
import org.json.JSONObject;

public class WarningCenterManager {
    private static final String TAG = "WarningCenterManager";
    private static volatile WarningCenterManager instance;

    private WarningCenterManager() {
    }

    private void alert(Context context, MijiaAlertModel mijiaAlertModel) {
        Intent intent = new Intent(context, WarningCenterAlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MijiaAlertModel", mijiaAlertModel);
        intent.putExtras(bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static WarningCenterManager getInstance() {
        if (instance == null) {
            synchronized (WarningCenterManager.class) {
                if (instance == null) {
                    instance = new WarningCenterManager();
                }
            }
        }
        return instance;
    }

    public void parseQuake(Context context, JSONObject jSONObject) {
        if (!MijiaUtils.isMijiaWarningOpen()) {
            Log.d(TAG, "mijia warning not open!");
            return;
        }
        try {
            MijiaAlertModel mijiaAlertModel = new MijiaAlertModel();
            mijiaAlertModel.setUid(jSONObject.getString(MijiaAlertModel.KEY_UID));
            mijiaAlertModel.setAlertType(jSONObject.getString(MijiaAlertModel.KEY_ALERTTYPE));
            mijiaAlertModel.setHomeName(jSONObject.getString(MijiaAlertModel.KEY_HOMENAME));
            mijiaAlertModel.setCtime(jSONObject.getLong(MijiaAlertModel.KEY_CTIME));
            mijiaAlertModel.setId(jSONObject.getString("id"));
            mijiaAlertModel.setThird_type(jSONObject.getString(MijiaAlertModel.KEY_THIRD_TYPE));
            mijiaAlertModel.setUrl(jSONObject.getString(MijiaAlertModel.KEY_URL));
            mijiaAlertModel.setRoomName(jSONObject.getString(MijiaAlertModel.KEY_ROOMNAME));
            alert(context, mijiaAlertModel);
        } catch (Exception unused) {
            Log.e(TAG, "receive error warning message");
        }
    }
}
