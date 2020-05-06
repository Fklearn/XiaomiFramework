package com.miui.optimizecenter.storage.view;

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

    /* renamed from: a  reason: collision with root package name */
    private static final String f5791a = "active";

    /* renamed from: b  reason: collision with root package name */
    private static final String f5792b = "deactive";

    /* renamed from: c  reason: collision with root package name */
    private float f5793c;

    /* renamed from: d  reason: collision with root package name */
    private float f5794d;
    private MamlView e;
    private ImageView f;
    private TextView g;
    private boolean h;

    public EmptyView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void a(String str) {
        MamlView mamlView;
        if (g() && (mamlView = this.e) != null) {
            mamlView.onCommand(str);
        }
    }

    private boolean f() {
        return (getResources().getConfiguration().uiMode & 48) == 32;
    }

    private boolean g() {
        return Build.VERSION.SDK_INT >= 23;
    }

    private String getAssetPath() {
        return f() ? "maml/emptyDark" : "maml/empty";
    }

    private void h() {
        MamlView mamlView = this.e;
        if (mamlView == null) {
            this.e = new MamlView(getContext(), getAssetPath(), 2);
        } else if (mamlView.getParent() != null) {
            ((ViewGroup) this.e.getParent()).removeView(this.e);
        }
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.empty_view_maml_w_h);
        addView(this.e, 0, new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize));
    }

    public void a() {
        MamlView mamlView;
        if (g() && this.h && isShown() && (mamlView = this.e) != null) {
            this.h = false;
            mamlView.onCommand(f5792b);
        }
    }

    public void b() {
        MamlView mamlView;
        if (g() && (mamlView = this.e) != null) {
            mamlView.onDestroy();
            this.e = null;
        }
    }

    public void c() {
        MamlView mamlView;
        if (!g() && isShown() && (mamlView = this.e) != null) {
            mamlView.onPause();
        }
    }

    public void d() {
        MamlView mamlView;
        if (g() && isShown() && (mamlView = this.e) != null) {
            mamlView.onResume();
        }
    }

    public void e() {
        MamlView mamlView;
        if (g() && isShown() && (mamlView = this.e) != null) {
            this.h = !this.h;
            mamlView.onCommand(this.h ? f5791a : f5792b);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f = (ImageView) findViewById(R.id.empty_icon_low_version);
        this.g = (TextView) findViewById(R.id.empty_hint);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            Log.i("EmptyView", "onTouchEvent: container down");
            this.f5793c = motionEvent.getRawX();
            this.f5794d = motionEvent.getRawY();
        } else if (action == 1) {
            Log.i("EmptyView", "onTouchEvent: container up");
            if (Math.max(Math.abs(motionEvent.getRawX() - this.f5793c), Math.abs(motionEvent.getRawY() - this.f5794d)) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                e();
            }
        } else if (action == 2) {
            Log.i("EmptyView", "onTouchEvent: container move");
            a();
        }
        return true;
    }

    public void setHintView(int i) {
        TextView textView = this.g;
        if (textView != null) {
            textView.setText(i);
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        this.g.setVisibility(i);
        if (g()) {
            ((LinearLayout.LayoutParams) this.g.getLayoutParams()).topMargin = getResources().getDimensionPixelSize(R.dimen.empty_view_hint_mt);
            if (i == 0) {
                h();
                return;
            }
            a("pause");
            a(f5792b);
            return;
        }
        this.f.setVisibility(i);
    }
}
