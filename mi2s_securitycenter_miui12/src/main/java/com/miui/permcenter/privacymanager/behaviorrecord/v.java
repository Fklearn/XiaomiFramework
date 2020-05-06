package com.miui.permcenter.privacymanager.behaviorrecord;

import com.miui.permcenter.b.b;
import java.util.ArrayList;
import java.util.Map;

class v implements b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6464a;

    v(PrivacyDetailActivity privacyDetailActivity) {
        this.f6464a = privacyDetailActivity;
    }

    public ArrayList<Integer> a(int i) {
        for (Map.Entry value : this.f6464a.B.entrySet()) {
            ArrayList<Integer> arrayList = (ArrayList) value.getValue();
            if (arrayList.contains(Integer.valueOf(i))) {
                return arrayList;
            }
        }
        return null;
    }
}
