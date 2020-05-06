package com.android.server.wm;

import android.app.RemoteAction;
import android.app.WindowConfiguration;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DockedDividerUtils;
import com.android.server.EventLogTags;
import com.android.server.wm.BoundsAnimationController;
import com.android.server.wm.DisplayContent;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TaskStack extends WindowContainer<Task> implements BoundsAnimationTarget, ConfigurationContainerListener {
    private static final float ADJUSTED_STACK_FRACTION_MIN = 0.3f;
    private static final float IME_ADJUST_DIM_AMOUNT = 0.25f;
    ActivityStack mActivityStack;
    private float mAdjustDividerAmount;
    private float mAdjustImeAmount;
    private final Rect mAdjustedBounds = new Rect();
    private boolean mAdjustedForIme;
    private final AnimatingAppWindowTokenRegistry mAnimatingAppWindowTokenRegistry = new AnimatingAppWindowTokenRegistry();
    private WindowStateAnimator mAnimationBackgroundAnimator;
    private SurfaceControl mAnimationBackgroundSurface;
    private boolean mAnimationBackgroundSurfaceIsShown = false;
    @BoundsAnimationController.AnimationType
    private int mAnimationType;
    private boolean mBoundsAnimating = false;
    private boolean mBoundsAnimatingRequested = false;
    private boolean mBoundsAnimatingToFullscreen = false;
    private Rect mBoundsAnimationSourceHintBounds = new Rect();
    private Rect mBoundsAnimationTarget = new Rect();
    private boolean mCancelCurrentBoundsAnimation = false;
    boolean mDeferRemoval;
    private Dimmer mDimmer = new Dimmer(this);
    private final int mDockedStackMinimizeThickness;
    final AppTokenList mExitingAppTokens = new AppTokenList();
    private final Rect mFullyAdjustedImeBounds = new Rect();
    private boolean mImeGoingAway;
    private WindowState mImeWin;
    private boolean mKeepScaled = false;
    private final Point mLastSurfaceSize = new Point();
    private float mMinimizeAmount;
    Rect mPreAnimationBounds = new Rect();
    final int mStackId;
    private final Rect mTmpAdjustedBounds = new Rect();
    final AppTokenList mTmpAppTokens = new AppTokenList();
    final Rect mTmpDimBoundsRect = new Rect();
    private Rect mTmpFromBounds = new Rect();
    private Rect mTmpRect = new Rect();
    private Rect mTmpRect2 = new Rect();
    private Rect mTmpRect3 = new Rect();
    private Rect mTmpToBounds = new Rect();

    public /* bridge */ /* synthetic */ void commitPendingTransaction() {
        super.commitPendingTransaction();
    }

    public /* bridge */ /* synthetic */ int compareTo(WindowContainer windowContainer) {
        return super.compareTo(windowContainer);
    }

    public /* bridge */ /* synthetic */ SurfaceControl getAnimationLeashParent() {
        return super.getAnimationLeashParent();
    }

    public /* bridge */ /* synthetic */ int getMiuiConfigFlag() {
        return super.getMiuiConfigFlag();
    }

    public /* bridge */ /* synthetic */ boolean getMiuiProjection() {
        return super.getMiuiProjection();
    }

    public /* bridge */ /* synthetic */ SurfaceControl getParentSurfaceControl() {
        return super.getParentSurfaceControl();
    }

    public /* bridge */ /* synthetic */ SurfaceControl.Transaction getPendingTransaction() {
        return super.getPendingTransaction();
    }

    public /* bridge */ /* synthetic */ SurfaceControl getSurfaceControl() {
        return super.getSurfaceControl();
    }

    public /* bridge */ /* synthetic */ int getSurfaceHeight() {
        return super.getSurfaceHeight();
    }

    public /* bridge */ /* synthetic */ int getSurfaceWidth() {
        return super.getSurfaceWidth();
    }

    public /* bridge */ /* synthetic */ SurfaceControl.Builder makeAnimationLeash() {
        return super.makeAnimationLeash();
    }

    public /* bridge */ /* synthetic */ void onAnimationLeashCreated(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        super.onAnimationLeashCreated(transaction, surfaceControl);
    }

    public /* bridge */ /* synthetic */ void onAnimationLeashLost(SurfaceControl.Transaction transaction) {
        super.onAnimationLeashLost(transaction);
    }

    public /* bridge */ /* synthetic */ void onRequestedOverrideConfigurationChanged(Configuration configuration) {
        super.onRequestedOverrideConfigurationChanged(configuration);
    }

    public /* bridge */ /* synthetic */ void setMiuiProjection(boolean z) {
        super.setMiuiProjection(z);
    }

    TaskStack(WindowManagerService service, int stackId, ActivityStack activityStack) {
        super(service);
        this.mStackId = stackId;
        this.mActivityStack = activityStack;
        activityStack.registerConfigurationChangeListener(this);
        this.mDockedStackMinimizeThickness = service.mContext.getResources().getDimensionPixelSize(17105144);
        EventLog.writeEvent(EventLogTags.WM_STACK_CREATED, stackId);
    }

    /* access modifiers changed from: package-private */
    public Task findHomeTask() {
        if (!isActivityTypeHome() || this.mChildren.isEmpty()) {
            return null;
        }
        return (Task) this.mChildren.get(this.mChildren.size() - 1);
    }

    /* access modifiers changed from: package-private */
    public void prepareFreezingTaskBounds() {
        for (int taskNdx = this.mChildren.size() - 1; taskNdx >= 0; taskNdx--) {
            ((Task) this.mChildren.get(taskNdx)).prepareFreezingBounds();
        }
    }

    private void setAdjustedBounds(Rect bounds) {
        if (!this.mAdjustedBounds.equals(bounds) || isAnimatingForIme()) {
            this.mAdjustedBounds.set(bounds);
            boolean adjusted = !this.mAdjustedBounds.isEmpty();
            Rect insetBounds = null;
            if (adjusted && isAdjustedForMinimizedDockedStack()) {
                insetBounds = getRawBounds();
            } else if (adjusted && this.mAdjustedForIme) {
                insetBounds = this.mImeGoingAway ? getRawBounds() : this.mFullyAdjustedImeBounds;
            }
            alignTasksToAdjustedBounds(adjusted ? this.mAdjustedBounds : getRawBounds(), insetBounds);
            this.mDisplayContent.setLayoutNeeded();
            updateSurfaceBounds();
        }
    }

    private void alignTasksToAdjustedBounds(Rect adjustedBounds, Rect tempInsetBounds) {
        if (!matchParentBounds()) {
            boolean alignBottom = this.mAdjustedForIme && getDockSide() == 2;
            for (int taskNdx = this.mChildren.size() - 1; taskNdx >= 0; taskNdx--) {
                ((Task) this.mChildren.get(taskNdx)).alignToAdjustedBounds(adjustedBounds, tempInsetBounds, alignBottom);
            }
        }
    }

    private void updateAnimationBackgroundBounds() {
        if (this.mAnimationBackgroundSurface != null) {
            getRawBounds(this.mTmpRect);
            Rect stackBounds = getBounds();
            getPendingTransaction().setWindowCrop(this.mAnimationBackgroundSurface, this.mTmpRect.width(), this.mTmpRect.height()).setPosition(this.mAnimationBackgroundSurface, (float) (this.mTmpRect.left - stackBounds.left), (float) (this.mTmpRect.top - stackBounds.top));
            scheduleAnimation();
        }
    }

    private void hideAnimationSurface() {
        if (this.mAnimationBackgroundSurface != null) {
            getPendingTransaction().hide(this.mAnimationBackgroundSurface);
            this.mAnimationBackgroundSurfaceIsShown = false;
            scheduleAnimation();
        }
    }

    private void showAnimationSurface(float alpha) {
        if (this.mAnimationBackgroundSurface != null) {
            getPendingTransaction().setLayer(this.mAnimationBackgroundSurface, Integer.MIN_VALUE).setAlpha(this.mAnimationBackgroundSurface, alpha).show(this.mAnimationBackgroundSurface);
            this.mAnimationBackgroundSurfaceIsShown = true;
            scheduleAnimation();
        }
    }

    public int setBounds(Rect bounds) {
        return setBounds(getRequestedOverrideBounds(), bounds);
    }

    private int setBounds(Rect existing, Rect bounds) {
        if (equivalentBounds(existing, bounds)) {
            return 0;
        }
        int result = super.setBounds(bounds);
        if (getParent() != null) {
            updateAnimationBackgroundBounds();
        }
        updateAdjustedBounds();
        updateSurfaceBounds();
        return result;
    }

    /* access modifiers changed from: package-private */
    public void getRawBounds(Rect out) {
        out.set(getRawBounds());
    }

    /* access modifiers changed from: package-private */
    public Rect getRawBounds() {
        return super.getBounds();
    }

    public void getBounds(Rect bounds) {
        bounds.set(getBounds());
    }

    public Rect getBounds() {
        if (!this.mAdjustedBounds.isEmpty()) {
            return this.mAdjustedBounds;
        }
        return super.getBounds();
    }

    private void setAnimationFinalBounds(Rect sourceHintBounds, Rect destBounds, boolean toFullscreen) {
        this.mBoundsAnimatingRequested = true;
        this.mBoundsAnimatingToFullscreen = toFullscreen;
        if (destBounds != null) {
            this.mBoundsAnimationTarget.set(destBounds);
        } else {
            this.mBoundsAnimationTarget.setEmpty();
        }
        if (sourceHintBounds != null) {
            this.mBoundsAnimationSourceHintBounds.set(sourceHintBounds);
        } else if (!this.mBoundsAnimating) {
            this.mBoundsAnimationSourceHintBounds.setEmpty();
        }
        this.mPreAnimationBounds.set(getRawBounds());
    }

    /* access modifiers changed from: package-private */
    public void getFinalAnimationBounds(Rect outBounds) {
        outBounds.set(this.mBoundsAnimationTarget);
    }

    /* access modifiers changed from: package-private */
    public void getFinalAnimationSourceHintBounds(Rect outBounds) {
        outBounds.set(this.mBoundsAnimationSourceHintBounds);
    }

    /* access modifiers changed from: package-private */
    public void getAnimationOrCurrentBounds(Rect outBounds) {
        if ((this.mBoundsAnimatingRequested || this.mBoundsAnimating) && !this.mBoundsAnimationTarget.isEmpty()) {
            getFinalAnimationBounds(outBounds);
        } else {
            getBounds(outBounds);
        }
    }

    public void getDimBounds(Rect out) {
        getBounds(out);
    }

    /* access modifiers changed from: package-private */
    public boolean calculatePinnedBoundsForConfigChange(Rect inOutBounds) {
        boolean animating = false;
        if ((this.mBoundsAnimatingRequested || this.mBoundsAnimating) && !this.mBoundsAnimationTarget.isEmpty()) {
            animating = true;
            getFinalAnimationBounds(this.mTmpRect2);
        } else {
            this.mTmpRect2.set(inOutBounds);
        }
        boolean updated = this.mDisplayContent.mPinnedStackControllerLocked.onTaskStackBoundsChanged(this.mTmpRect2, this.mTmpRect3);
        if (updated) {
            inOutBounds.set(this.mTmpRect3);
            if (animating && !inOutBounds.equals(this.mBoundsAnimationTarget)) {
                DisplayContent displayContent = getDisplayContent();
                displayContent.mBoundsAnimationController.getHandler().post(new Runnable(displayContent) {
                    private final /* synthetic */ DisplayContent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TaskStack.this.lambda$calculatePinnedBoundsForConfigChange$0$TaskStack(this.f$1);
                    }
                });
            }
            this.mBoundsAnimationTarget.setEmpty();
            this.mBoundsAnimationSourceHintBounds.setEmpty();
            this.mCancelCurrentBoundsAnimation = true;
        }
        return updated;
    }

    public /* synthetic */ void lambda$calculatePinnedBoundsForConfigChange$0$TaskStack(DisplayContent displayContent) {
        displayContent.mBoundsAnimationController.cancel(this);
    }

    /* access modifiers changed from: package-private */
    public void calculateDockedBoundsForConfigChange(Configuration parentConfig, Rect inOutBounds) {
        int i = 0;
        boolean primary = getRequestedOverrideWindowingMode() == 3;
        repositionSplitScreenStackAfterRotation(parentConfig, primary, inOutBounds);
        snapDockedStackAfterRotation(parentConfig, this.mDisplayContent.getDisplayInfo().displayCutout, inOutBounds);
        if (primary) {
            int newDockSide = getDockSide(parentConfig, inOutBounds);
            WindowManagerService windowManagerService = this.mWmService;
            if (!(newDockSide == 1 || newDockSide == 2)) {
                i = 1;
            }
            windowManagerService.setDockedStackCreateStateLocked(i, (Rect) null);
            this.mDisplayContent.getDockedDividerController().notifyDockSideChanged(newDockSide);
        }
    }

    /* access modifiers changed from: package-private */
    public void repositionSplitScreenStackAfterRotation(Configuration parentConfig, boolean primary, Rect inOutBounds) {
        int dockSide = getDockSide(this.mDisplayContent, parentConfig, inOutBounds);
        int otherDockSide = DockedDividerUtils.invertDockSide(dockSide);
        if (!this.mDisplayContent.getDockedDividerController().canPrimaryStackDockTo(primary ? dockSide : otherDockSide, parentConfig.windowConfiguration.getBounds(), parentConfig.windowConfiguration.getRotation())) {
            Rect parentBounds = parentConfig.windowConfiguration.getBounds();
            if (otherDockSide == 1) {
                int movement = inOutBounds.left;
                inOutBounds.left -= movement;
                inOutBounds.right -= movement;
            } else if (otherDockSide == 2) {
                int movement2 = inOutBounds.top;
                inOutBounds.top -= movement2;
                inOutBounds.bottom -= movement2;
            } else if (otherDockSide == 3) {
                int movement3 = parentBounds.right - inOutBounds.right;
                inOutBounds.left += movement3;
                inOutBounds.right += movement3;
            } else if (otherDockSide == 4) {
                int movement4 = parentBounds.bottom - inOutBounds.bottom;
                inOutBounds.top += movement4;
                inOutBounds.bottom += movement4;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void snapDockedStackAfterRotation(Configuration parentConfig, DisplayCutout displayCutout, Rect outBounds) {
        Configuration configuration = parentConfig;
        Rect rect = outBounds;
        int dividerSize = this.mDisplayContent.getDockedDividerController().getContentWidth();
        int dockSide = getDockSide(configuration, rect);
        int dividerPosition = DockedDividerUtils.calculatePositionForBounds(rect, dockSide, dividerSize);
        int displayWidth = configuration.windowConfiguration.getBounds().width();
        int displayHeight = configuration.windowConfiguration.getBounds().height();
        int rotation = configuration.windowConfiguration.getRotation();
        int orientation = configuration.orientation;
        this.mDisplayContent.getDisplayPolicy().getStableInsetsLw(rotation, displayWidth, displayHeight, displayCutout, outBounds);
        Resources resources = this.mWmService.mContext.getResources();
        boolean z = true;
        if (orientation != 1) {
            z = false;
        }
        int i = orientation;
        DockedDividerUtils.calculateBoundsForPosition(new DividerSnapAlgorithm(resources, displayWidth, displayHeight, dividerSize, z, outBounds, getDockSide(), isMinimizedDockAndHomeStackResizable()).calculateNonDismissingSnapTarget(dividerPosition).position, dockSide, outBounds, displayWidth, displayHeight, dividerSize);
    }

    /* access modifiers changed from: package-private */
    public void addTask(Task task, int position) {
        addTask(task, position, task.showForAllUsers(), true);
    }

    /* access modifiers changed from: package-private */
    public void addTask(Task task, int position, boolean showForAllUsers, boolean moveParents) {
        TaskStack currentStack = task.mStack;
        if (currentStack == null || currentStack.mStackId == this.mStackId) {
            task.mStack = this;
            addChild(task, (Comparator) null);
            positionChildAt(position, task, moveParents, showForAllUsers);
            return;
        }
        throw new IllegalStateException("Trying to add taskId=" + task.mTaskId + " to stackId=" + this.mStackId + ", but it is already attached to stackId=" + task.mStack.mStackId);
    }

    /* access modifiers changed from: package-private */
    public void positionChildAt(Task child, int position) {
        if (child != null) {
            child.positionAt(position);
            getDisplayContent().layoutAndAssignWindowLayersIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void positionChildAtTop(Task child, boolean includingParents) {
        if (child != null) {
            positionChildAt(Integer.MAX_VALUE, child, includingParents);
            DisplayContent displayContent = getDisplayContent();
            if (displayContent.mAppTransition.isTransitionSet()) {
                child.setSendingToBottom(false);
            }
            displayContent.layoutAndAssignWindowLayersIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void positionChildAtBottom(Task child, boolean includingParents) {
        if (child != null) {
            positionChildAt(Integer.MIN_VALUE, child, includingParents);
            if (getDisplayContent().mAppTransition.isTransitionSet()) {
                child.setSendingToBottom(true);
            }
            getDisplayContent().layoutAndAssignWindowLayersIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void positionChildAt(int position, Task child, boolean includingParents) {
        positionChildAt(position, child, includingParents, child.showForAllUsers());
    }

    private void positionChildAt(int position, Task child, boolean includingParents, boolean showForAllUsers) {
        int targetPosition = findPositionForTask(child, position, showForAllUsers, false);
        super.positionChildAt(targetPosition, child, includingParents);
        EventLog.writeEvent(EventLogTags.WM_TASK_MOVED, new Object[]{Integer.valueOf(child.mTaskId), Integer.valueOf(targetPosition == this.mChildren.size() - 1 ? 1 : 0), Integer.valueOf(targetPosition)});
    }

    /* access modifiers changed from: package-private */
    public void reparent(int displayId, Rect outStackBounds, boolean onTop) {
        DisplayContent targetDc = this.mWmService.mRoot.getDisplayContent(displayId);
        if (targetDc != null) {
            targetDc.moveStackToDisplay(this, onTop);
            if (matchParentBounds()) {
                outStackBounds.setEmpty();
            } else {
                getRawBounds(outStackBounds);
            }
        } else {
            throw new IllegalArgumentException("Trying to move stackId=" + this.mStackId + " to unknown displayId=" + displayId);
        }
    }

    private int findPositionForTask(Task task, int targetPosition, boolean showForAllUsers, boolean addingNew) {
        boolean canShowTask = showForAllUsers || this.mWmService.isCurrentProfileLocked(task.mUserId);
        int stackSize = this.mChildren.size();
        int minPosition = 0;
        int maxPosition = addingNew ? stackSize : stackSize - 1;
        if (canShowTask) {
            minPosition = computeMinPosition(0, stackSize);
        } else {
            maxPosition = computeMaxPosition(maxPosition);
        }
        if (targetPosition == Integer.MIN_VALUE && minPosition == 0) {
            return Integer.MIN_VALUE;
        }
        if (targetPosition == Integer.MAX_VALUE) {
            if (maxPosition == (addingNew ? stackSize : stackSize - 1)) {
                return Integer.MAX_VALUE;
            }
        }
        return Math.min(Math.max(targetPosition, minPosition), maxPosition);
    }

    private int computeMinPosition(int minPosition, int size) {
        while (minPosition < size) {
            Task tmpTask = (Task) this.mChildren.get(minPosition);
            if (tmpTask.showForAllUsers() || this.mWmService.isCurrentProfileLocked(tmpTask.mUserId)) {
                break;
            }
            minPosition++;
        }
        return minPosition;
    }

    private int computeMaxPosition(int maxPosition) {
        while (maxPosition > 0) {
            Task tmpTask = (Task) this.mChildren.get(maxPosition);
            if (!(tmpTask.showForAllUsers() || this.mWmService.isCurrentProfileLocked(tmpTask.mUserId))) {
                break;
            }
            maxPosition--;
        }
        return maxPosition;
    }

    /* access modifiers changed from: package-private */
    public void removeChild(Task task) {
        super.removeChild(task);
        task.mStack = null;
        if (this.mDisplayContent != null) {
            if (this.mChildren.isEmpty()) {
                getParent().positionChildAt(Integer.MIN_VALUE, this, false);
            }
            this.mDisplayContent.setLayoutNeeded();
        }
        for (int appNdx = this.mExitingAppTokens.size() - 1; appNdx >= 0; appNdx--) {
            AppWindowToken wtoken = (AppWindowToken) this.mExitingAppTokens.get(appNdx);
            if (wtoken.getTask() == task) {
                wtoken.mIsExiting = false;
                this.mExitingAppTokens.remove(appNdx);
            }
        }
    }

    public void onConfigurationChanged(Configuration newParentConfig) {
        if (!this.mWmService.getCastRotationMode() || this.mWmService.getCastStackId() != this.mStackId) {
            int prevWindowingMode = getWindowingMode();
            super.onConfigurationChanged(newParentConfig);
            updateSurfaceSize(getPendingTransaction());
            int windowingMode = getWindowingMode();
            boolean isAlwaysOnTop = isAlwaysOnTop();
            if (this.mDisplayContent != null && prevWindowingMode != windowingMode) {
                this.mDisplayContent.onStackWindowingModeChanged(this);
                if (inSplitScreenSecondaryWindowingMode()) {
                    forAllWindows((Consumer<WindowState>) $$Lambda$TaskStack$PVMhxGhbT6eBbe3ARm5uodEqxDE.INSTANCE, true);
                }
            }
        }
    }

    private void updateSurfaceBounds() {
        updateSurfaceSize(getPendingTransaction());
        updateSurfacePosition();
        scheduleAnimation();
    }

    /* access modifiers changed from: package-private */
    public int getStackOutset() {
        DisplayContent displayContent = getDisplayContent();
        if (!inPinnedWindowingMode() || displayContent == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = displayContent.getDisplayMetrics();
        WindowManagerService windowManagerService = this.mWmService;
        return (int) Math.ceil((double) (WindowManagerService.dipToPixel(5, displayMetrics) * 2));
    }

    /* access modifiers changed from: package-private */
    public void getRelativeDisplayedPosition(Point outPos) {
        super.getRelativeDisplayedPosition(outPos);
        int outset = getStackOutset();
        outPos.x -= outset;
        outPos.y -= outset;
    }

    private void updateSurfaceSize(SurfaceControl.Transaction transaction) {
        if (this.mSurfaceControl != null) {
            Rect stackBounds = getDisplayedBounds();
            int width = stackBounds.width();
            int height = stackBounds.height();
            int outset = getStackOutset();
            int width2 = width + (outset * 2);
            int height2 = height + (outset * 2);
            if (width2 != this.mLastSurfaceSize.x || height2 != this.mLastSurfaceSize.y) {
                if (getWindowConfiguration().tasksAreFloating()) {
                    transaction.setWindowCrop(this.mSurfaceControl, -1, -1);
                } else {
                    transaction.setWindowCrop(this.mSurfaceControl, width2, height2);
                }
                this.mLastSurfaceSize.set(width2, height2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Point getLastSurfaceSize() {
        return this.mLastSurfaceSize;
    }

    /* access modifiers changed from: package-private */
    public void onDisplayChanged(DisplayContent dc) {
        if (this.mDisplayContent == null || this.mDisplayContent == dc) {
            super.onDisplayChanged(dc);
            updateSurfaceBounds();
            if (this.mAnimationBackgroundSurface == null) {
                SurfaceControl.Builder colorLayer = makeChildSurface((WindowContainer) null).setColorLayer();
                this.mAnimationBackgroundSurface = colorLayer.setName("animation background stackId=" + this.mStackId).build();
                return;
            }
            return;
        }
        throw new IllegalStateException("onDisplayChanged: Already attached");
    }

    /* access modifiers changed from: package-private */
    public void getStackDockedModeBoundsLocked(Configuration parentConfig, Rect dockedBounds, Rect currentTempTaskBounds, Rect outStackBounds, Rect outTempTaskBounds) {
        Rect rect = currentTempTaskBounds;
        Rect rect2 = outStackBounds;
        outTempTaskBounds.setEmpty();
        if (dockedBounds == null) {
            Configuration configuration = parentConfig;
            Rect rect3 = outTempTaskBounds;
        } else if (dockedBounds.isEmpty()) {
            Configuration configuration2 = parentConfig;
            Rect rect4 = outTempTaskBounds;
        } else {
            int dockedSide = getDockSide(parentConfig, dockedBounds);
            if (isActivityTypeHome()) {
                Task homeTask = findHomeTask();
                if (homeTask == null || !homeTask.isResizeable()) {
                    Configuration configuration3 = parentConfig;
                    outStackBounds.setEmpty();
                } else {
                    Configuration configuration4 = parentConfig;
                    getDisplayContent().mDividerControllerLocked.getHomeStackBoundsInDockedMode(parentConfig, dockedSide, rect2);
                }
                outTempTaskBounds.set(rect2);
                return;
            }
            Configuration configuration5 = parentConfig;
            Rect rect5 = outTempTaskBounds;
            if (isMinimizedDockAndHomeStackResizable() && rect != null) {
                rect2.set(rect);
                return;
            } else if (dockedSide == -1) {
                Slog.e(DisplayPolicy.TAG, "Failed to get valid docked side for docked stack");
                rect2.set(getRawBounds());
                return;
            } else {
                getStackDockedModeBounds(parentConfig, false, outStackBounds, dockedBounds, this.mDisplayContent.mDividerControllerLocked.getContentWidth(), dockedSide == 2 || dockedSide == 1);
                return;
            }
        }
        getStackDockedModeBounds(parentConfig, true, outStackBounds, dockedBounds, this.mDisplayContent.mDividerControllerLocked.getContentWidth(), this.mWmService.mDockedStackCreateMode == 0);
    }

    private void getStackDockedModeBounds(Configuration parentConfig, boolean primary, Rect outBounds, Rect dockedBounds, int dockDividerWidth, boolean dockOnTopOrLeft) {
        Configuration configuration = parentConfig;
        Rect rect = outBounds;
        Rect rect2 = dockedBounds;
        Rect displayRect = configuration.windowConfiguration.getBounds();
        boolean splitHorizontally = displayRect.width() > displayRect.height();
        rect.set(displayRect);
        if (!primary) {
            if (!dockOnTopOrLeft) {
                if (splitHorizontally) {
                    rect.right = rect2.left - dockDividerWidth;
                } else {
                    rect.bottom = rect2.top - dockDividerWidth;
                }
            } else if (splitHorizontally) {
                rect.left = rect2.right + dockDividerWidth;
            } else {
                rect.top = rect2.bottom + dockDividerWidth;
            }
            DockedDividerUtils.sanitizeStackBounds(rect, !dockOnTopOrLeft);
        } else if (this.mWmService.mDockedStackCreateBounds != null) {
            rect.set(this.mWmService.mDockedStackCreateBounds);
        } else {
            DisplayCutout displayCutout = this.mDisplayContent.getDisplayInfo().displayCutout;
            this.mDisplayContent.getDisplayPolicy().getStableInsetsLw(configuration.windowConfiguration.getRotation(), displayRect.width(), displayRect.height(), displayCutout, this.mTmpRect2);
            Resources resources = this.mWmService.mContext.getResources();
            int width = displayRect.width();
            int height = displayRect.height();
            DisplayCutout displayCutout2 = displayCutout;
            int position = new DividerSnapAlgorithm(resources, width, height, dockDividerWidth, configuration.orientation == 1, this.mTmpRect2).getMiddleTarget().position;
            if (dockOnTopOrLeft) {
                if (splitHorizontally) {
                    rect.right = position;
                } else {
                    rect.bottom = position;
                }
            } else if (splitHorizontally) {
                rect.left = position + dockDividerWidth;
            } else {
                rect.top = position + dockDividerWidth;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resetDockedStackToMiddle() {
        if (!inSplitScreenPrimaryWindowingMode()) {
            Rect dockedBounds = null;
            this.mWmService.mDockedStackCreateBounds = null;
            Rect bounds = new Rect();
            Rect tempBounds = new Rect();
            TaskStack dockedStack = this.mDisplayContent.getSplitScreenPrimaryStackIgnoringVisibility();
            if (!(dockedStack == null || dockedStack == this)) {
                dockedBounds = dockedStack.getRawBounds();
            }
            getStackDockedModeBoundsLocked(this.mDisplayContent.getConfiguration(), dockedBounds, (Rect) null, bounds, tempBounds);
            this.mActivityStack.requestResize(bounds);
            return;
        }
        throw new IllegalStateException("Not a docked stack=" + this);
    }

    /* access modifiers changed from: package-private */
    public void removeIfPossible() {
        boolean isFreeformStack = getWindowingMode() == 5;
        DisplayContent lastDc = this.mDisplayContent;
        if (isSelfOrChildAnimating()) {
            this.mDeferRemoval = true;
            return;
        }
        removeImmediately();
        resetFreeFormWindowShowedIfNeed(isFreeformStack, lastDc);
    }

    private void resetFreeFormWindowShowedIfNeed(boolean isFreeformStack, DisplayContent dc) {
        if (isFreeformStack && dc != null && !dc.isStackVisible(5)) {
            DisplayContent.mFreeFormWindowShowed = false;
            if (this.mWmService.mMiuiFreeFormGestureController != null) {
                this.mWmService.mMiuiFreeFormGestureController.startCloseFreeFormWindow();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeImmediately() {
        ActivityStack activityStack = this.mActivityStack;
        if (activityStack != null) {
            activityStack.unregisterConfigurationChangeListener(this);
        }
        super.removeImmediately();
    }

    /* access modifiers changed from: package-private */
    public void onParentChanged() {
        super.onParentChanged();
        if (getParent() == null && this.mDisplayContent != null) {
            EventLog.writeEvent(EventLogTags.WM_STACK_REMOVED, this.mStackId);
            SurfaceControl surfaceControl = this.mAnimationBackgroundSurface;
            if (surfaceControl != null) {
                surfaceControl.remove();
                this.mAnimationBackgroundSurface = null;
            }
            this.mDisplayContent = null;
            this.mWmService.mWindowPlacerLocked.requestTraversal();
        }
    }

    /* access modifiers changed from: package-private */
    public void resetAnimationBackgroundAnimator() {
        this.mAnimationBackgroundAnimator = null;
        hideAnimationSurface();
    }

    /* access modifiers changed from: package-private */
    public void setAnimationBackground(WindowStateAnimator winAnimator, int color) {
        if (this.mAnimationBackgroundAnimator == null) {
            this.mAnimationBackgroundAnimator = winAnimator;
            showAnimationSurface(((float) ((color >> 24) & 255)) / 255.0f);
        }
    }

    /* access modifiers changed from: package-private */
    public void switchUser() {
        super.switchUser();
        int top = this.mChildren.size();
        for (int taskNdx = 0; taskNdx < top; taskNdx++) {
            Task task = (Task) this.mChildren.get(taskNdx);
            if (this.mWmService.isCurrentProfileLocked(task.mUserId) || task.showForAllUsers()) {
                this.mChildren.remove(taskNdx);
                this.mChildren.add(task);
                top--;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdjustedForIme(WindowState imeWin, boolean keepLastAmount) {
        this.mImeWin = imeWin;
        this.mImeGoingAway = false;
        if (!this.mAdjustedForIme || keepLastAmount) {
            this.mAdjustedForIme = true;
            DockedStackDividerController controller = getDisplayContent().mDividerControllerLocked;
            float adjustDividerAmount = 0.0f;
            float adjustImeAmount = keepLastAmount ? controller.mLastAnimationProgress : 0.0f;
            if (keepLastAmount) {
                adjustDividerAmount = controller.mLastDividerProgress;
            }
            updateAdjustForIme(adjustImeAmount, adjustDividerAmount, true);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAdjustedForIme() {
        return this.mAdjustedForIme;
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimatingForIme() {
        WindowState windowState = this.mImeWin;
        return windowState != null && windowState.isAnimatingLw();
    }

    /* access modifiers changed from: package-private */
    public boolean updateAdjustForIme(float adjustAmount, float adjustDividerAmount, boolean force) {
        if (adjustAmount == this.mAdjustImeAmount && adjustDividerAmount == this.mAdjustDividerAmount && !force) {
            return false;
        }
        this.mAdjustImeAmount = adjustAmount;
        this.mAdjustDividerAmount = adjustDividerAmount;
        updateAdjustedBounds();
        return isVisible();
    }

    /* access modifiers changed from: package-private */
    public void resetAdjustedForIme(boolean adjustBoundsNow) {
        if (adjustBoundsNow) {
            this.mImeWin = null;
            this.mImeGoingAway = false;
            this.mAdjustImeAmount = 0.0f;
            this.mAdjustDividerAmount = 0.0f;
            if (this.mAdjustedForIme) {
                this.mAdjustedForIme = false;
                updateAdjustedBounds();
                this.mWmService.setResizeDimLayer(false, getWindowingMode(), 1.0f);
                return;
            }
            return;
        }
        this.mImeGoingAway |= this.mAdjustedForIme;
    }

    /* access modifiers changed from: package-private */
    public boolean setAdjustedForMinimizedDock(float minimizeAmount) {
        if (minimizeAmount == this.mMinimizeAmount) {
            return false;
        }
        this.mMinimizeAmount = minimizeAmount;
        updateAdjustedBounds();
        return isVisible();
    }

    /* access modifiers changed from: package-private */
    public boolean shouldIgnoreInput() {
        return isAdjustedForMinimizedDockedStack() || (inSplitScreenPrimaryWindowingMode() && isMinimizedDockAndHomeStackResizable());
    }

    /* access modifiers changed from: package-private */
    public void beginImeAdjustAnimation() {
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            Task task = (Task) this.mChildren.get(j);
            if (task.hasContentToDisplay()) {
                task.setDragResizing(true, 1);
                task.setWaitingForDrawnIfResizingChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void endImeAdjustAnimation() {
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            ((Task) this.mChildren.get(j)).setDragResizing(false, 1);
        }
    }

    /* access modifiers changed from: package-private */
    public int getMinTopStackBottom(Rect displayContentRect, int originalStackBottom) {
        return displayContentRect.top + ((int) (((float) (originalStackBottom - displayContentRect.top)) * ADJUSTED_STACK_FRACTION_MIN));
    }

    private boolean adjustForIME(WindowState imeWin) {
        if (getDisplayContent().mAppTransition.isRunning()) {
            return false;
        }
        int dockedSide = getDockSide();
        boolean dockedTopOrBottom = dockedSide == 2 || dockedSide == 4;
        if (imeWin == null) {
        } else if (!dockedTopOrBottom) {
            int i = dockedSide;
        } else {
            Rect displayStableRect = this.mTmpRect;
            Rect contentBounds = this.mTmpRect2;
            getDisplayContent().getStableRect(displayStableRect);
            contentBounds.set(displayStableRect);
            int imeTop = Math.max(imeWin.getFrameLw().top, contentBounds.top) + imeWin.getGivenContentInsetsLw().top;
            if (contentBounds.bottom > imeTop) {
                contentBounds.bottom = imeTop;
            }
            int yOffset = displayStableRect.bottom - contentBounds.bottom;
            int dividerWidth = getDisplayContent().mDividerControllerLocked.getContentWidth();
            int dividerWidthInactive = getDisplayContent().mDividerControllerLocked.getContentWidthInactive();
            if (dockedSide == 2) {
                int bottom = Math.max(((getRawBounds().bottom - yOffset) + dividerWidth) - dividerWidthInactive, getMinTopStackBottom(displayStableRect, getRawBounds().bottom));
                this.mTmpAdjustedBounds.set(getRawBounds());
                Rect rect = this.mTmpAdjustedBounds;
                float f = this.mAdjustImeAmount;
                rect.bottom = (int) ((((float) bottom) * f) + ((1.0f - f) * ((float) getRawBounds().bottom)));
                this.mFullyAdjustedImeBounds.set(getRawBounds());
                int i2 = dockedSide;
                Rect rect2 = displayStableRect;
                return true;
            }
            int top = Math.max(getRawBounds().top - yOffset, getMinTopStackBottom(displayStableRect, getRawBounds().top - dividerWidth) + dividerWidthInactive);
            this.mTmpAdjustedBounds.set(getRawBounds());
            int i3 = dockedSide;
            Rect rect3 = displayStableRect;
            this.mTmpAdjustedBounds.top = getRawBounds().top + ((int) ((this.mAdjustImeAmount * ((float) (top - ((getRawBounds().top - dividerWidth) + dividerWidthInactive)))) + (this.mAdjustDividerAmount * ((float) (dividerWidthInactive - dividerWidth)))));
            this.mFullyAdjustedImeBounds.set(getRawBounds());
            Rect rect4 = this.mFullyAdjustedImeBounds;
            rect4.top = top;
            rect4.bottom = getRawBounds().height() + top;
            return true;
        }
        return false;
    }

    private boolean adjustForMinimizedDockedStack(float minimizeAmount) {
        int dockSide = getDockSide();
        if (dockSide == -1 && !this.mTmpAdjustedBounds.isEmpty()) {
            return false;
        }
        if (dockSide == 2) {
            this.mWmService.getStableInsetsLocked(0, this.mTmpRect);
            int topInset = this.mTmpRect.top;
            this.mTmpAdjustedBounds.set(getRawBounds());
            this.mTmpAdjustedBounds.bottom = (int) ((((float) topInset) * minimizeAmount) + ((1.0f - minimizeAmount) * ((float) getRawBounds().bottom)));
        } else if (dockSide == 1) {
            this.mTmpAdjustedBounds.set(getRawBounds());
            int width = getRawBounds().width();
            this.mTmpAdjustedBounds.right = (int) ((((float) this.mDockedStackMinimizeThickness) * minimizeAmount) + ((1.0f - minimizeAmount) * ((float) getRawBounds().right)));
            Rect rect = this.mTmpAdjustedBounds;
            rect.left = rect.right - width;
        } else if (dockSide == 3) {
            this.mTmpAdjustedBounds.set(getRawBounds());
            this.mTmpAdjustedBounds.left = (int) ((((float) (getRawBounds().right - this.mDockedStackMinimizeThickness)) * minimizeAmount) + ((1.0f - minimizeAmount) * ((float) getRawBounds().left)));
        }
        return true;
    }

    private boolean isMinimizedDockAndHomeStackResizable() {
        return this.mDisplayContent.mDividerControllerLocked.isMinimizedDock() && this.mDisplayContent.mDividerControllerLocked.isHomeStackResizable();
    }

    /* access modifiers changed from: package-private */
    public int getMinimizeDistance() {
        int dockSide = getDockSide();
        if (dockSide == -1) {
            return 0;
        }
        if (dockSide == 2) {
            this.mWmService.getStableInsetsLocked(0, this.mTmpRect);
            return getRawBounds().bottom - this.mTmpRect.top;
        } else if (dockSide == 1 || dockSide == 3) {
            return getRawBounds().width() - this.mDockedStackMinimizeThickness;
        } else {
            return 0;
        }
    }

    private void updateAdjustedBounds() {
        boolean adjust = false;
        float f = this.mMinimizeAmount;
        if (f != 0.0f) {
            adjust = adjustForMinimizedDockedStack(f);
        } else if (this.mAdjustedForIme) {
            adjust = adjustForIME(this.mImeWin);
        }
        if (!adjust) {
            this.mTmpAdjustedBounds.setEmpty();
        }
        setAdjustedBounds(this.mTmpAdjustedBounds);
        boolean isImeTarget = this.mWmService.getImeFocusStackLocked() == this;
        if (this.mAdjustedForIme && adjust && !isImeTarget) {
            this.mWmService.setResizeDimLayer(true, getWindowingMode(), Math.max(this.mAdjustImeAmount, this.mAdjustDividerAmount) * IME_ADJUST_DIM_AMOUNT);
        }
    }

    /* access modifiers changed from: package-private */
    public void applyAdjustForImeIfNeeded(Task task) {
        if (this.mMinimizeAmount == 0.0f && this.mAdjustedForIme && !this.mAdjustedBounds.isEmpty()) {
            task.alignToAdjustedBounds(this.mAdjustedBounds, this.mImeGoingAway ? getRawBounds() : this.mFullyAdjustedImeBounds, getDockSide() == 2);
            this.mDisplayContent.setLayoutNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAdjustedForMinimizedDockedStack() {
        return this.mMinimizeAmount != 0.0f;
    }

    /* access modifiers changed from: package-private */
    public boolean isTaskAnimating() {
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            if (((Task) this.mChildren.get(j)).isTaskAnimating()) {
                return true;
            }
        }
        return false;
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        if (logLevel != 2 || isVisible()) {
            long token = proto.start(fieldId);
            super.writeToProto(proto, 1146756268033L, logLevel);
            proto.write(1120986464258L, this.mStackId);
            for (int taskNdx = this.mChildren.size() - 1; taskNdx >= 0; taskNdx--) {
                ((Task) this.mChildren.get(taskNdx)).writeToProto(proto, 2246267895811L, logLevel);
            }
            proto.write(1133871366148L, matchParentBounds());
            getRawBounds().writeToProto(proto, 1146756268037L);
            proto.write(1133871366150L, this.mAnimationBackgroundSurfaceIsShown);
            proto.write(1133871366151L, this.mDeferRemoval);
            proto.write(1108101562376L, this.mMinimizeAmount);
            proto.write(1133871366153L, this.mAdjustedForIme);
            proto.write(1108101562378L, this.mAdjustImeAmount);
            proto.write(1108101562379L, this.mAdjustDividerAmount);
            this.mAdjustedBounds.writeToProto(proto, 1146756268044L);
            proto.write(1133871366157L, this.mBoundsAnimating);
            proto.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        pw.println(prefix + "mStackId=" + this.mStackId);
        pw.println(prefix + "mDeferRemoval=" + this.mDeferRemoval);
        pw.println(prefix + "mBounds=" + getRawBounds().toShortString());
        if (this.mMinimizeAmount != 0.0f) {
            pw.println(prefix + "mMinimizeAmount=" + this.mMinimizeAmount);
        }
        if (this.mAdjustedForIme) {
            pw.println(prefix + "mAdjustedForIme=true");
            pw.println(prefix + "mAdjustImeAmount=" + this.mAdjustImeAmount);
            pw.println(prefix + "mAdjustDividerAmount=" + this.mAdjustDividerAmount);
        }
        if (!this.mAdjustedBounds.isEmpty()) {
            pw.println(prefix + "mAdjustedBounds=" + this.mAdjustedBounds.toShortString());
        }
        for (int taskNdx = this.mChildren.size() - 1; taskNdx >= 0; taskNdx += -1) {
            ((Task) this.mChildren.get(taskNdx)).dump(pw, prefix + "  ", dumpAll);
        }
        if (this.mAnimationBackgroundSurfaceIsShown != 0) {
            pw.println(prefix + "mWindowAnimationBackgroundSurface is shown");
        }
        if (!this.mExitingAppTokens.isEmpty()) {
            pw.println();
            pw.println("  Exiting application tokens:");
            for (int i = this.mExitingAppTokens.size() - 1; i >= 0; i--) {
                WindowToken token = (WindowToken) this.mExitingAppTokens.get(i);
                pw.print("  Exiting App #");
                pw.print(i);
                pw.print(' ');
                pw.print(token);
                pw.println(':');
                token.dump(pw, "    ", dumpAll);
            }
        }
        pw.println(prefix + "mMiuiConfigFlag=" + this.mMiuiConfigFlag);
        this.mAnimatingAppWindowTokenRegistry.dump(pw, "AnimatingApps:", prefix);
    }

    /* access modifiers changed from: package-private */
    public boolean fillsParent() {
        return matchParentBounds();
    }

    public String toString() {
        return "{stackId=" + this.mStackId + " tasks=" + this.mChildren + "}";
    }

    /* access modifiers changed from: package-private */
    public String getName() {
        return toShortString();
    }

    public String toShortString() {
        return "Stack=" + this.mStackId;
    }

    /* access modifiers changed from: package-private */
    public int getDockSide() {
        return getDockSide(this.mDisplayContent.getConfiguration(), getRawBounds());
    }

    /* access modifiers changed from: package-private */
    public int getDockSideForDisplay(DisplayContent dc) {
        return getDockSide(dc, dc.getConfiguration(), getRawBounds());
    }

    /* access modifiers changed from: package-private */
    public int getDockSide(Configuration parentConfig, Rect bounds) {
        if (this.mDisplayContent == null) {
            return -1;
        }
        return getDockSide(this.mDisplayContent, parentConfig, bounds);
    }

    private int getDockSide(DisplayContent dc, Configuration parentConfig, Rect bounds) {
        return dc.getDockedDividerController().getDockSide(bounds, parentConfig.windowConfiguration.getBounds(), parentConfig.orientation, parentConfig.windowConfiguration.getRotation());
    }

    /* access modifiers changed from: package-private */
    public boolean hasTaskForUser(int userId) {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if (((Task) this.mChildren.get(i)).mUserId == userId) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void findTaskForResizePoint(int x, int y, int delta, DisplayContent.TaskForResizePointSearchResult results) {
        if (!getWindowConfiguration().canResizeTask()) {
            results.searchDone = true;
            return;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            Task task = (Task) this.mChildren.get(i);
            if (task.getWindowingMode() == 1) {
                results.searchDone = true;
                return;
            }
            task.getDimBounds(this.mTmpRect);
            this.mTmpRect.inset(-delta, -delta);
            if (this.mTmpRect.contains(x, y)) {
                this.mTmpRect.inset(delta, delta);
                results.searchDone = true;
                if (!this.mTmpRect.contains(x, y)) {
                    results.taskForResize = task;
                    return;
                }
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setTouchExcludeRegion(Task focusedTask, int delta, Region touchExcludeRegion, Rect contentRect, Rect postExclude) {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            Task task = (Task) this.mChildren.get(i);
            AppWindowToken token = task.getTopVisibleAppToken();
            if ((token != null && token.hasContentToDisplay()) || task == focusedTask) {
                if (!task.isActivityTypeHome() || !isMinimizedDockAndHomeStackResizable()) {
                    task.getDimBounds(this.mTmpRect);
                } else {
                    this.mDisplayContent.getBounds(this.mTmpRect);
                }
                if (task == focusedTask) {
                    postExclude.set(this.mTmpRect);
                }
                boolean isFreeformed = task.inFreeformWindowingMode();
                if (task != focusedTask || isFreeformed) {
                    if (isFreeformed) {
                        this.mTmpRect.inset(-delta, -delta);
                        this.mTmpRect.intersect(contentRect);
                    }
                    touchExcludeRegion.op(this.mTmpRect, Region.Op.DIFFERENCE);
                }
            }
        }
    }

    public boolean setPinnedStackSize(Rect stackBounds, Rect tempTaskBounds) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mCancelCurrentBoundsAnimation) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                try {
                    this.mWmService.mActivityTaskManager.resizePinnedStack(stackBounds, tempTaskBounds);
                    return true;
                } catch (RemoteException e) {
                    return true;
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onAllWindowsDrawn() {
        if (inPinnedWindowingMode()) {
            this.mKeepScaled = false;
        }
        if (this.mBoundsAnimating || this.mBoundsAnimatingRequested) {
            getDisplayContent().mBoundsAnimationController.onAllWindowsDrawn();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002b, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0032, code lost:
        if (inPinnedWindowingMode() == false) goto L_0x0049;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r4.mWmService.mActivityTaskManager.notifyPinnedStackAnimationStarted();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onAnimationStart(boolean r5, boolean r6, @com.android.server.wm.BoundsAnimationController.AnimationType int r7) {
        /*
            r4 = this;
            com.android.server.wm.WindowManagerService r0 = r4.mWmService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x004a }
            boolean r1 = r4.isAttached()     // Catch:{ all -> 0x004a }
            r2 = 0
            if (r1 != 0) goto L_0x0014
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x0014:
            r4.mBoundsAnimatingRequested = r2     // Catch:{ all -> 0x004a }
            r1 = 1
            r4.mBoundsAnimating = r1     // Catch:{ all -> 0x004a }
            boolean r3 = r4.inPinnedWindowingMode()     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x0021
            r4.mKeepScaled = r2     // Catch:{ all -> 0x004a }
        L_0x0021:
            r4.mAnimationType = r7     // Catch:{ all -> 0x004a }
            if (r5 == 0) goto L_0x002a
            com.android.server.wm.-$$Lambda$TaskStack$NPerlV3pAikqmRCCx3JO0qCLTyw r3 = com.android.server.wm.$$Lambda$TaskStack$NPerlV3pAikqmRCCx3JO0qCLTyw.INSTANCE     // Catch:{ all -> 0x004a }
            r4.forAllWindows((java.util.function.Consumer<com.android.server.wm.WindowState>) r3, (boolean) r2)     // Catch:{ all -> 0x004a }
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            boolean r0 = r4.inPinnedWindowingMode()
            if (r0 == 0) goto L_0x0049
            com.android.server.wm.WindowManagerService r0 = r4.mWmService     // Catch:{ RemoteException -> 0x003c }
            android.app.IActivityTaskManager r0 = r0.mActivityTaskManager     // Catch:{ RemoteException -> 0x003c }
            r0.notifyPinnedStackAnimationStarted()     // Catch:{ RemoteException -> 0x003c }
            goto L_0x003d
        L_0x003c:
            r0 = move-exception
        L_0x003d:
            if (r5 != 0) goto L_0x0041
            if (r7 != r1) goto L_0x0049
        L_0x0041:
            com.android.server.wm.ActivityStack r0 = r4.mActivityStack
            if (r0 == 0) goto L_0x0049
            r2 = 0
            r0.updatePictureInPictureModeForPinnedStackAnimation(r2, r6)
        L_0x0049:
            return r1
        L_0x004a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskStack.onAnimationStart(boolean, boolean, int):boolean");
    }

    public void onAnimationEnd(boolean schedulePipModeChangedCallback, Rect finalStackSize, boolean moveToFullscreen) {
        if (inPinnedWindowingMode()) {
            this.mKeepScaled = true;
            if (schedulePipModeChangedCallback) {
                this.mActivityStack.updatePictureInPictureModeForPinnedStackAnimation(this.mBoundsAnimationTarget, false);
            }
            if (this.mAnimationType == 1) {
                setPinnedStackAlpha(1.0f);
                this.mActivityStack.mService.notifyPinnedStackAnimationEnded();
                return;
            }
            if (finalStackSize == null || this.mCancelCurrentBoundsAnimation) {
                onPipAnimationEndResize();
            } else {
                setPinnedStackSize(finalStackSize, (Rect) null);
            }
            this.mActivityStack.mService.notifyPinnedStackAnimationEnded();
            if (moveToFullscreen) {
                this.mActivityStack.mService.moveTasksToFullscreenStack(this.mStackId, true);
                return;
            }
            return;
        }
        onPipAnimationEndResize();
    }

    /* access modifiers changed from: package-private */
    public Rect getPictureInPictureBounds(float aspectRatio, Rect stackBounds) {
        DisplayContent displayContent;
        if (!this.mWmService.mSupportsPictureInPicture || (displayContent = getDisplayContent()) == null || !inPinnedWindowingMode()) {
            return null;
        }
        PinnedStackController pinnedStackController = displayContent.getPinnedStackController();
        if (stackBounds == null) {
            stackBounds = pinnedStackController.getDefaultOrLastSavedBounds();
        }
        if (pinnedStackController.isValidPictureInPictureAspectRatio(aspectRatio)) {
            return pinnedStackController.transformBoundsToAspectRatio(stackBounds, aspectRatio, true);
        }
        return stackBounds;
    }

    /* access modifiers changed from: package-private */
    public void animateResizePinnedStack(Rect toBounds, Rect sourceHintBounds, int animationDuration, boolean fromFullscreen) {
        int schedulePipModeChangedState;
        Rect toBounds2;
        int intendedAnimationType;
        if (inPinnedWindowingMode()) {
            this.mWmService.mMiuiGestureController.ensurePinnedStackVisible(this);
            Rect fromBounds = new Rect();
            getBounds(fromBounds);
            boolean toFullscreen = toBounds == null;
            if (toFullscreen) {
                if (!fromFullscreen) {
                    this.mWmService.getStackBounds(1, 1, this.mTmpToBounds);
                    if (!this.mTmpToBounds.isEmpty()) {
                        schedulePipModeChangedState = 1;
                        toBounds2 = new Rect(this.mTmpToBounds);
                    } else {
                        Rect toBounds3 = new Rect();
                        getDisplayContent().getBounds(toBounds3);
                        schedulePipModeChangedState = 1;
                        toBounds2 = toBounds3;
                    }
                } else {
                    throw new IllegalArgumentException("Should not defer scheduling PiP mode change on animation to fullscreen.");
                }
            } else if (fromFullscreen) {
                toBounds2 = toBounds;
                schedulePipModeChangedState = 2;
            } else {
                toBounds2 = toBounds;
                schedulePipModeChangedState = 0;
            }
            setAnimationFinalBounds(sourceHintBounds, toBounds2, toFullscreen);
            Rect finalToBounds = toBounds2;
            int finalSchedulePipModeChangedState = schedulePipModeChangedState;
            DisplayContent displayContent = getDisplayContent();
            int intendedAnimationType2 = displayContent.mBoundsAnimationController.getAnimationType();
            if (intendedAnimationType2 == 1) {
                if (fromFullscreen) {
                    setPinnedStackAlpha(0.0f);
                }
                if (toBounds2.width() == fromBounds.width() && toBounds2.height() == fromBounds.height()) {
                    intendedAnimationType = 0;
                    this.mCancelCurrentBoundsAnimation = false;
                    $$Lambda$TaskStack$Vzix6ElfYqr96C0Kgjxo_MdVpAg r10 = r0;
                    Rect rect = fromBounds;
                    Handler handler = displayContent.mBoundsAnimationController.getHandler();
                    DisplayContent displayContent2 = displayContent;
                    $$Lambda$TaskStack$Vzix6ElfYqr96C0Kgjxo_MdVpAg r0 = new Runnable(displayContent, fromBounds, finalToBounds, animationDuration, finalSchedulePipModeChangedState, fromFullscreen, toFullscreen, intendedAnimationType) {
                        private final /* synthetic */ DisplayContent f$1;
                        private final /* synthetic */ Rect f$2;
                        private final /* synthetic */ Rect f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ int f$5;
                        private final /* synthetic */ boolean f$6;
                        private final /* synthetic */ boolean f$7;
                        private final /* synthetic */ int f$8;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                        }

                        public final void run() {
                            TaskStack.this.lambda$animateResizePinnedStack$3$TaskStack(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                        }
                    };
                    handler.post(r10);
                }
            }
            intendedAnimationType = intendedAnimationType2;
            this.mCancelCurrentBoundsAnimation = false;
            $$Lambda$TaskStack$Vzix6ElfYqr96C0Kgjxo_MdVpAg r102 = r0;
            Rect rect2 = fromBounds;
            Handler handler2 = displayContent.mBoundsAnimationController.getHandler();
            DisplayContent displayContent22 = displayContent;
            $$Lambda$TaskStack$Vzix6ElfYqr96C0Kgjxo_MdVpAg r02 = new Runnable(displayContent, fromBounds, finalToBounds, animationDuration, finalSchedulePipModeChangedState, fromFullscreen, toFullscreen, intendedAnimationType) {
                private final /* synthetic */ DisplayContent f$1;
                private final /* synthetic */ Rect f$2;
                private final /* synthetic */ Rect f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ boolean f$7;
                private final /* synthetic */ int f$8;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                }

                public final void run() {
                    TaskStack.this.lambda$animateResizePinnedStack$3$TaskStack(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                }
            };
            handler2.post(r102);
        }
    }

    public /* synthetic */ void lambda$animateResizePinnedStack$3$TaskStack(DisplayContent displayContent, Rect fromBounds, Rect finalToBounds, int animationDuration, int finalSchedulePipModeChangedState, boolean fromFullscreen, boolean toFullscreen, int animationType) {
        displayContent.mBoundsAnimationController.animateBounds(this, fromBounds, finalToBounds, animationDuration, finalSchedulePipModeChangedState, fromFullscreen, toFullscreen, animationType);
    }

    /* access modifiers changed from: package-private */
    public void setPictureInPictureAspectRatio(float aspectRatio) {
        if (this.mWmService.mSupportsPictureInPicture && inPinnedWindowingMode()) {
            PinnedStackController pinnedStackController = getDisplayContent().getPinnedStackController();
            if (Float.compare(aspectRatio, pinnedStackController.getAspectRatio()) != 0) {
                getAnimationOrCurrentBounds(this.mTmpFromBounds);
                this.mTmpToBounds.set(this.mTmpFromBounds);
                getPictureInPictureBounds(aspectRatio, this.mTmpToBounds);
                if (!this.mTmpToBounds.equals(this.mTmpFromBounds)) {
                    animateResizePinnedStack(this.mTmpToBounds, (Rect) null, -1, false);
                }
                pinnedStackController.setAspectRatio(pinnedStackController.isValidPictureInPictureAspectRatio(aspectRatio) ? aspectRatio : -1.0f);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setPictureInPictureActions(List<RemoteAction> actions) {
        if (this.mWmService.mSupportsPictureInPicture && inPinnedWindowingMode()) {
            getDisplayContent().getPinnedStackController().setActions(actions);
        }
    }

    public boolean isAttached() {
        boolean z;
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                z = this.mDisplayContent != null;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public void onPipAnimationEndResize() {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mBoundsAnimating = false;
                for (int i = 0; i < this.mChildren.size(); i++) {
                    ((Task) this.mChildren.get(i)).clearPreserveNonFloatingState();
                }
                this.mWmService.requestTraversal();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0042, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0045, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldDeferStartOnMoveToFullscreen() {
        /*
            r7 = this;
            com.android.server.wm.WindowManagerService r0 = r7.mWmService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x004b }
            boolean r1 = r7.isAttached()     // Catch:{ all -> 0x004b }
            r2 = 0
            if (r1 != 0) goto L_0x0014
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x0014:
            com.android.server.wm.DisplayContent r1 = r7.mDisplayContent     // Catch:{ all -> 0x004b }
            com.android.server.wm.TaskStack r1 = r1.getHomeStack()     // Catch:{ all -> 0x004b }
            r3 = 1
            if (r1 != 0) goto L_0x0022
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x0022:
            com.android.server.wm.WindowContainer r4 = r1.getTopChild()     // Catch:{ all -> 0x004b }
            com.android.server.wm.Task r4 = (com.android.server.wm.Task) r4     // Catch:{ all -> 0x004b }
            if (r4 != 0) goto L_0x002f
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x002f:
            com.android.server.wm.AppWindowToken r5 = r4.getTopVisibleAppToken()     // Catch:{ all -> 0x004b }
            boolean r6 = r4.isVisible()     // Catch:{ all -> 0x004b }
            if (r6 == 0) goto L_0x0046
            if (r5 != 0) goto L_0x003c
            goto L_0x0046
        L_0x003c:
            boolean r6 = r5.allDrawn     // Catch:{ all -> 0x004b }
            if (r6 != 0) goto L_0x0041
            r2 = r3
        L_0x0041:
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x0046:
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x004b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskStack.shouldDeferStartOnMoveToFullscreen():boolean");
    }

    public boolean deferScheduleMultiWindowModeChanged() {
        if (!inPinnedWindowingMode()) {
            return false;
        }
        if (this.mBoundsAnimatingRequested || this.mBoundsAnimating) {
            return true;
        }
        return false;
    }

    public boolean isForceScaled() {
        return this.mBoundsAnimating || this.mKeepScaled;
    }

    public boolean isAnimatingBounds() {
        return this.mBoundsAnimating;
    }

    public boolean lastAnimatingBoundsWasToFullscreen() {
        return this.mBoundsAnimatingToFullscreen;
    }

    public boolean isAnimatingBoundsToFullscreen() {
        return isAnimatingBounds() && lastAnimatingBoundsWasToFullscreen();
    }

    public boolean pinnedStackResizeDisallowed() {
        if (!this.mBoundsAnimating || !this.mCancelCurrentBoundsAnimation) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean checkCompleteDeferredRemoval() {
        boolean z = true;
        if (isSelfOrChildAnimating()) {
            return true;
        }
        if (this.mDeferRemoval) {
            if (getWindowingMode() != 5) {
                z = false;
            }
            boolean isFreeformStack = z;
            DisplayContent lastDc = this.mDisplayContent;
            removeImmediately();
            resetFreeFormWindowShowedIfNeed(isFreeformStack, lastDc);
        }
        return super.checkCompleteDeferredRemoval();
    }

    /* access modifiers changed from: package-private */
    public int getOrientation() {
        if (canSpecifyOrientation()) {
            return super.getOrientation();
        }
        return -2;
    }

    private boolean canSpecifyOrientation() {
        int windowingMode = getWindowingMode();
        int activityType = getActivityType();
        return windowingMode == 1 || activityType == 2 || activityType == 3 || activityType == 4;
    }

    /* access modifiers changed from: package-private */
    public Dimmer getDimmer() {
        return this.mDimmer;
    }

    /* access modifiers changed from: package-private */
    public void prepareSurfaces() {
        this.mDimmer.resetDimStates();
        super.prepareSurfaces();
        getDimBounds(this.mTmpDimBoundsRect);
        this.mTmpDimBoundsRect.offsetTo(0, 0);
        if (this.mDimmer.updateDims(getPendingTransaction(), this.mTmpDimBoundsRect)) {
            scheduleAnimation();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0031, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setPinnedStackAlpha(float r6) {
        /*
            r5 = this;
            com.android.server.wm.WindowManagerService r0 = r5.mWmService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0037 }
            android.view.SurfaceControl r1 = r5.getSurfaceControl()     // Catch:{ all -> 0x0037 }
            r2 = 0
            if (r1 == 0) goto L_0x0032
            boolean r3 = r1.isValid()     // Catch:{ all -> 0x0037 }
            if (r3 != 0) goto L_0x0016
            goto L_0x0032
        L_0x0016:
            android.view.SurfaceControl$Transaction r3 = r5.getPendingTransaction()     // Catch:{ all -> 0x0037 }
            boolean r4 = r5.mCancelCurrentBoundsAnimation     // Catch:{ all -> 0x0037 }
            if (r4 == 0) goto L_0x0021
            r4 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0022
        L_0x0021:
            r4 = r6
        L_0x0022:
            r3.setAlpha(r1, r4)     // Catch:{ all -> 0x0037 }
            r5.scheduleAnimation()     // Catch:{ all -> 0x0037 }
            boolean r3 = r5.mCancelCurrentBoundsAnimation     // Catch:{ all -> 0x0037 }
            if (r3 != 0) goto L_0x002d
            r2 = 1
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r2
        L_0x0037:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskStack.setPinnedStackAlpha(float):boolean");
    }

    public DisplayInfo getDisplayInfo() {
        return this.mDisplayContent.getDisplayInfo();
    }

    /* access modifiers changed from: package-private */
    public void dim(float alpha) {
        this.mDimmer.dimAbove(getPendingTransaction(), alpha);
        scheduleAnimation();
    }

    /* access modifiers changed from: package-private */
    public void stopDimming() {
        this.mDimmer.stopDim(getPendingTransaction());
        scheduleAnimation();
    }

    /* access modifiers changed from: package-private */
    public AnimatingAppWindowTokenRegistry getAnimatingAppWindowTokenRegistry() {
        return this.mAnimatingAppWindowTokenRegistry;
    }

    /* access modifiers changed from: package-private */
    public Task getTopVisibleTask() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            Task task = (Task) this.mChildren.get(i);
            if (task.getTopVisibleAppToken() != null) {
                return task;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isReallyVisible() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            Task task = (Task) this.mChildren.get(i);
            for (int j = task.mChildren.size() - 1; j >= 0; j--) {
                AppWindowToken token = (AppWindowToken) task.mChildren.get(j);
                if (token.isVisible() && !token.mIsDummyVisible) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setMiuiConfigFlag(@WindowConfiguration.MiuiConfigFlag int miuiConfigFlag, boolean isSetToStack) {
        super.setMiuiConfigFlag(miuiConfigFlag, isSetToStack);
        if ((miuiConfigFlag & 2) != 0) {
            DisplayContent displayContent = this.mDisplayContent;
            DisplayContent.mFreeFormWindowShowed = true;
            if (this.mWmService.mMiuiFreeFormGestureController != null) {
                ActivityRecord topActivity = this.mActivityStack.getTopActivity();
                Rect activityBounds = new Rect();
                if (topActivity != null) {
                    activityBounds = topActivity.getBounds();
                }
                this.mWmService.mMiuiFreeFormGestureController.startupFreeFormWindow(activityBounds);
            }
        } else {
            DisplayContent displayContent2 = this.mDisplayContent;
            DisplayContent.mFreeFormWindowShowed = false;
            if (this.mWmService.mMiuiFreeFormGestureController != null) {
                this.mWmService.mMiuiFreeFormGestureController.startCloseFreeFormWindow();
            }
        }
        Iterator it = this.mChildren.iterator();
        while (it.hasNext()) {
            Iterator it2 = ((Task) it.next()).mChildren.iterator();
            while (it2.hasNext()) {
                ((AppWindowToken) it2.next()).setMiuiConfigFlag(miuiConfigFlag, false);
            }
        }
    }
}
