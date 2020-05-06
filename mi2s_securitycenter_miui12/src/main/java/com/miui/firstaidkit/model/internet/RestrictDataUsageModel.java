package com.miui.firstaidkit.model.internet;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.networkassistant.ui.activity.FirewallActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.c.e;
import com.miui.securityscan.i.c;
import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestrictDataUsageModel extends AbsModel {
    private static final String SET_KEY_MOBILE = "RestrictDataUsageModel_Mobile";
    private static final String SET_KEY_WLAN = "RestrictDataUsageModel_Wlan";
    private static final String TAG = "RestrictDataUsageModel";
    private String appName = "";
    /* access modifiers changed from: private */
    public boolean canRecountTime;
    /* access modifiers changed from: private */
    public boolean canSaveCache = true;
    private final ContentResolver mResolver;
    /* access modifiers changed from: private */
    public Set<String> mobileValueSet;
    /* access modifiers changed from: private */
    public e spfHelper;
    private int visibleItemIndex;
    /* access modifiers changed from: private */
    public Set<String> wlanValueSet;

    private class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f3963a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public String f3964b;

        public a(String str, String str2) {
            this.f3963a = str;
            this.f3964b = str2;
        }

        public String toString() {
            return "AppInfo [appName=" + this.f3963a + ", packageName=" + this.f3964b + "]";
        }
    }

    public RestrictDataUsageModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("restrict_data_usage");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
        this.spfHelper = e.a(getContext(), "data_config");
        this.mResolver = getContext().getContentResolver();
        setOnAbsModelDisplayListener(new b(this));
    }

    private boolean isListNew(List<a> list, Set<String> set) {
        if (list == null || set == null) {
            return false;
        }
        for (a a2 : list) {
            if (!set.contains(a2.f3964b)) {
                return true;
            }
        }
        return false;
    }

    private List<a> queryMobileRestricts(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.networkassistant.provider/mobile_restrict"), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            while (query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("package_name"));
                String charSequence = c.a(context, string).toString();
                if (x.h(context, string)) {
                    arrayList.add(new a(charSequence, string));
                }
            }
            return arrayList;
        } finally {
            query.close();
        }
    }

    private List<a> queryWlanRestricts(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.networkassistant.provider/wlan_restrict"), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            while (query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("package_name"));
                String charSequence = c.a(context, string).toString();
                if (x.h(context, string)) {
                    arrayList.add(new a(charSequence, string));
                }
            }
            return arrayList;
        } finally {
            query.close();
        }
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.button_restrict_data_usage);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 41;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_restrict_data_usage);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_restrict_data_usage, new Object[]{this.appName});
    }

    public void ignore() {
    }

    public void optimize(Context context) {
        Intent intent = new Intent(context, FirewallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("VisibleItemIndex", this.visibleItemIndex);
        intent.putExtras(bundle);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        boolean z;
        this.canRecountTime = false;
        Context context = getContext();
        boolean z2 = true;
        if (!(Settings.Global.getInt(this.mResolver, "airplane_mode_on", 0) != 0) && c.f(context)) {
            if (c.g(context)) {
                List<a> queryWlanRestricts = queryWlanRestricts(context);
                Set<String> c2 = this.spfHelper.c(SET_KEY_WLAN);
                if (queryWlanRestricts != null && queryWlanRestricts.size() > 0) {
                    if (isListNew(queryWlanRestricts, c2)) {
                        this.canRecountTime = true;
                        this.wlanValueSet = new HashSet();
                        for (a a2 : queryWlanRestricts) {
                            this.wlanValueSet.add(a2.f3964b);
                        }
                        z = true;
                    } else {
                        z = false;
                    }
                    if (z) {
                        this.appName = queryWlanRestricts.get(0).f3963a;
                        this.visibleItemIndex = 1;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else if (c.e(context)) {
                List<a> queryMobileRestricts = queryMobileRestricts(context);
                Set<String> c3 = this.spfHelper.c(SET_KEY_MOBILE);
                if (queryMobileRestricts != null && queryMobileRestricts.size() > 0) {
                    if (isListNew(queryMobileRestricts, c3)) {
                        this.canRecountTime = true;
                        this.mobileValueSet = new HashSet();
                        for (a a3 : queryMobileRestricts) {
                            this.mobileValueSet.add(a3.f3964b);
                        }
                    } else {
                        z2 = false;
                    }
                    if (z2) {
                        this.appName = queryMobileRestricts.get(0).f3963a;
                        this.visibleItemIndex = 0;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
            setSafe(AbsModel.State.DANGER);
        }
    }
}
