package com.miui.maml.component;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.view.C0123a;
import androidx.core.view.ViewCompat;
import com.miui.maml.RenderVsyncUpdater;
import com.miui.maml.ResourceLoader;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.Variables;
import com.miui.maml.util.AssetsResourceLoader;
import com.miui.maml.util.FolderResourceLoader;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.MamlAccessHelper;
import com.miui.maml.util.Utils;
import com.miui.maml.util.ZipResourceLoader;
import java.lang.ref.WeakReference;

public class MamlView extends FrameLayout {
    private static final String BLUR_VAR_NAME = "__blur_ratio";
    public static final int MODE_ASSETS_FOLDER = 2;
    public static final int MODE_FOLDER = 3;
    public static final int MODE_ZIP = 1;
    private static final String TAG = "MamlView";
    private static final String VERSION = "1.0.0";
    private static final int VERSION_CODE = 1;
    /* access modifiers changed from: private */
    public boolean mCanvasParamsChanged;
    private final ScreenElementRoot.OnExternCommandListener mCommandListener;
    /* access modifiers changed from: private */
    public WeakReference<OnExternCommandListener> mExternCommandListener;
    /* access modifiers changed from: private */
    public boolean mHasDelay;
    private int mLastBlurRatio;
    private WindowManager.LayoutParams mLp;
    private MamlAccessHelper mMamlAccessHelper;
    protected boolean mNeedDisallowInterceptTouchEvent;
    /* access modifiers changed from: private */
    public int mPivotX;
    /* access modifiers changed from: private */
    public int mPivotY;
    protected ScreenElementRoot mRoot;
    /* access modifiers changed from: private */
    public float mScale;
    /* access modifiers changed from: private */
    public RenderVsyncUpdater mUpdater;
    /* access modifiers changed from: private */
    public InnerView mView;
    private WindowManager mWindowManager;
    /* access modifiers changed from: private */
    public float mX;
    /* access modifiers changed from: private */
    public float mY;

    private class InnerView extends View {
        public InnerView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            MamlView mamlView = MamlView.this;
            if (mamlView.mRoot == null) {
                return;
            }
            if (!mamlView.mHasDelay || MamlView.this.mUpdater.isStarted()) {
                if (MamlView.this.mCanvasParamsChanged) {
                    int save = canvas.save();
                    canvas.translate(MamlView.this.mX, MamlView.this.mY);
                    if (MamlView.this.mScale != 0.0f) {
                        canvas.scale(MamlView.this.mScale, MamlView.this.mScale, (float) MamlView.this.mPivotX, (float) MamlView.this.mPivotY);
                    }
                    MamlView.this.mRoot.render(canvas);
                    canvas.restoreToCount(save);
                } else {
                    MamlView.this.mRoot.render(canvas);
                }
                MamlView.this.mUpdater.doneRender();
            }
        }
    }

    public interface OnExternCommandListener {
        void onCommand(String str, Double d2, String str2);
    }

    public MamlView(Context context, ResourceLoader resourceLoader) {
        super(context.getApplicationContext());
        this.mCommandListener = new ScreenElementRoot.OnExternCommandListener() {
            public void onCommand(String str, Double d2, String str2) {
                OnExternCommandListener onExternCommandListener;
                if (MamlView.this.mExternCommandListener != null && (onExternCommandListener = (OnExternCommandListener) MamlView.this.mExternCommandListener.get()) != null) {
                    onExternCommandListener.onCommand(str, d2, str2);
                }
            }
        };
        load(context, resourceLoader);
    }

    public MamlView(Context context, ScreenElementRoot screenElementRoot) {
        this(context, screenElementRoot, new Handler(), 0);
    }

    public MamlView(Context context, ScreenElementRoot screenElementRoot, long j) {
        this(context, screenElementRoot, new Handler(), j);
    }

    public MamlView(Context context, ScreenElementRoot screenElementRoot, Handler handler, long j) {
        super(context);
        this.mCommandListener = new ScreenElementRoot.OnExternCommandListener() {
            public void onCommand(String str, Double d2, String str2) {
                OnExternCommandListener onExternCommandListener;
                if (MamlView.this.mExternCommandListener != null && (onExternCommandListener = (OnExternCommandListener) MamlView.this.mExternCommandListener.get()) != null) {
                    onExternCommandListener.onCommand(str, d2, str2);
                }
            }
        };
        initMamlview(context, screenElementRoot, handler, j);
    }

    public MamlView(Context context, String str, int i) {
        this(context, str, (String) null, i);
    }

    public MamlView(Context context, String str, String str2, int i) {
        super(context.getApplicationContext());
        this.mCommandListener = new ScreenElementRoot.OnExternCommandListener() {
            public void onCommand(String str, Double d2, String str2) {
                OnExternCommandListener onExternCommandListener;
                if (MamlView.this.mExternCommandListener != null && (onExternCommandListener = (OnExternCommandListener) MamlView.this.mExternCommandListener.get()) != null) {
                    onExternCommandListener.onCommand(str, d2, str2);
                }
            }
        };
        load(context, getResouceLoader(context, str, str2, i));
    }

    /* access modifiers changed from: private */
    public void blurBackground() {
        try {
            if (this.mRoot != null && this.mRoot.isMamlBlurWindow() && this.mLp != null && this.mRoot.getVariables().existsDouble(BLUR_VAR_NAME)) {
                int i = (int) this.mRoot.getVariables().getDouble(BLUR_VAR_NAME);
                if (i < 0) {
                    i = 0;
                } else if (i > 100) {
                    i = 100;
                }
                if (i != this.mLastBlurRatio) {
                    this.mLastBlurRatio = i;
                    if (i == 0) {
                        this.mLp.flags &= -5;
                    } else {
                        HideSdkDependencyUtils.WindowManager_LayoutParams_setLayoutParamsBlurRatio(this.mLp, (((float) i) * 1.0f) / 100.0f);
                        this.mLp.flags |= 4;
                    }
                    this.mWindowManager.updateViewLayout(this, this.mLp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResourceLoader getResouceLoader(Context context, String str, String str2, int i) {
        if (i == 1) {
            return new ZipResourceLoader(str, str2);
        }
        if (i == 2) {
            return new AssetsResourceLoader(context, str);
        }
        if (i != 3) {
            return null;
        }
        return new FolderResourceLoader(str);
    }

    private void initMamlview(Context context, ScreenElementRoot screenElementRoot, Handler handler, long j) {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (screenElementRoot != null) {
            this.mWindowManager = (WindowManager) context.getSystemService("window");
            this.mView = new InnerView(context);
            addView(this.mView, new ViewGroup.LayoutParams(-1, -1));
            this.mRoot = screenElementRoot;
            this.mRoot.setViewManager(this);
            this.mRoot.setOnHoverChangeListener(new ScreenElementRoot.OnHoverChangeListener() {
                public void onHoverChange(String str) {
                    MamlView.this.setContentDescription(str);
                    MamlView.this.sendAccessibilityEvent(32768);
                }
            });
            this.mUpdater = new RenderVsyncUpdater(this.mRoot, handler) {
                /* access modifiers changed from: protected */
                public void doRenderImp() {
                    MamlView.this.mView.postInvalidate();
                    MamlView.this.blurBackground();
                }
            };
            if (j > 0) {
                this.mHasDelay = true;
                this.mUpdater.setStartDelay(SystemClock.elapsedRealtime(), j);
            }
            init();
            if (Build.VERSION.SDK_INT >= 23) {
                this.mMamlAccessHelper = new MamlAccessHelper(this.mRoot, this);
                ViewCompat.a((View) this, (C0123a) this.mMamlAccessHelper);
            }
            this.mRoot.setMamlViewOnExternCommandListener(this.mCommandListener);
            return;
        }
        throw new NullPointerException();
    }

    private void load(Context context, ResourceLoader resourceLoader) {
        if (resourceLoader != null) {
            ScreenElementRoot screenElementRoot = new ScreenElementRoot(new ScreenContext(context.getApplicationContext(), new ResourceManager(resourceLoader)));
            if (screenElementRoot.load()) {
                screenElementRoot.init();
                initMamlview(context, screenElementRoot, new Handler(), 0);
            }
        }
    }

    public void cleanUp() {
        cleanUp(false);
    }

    public void cleanUp(boolean z) {
        setOnTouchListener((View.OnTouchListener) null);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setKeepResource(z);
            this.mUpdater.cleanUp();
        }
    }

    /* access modifiers changed from: protected */
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        MamlAccessHelper mamlAccessHelper = this.mMamlAccessHelper;
        if (mamlAccessHelper == null || !mamlAccessHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    public int getMamlVersionCode() {
        return 1;
    }

    public String getMamlVersionName() {
        return VERSION;
    }

    public final ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return (int) screenElementRoot.getHeight();
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return (int) screenElementRoot.getWidth();
        }
        return -1;
    }

    public double getVariableNumber(String str) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return Utils.getVariableNumber(str, screenElementRoot.getVariables());
        }
        return 0.0d;
    }

    public String getVariableString(String str) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return Utils.getVariableString(str, screenElementRoot.getVariables());
        }
        return null;
    }

    public void init() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setConfiguration(getResources().getConfiguration());
            this.mUpdater.init();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        onResume();
    }

    @Deprecated
    public void onCommand(String str) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.onCommand(str);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.onConfigurationChanged(configuration);
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        setOnTouchListener((View.OnTouchListener) null);
        RenderVsyncUpdater renderVsyncUpdater = this.mUpdater;
        if (renderVsyncUpdater != null) {
            renderVsyncUpdater.cleanUp();
        }
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.finish();
            this.mRoot.getVariables().reset();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onPause();
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.postMessage(MotionEvent.obtain(motionEvent));
        }
        return super.onHoverEvent(motionEvent);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            accessibilityNodeInfo.setText(screenElementRoot.getRawAttr("accessibilityText"));
        }
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            Variables variables = screenElementRoot.getContext().mVariables;
            variables.put(VariableNames.VARIABLE_VIEW_WIDTH, (double) (((float) (i3 - i)) / this.mRoot.getScale()));
            variables.put(VariableNames.VARIABLE_VIEW_HEIGHT, (double) (((float) (i4 - i2)) / this.mRoot.getScale()));
            ViewParent parent = getParent();
            while (parent instanceof View) {
                View view = (View) parent;
                i += view.getLeft() - view.getScrollX();
                i2 += view.getTop() - view.getScrollY();
                parent = view.getParent();
            }
            variables.put(VariableNames.VARIABLE_VIEW_X, (double) (((float) i) / this.mRoot.getScale()));
            variables.put(VariableNames.VARIABLE_VIEW_Y, (double) (((float) i2) / this.mRoot.getScale()));
            this.mUpdater.forceUpdate();
        }
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        RenderVsyncUpdater renderVsyncUpdater = this.mUpdater;
        if (renderVsyncUpdater != null) {
            renderVsyncUpdater.onPause();
        }
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        RenderVsyncUpdater renderVsyncUpdater = this.mUpdater;
        if (renderVsyncUpdater != null) {
            renderVsyncUpdater.onResume();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot == null) {
            return false;
        }
        boolean needDisallowInterceptTouchEvent = screenElementRoot.needDisallowInterceptTouchEvent();
        if (this.mNeedDisallowInterceptTouchEvent != needDisallowInterceptTouchEvent) {
            getParent().requestDisallowInterceptTouchEvent(needDisallowInterceptTouchEvent);
            this.mNeedDisallowInterceptTouchEvent = needDisallowInterceptTouchEvent;
        }
        this.mRoot.postMessage(MotionEvent.obtain(motionEvent));
        return true;
    }

    public void putVariableNumber(String str, double d2) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            Utils.putVariableNumber(str, screenElementRoot.getVariables(), d2);
        }
    }

    public void putVariableString(String str, String str2) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            Utils.putVariableString(str, screenElementRoot.getVariables(), str2);
        }
    }

    public void sendCommand(String str) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.onCommand(str);
        }
    }

    public MamlView setAutoCleanup(boolean z) {
        RenderVsyncUpdater renderVsyncUpdater = this.mUpdater;
        if (renderVsyncUpdater != null) {
            renderVsyncUpdater.setAutoCleanup(z);
        }
        return this;
    }

    public final void setKeepResource(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setKeepResource(z);
        }
    }

    public void setOnExternCommandListener(OnExternCommandListener onExternCommandListener) {
        this.mExternCommandListener = onExternCommandListener == null ? null : new WeakReference<>(onExternCommandListener);
    }

    public void setScale(float f, int i, int i2) {
        this.mScale = f;
        this.mPivotX = i;
        this.mPivotY = i2;
        this.mCanvasParamsChanged = true;
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.requestUpdate();
        }
    }

    public void setTranslate(float f, float f2) {
        this.mX = f;
        this.mY = f2;
        this.mCanvasParamsChanged = true;
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.requestUpdate();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            onResume();
        } else if (i == 4 || i == 8) {
            onPause();
        }
    }

    @Deprecated
    public void setWindowLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.mLp = layoutParams;
    }
}
