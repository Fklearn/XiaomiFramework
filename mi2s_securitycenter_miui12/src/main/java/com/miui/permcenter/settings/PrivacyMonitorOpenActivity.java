package com.miui.permcenter.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import b.b.c.c.a;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.permcenter.privacymanager.StatusBar;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permcenter.settings.t;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;

public class PrivacyMonitorOpenActivity extends a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public AlertDialog f6506a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ActivityManager f6507b;

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.permcenter.settings.PrivacyMonitorOpenActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void a(k kVar) {
        Intent intent;
        ActivityManager.RecentTaskInfo next;
        Iterator<ActivityManager.RecentTaskInfo> it = this.f6507b.getRecentTasks(64, 0).iterator();
        while (true) {
            if (!it.hasNext()) {
                intent = null;
                break;
            }
            next = it.next();
            if ((next.topActivity == null || !TextUtils.equals(kVar.b(), next.topActivity.getPackageName())) && ((next.baseActivity == null || !TextUtils.equals(kVar.b(), next.baseActivity.getPackageName())) && (next.baseIntent.getComponent() == null || !TextUtils.equals(kVar.b(), next.baseIntent.getComponent().getPackageName())))) {
            }
        }
        intent = next.baseIntent;
        if (intent == null) {
            Log.i("PrivacyMonitorOpen", kVar.b() + " not exist in recent task, go to single record");
            g.b((Context) this, PrivacyDetailActivity.a(kVar.b(), kVar.c(), "status_bar"), B.b());
        } else {
            g.b((Context) this, intent, B.e(kVar.c()));
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.permcenter.settings.PrivacyMonitorOpenActivity, miui.app.Activity] */
    private void a(List<k> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.pm_monitor_open, (ViewGroup) null);
        t tVar = new t(this);
        ((ListView) inflate.findViewById(R.id.listview)).setAdapter(tVar);
        tVar.a(list);
        tVar.a((t.a) new o(this, list, tVar));
        builder.setView(inflate);
        builder.setTitle(R.string.privacy_permission_status);
        builder.setOnDismissListener(new p(this));
        builder.setNegativeButton(R.string.cancel, new q(this));
        this.f6506a = builder.create();
        this.f6506a.setCanceledOnTouchOutside(false);
        this.f6506a.getWindow().getDecorView().setHapticFeedbackEnabled(false);
        if (!isFinishing()) {
            this.f6506a.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                List list = (List) extras.getSerializable("DATA");
                if (list != null) {
                    if (list.size() != 0) {
                        this.f6507b = (ActivityManager) getSystemService("activity");
                        ArrayList arrayList = new ArrayList();
                        for (int i = 0; i < list.size(); i++) {
                            StatusBar statusBar = (StatusBar) list.get(i);
                            k kVar = new k();
                            if (statusBar.permId == 32) {
                                kVar.a(1);
                            } else if (statusBar.permId == PermissionManager.PERM_ID_AUDIO_RECORDER) {
                                kVar.a(2);
                            } else if (statusBar.permId == PermissionManager.PERM_ID_VIDEO_RECORDER) {
                                kVar.a(3);
                            }
                            kVar.b(statusBar.mUserId);
                            kVar.a(statusBar.permId);
                            kVar.a(statusBar.pkgName);
                            arrayList.add(kVar);
                        }
                        a((List<k>) arrayList);
                        return;
                    }
                }
                finish();
                return;
            } catch (Exception e) {
                Log.e("PrivacyMonitorOpen", "get data error", e);
            }
        }
        finish();
    }
}
