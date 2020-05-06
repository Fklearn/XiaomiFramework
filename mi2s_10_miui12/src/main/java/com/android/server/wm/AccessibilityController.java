package com.android.server.wm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.ViewConfiguration;
import android.view.WindowInfo;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.os.SomeArgs;
import com.android.server.pm.DumpState;
import com.android.server.wm.AccessibilityController;
import com.android.server.wm.WindowManagerInternal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

final class AccessibilityController {
    private static final float[] sTempFloats = new float[9];
    private SparseArray<DisplayMagnifier> mDisplayMagnifiers = new SparseArray<>();
    private final WindowManagerService mService;
    private WindowsForAccessibilityObserver mWindowsForAccessibilityObserver;

    public AccessibilityController(WindowManagerService service) {
        this.mService = service;
    }

    public boolean setMagnificationCallbacksLocked(int displayId, WindowManagerInternal.MagnificationCallbacks callbacks) {
        Display display;
        if (callbacks == null) {
            DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
            if (displayMagnifier != null) {
                displayMagnifier.destroyLocked();
                this.mDisplayMagnifiers.remove(displayId);
                return true;
            }
            throw new IllegalStateException("Magnification callbacks already cleared!");
        } else if (this.mDisplayMagnifiers.get(displayId) == null) {
            DisplayContent dc = this.mService.mRoot.getDisplayContent(displayId);
            if (dc == null || (display = dc.getDisplay()) == null || display.getType() == 4) {
                return false;
            }
            this.mDisplayMagnifiers.put(displayId, new DisplayMagnifier(this.mService, dc, display, callbacks));
            return true;
        } else {
            throw new IllegalStateException("Magnification callbacks already set!");
        }
    }

    public void setWindowsForAccessibilityCallback(WindowManagerInternal.WindowsForAccessibilityCallback callback) {
        if (callback != null) {
            if (this.mWindowsForAccessibilityObserver == null) {
                this.mWindowsForAccessibilityObserver = new WindowsForAccessibilityObserver(this.mService, callback);
                return;
            }
            throw new IllegalStateException("Windows for accessibility callback already set!");
        } else if (this.mWindowsForAccessibilityObserver != null) {
            this.mWindowsForAccessibilityObserver = null;
        } else {
            throw new IllegalStateException("Windows for accessibility callback already cleared!");
        }
    }

    public void performComputeChangedWindowsNotLocked(boolean forceSend) {
        WindowsForAccessibilityObserver observer;
        synchronized (this.mService) {
            observer = this.mWindowsForAccessibilityObserver;
        }
        if (observer != null) {
            observer.performComputeChangedWindowsNotLocked(forceSend);
        }
    }

    public void setMagnificationSpecLocked(int displayId, MagnificationSpec spec) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.setMagnificationSpecLocked(spec);
        }
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
        if (windowsForAccessibilityObserver != null && displayId == 0) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
        }
    }

    public void getMagnificationRegionLocked(int displayId, Region outMagnificationRegion) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.getMagnificationRegionLocked(outMagnificationRegion);
        }
    }

    public void onRectangleOnScreenRequestedLocked(int displayId, Rect rectangle) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.onRectangleOnScreenRequestedLocked(rectangle);
        }
    }

    public void onWindowLayersChangedLocked(int displayId) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.onWindowLayersChangedLocked();
        }
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
        if (windowsForAccessibilityObserver != null && displayId == 0) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
        }
    }

    public void onRotationChangedLocked(DisplayContent displayContent) {
        int displayId = displayContent.getDisplayId();
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.onRotationChangedLocked(displayContent);
        }
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
        if (windowsForAccessibilityObserver != null && displayId == 0) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
        }
    }

    public void onAppWindowTransitionLocked(WindowState windowState, int transition) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(windowState.getDisplayId());
        if (displayMagnifier != null) {
            displayMagnifier.onAppWindowTransitionLocked(windowState, transition);
        }
    }

    public void onWindowTransitionLocked(WindowState windowState, int transition) {
        int displayId = windowState.getDisplayId();
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.onWindowTransitionLocked(windowState, transition);
        }
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
        if (windowsForAccessibilityObserver != null && displayId == 0) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
        }
    }

    public void onWindowFocusChangedNotLocked() {
        WindowsForAccessibilityObserver observer;
        synchronized (this.mService) {
            observer = this.mWindowsForAccessibilityObserver;
        }
        if (observer != null) {
            observer.performComputeChangedWindowsNotLocked(false);
        }
    }

    public void onSomeWindowResizedOrMovedLocked() {
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
        if (windowsForAccessibilityObserver != null) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
        }
    }

    public void drawMagnifiedRegionBorderIfNeededLocked(int displayId) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.drawMagnifiedRegionBorderIfNeededLocked();
        }
    }

    public MagnificationSpec getMagnificationSpecForWindowLocked(WindowState windowState) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(windowState.getDisplayId());
        if (displayMagnifier != null) {
            return displayMagnifier.getMagnificationSpecForWindowLocked(windowState);
        }
        return null;
    }

    public boolean hasCallbacksLocked() {
        return this.mDisplayMagnifiers.size() > 0 || this.mWindowsForAccessibilityObserver != null;
    }

    public void setForceShowMagnifiableBoundsLocked(int displayId, boolean show) {
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayId);
        if (displayMagnifier != null) {
            displayMagnifier.setForceShowMagnifiableBoundsLocked(show);
            displayMagnifier.showMagnificationBoundsIfNeeded();
        }
    }

    /* access modifiers changed from: private */
    public static void populateTransformationMatrixLocked(WindowState windowState, Matrix outMatrix) {
        windowState.getTransformationMatrix(sTempFloats, outMatrix);
    }

    private static final class DisplayMagnifier {
        private static final boolean DEBUG_LAYERS = false;
        private static final boolean DEBUG_RECTANGLE_REQUESTED = false;
        private static final boolean DEBUG_ROTATION = false;
        private static final boolean DEBUG_VIEWPORT_WINDOW = false;
        private static final boolean DEBUG_WINDOW_TRANSITIONS = false;
        private static final String LOG_TAG = "WindowManager";
        /* access modifiers changed from: private */
        public final WindowManagerInternal.MagnificationCallbacks mCallbacks;
        /* access modifiers changed from: private */
        public final Context mContext;
        /* access modifiers changed from: private */
        public final Display mDisplay;
        /* access modifiers changed from: private */
        public final DisplayContent mDisplayContent;
        private boolean mForceShowMagnifiableBounds = false;
        /* access modifiers changed from: private */
        public final Handler mHandler;
        /* access modifiers changed from: private */
        public final long mLongAnimationDuration;
        /* access modifiers changed from: private */
        public final MagnifiedViewport mMagnifedViewport;
        /* access modifiers changed from: private */
        public final WindowManagerService mService;
        /* access modifiers changed from: private */
        public final Rect mTempRect1 = new Rect();
        private final Rect mTempRect2 = new Rect();
        /* access modifiers changed from: private */
        public final Region mTempRegion1 = new Region();
        /* access modifiers changed from: private */
        public final Region mTempRegion2 = new Region();
        /* access modifiers changed from: private */
        public final Region mTempRegion3 = new Region();
        /* access modifiers changed from: private */
        public final Region mTempRegion4 = new Region();

        public DisplayMagnifier(WindowManagerService windowManagerService, DisplayContent displayContent, Display display, WindowManagerInternal.MagnificationCallbacks callbacks) {
            this.mContext = windowManagerService.mContext;
            this.mService = windowManagerService;
            this.mCallbacks = callbacks;
            this.mDisplayContent = displayContent;
            this.mDisplay = display;
            this.mHandler = new MyHandler(this.mService.mH.getLooper());
            this.mMagnifedViewport = new MagnifiedViewport();
            this.mLongAnimationDuration = (long) this.mContext.getResources().getInteger(17694722);
        }

        public void setMagnificationSpecLocked(MagnificationSpec spec) {
            this.mMagnifedViewport.updateMagnificationSpecLocked(spec);
            this.mMagnifedViewport.recomputeBoundsLocked();
            this.mService.applyMagnificationSpecLocked(this.mDisplay.getDisplayId(), spec);
            this.mService.scheduleAnimationLocked();
        }

        public void setForceShowMagnifiableBoundsLocked(boolean show) {
            this.mForceShowMagnifiableBounds = show;
            this.mMagnifedViewport.setMagnifiedRegionBorderShownLocked(show, true);
        }

        public boolean isForceShowingMagnifiableBoundsLocked() {
            return this.mForceShowMagnifiableBounds;
        }

        public void onRectangleOnScreenRequestedLocked(Rect rectangle) {
            if (this.mMagnifedViewport.isMagnifyingLocked()) {
                Rect magnifiedRegionBounds = this.mTempRect2;
                this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked(magnifiedRegionBounds);
                if (!magnifiedRegionBounds.contains(rectangle)) {
                    SomeArgs args = SomeArgs.obtain();
                    args.argi1 = rectangle.left;
                    args.argi2 = rectangle.top;
                    args.argi3 = rectangle.right;
                    args.argi4 = rectangle.bottom;
                    this.mHandler.obtainMessage(2, args).sendToTarget();
                }
            }
        }

        public void onWindowLayersChangedLocked() {
            this.mMagnifedViewport.recomputeBoundsLocked();
            this.mService.scheduleAnimationLocked();
        }

        public void onRotationChangedLocked(DisplayContent displayContent) {
            this.mMagnifedViewport.onRotationChangedLocked();
            this.mHandler.sendEmptyMessage(4);
        }

        public void onAppWindowTransitionLocked(WindowState windowState, int transition) {
            if (this.mMagnifedViewport.isMagnifyingLocked()) {
                switch (transition) {
                    case 6:
                    case 8:
                    case 10:
                    case 12:
                    case 13:
                    case 14:
                        this.mHandler.sendEmptyMessage(3);
                        return;
                    default:
                        return;
                }
            }
        }

        public void onWindowTransitionLocked(WindowState windowState, int transition) {
            boolean magnifying = this.mMagnifedViewport.isMagnifyingLocked();
            int type = windowState.mAttrs.type;
            if ((transition == 1 || transition == 3) && magnifying) {
                if (!(type == 2 || type == 4 || type == 1005 || type == 2020 || type == 2024 || type == 2035 || type == 2038)) {
                    switch (type) {
                        case 1000:
                        case 1001:
                        case 1002:
                        case 1003:
                            break;
                        default:
                            switch (type) {
                                case 2001:
                                case 2002:
                                case 2003:
                                    break;
                                default:
                                    switch (type) {
                                        case 2005:
                                        case 2006:
                                        case 2007:
                                        case 2008:
                                        case 2009:
                                        case 2010:
                                            break;
                                        default:
                                            return;
                                    }
                            }
                    }
                }
                Rect magnifiedRegionBounds = this.mTempRect2;
                this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked(magnifiedRegionBounds);
                Rect touchableRegionBounds = this.mTempRect1;
                windowState.getTouchableRegion(this.mTempRegion1);
                this.mTempRegion1.getBounds(touchableRegionBounds);
                if (!magnifiedRegionBounds.intersect(touchableRegionBounds)) {
                    this.mCallbacks.onRectangleOnScreenRequested(touchableRegionBounds.left, touchableRegionBounds.top, touchableRegionBounds.right, touchableRegionBounds.bottom);
                }
            }
        }

        public MagnificationSpec getMagnificationSpecForWindowLocked(WindowState windowState) {
            MagnificationSpec spec = this.mMagnifedViewport.getMagnificationSpecLocked();
            if (spec == null || spec.isNop() || windowState.shouldMagnify()) {
                return spec;
            }
            return null;
        }

        public void getMagnificationRegionLocked(Region outMagnificationRegion) {
            this.mMagnifedViewport.recomputeBoundsLocked();
            this.mMagnifedViewport.getMagnificationRegionLocked(outMagnificationRegion);
        }

        public void destroyLocked() {
            this.mMagnifedViewport.destroyWindow();
        }

        public void showMagnificationBoundsIfNeeded() {
            this.mHandler.obtainMessage(5).sendToTarget();
        }

        public void drawMagnifiedRegionBorderIfNeededLocked() {
            this.mMagnifedViewport.drawWindowIfNeededLocked();
        }

        private final class MagnifiedViewport {
            /* access modifiers changed from: private */
            public final float mBorderWidth;
            private final Path mCircularPath;
            private final int mDrawBorderInset;
            private boolean mFullRedrawNeeded;
            /* access modifiers changed from: private */
            public final int mHalfBorderWidth;
            private final Region mMagnificationRegion = new Region();
            private final MagnificationSpec mMagnificationSpec = MagnificationSpec.obtain();
            private final Region mOldMagnificationRegion = new Region();
            private int mTempLayer = 0;
            private final Matrix mTempMatrix = new Matrix();
            /* access modifiers changed from: private */
            public final Point mTempPoint = new Point();
            private final RectF mTempRectF = new RectF();
            private final SparseArray<WindowState> mTempWindowStates = new SparseArray<>();
            private final ViewportWindow mWindow;
            /* access modifiers changed from: private */
            public final WindowManager mWindowManager;

            public MagnifiedViewport() {
                this.mWindowManager = (WindowManager) DisplayMagnifier.this.mContext.getSystemService("window");
                this.mBorderWidth = DisplayMagnifier.this.mContext.getResources().getDimension(17104904);
                this.mHalfBorderWidth = (int) Math.ceil((double) (this.mBorderWidth / 2.0f));
                this.mDrawBorderInset = ((int) this.mBorderWidth) / 2;
                this.mWindow = new ViewportWindow(DisplayMagnifier.this.mContext);
                if (DisplayMagnifier.this.mContext.getResources().getConfiguration().isScreenRound()) {
                    this.mCircularPath = new Path();
                    DisplayMagnifier.this.mDisplay.getRealSize(this.mTempPoint);
                    int centerXY = this.mTempPoint.x / 2;
                    this.mCircularPath.addCircle((float) centerXY, (float) centerXY, (float) centerXY, Path.Direction.CW);
                } else {
                    this.mCircularPath = null;
                }
                recomputeBoundsLocked();
            }

            public void getMagnificationRegionLocked(Region outMagnificationRegion) {
                outMagnificationRegion.set(this.mMagnificationRegion);
            }

            public void updateMagnificationSpecLocked(MagnificationSpec spec) {
                if (spec != null) {
                    this.mMagnificationSpec.initialize(spec.scale, spec.offsetX, spec.offsetY);
                } else {
                    this.mMagnificationSpec.clear();
                }
                if (!DisplayMagnifier.this.mHandler.hasMessages(5)) {
                    setMagnifiedRegionBorderShownLocked(isMagnifyingLocked() || DisplayMagnifier.this.isForceShowingMagnifiableBoundsLocked(), true);
                }
            }

            public void recomputeBoundsLocked() {
                DisplayMagnifier.this.mDisplay.getRealSize(this.mTempPoint);
                int screenWidth = this.mTempPoint.x;
                int screenHeight = this.mTempPoint.y;
                this.mMagnificationRegion.set(0, 0, 0, 0);
                Region availableBounds = DisplayMagnifier.this.mTempRegion1;
                availableBounds.set(0, 0, screenWidth, screenHeight);
                Path path = this.mCircularPath;
                if (path != null) {
                    availableBounds.setPath(path, availableBounds);
                }
                Region nonMagnifiedBounds = DisplayMagnifier.this.mTempRegion4;
                nonMagnifiedBounds.set(0, 0, 0, 0);
                SparseArray<WindowState> visibleWindows = this.mTempWindowStates;
                visibleWindows.clear();
                populateWindowsOnScreenLocked(visibleWindows);
                for (int i = visibleWindows.size() - 1; i >= 0; i--) {
                    WindowState windowState = visibleWindows.valueAt(i);
                    if (windowState.mAttrs.type != 2027 && (windowState.mAttrs.privateFlags & DumpState.DUMP_DEXOPT) == 0) {
                        Matrix matrix = this.mTempMatrix;
                        AccessibilityController.populateTransformationMatrixLocked(windowState, matrix);
                        Region touchableRegion = DisplayMagnifier.this.mTempRegion3;
                        windowState.getTouchableRegion(touchableRegion);
                        Rect touchableFrame = DisplayMagnifier.this.mTempRect1;
                        touchableRegion.getBounds(touchableFrame);
                        RectF windowFrame = this.mTempRectF;
                        windowFrame.set(touchableFrame);
                        windowFrame.offset((float) (-windowState.getFrameLw().left), (float) (-windowState.getFrameLw().top));
                        matrix.mapRect(windowFrame);
                        Region windowBounds = DisplayMagnifier.this.mTempRegion2;
                        Rect rect = touchableFrame;
                        Region region = touchableRegion;
                        windowBounds.set((int) windowFrame.left, (int) windowFrame.top, (int) windowFrame.right, (int) windowFrame.bottom);
                        Region portionOfWindowAlreadyAccountedFor = DisplayMagnifier.this.mTempRegion3;
                        portionOfWindowAlreadyAccountedFor.set(this.mMagnificationRegion);
                        portionOfWindowAlreadyAccountedFor.op(nonMagnifiedBounds, Region.Op.UNION);
                        windowBounds.op(portionOfWindowAlreadyAccountedFor, Region.Op.DIFFERENCE);
                        if (windowState.shouldMagnify()) {
                            this.mMagnificationRegion.op(windowBounds, Region.Op.UNION);
                            this.mMagnificationRegion.op(availableBounds, Region.Op.INTERSECT);
                        } else {
                            nonMagnifiedBounds.op(windowBounds, Region.Op.UNION);
                            availableBounds.op(windowBounds, Region.Op.DIFFERENCE);
                        }
                        if (windowState.isLetterboxedForDisplayCutoutLw()) {
                            Region letterboxBounds = getLetterboxBounds(windowState);
                            nonMagnifiedBounds.op(letterboxBounds, Region.Op.UNION);
                            availableBounds.op(letterboxBounds, Region.Op.DIFFERENCE);
                        }
                        Region accountedBounds = DisplayMagnifier.this.mTempRegion2;
                        accountedBounds.set(this.mMagnificationRegion);
                        accountedBounds.op(nonMagnifiedBounds, Region.Op.UNION);
                        Region region2 = windowBounds;
                        RectF rectF = windowFrame;
                        Region accountedBounds2 = accountedBounds;
                        Matrix matrix2 = matrix;
                        accountedBounds.op(0, 0, screenWidth, screenHeight, Region.Op.INTERSECT);
                        if (accountedBounds2.isRect()) {
                            Rect accountedFrame = DisplayMagnifier.this.mTempRect1;
                            accountedBounds2.getBounds(accountedFrame);
                            if (accountedFrame.width() == screenWidth && accountedFrame.height() == screenHeight) {
                                break;
                            }
                        }
                    }
                }
                visibleWindows.clear();
                Region region3 = this.mMagnificationRegion;
                int i2 = this.mDrawBorderInset;
                region3.op(i2, i2, screenWidth - i2, screenHeight - i2, Region.Op.INTERSECT);
                if (!this.mOldMagnificationRegion.equals(this.mMagnificationRegion)) {
                    this.mWindow.setBounds(this.mMagnificationRegion);
                    Rect dirtyRect = DisplayMagnifier.this.mTempRect1;
                    if (this.mFullRedrawNeeded) {
                        this.mFullRedrawNeeded = false;
                        int i3 = this.mDrawBorderInset;
                        dirtyRect.set(i3, i3, screenWidth - i3, screenHeight - i3);
                        this.mWindow.invalidate(dirtyRect);
                    } else {
                        Region dirtyRegion = DisplayMagnifier.this.mTempRegion3;
                        dirtyRegion.set(this.mMagnificationRegion);
                        dirtyRegion.op(this.mOldMagnificationRegion, Region.Op.UNION);
                        dirtyRegion.op(nonMagnifiedBounds, Region.Op.INTERSECT);
                        dirtyRegion.getBounds(dirtyRect);
                        this.mWindow.invalidate(dirtyRect);
                    }
                    this.mOldMagnificationRegion.set(this.mMagnificationRegion);
                    SomeArgs args = SomeArgs.obtain();
                    args.arg1 = Region.obtain(this.mMagnificationRegion);
                    DisplayMagnifier.this.mHandler.obtainMessage(1, args).sendToTarget();
                }
            }

            private Region getLetterboxBounds(WindowState windowState) {
                AppWindowToken appToken = windowState.mAppToken;
                if (appToken == null) {
                    return new Region();
                }
                DisplayMagnifier.this.mDisplay.getRealSize(this.mTempPoint);
                Rect letterboxInsets = appToken.getLetterboxInsets();
                int screenWidth = this.mTempPoint.x;
                int screenHeight = this.mTempPoint.y;
                Rect nonLetterboxRect = DisplayMagnifier.this.mTempRect1;
                Region letterboxBounds = DisplayMagnifier.this.mTempRegion3;
                nonLetterboxRect.set(0, 0, screenWidth, screenHeight);
                nonLetterboxRect.inset(letterboxInsets);
                letterboxBounds.set(0, 0, screenWidth, screenHeight);
                letterboxBounds.op(nonLetterboxRect, Region.Op.DIFFERENCE);
                return letterboxBounds;
            }

            public void onRotationChangedLocked() {
                if (isMagnifyingLocked() || DisplayMagnifier.this.isForceShowingMagnifiableBoundsLocked()) {
                    setMagnifiedRegionBorderShownLocked(false, false);
                    Message message = DisplayMagnifier.this.mHandler.obtainMessage(5);
                    DisplayMagnifier.this.mHandler.sendMessageDelayed(message, (long) (((float) DisplayMagnifier.this.mLongAnimationDuration) * DisplayMagnifier.this.mService.getWindowAnimationScaleLocked()));
                }
                recomputeBoundsLocked();
                this.mWindow.updateSize();
            }

            public void setMagnifiedRegionBorderShownLocked(boolean shown, boolean animate) {
                if (shown) {
                    this.mFullRedrawNeeded = true;
                    this.mOldMagnificationRegion.set(0, 0, 0, 0);
                }
                this.mWindow.setShown(shown, animate);
            }

            public void getMagnifiedFrameInContentCoordsLocked(Rect rect) {
                MagnificationSpec spec = this.mMagnificationSpec;
                this.mMagnificationRegion.getBounds(rect);
                rect.offset((int) (-spec.offsetX), (int) (-spec.offsetY));
                rect.scale(1.0f / spec.scale);
            }

            public boolean isMagnifyingLocked() {
                return this.mMagnificationSpec.scale > 1.0f;
            }

            public MagnificationSpec getMagnificationSpecLocked() {
                return this.mMagnificationSpec;
            }

            public void drawWindowIfNeededLocked() {
                recomputeBoundsLocked();
                this.mWindow.drawIfNeeded();
            }

            public void destroyWindow() {
                this.mWindow.releaseSurface();
            }

            private void populateWindowsOnScreenLocked(SparseArray<WindowState> outWindows) {
                this.mTempLayer = 0;
                DisplayMagnifier.this.mDisplayContent.forAllWindows((Consumer<WindowState>) 
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000e: INVOKE  
                      (wrap: com.android.server.wm.DisplayContent : 0x0005: INVOKE  (r1v1 com.android.server.wm.DisplayContent) = 
                      (wrap: com.android.server.wm.AccessibilityController$DisplayMagnifier : 0x0003: IGET  (r1v0 com.android.server.wm.AccessibilityController$DisplayMagnifier) = 
                      (r3v0 'this' com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport A[THIS])
                     com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.this$0 com.android.server.wm.AccessibilityController$DisplayMagnifier)
                     com.android.server.wm.AccessibilityController.DisplayMagnifier.access$1100(com.android.server.wm.AccessibilityController$DisplayMagnifier):com.android.server.wm.DisplayContent type: STATIC)
                      (wrap: com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA : 0x000b: CONSTRUCTOR  (r2v0 com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA) = 
                      (r3v0 'this' com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport A[THIS])
                      (r4v0 'outWindows' android.util.SparseArray<com.android.server.wm.WindowState>)
                     call: com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA.<init>(com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport, android.util.SparseArray):void type: CONSTRUCTOR)
                      false
                     com.android.server.wm.DisplayContent.forAllWindows(java.util.function.Consumer, boolean):void type: VIRTUAL in method: com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.populateWindowsOnScreenLocked(android.util.SparseArray):void, dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: CONSTRUCTOR  (r2v0 com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA) = 
                      (r3v0 'this' com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport A[THIS])
                      (r4v0 'outWindows' android.util.SparseArray<com.android.server.wm.WindowState>)
                     call: com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA.<init>(com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport, android.util.SparseArray):void type: CONSTRUCTOR in method: com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.populateWindowsOnScreenLocked(android.util.SparseArray):void, dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    r0 = 0
                    r3.mTempLayer = r0
                    com.android.server.wm.AccessibilityController$DisplayMagnifier r1 = com.android.server.wm.AccessibilityController.DisplayMagnifier.this
                    com.android.server.wm.DisplayContent r1 = r1.mDisplayContent
                    com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA r2 = new com.android.server.wm.-$$Lambda$AccessibilityController$DisplayMagnifier$MagnifiedViewport$ZNyFGy-UXiWV1D2yZGvH-9qN0AA
                    r2.<init>(r3, r4)
                    r1.forAllWindows((java.util.function.Consumer<com.android.server.wm.WindowState>) r2, (boolean) r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.populateWindowsOnScreenLocked(android.util.SparseArray):void");
            }

            public /* synthetic */ void lambda$populateWindowsOnScreenLocked$0$AccessibilityController$DisplayMagnifier$MagnifiedViewport(SparseArray outWindows, WindowState w) {
                if (w.isOnScreen() && w.isVisibleLw() && w.mAttrs.alpha != 0.0f && !w.mWinAnimator.mEnterAnimationPending) {
                    this.mTempLayer++;
                    outWindows.put(this.mTempLayer, w);
                }
            }

            private final class ViewportWindow {
                private static final String SURFACE_TITLE = "Magnification Overlay";
                private int mAlpha;
                private final AnimationController mAnimationController;
                private final Region mBounds = new Region();
                private final Rect mDirtyRect = new Rect();
                private boolean mInvalidated;
                private final Paint mPaint = new Paint();
                private boolean mShown;
                private final Surface mSurface = new Surface();
                private final SurfaceControl mSurfaceControl;

                public ViewportWindow(Context context) {
                    SurfaceControl surfaceControl = null;
                    try {
                        DisplayMagnifier.this.mDisplay.getRealSize(MagnifiedViewport.this.mTempPoint);
                        surfaceControl = DisplayMagnifier.this.mDisplayContent.makeOverlay().setName(SURFACE_TITLE).setBufferSize(MagnifiedViewport.this.mTempPoint.x, MagnifiedViewport.this.mTempPoint.y).setFormat(-3).build();
                    } catch (Surface.OutOfResourcesException e) {
                    }
                    this.mSurfaceControl = surfaceControl;
                    this.mSurfaceControl.setLayer(DisplayMagnifier.this.mService.mPolicy.getWindowLayerFromTypeLw(2027) * 10000);
                    this.mSurfaceControl.setPosition(0.0f, 0.0f);
                    this.mSurface.copyFrom(this.mSurfaceControl);
                    this.mAnimationController = new AnimationController(context, DisplayMagnifier.this.mService.mH.getLooper());
                    TypedValue typedValue = new TypedValue();
                    context.getTheme().resolveAttribute(16843664, typedValue, true);
                    int borderColor = context.getColor(typedValue.resourceId);
                    this.mPaint.setStyle(Paint.Style.STROKE);
                    this.mPaint.setStrokeWidth(MagnifiedViewport.this.mBorderWidth);
                    this.mPaint.setColor(borderColor);
                    this.mInvalidated = true;
                }

                public void setShown(boolean shown, boolean animate) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mShown == shown) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mShown = shown;
                            this.mAnimationController.onFrameShownStateChanged(shown, animate);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                }

                public int getAlpha() {
                    int i;
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            i = this.mAlpha;
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return i;
                }

                public void setAlpha(int alpha) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mAlpha == alpha) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mAlpha = alpha;
                            invalidate((Rect) null);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                }

                public void setBounds(Region bounds) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mBounds.equals(bounds)) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mBounds.set(bounds);
                            invalidate(this.mDirtyRect);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                }

                public void updateSize() {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            MagnifiedViewport.this.mWindowManager.getDefaultDisplay().getRealSize(MagnifiedViewport.this.mTempPoint);
                            this.mSurfaceControl.setBufferSize(MagnifiedViewport.this.mTempPoint.x, MagnifiedViewport.this.mTempPoint.y);
                            invalidate(this.mDirtyRect);
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }

                public void invalidate(Rect dirtyRect) {
                    if (dirtyRect != null) {
                        this.mDirtyRect.set(dirtyRect);
                    } else {
                        this.mDirtyRect.setEmpty();
                    }
                    this.mInvalidated = true;
                    DisplayMagnifier.this.mService.scheduleAnimationLocked();
                }

                /* JADX WARNING: Code restructure failed: missing block: B:29:0x007e, code lost:
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:30:0x0081, code lost:
                    return;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void drawIfNeeded() {
                    /*
                        r6 = this;
                        com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport r0 = com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.this
                        com.android.server.wm.AccessibilityController$DisplayMagnifier r0 = com.android.server.wm.AccessibilityController.DisplayMagnifier.this
                        com.android.server.wm.WindowManagerService r0 = r0.mService
                        com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                        monitor-enter(r0)
                        com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0082 }
                        boolean r1 = r6.mInvalidated     // Catch:{ all -> 0x0082 }
                        if (r1 != 0) goto L_0x0017
                        monitor-exit(r0)     // Catch:{ all -> 0x0082 }
                        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                        return
                    L_0x0017:
                        r1 = 0
                        r6.mInvalidated = r1     // Catch:{ all -> 0x0082 }
                        int r2 = r6.mAlpha     // Catch:{ all -> 0x0082 }
                        if (r2 <= 0) goto L_0x0078
                        r2 = 0
                        android.graphics.Rect r3 = r6.mDirtyRect     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        boolean r3 = r3.isEmpty()     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        if (r3 == 0) goto L_0x002e
                        android.graphics.Region r3 = r6.mBounds     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        android.graphics.Rect r4 = r6.mDirtyRect     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        r3.getBounds(r4)     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                    L_0x002e:
                        android.graphics.Rect r3 = r6.mDirtyRect     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport r4 = com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.this     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        int r4 = r4.mHalfBorderWidth     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        int r4 = -r4
                        com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport r5 = com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.this     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        int r5 = r5.mHalfBorderWidth     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        int r5 = -r5
                        r3.inset(r4, r5)     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        android.view.Surface r3 = r6.mSurface     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        android.graphics.Rect r4 = r6.mDirtyRect     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        android.graphics.Canvas r3 = r3.lockCanvas(r4)     // Catch:{ IllegalArgumentException -> 0x004d, OutOfResourcesException -> 0x004b }
                        r2 = r3
                    L_0x004a:
                        goto L_0x004f
                    L_0x004b:
                        r3 = move-exception
                        goto L_0x004f
                    L_0x004d:
                        r3 = move-exception
                        goto L_0x004a
                    L_0x004f:
                        if (r2 != 0) goto L_0x0056
                        monitor-exit(r0)     // Catch:{ all -> 0x0082 }
                        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                        return
                    L_0x0056:
                        android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0082 }
                        r2.drawColor(r1, r3)     // Catch:{ all -> 0x0082 }
                        android.graphics.Paint r1 = r6.mPaint     // Catch:{ all -> 0x0082 }
                        int r3 = r6.mAlpha     // Catch:{ all -> 0x0082 }
                        r1.setAlpha(r3)     // Catch:{ all -> 0x0082 }
                        android.graphics.Region r1 = r6.mBounds     // Catch:{ all -> 0x0082 }
                        android.graphics.Path r1 = r1.getBoundaryPath()     // Catch:{ all -> 0x0082 }
                        android.graphics.Paint r3 = r6.mPaint     // Catch:{ all -> 0x0082 }
                        r2.drawPath(r1, r3)     // Catch:{ all -> 0x0082 }
                        android.view.Surface r3 = r6.mSurface     // Catch:{ all -> 0x0082 }
                        r3.unlockCanvasAndPost(r2)     // Catch:{ all -> 0x0082 }
                        android.view.SurfaceControl r3 = r6.mSurfaceControl     // Catch:{ all -> 0x0082 }
                        r3.show()     // Catch:{ all -> 0x0082 }
                        goto L_0x007d
                    L_0x0078:
                        android.view.SurfaceControl r1 = r6.mSurfaceControl     // Catch:{ all -> 0x0082 }
                        r1.hide()     // Catch:{ all -> 0x0082 }
                    L_0x007d:
                        monitor-exit(r0)     // Catch:{ all -> 0x0082 }
                        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                        return
                    L_0x0082:
                        r1 = move-exception
                        monitor-exit(r0)     // Catch:{ all -> 0x0082 }
                        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AccessibilityController.DisplayMagnifier.MagnifiedViewport.ViewportWindow.drawIfNeeded():void");
                }

                public void releaseSurface() {
                    this.mSurfaceControl.remove();
                    this.mSurface.release();
                }

                private final class AnimationController extends Handler {
                    private static final int MAX_ALPHA = 255;
                    private static final int MIN_ALPHA = 0;
                    private static final int MSG_FRAME_SHOWN_STATE_CHANGED = 1;
                    private static final String PROPERTY_NAME_ALPHA = "alpha";
                    private final ValueAnimator mShowHideFrameAnimator;

                    public AnimationController(Context context, Looper looper) {
                        super(looper);
                        this.mShowHideFrameAnimator = ObjectAnimator.ofInt(ViewportWindow.this, PROPERTY_NAME_ALPHA, new int[]{0, 255});
                        this.mShowHideFrameAnimator.setInterpolator(new DecelerateInterpolator(2.5f));
                        this.mShowHideFrameAnimator.setDuration((long) context.getResources().getInteger(17694722));
                    }

                    public void onFrameShownStateChanged(boolean shown, boolean animate) {
                        obtainMessage(1, shown, animate).sendToTarget();
                    }

                    public void handleMessage(Message message) {
                        boolean animate = true;
                        if (message.what == 1) {
                            boolean shown = message.arg1 == 1;
                            if (message.arg2 != 1) {
                                animate = false;
                            }
                            if (!animate) {
                                this.mShowHideFrameAnimator.cancel();
                                if (shown) {
                                    ViewportWindow.this.setAlpha(255);
                                } else {
                                    ViewportWindow.this.setAlpha(0);
                                }
                            } else if (this.mShowHideFrameAnimator.isRunning()) {
                                this.mShowHideFrameAnimator.reverse();
                            } else if (shown) {
                                this.mShowHideFrameAnimator.start();
                            } else {
                                this.mShowHideFrameAnimator.reverse();
                            }
                        }
                    }
                }
            }
        }

        private class MyHandler extends Handler {
            public static final int MESSAGE_NOTIFY_MAGNIFICATION_REGION_CHANGED = 1;
            public static final int MESSAGE_NOTIFY_RECTANGLE_ON_SCREEN_REQUESTED = 2;
            public static final int MESSAGE_NOTIFY_ROTATION_CHANGED = 4;
            public static final int MESSAGE_NOTIFY_USER_CONTEXT_CHANGED = 3;
            public static final int MESSAGE_SHOW_MAGNIFIED_REGION_BOUNDS_IF_NEEDED = 5;

            public MyHandler(Looper looper) {
                super(looper);
            }

            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Region magnifiedBounds = (Region) ((SomeArgs) message.obj).arg1;
                    DisplayMagnifier.this.mCallbacks.onMagnificationRegionChanged(magnifiedBounds);
                    magnifiedBounds.recycle();
                } else if (i == 2) {
                    SomeArgs args = (SomeArgs) message.obj;
                    DisplayMagnifier.this.mCallbacks.onRectangleOnScreenRequested(args.argi1, args.argi2, args.argi3, args.argi4);
                    args.recycle();
                } else if (i == 3) {
                    DisplayMagnifier.this.mCallbacks.onUserContextChanged();
                } else if (i == 4) {
                    DisplayMagnifier.this.mCallbacks.onRotationChanged(message.arg1);
                } else if (i == 5) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (DisplayMagnifier.this.mMagnifedViewport.isMagnifyingLocked() || DisplayMagnifier.this.isForceShowingMagnifiableBoundsLocked()) {
                                DisplayMagnifier.this.mMagnifedViewport.setMagnifiedRegionBorderShownLocked(true, true);
                                DisplayMagnifier.this.mService.scheduleAnimationLocked();
                            }
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }
    }

    private static final class WindowsForAccessibilityObserver {
        private static final boolean DEBUG = false;
        private static final String LOG_TAG = "WindowManager";
        private final WindowManagerInternal.WindowsForAccessibilityCallback mCallback;
        private final Context mContext;
        private final Handler mHandler;
        private final List<WindowInfo> mOldWindows = new ArrayList();
        private final long mRecurringAccessibilityEventsIntervalMillis;
        private final WindowManagerService mService;
        private final Set<IBinder> mTempBinderSet = new ArraySet();
        private int mTempLayer = 0;
        private final Matrix mTempMatrix = new Matrix();
        private final Point mTempPoint = new Point();
        private final Rect mTempRect = new Rect();
        private final RectF mTempRectF = new RectF();
        private final Region mTempRegion = new Region();
        private final Region mTempRegion1 = new Region();
        private final SparseArray<WindowState> mTempWindowStates = new SparseArray<>();

        public WindowsForAccessibilityObserver(WindowManagerService windowManagerService, WindowManagerInternal.WindowsForAccessibilityCallback callback) {
            this.mContext = windowManagerService.mContext;
            this.mService = windowManagerService;
            this.mCallback = callback;
            this.mHandler = new MyHandler(this.mService.mH.getLooper());
            this.mRecurringAccessibilityEventsIntervalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            computeChangedWindows(true);
        }

        public void performComputeChangedWindowsNotLocked(boolean forceSend) {
            this.mHandler.removeMessages(1);
            computeChangedWindows(forceSend);
        }

        public void scheduleComputeChangedWindowsLocked() {
            if (!this.mHandler.hasMessages(1)) {
                this.mHandler.sendEmptyMessageDelayed(1, this.mRecurringAccessibilityEventsIntervalMillis);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:65:0x0122, code lost:
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:66:0x0125, code lost:
            if (r20 != false) goto L_0x0129;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x0127, code lost:
            if (r2 == false) goto L_0x012e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x0129, code lost:
            r1.mCallback.onWindowsForAccessibilityChanged(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:0x012e, code lost:
            clearAndRecycleWindows(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x0131, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void computeChangedWindows(boolean r20) {
            /*
                r19 = this;
                r1 = r19
                r2 = 0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r3 = r0
                com.android.server.wm.WindowManagerService r0 = r1.mService
                com.android.server.wm.WindowManagerGlobalLock r4 = r0.mGlobalLock
                monitor-enter(r4)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0136 }
                com.android.server.wm.WindowManagerService r0 = r1.mService     // Catch:{ all -> 0x0136 }
                com.android.server.wm.DisplayContent r0 = r0.getDefaultDisplayContentLocked()     // Catch:{ all -> 0x0136 }
                com.android.server.wm.WindowState r0 = r0.mCurrentFocus     // Catch:{ all -> 0x0136 }
                if (r0 != 0) goto L_0x0020
                monitor-exit(r4)     // Catch:{ all -> 0x013e }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x0020:
                android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x0136 }
                java.lang.String r5 = "window"
                java.lang.Object r0 = r0.getSystemService(r5)     // Catch:{ all -> 0x0136 }
                android.view.WindowManager r0 = (android.view.WindowManager) r0     // Catch:{ all -> 0x0136 }
                android.view.Display r5 = r0.getDefaultDisplay()     // Catch:{ all -> 0x0136 }
                android.graphics.Point r6 = r1.mTempPoint     // Catch:{ all -> 0x0136 }
                r5.getRealSize(r6)     // Catch:{ all -> 0x0136 }
                android.graphics.Point r5 = r1.mTempPoint     // Catch:{ all -> 0x0136 }
                int r5 = r5.x     // Catch:{ all -> 0x0136 }
                android.graphics.Point r6 = r1.mTempPoint     // Catch:{ all -> 0x0136 }
                int r6 = r6.y     // Catch:{ all -> 0x0136 }
                android.graphics.Region r7 = r1.mTempRegion     // Catch:{ all -> 0x0136 }
                r8 = 0
                r7.set(r8, r8, r5, r6)     // Catch:{ all -> 0x0136 }
                android.util.SparseArray<com.android.server.wm.WindowState> r8 = r1.mTempWindowStates     // Catch:{ all -> 0x0136 }
                r1.populateVisibleWindowsOnScreenLocked(r8)     // Catch:{ all -> 0x0136 }
                java.util.Set<android.os.IBinder> r9 = r1.mTempBinderSet     // Catch:{ all -> 0x0136 }
                r9.clear()     // Catch:{ all -> 0x0136 }
                r10 = 0
                int r11 = r8.size()     // Catch:{ all -> 0x0136 }
                java.util.HashSet r12 = new java.util.HashSet     // Catch:{ all -> 0x0136 }
                r12.<init>()     // Catch:{ all -> 0x0136 }
                int r13 = r11 + -1
            L_0x0057:
                if (r13 < 0) goto L_0x0082
                java.lang.Object r14 = r8.valueAt(r13)     // Catch:{ all -> 0x013e }
                com.android.server.wm.WindowState r14 = (com.android.server.wm.WindowState) r14     // Catch:{ all -> 0x013e }
                android.graphics.Rect r15 = r1.mTempRect     // Catch:{ all -> 0x013e }
                r1.computeWindowBoundsInScreen(r14, r15)     // Catch:{ all -> 0x013e }
                boolean r16 = r1.windowMattersToAccessibility(r14, r15, r7, r12)     // Catch:{ all -> 0x013e }
                if (r16 == 0) goto L_0x0076
                addPopulatedWindowInfo(r14, r15, r3, r9)     // Catch:{ all -> 0x013e }
                r1.updateUnaccountedSpace(r14, r15, r7, r12)     // Catch:{ all -> 0x013e }
                boolean r16 = r14.isFocused()     // Catch:{ all -> 0x013e }
                r10 = r10 | r16
            L_0x0076:
                boolean r16 = r7.isEmpty()     // Catch:{ all -> 0x013e }
                if (r16 == 0) goto L_0x007f
                if (r10 == 0) goto L_0x007f
                goto L_0x0082
            L_0x007f:
                int r13 = r13 + -1
                goto L_0x0057
            L_0x0082:
                int r13 = r3.size()     // Catch:{ all -> 0x0136 }
                r14 = 0
            L_0x0087:
                if (r14 >= r13) goto L_0x00d2
                java.lang.Object r15 = r3.get(r14)     // Catch:{ all -> 0x0136 }
                android.view.WindowInfo r15 = (android.view.WindowInfo) r15     // Catch:{ all -> 0x0136 }
                r16 = r0
                android.os.IBinder r0 = r15.parentToken     // Catch:{ all -> 0x0136 }
                boolean r0 = r9.contains(r0)     // Catch:{ all -> 0x0136 }
                if (r0 != 0) goto L_0x009c
                r0 = 0
                r15.parentToken = r0     // Catch:{ all -> 0x013e }
            L_0x009c:
                java.util.List r0 = r15.childTokens     // Catch:{ all -> 0x0136 }
                if (r0 == 0) goto L_0x00c9
                java.util.List r0 = r15.childTokens     // Catch:{ all -> 0x0136 }
                int r0 = r0.size()     // Catch:{ all -> 0x0136 }
                int r17 = r0 + -1
                r18 = r0
                r0 = r17
            L_0x00ac:
                if (r0 < 0) goto L_0x00c6
                r17 = r2
                java.util.List r2 = r15.childTokens     // Catch:{ all -> 0x0132 }
                java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0132 }
                boolean r2 = r9.contains(r2)     // Catch:{ all -> 0x0132 }
                if (r2 != 0) goto L_0x00c1
                java.util.List r2 = r15.childTokens     // Catch:{ all -> 0x0132 }
                r2.remove(r0)     // Catch:{ all -> 0x0132 }
            L_0x00c1:
                int r0 = r0 + -1
                r2 = r17
                goto L_0x00ac
            L_0x00c6:
                r17 = r2
                goto L_0x00cb
            L_0x00c9:
                r17 = r2
            L_0x00cb:
                int r14 = r14 + 1
                r0 = r16
                r2 = r17
                goto L_0x0087
            L_0x00d2:
                r16 = r0
                r17 = r2
                r8.clear()     // Catch:{ all -> 0x0132 }
                r9.clear()     // Catch:{ all -> 0x0132 }
                if (r20 != 0) goto L_0x0118
                java.util.List<android.view.WindowInfo> r0 = r1.mOldWindows     // Catch:{ all -> 0x0132 }
                int r0 = r0.size()     // Catch:{ all -> 0x0132 }
                int r2 = r3.size()     // Catch:{ all -> 0x0132 }
                if (r0 == r2) goto L_0x00ed
                r0 = 1
                r2 = r0
                goto L_0x011a
            L_0x00ed:
                java.util.List<android.view.WindowInfo> r0 = r1.mOldWindows     // Catch:{ all -> 0x0132 }
                boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0132 }
                if (r0 == 0) goto L_0x00fb
                boolean r0 = r3.isEmpty()     // Catch:{ all -> 0x0132 }
                if (r0 != 0) goto L_0x0118
            L_0x00fb:
                r0 = 0
            L_0x00fc:
                if (r0 >= r13) goto L_0x0118
                java.util.List<android.view.WindowInfo> r2 = r1.mOldWindows     // Catch:{ all -> 0x0132 }
                java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0132 }
                android.view.WindowInfo r2 = (android.view.WindowInfo) r2     // Catch:{ all -> 0x0132 }
                java.lang.Object r14 = r3.get(r0)     // Catch:{ all -> 0x0132 }
                android.view.WindowInfo r14 = (android.view.WindowInfo) r14     // Catch:{ all -> 0x0132 }
                boolean r15 = r1.windowChangedNoLayer(r2, r14)     // Catch:{ all -> 0x0132 }
                if (r15 == 0) goto L_0x0115
                r15 = 1
                r2 = r15
                goto L_0x011a
            L_0x0115:
                int r0 = r0 + 1
                goto L_0x00fc
            L_0x0118:
                r2 = r17
            L_0x011a:
                if (r20 != 0) goto L_0x011e
                if (r2 == 0) goto L_0x0121
            L_0x011e:
                r1.cacheWindows(r3)     // Catch:{ all -> 0x013e }
            L_0x0121:
                monitor-exit(r4)     // Catch:{ all -> 0x013e }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r20 != 0) goto L_0x0129
                if (r2 == 0) goto L_0x012e
            L_0x0129:
                com.android.server.wm.WindowManagerInternal$WindowsForAccessibilityCallback r0 = r1.mCallback
                r0.onWindowsForAccessibilityChanged(r3)
            L_0x012e:
                clearAndRecycleWindows(r3)
                return
            L_0x0132:
                r0 = move-exception
                r2 = r17
                goto L_0x0139
            L_0x0136:
                r0 = move-exception
                r17 = r2
            L_0x0139:
                monitor-exit(r4)     // Catch:{ all -> 0x013e }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r0
            L_0x013e:
                r0 = move-exception
                goto L_0x0139
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AccessibilityController.WindowsForAccessibilityObserver.computeChangedWindows(boolean):void");
        }

        private boolean windowMattersToAccessibility(WindowState windowState, Rect boundsInScreen, Region unaccountedSpace, HashSet<Integer> skipRemainingWindowsForTasks) {
            if (windowState.isFocused()) {
                return true;
            }
            Task task = windowState.getTask();
            if (task != null && skipRemainingWindowsForTasks.contains(Integer.valueOf(task.mTaskId))) {
                return false;
            }
            if (((windowState.mAttrs.flags & 16) == 0 || windowState.mAttrs.type == 2034) && !unaccountedSpace.quickReject(boundsInScreen) && isReportedWindowType(windowState.mAttrs.type)) {
                return true;
            }
            return false;
        }

        private void updateUnaccountedSpace(WindowState windowState, Rect boundsInScreen, Region unaccountedSpace, HashSet<Integer> skipRemainingWindowsForTasks) {
            if (windowState.mAttrs.type != 2032) {
                unaccountedSpace.op(boundsInScreen, unaccountedSpace, Region.Op.REVERSE_DIFFERENCE);
                if ((windowState.mAttrs.flags & 40) == 0) {
                    unaccountedSpace.op(windowState.getDisplayFrameLw(), unaccountedSpace, Region.Op.REVERSE_DIFFERENCE);
                    Task task = windowState.getTask();
                    if (task != null) {
                        skipRemainingWindowsForTasks.add(Integer.valueOf(task.mTaskId));
                    } else {
                        unaccountedSpace.setEmpty();
                    }
                }
            }
        }

        private void computeWindowBoundsInScreen(WindowState windowState, Rect outBounds) {
            Region touchableRegion = this.mTempRegion1;
            windowState.getTouchableRegion(touchableRegion);
            Rect touchableFrame = this.mTempRect;
            touchableRegion.getBounds(touchableFrame);
            RectF windowFrame = this.mTempRectF;
            windowFrame.set(touchableFrame);
            windowFrame.offset((float) (-windowState.getFrameLw().left), (float) (-windowState.getFrameLw().top));
            Matrix matrix = this.mTempMatrix;
            AccessibilityController.populateTransformationMatrixLocked(windowState, matrix);
            matrix.mapRect(windowFrame);
            outBounds.set((int) windowFrame.left, (int) windowFrame.top, (int) windowFrame.right, (int) windowFrame.bottom);
        }

        private static void addPopulatedWindowInfo(WindowState windowState, Rect boundsInScreen, List<WindowInfo> out, Set<IBinder> tokenOut) {
            WindowInfo window = windowState.getWindowInfo();
            window.boundsInScreen.set(boundsInScreen);
            window.layer = tokenOut.size();
            out.add(window);
            tokenOut.add(window.token);
        }

        private void cacheWindows(List<WindowInfo> windows) {
            for (int i = this.mOldWindows.size() - 1; i >= 0; i--) {
                this.mOldWindows.remove(i).recycle();
            }
            int newWindowCount = windows.size();
            for (int i2 = 0; i2 < newWindowCount; i2++) {
                this.mOldWindows.add(WindowInfo.obtain(windows.get(i2)));
            }
        }

        private boolean windowChangedNoLayer(WindowInfo oldWindow, WindowInfo newWindow) {
            if (oldWindow == newWindow) {
                return false;
            }
            if (oldWindow == null || newWindow == null || oldWindow.type != newWindow.type || oldWindow.focused != newWindow.focused) {
                return true;
            }
            if (oldWindow.token == null) {
                if (newWindow.token != null) {
                    return true;
                }
            } else if (!oldWindow.token.equals(newWindow.token)) {
                return true;
            }
            if (oldWindow.parentToken == null) {
                if (newWindow.parentToken != null) {
                    return true;
                }
            } else if (!oldWindow.parentToken.equals(newWindow.parentToken)) {
                return true;
            }
            if (!oldWindow.boundsInScreen.equals(newWindow.boundsInScreen)) {
                return true;
            }
            if ((oldWindow.childTokens == null || newWindow.childTokens == null || oldWindow.childTokens.equals(newWindow.childTokens)) && TextUtils.equals(oldWindow.title, newWindow.title) && oldWindow.accessibilityIdOfAnchor == newWindow.accessibilityIdOfAnchor) {
                return false;
            }
            return true;
        }

        private static void clearAndRecycleWindows(List<WindowInfo> windows) {
            for (int i = windows.size() - 1; i >= 0; i--) {
                windows.remove(i).recycle();
            }
        }

        private static boolean isReportedWindowType(int windowType) {
            return (windowType == 2013 || windowType == 2021 || windowType == 2026 || windowType == 2016 || windowType == 2022 || windowType == 2018 || windowType == 2027 || windowType == 1004 || windowType == 2015 || windowType == 2030) ? false : true;
        }

        private void populateVisibleWindowsOnScreenLocked(SparseArray<WindowState> outWindows) {
            DisplayContent dc = this.mService.getDefaultDisplayContentLocked();
            this.mTempLayer = 0;
            dc.forAllWindows((Consumer<WindowState>) new Consumer(outWindows) {
                private final /* synthetic */ SparseArray f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    AccessibilityController.WindowsForAccessibilityObserver.this.lambda$populateVisibleWindowsOnScreenLocked$0$AccessibilityController$WindowsForAccessibilityObserver(this.f$1, (WindowState) obj);
                }
            }, false);
            this.mService.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(outWindows) {
                private final /* synthetic */ SparseArray f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    AccessibilityController.WindowsForAccessibilityObserver.this.lambda$populateVisibleWindowsOnScreenLocked$1$AccessibilityController$WindowsForAccessibilityObserver(this.f$1, (WindowState) obj);
                }
            }, false);
        }

        public /* synthetic */ void lambda$populateVisibleWindowsOnScreenLocked$0$AccessibilityController$WindowsForAccessibilityObserver(SparseArray outWindows, WindowState w) {
            if (w.isVisibleLw()) {
                int i = this.mTempLayer;
                this.mTempLayer = i + 1;
                outWindows.put(i, w);
            }
        }

        public /* synthetic */ void lambda$populateVisibleWindowsOnScreenLocked$1$AccessibilityController$WindowsForAccessibilityObserver(SparseArray outWindows, WindowState w) {
            WindowState win = findRootDisplayParentWindow(w);
            if (win != null && win.getDisplayContent().isDefaultDisplay && w.isVisibleLw()) {
                int i = this.mTempLayer;
                this.mTempLayer = i + 1;
                outWindows.put(i, w);
            }
        }

        private WindowState findRootDisplayParentWindow(WindowState win) {
            WindowState displayParentWindow = win.getDisplayContent().getParentWindow();
            if (displayParentWindow == null) {
                return null;
            }
            WindowState candidate = displayParentWindow;
            while (candidate != null) {
                displayParentWindow = candidate;
                candidate = displayParentWindow.getDisplayContent().getParentWindow();
            }
            return displayParentWindow;
        }

        private class MyHandler extends Handler {
            public static final int MESSAGE_COMPUTE_CHANGED_WINDOWS = 1;

            public MyHandler(Looper looper) {
                super(looper, (Handler.Callback) null, false);
            }

            public void handleMessage(Message message) {
                if (message.what == 1) {
                    WindowsForAccessibilityObserver.this.computeChangedWindows(false);
                }
            }
        }
    }
}
