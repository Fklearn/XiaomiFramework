package com.google.android.exoplayer2.offline;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public final class DownloadManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_MAX_SIMULTANEOUS_DOWNLOADS = 1;
    public static final int DEFAULT_MIN_RETRY_COUNT = 5;
    private static final String TAG = "DownloadManager";
    /* access modifiers changed from: private */
    public final ActionFile actionFile;
    private final ArrayList<Task> activeDownloadTasks;
    /* access modifiers changed from: private */
    public final DownloadAction.Deserializer[] deserializers;
    /* access modifiers changed from: private */
    public final DownloaderConstructorHelper downloaderConstructorHelper;
    private boolean downloadsStopped;
    private final Handler fileIOHandler;
    private final HandlerThread fileIOThread;
    /* access modifiers changed from: private */
    public final Handler handler;
    /* access modifiers changed from: private */
    public boolean initialized;
    /* access modifiers changed from: private */
    public final CopyOnWriteArraySet<Listener> listeners;
    private final int maxActiveDownloadTasks;
    private final int minRetryCount;
    private int nextTaskId;
    /* access modifiers changed from: private */
    public boolean released;
    /* access modifiers changed from: private */
    public final ArrayList<Task> tasks;

    public interface Listener {
        void onIdle(DownloadManager downloadManager);

        void onInitialized(DownloadManager downloadManager);

        void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState);
    }

    private static final class Task implements Runnable {
        public static final int STATE_QUEUED_CANCELING = 5;
        public static final int STATE_STARTED_CANCELING = 6;
        public static final int STATE_STARTED_STOPPING = 7;
        /* access modifiers changed from: private */
        public final DownloadAction action;
        /* access modifiers changed from: private */
        public volatile int currentState;
        private final DownloadManager downloadManager;
        private volatile Downloader downloader;
        private Throwable error;
        /* access modifiers changed from: private */
        public final int id;
        private final int minRetryCount;
        private Thread thread;

        @Retention(RetentionPolicy.SOURCE)
        public @interface InternalState {
        }

        private Task(int i, DownloadManager downloadManager2, DownloadAction downloadAction, int i2) {
            this.id = i;
            this.downloadManager = downloadManager2;
            this.action = downloadAction;
            this.currentState = 0;
            this.minRetryCount = i2;
        }

        /* access modifiers changed from: private */
        public boolean canStart() {
            return this.currentState == 0;
        }

        /* access modifiers changed from: private */
        public void cancel() {
            if (changeStateAndNotify(0, 5)) {
                this.downloadManager.handler.post(new Runnable() {
                    public void run() {
                        boolean unused = Task.this.changeStateAndNotify(5, 3);
                    }
                });
            } else if (changeStateAndNotify(1, 6)) {
                cancelDownload();
            }
        }

        private void cancelDownload() {
            if (this.downloader != null) {
                this.downloader.cancel();
            }
            this.thread.interrupt();
        }

        /* access modifiers changed from: private */
        public boolean changeStateAndNotify(int i, int i2) {
            return changeStateAndNotify(i, i2, (Throwable) null);
        }

        /* access modifiers changed from: private */
        public boolean changeStateAndNotify(int i, int i2, Throwable th) {
            boolean z = false;
            if (this.currentState != i) {
                return false;
            }
            this.currentState = i2;
            this.error = th;
            if (this.currentState != getExternalState()) {
                z = true;
            }
            if (!z) {
                this.downloadManager.onTaskStateChange(this);
            }
            return true;
        }

        private int getExternalState() {
            int i = this.currentState;
            if (i == 5) {
                return 0;
            }
            if (i == 6 || i == 7) {
                return 1;
            }
            return this.currentState;
        }

        private int getRetryDelayMillis(int i) {
            return Math.min((i - 1) * 1000, 5000);
        }

        private String getStateString() {
            int i = this.currentState;
            return (i == 5 || i == 6) ? "CANCELING" : i != 7 ? TaskState.getStateString(this.currentState) : "STOPPING";
        }

        /* access modifiers changed from: private */
        public void start() {
            if (changeStateAndNotify(0, 1)) {
                this.thread = new Thread(this);
                this.thread.start();
            }
        }

        /* access modifiers changed from: private */
        public void stop() {
            if (changeStateAndNotify(1, 7)) {
                DownloadManager.logd("Stopping", this);
                this.thread.interrupt();
            }
        }

        private static String toString(byte[] bArr) {
            if (bArr.length > 100) {
                return "<data is too long>";
            }
            return '\'' + Util.fromUtf8Bytes(bArr) + '\'';
        }

        public float getDownloadPercentage() {
            if (this.downloader != null) {
                return this.downloader.getDownloadPercentage();
            }
            return -1.0f;
        }

        public TaskState getDownloadState() {
            return new TaskState(this.id, this.action, getExternalState(), getDownloadPercentage(), getDownloadedBytes(), this.error);
        }

        public long getDownloadedBytes() {
            if (this.downloader != null) {
                return this.downloader.getDownloadedBytes();
            }
            return 0;
        }

        public boolean isActive() {
            return this.currentState == 5 || this.currentState == 1 || this.currentState == 7 || this.currentState == 6;
        }

        public boolean isFinished() {
            return this.currentState == 4 || this.currentState == 2 || this.currentState == 3;
        }

        public void run() {
            long j;
            int i;
            DownloadManager.logd("Task is started", this);
            try {
                this.downloader = this.action.createDownloader(this.downloadManager.downloaderConstructorHelper);
                if (this.action.isRemoveAction) {
                    this.downloader.remove();
                } else {
                    j = -1;
                    i = 0;
                    while (!Thread.interrupted()) {
                        this.downloader.download();
                    }
                }
                th = null;
            } catch (IOException e) {
                long downloadedBytes = this.downloader.getDownloadedBytes();
                if (downloadedBytes != j) {
                    DownloadManager.logd("Reset error count. downloadedBytes = " + downloadedBytes, this);
                    i = 0;
                    j = downloadedBytes;
                }
                if (this.currentState != 1 || (i = i + 1) > this.minRetryCount) {
                    throw e;
                }
                DownloadManager.logd("Download error. Retry " + i, this);
                Thread.sleep((long) getRetryDelayMillis(i));
            } catch (Throwable th) {
                th = th;
            }
            this.downloadManager.handler.post(new Runnable() {
                public void run() {
                    if (!Task.this.changeStateAndNotify(1, th != null ? 4 : 2, th) && !Task.this.changeStateAndNotify(6, 3) && !Task.this.changeStateAndNotify(7, 0)) {
                        throw new IllegalStateException();
                    }
                }
            });
        }

        public String toString() {
            return super.toString();
        }
    }

    public static final class TaskState {
        public static final int STATE_CANCELED = 3;
        public static final int STATE_COMPLETED = 2;
        public static final int STATE_FAILED = 4;
        public static final int STATE_QUEUED = 0;
        public static final int STATE_STARTED = 1;
        public final DownloadAction action;
        public final float downloadPercentage;
        public final long downloadedBytes;
        public final Throwable error;
        public final int state;
        public final int taskId;

        @Retention(RetentionPolicy.SOURCE)
        public @interface State {
        }

        private TaskState(int i, DownloadAction downloadAction, int i2, float f, long j, Throwable th) {
            this.taskId = i;
            this.action = downloadAction;
            this.state = i2;
            this.downloadPercentage = f;
            this.downloadedBytes = j;
            this.error = th;
        }

        public static String getStateString(int i) {
            if (i == 0) {
                return "QUEUED";
            }
            if (i == 1) {
                return "STARTED";
            }
            if (i == 2) {
                return "COMPLETED";
            }
            if (i == 3) {
                return "CANCELED";
            }
            if (i == 4) {
                return "FAILED";
            }
            throw new IllegalStateException();
        }
    }

    public DownloadManager(DownloaderConstructorHelper downloaderConstructorHelper2, int i, int i2, File file, DownloadAction.Deserializer... deserializerArr) {
        Assertions.checkArgument(deserializerArr.length > 0, "At least one Deserializer is required.");
        this.downloaderConstructorHelper = downloaderConstructorHelper2;
        this.maxActiveDownloadTasks = i;
        this.minRetryCount = i2;
        this.actionFile = new ActionFile(file);
        this.deserializers = deserializerArr;
        this.downloadsStopped = true;
        this.tasks = new ArrayList<>();
        this.activeDownloadTasks = new ArrayList<>();
        Looper myLooper = Looper.myLooper();
        this.handler = new Handler(myLooper == null ? Looper.getMainLooper() : myLooper);
        this.fileIOThread = new HandlerThread("DownloadManager file i/o");
        this.fileIOThread.start();
        this.fileIOHandler = new Handler(this.fileIOThread.getLooper());
        this.listeners = new CopyOnWriteArraySet<>();
        loadActions();
        logd("Created");
    }

    public DownloadManager(DownloaderConstructorHelper downloaderConstructorHelper2, File file, DownloadAction.Deserializer... deserializerArr) {
        this(downloaderConstructorHelper2, 1, 5, file, deserializerArr);
    }

    public DownloadManager(Cache cache, DataSource.Factory factory, File file, DownloadAction.Deserializer... deserializerArr) {
        this(new DownloaderConstructorHelper(cache, factory), file, deserializerArr);
    }

    /* access modifiers changed from: private */
    public Task addTaskForAction(DownloadAction downloadAction) {
        int i = this.nextTaskId;
        this.nextTaskId = i + 1;
        Task task = new Task(i, this, downloadAction, this.minRetryCount);
        this.tasks.add(task);
        logd("Task is added", task);
        return task;
    }

    private void loadActions() {
        this.fileIOHandler.post(new Runnable() {
            public void run() {
                final DownloadAction[] downloadActionArr;
                try {
                    downloadActionArr = DownloadManager.this.actionFile.load(DownloadManager.this.deserializers);
                    DownloadManager.logd("Action file is loaded.");
                } catch (Throwable th) {
                    Log.e(DownloadManager.TAG, "Action file loading failed.", th);
                    downloadActionArr = new DownloadAction[0];
                }
                DownloadManager.this.handler.post(new Runnable() {
                    public void run() {
                        if (!DownloadManager.this.released) {
                            ArrayList arrayList = new ArrayList(DownloadManager.this.tasks);
                            DownloadManager.this.tasks.clear();
                            for (DownloadAction access$1300 : downloadActionArr) {
                                Task unused = DownloadManager.this.addTaskForAction(access$1300);
                            }
                            DownloadManager.logd("Tasks are created.");
                            boolean unused2 = DownloadManager.this.initialized = true;
                            Iterator it = DownloadManager.this.listeners.iterator();
                            while (it.hasNext()) {
                                ((Listener) it.next()).onInitialized(DownloadManager.this);
                            }
                            if (!arrayList.isEmpty()) {
                                DownloadManager.this.tasks.addAll(arrayList);
                                DownloadManager.this.saveActions();
                            }
                            DownloadManager.this.maybeStartTasks();
                            for (int i = 0; i < DownloadManager.this.tasks.size(); i++) {
                                Task task = (Task) DownloadManager.this.tasks.get(i);
                                if (task.currentState == 0) {
                                    DownloadManager.this.notifyListenersTaskStateChange(task);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public static void logd(String str) {
    }

    /* access modifiers changed from: private */
    public static void logd(String str, Task task) {
        logd(str + ": " + task);
    }

    private void maybeNotifyListenersIdle() {
        if (isIdle()) {
            logd("Notify idle state");
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                it.next().onIdle(this);
            }
        }
    }

    /* access modifiers changed from: private */
    public void maybeStartTasks() {
        DownloadAction access$300;
        boolean z;
        if (this.initialized && !this.released) {
            boolean z2 = this.downloadsStopped || this.activeDownloadTasks.size() == this.maxActiveDownloadTasks;
            for (int i = 0; i < this.tasks.size(); i++) {
                Task task = this.tasks.get(i);
                if (task.canStart() && ((z = access$300.isRemoveAction) || !z2)) {
                    int i2 = 0;
                    boolean z3 = true;
                    while (true) {
                        if (i2 >= i) {
                            break;
                        }
                        Task task2 = this.tasks.get(i2);
                        if (task2.action.isSameMedia((access$300 = task.action))) {
                            if (z) {
                                logd(task + " clashes with " + task2);
                                task2.cancel();
                                z3 = false;
                            } else if (task2.action.isRemoveAction) {
                                z3 = false;
                                z2 = true;
                                break;
                            }
                        }
                        i2++;
                    }
                    if (z3) {
                        task.start();
                        if (!z) {
                            this.activeDownloadTasks.add(task);
                            z2 = this.activeDownloadTasks.size() == this.maxActiveDownloadTasks;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyListenersTaskStateChange(Task task) {
        logd("Task state is changed", task);
        TaskState downloadState = task.getDownloadState();
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onTaskStateChanged(this, downloadState);
        }
    }

    /* access modifiers changed from: private */
    public void onTaskStateChange(Task task) {
        if (!this.released) {
            boolean z = !task.isActive();
            if (z) {
                this.activeDownloadTasks.remove(task);
            }
            notifyListenersTaskStateChange(task);
            if (task.isFinished()) {
                this.tasks.remove(task);
                saveActions();
            }
            if (z) {
                maybeStartTasks();
                maybeNotifyListenersIdle();
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveActions() {
        if (!this.released) {
            final DownloadAction[] downloadActionArr = new DownloadAction[this.tasks.size()];
            for (int i = 0; i < this.tasks.size(); i++) {
                downloadActionArr[i] = this.tasks.get(i).action;
            }
            this.fileIOHandler.post(new Runnable() {
                public void run() {
                    try {
                        DownloadManager.this.actionFile.store(downloadActionArr);
                        DownloadManager.logd("Actions persisted.");
                    } catch (IOException e) {
                        Log.e(DownloadManager.TAG, "Persisting actions failed.", e);
                    }
                }
            });
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public TaskState[] getAllTaskStates() {
        Assertions.checkState(!this.released);
        TaskState[] taskStateArr = new TaskState[this.tasks.size()];
        for (int i = 0; i < taskStateArr.length; i++) {
            taskStateArr[i] = this.tasks.get(i).getDownloadState();
        }
        return taskStateArr;
    }

    public int getDownloadCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.tasks.size(); i2++) {
            if (!this.tasks.get(i2).action.isRemoveAction) {
                i++;
            }
        }
        return i;
    }

    public int getTaskCount() {
        Assertions.checkState(!this.released);
        return this.tasks.size();
    }

    @Nullable
    public TaskState getTaskState(int i) {
        Assertions.checkState(!this.released);
        for (int i2 = 0; i2 < this.tasks.size(); i2++) {
            Task task = this.tasks.get(i2);
            if (task.id == i) {
                return task.getDownloadState();
            }
        }
        return null;
    }

    public int handleAction(DownloadAction downloadAction) {
        Assertions.checkState(!this.released);
        Task addTaskForAction = addTaskForAction(downloadAction);
        if (this.initialized) {
            saveActions();
            maybeStartTasks();
            if (addTaskForAction.currentState == 0) {
                notifyListenersTaskStateChange(addTaskForAction);
            }
        }
        return addTaskForAction.id;
    }

    public int handleAction(byte[] bArr) {
        Assertions.checkState(!this.released);
        return handleAction(DownloadAction.deserializeFromStream(this.deserializers, new ByteArrayInputStream(bArr)));
    }

    public boolean isIdle() {
        Assertions.checkState(!this.released);
        if (!this.initialized) {
            return false;
        }
        for (int i = 0; i < this.tasks.size(); i++) {
            if (this.tasks.get(i).isActive()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInitialized() {
        Assertions.checkState(!this.released);
        return this.initialized;
    }

    public void release() {
        if (!this.released) {
            this.released = true;
            for (int i = 0; i < this.tasks.size(); i++) {
                this.tasks.get(i).stop();
            }
            final ConditionVariable conditionVariable = new ConditionVariable();
            this.fileIOHandler.post(new Runnable() {
                public void run() {
                    conditionVariable.open();
                }
            });
            conditionVariable.block();
            this.fileIOThread.quit();
            logd("Released");
        }
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void startDownloads() {
        Assertions.checkState(!this.released);
        if (this.downloadsStopped) {
            this.downloadsStopped = false;
            maybeStartTasks();
            logd("Downloads are started");
        }
    }

    public void stopDownloads() {
        Assertions.checkState(!this.released);
        if (!this.downloadsStopped) {
            this.downloadsStopped = true;
            for (int i = 0; i < this.activeDownloadTasks.size(); i++) {
                this.activeDownloadTasks.get(i).stop();
            }
            logd("Downloads are stopping");
        }
    }
}
