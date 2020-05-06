package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.miui.powercenter.f.b;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.List;

public class ConsumePowerRankModel extends AbsModel {
    private static final String ACTION_QUICK_OPTIMIZE_POWERCENTER = "android.intent.action.POWER_USAGE_SUMMARY";
    /* access modifiers changed from: private */
    public static List<com.miui.powercenter.f.a> appConsumeInfoList;
    /* access modifiers changed from: private */
    public boolean isTaskFinished;

    private class a extends AsyncTask<Void, Void, List<com.miui.powercenter.f.a>> {

        /* renamed from: a  reason: collision with root package name */
        Context f7764a;

        a(Context context) {
            this.f7764a = context;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.powercenter.f.a> doInBackground(Void... voidArr) {
            boolean unused = ConsumePowerRankModel.this.isTaskFinished = false;
            return b.a(this.f7764a);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.powercenter.f.a> list) {
            super.onPostExecute(list);
            boolean unused = ConsumePowerRankModel.this.isTaskFinished = true;
            List unused2 = ConsumePowerRankModel.appConsumeInfoList = list;
            ConsumePowerRankModel.this.setSafe((list == null || list.size() <= 0) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        }
    }

    public ConsumePowerRankModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("consume_power_rank");
    }

    public List<com.miui.powercenter.f.a> getAppConsumeInfoList() {
        return appConsumeInfoList;
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 33;
    }

    public String getSummary() {
        return null;
    }

    public String getTitle() {
        return getContext().getString(R.string.title_consume_power_rank);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(new Intent(ACTION_QUICK_OPTIMIZE_POWERCENTER), 100);
        }
    }

    public void scan() {
        new a(getContext()).execute(new Void[0]);
        if (!this.isTaskFinished) {
            List<com.miui.powercenter.f.a> list = appConsumeInfoList;
            setSafe((list == null || list.size() <= 0) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        }
    }
}
