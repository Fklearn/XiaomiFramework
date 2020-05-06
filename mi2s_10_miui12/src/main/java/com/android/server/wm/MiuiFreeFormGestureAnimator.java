package com.android.server.wm;

import android.animation.ValueAnimator;
import android.util.MiuiMultiWindowUtils;
import android.view.SurfaceControl;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import java.util.HashMap;
import java.util.Map;

public class MiuiFreeFormGestureAnimator {
    public static final String ALPHA_END_LISTENER = "ALPHA_END_LISTENER";
    public static final float DAMPINT = 0.99f;
    public static final String FLOAT_PROPERTY_ALPHA = "ALPHA";
    public static final String FLOAT_PROPERTY_SCALE = "SCALE";
    public static final String FLOAT_PROPERTY_SCALE_X = "SCALE_X";
    public static final String FLOAT_PROPERTY_SCALE_Y = "SCALE_Y";
    public static final String FLOAT_PROPERTY_TRANSLATE_X = "TRANSLATE_X";
    public static final String FLOAT_PROPERTY_TRANSLATE_Y = "TRANSLATE_Y";
    static final Interpolator QUINT_EASE_OUT_INTERPOLATOR = new QuintEaseOutInterpolator();
    public static final String SCALE_END_LISTENER = "SCALE_END_LISTENER";
    public static final String SCALE_X_END_LISTENER = "SCALE_X_END_LISTENER";
    public static final String SCALE_Y_END_LISTENER = "SCALE_Y_END_LISTENER";
    public static final float SMALL_WINDOW_DAMPINT = 0.7f;
    public static final float SMALL_WINDOW_MOVE_DAMPINT = 0.99f;
    public static final float SMALL_WINDOW_MOVE_STIFFNESS = 3947.8f;
    public static final float SMALL_WINDOW_STIFFNESS = 631.7f;
    public static final float SMALL_WINDOW_UP_DAMPINT = 0.75f;
    public static final float SMALL_WINDOW_UP_STIFFNESS = 130.5f;
    public static final int SPRINGANIMATION_ALPHA = 1;
    public static final int SPRINGANIMATION_SCALE = 4;
    public static final int SPRINGANIMATION_SCALE_X = 5;
    public static final int SPRINGANIMATION_SCALE_Y = 6;
    public static final int SPRINGANIMATION_TRANSLATE_X = 2;
    public static final int SPRINGANIMATION_TRANSLATE_Y = 3;
    public static final float STIFFNESS = 987.0f;
    public static final String TAG = "MiuiFreeFormGestureAnimator";
    public static final String TRANSLATE_X_END_LISTENER = "TRANSLATE_X_END_LISTENER";
    public static final String TRANSLATE_Y_END_LISTENER = "TRANSLATE_Y_END_LISTENER";
    private final SurfaceControl.Transaction mGestureTransaction = new SurfaceControl.Transaction();
    private final HashMap<AppWindowToken, SurfaceControl> mLeashMap = new HashMap<>();
    /* access modifiers changed from: private */
    public final MiuiFreeFormGesturePointerEventListener mListener;
    private final WindowManagerService mService;

    public static class TmpValues {
        final float[] floats = new float[9];
        final Transformation transformation = new Transformation();
    }

    MiuiFreeFormGestureAnimator(WindowManagerService service, MiuiFreeFormGesturePointerEventListener listener) {
        this.mService = service;
        this.mListener = listener;
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
    public void recreateLeashIfNeeded(AppWindowToken aToken) {
        synchronized (this) {
            if (aToken != null) {
                if (this.mLeashMap.containsKey(aToken)) {
                    return;
                }
            }
            createLeash(aToken);
        }
    }

    /* access modifiers changed from: package-private */
    public void setShadowRadiusParas(AppWindowToken aToken, float shadowRadius, float shadowPosYOffset, float shadowPosZ, float shadowLightRadius, float shadowSpotColorAlpha) {
        synchronized (this) {
            if (aToken != null) {
                if (!this.mLeashMap.containsKey(aToken)) {
                    return;
                }
            }
            SurfaceControl leash = this.mLeashMap.get(aToken);
            if (leash != null) {
                MiuiMultiWindowUtils.invoke(this.mGestureTransaction, "setShadowRadiusParas", new Object[]{leash, Float.valueOf(shadowRadius), Float.valueOf(shadowPosYOffset), Float.valueOf(shadowPosZ), Float.valueOf(shadowLightRadius), Float.valueOf(shadowSpotColorAlpha)});
            }
            applyTransaction();
        }
    }

    public void setShadowRadiusParas(WindowState window, float shadowRadius, float shadowPosYOffset, float shadowPosZ, float shadowLightRadius, float shadowSpotColorAlpha) {
        synchronized (this) {
            if (window != null) {
                WindowSurfaceController mWindowSurfaceControl = window.mWinAnimator.mSurfaceController;
                if (mWindowSurfaceControl != null) {
                    SurfaceControl surfaceControl = mWindowSurfaceControl.mSurfaceControl;
                    MiuiMultiWindowUtils.invoke(this.mGestureTransaction, "setShadowRadiusParas", new Object[]{surfaceControl, Float.valueOf(shadowRadius), Float.valueOf(shadowPosYOffset), Float.valueOf(shadowPosZ), Float.valueOf(shadowLightRadius), Float.valueOf(shadowSpotColorAlpha)});
                    applyTransaction();
                }
            }
        }
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setAlphaInTransaction(com.android.server.wm.AppWindowToken, float):void");
    }

    /* access modifiers changed from: package-private */
    public void setAlphaInTransaction(SurfaceControl Surface, float alpha) {
        synchronized (this) {
            if (Surface != null) {
                this.mGestureTransaction.setAlpha(Surface, alpha);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setDefaultRoundCorner(AppWindowToken aToken) {
        setRoundCorner(aToken, (float) AppTransitionInjector.DISPLAY_ROUND_CORNER_RADIUS);
    }

    /* access modifiers changed from: package-private */
    public void setRoundCorner(AppWindowToken aToken, float radius) {
        synchronized (this) {
            if (aToken != null) {
                if (!this.mLeashMap.containsKey(aToken)) {
                    return;
                }
            }
            SurfaceControl surfaceControl = this.mLeashMap.get(aToken);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideTaskDimmerLayer(AppWindowToken token) {
        Dimmer dimmer;
        if (token != null) {
            Task task = token.getTask();
            if (!(task == null || (dimmer = task.getDimmer()) == null || dimmer.mDimState == null || !dimmer.mDimState.isVisible || dimmer.mDimState.mDimLayer == null)) {
                this.mGestureTransaction.hide(dimmer.mDimState.mDimLayer);
            }
            applyTransaction();
        }
    }

    /* access modifiers changed from: package-private */
    public void hideAppWindowToken(AppWindowToken token) {
        SurfaceControl sc;
        if (token != null && (sc = token.mSurfaceControl) != null) {
            this.mGestureTransaction.hide(sc);
        }
    }

    /* access modifiers changed from: package-private */
    public void showAppWindowToken(AppWindowToken token) {
        SurfaceControl sc;
        if (token != null && (sc = token.mSurfaceControl) != null) {
            this.mGestureTransaction.show(sc);
        }
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setMatrixInTransaction(com.android.server.wm.AppWindowToken, float, float, float, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMatrixInTransaction(com.android.server.wm.AppWindowToken r3, android.graphics.Matrix r4, float[] r5) {
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
            r1.setMatrix(r0, r4, r5)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setMatrixInTransaction(com.android.server.wm.AppWindowToken, android.graphics.Matrix, float[]):void");
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setPositionInTransaction(com.android.server.wm.AppWindowToken, float, float):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setBlurInTransaction(com.android.server.wm.AppWindowToken r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L_0x000d
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r3.mLeashMap     // Catch:{ all -> 0x002c }
            boolean r0 = r0.containsKey(r4)     // Catch:{ all -> 0x002c }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r3)     // Catch:{ all -> 0x002c }
            return
        L_0x000d:
            java.util.HashMap<com.android.server.wm.AppWindowToken, android.view.SurfaceControl> r0 = r3.mLeashMap     // Catch:{ all -> 0x002c }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x002c }
            android.view.SurfaceControl r0 = (android.view.SurfaceControl) r0     // Catch:{ all -> 0x002c }
            if (r0 == 0) goto L_0x002a
            android.view.SurfaceControl$Transaction r1 = r3.mGestureTransaction     // Catch:{ all -> 0x002c }
            r2 = 1
            r1.setBlur(r0, r2)     // Catch:{ all -> 0x002c }
            android.view.SurfaceControl$Transaction r1 = r3.mGestureTransaction     // Catch:{ all -> 0x002c }
            r2 = 1065353216(0x3f800000, float:1.0)
            r1.setBlurRatio(r0, r2)     // Catch:{ all -> 0x002c }
            android.view.SurfaceControl$Transaction r1 = r3.mGestureTransaction     // Catch:{ all -> 0x002c }
            r2 = 0
            r1.setBlurMode(r0, r2)     // Catch:{ all -> 0x002c }
        L_0x002a:
            monitor-exit(r3)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x002c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setBlurInTransaction(com.android.server.wm.AppWindowToken):void");
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.setWindowCropInTransaction(com.android.server.wm.AppWindowToken, android.graphics.Rect):void");
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
            applyTransaction();
            this.mLeashMap.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public void reset(AppWindowToken aToken) {
        SurfaceControl leash;
        synchronized (this) {
            if (this.mLeashMap.containsKey(aToken) && (leash = this.mLeashMap.remove(aToken)) != null) {
                SurfaceControl surface = aToken.getSurfaceControl();
                SurfaceControl parent = aToken.getParentSurfaceControl();
                if (!(parent == null || surface == null)) {
                    this.mGestureTransaction.reparent(surface, parent);
                }
                this.mGestureTransaction.remove(leash);
                applyTransaction();
            }
        }
    }

    private SurfaceControl createLeashLocked(AppWindowToken aToken, SurfaceControl child) {
        if (aToken == null || child == null) {
            return null;
        }
        if (aToken.hasCommittedReparentToAnimationLeash()) {
            aToken.cancelAnimation();
        }
        if (aToken.isAppAnimating()) {
            aToken.cancelAnimation();
        }
        int width = aToken.getSurfaceWidth();
        int height = aToken.getSurfaceHeight();
        SurfaceControl.Builder parent = aToken.makeAnimationLeash().setParent(aToken.getAppAnimationLayer(1));
        SurfaceControl leash = parent.setName(child + "-miui-freeform-gesture-leash").setBufferSize(width, height).build();
        this.mGestureTransaction.show(leash);
        this.mGestureTransaction.reparent(child, leash);
        return leash;
    }

    public static class SfValueAnimator extends ValueAnimator {
        SfValueAnimator() {
            setFloatValues(new float[]{0.0f, 1.0f});
        }
    }

    private static class QuintEaseOutInterpolator implements Interpolator {
        private QuintEaseOutInterpolator() {
        }

        public float getInterpolation(float t) {
            float f = t - 1.0f;
            float t2 = f;
            return (f * t2 * t2 * t2 * t2) + 1.0f;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.miui.internal.dynamicanimation.animation.FloatPropertyCompat createWindowStateProperty(java.lang.String r14) {
        /*
            r13 = this;
            r0 = 0
            int r1 = r14.hashCode()
            java.lang.String r2 = "TRANSLATE_Y"
            java.lang.String r3 = "TRANSLATE_X"
            java.lang.String r4 = "SCALE"
            java.lang.String r5 = "ALPHA"
            java.lang.String r6 = "SCALE_Y"
            java.lang.String r7 = "SCALE_X"
            r8 = 5
            r9 = 4
            r10 = 3
            r11 = 2
            r12 = 1
            switch(r1) {
                case -1666090365: goto L_0x0042;
                case -1666090364: goto L_0x003a;
                case 62372158: goto L_0x0032;
                case 78713130: goto L_0x002a;
                case 312310151: goto L_0x0022;
                case 312310152: goto L_0x001a;
                default: goto L_0x0019;
            }
        L_0x0019:
            goto L_0x004a
        L_0x001a:
            boolean r1 = r14.equals(r2)
            if (r1 == 0) goto L_0x0019
            r1 = r8
            goto L_0x004b
        L_0x0022:
            boolean r1 = r14.equals(r3)
            if (r1 == 0) goto L_0x0019
            r1 = r9
            goto L_0x004b
        L_0x002a:
            boolean r1 = r14.equals(r4)
            if (r1 == 0) goto L_0x0019
            r1 = r12
            goto L_0x004b
        L_0x0032:
            boolean r1 = r14.equals(r5)
            if (r1 == 0) goto L_0x0019
            r1 = 0
            goto L_0x004b
        L_0x003a:
            boolean r1 = r14.equals(r6)
            if (r1 == 0) goto L_0x0019
            r1 = r10
            goto L_0x004b
        L_0x0042:
            boolean r1 = r14.equals(r7)
            if (r1 == 0) goto L_0x0019
            r1 = r11
            goto L_0x004b
        L_0x004a:
            r1 = -1
        L_0x004b:
            if (r1 == 0) goto L_0x007b
            if (r1 == r12) goto L_0x0074
            if (r1 == r11) goto L_0x006d
            if (r1 == r10) goto L_0x0066
            if (r1 == r9) goto L_0x005f
            if (r1 == r8) goto L_0x0058
            goto L_0x0082
        L_0x0058:
            com.android.server.wm.MiuiFreeFormGestureAnimator$6 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$6
            r1.<init>(r2)
            r0 = r1
            goto L_0x0082
        L_0x005f:
            com.android.server.wm.MiuiFreeFormGestureAnimator$5 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$5
            r1.<init>(r3)
            r0 = r1
            goto L_0x0082
        L_0x0066:
            com.android.server.wm.MiuiFreeFormGestureAnimator$4 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$4
            r1.<init>(r6)
            r0 = r1
            goto L_0x0082
        L_0x006d:
            com.android.server.wm.MiuiFreeFormGestureAnimator$3 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$3
            r1.<init>(r7)
            r0 = r1
            goto L_0x0082
        L_0x0074:
            com.android.server.wm.MiuiFreeFormGestureAnimator$2 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$2
            r1.<init>(r4)
            r0 = r1
            goto L_0x0082
        L_0x007b:
            com.android.server.wm.MiuiFreeFormGestureAnimator$1 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$1
            r1.<init>(r5)
            r0 = r1
        L_0x0082:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.createWindowStateProperty(java.lang.String):com.miui.internal.dynamicanimation.animation.FloatPropertyCompat");
    }

    public MiuiFreeFormSpringAnimation createSpringAnimation(WindowState w, float startValue, float finalPosition, float stiffness, float damping, float startVelocity, int animalType) {
        MiuiFreeFormSpringAnimation springAnimation = null;
        switch (animalType) {
            case 1:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_ALPHA), finalPosition);
                break;
            case 2:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_TRANSLATE_X), finalPosition);
                break;
            case 3:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_TRANSLATE_Y), finalPosition);
                break;
            case 4:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_SCALE), finalPosition);
                break;
            case 5:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_SCALE_X), finalPosition);
                break;
            case 6:
                springAnimation = new MiuiFreeFormSpringAnimation(w, createWindowStateProperty(FLOAT_PROPERTY_SCALE_Y), finalPosition);
                break;
        }
        springAnimation.getSpring().setStiffness(stiffness);
        springAnimation.getSpring().setDampingRatio(damping);
        springAnimation.setMinimumVisibleChange(0.002f);
        springAnimation.setStartValue(startValue);
        springAnimation.setStartVelocity(startVelocity);
        return springAnimation;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.wm.MiuiFreeFormDynamicAnimation.OnAnimationEndListener createAnimationEndListener(java.lang.String r8) {
        /*
            r7 = this;
            r0 = 0
            int r1 = r8.hashCode()
            r2 = 5
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r1) {
                case -832953965: goto L_0x0040;
                case -327395340: goto L_0x0036;
                case 26978575: goto L_0x002c;
                case 112134317: goto L_0x0022;
                case 532537200: goto L_0x0018;
                case 1393369369: goto L_0x000e;
                default: goto L_0x000d;
            }
        L_0x000d:
            goto L_0x004a
        L_0x000e:
            java.lang.String r1 = "ALPHA_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = 0
            goto L_0x004b
        L_0x0018:
            java.lang.String r1 = "TRANSLATE_X_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = r3
            goto L_0x004b
        L_0x0022:
            java.lang.String r1 = "SCALE_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = r6
            goto L_0x004b
        L_0x002c:
            java.lang.String r1 = "TRANSLATE_Y_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = r2
            goto L_0x004b
        L_0x0036:
            java.lang.String r1 = "SCALE_X_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = r5
            goto L_0x004b
        L_0x0040:
            java.lang.String r1 = "SCALE_Y_END_LISTENER"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x000d
            r1 = r4
            goto L_0x004b
        L_0x004a:
            r1 = -1
        L_0x004b:
            if (r1 == 0) goto L_0x007b
            if (r1 == r6) goto L_0x0074
            if (r1 == r5) goto L_0x006d
            if (r1 == r4) goto L_0x0066
            if (r1 == r3) goto L_0x005f
            if (r1 == r2) goto L_0x0058
            goto L_0x0082
        L_0x0058:
            com.android.server.wm.MiuiFreeFormGestureAnimator$12 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$12
            r1.<init>()
            r0 = r1
            goto L_0x0082
        L_0x005f:
            com.android.server.wm.MiuiFreeFormGestureAnimator$11 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$11
            r1.<init>()
            r0 = r1
            goto L_0x0082
        L_0x0066:
            com.android.server.wm.MiuiFreeFormGestureAnimator$10 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$10
            r1.<init>()
            r0 = r1
            goto L_0x0082
        L_0x006d:
            com.android.server.wm.MiuiFreeFormGestureAnimator$9 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$9
            r1.<init>()
            r0 = r1
            goto L_0x0082
        L_0x0074:
            com.android.server.wm.MiuiFreeFormGestureAnimator$8 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$8
            r1.<init>()
            r0 = r1
            goto L_0x0082
        L_0x007b:
            com.android.server.wm.MiuiFreeFormGestureAnimator$7 r1 = new com.android.server.wm.MiuiFreeFormGestureAnimator$7
            r1.<init>()
            r0 = r1
        L_0x0082:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormGestureAnimator.createAnimationEndListener(java.lang.String):com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener");
    }

    public static final class AnimalLock {
        MiuiFreeFormSpringAnimation mAlphaAnimation;
        boolean mAlphaEnd = true;
        int mCurrentAnimation = -1;
        MiuiFreeFormSpringAnimation mScaleAnimation;
        boolean mScaleEnd = true;
        MiuiFreeFormSpringAnimation mScaleXAnimation;
        boolean mScaleXEnd = true;
        MiuiFreeFormSpringAnimation mScaleYAnimation;
        boolean mScaleYEnd = true;
        MiuiFreeFormSpringAnimation mTranslateXAnimation;
        boolean mTranslateXEnd = true;
        MiuiFreeFormSpringAnimation mTranslateYAnimation;
        boolean mTranslateYEnd = true;
        WindowState mWindow;

        AnimalLock(WindowState w) {
            this.mWindow = w;
        }

        public void resetAnimalState() {
            this.mScaleEnd = true;
            this.mScaleXEnd = true;
            this.mScaleYEnd = true;
            this.mTranslateYEnd = true;
            this.mTranslateXEnd = true;
            this.mAlphaEnd = true;
            this.mScaleAnimation = null;
            this.mScaleXAnimation = null;
            this.mScaleYAnimation = null;
            this.mTranslateYAnimation = null;
            this.mTranslateXAnimation = null;
            this.mAlphaAnimation = null;
            this.mWindow = null;
        }

        public void skipToEnd() {
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation = this.mScaleAnimation;
            if (miuiFreeFormSpringAnimation != null) {
                miuiFreeFormSpringAnimation.skipToEnd();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation2 = this.mScaleXAnimation;
            if (miuiFreeFormSpringAnimation2 != null) {
                miuiFreeFormSpringAnimation2.skipToEnd();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation3 = this.mScaleYAnimation;
            if (miuiFreeFormSpringAnimation3 != null) {
                miuiFreeFormSpringAnimation3.skipToEnd();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation4 = this.mTranslateYAnimation;
            if (miuiFreeFormSpringAnimation4 != null) {
                miuiFreeFormSpringAnimation4.skipToEnd();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation5 = this.mTranslateXAnimation;
            if (miuiFreeFormSpringAnimation5 != null) {
                miuiFreeFormSpringAnimation5.skipToEnd();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation6 = this.mAlphaAnimation;
            if (miuiFreeFormSpringAnimation6 != null) {
                miuiFreeFormSpringAnimation6.skipToEnd();
            }
            resetAnimalState();
        }

        public void cancel() {
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation = this.mScaleAnimation;
            if (miuiFreeFormSpringAnimation != null) {
                miuiFreeFormSpringAnimation.cancel();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation2 = this.mScaleXAnimation;
            if (miuiFreeFormSpringAnimation2 != null) {
                miuiFreeFormSpringAnimation2.cancel();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation3 = this.mScaleYAnimation;
            if (miuiFreeFormSpringAnimation3 != null) {
                miuiFreeFormSpringAnimation3.cancel();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation4 = this.mTranslateYAnimation;
            if (miuiFreeFormSpringAnimation4 != null) {
                miuiFreeFormSpringAnimation4.cancel();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation5 = this.mTranslateXAnimation;
            if (miuiFreeFormSpringAnimation5 != null) {
                miuiFreeFormSpringAnimation5.cancel();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation6 = this.mAlphaAnimation;
            if (miuiFreeFormSpringAnimation6 != null) {
                miuiFreeFormSpringAnimation6.cancel();
            }
            resetAnimalState();
        }

        public void start(int animationType) {
            this.mCurrentAnimation = animationType;
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation = this.mScaleAnimation;
            if (miuiFreeFormSpringAnimation != null) {
                this.mScaleEnd = false;
                miuiFreeFormSpringAnimation.start();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation2 = this.mScaleXAnimation;
            if (miuiFreeFormSpringAnimation2 != null) {
                this.mScaleXEnd = false;
                miuiFreeFormSpringAnimation2.start();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation3 = this.mScaleYAnimation;
            if (miuiFreeFormSpringAnimation3 != null) {
                this.mScaleYEnd = false;
                miuiFreeFormSpringAnimation3.start();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation4 = this.mTranslateYAnimation;
            if (miuiFreeFormSpringAnimation4 != null) {
                this.mTranslateYEnd = false;
                miuiFreeFormSpringAnimation4.start();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation5 = this.mTranslateXAnimation;
            if (miuiFreeFormSpringAnimation5 != null) {
                this.mTranslateXEnd = false;
                miuiFreeFormSpringAnimation5.start();
            }
            MiuiFreeFormSpringAnimation miuiFreeFormSpringAnimation6 = this.mAlphaAnimation;
            if (miuiFreeFormSpringAnimation6 != null) {
                this.mAlphaEnd = false;
                miuiFreeFormSpringAnimation6.start();
            }
        }

        public boolean isAnimalFinished() {
            return this.mScaleEnd && this.mScaleXEnd && this.mScaleYEnd && this.mTranslateYEnd && this.mTranslateXEnd && this.mAlphaEnd;
        }
    }
}
