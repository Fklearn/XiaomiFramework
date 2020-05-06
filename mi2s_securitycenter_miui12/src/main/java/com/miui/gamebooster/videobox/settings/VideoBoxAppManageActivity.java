package com.miui.gamebooster.videobox.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.b.b;
import b.b.c.b.c;
import com.miui.appmanager.AppManageUtils;
import com.miui.gamebooster.customview.b.f;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.n.d.n;
import com.miui.gamebooster.service.IVideoToolBox;
import com.miui.gamebooster.service.VideoToolBoxService;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.widget.ProgressBar;
import miuix.recyclerview.widget.RecyclerView;

public class VideoBoxAppManageActivity extends b.b.c.c.a implements f.a, b.c {

    /* renamed from: a  reason: collision with root package name */
    public static final List<String> f5181a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f5182b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ProgressBar f5183c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public f f5184d;
    /* access modifiers changed from: private */
    public List<n> e = new ArrayList();
    /* access modifiers changed from: private */
    public ArrayList<String> f = new ArrayList<>();
    /* access modifiers changed from: private */
    public IVideoToolBox g;
    private ServiceConnection h = new b(this);

    public class a extends AsyncTask<Void, Void, List<n>> {

        /* renamed from: a  reason: collision with root package name */
        Context f5185a;

        public a(Context context) {
            this.f5185a = context.getApplicationContext();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<n> doInBackground(Void... voidArr) {
            VideoBoxAppManageActivity videoBoxAppManageActivity = VideoBoxAppManageActivity.this;
            ArrayList unused = videoBoxAppManageActivity.f = f.a((ArrayList<String>) videoBoxAppManageActivity.f);
            for (String next : VideoBoxAppManageActivity.f5181a) {
                if (VideoBoxAppManageActivity.this.f.contains(next)) {
                    VideoBoxAppManageActivity.this.f.remove(next);
                }
            }
            ArrayList<String> a2 = com.miui.common.persistence.b.a("gb_added_games", (ArrayList<String>) new ArrayList());
            b a3 = b.a(this.f5185a);
            a3.a((b.c) VideoBoxAppManageActivity.this);
            List<PackageInfo> a4 = a3.a();
            ArrayList arrayList = new ArrayList();
            for (PackageInfo next2 : a4) {
                if (next2 != null) {
                    arrayList.add(next2.packageName);
                }
            }
            List a5 = VideoBoxAppManageActivity.this.a(this.f5185a.getPackageManager(), 0, AppManageUtils.h(this.f5185a));
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str = (String) it.next();
                try {
                    if (!a2.contains(str) && a5.contains(str) && !AppManageUtils.g.contains(str)) {
                        if (!VideoBoxAppManageActivity.f5181a.contains(str)) {
                            c a6 = a3.a(str);
                            n nVar = new n(str, "pkg_icon://".concat(str), a6.a(), a6.b(), VideoBoxAppManageActivity.this.f.contains(str));
                            nVar.a(VideoBoxAppManageActivity.this.f.contains(str));
                            arrayList2.add(nVar);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(arrayList2, new n.a());
            return arrayList2;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<n> list) {
            VideoBoxAppManageActivity.this.e.clear();
            VideoBoxAppManageActivity.this.e.addAll(list);
            if (VideoBoxAppManageActivity.this.f5184d != null) {
                VideoBoxAppManageActivity.this.f5184d.a(VideoBoxAppManageActivity.this.e);
                VideoBoxAppManageActivity.this.f5184d.notifyDataSetChanged();
            }
            VideoBoxAppManageActivity.this.f5183c.setVisibility(8);
            VideoBoxAppManageActivity.this.n();
        }
    }

    static {
        f5181a.add("com.miui.securitycenter");
        f5181a.add("com.android.settings");
        f5181a.add("com.xiaomi.scanner");
        f5181a.add("com.android.deskclock");
        f5181a.add("com.miui.weather2");
        f5181a.add("com.miui.compass");
        f5181a.add("com.duokan.phone.remotecontroller");
    }

    /* access modifiers changed from: private */
    public List<String> a(PackageManager packageManager, int i, HashSet<ComponentName> hashSet) {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage((String) null);
        for (ResolveInfo next : AppManageUtils.a(packageManager, intent, 0, i)) {
            if (!hashSet.contains(new ComponentName(next.activityInfo.packageName, next.activityInfo.name))) {
                arrayList.add(next.activityInfo.packageName);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void l() {
        Log.i("VideoBoxAppManage", "refreshListForAppChange");
        List<PackageInfo> a2 = b.a(getApplicationContext()).a();
        ArrayList arrayList = new ArrayList();
        for (PackageInfo next : a2) {
            if (next != null) {
                arrayList.add(next.packageName);
            }
        }
        ArrayList arrayList2 = new ArrayList();
        for (n next2 : this.e) {
            if (!arrayList.contains(next2.c())) {
                arrayList2.add(next2);
            }
        }
        if (!arrayList2.isEmpty()) {
            this.e.removeAll(arrayList2);
        }
        this.f = f.a(this.f);
        runOnUiThread(new e(this));
    }

    private void m() {
        ServiceConnection serviceConnection = this.h;
        if (serviceConnection != null) {
            try {
                unbindService(serviceConnection);
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void n() {
        com.miui.securitycenter.n.a().b(new c(this));
    }

    public boolean a(View view, g gVar, int i) {
        return false;
    }

    public void b(View view, g gVar, int i) {
        n nVar = this.e.get(i);
        nVar.a(!nVar.d());
        boolean d2 = nVar.d();
        if (d2 && !this.f.contains(nVar.c())) {
            this.f.add(nVar.c());
            C0373d.a.b(nVar.c());
        } else if (!d2 && this.f.contains(nVar.c())) {
            this.f.remove(nVar.c());
        }
        this.f5184d.notifyDataSetChanged();
        n();
    }

    public void h() {
        com.miui.securitycenter.n.a().b(new d(this));
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.gamebooster.videobox.settings.VideoBoxAppManageActivity, com.miui.gamebooster.customview.b.f$a] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!e.a()) {
            Log.e("VideoBoxAppManage", "Device not support vtb!!!");
            finish();
            return;
        }
        setContentView(R.layout.videobox_manage_apps_layout);
        this.f5183c = findViewById(R.id.vb_progressBar);
        this.f5182b = (RecyclerView) findViewById(R.id.app_list);
        this.f5182b.setLayoutManager(new LinearLayoutManager(this));
        this.f5184d = new f(this);
        this.f5184d.a(new com.miui.gamebooster.videobox.adapter.a.a());
        this.f5184d.a((f.a) this);
        this.f5182b.setAdapter(this.f5184d);
        bindService(new Intent(this, VideoToolBoxService.class), this.h, 1);
        new a(this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        VideoBoxAppManageActivity.super.onDestroy();
        m();
    }
}
