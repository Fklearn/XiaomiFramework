package com.miui.firstaidkit.model.operation;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import b.b.o.g.c;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;

public class AirplaneModel extends AbsModel {
    private final ContentResolver mResolver = getContext().getContentResolver();

    public AirplaneModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("air_plane");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_airplane_button);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 53;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_airplane_summary);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_airplane_title);
    }

    public void optimize(Context context) {
        c.a a2 = c.a.a("miui.app.ToggleManager");
        a2.b("createInstance", new Class[]{Context.class}, getContext());
        a2.e();
        a2.a("performToggle", new Class[]{Integer.TYPE}, 9);
        a2.a();
        Handler firstAidEventHandler = getFirstAidEventHandler();
        if (firstAidEventHandler != null) {
            firstAidEventHandler.sendEmptyMessage(201);
        }
    }

    public void scan() {
        boolean z = false;
        if (Settings.Global.getInt(this.mResolver, "airplane_mode_on", 0) != 0) {
            z = true;
        }
        setSafe(z ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }
}
