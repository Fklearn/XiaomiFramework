package com.miui.permcenter.privacymanager.behaviorrecord;

import com.miui.permcenter.b.b;
import java.util.ArrayList;
import java.util.Map;

class g implements b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6445a;

    g(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6445a = appBehaviorRecordActivity;
    }

    public ArrayList<Integer> a(int i) {
        for (Map.Entry value : this.f6445a.g.entrySet()) {
            ArrayList<Integer> arrayList = (ArrayList) value.getValue();
            if (arrayList.contains(Integer.valueOf(i))) {
                return arrayList;
            }
        }
        return null;
    }
}
