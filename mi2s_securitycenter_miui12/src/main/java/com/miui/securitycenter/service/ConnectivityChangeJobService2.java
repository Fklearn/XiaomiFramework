package com.miui.securitycenter.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;
import b.b.c.j.d;
import b.b.c.j.g;
import com.miui.antispam.service.AntiSpamService;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.monthreport.l;
import com.miui.networkassistant.xman.XmanHelper;
import com.miui.networkassistant.zman.ZmanHelper;
import com.miui.push.b;
import java.util.List;
import miui.os.Build;

public class ConnectivityChangeJobService2 extends JobService {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7516a = "ConnectivityChangeJobService2";

    private class a extends AsyncTask<Void, Void, Void> {
        private a() {
        }

        /* synthetic */ a(ConnectivityChangeJobService2 connectivityChangeJobService2, a aVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            long currentTimeMillis = System.currentTimeMillis() - 604800000;
            List<String> b2 = C0378i.b(ConnectivityChangeJobService2.this.getApplicationContext(), currentTimeMillis);
            if (b2 == null || b2.size() <= 0) {
                return null;
            }
            for (int size = b2.size() - 1; size >= 0; size--) {
                C0378i.a(b2.get(size));
            }
            C0378i.a(ConnectivityChangeJobService2.this.getApplicationContext(), currentTimeMillis);
            return null;
        }
    }

    private void a() {
        new a(this, (a) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private static boolean a(JobScheduler jobScheduler, int i) {
        List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
        if (allPendingJobs != null) {
            for (JobInfo next : allPendingJobs) {
                if (next != null && next.getId() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    private void b() {
        d.a(new c(this));
    }

    private void c() {
        d.a(new b(this));
    }

    private void d() {
        d.a(new d(this));
    }

    public static void d(Context context) {
        Log.i(f7516a, "setSchedule:");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (jobScheduler != null && !a(jobScheduler, 210100)) {
            jobScheduler.schedule(new JobInfo.Builder(210100, new ComponentName(context, ConnectivityChangeJobService2.class)).setPeriodic(86400000).setPersisted(true).setRequiredNetworkType(1).build());
        }
    }

    /* access modifiers changed from: private */
    public static void e(Context context) {
        b.b(context);
    }

    /* access modifiers changed from: private */
    public static void f(Context context) {
        if (C0388t.u()) {
            com.miui.gamebooster.o.a.a.b(context);
        }
    }

    /* access modifiers changed from: private */
    public static void g(Context context) {
        if (e.a() && f.b(context)) {
            com.miui.gamebooster.videobox.settings.a.b(context);
            if (com.miui.gamebooster.videobox.utils.f.a()) {
                com.miui.gamebooster.videobox.settings.a.a(context);
            }
        }
    }

    private void h(Context context) {
        Intent intent = new Intent(context, AntiSpamService.class);
        intent.setAction(AntiSpamService.f2390b);
        try {
            b.b.o.g.e.b(context, "startServiceAsUser", new Class[]{Intent.class, UserHandle.class}, intent, UserHandle.CURRENT_OR_SELF);
        } catch (Exception e) {
            Log.e(f7516a, "startCloudPhoneListUpdate exception: ", e);
        }
    }

    private void i(Context context) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            try {
                b.b.o.g.e.b(context, "startServiceAsUser", new Class[]{Intent.class, UserHandle.class}, new Intent(context, CloudThirdDesktopService.class), UserHandle.CURRENT_OR_SELF);
            } catch (Exception e) {
                Log.e(f7516a, "startCloudThirdDesktopUpdate exception: ", e);
            }
        }
    }

    private static void j(Context context) {
        d.a(new a(context));
    }

    public boolean onStartJob(JobParameters jobParameters) {
        String str = f7516a;
        Log.i(str, "onStartJob:" + jobParameters.getJobId());
        j(this);
        l.a((Context) this).d();
        if (g.a(this) == 0) {
            h(this);
            i(this);
        }
        c();
        b();
        d();
        XmanHelper.checkXmanCloudDataAsync(this);
        ZmanHelper.checkZmanCloudDataAsync(this);
        a();
        return false;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        String str = f7516a;
        Log.i(str, "onStopJob:" + jobParameters.getJobId());
        return false;
    }
}
