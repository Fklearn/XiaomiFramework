package com.miui.firstaidkit.model.operation;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import b.b.o.g.c;
import b.b.o.g.d;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class EyeshieldModel extends AbsModel {
    public static final String SCREEN_PAPER_MODE_ENABLED = "screen_paper_mode_enabled";
    public static final boolean SCREEN_PAPER_MODE_ENABLED_DEFAULT = false;
    private static final String TAG = "EyeshieldModel";

    public EyeshieldModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("eye_shield");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.button_eye_shield);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 46;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_eye_shield);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_eye_shield);
    }

    public void ignore() {
    }

    public void optimize(Context context) {
        c.a a2 = c.a.a("miui.app.ToggleManager");
        a2.b("createInstance", new Class[]{Context.class}, getContext());
        a2.e();
        a2.a("performToggle", new Class[]{Integer.TYPE}, 26);
        a2.a();
        Handler firstAidEventHandler = getFirstAidEventHandler();
        if (firstAidEventHandler != null) {
            firstAidEventHandler.sendEmptyMessage(201);
        }
    }

    public void scan() {
        int myUserId = UserHandle.myUserId();
        Log.d(TAG, "scan currentUserId: " + myUserId);
        try {
            boolean z = false;
            int intValue = ((Integer) d.a(TAG, Class.forName("android.provider.Settings$System"), Integer.TYPE, "getIntForUser", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE, Integer.TYPE}, getContext().getContentResolver(), SCREEN_PAPER_MODE_ENABLED, 0, Integer.valueOf(myUserId))).intValue();
            Log.d(TAG, "modeType = " + intValue);
            if (intValue != 0) {
                z = true;
            }
            setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
        } catch (Exception e) {
            Log.e(TAG, "scan error  ", e);
        }
    }
}
