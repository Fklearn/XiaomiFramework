package com.android.server.wm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.wm.PersisterQueue;
import com.android.server.wm.TaskPersister;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.function.Predicate;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class TaskPersister implements PersisterQueue.Listener {
    static final boolean DEBUG = false;
    private static final String IMAGES_DIRNAME = "recent_images";
    static final String IMAGE_EXTENSION = ".png";
    private static final String PERSISTED_TASK_IDS_FILENAME = "persisted_taskIds.txt";
    static final String TAG = "TaskPersister";
    private static final String TAG_TASK = "task";
    private static final String TASKS_DIRNAME = "recent_tasks";
    private static final String TASK_FILENAME_SUFFIX = "_task.xml";
    private final Object mIoLock = new Object();
    private final PersisterQueue mPersisterQueue;
    private final RecentTasks mRecentTasks;
    private final ActivityTaskManagerService mService;
    private final ActivityStackSupervisor mStackSupervisor;
    private final File mTaskIdsDir;
    private final SparseArray<SparseBooleanArray> mTaskIdsInFile = new SparseArray<>();
    private final ArraySet<Integer> mTmpTaskIds = new ArraySet<>();

    TaskPersister(File systemDir, ActivityStackSupervisor stackSupervisor, ActivityTaskManagerService service, RecentTasks recentTasks, PersisterQueue persisterQueue) {
        File legacyImagesDir = new File(systemDir, IMAGES_DIRNAME);
        if (legacyImagesDir.exists() && (!FileUtils.deleteContents(legacyImagesDir) || !legacyImagesDir.delete())) {
            Slog.i(TAG, "Failure deleting legacy images directory: " + legacyImagesDir);
        }
        File legacyTasksDir = new File(systemDir, TASKS_DIRNAME);
        if (legacyTasksDir.exists() && (!FileUtils.deleteContents(legacyTasksDir) || !legacyTasksDir.delete())) {
            Slog.i(TAG, "Failure deleting legacy tasks directory: " + legacyTasksDir);
        }
        this.mTaskIdsDir = new File(Environment.getDataDirectory(), "system_de");
        this.mStackSupervisor = stackSupervisor;
        this.mService = service;
        this.mRecentTasks = recentTasks;
        this.mPersisterQueue = persisterQueue;
        this.mPersisterQueue.addListener(this);
    }

    @VisibleForTesting
    TaskPersister(File workingDir) {
        this.mTaskIdsDir = workingDir;
        this.mStackSupervisor = null;
        this.mService = null;
        this.mRecentTasks = null;
        this.mPersisterQueue = new PersisterQueue();
        this.mPersisterQueue.addListener(this);
    }

    private void removeThumbnails(TaskRecord task) {
        this.mPersisterQueue.removeItems(new Predicate() {
            public final boolean test(Object obj) {
                return new File(((TaskPersister.ImageWriteQueueItem) obj).mFilePath).getName().startsWith(Integer.toString(TaskRecord.this.taskId));
            }
        }, ImageWriteQueueItem.class);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* access modifiers changed from: package-private */
    public SparseBooleanArray loadPersistedTaskIdsForUser(int userId) {
        if (this.mTaskIdsInFile.get(userId) != null) {
            return this.mTaskIdsInFile.get(userId).clone();
        }
        SparseBooleanArray persistedTaskIds = new SparseBooleanArray();
        synchronized (this.mIoLock) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(getUserPersistedTaskIdsFile(userId)));
                while (true) {
                    String readLine = reader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    for (String taskIdString : line.split("\\s+")) {
                        persistedTaskIds.put(Integer.parseInt(taskIdString), true);
                    }
                }
                IoUtils.closeQuietly(reader);
            } catch (FileNotFoundException e) {
                IoUtils.closeQuietly((AutoCloseable) null);
            } catch (Exception e2) {
                try {
                    Slog.e(TAG, "Error while reading taskIds file for user " + userId, e2);
                    IoUtils.closeQuietly((AutoCloseable) null);
                } catch (Throwable th) {
                    IoUtils.closeQuietly((AutoCloseable) null);
                    throw th;
                }
            }
        }
        this.mTaskIdsInFile.put(userId, persistedTaskIds);
        return persistedTaskIds.clone();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void writePersistedTaskIdsForUser(SparseBooleanArray taskIds, int userId) {
        if (userId >= 0) {
            File persistedTaskIdsFile = getUserPersistedTaskIdsFile(userId);
            synchronized (this.mIoLock) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(persistedTaskIdsFile));
                    for (int i = 0; i < taskIds.size(); i++) {
                        if (taskIds.valueAt(i)) {
                            writer.write(String.valueOf(taskIds.keyAt(i)));
                            writer.newLine();
                        }
                    }
                    IoUtils.closeQuietly(writer);
                } catch (Exception e) {
                    try {
                        Slog.e(TAG, "Error while writing taskIds file for user " + userId, e);
                        IoUtils.closeQuietly((AutoCloseable) null);
                    } catch (Throwable th) {
                        IoUtils.closeQuietly((AutoCloseable) null);
                        throw th;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unloadUserDataFromMemory(int userId) {
        this.mTaskIdsInFile.delete(userId);
    }

    /* access modifiers changed from: package-private */
    public void wakeup(TaskRecord task, boolean flush) {
        synchronized (this.mPersisterQueue) {
            if (task != null) {
                TaskWriteQueueItem item = (TaskWriteQueueItem) this.mPersisterQueue.findLastItem(new Predicate() {
                    public final boolean test(Object obj) {
                        return TaskPersister.lambda$wakeup$1(TaskRecord.this, (TaskPersister.TaskWriteQueueItem) obj);
                    }
                }, TaskWriteQueueItem.class);
                if (item != null && !task.inRecents) {
                    removeThumbnails(task);
                }
                if (item == null && task.isPersistable) {
                    this.mPersisterQueue.addItem(new TaskWriteQueueItem(task, this.mService), flush);
                }
            } else {
                this.mPersisterQueue.addItem(PersisterQueue.EMPTY_ITEM, flush);
            }
        }
        this.mPersisterQueue.yieldIfQueueTooDeep();
    }

    static /* synthetic */ boolean lambda$wakeup$1(TaskRecord task, TaskWriteQueueItem queueItem) {
        return task == queueItem.mTask;
    }

    /* access modifiers changed from: package-private */
    public void flush() {
        this.mPersisterQueue.flush();
    }

    /* access modifiers changed from: package-private */
    public void saveImage(Bitmap image, String filePath) {
        this.mPersisterQueue.updateLastOrAddItem(new ImageWriteQueueItem(filePath, image), false);
    }

    /* access modifiers changed from: package-private */
    public Bitmap getTaskDescriptionIcon(String filePath) {
        Bitmap icon = getImageFromWriteQueue(filePath);
        if (icon != null) {
            return icon;
        }
        return restoreImage(filePath);
    }

    private Bitmap getImageFromWriteQueue(String filePath) {
        ImageWriteQueueItem item = (ImageWriteQueueItem) this.mPersisterQueue.findLastItem(new Predicate(filePath) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((TaskPersister.ImageWriteQueueItem) obj).mFilePath.equals(this.f$0);
            }
        }, ImageWriteQueueItem.class);
        if (item != null) {
            return item.mImage;
        }
        return null;
    }

    private String fileToString(File file) {
        String newline = System.lineSeparator();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer(((int) file.length()) * 2);
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine != null) {
                    sb.append(line + newline);
                } else {
                    reader.close();
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            Slog.e(TAG, "Couldn't read file " + file.getName());
            return null;
        }
    }

    private TaskRecord taskIdToTask(int taskId, ArrayList<TaskRecord> tasks) {
        if (taskId < 0) {
            return null;
        }
        for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = tasks.get(taskNdx);
            if (task.taskId == taskId) {
                return task;
            }
        }
        Slog.e(TAG, "Restore affiliation error looking for taskId=" + taskId);
        return null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01f8 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.android.server.wm.TaskRecord> restoreTasksForUserLocked(int r19, android.util.SparseBooleanArray r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            android.util.ArraySet r0 = new android.util.ArraySet
            r0.<init>()
            r4 = r0
            java.io.File r5 = getUserTasksDir(r19)
            java.io.File[] r6 = r5.listFiles()
            java.lang.String r7 = "TaskPersister"
            if (r6 != 0) goto L_0x0031
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "restoreTasksForUserLocked: Unable to list files from "
            r0.append(r8)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Slog.e(r7, r0)
            return r3
        L_0x0031:
            r0 = 0
            r8 = r0
        L_0x0033:
            int r0 = r6.length
            r9 = 1
            if (r8 >= r0) goto L_0x01fe
            r10 = r6[r8]
            java.lang.String r0 = r10.getName()
            java.lang.String r11 = "_task.xml"
            boolean r0 = r0.endsWith(r11)
            if (r0 != 0) goto L_0x004b
            r16 = r6
            r17 = r8
            goto L_0x01f8
        L_0x004b:
            java.lang.String r0 = r10.getName()     // Catch:{ NumberFormatException -> 0x01ed }
            java.lang.String r12 = r10.getName()     // Catch:{ NumberFormatException -> 0x01ed }
            int r12 = r12.length()     // Catch:{ NumberFormatException -> 0x01ed }
            int r11 = r11.length()     // Catch:{ NumberFormatException -> 0x01ed }
            int r12 = r12 - r11
            r11 = 0
            java.lang.String r0 = r0.substring(r11, r12)     // Catch:{ NumberFormatException -> 0x01ed }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x01ed }
            r12 = r20
            boolean r11 = r12.get(r0, r11)     // Catch:{ NumberFormatException -> 0x01ed }
            if (r11 == 0) goto L_0x0093
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x008c }
            r9.<init>()     // Catch:{ NumberFormatException -> 0x008c }
            java.lang.String r11 = "Task #"
            r9.append(r11)     // Catch:{ NumberFormatException -> 0x008c }
            r9.append(r0)     // Catch:{ NumberFormatException -> 0x008c }
            java.lang.String r11 = " has already been created so we don't restore again"
            r9.append(r11)     // Catch:{ NumberFormatException -> 0x008c }
            java.lang.String r9 = r9.toString()     // Catch:{ NumberFormatException -> 0x008c }
            android.util.Slog.w(r7, r9)     // Catch:{ NumberFormatException -> 0x008c }
            r16 = r6
            r17 = r8
            goto L_0x01f8
        L_0x008c:
            r0 = move-exception
            r16 = r6
            r17 = r8
            goto L_0x01f2
        L_0x0093:
            r11 = 0
            r13 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            java.io.FileReader r14 = new java.io.FileReader     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r14.<init>(r10)     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r0.<init>(r14)     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r11 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r0.setInput(r11)     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
        L_0x00a8:
            int r14 = r0.next()     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r15 = r14
            if (r14 == r9) goto L_0x0190
            r14 = 3
            if (r15 == r14) goto L_0x0190
            java.lang.String r14 = r0.getName()     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            r9 = 2
            if (r15 != r9) goto L_0x017e
            java.lang.String r9 = "task"
            boolean r9 = r9.equals(r14)     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            if (r9 == 0) goto L_0x015d
            com.android.server.wm.ActivityStackSupervisor r9 = r1.mStackSupervisor     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            com.android.server.wm.TaskRecord r9 = com.android.server.wm.TaskRecord.restoreFromXml(r0, r9)     // Catch:{ Exception -> 0x01a3, all -> 0x019d }
            if (r9 == 0) goto L_0x0138
            r16 = r6
            int r6 = r9.taskId     // Catch:{ Exception -> 0x0133, all -> 0x012e }
            com.android.server.wm.ActivityTaskManagerService r12 = r1.mService     // Catch:{ Exception -> 0x0133, all -> 0x012e }
            com.android.server.wm.RootActivityContainer r12 = r12.mRootActivityContainer     // Catch:{ Exception -> 0x0133, all -> 0x012e }
            r17 = r8
            r8 = 1
            com.android.server.wm.TaskRecord r12 = r12.anyTaskForId(r6, r8)     // Catch:{ Exception -> 0x018e }
            if (r12 == 0) goto L_0x00f4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018e }
            r8.<init>()     // Catch:{ Exception -> 0x018e }
            java.lang.String r12 = "Existing task with taskId "
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            r8.append(r6)     // Catch:{ Exception -> 0x018e }
            java.lang.String r12 = "found"
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x018e }
            android.util.Slog.wtf(r7, r8)     // Catch:{ Exception -> 0x018e }
            goto L_0x012d
        L_0x00f4:
            int r8 = r9.userId     // Catch:{ Exception -> 0x018e }
            if (r2 == r8) goto L_0x011b
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018e }
            r8.<init>()     // Catch:{ Exception -> 0x018e }
            java.lang.String r12 = "Task with userId "
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            int r12 = r9.userId     // Catch:{ Exception -> 0x018e }
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            java.lang.String r12 = " found in "
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            java.lang.String r12 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x018e }
            r8.append(r12)     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x018e }
            android.util.Slog.wtf(r7, r8)     // Catch:{ Exception -> 0x018e }
            goto L_0x012d
        L_0x011b:
            com.android.server.wm.ActivityStackSupervisor r8 = r1.mStackSupervisor     // Catch:{ Exception -> 0x018e }
            r8.setNextTaskIdForUserLocked(r6, r2)     // Catch:{ Exception -> 0x018e }
            r8 = 1
            r9.isPersistable = r8     // Catch:{ Exception -> 0x018e }
            r3.add(r9)     // Catch:{ Exception -> 0x018e }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x018e }
            r4.add(r8)     // Catch:{ Exception -> 0x018e }
        L_0x012d:
            goto L_0x015c
        L_0x012e:
            r0 = move-exception
            r17 = r8
            goto L_0x01e4
        L_0x0133:
            r0 = move-exception
            r17 = r8
            goto L_0x01a8
        L_0x0138:
            r16 = r6
            r17 = r8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018e }
            r6.<init>()     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = "restoreTasksForUserLocked: Unable to restore taskFile="
            r6.append(r8)     // Catch:{ Exception -> 0x018e }
            r6.append(r10)     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = ": "
            r6.append(r8)     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = r1.fileToString(r10)     // Catch:{ Exception -> 0x018e }
            r6.append(r8)     // Catch:{ Exception -> 0x018e }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x018e }
            android.util.Slog.e(r7, r6)     // Catch:{ Exception -> 0x018e }
        L_0x015c:
            goto L_0x0182
        L_0x015d:
            r16 = r6
            r17 = r8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018e }
            r6.<init>()     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = "restoreTasksForUserLocked: Unknown xml event="
            r6.append(r8)     // Catch:{ Exception -> 0x018e }
            r6.append(r15)     // Catch:{ Exception -> 0x018e }
            java.lang.String r8 = " name="
            r6.append(r8)     // Catch:{ Exception -> 0x018e }
            r6.append(r14)     // Catch:{ Exception -> 0x018e }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x018e }
            android.util.Slog.wtf(r7, r6)     // Catch:{ Exception -> 0x018e }
            goto L_0x0182
        L_0x017e:
            r16 = r6
            r17 = r8
        L_0x0182:
            com.android.internal.util.XmlUtils.skipCurrentTag(r0)     // Catch:{ Exception -> 0x018e }
            r12 = r20
            r6 = r16
            r8 = r17
            r9 = 1
            goto L_0x00a8
        L_0x018e:
            r0 = move-exception
            goto L_0x01a8
        L_0x0190:
            r16 = r6
            r17 = r8
            libcore.io.IoUtils.closeQuietly(r11)
            if (r13 == 0) goto L_0x01f8
            r10.delete()
            goto L_0x01f8
        L_0x019d:
            r0 = move-exception
            r16 = r6
            r17 = r8
            goto L_0x01e4
        L_0x01a3:
            r0 = move-exception
            r16 = r6
            r17 = r8
        L_0x01a8:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e3 }
            r6.<init>()     // Catch:{ all -> 0x01e3 }
            java.lang.String r8 = "Unable to parse "
            r6.append(r8)     // Catch:{ all -> 0x01e3 }
            r6.append(r10)     // Catch:{ all -> 0x01e3 }
            java.lang.String r8 = ". Error "
            r6.append(r8)     // Catch:{ all -> 0x01e3 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e3 }
            android.util.Slog.wtf(r7, r6, r0)     // Catch:{ all -> 0x01e3 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e3 }
            r6.<init>()     // Catch:{ all -> 0x01e3 }
            java.lang.String r8 = "Failing file: "
            r6.append(r8)     // Catch:{ all -> 0x01e3 }
            java.lang.String r8 = r1.fileToString(r10)     // Catch:{ all -> 0x01e3 }
            r6.append(r8)     // Catch:{ all -> 0x01e3 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e3 }
            android.util.Slog.e(r7, r6)     // Catch:{ all -> 0x01e3 }
            r0 = 1
            libcore.io.IoUtils.closeQuietly(r11)
            if (r0 == 0) goto L_0x01f8
            r10.delete()
            goto L_0x01f8
        L_0x01e3:
            r0 = move-exception
        L_0x01e4:
            libcore.io.IoUtils.closeQuietly(r11)
            if (r13 == 0) goto L_0x01ec
            r10.delete()
        L_0x01ec:
            throw r0
        L_0x01ed:
            r0 = move-exception
            r16 = r6
            r17 = r8
        L_0x01f2:
            java.lang.String r6 = "Unexpected task file name"
            android.util.Slog.w(r7, r6, r0)
        L_0x01f8:
            int r8 = r17 + 1
            r6 = r16
            goto L_0x0033
        L_0x01fe:
            r16 = r6
            r17 = r8
            java.io.File[] r0 = r5.listFiles()
            removeObsoleteFiles(r4, r0)
            int r0 = r3.size()
            r6 = 1
            int r0 = r0 - r6
        L_0x020f:
            if (r0 < 0) goto L_0x022c
            java.lang.Object r6 = r3.get(r0)
            com.android.server.wm.TaskRecord r6 = (com.android.server.wm.TaskRecord) r6
            int r7 = r6.mPrevAffiliateTaskId
            com.android.server.wm.TaskRecord r7 = r1.taskIdToTask(r7, r3)
            r6.setPrevAffiliate(r7)
            int r7 = r6.mNextAffiliateTaskId
            com.android.server.wm.TaskRecord r7 = r1.taskIdToTask(r7, r3)
            r6.setNextAffiliate(r7)
            int r0 = r0 + -1
            goto L_0x020f
        L_0x022c:
            com.android.server.wm.TaskPersister$1 r0 = new com.android.server.wm.TaskPersister$1
            r0.<init>()
            java.util.Collections.sort(r3, r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskPersister.restoreTasksForUserLocked(int, android.util.SparseBooleanArray):java.util.List");
    }

    public void onPreProcessItem(boolean queueEmpty) {
        if (queueEmpty) {
            this.mTmpTaskIds.clear();
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mRecentTasks.getPersistableTaskIds(this.mTmpTaskIds);
                    this.mService.mWindowManager.removeObsoleteTaskFiles(this.mTmpTaskIds, this.mRecentTasks.usersWithRecentsLoadedLocked());
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            removeObsoleteFiles(this.mTmpTaskIds);
        }
        writeTaskIdsFiles();
    }

    private static void removeObsoleteFiles(ArraySet<Integer> persistentTaskIds, File[] files) {
        if (files == null) {
            Slog.e(TAG, "File error accessing recents directory (directory doesn't exist?).");
            return;
        }
        for (File file : files) {
            String filename = file.getName();
            int taskIdEnd = filename.indexOf(95);
            if (taskIdEnd > 0) {
                try {
                    if (!persistentTaskIds.contains(Integer.valueOf(Integer.parseInt(filename.substring(0, taskIdEnd))))) {
                        file.delete();
                    }
                } catch (Exception e) {
                    Slog.wtf(TAG, "removeObsoleteFiles: Can't parse file=" + file.getName());
                    file.delete();
                }
            }
        }
    }

    private void writeTaskIdsFiles() {
        SparseArray<SparseBooleanArray> changedTaskIdsPerUser = new SparseArray<>();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int userId : this.mRecentTasks.usersWithRecentsLoadedLocked()) {
                    SparseBooleanArray taskIdsToSave = this.mRecentTasks.getTaskIdsForUser(userId);
                    SparseBooleanArray persistedIdsInFile = this.mTaskIdsInFile.get(userId);
                    if (persistedIdsInFile == null || !persistedIdsInFile.equals(taskIdsToSave)) {
                        SparseBooleanArray taskIdsToSaveCopy = taskIdsToSave.clone();
                        this.mTaskIdsInFile.put(userId, taskIdsToSaveCopy);
                        changedTaskIdsPerUser.put(userId, taskIdsToSaveCopy);
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        for (int i = 0; i < changedTaskIdsPerUser.size(); i++) {
            writePersistedTaskIdsForUser(changedTaskIdsPerUser.valueAt(i), changedTaskIdsPerUser.keyAt(i));
        }
    }

    private void removeObsoleteFiles(ArraySet<Integer> persistentTaskIds) {
        int[] candidateUserIds;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                candidateUserIds = this.mRecentTasks.usersWithRecentsLoadedLocked();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        for (int userId : candidateUserIds) {
            removeObsoleteFiles(persistentTaskIds, getUserImagesDir(userId).listFiles());
            removeObsoleteFiles(persistentTaskIds, getUserTasksDir(userId).listFiles());
        }
    }

    static Bitmap restoreImage(String filename) {
        return BitmapFactory.decodeFile(filename);
    }

    private File getUserPersistedTaskIdsFile(int userId) {
        File userTaskIdsDir = new File(this.mTaskIdsDir, String.valueOf(userId));
        if (!userTaskIdsDir.exists() && !userTaskIdsDir.mkdirs()) {
            Slog.e(TAG, "Error while creating user directory: " + userTaskIdsDir);
        }
        return new File(userTaskIdsDir, PERSISTED_TASK_IDS_FILENAME);
    }

    /* access modifiers changed from: private */
    public static File getUserTasksDir(int userId) {
        return new File(Environment.getDataSystemCeDirectory(userId), TASKS_DIRNAME);
    }

    static File getUserImagesDir(int userId) {
        return new File(Environment.getDataSystemCeDirectory(userId), IMAGES_DIRNAME);
    }

    /* access modifiers changed from: private */
    public static boolean createParentDirectory(String filePath) {
        File parentDir = new File(filePath).getParentFile();
        return parentDir.exists() || parentDir.mkdirs();
    }

    private static class TaskWriteQueueItem implements PersisterQueue.WriteQueueItem {
        private final ActivityTaskManagerService mService;
        /* access modifiers changed from: private */
        public final TaskRecord mTask;

        TaskWriteQueueItem(TaskRecord task, ActivityTaskManagerService service) {
            this.mTask = task;
            this.mService = service;
        }

        private StringWriter saveToXml(TaskRecord task) throws IOException, XmlPullParserException {
            XmlSerializer xmlSerializer = new FastXmlSerializer();
            StringWriter stringWriter = new StringWriter();
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startDocument((String) null, true);
            xmlSerializer.startTag((String) null, TaskPersister.TAG_TASK);
            task.saveToXml(xmlSerializer);
            xmlSerializer.endTag((String) null, TaskPersister.TAG_TASK);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            return stringWriter;
        }

        public void process() {
            StringWriter stringWriter = null;
            TaskRecord task = this.mTask;
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (task.inRecents) {
                        try {
                            stringWriter = saveToXml(task);
                        } catch (IOException | XmlPullParserException e) {
                        }
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (stringWriter != null) {
                AtomicFile atomicFile = null;
                try {
                    File userTasksDir = TaskPersister.getUserTasksDir(task.userId);
                    if (userTasksDir.isDirectory() || userTasksDir.mkdirs()) {
                        AtomicFile atomicFile2 = new AtomicFile(new File(userTasksDir, String.valueOf(task.taskId) + TaskPersister.TASK_FILENAME_SUFFIX));
                        FileOutputStream file = atomicFile2.startWrite();
                        file.write(stringWriter.toString().getBytes());
                        file.write(10);
                        atomicFile2.finishWrite(file);
                        return;
                    }
                    Slog.e(TaskPersister.TAG, "Failure creating tasks directory for user " + task.userId + ": " + userTasksDir + " Dropping persistence for task " + task);
                } catch (IOException e2) {
                    if (0 != 0) {
                        atomicFile.failWrite((FileOutputStream) null);
                    }
                    Slog.e(TaskPersister.TAG, "Unable to open " + atomicFile + " for persisting. " + e2);
                }
            }
        }

        public String toString() {
            return "TaskWriteQueueItem{task=" + this.mTask + "}";
        }
    }

    private static class ImageWriteQueueItem implements PersisterQueue.WriteQueueItem<ImageWriteQueueItem> {
        final String mFilePath;
        Bitmap mImage;

        ImageWriteQueueItem(String filePath, Bitmap image) {
            this.mFilePath = filePath;
            this.mImage = image;
        }

        public void process() {
            String filePath = this.mFilePath;
            if (!TaskPersister.createParentDirectory(filePath)) {
                Slog.e(TaskPersister.TAG, "Error while creating images directory for file: " + filePath);
                return;
            }
            Bitmap bitmap = this.mImage;
            FileOutputStream imageFile = null;
            try {
                imageFile = new FileOutputStream(new File(filePath));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageFile);
            } catch (Exception e) {
                Slog.e(TaskPersister.TAG, "saveImage: unable to save " + filePath, e);
            } catch (Throwable th) {
                IoUtils.closeQuietly(imageFile);
                throw th;
            }
            IoUtils.closeQuietly(imageFile);
        }

        public boolean matches(ImageWriteQueueItem item) {
            return this.mFilePath.equals(item.mFilePath);
        }

        public void updateFrom(ImageWriteQueueItem item) {
            this.mImage = item.mImage;
        }

        public String toString() {
            return "ImageWriteQueueItem{path=" + this.mFilePath + ", image=(" + this.mImage.getWidth() + "x" + this.mImage.getHeight() + ")}";
        }
    }
}
