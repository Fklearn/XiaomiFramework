package com.miui.firstaidkit.model.consumePower;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.permcenter.autostart.AutoStartManagementActivity;
import com.miui.permcenter.n;
import com.miui.permcenter.s;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import miui.os.Build;

public class AutoStartApplicationModel extends AbsModel {
    private static final String TAG = "AutoStartApplicationModel";

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f3958a;

        public a(Context context) {
            this.f3958a = context;
        }

        public Boolean call() {
            boolean z;
            List<String> a2 = s.a(this.f3958a);
            Collections.sort(x.b(this.f3958a));
            ArrayList<com.miui.permcenter.a> a3 = n.a(this.f3958a, (long) PermissionManager.PERM_ID_AUTOSTART);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            HashSet hashSet = new HashSet();
            if (Build.IS_CM_CUSTOMIZATION) {
                hashSet.add("com.greenpoint.android.mc10086.activity");
            }
            hashSet.add("com.miui.guardprovider");
            hashSet.add("com.xiaomi.account");
            hashSet.add("com.miui.virtualsim");
            Iterator<com.miui.permcenter.a> it = a3.iterator();
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    break;
                }
                com.miui.permcenter.a next = it.next();
                if (!hashSet.contains(next.e())) {
                    if (next.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue() == 3 || (a2 != null && Collections.binarySearch(a2, next.e()) >= 0)) {
                        next.b(true);
                        arrayList.add(next);
                    } else {
                        arrayList2.add(next);
                    }
                }
            }
            if (arrayList.size() > 5) {
                z = false;
            }
            return Boolean.valueOf(z);
        }
    }

    public AutoStartApplicationModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("auto_start_application");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_consume_power_button1);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 47;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_consume_power_summary1);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_consume_power_title);
    }

    public void optimize(Context context) {
        if (!x.a(context, new Intent(context, AutoStartManagementActivity.class), 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : ((Boolean) future.get()).booleanValue() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
            newSingleThreadExecutor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "scan error ", e);
        }
    }
}
