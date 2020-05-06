package com.miui.firstaidkit.model.internet;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import b.b.c.c.b.g;
import com.miui.networkassistant.ui.fragment.BgNetworkAppListFragment;
import com.miui.securitycenter.R;
import com.miui.securityscan.c.e;
import com.miui.securityscan.i.c;
import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackgroundConnectionModel extends AbsModel {
    private static final String BACKGROUND_CONNECTION_MODEL_BG = "BackgroundConnectionModel_BG";
    private static final String TAG = "BackgroundConnectionModel";
    private String appName = "";
    /* access modifiers changed from: private */
    public boolean canRecountTime;
    /* access modifiers changed from: private */
    public boolean canSaveCache = true;
    private final ContentResolver mResolver;
    /* access modifiers changed from: private */
    public e spfHelper;
    /* access modifiers changed from: private */
    public Set<String> valueSet;

    private class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f3960a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public String f3961b;

        public a(String str, String str2) {
            this.f3960a = str;
            this.f3961b = str2;
        }

        public String toString() {
            return "AppInfo [appName=" + this.f3960a + ", packageName=" + this.f3961b + "]";
        }
    }

    public BackgroundConnectionModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("background_connection");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
        this.spfHelper = e.a(getContext(), "data_config");
        this.mResolver = getContext().getContentResolver();
        setOnAbsModelDisplayListener(new a(this));
    }

    private boolean isListNew(List<a> list, Set<String> set) {
        if (list == null || set == null) {
            return false;
        }
        for (a a2 : list) {
            if (!set.contains(a2.f3961b)) {
                return true;
            }
        }
        return false;
    }

    private List<a> queryBackgroundRestricts(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.networkassistant.provider/firewall_background_restrict"), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            while (query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("package_name"));
                arrayList.add(new a(c.a(context, string).toString(), string));
            }
            return arrayList;
        } finally {
            query.close();
        }
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.button_background_connection);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 42;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_background_connection);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_background_connection, new Object[]{this.appName});
    }

    public void ignore() {
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            g.startWithFragmentForResult((Activity) context, (Class<? extends Fragment>) BgNetworkAppListFragment.class, (Bundle) null, 100);
        }
    }

    public void scan() {
        this.canRecountTime = false;
        Context context = getContext();
        boolean z = true;
        if (!(Settings.Global.getInt(this.mResolver, "airplane_mode_on", 0) != 0) && c.f(context)) {
            List<a> queryBackgroundRestricts = queryBackgroundRestricts(context);
            Set<String> c2 = this.spfHelper.c(BACKGROUND_CONNECTION_MODEL_BG);
            if (queryBackgroundRestricts != null && queryBackgroundRestricts.size() > 0) {
                if (isListNew(queryBackgroundRestricts, c2)) {
                    this.canRecountTime = true;
                    this.valueSet = new HashSet();
                    for (a a2 : queryBackgroundRestricts) {
                        this.valueSet.add(a2.f3961b);
                    }
                } else {
                    z = false;
                }
                if (z) {
                    this.appName = queryBackgroundRestricts.get(0).f3960a;
                    setSafe(AbsModel.State.DANGER);
                }
            }
        }
    }
}
