package com.android.server.wm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ResourceId;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.GraphicBuffer;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.RemoteAnimationAdapter;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.AttributeCache;
import com.android.server.pm.PackageManagerService;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.wm.WindowManagerInternal;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.animation.CurvedTranslateAnimation;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppTransition implements DumpUtils.Dump {
    private static final int APP_STATE_IDLE = 0;
    private static final int APP_STATE_READY = 1;
    private static final int APP_STATE_RUNNING = 2;
    private static final int APP_STATE_TIMEOUT = 3;
    private static final long APP_TRANSITION_TIMEOUT_MS = 5000;
    private static final int CLIP_REVEAL_TRANSLATION_Y_DP = 8;
    static final int DEFAULT_APP_TRANSITION_DURATION = 336;
    private static final int MAX_CLIP_REVEAL_TRANSITION_DURATION = 420;
    private static final int NEXT_TRANSIT_TYPE_CLIP_REVEAL = 8;
    private static final int NEXT_TRANSIT_TYPE_CUSTOM = 1;
    private static final int NEXT_TRANSIT_TYPE_CUSTOM_IN_PLACE = 7;
    private static final int NEXT_TRANSIT_TYPE_NONE = 0;
    private static final int NEXT_TRANSIT_TYPE_OPEN_CROSS_PROFILE_APPS = 9;
    private static final int NEXT_TRANSIT_TYPE_REMOTE = 10;
    private static final int NEXT_TRANSIT_TYPE_SCALE_UP = 2;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_DOWN = 6;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_UP = 5;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN = 4;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP = 3;
    private static final float RECENTS_THUMBNAIL_FADEIN_FRACTION = 0.5f;
    private static final float RECENTS_THUMBNAIL_FADEOUT_FRACTION = 0.5f;
    private static final String TAG = "WindowManager";
    private static final int THUMBNAIL_APP_TRANSITION_DURATION = 336;
    private static final Interpolator THUMBNAIL_DOCK_INTERPOLATOR = new PathInterpolator(0.85f, 0.0f, 1.0f, 1.0f);
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_DOWN = 2;
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_UP = 0;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_DOWN = 3;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_UP = 1;
    static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    private boolean mAllowCustomAnimation;
    private IRemoteCallback mAnimationExitFinishCallback;
    private IRemoteCallback mAnimationExitStartCallback;
    private IRemoteCallback mAnimationFinishedCallback;
    private IRemoteCallback mAnimationReenterFinishedCallback;
    private IRemoteCallback mAnimationReenterStartedCallback;
    private int mAppTransitionState = 0;
    private final Interpolator mClipHorizontalInterpolator = new PathInterpolator(0.0f, 0.0f, 0.4f, 1.0f);
    private final int mClipRevealTranslationY;
    private final int mConfigShortAnimTime;
    private final Context mContext;
    private int mCurrentUserId = 0;
    private final Interpolator mDecelerateInterpolator;
    private boolean mDefaultActivityAnimation;
    private final ExecutorService mDefaultExecutor = Executors.newSingleThreadExecutor();
    private AppTransitionAnimationSpec mDefaultNextAppTransitionAnimationSpec;
    private final int mDefaultWindowAnimationStyleResId;
    private final DisplayContent mDisplayContent;
    /* access modifiers changed from: private */
    public final Interpolator mFastOutLinearInInterpolator;
    private final Interpolator mFastOutSlowInInterpolator;
    private int mForeGroundColor;
    private final boolean mGridLayoutRecentsEnabled;
    final Runnable mHandleAppTransitionTimeoutRunnable = new Runnable() {
        public final void run() {
            AppTransition.this.lambda$new$0$AppTransition();
        }
    };
    final Handler mHandler;
    private String mLastChangingApp;
    private int mLastClipRevealMaxTranslation;
    private long mLastClipRevealTransitionDuration = 336;
    private String mLastClosingApp;
    private boolean mLastHadClipReveal;
    private String mLastOpeningApp;
    private int mLastUsedAppTransition = -1;
    private final Rect mLauncherAnimationRect = new Rect();
    /* access modifiers changed from: private */
    public final Interpolator mLinearOutSlowInInterpolator;
    private final ArrayList<WindowManagerInternal.AppTransitionListener> mListeners = new ArrayList<>();
    private boolean mLoadBackHomeAnimation;
    private boolean mLoadRoundedViewAnimation;
    private final boolean mLowRamRecentsEnabled;
    private int mNextAppTransition = -1;
    private final SparseArray<AppTransitionAnimationSpec> mNextAppTransitionAnimationsSpecs = new SparseArray<>();
    private IAppTransitionAnimationSpecsFuture mNextAppTransitionAnimationsSpecsFuture;
    boolean mNextAppTransitionAnimationsSpecsPending;
    private IRemoteCallback mNextAppTransitionCallback;
    private int mNextAppTransitionEnter;
    private int mNextAppTransitionExit;
    private int mNextAppTransitionFlags = 0;
    private IRemoteCallback mNextAppTransitionFutureCallback;
    private int mNextAppTransitionInPlace;
    private Rect mNextAppTransitionInsets = new Rect();
    private String mNextAppTransitionPackage;
    private boolean mNextAppTransitionScaleUp;
    private int mNextAppTransitionType = 0;
    private int mRadius;
    private RemoteAnimationController mRemoteAnimationController;
    private boolean mScaleBackToScreenCenter;
    private final WindowManagerService mService;
    private final Interpolator mThumbnailFadeInInterpolator;
    private final Interpolator mThumbnailFadeOutInterpolator;
    private Rect mTmpFromClipRect = new Rect();
    private final Rect mTmpRect = new Rect();
    private Rect mTmpToClipRect = new Rect();

    AppTransition(Context context, WindowManagerService service, DisplayContent displayContent) {
        this.mContext = context;
        this.mService = service;
        this.mHandler = new Handler(service.mH.getLooper());
        this.mDisplayContent = displayContent;
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.mConfigShortAnimTime = context.getResources().getInteger(17694720);
        this.mDecelerateInterpolator = AnimationUtils.loadInterpolator(context, 17563651);
        this.mThumbnailFadeInInterpolator = new Interpolator() {
            public float getInterpolation(float input) {
                if (input < 0.5f) {
                    return 0.0f;
                }
                return AppTransition.this.mFastOutLinearInInterpolator.getInterpolation((input - 0.5f) / 0.5f);
            }
        };
        this.mThumbnailFadeOutInterpolator = new Interpolator() {
            public float getInterpolation(float input) {
                if (input >= 0.5f) {
                    return 1.0f;
                }
                return AppTransition.this.mLinearOutSlowInInterpolator.getInterpolation(input / 0.5f);
            }
        };
        this.mClipRevealTranslationY = (int) (this.mContext.getResources().getDisplayMetrics().density * 8.0f);
        this.mGridLayoutRecentsEnabled = SystemProperties.getBoolean("ro.recents.grid", false);
        this.mLowRamRecentsEnabled = ActivityManager.isLowRamDeviceStatic();
        TypedArray windowStyle = this.mContext.getTheme().obtainStyledAttributes(R.styleable.Window);
        this.mDefaultWindowAnimationStyleResId = windowStyle.getResourceId(8, 0);
        windowStyle.recycle();
    }

    /* access modifiers changed from: package-private */
    public boolean isTransitionSet() {
        return this.mNextAppTransition != -1;
    }

    /* access modifiers changed from: package-private */
    public boolean isTransitionEqual(int transit) {
        return this.mNextAppTransition == transit;
    }

    /* access modifiers changed from: package-private */
    public int getAppTransition() {
        return this.mNextAppTransition;
    }

    private void setAppTransition(int transit, int flags) {
        this.mNextAppTransition = transit;
        this.mNextAppTransitionFlags |= flags;
        setLastAppTransition(-1, (AppWindowToken) null, (AppWindowToken) null, (AppWindowToken) null);
        updateBooster();
    }

    /* access modifiers changed from: package-private */
    public void setLastAppTransition(int transit, AppWindowToken openingApp, AppWindowToken closingApp, AppWindowToken changingApp) {
        this.mLastUsedAppTransition = transit;
        this.mLastOpeningApp = "" + openingApp;
        this.mLastClosingApp = "" + closingApp;
        this.mLastChangingApp = "" + changingApp;
    }

    /* access modifiers changed from: package-private */
    public boolean isReady() {
        int i = this.mAppTransitionState;
        return i == 1 || i == 3;
    }

    /* access modifiers changed from: package-private */
    public void setReady() {
        setAppTransitionState(1);
        fetchAppTransitionSpecsFromFuture();
    }

    /* access modifiers changed from: package-private */
    public boolean isRunning() {
        return this.mAppTransitionState == 2;
    }

    /* access modifiers changed from: package-private */
    public void setIdle() {
        setAppTransitionState(0);
    }

    /* access modifiers changed from: package-private */
    public boolean isTimeout() {
        return this.mAppTransitionState == 3;
    }

    /* access modifiers changed from: package-private */
    public void setTimeout() {
        setAppTransitionState(3);
    }

    /* access modifiers changed from: package-private */
    public GraphicBuffer getAppTransitionThumbnailHeader(int taskId) {
        AppTransitionAnimationSpec spec = this.mNextAppTransitionAnimationsSpecs.get(taskId);
        if (spec == null) {
            spec = this.mDefaultNextAppTransitionAnimationSpec;
        }
        if (spec != null) {
            return spec.buffer;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextThumbnailTransitionAspectScaled() {
        int i = this.mNextAppTransitionType;
        return i == 5 || i == 6;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextThumbnailTransitionScaleUp() {
        return this.mNextAppTransitionScaleUp;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextAppTransitionThumbnailUp() {
        int i = this.mNextAppTransitionType;
        return i == 3 || i == 5;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextAppTransitionThumbnailDown() {
        int i = this.mNextAppTransitionType;
        return i == 4 || i == 6;
    }

    /* access modifiers changed from: package-private */
    public boolean isNextAppTransitionOpenCrossProfileApps() {
        return this.mNextAppTransitionType == 9;
    }

    /* access modifiers changed from: package-private */
    public boolean isFetchingAppTransitionsSpecs() {
        return this.mNextAppTransitionAnimationsSpecsPending;
    }

    private boolean prepare() {
        if (isRunning()) {
            return false;
        }
        setAppTransitionState(0);
        notifyAppTransitionPendingLocked();
        this.mLastHadClipReveal = false;
        this.mLastClipRevealMaxTranslation = 0;
        this.mLastClipRevealTransitionDuration = 336;
        return true;
    }

    /* access modifiers changed from: package-private */
    public int goodToGo(int transit, AppWindowToken topOpeningApp, ArraySet<AppWindowToken> openingApps) {
        AnimationAdapter topOpeningAnim;
        long j;
        this.mNextAppTransition = -1;
        this.mNextAppTransitionFlags = 0;
        setAppTransitionState(2);
        if (topOpeningApp != null) {
            topOpeningAnim = topOpeningApp.getAnimation();
        } else {
            topOpeningAnim = null;
        }
        long durationHint = topOpeningAnim != null ? topOpeningAnim.getDurationHint() : 0;
        if (topOpeningAnim != null) {
            j = topOpeningAnim.getStatusBarTransitionsStartTime();
        } else {
            j = SystemClock.uptimeMillis();
        }
        int redoLayout = notifyAppTransitionStartingLocked(transit, durationHint, j, 120);
        this.mDisplayContent.getDockedDividerController().notifyAppTransitionStarting(openingApps, transit);
        RemoteAnimationController remoteAnimationController = this.mRemoteAnimationController;
        if (remoteAnimationController != null) {
            remoteAnimationController.goodToGo();
        }
        return redoLayout;
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        this.mNextAppTransitionType = 0;
        this.mNextAppTransitionPackage = null;
        this.mNextAppTransitionAnimationsSpecs.clear();
        this.mRemoteAnimationController = null;
        this.mNextAppTransitionAnimationsSpecsFuture = null;
        this.mDefaultNextAppTransitionAnimationSpec = null;
        this.mAnimationFinishedCallback = null;
        this.mAnimationReenterFinishedCallback = null;
        this.mLauncherAnimationRect.setEmpty();
        this.mRadius = 0;
        this.mForeGroundColor = 0;
        this.mLoadRoundedViewAnimation = false;
        this.mScaleBackToScreenCenter = false;
    }

    /* access modifiers changed from: package-private */
    public void freeze() {
        int transit = this.mNextAppTransition;
        RemoteAnimationController remoteAnimationController = this.mRemoteAnimationController;
        if (remoteAnimationController != null) {
            remoteAnimationController.cancelAnimation("freeze");
        }
        setAppTransition(-1, 0);
        clear();
        setReady();
        notifyAppTransitionCancelledLocked(transit);
    }

    private void setAppTransitionState(int state) {
        this.mAppTransitionState = state;
        updateBooster();
    }

    /* access modifiers changed from: package-private */
    public void updateBooster() {
        WindowManagerService.sThreadPriorityBooster.setAppTransitionRunning(needsBoosting());
    }

    private boolean needsBoosting() {
        int i;
        boolean recentsAnimRunning = this.mService.getRecentsAnimationController() != null;
        if (this.mNextAppTransition != -1 || (i = this.mAppTransitionState) == 1 || i == 2 || recentsAnimRunning) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void registerListenerLocked(WindowManagerInternal.AppTransitionListener listener) {
        this.mListeners.add(listener);
    }

    /* access modifiers changed from: package-private */
    public void unregisterListener(WindowManagerInternal.AppTransitionListener listener) {
        this.mListeners.remove(listener);
    }

    public void notifyAppTransitionFinishedLocked(IBinder token) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionFinishedLocked(token);
        }
    }

    private void notifyAppTransitionPendingLocked() {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionPendingLocked();
        }
    }

    private void notifyAppTransitionCancelledLocked(int transit) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onAppTransitionCancelledLocked(transit);
        }
    }

    private int notifyAppTransitionStartingLocked(int transit, long duration, long statusBarAnimationStartTime, long statusBarAnimationDuration) {
        int redoLayout = 0;
        for (int i = 0; i < this.mListeners.size(); i++) {
            redoLayout |= this.mListeners.get(i).onAppTransitionStartingLocked(transit, duration, statusBarAnimationStartTime, statusBarAnimationDuration);
        }
        return redoLayout;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getDefaultWindowAnimationStyleResId() {
        return this.mDefaultWindowAnimationStyleResId;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getAnimationStyleResId(WindowManager.LayoutParams lp) {
        int resId = lp.windowAnimations;
        if (!AppTransitionInjector.WHITE_LIST_ALLOW_CUSTOM_APPLICATION_TRANSITION.contains(lp.packageName) && lp.type == 3) {
            return this.mDefaultWindowAnimationStyleResId;
        }
        return resId;
    }

    private AttributeCache.Entry getCachedAnimations(WindowManager.LayoutParams lp) {
        if (lp == null || lp.windowAnimations == 0) {
            return null;
        }
        String packageName = lp.packageName != null ? lp.packageName : PackageManagerService.PLATFORM_PACKAGE_NAME;
        int resId = getAnimationStyleResId(lp);
        if ((-16777216 & resId) == 16777216) {
            packageName = PackageManagerService.PLATFORM_PACKAGE_NAME;
        }
        this.mDefaultActivityAnimation = PackageManagerService.PLATFORM_PACKAGE_NAME.equals(packageName);
        return AttributeCache.instance().get(packageName, resId, R.styleable.WindowAnimation, this.mCurrentUserId);
    }

    private AttributeCache.Entry getCachedAnimations(String packageName, int resId) {
        if (packageName == null) {
            return null;
        }
        if ((-16777216 & resId) == 16777216) {
            packageName = PackageManagerService.PLATFORM_PACKAGE_NAME;
        }
        return AttributeCache.instance().get(packageName, resId, R.styleable.WindowAnimation, this.mCurrentUserId);
    }

    /* access modifiers changed from: package-private */
    public int getResIdAttr(WindowManager.LayoutParams lp, int animAttr, int transit) {
        AttributeCache.Entry ent;
        int resId = 0;
        Context context = this.mContext;
        if (animAttr >= 0 && (ent = getCachedAnimations(lp)) != null) {
            Context context2 = ent.context;
            resId = ent.array.getResourceId(animAttr, 0);
        }
        return updateToTranslucentAnimIfNeeded(resId, transit);
    }

    /* access modifiers changed from: package-private */
    public Animation loadAnimationAttr(WindowManager.LayoutParams lp, int animAttr, int transit) {
        AttributeCache.Entry ent;
        int resId = 0;
        Context context = this.mContext;
        if (animAttr >= 0 && (ent = getCachedAnimations(lp)) != null) {
            context = ent.context;
            resId = ent.array.getResourceId(animAttr, 0);
        }
        int resId2 = updateToTranslucentAnimIfNeeded(resId, transit);
        if (ResourceId.isValid(resId2)) {
            return loadAnimationSafely(context, resId2);
        }
        return null;
    }

    private Animation loadAnimationRes(WindowManager.LayoutParams lp, int resId) {
        Context context = this.mContext;
        if (!ResourceId.isValid(resId)) {
            return null;
        }
        AttributeCache.Entry ent = getCachedAnimations(lp);
        if (ent != null) {
            context = ent.context;
        }
        return loadAnimationSafely(context, resId);
    }

    private Animation loadAnimationRes(String packageName, int resId) {
        AttributeCache.Entry ent;
        if (!ResourceId.isValid(resId) || (ent = getCachedAnimations(packageName, resId)) == null) {
            return null;
        }
        return loadAnimationSafely(ent.context, resId);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Animation loadAnimationSafely(Context context, int resId) {
        try {
            return AnimationUtils.loadAnimation(context, resId);
        } catch (Resources.NotFoundException e) {
            Slog.w("WindowManager", "Unable to load animation resource", e);
            return null;
        }
    }

    private int updateToTranslucentAnimIfNeeded(int anim, int transit) {
        if (transit == 24 && anim == 17432726) {
            return 17432729;
        }
        if (transit == 25 && anim == 17432725) {
            return 17432728;
        }
        return anim;
    }

    private static float computePivot(int startPos, float finalScale) {
        float denom = finalScale - 1.0f;
        if (Math.abs(denom) < 1.0E-4f) {
            return (float) startPos;
        }
        return ((float) (-startPos)) / denom;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: android.view.animation.AnimationSet} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.animation.Animation createScaleUpAnimationLocked(int r18, boolean r19, android.graphics.Rect r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            android.graphics.Rect r2 = r0.mTmpRect
            r0.getDefaultNextAppTransitionStartRect(r2)
            int r2 = r20.width()
            int r3 = r20.height()
            r4 = 0
            r5 = 1
            r6 = 1065353216(0x3f800000, float:1.0)
            if (r19 == 0) goto L_0x0067
            android.graphics.Rect r7 = r0.mTmpRect
            int r7 = r7.width()
            float r7 = (float) r7
            float r8 = (float) r2
            float r7 = r7 / r8
            android.graphics.Rect r8 = r0.mTmpRect
            int r8 = r8.height()
            float r8 = (float) r8
            float r9 = (float) r3
            float r8 = r8 / r9
            android.view.animation.ScaleAnimation r16 = new android.view.animation.ScaleAnimation
            r11 = 1065353216(0x3f800000, float:1.0)
            r13 = 1065353216(0x3f800000, float:1.0)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.left
            float r14 = computePivot(r9, r7)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.top
            float r15 = computePivot(r9, r8)
            r9 = r16
            r10 = r7
            r12 = r8
            r9.<init>(r10, r11, r12, r13, r14, r15)
            android.view.animation.Interpolator r10 = r0.mDecelerateInterpolator
            r9.setInterpolator(r10)
            android.view.animation.AlphaAnimation r10 = new android.view.animation.AlphaAnimation
            r10.<init>(r4, r6)
            r4 = r10
            android.view.animation.Interpolator r6 = r0.mThumbnailFadeOutInterpolator
            r4.setInterpolator(r6)
            android.view.animation.AnimationSet r6 = new android.view.animation.AnimationSet
            r10 = 0
            r6.<init>(r10)
            r6.addAnimation(r9)
            r6.addAnimation(r4)
            r6.setDetachWallpaper(r5)
            r4 = r6
            goto L_0x007f
        L_0x0067:
            r7 = 14
            if (r1 == r7) goto L_0x0076
            r7 = 15
            if (r1 != r7) goto L_0x0070
            goto L_0x0076
        L_0x0070:
            android.view.animation.AlphaAnimation r4 = new android.view.animation.AlphaAnimation
            r4.<init>(r6, r6)
            goto L_0x007f
        L_0x0076:
            android.view.animation.AlphaAnimation r7 = new android.view.animation.AlphaAnimation
            r7.<init>(r6, r4)
            r4 = r7
            r4.setDetachWallpaper(r5)
        L_0x007f:
            r6 = 6
            if (r1 == r6) goto L_0x0088
            r6 = 7
            if (r1 == r6) goto L_0x0088
            r6 = 336(0x150, double:1.66E-321)
            goto L_0x008c
        L_0x0088:
            int r6 = r0.mConfigShortAnimTime
            long r6 = (long) r6
        L_0x008c:
            r4.setDuration(r6)
            r4.setFillAfter(r5)
            android.view.animation.Interpolator r5 = r0.mDecelerateInterpolator
            r4.setInterpolator(r5)
            r4.initialize(r2, r3, r2, r3)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransition.createScaleUpAnimationLocked(int, boolean, android.graphics.Rect):android.view.animation.Animation");
    }

    private void getDefaultNextAppTransitionStartRect(Rect rect) {
        AppTransitionAnimationSpec appTransitionAnimationSpec = this.mDefaultNextAppTransitionAnimationSpec;
        if (appTransitionAnimationSpec == null || appTransitionAnimationSpec.rect == null) {
            Slog.e("WindowManager", "Starting rect for app requested, but none available", new Throwable());
            rect.setEmpty();
            return;
        }
        rect.set(this.mDefaultNextAppTransitionAnimationSpec.rect);
    }

    /* access modifiers changed from: package-private */
    public void getNextAppTransitionStartRect(int taskId, Rect rect) {
        AppTransitionAnimationSpec spec = this.mNextAppTransitionAnimationsSpecs.get(taskId);
        if (spec == null) {
            spec = this.mDefaultNextAppTransitionAnimationSpec;
        }
        if (spec == null || spec.rect == null) {
            Slog.e("WindowManager", "Starting rect for task: " + taskId + " requested, but not available", new Throwable());
            rect.setEmpty();
            return;
        }
        rect.set(spec.rect);
    }

    private void putDefaultNextAppTransitionCoordinates(int left, int top, int width, int height, GraphicBuffer buffer) {
        this.mDefaultNextAppTransitionAnimationSpec = new AppTransitionAnimationSpec(-1, buffer, new Rect(left, top, left + width, top + height));
    }

    /* access modifiers changed from: package-private */
    public long getLastClipRevealTransitionDuration() {
        return this.mLastClipRevealTransitionDuration;
    }

    /* access modifiers changed from: package-private */
    public int getLastClipRevealMaxTranslation() {
        return this.mLastClipRevealMaxTranslation;
    }

    /* access modifiers changed from: package-private */
    public boolean hadClipRevealAnimation() {
        return this.mLastHadClipReveal;
    }

    private long calculateClipRevealTransitionDuration(boolean cutOff, float translationX, float translationY, Rect displayFrame) {
        if (!cutOff) {
            return 336;
        }
        return (long) ((84.0f * Math.max(Math.abs(translationX) / ((float) displayFrame.width()), Math.abs(translationY) / ((float) displayFrame.height()))) + 336.0f);
    }

    /* JADX WARNING: type inference failed for: r4v6, types: [android.view.animation.Animation] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.animation.Animation createClipRevealAnimationLocked(int r32, boolean r33, android.graphics.Rect r34, android.graphics.Rect r35) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            r2 = r34
            r3 = 0
            if (r33 == 0) goto L_0x0160
            int r6 = r34.width()
            int r15 = r34.height()
            android.graphics.Rect r7 = r0.mTmpRect
            r0.getDefaultNextAppTransitionStartRect(r7)
            r7 = 0
            if (r15 <= 0) goto L_0x0028
            android.graphics.Rect r8 = r0.mTmpRect
            int r8 = r8.top
            float r8 = (float) r8
            int r9 = r35.height()
            float r9 = (float) r9
            float r7 = r8 / r9
            r16 = r7
            goto L_0x002a
        L_0x0028:
            r16 = r7
        L_0x002a:
            int r7 = r0.mClipRevealTranslationY
            int r8 = r35.height()
            float r8 = (float) r8
            r9 = 1088421888(0x40e00000, float:7.0)
            float r8 = r8 / r9
            float r8 = r8 * r16
            int r8 = (int) r8
            int r7 = r7 + r8
            r8 = 0
            r9 = r7
            android.graphics.Rect r10 = r0.mTmpRect
            int r17 = r10.centerX()
            android.graphics.Rect r10 = r0.mTmpRect
            int r18 = r10.centerY()
            android.graphics.Rect r10 = r0.mTmpRect
            int r10 = r10.width()
            int r19 = r10 / 2
            android.graphics.Rect r10 = r0.mTmpRect
            int r10 = r10.height()
            int r20 = r10 / 2
            int r10 = r17 - r19
            int r11 = r2.left
            int r10 = r10 - r11
            int r11 = r18 - r20
            int r12 = r2.top
            int r11 = r11 - r12
            r12 = 0
            int r13 = r2.top
            int r14 = r18 - r20
            if (r13 <= r14) goto L_0x0076
            int r13 = r18 - r20
            int r14 = r2.top
            int r7 = r13 - r14
            r9 = 0
            r11 = 0
            r12 = 1
            r14 = r7
            r22 = r9
            r21 = r11
            goto L_0x007b
        L_0x0076:
            r14 = r7
            r22 = r9
            r21 = r11
        L_0x007b:
            int r7 = r2.left
            int r9 = r17 - r19
            if (r7 <= r9) goto L_0x0089
            int r7 = r17 - r19
            int r9 = r2.left
            int r8 = r7 - r9
            r10 = 0
            r12 = 1
        L_0x0089:
            int r7 = r2.right
            int r9 = r17 + r19
            if (r7 >= r9) goto L_0x00a2
            int r7 = r17 + r19
            int r9 = r2.right
            int r8 = r7 - r9
            android.graphics.Rect r7 = r0.mTmpRect
            int r7 = r7.width()
            int r10 = r6 - r7
            r12 = 1
            r11 = r10
            r13 = r12
            r12 = r8
            goto L_0x00a5
        L_0x00a2:
            r11 = r10
            r13 = r12
            r12 = r8
        L_0x00a5:
            float r7 = (float) r12
            float r8 = (float) r14
            r10 = r35
            long r8 = r0.calculateClipRevealTransitionDuration(r13, r7, r8, r10)
            com.android.server.wm.animation.ClipRectLRAnimation r7 = new com.android.server.wm.animation.ClipRectLRAnimation
            android.graphics.Rect r4 = r0.mTmpRect
            int r4 = r4.width()
            int r4 = r4 + r11
            r5 = 0
            r7.<init>(r11, r4, r5, r6)
            r4 = r7
            android.view.animation.Interpolator r7 = r0.mClipHorizontalInterpolator
            r4.setInterpolator(r7)
            float r7 = (float) r8
            r24 = 1075838976(0x40200000, float:2.5)
            float r7 = r7 / r24
            r24 = r6
            long r5 = (long) r7
            r4.setDuration(r5)
            android.view.animation.TranslateAnimation r5 = new android.view.animation.TranslateAnimation
            float r6 = (float) r12
            float r7 = (float) r14
            r5.<init>(r6, r3, r7, r3)
            r3 = r5
            if (r13 == 0) goto L_0x00d8
            android.view.animation.Interpolator r5 = TOUCH_RESPONSE_INTERPOLATOR
            goto L_0x00da
        L_0x00d8:
            android.view.animation.Interpolator r5 = r0.mLinearOutSlowInInterpolator
        L_0x00da:
            r3.setInterpolator(r5)
            r3.setDuration(r8)
            com.android.server.wm.animation.ClipRectTBAnimation r5 = new com.android.server.wm.animation.ClipRectTBAnimation
            android.graphics.Rect r6 = r0.mTmpRect
            int r6 = r6.height()
            int r6 = r21 + r6
            r25 = 0
            r26 = 0
            android.view.animation.Interpolator r7 = r0.mLinearOutSlowInInterpolator
            r27 = r7
            r7 = r5
            r28 = r8
            r8 = r21
            r9 = r6
            r10 = r25
            r6 = r11
            r11 = r15
            r25 = r12
            r12 = r22
            r30 = r13
            r13 = r26
            r26 = r14
            r14 = r27
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)
            android.view.animation.Interpolator r7 = TOUCH_RESPONSE_INTERPOLATOR
            r5.setInterpolator(r7)
            r7 = r28
            r5.setDuration(r7)
            r9 = 4
            long r9 = r7 / r9
            android.view.animation.AlphaAnimation r11 = new android.view.animation.AlphaAnimation
            r12 = 1056964608(0x3f000000, float:0.5)
            r13 = 1065353216(0x3f800000, float:1.0)
            r11.<init>(r12, r13)
            r11.setDuration(r9)
            android.view.animation.Interpolator r12 = r0.mLinearOutSlowInInterpolator
            r11.setInterpolator(r12)
            android.view.animation.AnimationSet r12 = new android.view.animation.AnimationSet
            r13 = 0
            r12.<init>(r13)
            r12.addAnimation(r4)
            r12.addAnimation(r5)
            r12.addAnimation(r3)
            r12.addAnimation(r11)
            r14 = 1
            r12.setZAdjustment(r14)
            r13 = r24
            r12.initialize(r13, r15, r13, r15)
            r23 = r12
            r0.mLastHadClipReveal = r14
            r0.mLastClipRevealTransitionDuration = r7
            if (r30 == 0) goto L_0x015a
            int r14 = java.lang.Math.abs(r26)
            int r2 = java.lang.Math.abs(r25)
            int r2 = java.lang.Math.max(r14, r2)
            goto L_0x015b
        L_0x015a:
            r2 = 0
        L_0x015b:
            r0.mLastClipRevealMaxTranslation = r2
            r2 = r23
            goto L_0x0197
        L_0x0160:
            r2 = 6
            if (r1 == r2) goto L_0x0169
            r2 = 7
            if (r1 == r2) goto L_0x0169
            r4 = 336(0x150, double:1.66E-321)
            goto L_0x016d
        L_0x0169:
            int r2 = r0.mConfigShortAnimTime
            long r4 = (long) r2
        L_0x016d:
            r2 = 14
            if (r1 == r2) goto L_0x0181
            r2 = 15
            if (r1 != r2) goto L_0x0178
            r6 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0183
        L_0x0178:
            android.view.animation.AlphaAnimation r2 = new android.view.animation.AlphaAnimation
            r6 = 1065353216(0x3f800000, float:1.0)
            r2.<init>(r6, r6)
            r3 = 1
            goto L_0x018c
        L_0x0181:
            r6 = 1065353216(0x3f800000, float:1.0)
        L_0x0183:
            android.view.animation.AlphaAnimation r2 = new android.view.animation.AlphaAnimation
            r2.<init>(r6, r3)
            r3 = 1
            r2.setDetachWallpaper(r3)
        L_0x018c:
            android.view.animation.Interpolator r6 = r0.mDecelerateInterpolator
            r2.setInterpolator(r6)
            r2.setDuration(r4)
            r2.setFillAfter(r3)
        L_0x0197:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransition.createClipRevealAnimationLocked(int, boolean, android.graphics.Rect, android.graphics.Rect):android.view.animation.Animation");
    }

    /* access modifiers changed from: package-private */
    public Animation prepareThumbnailAnimationWithDuration(Animation a, int appWidth, int appHeight, long duration, Interpolator interpolator) {
        if (duration > 0) {
            a.setDuration(duration);
        }
        a.setFillAfter(true);
        if (interpolator != null) {
            a.setInterpolator(interpolator);
        }
        a.initialize(appWidth, appHeight, appWidth, appHeight);
        return a;
    }

    /* access modifiers changed from: package-private */
    public Animation prepareThumbnailAnimation(Animation a, int appWidth, int appHeight, int transit) {
        int duration;
        if (transit == 6 || transit == 7) {
            duration = this.mConfigShortAnimTime;
        } else {
            duration = 336;
        }
        return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, (long) duration, this.mDecelerateInterpolator);
    }

    /* access modifiers changed from: package-private */
    public int getThumbnailTransitionState(boolean enter) {
        if (enter) {
            if (this.mNextAppTransitionScaleUp) {
                return 0;
            }
            return 2;
        } else if (this.mNextAppTransitionScaleUp) {
            return 1;
        } else {
            return 3;
        }
    }

    /* access modifiers changed from: package-private */
    public GraphicBuffer createCrossProfileAppsThumbnail(int thumbnailDrawableRes, Rect frame) {
        int width = frame.width();
        int height = frame.height();
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(width, height);
        canvas.drawColor(Color.argb(0.6f, 0.0f, 0.0f, 0.0f));
        int thumbnailSize = this.mService.mContext.getResources().getDimensionPixelSize(17105086);
        Drawable drawable = this.mService.mContext.getDrawable(thumbnailDrawableRes);
        drawable.setBounds((width - thumbnailSize) / 2, (height - thumbnailSize) / 2, (width + thumbnailSize) / 2, (height + thumbnailSize) / 2);
        drawable.setTint(this.mContext.getColor(17170443));
        drawable.draw(canvas);
        picture.endRecording();
        return Bitmap.createBitmap(picture).createGraphicBufferHandle();
    }

    /* access modifiers changed from: package-private */
    public Animation createCrossProfileAppsThumbnailAnimationLocked(Rect appRect) {
        return prepareThumbnailAnimationWithDuration(loadAnimationRes(PackageManagerService.PLATFORM_PACKAGE_NAME, 17432744), appRect.width(), appRect.height(), 0, (Interpolator) null);
    }

    /* access modifiers changed from: package-private */
    public Animation createThumbnailAspectScaleAnimationLocked(Rect appRect, Rect contentInsets, GraphicBuffer thumbnailHeader, int taskId, int uiMode, int orientation) {
        float pivotY;
        float pivotX;
        float fromY;
        float fromY2;
        float fromX;
        float pivotX2;
        int appWidth;
        Animation translate;
        float fromY3;
        long j;
        float fromY4;
        Rect rect = appRect;
        Rect rect2 = contentInsets;
        int thumbWidthI = thumbnailHeader.getWidth();
        float thumbWidth = thumbWidthI > 0 ? (float) thumbWidthI : 1.0f;
        int thumbHeightI = thumbnailHeader.getHeight();
        int appWidth2 = appRect.width();
        float scaleW = ((float) appWidth2) / thumbWidth;
        getNextAppTransitionStartRect(taskId, this.mTmpRect);
        if (shouldScaleDownThumbnailTransition(uiMode, orientation)) {
            float fromX2 = (float) this.mTmpRect.left;
            float fromY5 = (float) this.mTmpRect.top;
            fromY = (((float) (this.mTmpRect.width() / 2)) * (scaleW - 1.0f)) + ((float) rect.left);
            float toY = (((float) (appRect.height() / 2)) * (1.0f - (1.0f / scaleW))) + ((float) rect.top);
            float pivotX3 = (float) (this.mTmpRect.width() / 2);
            float pivotY2 = ((float) (appRect.height() / 2)) / scaleW;
            if (this.mGridLayoutRecentsEnabled) {
                pivotX2 = fromY5 - ((float) thumbHeightI);
                pivotX = pivotX3;
                pivotY = pivotY2;
                fromY2 = fromX2;
                fromX = toY - (((float) thumbHeightI) * scaleW);
            } else {
                pivotX2 = fromY5;
                pivotX = pivotX3;
                pivotY = pivotY2;
                fromY2 = fromX2;
                fromX = toY;
            }
        } else {
            fromY2 = (float) this.mTmpRect.left;
            pivotX = 0.0f;
            pivotY = 0.0f;
            pivotX2 = (float) this.mTmpRect.top;
            fromY = (float) rect.left;
            fromX = (float) rect.top;
        }
        long duration = getAspectScaleDuration();
        Interpolator interpolator = getAspectScaleInterpolator();
        long duration2 = duration;
        if (this.mNextAppTransitionScaleUp) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, scaleW, 1.0f, scaleW, pivotX, pivotY);
            scaleAnimation.setInterpolator(interpolator);
            long duration3 = duration2;
            scaleAnimation.setDuration(duration3);
            float f = thumbWidth;
            appWidth = appWidth2;
            Animation alpha = new AlphaAnimation(1.0f, 0.0f);
            alpha.setInterpolator(this.mNextAppTransition == 19 ? THUMBNAIL_DOCK_INTERPOLATOR : this.mThumbnailFadeOutInterpolator);
            if (this.mNextAppTransition == 19) {
                j = duration3 / 2;
            } else {
                j = duration3;
            }
            alpha.setDuration(j);
            Animation translate2 = createCurvedMotion(fromY2, fromY, pivotX2, fromX);
            translate2.setInterpolator(interpolator);
            translate2.setDuration(duration3);
            this.mTmpFromClipRect.set(0, 0, thumbWidthI, thumbHeightI);
            this.mTmpToClipRect.set(appRect);
            this.mTmpToClipRect.offsetTo(0, 0);
            Rect rect3 = this.mTmpToClipRect;
            rect3.right = (int) (((float) rect3.right) / scaleW);
            Rect rect4 = this.mTmpToClipRect;
            rect4.bottom = (int) (((float) rect4.bottom) / scaleW);
            Rect rect5 = contentInsets;
            if (rect5 != null) {
                int i = thumbWidthI;
                int i2 = thumbHeightI;
                fromY4 = pivotX2;
                this.mTmpToClipRect.inset((int) (((float) (-rect5.left)) * scaleW), (int) (((float) (-rect5.top)) * scaleW), (int) (((float) (-rect5.right)) * scaleW), (int) (((float) (-rect5.bottom)) * scaleW));
            } else {
                fromY4 = pivotX2;
                int i3 = thumbWidthI;
                int i4 = thumbHeightI;
            }
            Animation clipAnim = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
            clipAnim.setInterpolator(interpolator);
            clipAnim.setDuration(duration3);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(scaleAnimation);
            if (!this.mGridLayoutRecentsEnabled) {
                set.addAnimation(alpha);
            }
            set.addAnimation(translate2);
            set.addAnimation(clipAnim);
            translate = set;
            long j2 = duration3;
            fromY3 = fromY4;
        } else {
            int i5 = thumbWidthI;
            float f2 = thumbWidth;
            int i6 = thumbHeightI;
            appWidth = appWidth2;
            long duration4 = duration2;
            ScaleAnimation scaleAnimation2 = new ScaleAnimation(scaleW, 1.0f, scaleW, 1.0f, pivotX, pivotY);
            scaleAnimation2.setInterpolator(interpolator);
            scaleAnimation2.setDuration(duration4);
            Animation alpha2 = new AlphaAnimation(0.0f, 1.0f);
            alpha2.setInterpolator(this.mThumbnailFadeInInterpolator);
            alpha2.setDuration(duration4);
            fromY3 = pivotX2;
            Animation translate3 = createCurvedMotion(fromY, fromY2, fromX, fromY3);
            translate3.setInterpolator(interpolator);
            translate3.setDuration(duration4);
            AnimationSet set2 = new AnimationSet(false);
            set2.addAnimation(scaleAnimation2);
            if (!this.mGridLayoutRecentsEnabled) {
                set2.addAnimation(alpha2);
            }
            set2.addAnimation(translate3);
            translate = set2;
        }
        float f3 = fromY3;
        float f4 = fromX;
        float f5 = fromY2;
        float f6 = fromY;
        Interpolator interpolator2 = interpolator;
        return prepareThumbnailAnimationWithDuration(translate, appWidth, appRect.height(), 0, (Interpolator) null);
    }

    private Animation createCurvedMotion(float fromX, float toX, float fromY, float toY) {
        if (Math.abs(toX - fromX) < 1.0f || this.mNextAppTransition != 19) {
            return new TranslateAnimation(fromX, toX, fromY, toY);
        }
        return new CurvedTranslateAnimation(createCurvedPath(fromX, toX, fromY, toY));
    }

    private Path createCurvedPath(float fromX, float toX, float fromY, float toY) {
        Path path = new Path();
        path.moveTo(fromX, fromY);
        if (fromY > toY) {
            path.cubicTo(fromX, fromY, toX, (0.9f * fromY) + (0.1f * toY), toX, toY);
        } else {
            path.cubicTo(fromX, fromY, fromX, (0.1f * fromY) + (0.9f * toY), toX, toY);
        }
        return path;
    }

    private long getAspectScaleDuration() {
        if (this.mNextAppTransition == 19) {
            return 453;
        }
        return 336;
    }

    private Interpolator getAspectScaleInterpolator() {
        if (this.mNextAppTransition == 19) {
            return this.mFastOutSlowInInterpolator;
        }
        return TOUCH_RESPONSE_INTERPOLATOR;
    }

    /* access modifiers changed from: package-private */
    public Animation createAspectScaledThumbnailEnterExitAnimationLocked(int thumbTransitState, int uiMode, int orientation, int transit, Rect containingFrame, Rect contentInsets, Rect surfaceInsets, Rect stableInsets, boolean freeform, int taskId) {
        int appWidth;
        Animation a;
        Animation clipAnim;
        Animation translateAnim;
        Animation animation;
        Animation translateAnim2;
        int i = thumbTransitState;
        int i2 = transit;
        Rect rect = containingFrame;
        Rect rect2 = contentInsets;
        Rect rect3 = surfaceInsets;
        Rect rect4 = stableInsets;
        int i3 = taskId;
        int appWidth2 = containingFrame.width();
        int appHeight = containingFrame.height();
        getDefaultNextAppTransitionStartRect(this.mTmpRect);
        int thumbWidthI = this.mTmpRect.width();
        float thumbWidth = thumbWidthI > 0 ? (float) thumbWidthI : 1.0f;
        int thumbHeightI = this.mTmpRect.height();
        float thumbHeight = thumbHeightI > 0 ? (float) thumbHeightI : 1.0f;
        int thumbStartX = (this.mTmpRect.left - rect.left) - rect2.left;
        int thumbStartY = this.mTmpRect.top - rect.top;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        throw new RuntimeException("Invalid thumbnail transition state");
                    }
                } else if (i2 == 14) {
                    a = new AlphaAnimation(0.0f, 1.0f);
                    appWidth = appWidth2;
                } else {
                    a = new AlphaAnimation(1.0f, 1.0f);
                    appWidth = appWidth2;
                }
            } else if (i2 == 14) {
                a = new AlphaAnimation(1.0f, 0.0f);
                appWidth = appWidth2;
            } else {
                a = new AlphaAnimation(1.0f, 1.0f);
                appWidth = appWidth2;
            }
            int i4 = thumbHeightI;
            int i5 = thumbStartX;
            int i6 = thumbWidthI;
            return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, getAspectScaleDuration(), getAspectScaleInterpolator());
        }
        boolean scaleUp = i == 0;
        if (!freeform || !scaleUp) {
            if (freeform) {
                a = createAspectScaledThumbnailExitFreeformAnimationLocked(rect, rect3, i3);
                appWidth = appWidth2;
            } else {
                AnimationSet set = new AnimationSet(true);
                this.mTmpFromClipRect.set(rect);
                this.mTmpToClipRect.set(rect);
                this.mTmpFromClipRect.offsetTo(0, 0);
                this.mTmpToClipRect.offsetTo(0, 0);
                this.mTmpFromClipRect.inset(rect2);
                this.mNextAppTransitionInsets.set(rect2);
                if (shouldScaleDownThumbnailTransition(uiMode, orientation)) {
                    float scale = thumbWidth / ((float) ((appWidth2 - rect2.left) - rect2.right));
                    if (!this.mGridLayoutRecentsEnabled) {
                        Rect rect5 = this.mTmpFromClipRect;
                        rect5.bottom = rect5.top + ((int) (thumbHeight / scale));
                    }
                    this.mNextAppTransitionInsets.set(rect2);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(scaleUp ? scale : 1.0f, scaleUp ? 1.0f : scale, scaleUp ? scale : 1.0f, scaleUp ? 1.0f : scale, ((float) containingFrame.width()) / 2.0f, (((float) containingFrame.height()) / 2.0f) + ((float) rect2.top));
                    float targetX = (float) (this.mTmpRect.left - rect.left);
                    float x = (((float) containingFrame.width()) / 2.0f) - ((((float) containingFrame.width()) / 2.0f) * scale);
                    float targetY = (float) (this.mTmpRect.top - rect.top);
                    float y = (((float) containingFrame.height()) / 2.0f) - ((((float) containingFrame.height()) / 2.0f) * scale);
                    if (!this.mLowRamRecentsEnabled || rect2.top != 0 || !scaleUp) {
                        appWidth = appWidth2;
                    } else {
                        float f = scale;
                        appWidth = appWidth2;
                        this.mTmpFromClipRect.top += rect4.top;
                        y += (float) rect4.top;
                    }
                    float scale2 = targetX - x;
                    float startY = targetY - y;
                    if (scaleUp) {
                        float f2 = targetX;
                        float f3 = x;
                        animation = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
                    } else {
                        float f4 = x;
                        animation = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
                    }
                    Animation clipAnim2 = animation;
                    if (scaleUp) {
                        translateAnim2 = createCurvedMotion(scale2, 0.0f, startY - ((float) rect2.top), 0.0f);
                    } else {
                        translateAnim2 = createCurvedMotion(0.0f, scale2, 0.0f, startY - ((float) rect2.top));
                    }
                    set.addAnimation(clipAnim2);
                    set.addAnimation(scaleAnimation);
                    set.addAnimation(translateAnim2);
                } else {
                    appWidth = appWidth2;
                    Rect rect6 = this.mTmpFromClipRect;
                    rect6.bottom = rect6.top + thumbHeightI;
                    Rect rect7 = this.mTmpFromClipRect;
                    rect7.right = rect7.left + thumbWidthI;
                    if (scaleUp) {
                        clipAnim = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
                    } else {
                        clipAnim = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
                    }
                    if (scaleUp) {
                        translateAnim = createCurvedMotion((float) thumbStartX, 0.0f, (float) (thumbStartY - rect2.top), 0.0f);
                    } else {
                        translateAnim = createCurvedMotion(0.0f, (float) thumbStartX, 0.0f, (float) (thumbStartY - rect2.top));
                    }
                    set.addAnimation(clipAnim);
                    set.addAnimation(translateAnim);
                }
                Animation animation2 = set;
                animation2.setZAdjustment(1);
                a = animation2;
            }
            int i42 = thumbHeightI;
            int i52 = thumbStartX;
            int i62 = thumbWidthI;
            return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, getAspectScaleDuration(), getAspectScaleInterpolator());
        }
        a = createAspectScaledThumbnailEnterFreeformAnimationLocked(rect, rect3, i3);
        appWidth = appWidth2;
        int i422 = thumbHeightI;
        int i522 = thumbStartX;
        int i622 = thumbWidthI;
        return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, getAspectScaleDuration(), getAspectScaleInterpolator());
    }

    private Animation createAspectScaledThumbnailEnterFreeformAnimationLocked(Rect frame, Rect surfaceInsets, int taskId) {
        getNextAppTransitionStartRect(taskId, this.mTmpRect);
        return createAspectScaledThumbnailFreeformAnimationLocked(this.mTmpRect, frame, surfaceInsets, true);
    }

    private Animation createAspectScaledThumbnailExitFreeformAnimationLocked(Rect frame, Rect surfaceInsets, int taskId) {
        getNextAppTransitionStartRect(taskId, this.mTmpRect);
        return createAspectScaledThumbnailFreeformAnimationLocked(frame, this.mTmpRect, surfaceInsets, false);
    }

    private AnimationSet createAspectScaledThumbnailFreeformAnimationLocked(Rect sourceFrame, Rect destFrame, Rect surfaceInsets, boolean enter) {
        ScaleAnimation scaleAnimation;
        TranslateAnimation translation;
        Rect rect = sourceFrame;
        Rect rect2 = destFrame;
        Rect rect3 = surfaceInsets;
        float sourceWidth = (float) sourceFrame.width();
        float sourceHeight = (float) sourceFrame.height();
        float destWidth = (float) destFrame.width();
        float destHeight = (float) destFrame.height();
        float scaleH = enter ? sourceWidth / destWidth : destWidth / sourceWidth;
        float scaleV = enter ? sourceHeight / destHeight : destHeight / sourceHeight;
        AnimationSet set = new AnimationSet(true);
        int i = 0;
        int surfaceInsetsH = rect3 == null ? 0 : rect3.left + rect3.right;
        if (rect3 != null) {
            i = rect3.top + rect3.bottom;
        }
        int surfaceInsetsV = i;
        float scaleHCenter = ((enter ? destWidth : sourceWidth) + ((float) surfaceInsetsH)) / 2.0f;
        float scaleVCenter = ((enter ? destHeight : sourceHeight) + ((float) surfaceInsetsV)) / 2.0f;
        if (enter) {
            int i2 = surfaceInsetsV;
            int i3 = surfaceInsetsH;
            scaleAnimation = new ScaleAnimation(scaleH, 1.0f, scaleV, 1.0f, scaleHCenter, scaleVCenter);
        } else {
            int i4 = surfaceInsetsH;
            scaleAnimation = new ScaleAnimation(1.0f, scaleH, 1.0f, scaleV, scaleHCenter, scaleVCenter);
        }
        ScaleAnimation scale = scaleAnimation;
        int sourceHCenter = rect.left + (sourceFrame.width() / 2);
        int sourceVCenter = rect.top + (sourceFrame.height() / 2);
        int destHCenter = rect2.left + (destFrame.width() / 2);
        int destVCenter = rect2.top + (destFrame.height() / 2);
        int fromX = enter ? sourceHCenter - destHCenter : destHCenter - sourceHCenter;
        int fromY = enter ? sourceVCenter - destVCenter : destVCenter - sourceVCenter;
        if (enter) {
            float f = sourceWidth;
            int fromY2 = fromY;
            float f2 = sourceHeight;
            float f3 = destWidth;
            translation = new TranslateAnimation((float) fromX, 0.0f, (float) fromY2, 0.0f);
        } else {
            int fromX2 = fromX;
            float f4 = sourceWidth;
            int fromY3 = fromY;
            float f5 = sourceHeight;
            translation = new TranslateAnimation(0.0f, (float) fromX2, 0.0f, (float) fromY3);
        }
        set.addAnimation(scale);
        set.addAnimation(translation);
        final IRemoteCallback callback = this.mAnimationFinishedCallback;
        if (callback != null) {
            set.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    AppTransition.this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppTransition$3$llbNiZO5SMSamZHTNM_5S77eNNU.INSTANCE, callback));
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        return set;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: android.view.animation.ScaleAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: android.view.animation.ScaleAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: android.view.animation.AnimationSet} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: android.view.animation.ScaleAnimation} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.animation.Animation createThumbnailScaleAnimationLocked(int r19, int r20, int r21, android.graphics.GraphicBuffer r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            android.graphics.Rect r3 = r0.mTmpRect
            r0.getDefaultNextAppTransitionStartRect(r3)
            int r3 = r22.getWidth()
            r4 = 1065353216(0x3f800000, float:1.0)
            if (r3 <= 0) goto L_0x0015
            float r5 = (float) r3
            goto L_0x0016
        L_0x0015:
            r5 = r4
        L_0x0016:
            int r6 = r22.getHeight()
            if (r6 <= 0) goto L_0x001e
            float r7 = (float) r6
            goto L_0x001f
        L_0x001e:
            r7 = r4
        L_0x001f:
            boolean r8 = r0.mNextAppTransitionScaleUp
            if (r8 == 0) goto L_0x0069
            float r8 = (float) r1
            float r8 = r8 / r5
            float r9 = (float) r2
            float r16 = r9 / r7
            android.view.animation.ScaleAnimation r17 = new android.view.animation.ScaleAnimation
            r10 = 1065353216(0x3f800000, float:1.0)
            r12 = 1065353216(0x3f800000, float:1.0)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.left
            float r11 = r4 / r8
            float r14 = computePivot(r9, r11)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.top
            float r11 = r4 / r16
            float r15 = computePivot(r9, r11)
            r9 = r17
            r11 = r8
            r13 = r16
            r9.<init>(r10, r11, r12, r13, r14, r15)
            android.view.animation.Interpolator r10 = r0.mDecelerateInterpolator
            r9.setInterpolator(r10)
            android.view.animation.AlphaAnimation r10 = new android.view.animation.AlphaAnimation
            r11 = 0
            r10.<init>(r4, r11)
            r4 = r10
            android.view.animation.Interpolator r10 = r0.mThumbnailFadeOutInterpolator
            r4.setInterpolator(r10)
            android.view.animation.AnimationSet r10 = new android.view.animation.AnimationSet
            r11 = 0
            r10.<init>(r11)
            r10.addAnimation(r9)
            r10.addAnimation(r4)
            r4 = r10
            goto L_0x0092
        L_0x0069:
            float r8 = (float) r1
            float r8 = r8 / r5
            float r9 = (float) r2
            float r16 = r9 / r7
            android.view.animation.ScaleAnimation r17 = new android.view.animation.ScaleAnimation
            r11 = 1065353216(0x3f800000, float:1.0)
            r13 = 1065353216(0x3f800000, float:1.0)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.left
            float r10 = r4 / r8
            float r14 = computePivot(r9, r10)
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.top
            float r4 = r4 / r16
            float r15 = computePivot(r9, r4)
            r9 = r17
            r10 = r8
            r12 = r16
            r9.<init>(r10, r11, r12, r13, r14, r15)
            r4 = r17
        L_0x0092:
            r8 = r21
            android.view.animation.Animation r9 = r0.prepareThumbnailAnimation(r4, r1, r2, r8)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransition.createThumbnailScaleAnimationLocked(int, int, int, android.graphics.GraphicBuffer):android.view.animation.Animation");
    }

    /* access modifiers changed from: package-private */
    public Animation createThumbnailEnterExitAnimationLocked(int thumbTransitState, Rect containingFrame, int transit, int taskId) {
        Animation a;
        int i = thumbTransitState;
        int i2 = transit;
        int appWidth = containingFrame.width();
        int appHeight = containingFrame.height();
        GraphicBuffer thumbnailHeader = getAppTransitionThumbnailHeader(taskId);
        getDefaultNextAppTransitionStartRect(this.mTmpRect);
        int thumbWidthI = thumbnailHeader != null ? thumbnailHeader.getWidth() : appWidth;
        float thumbWidth = thumbWidthI > 0 ? (float) thumbWidthI : 1.0f;
        int thumbHeightI = thumbnailHeader != null ? thumbnailHeader.getHeight() : appHeight;
        float thumbHeight = thumbHeightI > 0 ? (float) thumbHeightI : 1.0f;
        if (i == 0) {
            float scaleW = thumbWidth / ((float) appWidth);
            float scaleH = thumbHeight / ((float) appHeight);
            a = new ScaleAnimation(scaleW, 1.0f, scaleH, 1.0f, computePivot(this.mTmpRect.left, scaleW), computePivot(this.mTmpRect.top, scaleH));
        } else if (i != 1) {
            if (i == 2) {
                a = new AlphaAnimation(1.0f, 1.0f);
            } else if (i == 3) {
                float scaleW2 = thumbWidth / ((float) appWidth);
                float scaleH2 = thumbHeight / ((float) appHeight);
                Animation scale = new ScaleAnimation(1.0f, scaleW2, 1.0f, scaleH2, computePivot(this.mTmpRect.left, scaleW2), computePivot(this.mTmpRect.top, scaleH2));
                Animation alpha = new AlphaAnimation(1.0f, 0.0f);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(scale);
                set.addAnimation(alpha);
                set.setZAdjustment(1);
                a = set;
            } else {
                throw new RuntimeException("Invalid thumbnail transition state");
            }
        } else if (i2 == 14) {
            a = new AlphaAnimation(1.0f, 0.0f);
        } else {
            a = new AlphaAnimation(1.0f, 1.0f);
        }
        return prepareThumbnailAnimation(a, appWidth, appHeight, i2);
    }

    private Animation createRelaunchAnimation(Rect containingFrame, Rect contentInsets) {
        getDefaultNextAppTransitionStartRect(this.mTmpFromClipRect);
        int left = this.mTmpFromClipRect.left;
        int top = this.mTmpFromClipRect.top;
        this.mTmpFromClipRect.offset(-left, -top);
        this.mTmpToClipRect.set(0, 0, containingFrame.width(), containingFrame.height());
        AnimationSet set = new AnimationSet(true);
        float fromWidth = (float) this.mTmpFromClipRect.width();
        float toWidth = (float) this.mTmpToClipRect.width();
        float fromHeight = (float) this.mTmpFromClipRect.height();
        float toHeight = (float) ((this.mTmpToClipRect.height() - contentInsets.top) - contentInsets.bottom);
        int translateAdjustment = 0;
        if (fromWidth > toWidth || fromHeight > toHeight) {
            set.addAnimation(new ScaleAnimation(fromWidth / toWidth, 1.0f, fromHeight / toHeight, 1.0f));
            translateAdjustment = (int) ((((float) contentInsets.top) * fromHeight) / toHeight);
        } else {
            set.addAnimation(new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect));
        }
        set.addAnimation(new TranslateAnimation((float) (left - containingFrame.left), 0.0f, (float) ((top - containingFrame.top) - translateAdjustment), 0.0f));
        set.setDuration(336);
        set.setZAdjustment(1);
        return set;
    }

    /* access modifiers changed from: package-private */
    public boolean canSkipFirstFrame() {
        int i = this.mNextAppTransitionType;
        return (i == 1 || i == 7 || i == 8 || this.mNextAppTransition == 20) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public RemoteAnimationController getRemoteAnimationController() {
        return this.mRemoteAnimationController;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0239  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.animation.Animation loadAnimation(android.view.WindowManager.LayoutParams r18, int r19, boolean r20, int r21, int r22, android.graphics.Rect r23, android.graphics.Rect r24, android.graphics.Rect r25, android.graphics.Rect r26, android.graphics.Rect r27, boolean r28, boolean r29, int r30) {
        /*
            r17 = this;
            r11 = r17
            r12 = r18
            r13 = r19
            r14 = r20
            r15 = r23
            com.android.server.wm.WindowManagerService r0 = r11.mService
            boolean r0 = r0.isAppTransitionSkipped()
            r1 = 0
            if (r0 == 0) goto L_0x0014
            return r1
        L_0x0014:
            r11.prepareMiuiAppTransitionIfNeeded(r13)
            r16 = 0
            r0 = 1
            if (r29 == 0) goto L_0x0032
            com.android.server.wm.WindowManagerService r1 = r11.mService
            int r1 = r1.getCurrentFreeFormWindowMode()
            if (r1 == r0) goto L_0x002e
            com.android.server.wm.WindowManagerService r0 = r11.mService
            android.view.animation.Animation r16 = com.android.server.wm.AppTransitionInjector.loadFreeFormAnimation(r0, r13, r14, r15)
            r1 = r30
            goto L_0x0282
        L_0x002e:
            r1 = r30
            goto L_0x0282
        L_0x0032:
            boolean r2 = isKeyguardGoingAwayTransit(r19)
            if (r2 == 0) goto L_0x0042
            if (r14 == 0) goto L_0x0042
            android.view.animation.Animation r16 = r11.loadKeyguardExitAnimation(r13)
            r1 = r30
            goto L_0x0282
        L_0x0042:
            r2 = 22
            if (r13 != r2) goto L_0x004c
            r16 = 0
            r1 = r30
            goto L_0x0282
        L_0x004c:
            r3 = 23
            if (r13 != r3) goto L_0x005d
            if (r14 != 0) goto L_0x005d
            r0 = 17432917(0x10a0155, float:2.5347553E-38)
            android.view.animation.Animation r16 = r11.loadAnimationRes((android.view.WindowManager.LayoutParams) r12, (int) r0)
            r1 = r30
            goto L_0x0282
        L_0x005d:
            r4 = 26
            if (r13 != r4) goto L_0x0067
            r16 = 0
            r1 = r30
            goto L_0x0282
        L_0x0067:
            r4 = 10
            r5 = 8
            r6 = 6
            if (r28 == 0) goto L_0x0085
            if (r13 == r6) goto L_0x0074
            if (r13 == r5) goto L_0x0074
            if (r13 != r4) goto L_0x0085
        L_0x0074:
            if (r14 == 0) goto L_0x007a
            r0 = 17432904(0x10a0148, float:2.5347516E-38)
            goto L_0x007d
        L_0x007a:
            r0 = 17432905(0x10a0149, float:2.534752E-38)
        L_0x007d:
            android.view.animation.Animation r16 = r11.loadAnimationRes((android.view.WindowManager.LayoutParams) r12, (int) r0)
            r1 = r30
            goto L_0x0282
        L_0x0085:
            r7 = 11
            r8 = 9
            r9 = 7
            if (r28 == 0) goto L_0x00a3
            if (r13 == r9) goto L_0x0092
            if (r13 == r8) goto L_0x0092
            if (r13 != r7) goto L_0x00a3
        L_0x0092:
            if (r14 == 0) goto L_0x0098
            r0 = 17432902(0x10a0146, float:2.534751E-38)
            goto L_0x009b
        L_0x0098:
            r0 = 17432903(0x10a0147, float:2.5347513E-38)
        L_0x009b:
            android.view.animation.Animation r16 = r11.loadAnimationRes((android.view.WindowManager.LayoutParams) r12, (int) r0)
            r1 = r30
            goto L_0x0282
        L_0x00a3:
            r10 = 18
            if (r13 != r10) goto L_0x00b1
            r0 = r25
            android.view.animation.Animation r16 = r11.createRelaunchAnimation(r15, r0)
            r1 = r30
            goto L_0x0282
        L_0x00b1:
            int r1 = r11.mNextAppTransitionType
            if (r1 != r0) goto L_0x00c6
            java.lang.String r0 = r11.mNextAppTransitionPackage
            if (r14 == 0) goto L_0x00bc
            int r1 = r11.mNextAppTransitionEnter
            goto L_0x00be
        L_0x00bc:
            int r1 = r11.mNextAppTransitionExit
        L_0x00be:
            android.view.animation.Animation r16 = r11.loadAnimationRes((java.lang.String) r0, (int) r1)
            r1 = r30
            goto L_0x0282
        L_0x00c6:
            if (r1 != r9) goto L_0x00d4
            java.lang.String r0 = r11.mNextAppTransitionPackage
            int r1 = r11.mNextAppTransitionInPlace
            android.view.animation.Animation r16 = r11.loadAnimationRes((java.lang.String) r0, (int) r1)
            r1 = r30
            goto L_0x0282
        L_0x00d4:
            if (r1 != r5) goto L_0x00e0
            r0 = r24
            android.view.animation.Animation r16 = r11.createClipRevealAnimationLocked(r13, r14, r15, r0)
            r1 = r30
            goto L_0x0282
        L_0x00e0:
            r0 = 2
            if (r1 != r0) goto L_0x00eb
            android.view.animation.Animation r16 = r11.createScaleUpAnimationLocked(r13, r14, r15)
            r1 = r30
            goto L_0x0282
        L_0x00eb:
            r0 = 3
            if (r1 == r0) goto L_0x026f
            r2 = 4
            if (r1 != r2) goto L_0x00f3
            goto L_0x026f
        L_0x00f3:
            r2 = 5
            if (r1 == r2) goto L_0x0246
            if (r1 != r6) goto L_0x00fa
            goto L_0x0246
        L_0x00fa:
            if (r1 != r8) goto L_0x013b
            if (r14 == 0) goto L_0x013b
            r1 = 17432894(0x10a013e, float:2.5347488E-38)
            java.lang.String r2 = "android"
            android.view.animation.Animation r1 = r11.loadAnimationRes((java.lang.String) r2, (int) r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "applyAnimation NEXT_TRANSIT_TYPE_OPEN_CROSS_PROFILE_APPS: anim="
            r2.append(r3)
            r2.append(r1)
            java.lang.String r3 = " transit="
            r2.append(r3)
            java.lang.String r3 = appTransitionToString(r19)
            r2.append(r3)
            java.lang.String r3 = " isEntrance=true Callers="
            r2.append(r3)
            java.lang.String r0 = android.os.Debug.getCallers(r0)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.String r2 = "WindowManager"
            android.util.Slog.v(r2, r0)
            r16 = r1
            r1 = r30
            goto L_0x0282
        L_0x013b:
            r0 = 27
            if (r13 != r0) goto L_0x0153
            android.view.animation.AlphaAnimation r0 = new android.view.animation.AlphaAnimation
            r1 = 1065353216(0x3f800000, float:1.0)
            r2 = 1065353216(0x3f800000, float:1.0)
            r0.<init>(r1, r2)
            r1 = 336(0x150, double:1.66E-321)
            r0.setDuration(r1)
            r1 = r30
            r16 = r0
            goto L_0x0282
        L_0x0153:
            int r0 = r11.mNextAppTransitionType
            r1 = 101(0x65, float:1.42E-43)
            if (r0 != r1) goto L_0x0168
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            r11.getDefaultNextAppTransitionStartRect(r0)
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            android.view.animation.Animation r16 = com.android.server.wm.AppTransitionInjector.createLaunchAppFromHomeAnimation(r13, r14, r15, r0)
            r1 = r30
            goto L_0x0282
        L_0x0168:
            r1 = 102(0x66, float:1.43E-43)
            if (r0 != r1) goto L_0x0176
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            android.view.animation.Animation r16 = com.android.server.wm.AppTransitionInjector.createWallPaperOpenAnimation(r14, r15, r0)
            r1 = r30
            goto L_0x0282
        L_0x0176:
            r1 = 103(0x67, float:1.44E-43)
            if (r0 != r1) goto L_0x0199
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            r11.getDefaultNextAppTransitionStartRect(r0)
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            int r1 = r11.mRadius
            float r1 = (float) r1
            android.view.animation.Animation r0 = com.android.server.wm.AppTransitionInjector.createLaunchActivityFromRoundedViewAnimation(r13, r14, r15, r0, r1)
            if (r14 == 0) goto L_0x0193
            android.os.Handler r1 = r11.mHandler
            android.os.IRemoteCallback r2 = r11.mAnimationExitStartCallback
            android.os.IRemoteCallback r3 = r11.mAnimationExitFinishCallback
            com.android.server.wm.AppTransitionInjector.addAnimationListener(r0, r1, r2, r3)
        L_0x0193:
            r1 = r30
            r16 = r0
            goto L_0x0282
        L_0x0199:
            r1 = 104(0x68, float:1.46E-43)
            if (r0 != r1) goto L_0x01bb
            boolean r0 = r11.mScaleBackToScreenCenter
            if (r0 == 0) goto L_0x01a6
            android.view.animation.Animation r0 = com.android.server.wm.AppTransitionInjector.createBackActivityScaledToScreenCenter(r14, r15)
            goto L_0x01af
        L_0x01a6:
            android.graphics.Rect r0 = r11.mLauncherAnimationRect
            int r1 = r11.mRadius
            float r1 = (float) r1
            android.view.animation.Animation r0 = com.android.server.wm.AppTransitionInjector.createBackActivityFromRoundedViewAnimation(r14, r15, r0, r1)
        L_0x01af:
            if (r14 != 0) goto L_0x0193
            android.os.Handler r1 = r11.mHandler
            android.os.IRemoteCallback r2 = r11.mAnimationReenterStartedCallback
            android.os.IRemoteCallback r3 = r11.mAnimationReenterFinishedCallback
            com.android.server.wm.AppTransitionInjector.addAnimationListener(r0, r1, r2, r3)
            goto L_0x0193
        L_0x01bb:
            r0 = 0
            r1 = 19
            if (r13 == r1) goto L_0x021e
            r1 = 24
            if (r13 == r1) goto L_0x0217
            r1 = 25
            if (r13 == r1) goto L_0x0211
            switch(r13) {
                case 6: goto L_0x0217;
                case 7: goto L_0x0211;
                case 8: goto L_0x021e;
                case 9: goto L_0x020b;
                case 10: goto L_0x0202;
                case 11: goto L_0x01f9;
                case 12: goto L_0x01f2;
                case 13: goto L_0x01e9;
                case 14: goto L_0x01e0;
                case 15: goto L_0x01d7;
                case 16: goto L_0x01cd;
                default: goto L_0x01cb;
            }
        L_0x01cb:
            goto L_0x0224
        L_0x01cd:
            if (r14 == 0) goto L_0x01d2
            r1 = 25
            goto L_0x01d4
        L_0x01d2:
            r1 = 24
        L_0x01d4:
            r0 = r1
            goto L_0x0224
        L_0x01d7:
            if (r14 == 0) goto L_0x01dc
            r3 = 22
            goto L_0x01dd
        L_0x01dc:
        L_0x01dd:
            r0 = r3
            goto L_0x0224
        L_0x01e0:
            if (r14 == 0) goto L_0x01e5
            r1 = 20
            goto L_0x01e7
        L_0x01e5:
            r1 = 21
        L_0x01e7:
            r0 = r1
            goto L_0x0224
        L_0x01e9:
            if (r14 == 0) goto L_0x01ee
            r1 = 16
            goto L_0x01f0
        L_0x01ee:
            r1 = 17
        L_0x01f0:
            r0 = r1
            goto L_0x0224
        L_0x01f2:
            if (r14 == 0) goto L_0x01f5
            goto L_0x01f7
        L_0x01f5:
            r10 = 19
        L_0x01f7:
            r0 = r10
            goto L_0x0224
        L_0x01f9:
            if (r14 == 0) goto L_0x01fe
            r1 = 14
            goto L_0x0200
        L_0x01fe:
            r1 = 15
        L_0x0200:
            r0 = r1
            goto L_0x0224
        L_0x0202:
            if (r14 == 0) goto L_0x0207
            r1 = 12
            goto L_0x0209
        L_0x0207:
            r1 = 13
        L_0x0209:
            r0 = r1
            goto L_0x0224
        L_0x020b:
            if (r14 == 0) goto L_0x020e
            goto L_0x020f
        L_0x020e:
            r4 = r7
        L_0x020f:
            r0 = r4
            goto L_0x0224
        L_0x0211:
            if (r14 == 0) goto L_0x0214
            goto L_0x0215
        L_0x0214:
            r6 = r9
        L_0x0215:
            r0 = r6
            goto L_0x0224
        L_0x0217:
            if (r14 == 0) goto L_0x021b
            r2 = 4
            goto L_0x021c
        L_0x021b:
        L_0x021c:
            r0 = r2
            goto L_0x0224
        L_0x021e:
            if (r14 == 0) goto L_0x0221
            goto L_0x0222
        L_0x0221:
            r5 = r8
        L_0x0222:
            r0 = r5
        L_0x0224:
            android.view.animation.Animation r1 = com.android.server.wm.AppTransitionInjector.loadDefaultAnimationNotCheck(r12, r13, r14, r15)
            int r2 = r11.getResIdAttr(r12, r0, r13)
            if (r1 == 0) goto L_0x0239
            boolean r3 = com.android.server.wm.AppTransitionInjector.useDefaultAnimationAttr(r12, r2)
            if (r3 == 0) goto L_0x0239
            r16 = r1
            r1 = r30
            goto L_0x0282
        L_0x0239:
            if (r0 == 0) goto L_0x0240
            android.view.animation.Animation r3 = r11.loadAnimationAttr(r12, r0, r13)
            goto L_0x0241
        L_0x0240:
            r3 = 0
        L_0x0241:
            r16 = r3
            r1 = r30
            goto L_0x0282
        L_0x0246:
            int r0 = r11.mNextAppTransitionType
            if (r0 != r2) goto L_0x024c
            r0 = 1
            goto L_0x024d
        L_0x024c:
            r0 = 0
        L_0x024d:
            r11.mNextAppTransitionScaleUp = r0
            int r1 = r11.getThumbnailTransitionState(r14)
            r0 = r17
            r2 = r21
            r3 = r22
            r4 = r19
            r5 = r23
            r6 = r25
            r7 = r26
            r8 = r27
            r9 = r29
            r10 = r30
            android.view.animation.Animation r16 = r0.createAspectScaledThumbnailEnterExitAnimationLocked(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = r30
            goto L_0x0282
        L_0x026f:
            int r1 = r11.mNextAppTransitionType
            if (r1 != r0) goto L_0x0275
            r0 = 1
            goto L_0x0276
        L_0x0275:
            r0 = 0
        L_0x0276:
            r11.mNextAppTransitionScaleUp = r0
            int r0 = r11.getThumbnailTransitionState(r14)
            r1 = r30
            android.view.animation.Animation r16 = r11.createThumbnailEnterExitAnimationLocked(r0, r15, r13, r1)
        L_0x0282:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransition.loadAnimation(android.view.WindowManager$LayoutParams, int, boolean, int, int, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, boolean, boolean, int):android.view.animation.Animation");
    }

    private Animation loadKeyguardExitAnimation(int transit) {
        int i = this.mNextAppTransitionFlags;
        if ((i & 2) != 0) {
            return null;
        }
        boolean z = true;
        boolean toShade = (i & 1) != 0;
        WindowManagerPolicy windowManagerPolicy = this.mService.mPolicy;
        if (transit != 21) {
            z = false;
        }
        return windowManagerPolicy.createHiddenByKeyguardExitForAppWindow(z, toShade);
    }

    /* access modifiers changed from: package-private */
    public int getAppStackClipMode() {
        int i = this.mNextAppTransition;
        if (i == 20 || i == 21) {
            return 1;
        }
        if (i == 18 || i == 19 || this.mNextAppTransitionType == 8) {
            return 2;
        }
        return 0;
    }

    public int getTransitFlags() {
        return this.mNextAppTransitionFlags;
    }

    /* access modifiers changed from: package-private */
    public void postAnimationCallback() {
        IRemoteCallback iRemoteCallback = this.mNextAppTransitionCallback;
        if (iRemoteCallback != null) {
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppTransition$B95jxKE2FnT5RNLStTafenhEYj4.INSTANCE, iRemoteCallback));
            this.mNextAppTransitionCallback = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransition(String packageName, int enterAnim, int exitAnim, IRemoteCallback startedCallback) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 1;
            this.mNextAppTransitionPackage = packageName;
            this.mNextAppTransitionEnter = enterAnim;
            this.mNextAppTransitionExit = exitAnim;
            postAnimationCallback();
            this.mNextAppTransitionCallback = startedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionScaleUp(int startX, int startY, int startWidth, int startHeight) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 2;
            putDefaultNextAppTransitionCoordinates(startX, startY, startWidth, startHeight, (GraphicBuffer) null);
            postAnimationCallback();
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionClipReveal(int startX, int startY, int startWidth, int startHeight) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 8;
            putDefaultNextAppTransitionCoordinates(startX, startY, startWidth, startHeight, (GraphicBuffer) null);
            postAnimationCallback();
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionLaunchFromHome(int startX, int startY, int startWidth, int startHeight) {
        if (isTransitionSet()) {
            clear();
            this.mNextAppTransitionType = 101;
            putDefaultNextAppTransitionCoordinates(startX, startY, startWidth, startHeight, (GraphicBuffer) null);
            postAnimationCallback();
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingActivityTransitionFromRoundedView(int startX, int startY, int startWidth, int startHeight, int radius, int foreGroundColor, GraphicBuffer buffer, IRemoteCallback startedCallback, IRemoteCallback finishedCallback) {
        if (isTransitionSet()) {
            clear();
            this.mNextAppTransitionType = 103;
            putDefaultNextAppTransitionCoordinates(startX, startY, startWidth, startHeight, buffer);
            postAnimationCallback();
            this.mRadius = radius;
            this.mForeGroundColor = foreGroundColor;
            this.mAnimationExitStartCallback = startedCallback;
            this.mAnimationExitFinishCallback = finishedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public int getRadius() {
        return this.mRadius;
    }

    /* access modifiers changed from: package-private */
    public int getForeGroundColor() {
        return this.mForeGroundColor;
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionThumb(GraphicBuffer srcThumb, int startX, int startY, IRemoteCallback startedCallback, boolean scaleUp) {
        int i;
        if (canOverridePendingAppTransition()) {
            clear();
            if (scaleUp) {
                i = 3;
            } else {
                i = 4;
            }
            this.mNextAppTransitionType = i;
            this.mNextAppTransitionScaleUp = scaleUp;
            putDefaultNextAppTransitionCoordinates(startX, startY, 0, 0, srcThumb);
            postAnimationCallback();
            this.mNextAppTransitionCallback = startedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionAspectScaledThumb(GraphicBuffer srcThumb, int startX, int startY, int targetWidth, int targetHeight, IRemoteCallback startedCallback, boolean scaleUp) {
        int i;
        if (canOverridePendingAppTransition()) {
            clear();
            if (scaleUp) {
                i = 5;
            } else {
                i = 6;
            }
            this.mNextAppTransitionType = i;
            this.mNextAppTransitionScaleUp = scaleUp;
            putDefaultNextAppTransitionCoordinates(startX, startY, targetWidth, targetHeight, srcThumb);
            postAnimationCallback();
            this.mNextAppTransitionCallback = startedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionMultiThumb(AppTransitionAnimationSpec[] specs, IRemoteCallback onAnimationStartedCallback, IRemoteCallback onAnimationFinishedCallback, boolean scaleUp) {
        int i;
        if (canOverridePendingAppTransition()) {
            clear();
            if (scaleUp) {
                i = 5;
            } else {
                i = 6;
            }
            this.mNextAppTransitionType = i;
            this.mNextAppTransitionScaleUp = scaleUp;
            if (specs != null) {
                for (int i2 = 0; i2 < specs.length; i2++) {
                    AppTransitionAnimationSpec spec = specs[i2];
                    if (spec != null) {
                        this.mNextAppTransitionAnimationsSpecs.put(spec.taskId, spec);
                        if (i2 == 0) {
                            Rect rect = spec.rect;
                            putDefaultNextAppTransitionCoordinates(rect.left, rect.top, rect.width(), rect.height(), spec.buffer);
                        }
                    }
                }
            }
            postAnimationCallback();
            this.mNextAppTransitionCallback = onAnimationStartedCallback;
            this.mAnimationFinishedCallback = onAnimationFinishedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture specsFuture, IRemoteCallback callback, boolean scaleUp) {
        int i;
        if (canOverridePendingAppTransition()) {
            clear();
            if (scaleUp) {
                i = 5;
            } else {
                i = 6;
            }
            this.mNextAppTransitionType = i;
            this.mNextAppTransitionAnimationsSpecsFuture = specsFuture;
            this.mNextAppTransitionScaleUp = scaleUp;
            this.mNextAppTransitionFutureCallback = callback;
            if (isReady()) {
                fetchAppTransitionSpecsFromFuture();
            }
            if (this.mService.mPendingExecuteAppTransition) {
                WindowManagerService windowManagerService = this.mService;
                windowManagerService.mPendingExecuteAppTransition = false;
                windowManagerService.executeAppTransition();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter) {
        if (isTransitionSet()) {
            clear();
            this.mNextAppTransitionType = 10;
            this.mRemoteAnimationController = new RemoteAnimationController(this.mService, remoteAnimationAdapter, this.mHandler);
        }
    }

    /* access modifiers changed from: package-private */
    public void overrideInPlaceAppTransition(String packageName, int anim) {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 7;
            this.mNextAppTransitionPackage = packageName;
            this.mNextAppTransitionInPlace = anim;
        }
    }

    /* access modifiers changed from: package-private */
    public void overridePendingAppTransitionStartCrossProfileApps() {
        if (canOverridePendingAppTransition()) {
            clear();
            this.mNextAppTransitionType = 9;
            postAnimationCallback();
        }
    }

    private boolean canOverridePendingAppTransition() {
        return isTransitionSet() && this.mNextAppTransitionType != 10;
    }

    private void fetchAppTransitionSpecsFromFuture() {
        if (this.mNextAppTransitionAnimationsSpecsFuture != null) {
            this.mService.startFetchingAppTransitionSpecs(this.mDisplayContent.getDisplayId());
            this.mNextAppTransitionAnimationsSpecsPending = true;
            IAppTransitionAnimationSpecsFuture future = this.mNextAppTransitionAnimationsSpecsFuture;
            this.mNextAppTransitionAnimationsSpecsFuture = null;
            this.mDefaultExecutor.execute(new Runnable(future) {
                private final /* synthetic */ IAppTransitionAnimationSpecsFuture f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AppTransition.this.lambda$fetchAppTransitionSpecsFromFuture$1$AppTransition(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$fetchAppTransitionSpecsFromFuture$1$AppTransition(IAppTransitionAnimationSpecsFuture future) {
        AppTransitionAnimationSpec[] specs = null;
        try {
            Binder.allowBlocking(future.asBinder());
            specs = future.get();
        } catch (RemoteException e) {
            Slog.w("WindowManager", "Failed to fetch app transition specs: " + e);
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                overridePendingAppTransitionMultiThumb(specs, this.mNextAppTransitionFutureCallback, (IRemoteCallback) null, this.mNextAppTransitionScaleUp);
                this.mNextAppTransitionFutureCallback = null;
                this.mService.finishFetchingAppTransitionSpecs(this.mDisplayContent.getDisplayId());
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public String toString() {
        return "mNextAppTransition=" + appTransitionToString(this.mNextAppTransition);
    }

    public static String appTransitionToString(int transition) {
        if (transition == -1) {
            return "TRANSIT_UNSET";
        }
        if (transition == 0) {
            return "TRANSIT_NONE";
        }
        switch (transition) {
            case 6:
                return "TRANSIT_ACTIVITY_OPEN";
            case 7:
                return "TRANSIT_ACTIVITY_CLOSE";
            case 8:
                return "TRANSIT_TASK_OPEN";
            case 9:
                return "TRANSIT_TASK_CLOSE";
            case 10:
                return "TRANSIT_TASK_TO_FRONT";
            case 11:
                return "TRANSIT_TASK_TO_BACK";
            case 12:
                return "TRANSIT_WALLPAPER_CLOSE";
            case 13:
                return "TRANSIT_WALLPAPER_OPEN";
            case 14:
                return "TRANSIT_WALLPAPER_INTRA_OPEN";
            case 15:
                return "TRANSIT_WALLPAPER_INTRA_CLOSE";
            case 16:
                return "TRANSIT_TASK_OPEN_BEHIND";
            default:
                switch (transition) {
                    case 18:
                        return "TRANSIT_ACTIVITY_RELAUNCH";
                    case 19:
                        return "TRANSIT_DOCK_TASK_FROM_RECENTS";
                    case 20:
                        return "TRANSIT_KEYGUARD_GOING_AWAY";
                    case 21:
                        return "TRANSIT_KEYGUARD_GOING_AWAY_ON_WALLPAPER";
                    case 22:
                        return "TRANSIT_KEYGUARD_OCCLUDE";
                    case WindowManagerService.H.BOOT_TIMEOUT /*23*/:
                        return "TRANSIT_KEYGUARD_UNOCCLUDE";
                    case WindowManagerService.H.WAITING_FOR_DRAWN_TIMEOUT /*24*/:
                        return "TRANSIT_TRANSLUCENT_ACTIVITY_OPEN";
                    case WindowManagerService.H.SHOW_STRICT_MODE_VIOLATION /*25*/:
                        return "TRANSIT_TRANSLUCENT_ACTIVITY_CLOSE";
                    case 26:
                        return "TRANSIT_CRASHING_ACTIVITY_CLOSE";
                    default:
                        return "<UNKNOWN: " + transition + ">";
                }
        }
    }

    private String appStateToString() {
        int i = this.mAppTransitionState;
        if (i == 0) {
            return "APP_STATE_IDLE";
        }
        if (i == 1) {
            return "APP_STATE_READY";
        }
        if (i == 2) {
            return "APP_STATE_RUNNING";
        }
        if (i == 3) {
            return "APP_STATE_TIMEOUT";
        }
        return "unknown state=" + this.mAppTransitionState;
    }

    private String transitTypeToString() {
        switch (this.mNextAppTransitionType) {
            case 0:
                return "NEXT_TRANSIT_TYPE_NONE";
            case 1:
                return "NEXT_TRANSIT_TYPE_CUSTOM";
            case 2:
                return "NEXT_TRANSIT_TYPE_SCALE_UP";
            case 3:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP";
            case 4:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN";
            case 5:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_UP";
            case 6:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_ASPECT_SCALE_DOWN";
            case 7:
                return "NEXT_TRANSIT_TYPE_CUSTOM_IN_PLACE";
            case 9:
                return "NEXT_TRANSIT_TYPE_OPEN_CROSS_PROFILE_APPS";
            default:
                return "unknown type=" + this.mNextAppTransitionType;
        }
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1159641169921L, this.mAppTransitionState);
        proto.write(1159641169922L, this.mLastUsedAppTransition);
        proto.end(token);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.println(this);
        pw.print(prefix);
        pw.print("mAppTransitionState=");
        pw.println(appStateToString());
        if (this.mNextAppTransitionType != 0) {
            pw.print(prefix);
            pw.print("mNextAppTransitionType=");
            pw.println(transitTypeToString());
        }
        switch (this.mNextAppTransitionType) {
            case 1:
                pw.print(prefix);
                pw.print("mNextAppTransitionPackage=");
                pw.println(this.mNextAppTransitionPackage);
                pw.print(prefix);
                pw.print("mNextAppTransitionEnter=0x");
                pw.print(Integer.toHexString(this.mNextAppTransitionEnter));
                pw.print(" mNextAppTransitionExit=0x");
                pw.println(Integer.toHexString(this.mNextAppTransitionExit));
                break;
            case 2:
                getDefaultNextAppTransitionStartRect(this.mTmpRect);
                pw.print(prefix);
                pw.print("mNextAppTransitionStartX=");
                pw.print(this.mTmpRect.left);
                pw.print(" mNextAppTransitionStartY=");
                pw.println(this.mTmpRect.top);
                pw.print(prefix);
                pw.print("mNextAppTransitionStartWidth=");
                pw.print(this.mTmpRect.width());
                pw.print(" mNextAppTransitionStartHeight=");
                pw.println(this.mTmpRect.height());
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                pw.print(prefix);
                pw.print("mDefaultNextAppTransitionAnimationSpec=");
                pw.println(this.mDefaultNextAppTransitionAnimationSpec);
                pw.print(prefix);
                pw.print("mNextAppTransitionAnimationsSpecs=");
                pw.println(this.mNextAppTransitionAnimationsSpecs);
                pw.print(prefix);
                pw.print("mNextAppTransitionScaleUp=");
                pw.println(this.mNextAppTransitionScaleUp);
                break;
            case 7:
                pw.print(prefix);
                pw.print("mNextAppTransitionPackage=");
                pw.println(this.mNextAppTransitionPackage);
                pw.print(prefix);
                pw.print("mNextAppTransitionInPlace=0x");
                pw.print(Integer.toHexString(this.mNextAppTransitionInPlace));
                break;
        }
        if (this.mNextAppTransitionCallback != null) {
            pw.print(prefix);
            pw.print("mNextAppTransitionCallback=");
            pw.println(this.mNextAppTransitionCallback);
        }
        if (this.mLastUsedAppTransition != 0) {
            pw.print(prefix);
            pw.print("mLastUsedAppTransition=");
            pw.println(appTransitionToString(this.mLastUsedAppTransition));
            pw.print(prefix);
            pw.print("mLastOpeningApp=");
            pw.println(this.mLastOpeningApp);
            pw.print(prefix);
            pw.print("mLastClosingApp=");
            pw.println(this.mLastClosingApp);
            pw.print(prefix);
            pw.print("mLastChangingApp=");
            pw.println(this.mLastChangingApp);
        }
    }

    public void setCurrentUser(int newUserId) {
        this.mCurrentUserId = newUserId;
    }

    /* access modifiers changed from: package-private */
    public boolean prepareAppTransitionLocked(int transit, boolean alwaysKeepCurrent, int flags, boolean forceOverride) {
        int i;
        boolean allowSetCrashing = !isKeyguardTransit(this.mNextAppTransition) && transit == 26;
        if (forceOverride || isKeyguardTransit(transit) || !isTransitionSet() || (i = this.mNextAppTransition) == 0 || allowSetCrashing) {
            setAppTransition(transit, flags);
        } else if (!alwaysKeepCurrent && !isKeyguardTransit(i) && this.mNextAppTransition != 26) {
            if (transit == 8 && isTransitionEqual(9)) {
                setAppTransition(transit, flags);
            } else if (transit == 6 && isTransitionEqual(7)) {
                setAppTransition(transit, flags);
            } else if (isTaskTransit(transit) && isActivityTransit(this.mNextAppTransition)) {
                setAppTransition(transit, flags);
            }
        }
        boolean prepared = prepare();
        if (isTransitionSet()) {
            removeAppTransitionTimeoutCallbacks();
            this.mHandler.postDelayed(this.mHandleAppTransitionTimeoutRunnable, APP_TRANSITION_TIMEOUT_MS);
        }
        return prepared;
    }

    public static boolean isKeyguardGoingAwayTransit(int transit) {
        return transit == 20 || transit == 21;
    }

    private static boolean isKeyguardTransit(int transit) {
        return isKeyguardGoingAwayTransit(transit) || transit == 22 || transit == 23;
    }

    static boolean isTaskTransit(int transit) {
        return isTaskOpenTransit(transit) || transit == 9 || transit == 11 || transit == 17;
    }

    private static boolean isTaskOpenTransit(int transit) {
        return transit == 8 || transit == 16 || transit == 10;
    }

    static boolean isActivityTransit(int transit) {
        return transit == 6 || transit == 7 || transit == 18;
    }

    static boolean isChangeTransit(int transit) {
        return transit == 27;
    }

    private boolean shouldScaleDownThumbnailTransition(int uiMode, int orientation) {
        return this.mGridLayoutRecentsEnabled || orientation == 1;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003d, code lost:
        return;
     */
    /* renamed from: handleAppTransitionTimeout */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$new$0$AppTransition() {
        /*
            r3 = this;
            com.android.server.wm.WindowManagerService r0 = r3.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x003e }
            com.android.server.wm.DisplayContent r1 = r3.mDisplayContent     // Catch:{ all -> 0x003e }
            if (r1 != 0) goto L_0x0011
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0011:
            boolean r2 = r3.isTransitionSet()     // Catch:{ all -> 0x003e }
            if (r2 != 0) goto L_0x002f
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r2 = r1.mOpeningApps     // Catch:{ all -> 0x003e }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x003e }
            if (r2 == 0) goto L_0x002f
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r2 = r1.mClosingApps     // Catch:{ all -> 0x003e }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x003e }
            if (r2 == 0) goto L_0x002f
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r2 = r1.mChangingApps     // Catch:{ all -> 0x003e }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x003e }
            if (r2 != 0) goto L_0x0039
        L_0x002f:
            r3.setTimeout()     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService r2 = r3.mService     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowSurfacePlacer r2 = r2.mWindowPlacerLocked     // Catch:{ all -> 0x003e }
            r2.performSurfacePlacement()     // Catch:{ all -> 0x003e }
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x003e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransition.lambda$new$0$AppTransition():void");
    }

    /* access modifiers changed from: private */
    public static void doAnimationCallback(IRemoteCallback callback) {
        try {
            callback.sendResult((Bundle) null);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void removeAppTransitionTimeoutCallbacks() {
        this.mHandler.removeCallbacks(this.mHandleAppTransitionTimeoutRunnable);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldAppTransitionRoundCorner(int transit) {
        int i;
        if (isTaskTransit(transit) || (i = this.mNextAppTransitionType) == 101 || i == 102) {
            return true;
        }
        return !(i == 1 || i == 7 || !this.mDefaultActivityAnimation) || transit == 12 || transit == 13 || this.mScaleBackToScreenCenter;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldActivityTransitionRoundCorner() {
        int i = this.mNextAppTransitionType;
        return i == 103 || (i == 104 && !this.mNextAppTransitionScaleUp);
    }

    /* access modifiers changed from: package-private */
    public void overrideMiuiAnimationInfo(GraphicBuffer icon, Rect rect) {
        if (this.mLoadBackHomeAnimation) {
            if (icon != null) {
                putDefaultNextAppTransitionCoordinates(0, 0, 0, 0, icon);
            }
            if (rect != null) {
                this.mLauncherAnimationRect.set(rect);
            } else {
                this.mLauncherAnimationRect.setEmpty();
            }
            this.mService.finishFetchingAppTransitionSpecs(this.mDisplayContent.getDisplayId());
        }
    }

    /* access modifiers changed from: package-private */
    public void overrideMiuiRoundedViewAnimationInfo(GraphicBuffer icon, Rect rect, int radius, int foreGroundColor, IRemoteCallback animationReenterStartedCallback, IRemoteCallback animationReenterFinishedCallback) {
        if (this.mLoadRoundedViewAnimation) {
            if (icon != null) {
                putDefaultNextAppTransitionCoordinates(0, 0, 0, 0, icon);
            }
            this.mRadius = radius;
            this.mForeGroundColor = foreGroundColor;
            if (rect != null) {
                this.mLauncherAnimationRect.set(rect);
            } else {
                this.mLauncherAnimationRect.setEmpty();
            }
            this.mService.finishFetchingAppTransitionSpecs(this.mDisplayContent.getDisplayId());
            this.mAnimationReenterStartedCallback = animationReenterStartedCallback;
            this.mAnimationReenterFinishedCallback = animationReenterFinishedCallback;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean needCreateMiuiThumbnail() {
        if (this.mNextAppTransitionType != 102) {
            return false;
        }
        if (this.mLauncherAnimationRect.isEmpty()) {
            AppTransitionInjector.notifyMiuiAnimationEnd(this.mService.mMiuiAppTransitionAnimationHelper);
            this.mNextAppTransitionType = 0;
        }
        if (this.mNextAppTransitionType == 102) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean needCreateMiuiActivityThumbnail(int type) {
        if (this.mScaleBackToScreenCenter || this.mNextAppTransitionType != type) {
            return false;
        }
        if (!this.mLauncherAnimationRect.isEmpty()) {
            return true;
        }
        this.mNextAppTransitionType = 0;
        return false;
    }

    /* access modifiers changed from: package-private */
    public void clearNextAppTransitionBackHomeType() {
        if (this.mNextAppTransitionType == 102) {
            this.mNextAppTransitionType = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateAllowCustomAnimationIfNeeded(ArraySet<AppWindowToken> closingApps) {
        this.mAllowCustomAnimation = false;
        if (this.mNextAppTransitionType == 1) {
            this.mAllowCustomAnimation = AppTransitionInjector.allowCustomAnimation(closingApps);
        }
    }

    /* access modifiers changed from: package-private */
    public void setLoadBackHomeAnimation(boolean loadBackHomeAnimation) {
        this.mLoadBackHomeAnimation = loadBackHomeAnimation;
    }

    /* access modifiers changed from: package-private */
    public void setLoadRoundedViewAnimation(boolean loadRoundedViewAnimation, boolean scaleBackToScreenCenter) {
        this.mLoadRoundedViewAnimation = loadRoundedViewAnimation;
        this.mScaleBackToScreenCenter = scaleBackToScreenCenter;
    }

    private void prepareMiuiAppTransitionIfNeeded(int transit) {
        if (this.mService.mIsInMultiWindowMode && this.mNextAppTransitionType == 101) {
            this.mNextAppTransitionType = 8;
        }
        if (this.mLoadBackHomeAnimation && (this.mService.mIsInMultiWindowMode || transit != 13)) {
            AppTransitionInjector.notifyMiuiAnimationEnd(this.mService.mMiuiAppTransitionAnimationHelper);
            this.mLoadBackHomeAnimation = false;
        }
        if (this.mLoadBackHomeAnimation && (this.mNextAppTransitionType != 1 || !this.mAllowCustomAnimation)) {
            this.mNextAppTransitionType = 102;
        }
        if (this.mLoadRoundedViewAnimation && (this.mService.mIsInMultiWindowMode || transit != 7)) {
            AppTransitionInjector.notifyMiuiAnimationEnd(this.mService.mMiuiAppTransitionAnimationHelper);
            this.mLoadRoundedViewAnimation = false;
        }
        if (this.mLoadRoundedViewAnimation && !this.mAllowCustomAnimation) {
            this.mNextAppTransitionType = HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION;
        }
    }
}
