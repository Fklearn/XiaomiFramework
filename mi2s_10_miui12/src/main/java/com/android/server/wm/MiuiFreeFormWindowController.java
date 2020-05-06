package com.android.server.wm;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.miui.R;
import android.os.Handler;
import android.os.Looper;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MiuiFreeFormWindowController {
    public static int DropWindowType = -1;
    public static final int LANDCAPE_DROP_DOWN = 1;
    public static final int OPEN_CLOSE_TIP = 1;
    public static final int PORTRAIT_LANDCAPE_DROP__DOWN = 0;
    private static final int PRIVATE_FLAG_IS_ROUNDED_CORNERS_OVERLAY = 1048576;
    static final String TAG = "MiuiFreeFormWindowController";
    public static final int UNDEFINED_DROP__DOWN = -1;
    public static boolean mTipShowing = false;
    /* access modifiers changed from: private */
    public Context mContext;
    MiuiFreeFormGestureController mGestureController;
    /* access modifiers changed from: private */
    public MiuiFreeFormHotSpotView mHotSpotView;
    /* access modifiers changed from: private */
    public LayoutInflater mInflater;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mOverlayLayoutParams;
    /* access modifiers changed from: private */
    public MiuiFreeFormOverlayView mOverlayView;
    int mScreenLongSide;
    int mScreenShortSide;
    /* access modifiers changed from: private */
    public View mTipView;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mTiplayLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager = ((WindowManager) this.mContext.getSystemService("window"));
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public MiuiFreeFormWindowController(Context context, MiuiFreeFormGestureController controller) {
        this.mContext = context;
        this.mGestureController = controller;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mScreenLongSide = Math.max(controller.mDisplayContent.mBaseDisplayHeight, controller.mDisplayContent.mBaseDisplayWidth);
        this.mScreenShortSide = Math.min(controller.mDisplayContent.mBaseDisplayHeight, controller.mDisplayContent.mBaseDisplayWidth);
    }

    public WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, 2015, 1336, 1);
        lp.privateFlags = 16;
        lp.privateFlags |= 64;
        lp.privateFlags |= 1048576;
        lp.layoutInDisplayCutoutMode = 1;
        lp.gravity = 51;
        lp.y = 0;
        lp.x = 0;
        lp.setTitle("Freeform-HotSpotView");
        return lp;
    }

    public WindowManager.LayoutParams createOverlayLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, 2015, 1336, 1);
        lp.privateFlags = 16;
        lp.privateFlags |= 64;
        lp.privateFlags |= 1048576;
        lp.layoutInDisplayCutoutMode = 1;
        lp.windowAnimations = -1;
        lp.rotationAnimation = -1;
        lp.gravity = 51;
        lp.y = 0;
        lp.x = 0;
        lp.setTitle("Freeform-OverLayView");
        return lp;
    }

    public WindowManager.LayoutParams createTipLayoutParams(Point position, Point size) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(size.x, size.y, 2008, 1288, 1);
        lp.gravity = 51;
        lp.y = position.y;
        lp.x = position.x;
        lp.windowAnimations = R.style.FreeformTipWindow;
        lp.setTitle("Freeform-TipWindow");
        return lp;
    }

    public void startRemoveOverLayViewAnimation() {
        Slog.d(TAG, "startRemoveOverLayViewAnimation");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.startRemoveOverLayViewAnimation();
                }
            }
        });
    }

    public void addOverlayView() {
        Slog.d(TAG, "addOverlayView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                try {
                    if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                        try {
                            MiuiFreeFormWindowController.this.mWindowManager.removeView(MiuiFreeFormWindowController.this.mOverlayView);
                            WindowManager.LayoutParams unused = MiuiFreeFormWindowController.this.mOverlayLayoutParams = null;
                            MiuiFreeFormOverlayView unused2 = MiuiFreeFormWindowController.this.mOverlayView = null;
                        } catch (Exception e) {
                            WindowManager.LayoutParams unused3 = MiuiFreeFormWindowController.this.mOverlayLayoutParams = null;
                            MiuiFreeFormOverlayView unused4 = MiuiFreeFormWindowController.this.mOverlayView = null;
                        }
                    }
                    MiuiFreeFormOverlayView unused5 = MiuiFreeFormWindowController.this.mOverlayView = (MiuiFreeFormOverlayView) MiuiFreeFormWindowController.this.mInflater.inflate(R.layout.freeform_overlay_window, (ViewGroup) null);
                    WindowManager.LayoutParams unused6 = MiuiFreeFormWindowController.this.mOverlayLayoutParams = MiuiFreeFormWindowController.this.createOverlayLayoutParams();
                    MiuiFreeFormWindowController.this.mWindowManager.addView(MiuiFreeFormWindowController.this.mOverlayView, MiuiFreeFormWindowController.this.mOverlayLayoutParams);
                    MiuiFreeFormWindowController.this.mOverlayView.setController(MiuiFreeFormWindowController.this);
                    MiuiFreeFormWindowController.this.hideOverlayView();
                } catch (Exception e2) {
                }
            }
        });
    }

    public void updateOvleryView(int orientation) {
        Slog.d(TAG, "updateOvleryView mOverlayLayoutParams:" + this.mOverlayLayoutParams);
        WindowManager.LayoutParams layoutParams = this.mOverlayLayoutParams;
        if (layoutParams != null && this.mOverlayView != null) {
            if (orientation == 1) {
                layoutParams.screenOrientation = 1;
                layoutParams.width = this.mScreenShortSide;
                layoutParams.height = this.mScreenLongSide;
            } else {
                layoutParams.screenOrientation = 0;
                layoutParams.width = this.mScreenLongSide;
                layoutParams.height = this.mScreenShortSide;
            }
            this.mWindowManager.updateViewLayout(this.mOverlayView, this.mOverlayLayoutParams);
        }
    }

    public void removeOverlayView() {
        Slog.d(TAG, "removeOverlayView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                try {
                    if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                        MiuiFreeFormWindowController.this.mWindowManager.removeView(MiuiFreeFormWindowController.this.mOverlayView);
                        WindowManager.LayoutParams unused = MiuiFreeFormWindowController.this.mOverlayLayoutParams = null;
                        MiuiFreeFormOverlayView unused2 = MiuiFreeFormWindowController.this.mOverlayView = null;
                    }
                } catch (Exception e) {
                    WindowManager.LayoutParams unused3 = MiuiFreeFormWindowController.this.mOverlayLayoutParams = null;
                    MiuiFreeFormOverlayView unused4 = MiuiFreeFormWindowController.this.mOverlayView = null;
                }
            }
        });
    }

    public void startBorderAnimation(final boolean appear) {
        Slog.d(TAG, "startBorderAnimation");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.startBorderAnimation(appear);
                }
            }
        });
    }

    public void setStartBounds(final Rect contentBounds) {
        Slog.d(TAG, "setContentBounds=" + contentBounds);
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.setStartBounds(contentBounds);
                }
            }
        });
    }

    public void startContentAnimation(final int animationType, final String packageName) {
        Slog.d(TAG, "startContentAnimation");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.startContentAnimation(animationType, packageName);
                }
            }
        });
    }

    public void setDisableScreenRotation(boolean disableScreenRotation) {
        this.mGestureController.setDisableScreenRotation(disableScreenRotation);
    }

    public boolean isScreenRotationDisabled() {
        return this.mGestureController.isScreenRotationDisabled();
    }

    public void startShowFullScreenWindow() {
        this.mGestureController.startShowFullScreenWindow();
    }

    public void showOverlayView() {
        Slog.d(TAG, "showOverlayView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.show();
                }
            }
        });
    }

    public void hideOverlayView() {
        Slog.d(TAG, "hideOverlayView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mOverlayView != null) {
                    MiuiFreeFormWindowController.this.mOverlayView.hide();
                }
            }
        });
    }

    public void addHotSpotView() {
        Slog.d(TAG, "addHotSpotView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView == null) {
                    MiuiFreeFormWindowController miuiFreeFormWindowController = MiuiFreeFormWindowController.this;
                    WindowManager.LayoutParams unused = miuiFreeFormWindowController.mLayoutParams = miuiFreeFormWindowController.createLayoutParams();
                    MiuiFreeFormWindowController miuiFreeFormWindowController2 = MiuiFreeFormWindowController.this;
                    MiuiFreeFormHotSpotView unused2 = miuiFreeFormWindowController2.mHotSpotView = new MiuiFreeFormHotSpotView(miuiFreeFormWindowController2.mContext);
                    MiuiFreeFormWindowController.this.mWindowManager.addView(MiuiFreeFormWindowController.this.mHotSpotView, MiuiFreeFormWindowController.this.mLayoutParams);
                    MiuiFreeFormWindowController.this.mHotSpotView.hide();
                }
            }
        });
    }

    public void removeHotSpotView() {
        Slog.d(TAG, "removeHotSpotView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                try {
                    if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                        MiuiFreeFormWindowController.this.mWindowManager.removeView(MiuiFreeFormWindowController.this.mHotSpotView);
                        MiuiFreeFormHotSpotView unused = MiuiFreeFormWindowController.this.mHotSpotView = null;
                        WindowManager.LayoutParams unused2 = MiuiFreeFormWindowController.this.mLayoutParams = null;
                    }
                } catch (Exception e) {
                    MiuiFreeFormHotSpotView unused3 = MiuiFreeFormWindowController.this.mHotSpotView = null;
                    WindowManager.LayoutParams unused4 = MiuiFreeFormWindowController.this.mLayoutParams = null;
                }
            }
        });
    }

    public void showHotSpotView() {
        Slog.d(TAG, "showHotSpotView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                    MiuiFreeFormWindowController.this.mHotSpotView.show();
                }
            }
        });
    }

    public void hideHotSpotView() {
        Slog.d(TAG, "hideHotSpotView");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                    MiuiFreeFormWindowController.this.mHotSpotView.hide();
                }
            }
        });
    }

    public void enterSmallWindow() {
        Slog.d(TAG, "enterSmallWindow");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                    MiuiFreeFormWindowController.this.mHotSpotView.enterSmallWindow();
                }
            }
        });
    }

    public void outSmallWindow() {
        Slog.d(TAG, "outSmallWindow");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                    MiuiFreeFormWindowController.this.mHotSpotView.outSmallWindow();
                }
            }
        });
    }

    public void inHotSpotArea(final int hotSpotNum, final float x, final float y) {
        Slog.d(TAG, "inHotSpotArea");
        this.mainHandler.post(new Runnable() {
            public void run() {
                if (MiuiFreeFormWindowController.this.mHotSpotView != null) {
                    MiuiFreeFormWindowController.this.mHotSpotView.inHotSpotArea(hotSpotNum, x, y);
                }
            }
        });
    }

    public void showTipWindow(int type, Rect windowBounds) {
        if (type == 1) {
            addOpenCloseTipWindow(windowBounds);
        }
    }

    public void addOpenCloseTipWindow(final Rect windowBounds) {
        synchronized (this) {
            if (!mTipShowing) {
                if (windowBounds != null) {
                    mTipShowing = true;
                    this.mainHandler.post(new Runnable() {
                        public void run() {
                            MiuiFreeFormWindowController miuiFreeFormWindowController = MiuiFreeFormWindowController.this;
                            View unused = miuiFreeFormWindowController.mTipView = miuiFreeFormWindowController.mInflater.inflate(R.layout.freeform_open_close_tips_window, (ViewGroup) null);
                            Slog.d(MiuiFreeFormWindowController.TAG, "addOpenCloseTipWindow mTipView=" + MiuiFreeFormWindowController.this.mTipView);
                            if (MiuiFreeFormWindowController.this.mTipView != null) {
                                MiuiFreeFormWindowController.this.mTipView.measure(0, 0);
                                int tipHeight = MiuiFreeFormWindowController.this.mTipView.getMeasuredHeight();
                                int tipWidth = MiuiFreeFormWindowController.this.mTipView.getMeasuredWidth();
                                int i = windowBounds.top;
                                Point positon = new Point(windowBounds.left + ((((int) (((float) windowBounds.width()) * MiuiMultiWindowUtils.sScale)) - tipWidth) / 2), (int) (((((float) (i + ((int) (((float) windowBounds.height()) * MiuiMultiWindowUtils.sScale)))) - (((float) MiuiMultiWindowUtils.BOTTOM_DECOR_CAPTIONVIEW_HEIGHT) * MiuiMultiWindowUtils.sScale)) - ((float) tipHeight)) - 43.0f));
                                Point size = new Point(tipWidth, tipHeight);
                                MiuiFreeFormWindowController miuiFreeFormWindowController2 = MiuiFreeFormWindowController.this;
                                WindowManager.LayoutParams unused2 = miuiFreeFormWindowController2.mTiplayLayoutParams = miuiFreeFormWindowController2.createTipLayoutParams(positon, size);
                                ((LinearLayout) MiuiFreeFormWindowController.this.mTipView.findViewById(R.id.action)).setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        MiuiFreeFormWindowController.this.removeOpenCloseTipWindow();
                                    }
                                });
                                Slog.d(MiuiFreeFormWindowController.TAG, "addOpenCloseTipWindow positon=" + positon + " size:" + size);
                                MiuiFreeFormWindowController.this.mWindowManager.addView(MiuiFreeFormWindowController.this.mTipView, MiuiFreeFormWindowController.this.mTiplayLayoutParams);
                            }
                        }
                    });
                }
            }
        }
    }

    public void removeOpenCloseTipWindow() {
        synchronized (this) {
            if (mTipShowing) {
                mTipShowing = false;
                this.mainHandler.post(new Runnable() {
                    public void run() {
                        if (MiuiFreeFormWindowController.this.mTipView != null && MiuiFreeFormWindowController.this.mTipView.getWindowToken() != null) {
                            MiuiFreeFormWindowController.this.mWindowManager.removeView(MiuiFreeFormWindowController.this.mTipView);
                            View unused = MiuiFreeFormWindowController.this.mTipView = null;
                            WindowManager.LayoutParams unused2 = MiuiFreeFormWindowController.this.mTiplayLayoutParams = null;
                            Slog.d(MiuiFreeFormWindowController.TAG, "removeOpenCloseTipWindow");
                        }
                    }
                });
            }
        }
    }
}
