package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.firstaidkit.l;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FirstAidModel extends AbsModel {
    private static final String TAG = "FirstAidModel";
    /* access modifiers changed from: private */
    public l firstAidKitManualItemManager = l.a(getContext());

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7774a;

        public a(Context context) {
            this.f7774a = context;
        }

        public Boolean call() {
            return Boolean.valueOf(FirstAidModel.this.firstAidKitManualItemManager.a());
        }
    }

    public FirstAidModel(String str, Integer num) {
        super(str, num);
        setTrackStr("first_aid_kit");
        setDelayOptimized(true);
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 58;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_first_aid_kit);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_first_aid_kit);
    }

    public void optimize(Context context) {
        Intent intent = new Intent("com.miui.securitycenter.action.FIRST_AID_KIT");
        intent.putExtra("enter_homepage_way", "00006");
        if (!x.c(context, intent)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : ((Boolean) future.get()).booleanValue() ? AbsModel.State.DANGER : AbsModel.State.SAFE);
            newSingleThreadExecutor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "scan error ", e);
        }
    }
}
