package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.c.j.u;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import miui.os.Build;

public class PackageVerifyFailedModel extends AbsModel {
    private static final String TAG = "PackageVerifyFailedModel";

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7780a;

        public a(Context context) {
            this.f7780a = context;
        }

        public Boolean call() {
            return Boolean.valueOf(!Build.IS_INTERNATIONAL_BUILD && i.g(this.f7780a) && u.l(this.f7780a) && u.g(this.f7780a) && !B.g());
        }
    }

    public PackageVerifyFailedModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("flow_verify_failed");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 36;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_flow_verify_failed);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_flow_verify_failed);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(new Intent(Constants.App.ACTION_NETWORK_ASSISTANT_TC_DIAGNOSTIC), 100);
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
