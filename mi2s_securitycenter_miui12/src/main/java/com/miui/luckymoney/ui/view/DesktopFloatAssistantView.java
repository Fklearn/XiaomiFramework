package com.miui.luckymoney.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.service.LuckyMoneyMonitorService;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.activity.FloatAssistantActivity;
import com.miui.luckymoney.utils.DateUtil;
import com.miui.luckymoney.utils.ImageUtil;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;

public class DesktopFloatAssistantView {
    private static final String TAG = "FloatAssistantView";
    /* access modifiers changed from: private */
    public ImageView cancleImage;
    private FrameLayout mCancleLayout;
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public ImageView mFloatImageView;
    /* access modifiers changed from: private */
    public FrameLayout mFloatLayout;
    private boolean mFloatTipsEnable;
    /* access modifiers changed from: private */
    public ImageView mFloatTipsImageView;
    /* access modifiers changed from: private */
    public boolean mIsHide;
    /* access modifiers changed from: private */
    public boolean mIsMove;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            int id = view.getId();
            if (id != R.id.iv_desktop_float_view) {
                if (id == R.id.iv_float_tips_view) {
                    MiStatUtil.recordFloatTips(DesktopFloatAssistantView.this.mCommonConfig.getFloatTipsStartTime() + "", true);
                    Log.i(DesktopFloatAssistantView.TAG, "click float tips");
                } else {
                    return;
                }
            }
            DesktopFloatAssistantView.this.hideFloatTipsGone();
            Intent intent = new Intent(DesktopFloatAssistantView.this.mContext, FloatAssistantActivity.class);
            intent.addFlags(268435456);
            g.b(DesktopFloatAssistantView.this.mContext, intent, B.b());
            MiStatUtil.recordFloatWindowClick();
        }
    };
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        float left = 0.0f;
        float startX = 0.0f;
        float startY = 0.0f;
        float top = 0.0f;
        float x = 0.0f;
        float y = 0.0f;

        private boolean isCollision(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            if (i >= i5 && i >= i7 + i5) {
                return false;
            }
            if (i <= i5 && i + i3 <= i5) {
                return false;
            }
            if (i2 < i6 || i2 < i8 + i6) {
                return i2 > i6 || i2 + i4 > i6;
            }
            return false;
        }

        private void updateView() {
            DesktopFloatAssistantView.this.wmParams.x = (int) ((this.x - this.startX) - this.left);
            DesktopFloatAssistantView.this.wmParams.y = (int) ((this.y - this.startY) - this.top);
            if (!DesktopFloatAssistantView.this.mIsHide) {
                boolean unused = DesktopFloatAssistantView.this.hideOnBothSides();
            }
            DesktopFloatAssistantView.this.mWindowManager.updateViewLayout(DesktopFloatAssistantView.this.mFloatLayout, DesktopFloatAssistantView.this.wmParams);
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            DesktopFloatAssistantView.this.hideFloatTipsGone();
            this.left = (float) DesktopFloatAssistantView.this.mFloatImageView.getLeft();
            this.top = (float) DesktopFloatAssistantView.this.mFloatImageView.getTop();
            this.x = motionEvent.getRawX();
            this.y = motionEvent.getRawY();
            int action = motionEvent.getAction();
            if (action == 0) {
                boolean unused = DesktopFloatAssistantView.this.mIsMove = false;
                this.startX = motionEvent.getX();
                this.startY = motionEvent.getY();
                DesktopFloatAssistantView.this.createCancleFloatView();
            } else if (action == 1) {
                float x2 = motionEvent.getX();
                float y2 = motionEvent.getY();
                float f = this.startX;
                float f2 = (x2 - f) * (x2 - f);
                float f3 = this.startY;
                if (f2 + ((y2 - f3) * (y2 - f3)) > 10.0f || DesktopFloatAssistantView.this.mIsMove) {
                    boolean unused2 = DesktopFloatAssistantView.this.mIsMove = true;
                    DesktopFloatAssistantView desktopFloatAssistantView = DesktopFloatAssistantView.this;
                    boolean unused3 = desktopFloatAssistantView.mIsHide = desktopFloatAssistantView.hideOnBothSides();
                    DesktopFloatAssistantView.this.mWindowManager.updateViewLayout(DesktopFloatAssistantView.this.mFloatLayout, DesktopFloatAssistantView.this.wmParams);
                    DesktopFloatAssistantView.this.mCommonConfig.setLastFloatViewXPos((int) (this.x - this.startX));
                    DesktopFloatAssistantView.this.mCommonConfig.setLastFloatViewYPos((int) (this.y - this.startY));
                }
                this.startX = 0.0f;
                this.startY = 0.0f;
                int[] iArr = new int[2];
                DesktopFloatAssistantView.this.cancleImage.getLocationOnScreen(iArr);
                if (isCollision(DesktopFloatAssistantView.this.wmParams.x, DesktopFloatAssistantView.this.wmParams.y, DesktopFloatAssistantView.this.mFloatLayout.getWidth(), DesktopFloatAssistantView.this.mFloatLayout.getHeight(), iArr[0], iArr[1], DesktopFloatAssistantView.this.cancleImage.getWidth(), DesktopFloatAssistantView.this.cancleImage.getHeight())) {
                    DesktopFloatAssistantView.this.mCommonConfig.setDesktopFloatWindowEnable(false);
                    DesktopFloatAssistantView.this.mCommonConfig.setLastFloatViewXPos(CommonPerConstants.DEFAULT.LAST_FLOAT_VIEW_X_POS_DEFAULT);
                    DesktopFloatAssistantView.this.mCommonConfig.setLastFloatViewYPos(CommonPerConstants.DEFAULT.LAST_FLOAT_VIEW_Y_POS_DEFAULT);
                    DesktopFloatAssistantView.this.sendConfigChangedBroadcast(Constants.TYPE_SHOW_FLOAT_WINDOW_BUTTON);
                }
                DesktopFloatAssistantView.this.removeCancleFloatView();
            } else if (action == 2) {
                float x3 = motionEvent.getX();
                float y3 = motionEvent.getY();
                float f4 = this.startX;
                float f5 = (x3 - f4) * (x3 - f4);
                float f6 = this.startY;
                if (f5 + ((y3 - f6) * (y3 - f6)) > 10.0f) {
                    boolean unused4 = DesktopFloatAssistantView.this.mIsMove = true;
                    updateView();
                    DesktopFloatAssistantView.this.mFloatImageView.setImageResource(R.drawable.desktop_float_view);
                }
                DesktopFloatAssistantView.this.cancleImage.getLocationOnScreen(new int[2]);
            }
            return DesktopFloatAssistantView.this.mIsMove;
        }
    };
    private LuckyMoneyMonitorService mService;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams wmParams;

    public DesktopFloatAssistantView(LuckyMoneyMonitorService luckyMoneyMonitorService) {
        this.mService = luckyMoneyMonitorService;
        this.mContext = luckyMoneyMonitorService.getApplicationContext();
        initFloatView();
    }

    /* access modifiers changed from: private */
    public void createCancleFloatView() {
        removeCancleFloatView();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = 2003;
        layoutParams.format = -3;
        layoutParams.flags = 264;
        layoutParams.gravity = 49;
        layoutParams.width = -2;
        layoutParams.height = -2;
        this.mCancleLayout = (FrameLayout) LayoutInflater.from(this.mContext).inflate(R.layout.desktop_cancle_view_layout, (ViewGroup) null);
        int i = 0;
        layoutParams.x = 0;
        layoutParams.y = 0;
        if (i.e()) {
            int identifier = this.mContext.getResources().getIdentifier("status_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
            if (identifier > 0) {
                i = this.mContext.getResources().getDimensionPixelSize(identifier);
            }
            layoutParams.y = i;
        }
        this.mWindowManager.addView(this.mCancleLayout, layoutParams);
        this.cancleImage = (ImageView) this.mCancleLayout.findViewById(R.id.cancleImage);
        Log.i(TAG, "createCancleFloatView");
    }

    /* access modifiers changed from: private */
    public void hideFloatTipsGone() {
        if (this.mFloatTipsEnable && this.mFloatTipsImageView.getVisibility() != 8) {
            int left = this.mFloatImageView.getLeft();
            int top = this.mFloatImageView.getTop();
            this.mFloatLayout.setVisibility(8);
            this.mFloatTipsImageView.setVisibility(8);
            CommonConfig commonConfig = this.mCommonConfig;
            commonConfig.setFloatTipsUpdateTime(commonConfig.getFloatTipsStopTime() + this.mCommonConfig.getFloatTipsDuration());
            this.mCommonConfig.setLastFloatViewXPos(this.wmParams.x + left);
            this.mCommonConfig.setLastFloatViewYPos(this.wmParams.y + top);
            WindowManager.LayoutParams layoutParams = this.wmParams;
            layoutParams.x += left;
            layoutParams.y += top;
            this.mWindowManager.updateViewLayout(this.mFloatLayout, layoutParams);
            this.mFloatLayout.post(new Runnable() {
                public void run() {
                    DesktopFloatAssistantView desktopFloatAssistantView = DesktopFloatAssistantView.this;
                    desktopFloatAssistantView.setLayout(desktopFloatAssistantView.mFloatTipsImageView, 0, 0, 0, 0);
                    DesktopFloatAssistantView desktopFloatAssistantView2 = DesktopFloatAssistantView.this;
                    desktopFloatAssistantView2.setLayout(desktopFloatAssistantView2.mFloatImageView, 0, 0, 0, 0);
                    DesktopFloatAssistantView.this.mFloatLayout.setVisibility(0);
                }
            });
            if (this.mService.mMainHandler.hasMessages(4)) {
                Log.i(TAG, "remove float tips msg");
                this.mService.mMainHandler.removeMessages(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean hideOnBothSides() {
        WindowManager.LayoutParams layoutParams = this.wmParams;
        int i = layoutParams.x;
        if (i < 5) {
            layoutParams.x = 0;
            this.mFloatImageView.setImageResource(R.drawable.desktop_float_view_hide_left);
            return true;
        } else if (i <= this.mContext.getResources().getDisplayMetrics().widthPixels - 160) {
            return false;
        } else {
            this.wmParams.x = this.mContext.getResources().getDisplayMetrics().widthPixels;
            if (Build.VERSION.SDK_INT < 24) {
                this.mFloatImageView.setVisibility(8);
                this.mFloatImageView.setImageResource(R.drawable.desktop_float_view_hide_right);
                this.mFloatImageView.postDelayed(new Runnable() {
                    public void run() {
                        DesktopFloatAssistantView.this.mFloatImageView.setVisibility(0);
                    }
                }, 500);
                return true;
            }
            this.mFloatImageView.setImageResource(R.drawable.desktop_float_view_hide_right);
            return true;
        }
    }

    private void initFloatView() {
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.wmParams = new WindowManager.LayoutParams();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mFloatTipsEnable = isTipsLocationFit() && System.currentTimeMillis() > this.mCommonConfig.getFloatTipsUpdateTime() && DateUtil.isTipsTimeEnable(this.mContext);
    }

    private boolean isTipsLocationFit() {
        return ((float) this.mCommonConfig.getLastFloatViewYPos()) > (this.mContext.getResources().getDimension(R.dimen.float_tips_view_height) + this.mContext.getResources().getDimension(R.dimen.float_view_height)) - 50.0f;
    }

    /* access modifiers changed from: private */
    public void sendConfigChangedBroadcast(String str) {
        Intent intent = new Intent(com.miui.luckymoney.config.Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(com.miui.luckymoney.config.Constants.KEY_CONFIG_CHANGED_FLAG, str);
        this.mContext.sendBroadcastAsUser(intent, B.b());
    }

    /* access modifiers changed from: private */
    public void setLayout(View view, int i, int i2, int i3, int i4) {
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        marginLayoutParams.setMargins(i, i2, i3, i4);
        view.setLayoutParams(new FrameLayout.LayoutParams(marginLayoutParams));
    }

    public void createFloatView() {
        WindowManager.LayoutParams layoutParams = this.wmParams;
        layoutParams.type = 2003;
        layoutParams.format = -3;
        layoutParams.flags = 264;
        layoutParams.gravity = 51;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.x = this.mCommonConfig.getLastFloatViewXPos();
        this.wmParams.y = this.mCommonConfig.getLastFloatViewYPos();
        this.mFloatLayout = (FrameLayout) LayoutInflater.from(this.mContext).inflate(this.mFloatTipsEnable ? R.layout.desktop_float_view_layout_tips : R.layout.desktop_float_view_layout, (ViewGroup) null);
        this.mFloatImageView = (ImageView) this.mFloatLayout.findViewById(R.id.iv_desktop_float_view);
        this.mIsHide = hideOnBothSides();
        if (this.mFloatTipsEnable) {
            int width = this.mWindowManager.getDefaultDisplay().getWidth();
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
            this.mFloatTipsImageView = (ImageView) this.mFloatLayout.findViewById(R.id.iv_float_tips_view);
            this.mFloatTipsImageView.setVisibility(0);
            this.mFloatTipsImageView.setOnClickListener(this.mOnClickListener);
            if (this.wmParams.x < width / 2) {
                Bitmap loadBitmapfromFile = ImageUtil.loadBitmapfromFile(ResFileUtils.getResFile(this.mContext, ResFileUtils.FLOATTIPS, this.mCommonConfig.getFloatTipsImageLeft()), this.mContext);
                if (loadBitmapfromFile != null) {
                    this.mFloatTipsImageView.setImageBitmap(loadBitmapfromFile);
                }
                this.mFloatImageView.measure(makeMeasureSpec, makeMeasureSpec2);
                this.mFloatTipsImageView.measure(makeMeasureSpec, makeMeasureSpec2);
                int max = Math.max(this.mFloatImageView.getMeasuredWidth() - (this.mFloatTipsImageView.getMeasuredWidth() / 2), 0);
                int measuredHeight = this.mFloatImageView.getMeasuredHeight();
                setLayout(this.mFloatTipsImageView, max, 0, 0, 0);
                setLayout(this.mFloatImageView, 0, measuredHeight, 0, 0);
            } else {
                Bitmap loadBitmapfromFile2 = ImageUtil.loadBitmapfromFile(ResFileUtils.getResFile(this.mContext, ResFileUtils.FLOATTIPS, this.mCommonConfig.getFloatTipsImageRight()), this.mContext);
                if (loadBitmapfromFile2 != null) {
                    this.mFloatTipsImageView.setImageBitmap(loadBitmapfromFile2);
                }
                this.mFloatImageView.measure(makeMeasureSpec, makeMeasureSpec2);
                this.mFloatTipsImageView.measure(makeMeasureSpec, makeMeasureSpec2);
                setLayout(this.mFloatImageView, Math.max(this.mFloatTipsImageView.getMeasuredWidth() / 2, Math.max(this.mFloatTipsImageView.getMeasuredWidth() - this.mFloatImageView.getMeasuredWidth(), 0)), this.mFloatImageView.getMeasuredHeight(), 0, 0);
            }
            MiStatUtil.recordFloatTips(this.mCommonConfig.getFloatTipsStartTime() + "", false);
        }
        this.mWindowManager.addView(this.mFloatLayout, this.wmParams);
        this.mFloatImageView.setOnTouchListener(this.mOnTouchListener);
        this.mFloatImageView.setOnClickListener(this.mOnClickListener);
    }

    public void removeCancleFloatView() {
        FrameLayout frameLayout;
        WindowManager windowManager = this.mWindowManager;
        if (windowManager != null && (frameLayout = this.mCancleLayout) != null) {
            windowManager.removeView(frameLayout);
            this.mCancleLayout = null;
            Log.i(TAG, "removeCancleFloatView");
        }
    }

    public void removeFloatView() {
        FrameLayout frameLayout;
        if (!(this.mWindowManager == null || (frameLayout = this.mFloatLayout) == null)) {
            frameLayout.setClickable(false);
            hideFloatTipsGone();
            Log.i(TAG, "remove float view : " + this.mFloatTipsEnable);
            try {
                this.mWindowManager.removeView(this.mFloatLayout);
            } catch (Exception unused) {
            }
        }
        new Handler(this.mContext.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                DesktopFloatAssistantView.this.removeCancleFloatView();
            }
        }, 1000);
    }
}
