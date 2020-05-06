package com.miui.securityscan.model.manualitem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import b.b.c.b;
import b.b.c.j.A;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.c.j.u;
import b.b.c.j.x;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.ui.fragment.OperatorSettingFragment;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import miui.os.Build;

public class DataPackageModel extends AbsModel {
    private static final String TAG = "DataPackageModel";
    /* access modifiers changed from: private */
    public boolean isCurrentSet;

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7768a;

        public a(Context context) {
            this.f7768a = context;
        }

        public Boolean call() {
            boolean z = false;
            boolean unused = DataPackageModel.this.isCurrentSet = false;
            if (Build.IS_INTERNATIONAL_BUILD || !i.g(this.f7768a) || B.g() || !u.l(this.f7768a)) {
                z = true;
            } else {
                boolean z2 = u.a(this.f7768a) >= 0;
                boolean h = u.h(this.f7768a);
                if (z2 && h) {
                    z = true;
                }
                boolean unused2 = DataPackageModel.this.isCurrentSet = true;
            }
            return Boolean.valueOf(z);
        }
    }

    public DataPackageModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("data_flow");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 6;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_data_flow);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_data_flow);
    }

    public void optimize(Context context) {
        Intent intent = new Intent(b.f1605b);
        Bundle bundle = new Bundle();
        bundle.putInt(Sim.SIM_SLOT_NUM_TAG, i.c());
        bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_FROM_NOTIFICATION, true);
        intent.putExtras(bundle);
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        String str;
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : ((Boolean) future.get()).booleanValue() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
            if (isSafe() != AbsModel.State.SAFE || !this.isCurrentSet) {
                Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.SECURITY);
                if (a2.containsKey(getItemKey())) {
                    a2.remove(getItemKey());
                }
            } else {
                C0570q.b().a(C0570q.a.SECURITY, getItemKey(), new C0569p(getContext().getString(R.string.title_data_flow_monitor), false));
            }
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
