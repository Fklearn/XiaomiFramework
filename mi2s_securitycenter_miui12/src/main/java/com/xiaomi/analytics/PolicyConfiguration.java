package com.xiaomi.analytics;

import com.xiaomi.analytics.a.b.a;

public class PolicyConfiguration {

    /* renamed from: a  reason: collision with root package name */
    private Privacy f8274a;

    public enum Privacy {
        NO,
        USER
    }

    private void b(a aVar) {
        Privacy privacy = this.f8274a;
        if (privacy != null && aVar != null) {
            aVar.setDefaultPolicy("privacy_policy", privacy == Privacy.NO ? "privacy_no" : "privacy_user");
        }
    }

    public void a(a aVar) {
        if (aVar != null) {
            b(aVar);
        }
    }
}
