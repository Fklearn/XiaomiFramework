package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.util.Log;
import b.b.c.j.i;
import b.b.c.j.u;
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
import miui.os.Build;
import miui.provider.ExtraNetwork;

public class DataPurchaseModel extends AbsModel {
    private static final String TAG = "DataPurchaseModel";
    private long dataAmount = -1;

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7770a;

        public a(Context context) {
            this.f7770a = context;
        }

        public Boolean call() {
            return Boolean.valueOf(!Build.IS_INTERNATIONAL_BUILD && i.g(this.f7770a) && u.i(this.f7770a));
        }
    }

    public DataPurchaseModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("flow_purchase");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 5;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_flow_purchase);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_flow_purchase);
    }

    public void optimize(Context context) {
        M.b(true);
        ExtraNetwork.navigateToTrafficPurchasePage(context, "100008");
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
