package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.B;
import b.b.c.j.u;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PackageVerifyModel extends AbsModel {
    private static final int DAY_TO_MILL_SECONDS_UNIT = 86400000;
    private static final String TAG = "PackageVerifyModel";
    private static final int TIME_DIFF_FLOW_VERIFY = 259200000;
    /* access modifiers changed from: private */
    public long correctTime = -1;
    /* access modifiers changed from: private */
    public int dayDiff = 0;

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7782a;

        public a(Context context) {
            this.f7782a = context;
        }

        public Boolean call() {
            boolean z;
            if (u.l(this.f7782a) && !u.f(this.f7782a) && u.a(this.f7782a) != 0 && !B.g() && u.h(this.f7782a)) {
                long unused = PackageVerifyModel.this.correctTime = u.b(this.f7782a);
                if (PackageVerifyModel.this.correctTime != -1) {
                    long currentTimeMillis = System.currentTimeMillis() - PackageVerifyModel.this.correctTime;
                    if (currentTimeMillis > 259200000) {
                        int unused2 = PackageVerifyModel.this.dayDiff = (int) Math.floor((double) (currentTimeMillis / 86400000));
                        z = true;
                        return Boolean.valueOf(z);
                    }
                }
            }
            z = false;
            return Boolean.valueOf(z);
        }
    }

    public PackageVerifyModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("flow_verify");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 8;
    }

    public String getSummary() {
        if (this.correctTime == 0) {
            return getContext().getString(R.string.summary_never_flow_verify);
        }
        Resources resources = getContext().getResources();
        int i = this.dayDiff;
        return resources.getQuantityString(R.plurals.summary_flow_verify, i, new Object[]{Integer.valueOf(i)});
    }

    public String getTitle() {
        return getContext().getString(R.string.title_flow_verify);
    }

    public void optimize(Context context) {
        if (!x.a(context, new Intent("miui.intent.action.NETWORKASSISTANT_ENTRANCE"), 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        String str;
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : !((Boolean) future.get()).booleanValue() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
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
