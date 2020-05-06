package com.miui.earthquakewarning.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.miui.maml.component.MamlView;
import com.miui.securitycenter.R;

public class EmptyView extends LinearLayout {
    private static final String COMMAND_ACTIVE = "active";
    private static final String COMMAND_DEACTIVE = "deactive";
    private static final String COMMAND_FINISH = "finish";
    private static final String COMMAND_INIT = "init";
    private static final String COMMAND_PAUSE = "pause";
    private static final String COMMAND_PLAY = "play";
    private static final String COMMAND_RESUME = "resume";
    private static final String TAG = "EmptyView";
    private TextView hintView;
    private boolean isActive;
    private ImageView lowVerionIconView;
    private float mRawX;
    private float mRawY;
    private MamlView mamlView;

    public EmptyView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private String getAssetPath() {
        return isDarkMode() ? "maml/common_empty_dark" : "maml/common_empty";
    }

    private boolean isDarkMode() {
        return (getResources().getConfiguration().uiMode & 48) == 32;
    }

    private boolean isMamlValid() {
        return Build.VERSION.SDK_INT >= 23;
    }

    private void loadMamlView() {
        MamlView mamlView2 = this.mamlView;
        if (mamlView2 == null) {
            this.mamlView = new MamlView(getContext(), getAssetPath(), 2);
        } else if (mamlView2.getParent() != null) {
            ((ViewGroup) this.mamlView.getParent()).removeView(this.mamlView);
        }
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.view_dimen_480);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.topMargin = 200;
        addView(this.mamlView, 0, layoutParams);
    }

    private void onCommandMaml(String str) {
        MamlView mamlView2;
        if (isMamlValid() && (mamlView2 = this.mamlView) != null) {
            mamlView2.onCommand(str);
        }
    }

    public void deActiveState() {
        MamlView mamlView2;
        if (isMamlValid() && this.isActive && isShown() && (mamlView2 = this.mamlView) != null) {
            this.isActive = false;
            mamlView2.onCommand(COMMAND_DEACTIVE);
        }
    }

    public void onDestroy() {
        MamlView mamlView2;
        if (isMamlValid() && (mamlView2 = this.mamlView) != null) {
            mamlView2.onDestroy();
            this.mamlView = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.lowVerionIconView = (ImageView) findViewById(R.id.empty_icon_low_version);
        this.hintView = (TextView) findViewById(R.id.empty_hint);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    public void onPause() {
        MamlView mamlView2;
        if (!isMamlValid() && isShown() && (mamlView2 = this.mamlView) != null) {
            mamlView2.onPause();
        }
    }

    public void onResume() {
        MamlView mamlView2;
        if (isMamlValid() && isShown() && (mamlView2 = this.mamlView) != null) {
            mamlView2.onResume();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            Log.i(TAG, "onTouchEvent: container down");
            this.mRawX = motionEvent.getRawX();
            this.mRawY = motionEvent.getRawY();
        } else if (action == 1) {
            Log.i(TAG, "onTouchEvent: container up");
            if (Math.max(Math.abs(motionEvent.getRawX() - this.mRawX), Math.abs(motionEvent.getRawY() - this.mRawY)) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                reverseActiveState();
            }
        } else if (action == 2) {
            Log.i(TAG, "onTouchEvent: container move");
            deActiveState();
        }
        return true;
    }

    public void reverseActiveState() {
        MamlView mamlView2;
        if (isMamlValid() && isShown() && (mamlView2 = this.mamlView) != null) {
            this.isActive = !this.isActive;
            mamlView2.onCommand(this.isActive ? COMMAND_ACTIVE : COMMAND_DEACTIVE);
        }
    }

    public void setHintView(int i) {
        TextView textView = this.hintView;
        if (textView != null) {
            textView.setText(i);
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        this.hintView.setVisibility(i);
        if (isMamlValid()) {
            ((LinearLayout.LayoutParams) this.hintView.getLayoutParams()).topMargin = getResources().getDimensionPixelSize(R.dimen.ew_empty_view_hint_mt);
            if (i == 0) {
                loadMamlView();
                return;
            }
            onCommandMaml(COMMAND_PAUSE);
            onCommandMaml(COMMAND_DEACTIVE);
            return;
        }
        this.lowVerionIconView.setVisibility(i);
    }
}
