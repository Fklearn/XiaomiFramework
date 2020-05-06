package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import miui.os.Build;

public class FlowRankModel extends AbsModel {
    private static final String TAG = "FlowRankModel";
    private List<RankDataModel> flowRankDataModels;

    public class RankDataModel implements Serializable {
        private static final long serialVersionUID = -1730641265840424208L;
        private int aconId;
        private String title;
        private String value;

        public RankDataModel(int i, String str, String str2) {
            this.aconId = i;
            this.title = str;
            this.value = str2;
        }

        public int getAconId() {
            return this.aconId;
        }

        public String getTitle() {
            return this.title;
        }

        public String getValue() {
            return this.value;
        }

        public void setAconId(int i) {
            this.aconId = i;
        }

        public void setTitle(String str) {
            this.title = str;
        }

        public void setValue(String str) {
            this.value = str;
        }
    }

    class a implements Callable<Boolean> {

        /* renamed from: a  reason: collision with root package name */
        Context f7776a;

        public a(Context context) {
            this.f7776a = context;
        }

        public Boolean call() {
            boolean z = true;
            if (!Build.IS_INTERNATIONAL_BUILD && FlowRankModel.this.isTopFlowDisplay(this.f7776a)) {
                z = false;
            }
            return Boolean.valueOf(z);
        }
    }

    public FlowRankModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("flow_rank");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: android.database.Cursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: android.database.Cursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.database.Cursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: android.database.Cursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: android.database.Cursor} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isTopFlowDisplay(android.content.Context r11) {
        /*
            r10 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            r2 = 0
            java.lang.String r3 = "content://com.miui.networkassistant.provider/top_usage_app/10"
            android.net.Uri r5 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0089 }
            android.content.ContentResolver r4 = r11.getContentResolver()     // Catch:{ Exception -> 0x0089 }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r11 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0089 }
            if (r11 == 0) goto L_0x004c
        L_0x001b:
            boolean r3 = r11.moveToNext()     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            if (r3 == 0) goto L_0x004c
            java.lang.String r3 = "package_name"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            java.lang.String r3 = r11.getString(r3)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            java.lang.String r4 = "traffic_used"
            int r4 = r11.getColumnIndex(r4)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            java.lang.String r4 = r11.getString(r4)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r5 = new com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            r6 = -1
            r5.<init>(r6, r3, r4)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            r0.add(r5)     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            int r3 = r0.size()     // Catch:{ Exception -> 0x0049, all -> 0x0047 }
            r4 = 10
            if (r3 < r4) goto L_0x001b
            goto L_0x004c
        L_0x0047:
            r0 = move-exception
            goto L_0x0091
        L_0x0049:
            r0 = move-exception
            r1 = r11
            goto L_0x008a
        L_0x004c:
            miui.util.IOUtils.closeQuietly(r11)
            java.util.Iterator r11 = r0.iterator()
        L_0x0053:
            boolean r3 = r11.hasNext()
            if (r3 == 0) goto L_0x006c
            java.lang.Object r3 = r11.next()
            com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r3 = (com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel) r3
            java.lang.String r4 = r3.getTitle()
            java.lang.String r5 = "android"
            boolean r4 = r5.equalsIgnoreCase(r4)
            if (r4 == 0) goto L_0x0053
            r1 = r3
        L_0x006c:
            if (r1 == 0) goto L_0x0071
            r0.remove(r1)
        L_0x0071:
            int r11 = r0.size()
            r1 = 5
            if (r11 <= r1) goto L_0x007c
            java.util.List r0 = r0.subList(r2, r1)
        L_0x007c:
            r10.flowRankDataModels = r0
            int r11 = r0.size()
            if (r11 != r1) goto L_0x0085
            r2 = 1
        L_0x0085:
            return r2
        L_0x0086:
            r0 = move-exception
            r11 = r1
            goto L_0x0091
        L_0x0089:
            r0 = move-exception
        L_0x008a:
            r0.printStackTrace()     // Catch:{ all -> 0x0086 }
            miui.util.IOUtils.closeQuietly(r1)
            return r2
        L_0x0091:
            miui.util.IOUtils.closeQuietly(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.model.manualitem.FlowRankModel.isTopFlowDisplay(android.content.Context):boolean");
    }

    public String getDesc() {
        return null;
    }

    public List<RankDataModel> getFlowRankDataModels() {
        return this.flowRankDataModels;
    }

    public int getIndex() {
        return 32;
    }

    public String getSummary() {
        return null;
    }

    public String getTitle() {
        return getContext().getString(R.string.title_flow_rank);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            try {
                Intent intent = new Intent("miui.intent.action.NETWORKASSISTANT_TRAFFIC_SORTED");
                Bundle bundle = new Bundle();
                bundle.putBoolean("slot_num_tag", true);
                intent.putExtras(bundle);
                ((Activity) context).startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void scan() {
        String str;
        try {
            a aVar = new a(getContext());
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            Future future = (Future) newSingleThreadExecutor.invokeAll(Arrays.asList(new a[]{aVar}), 1000, TimeUnit.MILLISECONDS).get(0);
            setSafe(future.isCancelled() ? AbsModel.State.SAFE : ((Boolean) future.get()).booleanValue() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
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

    public void setFlowRankDataModels(List<RankDataModel> list) {
        this.flowRankDataModels = list;
    }
}
