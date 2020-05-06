package com.android.server.am;

import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerInternal;

public class JobServiceContextInjectorCompat {
    public static void cancelJob(int uid, int jobId) {
        JobSchedulerInternal jobScheduler = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
        if (jobScheduler != null) {
            jobScheduler.cancelJob(uid, jobId);
        }
    }
}
