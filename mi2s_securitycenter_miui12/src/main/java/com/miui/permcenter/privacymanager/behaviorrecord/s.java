package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.permcenter.b.a;
import com.miui.permcenter.privacymanager.a.b;
import com.miui.securitycenter.R;

class s implements a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6461a;

    s(PrivacyDetailActivity privacyDetailActivity) {
        this.f6461a = privacyDetailActivity;
    }

    public View a() {
        return null;
    }

    /* JADX WARNING: type inference failed for: r0v7, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public String getGroupName(int i) {
        if (i >= this.f6461a.A.size()) {
            return null;
        }
        com.miui.permcenter.privacymanager.a.a aVar = (com.miui.permcenter.privacymanager.a.a) this.f6461a.A.get(i);
        return aVar.b(b.e) ? this.f6461a.getString(R.string.app_behavior_this_run) : o.b((Context) this.f6461a, aVar.b());
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* JADX WARNING: type inference failed for: r2v8, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public View getGroupView(int i) {
        String str;
        PrivacyDetailActivity privacyDetailActivity;
        int i2;
        View view = null;
        if (i < this.f6461a.A.size()) {
            com.miui.permcenter.privacymanager.a.a aVar = (com.miui.permcenter.privacymanager.a.a) this.f6461a.A.get(i);
            if (aVar.b(b.e)) {
                str = this.f6461a.getString(R.string.app_behavior_this_run);
            } else {
                ? r2 = this.f6461a;
                str = o.b((Context) r2, ((com.miui.permcenter.privacymanager.a.a) r2.A.get(i)).b());
            }
            if (!TextUtils.isEmpty(str)) {
                view = LayoutInflater.from(this.f6461a).inflate(R.layout.listitem_app_behavior_header, (ViewGroup) null, false);
                TextView textView = (TextView) view.findViewById(R.id.header_title);
                textView.setText(str);
                if (aVar.b(b.e)) {
                    privacyDetailActivity = this.f6461a;
                    i2 = R.color.tx_runtime_behavior;
                } else {
                    privacyDetailActivity = this.f6461a;
                    i2 = R.color.app_behavior_record_header_color;
                }
                textView.setTextColor(privacyDetailActivity.getColor(i2));
            }
        }
        return view;
    }
}
