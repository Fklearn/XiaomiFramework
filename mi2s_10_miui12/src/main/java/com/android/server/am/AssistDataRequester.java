package com.android.server.am;

import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IActivityTaskManager;
import android.app.IAssistDataReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.IWindowManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AssistDataRequester extends IAssistDataReceiver.Stub {
    public static final String KEY_RECEIVER_EXTRA_COUNT = "count";
    public static final String KEY_RECEIVER_EXTRA_INDEX = "index";
    @VisibleForTesting
    public IActivityTaskManager mActivityTaskManager;
    private AppOpsManager mAppOpsManager;
    private final ArrayList<Bundle> mAssistData = new ArrayList<>();
    private final ArrayList<Bitmap> mAssistScreenshot = new ArrayList<>();
    private AssistDataRequesterCallbacks mCallbacks;
    private Object mCallbacksLock;
    private boolean mCanceled;
    private Context mContext;
    private int mPendingDataCount;
    private int mPendingScreenshotCount;
    private int mRequestScreenshotAppOps;
    private int mRequestStructureAppOps;
    private IWindowManager mWindowManager;

    public interface AssistDataRequesterCallbacks {
        @GuardedBy({"mCallbacksLock"})
        boolean canHandleReceivedAssistDataLocked();

        @GuardedBy({"mCallbacksLock"})
        void onAssistDataReceivedLocked(Bundle data, int activityIndex, int activityCount) {
        }

        @GuardedBy({"mCallbacksLock"})
        void onAssistScreenshotReceivedLocked(Bitmap screenshot) {
        }

        @GuardedBy({"mCallbacksLock"})
        void onAssistRequestCompleted() {
        }
    }

    public AssistDataRequester(Context context, IWindowManager windowManager, AppOpsManager appOpsManager, AssistDataRequesterCallbacks callbacks, Object callbacksLock, int requestStructureAppOps, int requestScreenshotAppOps) {
        this.mCallbacks = callbacks;
        this.mCallbacksLock = callbacksLock;
        this.mWindowManager = windowManager;
        this.mActivityTaskManager = ActivityTaskManager.getService();
        this.mContext = context;
        this.mAppOpsManager = appOpsManager;
        this.mRequestStructureAppOps = requestStructureAppOps;
        this.mRequestScreenshotAppOps = requestScreenshotAppOps;
    }

    public void requestAutofillData(List<IBinder> activityTokens, int callingUid, String callingPackage) {
        requestData(activityTokens, true, true, false, true, false, callingUid, callingPackage);
    }

    public void requestAssistData(List<IBinder> activityTokens, boolean fetchData, boolean fetchScreenshot, boolean allowFetchData, boolean allowFetchScreenshot, int callingUid, String callingPackage) {
        requestData(activityTokens, false, fetchData, fetchScreenshot, allowFetchData, allowFetchScreenshot, callingUid, callingPackage);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: android.os.Bundle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: android.os.Bundle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: android.os.Bundle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: android.os.Bundle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: android.os.Bundle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: android.os.Bundle} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void requestData(java.util.List<android.os.IBinder> r22, boolean r23, boolean r24, boolean r25, boolean r26, boolean r27, int r28, java.lang.String r29) {
        /*
            r21 = this;
            r8 = r21
            r9 = r28
            r10 = r29
            boolean r0 = r22.isEmpty()
            if (r0 == 0) goto L_0x0010
            r21.tryDispatchRequestComplete()
            return
        L_0x0010:
            r1 = 0
            android.app.IActivityTaskManager r0 = r8.mActivityTaskManager     // Catch:{ RemoteException -> 0x001a }
            boolean r0 = r0.isAssistDataAllowedOnCurrentActivity()     // Catch:{ RemoteException -> 0x001a }
            r1 = r0
            r11 = r1
            goto L_0x001c
        L_0x001a:
            r0 = move-exception
            r11 = r1
        L_0x001c:
            r12 = r26 & r11
            r13 = 0
            r14 = 1
            if (r24 == 0) goto L_0x002b
            if (r11 == 0) goto L_0x002b
            int r0 = r8.mRequestScreenshotAppOps
            r1 = -1
            if (r0 == r1) goto L_0x002b
            r0 = r14
            goto L_0x002c
        L_0x002b:
            r0 = r13
        L_0x002c:
            r15 = r27 & r0
            r8.mCanceled = r13
            r8.mPendingDataCount = r13
            r8.mPendingScreenshotCount = r13
            java.util.ArrayList<android.os.Bundle> r0 = r8.mAssistData
            r0.clear()
            java.util.ArrayList<android.graphics.Bitmap> r0 = r8.mAssistScreenshot
            r0.clear()
            r7 = 0
            if (r24 == 0) goto L_0x00ff
            android.app.AppOpsManager r0 = r8.mAppOpsManager
            int r1 = r8.mRequestStructureAppOps
            int r0 = r0.checkOpNoThrow(r1, r9, r10)
            if (r0 != 0) goto L_0x00eb
            if (r12 == 0) goto L_0x00eb
            int r6 = r22.size()
            r0 = 0
            r5 = r0
        L_0x0053:
            if (r5 >= r6) goto L_0x00e5
            r4 = r22
            java.lang.Object r0 = r4.get(r5)
            r3 = r0
            android.os.IBinder r3 = (android.os.IBinder) r3
            android.content.Context r0 = r8.mContext     // Catch:{ RemoteException -> 0x00d5 }
            java.lang.String r1 = "assist_with_context"
            com.android.internal.logging.MetricsLogger.count(r0, r1, r14)     // Catch:{ RemoteException -> 0x00d5 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ RemoteException -> 0x00d5 }
            r0.<init>()     // Catch:{ RemoteException -> 0x00d5 }
            java.lang.String r1 = "index"
            r0.putInt(r1, r5)     // Catch:{ RemoteException -> 0x00d5 }
            java.lang.String r1 = "count"
            r0.putInt(r1, r6)     // Catch:{ RemoteException -> 0x00d5 }
            if (r23 == 0) goto L_0x008e
            android.app.IActivityTaskManager r1 = r8.mActivityTaskManager     // Catch:{ RemoteException -> 0x0085 }
            boolean r1 = r1.requestAutofillData(r8, r0, r3, r13)     // Catch:{ RemoteException -> 0x0085 }
            r18 = r3
            r19 = r5
            r20 = r6
            r13 = r7
            goto L_0x00b3
        L_0x0085:
            r0 = move-exception
            r18 = r3
            r19 = r5
            r20 = r6
            r13 = r7
            goto L_0x00dd
        L_0x008e:
            android.app.IActivityTaskManager r1 = r8.mActivityTaskManager     // Catch:{ RemoteException -> 0x00d5 }
            r2 = 1
            if (r5 != 0) goto L_0x0096
            r16 = r14
            goto L_0x0098
        L_0x0096:
            r16 = r13
        L_0x0098:
            if (r5 != 0) goto L_0x009d
            r17 = r14
            goto L_0x009f
        L_0x009d:
            r17 = r13
        L_0x009f:
            r18 = r3
            r3 = r21
            r4 = r0
            r19 = r5
            r5 = r18
            r20 = r6
            r6 = r16
            r13 = r7
            r7 = r17
            boolean r1 = r1.requestAssistContextExtras(r2, r3, r4, r5, r6, r7)     // Catch:{ RemoteException -> 0x00bc }
        L_0x00b3:
            if (r1 == 0) goto L_0x00be
            int r2 = r8.mPendingDataCount     // Catch:{ RemoteException -> 0x00bc }
            int r2 = r2 + r14
            r8.mPendingDataCount = r2     // Catch:{ RemoteException -> 0x00bc }
            goto L_0x00d4
        L_0x00bc:
            r0 = move-exception
            goto L_0x00dd
        L_0x00be:
            if (r19 != 0) goto L_0x00d4
            com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks r2 = r8.mCallbacks     // Catch:{ RemoteException -> 0x00bc }
            boolean r2 = r2.canHandleReceivedAssistDataLocked()     // Catch:{ RemoteException -> 0x00bc }
            if (r2 == 0) goto L_0x00cc
            r8.dispatchAssistDataReceived(r13)     // Catch:{ RemoteException -> 0x00bc }
            goto L_0x00d1
        L_0x00cc:
            java.util.ArrayList<android.os.Bundle> r2 = r8.mAssistData     // Catch:{ RemoteException -> 0x00bc }
            r2.add(r13)     // Catch:{ RemoteException -> 0x00bc }
        L_0x00d1:
            r2 = 0
            r15 = r2
            goto L_0x00ea
        L_0x00d4:
            goto L_0x00dd
        L_0x00d5:
            r0 = move-exception
            r18 = r3
            r19 = r5
            r20 = r6
            r13 = r7
        L_0x00dd:
            int r5 = r19 + 1
            r7 = r13
            r6 = r20
            r13 = 0
            goto L_0x0053
        L_0x00e5:
            r19 = r5
            r20 = r6
            r13 = r7
        L_0x00ea:
            goto L_0x0100
        L_0x00eb:
            r13 = r7
            com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks r0 = r8.mCallbacks
            boolean r0 = r0.canHandleReceivedAssistDataLocked()
            if (r0 == 0) goto L_0x00f8
            r8.dispatchAssistDataReceived(r13)
            goto L_0x00fd
        L_0x00f8:
            java.util.ArrayList<android.os.Bundle> r0 = r8.mAssistData
            r0.add(r13)
        L_0x00fd:
            r15 = 0
            goto L_0x0100
        L_0x00ff:
            r13 = r7
        L_0x0100:
            if (r25 == 0) goto L_0x0133
            android.app.AppOpsManager r0 = r8.mAppOpsManager
            int r1 = r8.mRequestScreenshotAppOps
            int r0 = r0.checkOpNoThrow(r1, r9, r10)
            if (r0 != 0) goto L_0x0122
            if (r15 == 0) goto L_0x0122
            android.content.Context r0 = r8.mContext     // Catch:{ RemoteException -> 0x0120 }
            java.lang.String r1 = "assist_with_screen"
            com.android.internal.logging.MetricsLogger.count(r0, r1, r14)     // Catch:{ RemoteException -> 0x0120 }
            int r0 = r8.mPendingScreenshotCount     // Catch:{ RemoteException -> 0x0120 }
            int r0 = r0 + r14
            r8.mPendingScreenshotCount = r0     // Catch:{ RemoteException -> 0x0120 }
            android.view.IWindowManager r0 = r8.mWindowManager     // Catch:{ RemoteException -> 0x0120 }
            r0.requestAssistScreenshot(r8)     // Catch:{ RemoteException -> 0x0120 }
            goto L_0x0121
        L_0x0120:
            r0 = move-exception
        L_0x0121:
            goto L_0x0133
        L_0x0122:
            com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks r0 = r8.mCallbacks
            boolean r0 = r0.canHandleReceivedAssistDataLocked()
            if (r0 == 0) goto L_0x012e
            r8.dispatchAssistScreenshotReceived(r13)
            goto L_0x0133
        L_0x012e:
            java.util.ArrayList<android.graphics.Bitmap> r0 = r8.mAssistScreenshot
            r0.add(r13)
        L_0x0133:
            r21.tryDispatchRequestComplete()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AssistDataRequester.requestData(java.util.List, boolean, boolean, boolean, boolean, boolean, int, java.lang.String):void");
    }

    public void processPendingAssistData() {
        flushPendingAssistData();
        tryDispatchRequestComplete();
    }

    private void flushPendingAssistData() {
        int dataCount = this.mAssistData.size();
        for (int i = 0; i < dataCount; i++) {
            dispatchAssistDataReceived(this.mAssistData.get(i));
        }
        this.mAssistData.clear();
        int screenshotsCount = this.mAssistScreenshot.size();
        for (int i2 = 0; i2 < screenshotsCount; i2++) {
            dispatchAssistScreenshotReceived(this.mAssistScreenshot.get(i2));
        }
        this.mAssistScreenshot.clear();
    }

    public int getPendingDataCount() {
        return this.mPendingDataCount;
    }

    public int getPendingScreenshotCount() {
        return this.mPendingScreenshotCount;
    }

    public void cancel() {
        this.mCanceled = true;
        this.mPendingDataCount = 0;
        this.mPendingScreenshotCount = 0;
        this.mAssistData.clear();
        this.mAssistScreenshot.clear();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0027, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHandleAssistData(android.os.Bundle r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mCallbacksLock
            monitor-enter(r0)
            boolean r1 = r2.mCanceled     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0009:
            int r1 = r2.mPendingDataCount     // Catch:{ all -> 0x0028 }
            int r1 = r1 + -1
            r2.mPendingDataCount = r1     // Catch:{ all -> 0x0028 }
            com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks r1 = r2.mCallbacks     // Catch:{ all -> 0x0028 }
            boolean r1 = r1.canHandleReceivedAssistDataLocked()     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0021
            r2.flushPendingAssistData()     // Catch:{ all -> 0x0028 }
            r2.dispatchAssistDataReceived(r3)     // Catch:{ all -> 0x0028 }
            r2.tryDispatchRequestComplete()     // Catch:{ all -> 0x0028 }
            goto L_0x0026
        L_0x0021:
            java.util.ArrayList<android.os.Bundle> r1 = r2.mAssistData     // Catch:{ all -> 0x0028 }
            r1.add(r3)     // Catch:{ all -> 0x0028 }
        L_0x0026:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AssistDataRequester.onHandleAssistData(android.os.Bundle):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0027, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHandleAssistScreenshot(android.graphics.Bitmap r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mCallbacksLock
            monitor-enter(r0)
            boolean r1 = r2.mCanceled     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0009:
            int r1 = r2.mPendingScreenshotCount     // Catch:{ all -> 0x0028 }
            int r1 = r1 + -1
            r2.mPendingScreenshotCount = r1     // Catch:{ all -> 0x0028 }
            com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks r1 = r2.mCallbacks     // Catch:{ all -> 0x0028 }
            boolean r1 = r1.canHandleReceivedAssistDataLocked()     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0021
            r2.flushPendingAssistData()     // Catch:{ all -> 0x0028 }
            r2.dispatchAssistScreenshotReceived(r3)     // Catch:{ all -> 0x0028 }
            r2.tryDispatchRequestComplete()     // Catch:{ all -> 0x0028 }
            goto L_0x0026
        L_0x0021:
            java.util.ArrayList<android.graphics.Bitmap> r1 = r2.mAssistScreenshot     // Catch:{ all -> 0x0028 }
            r1.add(r3)     // Catch:{ all -> 0x0028 }
        L_0x0026:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AssistDataRequester.onHandleAssistScreenshot(android.graphics.Bitmap):void");
    }

    private void dispatchAssistDataReceived(Bundle data) {
        int activityIndex = 0;
        int activityCount = 0;
        Bundle receiverExtras = data != null ? data.getBundle(ActivityTaskManagerInternal.ASSIST_KEY_RECEIVER_EXTRAS) : null;
        if (receiverExtras != null) {
            activityIndex = receiverExtras.getInt(KEY_RECEIVER_EXTRA_INDEX);
            activityCount = receiverExtras.getInt(KEY_RECEIVER_EXTRA_COUNT);
        }
        this.mCallbacks.onAssistDataReceivedLocked(data, activityIndex, activityCount);
    }

    private void dispatchAssistScreenshotReceived(Bitmap screenshot) {
        this.mCallbacks.onAssistScreenshotReceivedLocked(screenshot);
    }

    private void tryDispatchRequestComplete() {
        if (this.mPendingDataCount == 0 && this.mPendingScreenshotCount == 0 && this.mAssistData.isEmpty() && this.mAssistScreenshot.isEmpty()) {
            this.mCallbacks.onAssistRequestCompleted();
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mPendingDataCount=");
        pw.println(this.mPendingDataCount);
        pw.print(prefix);
        pw.print("mAssistData=");
        pw.println(this.mAssistData);
        pw.print(prefix);
        pw.print("mPendingScreenshotCount=");
        pw.println(this.mPendingScreenshotCount);
        pw.print(prefix);
        pw.print("mAssistScreenshot=");
        pw.println(this.mAssistScreenshot);
    }
}
