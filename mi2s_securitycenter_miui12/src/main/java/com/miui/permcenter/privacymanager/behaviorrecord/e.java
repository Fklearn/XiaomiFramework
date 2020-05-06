package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.permcenter.b.a;
import com.miui.securitycenter.R;

class e implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f6442a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6443b;

    e(AppBehaviorRecordActivity appBehaviorRecordActivity, String str) {
        this.f6443b = appBehaviorRecordActivity;
        this.f6442a = str;
    }

    public View a() {
        View inflate = this.f6443b.f6392a.inflate(R.layout.listitem_app_behavior_fixed_header, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_classify_menu)).setText(b());
        return inflate;
    }

    public String b() {
        return this.f6442a;
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    public String getGroupName(int i) {
        if (i >= this.f6443b.f.size()) {
            return null;
        }
        ? r0 = this.f6443b;
        return o.b((Context) r0, ((com.miui.permcenter.privacymanager.a.a) r0.f.get(i)).b());
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    public View getGroupView(int i) {
        if (i >= this.f6443b.f.size()) {
            return null;
        }
        ? r0 = this.f6443b;
        String b2 = o.b((Context) r0, ((com.miui.permcenter.privacymanager.a.a) r0.f.get(i)).b());
        if (TextUtils.isEmpty(b2)) {
            return null;
        }
        View inflate = this.f6443b.f6392a.inflate(R.layout.listitem_app_behavior_header, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(b2);
        return inflate;
    }
}
