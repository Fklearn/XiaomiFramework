package com.android.server.am;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.MiuiFgThread;
import com.android.server.job.JobServiceContext;
import com.android.server.job.controllers.JobStatus;

public class JobServiceContextInjector {
    protected static final String TAG = "JobServiceContext";

    public static boolean bindService(JobServiceContext jobContext, Context context, Intent service, JobStatus job) {
        if (AutoStartManagerService.isAllowStartService(context, service, job.getUserId(), job.getUid())) {
            return context.bindServiceAsUser(service, jobContext, 261, MiuiFgThread.getHandler(), new UserHandle(job.getUserId()));
        }
        int jobId = job.getJobId();
        int uid = job.getUid();
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        JobServiceContextInjectorCompat.cancelJob(job.getUid(), jobId);
        Slog.i(TAG, "MIUILOG- Reject Start Job, cancelJob uid : " + uid + " jobId :" + jobId);
        return false;
    }
}
