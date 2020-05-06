package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.model.AbsModel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MifiInsuranceModel extends AbsModel {
    private static final String TAG = "MifiInsuranceModel";

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7778a;

        public a(Context context) {
            this.f7778a = context;
        }

        public Boolean call() {
            return Boolean.valueOf(!TextUtils.isEmpty(M.c()));
        }
    }

    public MifiInsuranceModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("mifi_insurance");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 40;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_mifi_insurance);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_mifi_insurance);
    }

    public void optimize(Context context) {
        try {
            if (!x.a(context, Intent.parseUri(M.c(), 0), 100)) {
                A.a(context, (int) R.string.app_not_installed_toast);
            }
        } catch (Exception e) {
            Log.e(TAG, "MifiInsuranceModel optimize error", e);
        }
    }

    public void scan() {
        String str;
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : ((Boolean) future.get()).booleanValue() ? AbsModel.State.DANGER : AbsModel.State.SAFE);
            newSingleThreadExecutor.shutdown();
        } catch (InterruptedException e) {
            e = e;
            str = "scan Interrupted ";
            Log.e(TAG, str, e);
        } catch (ExecutionException e2) {
            e = e2;
            str = "scan ExecutionException ";
            Log.e(TAG, str, e);
        }
    }
}
