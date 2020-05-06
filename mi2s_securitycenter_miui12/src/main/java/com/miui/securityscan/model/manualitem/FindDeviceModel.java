package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import miui.cloud.sync.SyncSettingHelper;

public class FindDeviceModel extends AbsModel {
    private static final String TAG = "FindDeviceModel";

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7772a;

        public a(Context context) {
            this.f7772a = context;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
            if (r1 == null) goto L_0x004b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x002d, code lost:
            if (r1 != null) goto L_0x002f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x002f, code lost:
            r1.release();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean call() {
            /*
                r5 = this;
                java.lang.String r0 = "FindDeviceModel"
                boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD
                r2 = 1
                if (r1 != 0) goto L_0x0044
                boolean r1 = b.b.c.j.B.g()
                if (r1 == 0) goto L_0x000e
                goto L_0x0044
            L_0x000e:
                r1 = 0
                android.content.Context r3 = r5.f7772a     // Catch:{ Exception -> 0x0035 }
                miui.cloud.finddevice.FindDeviceStatusManager r1 = miui.cloud.finddevice.FindDeviceStatusManager.obtain(r3)     // Catch:{ Exception -> 0x0035 }
                boolean r2 = r1.isOpen()     // Catch:{ Exception -> 0x0035 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0035 }
                r3.<init>()     // Catch:{ Exception -> 0x0035 }
                java.lang.String r4 = "LoadDataTask isOpen = "
                r3.append(r4)     // Catch:{ Exception -> 0x0035 }
                r3.append(r2)     // Catch:{ Exception -> 0x0035 }
                java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0035 }
                android.util.Log.i(r0, r3)     // Catch:{ Exception -> 0x0035 }
                if (r1 == 0) goto L_0x004b
            L_0x002f:
                r1.release()
                goto L_0x004b
            L_0x0033:
                r0 = move-exception
                goto L_0x003e
            L_0x0035:
                r3 = move-exception
                java.lang.String r4 = "LoadDataTask FindDeviceStatusManager "
                android.util.Log.e(r0, r4, r3)     // Catch:{ all -> 0x0033 }
                if (r1 == 0) goto L_0x004b
                goto L_0x002f
            L_0x003e:
                if (r1 == 0) goto L_0x0043
                r1.release()
            L_0x0043:
                throw r0
            L_0x0044:
                com.miui.securityscan.model.manualitem.FindDeviceModel r0 = com.miui.securityscan.model.manualitem.FindDeviceModel.this
                com.miui.securityscan.model.AbsModel$State r1 = com.miui.securityscan.model.AbsModel.State.SAFE
                r0.setSafe(r1)
            L_0x004b:
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.model.manualitem.FindDeviceModel.a.call():java.lang.Boolean");
        }
    }

    public FindDeviceModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("find_device");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 26;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_find_device);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_find_device);
    }

    public void optimize(Context context) {
        try {
            M.b(true);
            if (context instanceof Activity) {
                SyncSettingHelper.openFindDeviceSettingUI((Activity) context);
            }
        } catch (Exception e) {
            Log.e(TAG, "miCloud Sdk error", e);
        }
    }

    public void scan() {
        String str;
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            if (future.isCancelled()) {
                Log.i(TAG, "scan future is cancelled");
                setSafe(AbsModel.State.SAFE);
            } else {
                Boolean bool = (Boolean) future.get();
                setSafe(bool.booleanValue() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
                Log.i(TAG, "scan isOpen = " + bool);
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
