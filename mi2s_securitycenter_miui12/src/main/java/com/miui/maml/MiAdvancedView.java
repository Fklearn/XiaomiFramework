package com.miui.maml;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.C0123a;
import androidx.core.view.ViewCompat;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.Variables;
import com.miui.maml.util.MamlAccessHelper;

public class MiAdvancedView extends View implements RendererController.IRenderable {
    private static final String LOG_TAG = "MiAdvancedView";
    private boolean mLoggedHardwareRender;
    private MamlAccessHelper mMamlAccessHelper;
    protected boolean mNeedDisallowInterceptTouchEvent;
    private boolean mPaused;
    private int mPivotX;
    private int mPivotY;
    protected ScreenElementRoot mRoot;
    private float mScale;
    private RenderThread mThread;
    private boolean mUseExternalRenderThread;

    public MiAdvancedView(Context context, ScreenElementRoot screenElementRoot) {
        super(context);
        this.mPaused = true;
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mRoot = screenElementRoot;
        ScreenElementRoot screenElementRoot2 = this.mRoot;
        if (screenElementRoot2 != null) {
            screenElementRoot2.setOnHoverChangeListener(new ScreenElementRoot.OnHoverChangeListener() {
                public void onHoverChange(String str) {
                    MiAdvancedView.this.setContentDescription(str);
                    MiAdvancedView.this.sendAccessibilityEvent(32768);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.mMamlAccessHelper = new MamlAccessHelper(this.mRoot, this);
            ViewCompat.a((View) this, (C0123a) this.mMamlAccessHelper);
        }
    }

    public MiAdvancedView(Context context, ScreenElementRoot screenElementRoot, RenderThread renderThread) {
        this(context, screenElementRoot);
        if (renderThread != null) {
            this.mUseExternalRenderThread = true;
            this.mThread = renderThread;
            init();
        }
    }

    public void cleanUp() {
        cleanUp(false);
    }

    public void cleanUp(boolean z) {
        this.mRoot.setKeepResource(z);
        setOnTouchListener((View.OnTouchListener) null);
        RenderThread renderThread = this.mThread;
        if (renderThread == null) {
            return;
        }
        if (!this.mUseExternalRenderThread) {
            renderThread.setStop();
            this.mThread = null;
            return;
        }
        this.mRoot.detachFromRenderThread(renderThread);
        this.mRoot.selfFinish();
    }

    /* access modifiers changed from: protected */
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        MamlAccessHelper mamlAccessHelper = this.mMamlAccessHelper;
        if (mamlAccessHelper == null || !mamlAccessHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    public void doRender() {
        postInvalidate();
    }

    public final ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        return (int) this.mRoot.getHeight();
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        return (int) this.mRoot.getWidth();
    }

    public void init() {
        this.mRoot.setRenderControllerRenderable(this);
        this.mRoot.setConfiguration(getResources().getConfiguration());
        this.mRoot.attachToRenderThread(this.mThread);
        this.mRoot.selfInit();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mUseExternalRenderThread && this.mThread == null) {
            this.mThread = new RenderThread();
            init();
            onCreateRenderThread(this.mThread);
            this.mThread.setPaused(this.mPaused);
            this.mThread.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mRoot.onConfigurationChanged(configuration);
    }

    /* access modifiers changed from: protected */
    public void onCreateRenderThread(RenderThread renderThread) {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        RenderThread renderThread = this.mThread;
        if (renderThread != null && renderThread.isStarted()) {
            if (!this.mLoggedHardwareRender) {
                Log.d(LOG_TAG, "canvas hardware render: " + canvas.isHardwareAccelerated());
                this.mLoggedHardwareRender = true;
            }
            if (this.mScale != 0.0f) {
                int save = canvas.save();
                float f = this.mScale;
                canvas.scale(f, f, (float) this.mPivotX, (float) this.mPivotY);
                this.mRoot.render(canvas);
                canvas.restoreToCount(save);
                return;
            }
            this.mRoot.render(canvas);
        }
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
        Variables variables = this.mRoot.getContext().mVariables;
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
        this.mRoot.requestUpdate();
    }

    public void onPause() {
        this.mPaused = true;
        RenderThread renderThread = this.mThread;
        if (renderThread == null) {
            return;
        }
        if (!this.mUseExternalRenderThread) {
            renderThread.setPaused(true);
        } else {
            this.mRoot.selfPause();
        }
    }

    public void onResume() {
        this.mPaused = false;
        RenderThread renderThread = this.mThread;
        if (renderThread == null) {
            return;
        }
        if (!this.mUseExternalRenderThread) {
            renderThread.setPaused(false);
        } else {
            this.mRoot.selfResume();
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

    public void setScale(float f, int i, int i2) {
        this.mScale = f;
        this.mPivotX = i;
        this.mPivotY = i2;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            onResume();
        } else if (i == 4 || i == 8) {
            onPause();
        }
    }
}
