package com.android.server.wm;

import android.view.SurfaceControl;
import android.view.animation.Transformation;
import java.util.HashMap;
import java.util.Map;

public class MiuiGestureAnimator {
    private AppWindowAnimatorHelper mAppWindowAnimatorHelper;
    private final SurfaceControl.Transaction mGestureTransaction = new SurfaceControl.Transaction();
    private boolean mIsThumbnailShown;
    private final HashMap<AppWindowToken, SurfaceControl> mLeashMap = new HashMap<>();
    private final WindowManagerService mService;
    private SurfaceControl mThumbnail;
    private SurfaceControl mThumbnailLeash;

    MiuiGestureAnimator(WindowManagerService service) {
        this.mService = service;
        AppTransitionInjector.initDisplayRoundCorner(service.mContext);
    }

    /* access modifiers changed from: package-private */
    public void createLeash(AppWindowToken aToken) {
        if (aToken != null) {
            synchronized (this) {
                SurfaceControl leash = createLeashLocked(aToken, aToken.getSurfaceControl());
                if (leash != null) {
                    leash.setLayer(aToken.getPrefixOrderIndex());
                    this.mLeashMap.put(aToken, leash);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void recreateLeashIfNeeded(AppWindowToken task) {
        synchronized (this) {
            if (task != null) {
                if (this.mLeashMap.containsKey(task)) {
                    return;
                }
            }
            createLeash(task);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00d4, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createThumbnail(com.android.server.wm.AppWindowToken r12, com.android.server.wm.WindowState r13, android.graphics.Bitmap r14) {
        /*
            r11 = this;
            monitor-enter(r11)
            r0 = 0
            if (r12 == 0) goto L_0x00d3
            if (r14 == 0) goto L_0x00d3
            boolean r1 = r14.isRecycled()     // Catch:{ all -> 0x00d5 }
            if (r1 == 0) goto L_0x000e
            goto L_0x00d3
        L_0x000e:
            android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.HARDWARE     // Catch:{ all -> 0x00d5 }
            android.graphics.Bitmap r1 = r14.copy(r1, r0)     // Catch:{ all -> 0x00d5 }
            if (r1 == 0) goto L_0x001b
            android.graphics.GraphicBuffer r2 = r1.createGraphicBufferHandle()     // Catch:{ all -> 0x00d5 }
            goto L_0x001c
        L_0x001b:
            r2 = 0
        L_0x001c:
            r14.recycle()     // Catch:{ all -> 0x00d5 }
            if (r2 != 0) goto L_0x0023
            monitor-exit(r11)     // Catch:{ all -> 0x00d5 }
            return r0
        L_0x0023:
            com.android.server.wm.WindowManagerService r3 = r11.mService     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.DisplayContent r3 = r3.getDefaultDisplayContentLocked()     // Catch:{ all -> 0x00d5 }
            android.view.DisplayInfo r3 = r3.getDisplayInfo()     // Catch:{ all -> 0x00d5 }
            int r4 = r2.getWidth()     // Catch:{ all -> 0x00d5 }
            int r5 = r2.getHeight()     // Catch:{ all -> 0x00d5 }
            if (r13 == 0) goto L_0x003c
            android.graphics.Rect r6 = r13.getContainingFrame()     // Catch:{ all -> 0x00d5 }
            goto L_0x0045
        L_0x003c:
            android.graphics.Rect r6 = new android.graphics.Rect     // Catch:{ all -> 0x00d5 }
            int r7 = r3.appWidth     // Catch:{ all -> 0x00d5 }
            int r8 = r3.appHeight     // Catch:{ all -> 0x00d5 }
            r6.<init>(r0, r0, r7, r8)     // Catch:{ all -> 0x00d5 }
        L_0x0045:
            com.android.server.wm.AppWindowAnimatorHelper r7 = new com.android.server.wm.AppWindowAnimatorHelper     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.WindowManagerService r8 = r11.mService     // Catch:{ all -> 0x00d5 }
            com.miui.internal.transition.IMiuiAppTransitionAnimationHelper r8 = r8.mMiuiAppTransitionAnimationHelper     // Catch:{ all -> 0x00d5 }
            r7.<init>(r6, r8)     // Catch:{ all -> 0x00d5 }
            r11.mAppWindowAnimatorHelper = r7     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.AppWindowAnimatorHelper r7 = r11.mAppWindowAnimatorHelper     // Catch:{ all -> 0x00d5 }
            android.graphics.Rect r8 = new android.graphics.Rect     // Catch:{ all -> 0x00d5 }
            r8.<init>(r0, r0, r4, r5)     // Catch:{ all -> 0x00d5 }
            r7.setMiuiThumbnailRect(r8)     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl$Builder r7 = r12.makeSurface()     // Catch:{ all -> 0x00d5 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d5 }
            r8.<init>()     // Catch:{ all -> 0x00d5 }
            java.lang.String r9 = "gesture thumbnail anim: "
            r8.append(r9)     // Catch:{ all -> 0x00d5 }
            java.lang.String r9 = r12.toString()     // Catch:{ all -> 0x00d5 }
            r8.append(r9)     // Catch:{ all -> 0x00d5 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl$Builder r7 = r7.setName(r8)     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl$Builder r7 = r7.setBufferSize(r4, r5)     // Catch:{ all -> 0x00d5 }
            r8 = -3
            android.view.SurfaceControl$Builder r7 = r7.setFormat(r8)     // Catch:{ all -> 0x00d5 }
            int r8 = r12.windowType     // Catch:{ all -> 0x00d5 }
            if (r13 == 0) goto L_0x0088
            int r9 = r13.mOwnerUid     // Catch:{ all -> 0x00d5 }
            goto L_0x008c
        L_0x0088:
            int r9 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00d5 }
        L_0x008c:
            android.view.SurfaceControl$Builder r7 = r7.setMetadata(r8, r9)     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r7 = r7.build()     // Catch:{ all -> 0x00d5 }
            r11.mThumbnail = r7     // Catch:{ all -> 0x00d5 }
            android.view.Surface r7 = new android.view.Surface     // Catch:{ all -> 0x00d5 }
            r7.<init>()     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r8 = r11.mThumbnail     // Catch:{ all -> 0x00d5 }
            r7.copyFrom(r8)     // Catch:{ all -> 0x00d5 }
            r7.attachAndQueueBuffer(r2)     // Catch:{ all -> 0x00d5 }
            r7.release()     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl$Transaction r8 = r11.mGestureTransaction     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r9 = r11.mThumbnail     // Catch:{ all -> 0x00d5 }
            r10 = -2147483648(0xffffffff80000000, float:-0.0)
            r8.setLayer(r9, r10)     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r8 = r11.mThumbnail     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r8 = r11.createLeashLocked(r12, r8)     // Catch:{ all -> 0x00d5 }
            r11.mThumbnailLeash = r8     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r8 = r11.mThumbnailLeash     // Catch:{ all -> 0x00d5 }
            if (r8 == 0) goto L_0x00c9
            android.view.SurfaceControl$Transaction r8 = r11.mGestureTransaction     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r9 = r11.mThumbnailLeash     // Catch:{ all -> 0x00d5 }
            r8.setLayer(r9, r10)     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.AppWindowAnimatorHelper r8 = r11.mAppWindowAnimatorHelper     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl r9 = r11.mThumbnailLeash     // Catch:{ all -> 0x00d5 }
            r8.setLeash(r9)     // Catch:{ all -> 0x00d5 }
        L_0x00c9:
            r11.mIsThumbnailShown = r0     // Catch:{ all -> 0x00d5 }
            android.view.SurfaceControl$Transaction r0 = r11.mGestureTransaction     // Catch:{ all -> 0x00d5 }
            r0.apply()     // Catch:{ all -> 0x00d5 }
            r0 = 1
            monitor-exit(r11)     // Catch:{ all -> 0x00d5 }
            return r0
        L_0x00d3:
            monitor-exit(r11)     // Catch:{ all -> 0x00d5 }
            return r0
        L_0x00d5:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00d5 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.createThumbnail(com.android.server.wm.AppWindowToken, com.android.server.wm.WindowState, android.graphics.Bitmap):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAlphaInTransaction(com.android.server.wm.AppWindowToken r3, float r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x001e }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x001e }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x001e }
            if (r0 == 0) goto L_0x001c
            android.view.SurfaceControl$Transaction r1 = r2.mGestureTransaction     // Catch:{ all -> 0x001e }
            r1.setAlpha(r0, r4)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.setAlphaInTransaction(com.android.server.wm.AppWindowToken, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0022, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMatrixInTransaction(com.android.server.wm.AppWindowToken r8, float r9, float r10, float r11, float r12) {
        /*
            r7 = this;
            monitor-enter(r7)
            if (r8 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r7.mLeashMap     // Catch:{ all -> 0x0023 }
            boolean r0 = r0.containsKey(r8)     // Catch:{ all -> 0x0023 }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r7)     // Catch:{ all -> 0x0023 }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r7.mLeashMap     // Catch:{ all -> 0x0023 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x0023 }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x0023 }
            if (r0 == 0) goto L_0x0021
            android.view.SurfaceControl$Transaction r1 = r7.mGestureTransaction     // Catch:{ all -> 0x0023 }
            r2 = r0
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r1.setMatrix(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0023 }
        L_0x0021:
            monitor-exit(r7)     // Catch:{ all -> 0x0023 }
            return
        L_0x0023:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0023 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.setMatrixInTransaction(com.android.server.wm.AppWindowToken, float, float, float, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setPositionInTransaction(com.android.server.wm.AppWindowToken r3, float r4, float r5) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x001e }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x001e }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x001e }
            if (r0 == 0) goto L_0x001c
            android.view.SurfaceControl$Transaction r1 = r2.mGestureTransaction     // Catch:{ all -> 0x001e }
            r1.setPosition(r0, r4, r5)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.setPositionInTransaction(com.android.server.wm.AppWindowToken, float, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setWindowCropInTransaction(com.android.server.wm.AppWindowToken r3, android.graphics.Rect r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x001e }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x001e }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x001e }
            if (r0 == 0) goto L_0x001c
            android.view.SurfaceControl$Transaction r1 = r2.mGestureTransaction     // Catch:{ all -> 0x001e }
            r1.setWindowCrop(r0, r4)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.setWindowCropInTransaction(com.android.server.wm.AppWindowToken, android.graphics.Rect):void");
    }

    /* access modifiers changed from: package-private */
    public void setDefaultRoundCorner(AppWindowToken aToken) {
        setRoundCorner(aToken, (float) AppTransitionInjector.DISPLAY_ROUND_CORNER_RADIUS);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRoundCorner(com.android.server.wm.AppWindowToken r3, float r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x001e }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r2.mLeashMap     // Catch:{ all -> 0x001e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x001e }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x001e }
            if (r0 == 0) goto L_0x001c
            android.view.SurfaceControl$Transaction r1 = r2.mGestureTransaction     // Catch:{ all -> 0x001e }
            r1.setCornerRadius(r0, r4)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.setRoundCorner(com.android.server.wm.AppWindowToken, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void resetRoundCorner(com.android.server.wm.AppWindowToken r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r3.mLeashMap     // Catch:{ all -> 0x001f }
            boolean r0 = r0.containsKey(r4)     // Catch:{ all -> 0x001f }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r3.mLeashMap     // Catch:{ all -> 0x001f }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x001f }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x001f }
            if (r0 == 0) goto L_0x001d
            android.view.SurfaceControl$Transaction r1 = r3.mGestureTransaction     // Catch:{ all -> 0x001f }
            r2 = 0
            r1.setCornerRadius(r0, r2)     // Catch:{ all -> 0x001f }
        L_0x001d:
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            return
        L_0x001f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureAnimator.resetRoundCorner(com.android.server.wm.AppWindowToken):void");
    }

    /* access modifiers changed from: package-private */
    public void stepThumbnailAnimationIfNeeded(Transformation transformation) {
        synchronized (this) {
            if (this.mAppWindowAnimatorHelper != null) {
                this.mAppWindowAnimatorHelper.stepGestureThumbnailAnimation(this.mGestureTransaction, transformation);
                if (!this.mIsThumbnailShown && this.mThumbnail != null) {
                    this.mGestureTransaction.show(this.mThumbnail);
                    this.mIsThumbnailShown = true;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl.Transaction getGestureTransaction() {
        return this.mGestureTransaction;
    }

    /* access modifiers changed from: package-private */
    public void hideWindow(WindowContainer w) {
        SurfaceControl sc;
        if (w != null && (sc = w.mSurfaceControl) != null) {
            this.mGestureTransaction.hide(sc);
        }
    }

    /* access modifiers changed from: package-private */
    public void showWindow(WindowContainer w) {
        SurfaceControl sc;
        if (w != null && (sc = w.mSurfaceControl) != null) {
            this.mGestureTransaction.show(sc);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideTaskDimmerLayer(AppWindowToken token) {
        Task task;
        Dimmer dimmer;
        if (token != null && (task = token.getTask()) != null && (dimmer = task.getDimmer()) != null && dimmer.mDimState != null && dimmer.mDimState.isVisible && dimmer.mDimState.mDimLayer != null) {
            this.mGestureTransaction.hide(dimmer.mDimState.mDimLayer);
        }
    }

    /* access modifiers changed from: package-private */
    public void applyTransaction() {
        synchronized (this) {
            this.mGestureTransaction.apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        synchronized (this) {
            for (Map.Entry<AppWindowToken, SurfaceControl> entry : this.mLeashMap.entrySet()) {
                AppWindowToken aToken = entry.getKey();
                SurfaceControl leash = entry.getValue();
                if (aToken != null) {
                    if (leash != null) {
                        SurfaceControl surface = aToken.getSurfaceControl();
                        SurfaceControl parent = aToken.getParentSurfaceControl();
                        if (!(parent == null || surface == null)) {
                            this.mGestureTransaction.reparent(surface, parent);
                        }
                        this.mGestureTransaction.remove(leash);
                    }
                }
            }
            if (this.mThumbnail != null) {
                this.mThumbnail.remove();
                this.mThumbnail = null;
            }
            if (this.mThumbnailLeash != null) {
                this.mGestureTransaction.remove(this.mThumbnailLeash);
                this.mThumbnailLeash = null;
            }
            applyTransaction();
            this.mLeashMap.clear();
            this.mAppWindowAnimatorHelper = null;
        }
    }

    private SurfaceControl createLeashLocked(AppWindowToken aToken, SurfaceControl child) {
        int animationLayer;
        if (aToken == null || child == null) {
            return null;
        }
        if (aToken.hasCommittedReparentToAnimationLeash()) {
            aToken.cancelAnimation();
        }
        int width = aToken.getSurfaceWidth();
        int height = aToken.getSurfaceHeight();
        if (aToken.isActivityTypeHome()) {
            animationLayer = 2;
        } else {
            animationLayer = 1;
        }
        SurfaceControl.Builder parent = aToken.makeAnimationLeash().setParent(aToken.getAppAnimationLayer(animationLayer));
        SurfaceControl leash = parent.setName(child + "-miui-gesture-leash").setBufferSize(width, height).build();
        this.mGestureTransaction.show(leash);
        this.mGestureTransaction.reparent(child, leash);
        return leash;
    }
}
