package com.miui.antivirus.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import b.b.b.d.n;
import b.b.b.i;
import b.b.c.h.f;
import com.miui.activityutil.o;
import com.miui.antispam.service.a.b;
import com.miui.guardprovider.VirusObserver;
import com.miui.powercenter.utils.d;
import com.miui.securitycenter.h;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.os.SystemProperties;

public class VirusAutoUpdateJobService extends JobService {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Handler f2879a = new Handler(Looper.getMainLooper());

    private class a extends AsyncTask<Void, Void, Void> {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public b f2880a;

        a(b bVar) {
            this.f2880a = bVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            com.miui.guardprovider.b a2 = com.miui.guardprovider.b.a(VirusAutoUpdateJobService.this.getApplicationContext());
            i a3 = i.a(VirusAutoUpdateJobService.this.getApplicationContext());
            a3.a((VirusObserver) new m(this, a3, a2));
            return null;
        }
    }

    public class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private AtomicBoolean f2882a = new AtomicBoolean(false);

        /* renamed from: b  reason: collision with root package name */
        private int f2883b = 0;

        /* renamed from: c  reason: collision with root package name */
        private JobParameters f2884c;

        public b(JobParameters jobParameters) {
            this.f2884c = jobParameters;
        }

        public synchronized void a() {
            int i = this.f2883b + 1;
            this.f2883b = i;
            if (i > 2) {
                run();
            }
        }

        public void run() {
            if (this.f2882a.compareAndSet(false, true)) {
                VirusAutoUpdateJobService.this.jobFinished(this.f2884c, false);
                Log.i("VirusAutoUpdateJobService", "jobFinished");
            }
        }
    }

    private void a() {
        Log.w("VirusAutoUpdateJobService", "updateAvlURLRules");
        new Thread(new k(this)).start();
    }

    public static void a(Context context) {
        if (SystemProperties.getBoolean("persist.sys.miui_optimization", !o.f2310b.equals(SystemProperties.get("ro.miui.cts"))) && !a(context, 99)) {
            int schedule = ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(99, new ComponentName(context, VirusAutoUpdateJobService.class)).setPeriodic(86400000).setPersisted(true).setRequiredNetworkType(2).build());
            Log.i("VirusAutoUpdateJobService", "setVirusUpdateJob() result : " + schedule);
        }
    }

    private void a(b bVar) {
        new a(bVar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static boolean a(Context context, int i) {
        List<JobInfo> allPendingJobs = ((JobScheduler) context.getSystemService("jobscheduler")).getAllPendingJobs();
        if (allPendingJobs == null) {
            return false;
        }
        for (JobInfo id : allPendingJobs) {
            if (id.getId() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("VirusAutoUpdateJobService", "onStartJob: " + jobParameters.getJobId());
        if (f.j(this) && h.i()) {
            com.miui.superpower.c.a.a();
        }
        if (!b.b.c.j.f.c(this) || !h.i()) {
            return false;
        }
        b bVar = new b(jobParameters);
        com.miui.antispam.service.a.b.a((Context) this).a((Runnable) bVar, (b.C0036b) null, false);
        if (!n.l(this)) {
            Log.i("VirusAutoUpdateJobService", "NOT IN FBE MODE");
            a(bVar);
            a();
        } else {
            Log.i("VirusAutoUpdateJobService", "IN FBE MODE");
        }
        d.a().a((Runnable) bVar);
        this.f2879a.postDelayed(bVar, 12000);
        return true;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("VirusAutoUpdateJobService", "onStartJob:" + jobParameters.getJobId());
        return false;
    }
}
