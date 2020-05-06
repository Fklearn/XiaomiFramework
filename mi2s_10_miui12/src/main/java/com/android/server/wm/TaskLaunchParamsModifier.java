package com.android.server.wm;

import android.app.ActivityOptions;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.wm.LaunchParamsController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TaskLaunchParamsModifier implements LaunchParamsController.LaunchParamsModifier {
    private static final int BOUNDS_CONFLICT_THRESHOLD = 4;
    private static final int CASCADING_OFFSET_DP = 75;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_PORTRAIT_PHONE_HEIGHT_DP = 732;
    private static final int DEFAULT_PORTRAIT_PHONE_WIDTH_DP = 412;
    private static final int EPSILON = 2;
    private static final int MINIMAL_STEP = 1;
    private static final int STEP_DENOMINATOR = 16;
    private static final int SUPPORTS_SCREEN_RESIZEABLE_MASK = 539136;
    private static final String TAG = "ActivityTaskManager";
    private StringBuilder mLogBuilder;
    private final ActivityStackSupervisor mSupervisor;
    private final Rect mTmpBounds = new Rect();
    private final int[] mTmpDirections = new int[2];

    TaskLaunchParamsModifier(ActivityStackSupervisor supervisor) {
        this.mSupervisor = supervisor;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int onCalculate(TaskRecord task, ActivityInfo.WindowLayout layout, ActivityRecord activity, ActivityRecord source, ActivityOptions options, LaunchParamsController.LaunchParams currentParams, LaunchParamsController.LaunchParams outParams) {
        return onCalculate(task, layout, activity, source, options, 2, currentParams, outParams);
    }

    public int onCalculate(TaskRecord task, ActivityInfo.WindowLayout layout, ActivityRecord activity, ActivityRecord source, ActivityOptions options, int phase, LaunchParamsController.LaunchParams currentParams, LaunchParamsController.LaunchParams outParams) {
        initLogBuilder(task, activity);
        int result = calculate(task, layout, activity, source, options, phase, currentParams, outParams);
        outputLog();
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00f1 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int calculate(com.android.server.wm.TaskRecord r26, android.content.pm.ActivityInfo.WindowLayout r27, com.android.server.wm.ActivityRecord r28, com.android.server.wm.ActivityRecord r29, android.app.ActivityOptions r30, int r31, com.android.server.wm.LaunchParamsController.LaunchParams r32, com.android.server.wm.LaunchParamsController.LaunchParams r33) {
        /*
            r25 = this;
            r7 = r25
            r8 = r26
            r9 = r27
            r10 = r29
            r11 = r30
            r12 = r31
            r13 = r32
            r14 = r33
            if (r8 == 0) goto L_0x0021
            com.android.server.wm.ActivityRecord r0 = r26.getRootActivity()
            if (r0 != 0) goto L_0x001b
            r0 = r28
            goto L_0x001f
        L_0x001b:
            com.android.server.wm.ActivityRecord r0 = r26.getRootActivity()
        L_0x001f:
            r15 = r0
            goto L_0x0024
        L_0x0021:
            r0 = r28
            r15 = r0
        L_0x0024:
            r0 = 0
            if (r15 != 0) goto L_0x0028
            return r0
        L_0x0028:
            int r6 = r7.getPreferredLaunchDisplay(r8, r11, r10, r13)
            r14.mPreferredDisplayId = r6
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mSupervisor
            com.android.server.wm.RootActivityContainer r1 = r1.mRootActivityContainer
            com.android.server.wm.ActivityDisplay r5 = r1.getActivityDisplay((int) r6)
            r4 = 2
            if (r12 != 0) goto L_0x003a
            return r4
        L_0x003a:
            if (r11 == 0) goto L_0x0041
            int r1 = r30.getLaunchWindowingMode()
            goto L_0x0042
        L_0x0041:
            r1 = r0
        L_0x0042:
            r2 = 0
            boolean r16 = r7.canApplyFreeformWindowPolicy(r5, r1)
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mSupervisor
            boolean r3 = r3.canUseActivityOptionsLaunchBounds(r11)
            if (r3 == 0) goto L_0x006b
            if (r16 != 0) goto L_0x0058
            boolean r3 = r7.canApplyPipWindowPolicy(r1)
            if (r3 == 0) goto L_0x006b
        L_0x0058:
            r2 = 1
            if (r1 != 0) goto L_0x005d
            r3 = 5
            goto L_0x005e
        L_0x005d:
            r3 = r1
        L_0x005e:
            r1 = r3
            android.graphics.Rect r3 = r14.mBounds
            android.graphics.Rect r0 = r30.getLaunchBounds()
            r3.set(r0)
            r20 = r2
            goto L_0x0091
        L_0x006b:
            if (r1 != r4) goto L_0x006e
            goto L_0x008f
        L_0x006e:
            r0 = 1
            if (r1 != r0) goto L_0x0072
            goto L_0x008f
        L_0x0072:
            if (r9 == 0) goto L_0x008f
            if (r16 == 0) goto L_0x008f
            android.graphics.Rect r0 = r7.mTmpBounds
            r7.getLayoutBounds(r5, r15, r9, r0)
            android.graphics.Rect r0 = r7.mTmpBounds
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x008f
            r1 = 5
            android.graphics.Rect r0 = r14.mBounds
            android.graphics.Rect r3 = r7.mTmpBounds
            r0.set(r3)
            r0 = 1
            r20 = r0
            goto L_0x0091
        L_0x008f:
            r20 = r2
        L_0x0091:
            r0 = 0
            boolean r2 = r32.isEmpty()
            if (r2 != 0) goto L_0x00c9
            if (r20 != 0) goto L_0x00c9
            boolean r2 = r32.hasPreferredDisplay()
            if (r2 == 0) goto L_0x00a4
            int r2 = r13.mPreferredDisplayId
            if (r6 != r2) goto L_0x00c9
        L_0x00a4:
            boolean r2 = r32.hasWindowingMode()
            if (r2 == 0) goto L_0x00b3
            int r1 = r13.mWindowingMode
            r2 = 5
            if (r1 == r2) goto L_0x00b1
            r2 = 1
            goto L_0x00b2
        L_0x00b1:
            r2 = 0
        L_0x00b2:
            r0 = r2
        L_0x00b3:
            android.graphics.Rect r2 = r13.mBounds
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x00c9
            android.graphics.Rect r2 = r14.mBounds
            android.graphics.Rect r3 = r13.mBounds
            r2.set(r3)
            r2 = 5
            if (r1 != r2) goto L_0x00c9
            r0 = 1
            r21 = r0
            goto L_0x00cb
        L_0x00c9:
            r21 = r0
        L_0x00cb:
            boolean r0 = r5.inFreeformWindowingMode()
            if (r0 == 0) goto L_0x00e2
            if (r1 != r4) goto L_0x00d4
            goto L_0x00e2
        L_0x00d4:
            boolean r0 = r7.isTaskForcedMaximized(r15)
            if (r0 == 0) goto L_0x00e2
            r0 = 1
            android.graphics.Rect r1 = r14.mBounds
            r1.setEmpty()
            r3 = r0
            goto L_0x00e3
        L_0x00e2:
            r3 = r1
        L_0x00e3:
            int r0 = r5.getWindowingMode()
            if (r3 != r0) goto L_0x00eb
            r0 = 0
            goto L_0x00ec
        L_0x00eb:
            r0 = r3
        L_0x00ec:
            r14.mWindowingMode = r0
            r0 = 1
            if (r12 != r0) goto L_0x00f2
            return r4
        L_0x00f2:
            if (r3 == 0) goto L_0x00f6
            r0 = r3
            goto L_0x00fa
        L_0x00f6:
            int r0 = r5.getWindowingMode()
        L_0x00fa:
            r2 = r0
            if (r21 == 0) goto L_0x0124
            r0 = 5
            if (r2 != r0) goto L_0x0119
            int r0 = r13.mPreferredDisplayId
            if (r0 == r6) goto L_0x0109
            android.graphics.Rect r0 = r14.mBounds
            r7.adjustBoundsToFitInDisplay(r5, r0)
        L_0x0109:
            android.graphics.Rect r0 = r14.mBounds
            r7.adjustBoundsToAvoidConflictInDisplay(r5, r0)
            r18 = r2
            r19 = r3
            r22 = r4
            r23 = r5
            r24 = r6
            goto L_0x0165
        L_0x0119:
            r18 = r2
            r19 = r3
            r22 = r4
            r23 = r5
            r24 = r6
            goto L_0x0165
        L_0x0124:
            if (r10 == 0) goto L_0x0148
            boolean r0 = r29.inFreeformWindowingMode()
            if (r0 == 0) goto L_0x0148
            r0 = 5
            if (r2 != r0) goto L_0x0148
            android.graphics.Rect r0 = r14.mBounds
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0148
            int r0 = r29.getDisplayId()
            int r1 = r5.mDisplayId
            if (r0 != r1) goto L_0x0148
            android.graphics.Rect r0 = r29.getBounds()
            android.graphics.Rect r1 = r14.mBounds
            r7.cascadeBounds(r0, r5, r1)
        L_0x0148:
            android.graphics.Rect r1 = r14.mBounds
            r0 = r25
            r17 = r1
            r1 = r15
            r18 = r2
            r2 = r5
            r19 = r3
            r3 = r27
            r22 = r4
            r4 = r18
            r23 = r5
            r5 = r20
            r24 = r6
            r6 = r17
            r0.getTaskBounds(r1, r2, r3, r4, r5, r6)
        L_0x0165:
            return r22
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskLaunchParamsModifier.calculate(com.android.server.wm.TaskRecord, android.content.pm.ActivityInfo$WindowLayout, com.android.server.wm.ActivityRecord, com.android.server.wm.ActivityRecord, android.app.ActivityOptions, int, com.android.server.wm.LaunchParamsController$LaunchParams, com.android.server.wm.LaunchParamsController$LaunchParams):int");
    }

    private int getPreferredLaunchDisplay(TaskRecord task, ActivityOptions options, ActivityRecord source, LaunchParamsController.LaunchParams currentParams) {
        if (!this.mSupervisor.mService.mSupportsMultiDisplay) {
            return 0;
        }
        int displayId = -1;
        int optionLaunchId = options != null ? options.getLaunchDisplayId() : -1;
        if (optionLaunchId != -1) {
            displayId = optionLaunchId;
        }
        if (displayId == -1 && source != null && source.noDisplay) {
            displayId = source.mHandoverLaunchDisplayId;
        }
        ActivityStack stack = (displayId != -1 || task == null) ? null : task.getStack();
        if (stack != null) {
            displayId = stack.mDisplayId;
        }
        if (displayId == -1 && source != null) {
            displayId = source.getDisplayId();
        }
        if (displayId != -1 && this.mSupervisor.mRootActivityContainer.getActivityDisplay(displayId) == null) {
            displayId = currentParams.mPreferredDisplayId;
        }
        int displayId2 = displayId == -1 ? currentParams.mPreferredDisplayId : displayId;
        if (displayId2 == -1 || this.mSupervisor.mRootActivityContainer.getActivityDisplay(displayId2) == null) {
            return 0;
        }
        return displayId2;
    }

    private boolean canApplyFreeformWindowPolicy(ActivityDisplay display, int launchMode) {
        return this.mSupervisor.mService.mSupportsFreeformWindowManagement && (display.inFreeformWindowingMode() || launchMode == 5);
    }

    private boolean canApplyPipWindowPolicy(int launchMode) {
        return this.mSupervisor.mService.mSupportsPictureInPicture && launchMode == 2;
    }

    private void getLayoutBounds(ActivityDisplay display, ActivityRecord root, ActivityInfo.WindowLayout windowLayout, Rect outBounds) {
        int height;
        int width;
        float fractionOfHorizontalOffset;
        float fractionOfVerticalOffset;
        ActivityInfo.WindowLayout windowLayout2 = windowLayout;
        Rect rect = outBounds;
        int verticalGravity = windowLayout2.gravity & HdmiCecKeycode.UI_BROADCAST_DIGITAL_CABLE;
        int horizontalGravity = windowLayout2.gravity & 7;
        if (!windowLayout.hasSpecifiedSize() && verticalGravity == 0 && horizontalGravity == 0) {
            outBounds.setEmpty();
            return;
        }
        Rect bounds = display.getBounds();
        int defaultWidth = bounds.width();
        int defaultHeight = bounds.height();
        if (!windowLayout.hasSpecifiedSize()) {
            outBounds.setEmpty();
            getTaskBounds(root, display, windowLayout, 5, false, outBounds);
            width = outBounds.width();
            height = outBounds.height();
        } else {
            width = defaultWidth;
            if (windowLayout2.width > 0 && windowLayout2.width < defaultWidth) {
                width = windowLayout2.width;
            } else if (windowLayout2.widthFraction > 0.0f && windowLayout2.widthFraction < 1.0f) {
                width = (int) (((float) width) * windowLayout2.widthFraction);
            }
            height = defaultHeight;
            if (windowLayout2.height > 0 && windowLayout2.height < defaultHeight) {
                height = windowLayout2.height;
            } else if (windowLayout2.heightFraction > 0.0f && windowLayout2.heightFraction < 1.0f) {
                height = (int) (((float) height) * windowLayout2.heightFraction);
            }
        }
        if (horizontalGravity == 3) {
            fractionOfHorizontalOffset = 0.0f;
        } else if (horizontalGravity != 5) {
            fractionOfHorizontalOffset = 0.5f;
        } else {
            fractionOfHorizontalOffset = 1.0f;
        }
        if (verticalGravity == 48) {
            fractionOfVerticalOffset = 0.0f;
        } else if (verticalGravity != 80) {
            fractionOfVerticalOffset = 0.5f;
        } else {
            fractionOfVerticalOffset = 1.0f;
        }
        rect.set(0, 0, width, height);
        rect.offset((int) (((float) (defaultWidth - width)) * fractionOfHorizontalOffset), (int) (((float) (defaultHeight - height)) * fractionOfVerticalOffset));
    }

    private boolean isTaskForcedMaximized(ActivityRecord root) {
        if (root.appInfo.targetSdkVersion < 4 || (root.appInfo.flags & SUPPORTS_SCREEN_RESIZEABLE_MASK) == 0) {
            return true;
        }
        return !root.isResizeable();
    }

    private int resolveOrientation(ActivityRecord activity) {
        int orientation = activity.info.screenOrientation;
        if (orientation != 0) {
            if (orientation != 1) {
                if (orientation != 11) {
                    if (orientation != 12) {
                        if (orientation != 14) {
                            switch (orientation) {
                                case 5:
                                    break;
                                case 6:
                                case 8:
                                    break;
                                case 7:
                                case 9:
                                    break;
                                default:
                                    return -1;
                            }
                        }
                        return 14;
                    }
                }
            }
            return 1;
        }
        return 0;
    }

    private void cascadeBounds(Rect srcBounds, ActivityDisplay display, Rect outBounds) {
        outBounds.set(srcBounds);
        int defaultOffset = (int) ((75.0f * (((float) display.getConfiguration().densityDpi) / 160.0f)) + 0.5f);
        display.getBounds(this.mTmpBounds);
        outBounds.offset(Math.min(defaultOffset, Math.max(0, this.mTmpBounds.right - srcBounds.right)), Math.min(defaultOffset, Math.max(0, this.mTmpBounds.bottom - srcBounds.bottom)));
    }

    private void getTaskBounds(ActivityRecord root, ActivityDisplay display, ActivityInfo.WindowLayout layout, int resolvedMode, boolean hasInitialBounds, Rect inOutBounds) {
        if (resolvedMode == 1) {
            inOutBounds.setEmpty();
        } else if (resolvedMode == 5) {
            int orientation = resolveOrientation(root, display, inOutBounds);
            if (orientation == 1 || orientation == 0) {
                getDefaultFreeformSize(display, layout, orientation, this.mTmpBounds);
                if (!hasInitialBounds && !sizeMatches(inOutBounds, this.mTmpBounds)) {
                    centerBounds(display, this.mTmpBounds.width(), this.mTmpBounds.height(), inOutBounds);
                    adjustBoundsToFitInDisplay(display, inOutBounds);
                } else if (orientation != orientationFromBounds(inOutBounds)) {
                    centerBounds(display, inOutBounds.height(), inOutBounds.width(), inOutBounds);
                }
                adjustBoundsToAvoidConflictInDisplay(display, inOutBounds);
                return;
            }
            throw new IllegalStateException("Orientation must be one of portrait or landscape, but it's " + ActivityInfo.screenOrientationToString(orientation));
        }
    }

    private int convertOrientationToScreenOrientation(int orientation) {
        if (orientation == 1) {
            return 1;
        }
        if (orientation != 2) {
            return -1;
        }
        return 0;
    }

    private int resolveOrientation(ActivityRecord root, ActivityDisplay display, Rect bounds) {
        int orientation;
        int i;
        int orientation2 = resolveOrientation(root);
        if (orientation2 == 14) {
            if (bounds.isEmpty()) {
                i = convertOrientationToScreenOrientation(display.getConfiguration().orientation);
            } else {
                i = orientationFromBounds(bounds);
            }
            orientation2 = i;
        }
        if (orientation2 != -1) {
            return orientation2;
        }
        if (bounds.isEmpty()) {
            orientation = 1;
        } else {
            orientation = orientationFromBounds(bounds);
        }
        return orientation;
    }

    private void getDefaultFreeformSize(ActivityDisplay display, ActivityInfo.WindowLayout layout, int orientation, Rect bounds) {
        int defaultWidth;
        int defaultHeight;
        int phoneWidth;
        int phoneHeight;
        ActivityInfo.WindowLayout windowLayout = layout;
        Rect displayBounds = display.getBounds();
        int portraitHeight = Math.min(displayBounds.width(), displayBounds.height());
        int portraitWidth = (portraitHeight * portraitHeight) / Math.max(displayBounds.width(), displayBounds.height());
        if (orientation == 0) {
            defaultWidth = portraitHeight;
        } else {
            defaultWidth = portraitWidth;
        }
        if (orientation == 0) {
            defaultHeight = portraitWidth;
        } else {
            defaultHeight = portraitHeight;
        }
        float density = ((float) display.getConfiguration().densityDpi) / 160.0f;
        int phonePortraitWidth = (int) ((412.0f * density) + 0.5f);
        int phonePortraitHeight = (int) ((732.0f * density) + 0.5f);
        if (orientation == 0) {
            phoneWidth = phonePortraitHeight;
        } else {
            phoneWidth = phonePortraitWidth;
        }
        if (orientation == 0) {
            phoneHeight = phonePortraitWidth;
        } else {
            phoneHeight = phonePortraitHeight;
        }
        int layoutMinHeight = -1;
        int layoutMinWidth = windowLayout == null ? -1 : windowLayout.minWidth;
        if (windowLayout != null) {
            layoutMinHeight = windowLayout.minHeight;
        }
        Rect rect = displayBounds;
        bounds.set(0, 0, Math.min(defaultWidth, Math.max(phoneWidth, layoutMinWidth)), Math.min(defaultHeight, Math.max(phoneHeight, layoutMinHeight)));
    }

    private void centerBounds(ActivityDisplay display, int width, int height, Rect inOutBounds) {
        if (inOutBounds.isEmpty()) {
            display.getBounds(inOutBounds);
        }
        int left = inOutBounds.centerX() - (width / 2);
        int top = inOutBounds.centerY() - (height / 2);
        inOutBounds.set(left, top, left + width, top + height);
    }

    private void adjustBoundsToFitInDisplay(ActivityDisplay display, Rect inOutBounds) {
        int left;
        int dx;
        int dy;
        Rect displayBounds = display.getBounds();
        if (displayBounds.width() < inOutBounds.width() || displayBounds.height() < inOutBounds.height()) {
            if (this.mSupervisor.mRootActivityContainer.getConfiguration().getLayoutDirection() == 1) {
                left = displayBounds.width() - inOutBounds.width();
            } else {
                left = 0;
            }
            inOutBounds.offsetTo(left, 0);
            return;
        }
        if (inOutBounds.right > displayBounds.right) {
            dx = displayBounds.right - inOutBounds.right;
        } else if (inOutBounds.left < displayBounds.left) {
            dx = displayBounds.left - inOutBounds.left;
        } else {
            dx = 0;
        }
        if (inOutBounds.top < displayBounds.top) {
            dy = displayBounds.top - inOutBounds.top;
        } else if (inOutBounds.bottom > displayBounds.bottom) {
            dy = displayBounds.bottom - inOutBounds.bottom;
        } else {
            dy = 0;
        }
        inOutBounds.offset(dx, dy);
    }

    private void adjustBoundsToAvoidConflictInDisplay(ActivityDisplay display, Rect inOutBounds) {
        List<Rect> taskBoundsToCheck = new ArrayList<>();
        for (int i = 0; i < display.getChildCount(); i++) {
            ActivityStack stack = display.getChildAt(i);
            if (stack.inFreeformWindowingMode()) {
                for (int j = 0; j < stack.getChildCount(); j++) {
                    taskBoundsToCheck.add(stack.getChildAt(j).getBounds());
                }
            }
        }
        adjustBoundsToAvoidConflict(display.getBounds(), taskBoundsToCheck, inOutBounds);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void adjustBoundsToAvoidConflict(Rect displayBounds, List<Rect> taskBoundsToCheck, Rect inOutBounds) {
        if (displayBounds.contains(inOutBounds) && boundsConflict(taskBoundsToCheck, inOutBounds)) {
            calculateCandidateShiftDirections(displayBounds, inOutBounds);
            int[] iArr = this.mTmpDirections;
            int length = iArr.length;
            int i = 0;
            while (i < length) {
                int direction = iArr[i];
                if (direction != 0) {
                    this.mTmpBounds.set(inOutBounds);
                    while (boundsConflict(taskBoundsToCheck, this.mTmpBounds) && displayBounds.contains(this.mTmpBounds)) {
                        shiftBounds(direction, displayBounds, this.mTmpBounds);
                    }
                    if (boundsConflict(taskBoundsToCheck, this.mTmpBounds) || !displayBounds.contains(this.mTmpBounds)) {
                        i++;
                    } else {
                        inOutBounds.set(this.mTmpBounds);
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void calculateCandidateShiftDirections(Rect availableBounds, Rect initialBounds) {
        int i = 0;
        while (true) {
            int[] iArr = this.mTmpDirections;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = 0;
            i++;
        }
        int oneThirdWidth = ((availableBounds.left * 2) + availableBounds.right) / 3;
        int twoThirdWidth = (availableBounds.left + (availableBounds.right * 2)) / 3;
        int centerX = initialBounds.centerX();
        if (centerX < oneThirdWidth) {
            this.mTmpDirections[0] = 5;
        } else if (centerX > twoThirdWidth) {
            this.mTmpDirections[0] = 3;
        } else {
            int oneThirdHeight = ((availableBounds.top * 2) + availableBounds.bottom) / 3;
            int twoThirdHeight = (availableBounds.top + (availableBounds.bottom * 2)) / 3;
            int centerY = initialBounds.centerY();
            if (centerY < oneThirdHeight || centerY > twoThirdHeight) {
                int[] iArr2 = this.mTmpDirections;
                iArr2[0] = 5;
                iArr2[1] = 3;
                return;
            }
            int[] iArr3 = this.mTmpDirections;
            iArr3[0] = 85;
            iArr3[1] = 51;
        }
    }

    private boolean boundsConflict(List<Rect> taskBoundsToCheck, Rect candidateBounds) {
        Iterator<Rect> it = taskBoundsToCheck.iterator();
        while (true) {
            boolean bottomClose = false;
            if (!it.hasNext()) {
                return false;
            }
            Rect taskBounds = it.next();
            boolean leftClose = Math.abs(taskBounds.left - candidateBounds.left) < 4;
            boolean topClose = Math.abs(taskBounds.top - candidateBounds.top) < 4;
            boolean rightClose = Math.abs(taskBounds.right - candidateBounds.right) < 4;
            if (Math.abs(taskBounds.bottom - candidateBounds.bottom) < 4) {
                bottomClose = true;
            }
            if ((!leftClose || !topClose) && ((!leftClose || !bottomClose) && ((!rightClose || !topClose) && (!rightClose || !bottomClose)))) {
            }
        }
        return true;
    }

    private void shiftBounds(int direction, Rect availableRect, Rect inOutBounds) {
        int horizontalOffset;
        int verticalOffset;
        int i = direction & 7;
        if (i == 3) {
            horizontalOffset = -Math.max(1, availableRect.width() / 16);
        } else if (i != 5) {
            horizontalOffset = 0;
        } else {
            horizontalOffset = Math.max(1, availableRect.width() / 16);
        }
        int i2 = direction & HdmiCecKeycode.UI_BROADCAST_DIGITAL_CABLE;
        if (i2 == 48) {
            verticalOffset = -Math.max(1, availableRect.height() / 16);
        } else if (i2 != 80) {
            verticalOffset = 0;
        } else {
            verticalOffset = Math.max(1, availableRect.height() / 16);
        }
        inOutBounds.offset(horizontalOffset, verticalOffset);
    }

    private void initLogBuilder(TaskRecord task, ActivityRecord activity) {
    }

    private void appendLog(String log) {
    }

    private void outputLog() {
    }

    private static int orientationFromBounds(Rect bounds) {
        if (bounds.width() > bounds.height()) {
            return 0;
        }
        return 1;
    }

    private static boolean sizeMatches(Rect left, Rect right) {
        return Math.abs(right.width() - left.width()) < 2 && Math.abs(right.height() - left.height()) < 2;
    }
}
