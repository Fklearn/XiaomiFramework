package com.android.server.accessibility;

import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.MagnificationSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.List;

public class ActionReplacingCallback extends IAccessibilityInteractionConnectionCallback.Stub {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "ActionReplacingCallback";
    private final IAccessibilityInteractionConnection mConnectionWithReplacementActions;
    @GuardedBy({"mLock"})
    boolean mDone;
    private final int mInteractionId;
    private final Object mLock = new Object();
    @GuardedBy({"mLock"})
    boolean mMultiNodeCallbackHappened;
    @GuardedBy({"mLock"})
    AccessibilityNodeInfo mNodeFromOriginalWindow;
    @GuardedBy({"mLock"})
    List<AccessibilityNodeInfo> mNodesFromOriginalWindow;
    @GuardedBy({"mLock"})
    List<AccessibilityNodeInfo> mNodesWithReplacementActions;
    private final IAccessibilityInteractionConnectionCallback mServiceCallback;
    @GuardedBy({"mLock"})
    boolean mSingleNodeCallbackHappened;

    public ActionReplacingCallback(IAccessibilityInteractionConnectionCallback serviceCallback, IAccessibilityInteractionConnection connectionWithReplacementActions, int interactionId, int interrogatingPid, long interrogatingTid) {
        int i = interactionId;
        this.mServiceCallback = serviceCallback;
        this.mConnectionWithReplacementActions = connectionWithReplacementActions;
        this.mInteractionId = i;
        long identityToken = Binder.clearCallingIdentity();
        try {
            this.mConnectionWithReplacementActions.findAccessibilityNodeInfoByAccessibilityId(AccessibilityNodeInfo.ROOT_NODE_ID, (Region) null, i + 1, this, 0, interrogatingPid, interrogatingTid, (MagnificationSpec) null, (Bundle) null);
        } catch (RemoteException e) {
            this.mMultiNodeCallbackHappened = true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identityToken);
            throw th;
        }
        Binder.restoreCallingIdentity(identityToken);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000f, code lost:
        if (r1 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
        replaceInfoActionsAndCallService();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFindAccessibilityNodeInfoResult(android.view.accessibility.AccessibilityNodeInfo r4, int r5) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            int r1 = r3.mInteractionId     // Catch:{ all -> 0x001e }
            if (r5 != r1) goto L_0x0015
            r3.mNodeFromOriginalWindow = r4     // Catch:{ all -> 0x001e }
            r1 = 1
            r3.mSingleNodeCallbackHappened = r1     // Catch:{ all -> 0x001e }
            boolean r1 = r3.mMultiNodeCallbackHappened     // Catch:{ all -> 0x001e }
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            if (r1 == 0) goto L_0x0014
            r3.replaceInfoActionsAndCallService()
        L_0x0014:
            return
        L_0x0015:
            java.lang.String r1 = "ActionReplacingCallback"
            java.lang.String r2 = "Callback with unexpected interactionId"
            android.util.Slog.e(r1, r2)     // Catch:{ all -> 0x001e }
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.ActionReplacingCallback.setFindAccessibilityNodeInfoResult(android.view.accessibility.AccessibilityNodeInfo, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
        if (r1 == false) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        replaceInfoActionsAndCallService();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        if (r3 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
        replaceInfosActionsAndCallService();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFindAccessibilityNodeInfosResult(java.util.List<android.view.accessibility.AccessibilityNodeInfo> r5, int r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            int r1 = r4.mInteractionId     // Catch:{ all -> 0x002d }
            r2 = 1
            if (r6 != r1) goto L_0x000b
            r4.mNodesFromOriginalWindow = r5     // Catch:{ all -> 0x002d }
            goto L_0x0012
        L_0x000b:
            int r1 = r4.mInteractionId     // Catch:{ all -> 0x002d }
            int r1 = r1 + r2
            if (r6 != r1) goto L_0x0024
            r4.mNodesWithReplacementActions = r5     // Catch:{ all -> 0x002d }
        L_0x0012:
            boolean r1 = r4.mSingleNodeCallbackHappened     // Catch:{ all -> 0x002d }
            boolean r3 = r4.mMultiNodeCallbackHappened     // Catch:{ all -> 0x002d }
            r4.mMultiNodeCallbackHappened = r2     // Catch:{ all -> 0x002d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x001e
            r4.replaceInfoActionsAndCallService()
        L_0x001e:
            if (r3 == 0) goto L_0x0023
            r4.replaceInfosActionsAndCallService()
        L_0x0023:
            return
        L_0x0024:
            java.lang.String r1 = "ActionReplacingCallback"
            java.lang.String r2 = "Callback with unexpected interactionId"
            android.util.Slog.e(r1, r2)     // Catch:{ all -> 0x002d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.ActionReplacingCallback.setFindAccessibilityNodeInfosResult(java.util.List, int):void");
    }

    public void setPerformAccessibilityActionResult(boolean succeeded, int interactionId) throws RemoteException {
        this.mServiceCallback.setPerformAccessibilityActionResult(succeeded, interactionId);
    }

    private void replaceInfoActionsAndCallService() {
        synchronized (this.mLock) {
            if (!this.mDone) {
                if (this.mNodeFromOriginalWindow != null) {
                    replaceActionsOnInfoLocked(this.mNodeFromOriginalWindow);
                }
                recycleReplaceActionNodesLocked();
                AccessibilityNodeInfo nodeToReturn = this.mNodeFromOriginalWindow;
                this.mDone = true;
                try {
                    this.mServiceCallback.setFindAccessibilityNodeInfoResult(nodeToReturn, this.mInteractionId);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void replaceInfosActionsAndCallService() {
        synchronized (this.mLock) {
            if (!this.mDone) {
                if (this.mNodesFromOriginalWindow != null) {
                    for (int i = 0; i < this.mNodesFromOriginalWindow.size(); i++) {
                        replaceActionsOnInfoLocked(this.mNodesFromOriginalWindow.get(i));
                    }
                }
                recycleReplaceActionNodesLocked();
                List<AccessibilityNodeInfo> nodesToReturn = this.mNodesFromOriginalWindow == null ? null : new ArrayList<>(this.mNodesFromOriginalWindow);
                this.mDone = true;
                try {
                    this.mServiceCallback.setFindAccessibilityNodeInfosResult(nodesToReturn, this.mInteractionId);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private void replaceActionsOnInfoLocked(AccessibilityNodeInfo info) {
        info.removeAllActions();
        info.setClickable(false);
        info.setFocusable(false);
        info.setContextClickable(false);
        info.setScrollable(false);
        info.setLongClickable(false);
        info.setDismissable(false);
        if (info.getSourceNodeId() == AccessibilityNodeInfo.ROOT_NODE_ID && this.mNodesWithReplacementActions != null) {
            for (int i = 0; i < this.mNodesWithReplacementActions.size(); i++) {
                AccessibilityNodeInfo nodeWithReplacementActions = this.mNodesWithReplacementActions.get(i);
                if (nodeWithReplacementActions.getSourceNodeId() == AccessibilityNodeInfo.ROOT_NODE_ID) {
                    List<AccessibilityNodeInfo.AccessibilityAction> actions = nodeWithReplacementActions.getActionList();
                    if (actions != null) {
                        for (int j = 0; j < actions.size(); j++) {
                            info.addAction(actions.get(j));
                        }
                        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS);
                        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
                    }
                    info.setClickable(nodeWithReplacementActions.isClickable());
                    info.setFocusable(nodeWithReplacementActions.isFocusable());
                    info.setContextClickable(nodeWithReplacementActions.isContextClickable());
                    info.setScrollable(nodeWithReplacementActions.isScrollable());
                    info.setLongClickable(nodeWithReplacementActions.isLongClickable());
                    info.setDismissable(nodeWithReplacementActions.isDismissable());
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private void recycleReplaceActionNodesLocked() {
        List<AccessibilityNodeInfo> list = this.mNodesWithReplacementActions;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mNodesWithReplacementActions.get(i).recycle();
            }
            this.mNodesWithReplacementActions = null;
        }
    }
}
