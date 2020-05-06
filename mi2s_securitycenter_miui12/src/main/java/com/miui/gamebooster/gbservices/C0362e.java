package com.miui.gamebooster.gbservices;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import b.b.o.d.a;
import com.miui.common.persistence.b;
import com.miui.gamebooster.p.f;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.miui.gamebooster.gbservices.e  reason: case insensitive filesystem */
class C0362e extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4351a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0363f f4352b;

    C0362e(C0363f fVar, Context context) {
        this.f4352b = fVar;
        this.f4351a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) this.f4351a.getSystemService("activity")).getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName componentName = runningTasks.get(0).topActivity;
            String packageName = componentName == null ? null : componentName.getPackageName();
            if (!"com.xiaomi.gamecenter".equals(packageName)) {
                ArrayList arrayList = new ArrayList(b.a("gb_added_games", (ArrayList<String>) new ArrayList()));
                if (arrayList.size() == 0 || !arrayList.contains(packageName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        if (bool.booleanValue() && !this.f4352b.f4353a.k) {
            a.a(this.f4351a);
            f.a(this.f4351a, this.f4352b.f4353a.m).b();
            boolean unused = this.f4352b.f4353a.k = true;
        }
    }
}
