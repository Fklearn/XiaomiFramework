package com.miui.optimizemanage.memoryclean;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.optimizemanage.memoryclean.e;
import com.miui.securitycenter.R;
import java.util.Map;

class h implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f5970a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ LockAppManageActivity f5971b;

    h(LockAppManageActivity lockAppManageActivity, Map map) {
        this.f5971b = lockAppManageActivity;
        this.f5970a = map;
    }

    public String getGroupName(int i) {
        LockAppManageActivity lockAppManageActivity;
        int i2;
        if (((e.a) this.f5970a.get(Integer.valueOf(i))).f5962a == 1) {
            lockAppManageActivity = this.f5971b;
            i2 = R.string.om_lock_app_locked_app;
        } else {
            lockAppManageActivity = this.f5971b;
            i2 = R.string.om_lock_app_unlocked_app;
        }
        return lockAppManageActivity.getString(i2);
    }

    public View getGroupView(int i) {
        View inflate = this.f5971b.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(getGroupName(i));
        return inflate;
    }
}
