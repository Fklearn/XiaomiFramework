package com.android.server.job.controllers;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.MiuiFgThread;
import com.android.server.job.JobSchedulerService;
import java.util.function.Predicate;

public class UltraPowerSaverController extends StateController {
    private static final String TAG = "JobScheduler.UltraPowerSaver";
    /* access modifiers changed from: private */
    public static final Uri ULTRA_POWER_SAVING_URI = Settings.System.getUriFor("power_supersave_mode_open");
    private Callback mCallback;
    private Handler mHandler = new Handler(MiuiFgThread.get().getLooper());
    private SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);

    public interface Callback {
        void onUltraPowerSaverChanged(boolean z);
    }

    private class SettingsObserver extends ContentObserver {
        private ContentResolver mResolver;

        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (UltraPowerSaverController.ULTRA_POWER_SAVING_URI.equals(uri)) {
                UltraPowerSaverController.this.updateUltraPowerSavingState(this.mResolver);
            }
        }

        public void start(ContentResolver resolver) {
            this.mResolver = resolver;
            resolver.registerContentObserver(UltraPowerSaverController.ULTRA_POWER_SAVING_URI, false, this, -1);
        }
    }

    public UltraPowerSaverController(JobSchedulerService service) {
        super(service);
    }

    /* access modifiers changed from: private */
    public void updateUltraPowerSavingState(ContentResolver resolver) {
        boolean ultraPowerSaving = false;
        if (Settings.System.getInt(resolver, "power_supersave_mode_open", 0) != 0) {
            ultraPowerSaving = true;
        }
        Slog.d(TAG, "updateUltraPowerSavingState: ultraPowerSaving = " + ultraPowerSaving);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onUltraPowerSaverChanged(ultraPowerSaving);
        }
    }

    public void registerObserver() {
        this.mSettingsObserver.start(this.mContext.getContentResolver());
    }

    public void setCallback(Callback cb) {
        this.mCallback = cb;
    }

    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus lastJob) {
    }

    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus incomingJob, boolean forUpdate) {
    }

    public void dumpControllerStateLocked(IndentingPrintWriter pw, Predicate<JobStatus> predicate) {
    }

    public void dumpControllerStateLocked(ProtoOutputStream proto, long fieldId, Predicate<JobStatus> predicate) {
    }
}
