package com.miui.common.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;

public class OverScrollLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private LinearLayout f3786a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f3787b;

    /* renamed from: c  reason: collision with root package name */
    private View f3788c;

    /* renamed from: d  reason: collision with root package name */
    private View f3789d;
    private int e;
    private float f;
    private float g;
    private float h;
    private boolean i = false;
    private boolean j;
    private a k;
    private boolean l;
    private boolean m;
    private Activity n;

    public interface a {
        void a();
    }

    public OverScrollLayout(Context context) {
        super(context);
    }

    public OverScrollLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void a(float f2) {
        if (this.m) {
            float min = Math.min((float) this.e, getTranslationY() + (f2 * (0.4f - this.h)));
            if (min > 0.0f) {
                min = 0.0f;
            }
            setTranslationY(min);
            float abs = Math.abs(min);
            int i2 = this.e;
            this.h = (abs / ((float) i2)) * 0.4f;
            this.f3786a.setTranslationY(((float) i2) + min);
            this.f3787b.setTranslationY(((((float) this.e) - this.f3786a.getTranslationY()) - ((float) this.f3787b.getHeight())) / 2.0f);
            this.f3788c.setTranslationY(((((float) this.e) - this.f3786a.getTranslationY()) - ((float) this.f3788c.getHeight())) / 2.0f);
            this.f3789d.setTranslationY(((((float) this.e) - this.f3786a.getTranslationY()) - ((float) this.f3789d.getHeight())) / 2.0f);
        }
    }

    private boolean b() {
        return this.l && this.i;
    }

    public void a() {
        if (this.m) {
            animate().cancel();
            this.f3786a.animate().cancel();
            this.f3787b.animate().cancel();
            this.f3788c.animate().cancel();
            this.f3789d.animate().cancel();
            animate().translationY(0.0f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
            this.f3786a.animate().translationY((float) this.e).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
            this.f3787b.animate().translationY(((float) (-this.f3787b.getHeight())) / 2.0f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
            this.f3788c.animate().translationY(((float) (-this.f3788c.getHeight())) / 2.0f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
            this.f3789d.animate().translationY(((float) (-this.f3789d.getHeight())) / 2.0f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    public void a(LinearLayout linearLayout) {
        this.f3786a = linearLayout;
        this.f3787b = (TextView) this.f3786a.findViewById(R.id.refresh_tips);
        this.f3788c = this.f3786a.findViewById(R.id.refresh_tips_left);
        this.f3789d = this.f3786a.findViewById(R.id.refresh_tips_right);
        this.e = getContext().getResources().getDimensionPixelSize(R.dimen.refresh_view_hight);
        this.f3786a.setTranslationY((float) this.e);
        this.m = true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.j) {
            return true;
        }
        int action = motionEvent.getAction();
        float rawY = motionEvent.getRawY();
        float rawX = motionEvent.getRawX();
        if (action == 0) {
            this.l = false;
            this.f = rawY;
            this.g = rawX;
        } else if (action == 2) {
            if (Math.abs(rawX - this.g) * 1.2f < this.f - rawY) {
                this.l = true;
            } else {
                this.l = false;
            }
            if (b()) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!b()) {
            return super.onTouchEvent(motionEvent);
        }
        float rawY = motionEvent.getRawY();
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                Activity activity = this.n;
                if (activity != null && !((MainActivity) activity).m()) {
                    ((MainActivity) this.n).a(true, true);
                }
                a();
                a aVar = this.k;
                if (aVar != null) {
                    aVar.a();
                }
            } else if (action == 2) {
                Activity activity2 = this.n;
                if (activity2 != null && ((MainActivity) activity2).m()) {
                    ((MainActivity) this.n).a(false, true);
                }
                a(rawY - this.f);
            }
            return true;
        }
        this.f = rawY;
        return true;
    }

    public void setActivity(Activity activity) {
        if (activity != null && (activity instanceof MainActivity)) {
            this.n = activity;
        }
    }

    public void setCanRefresh(boolean z) {
        this.i = z;
    }

    public void setIntercept(boolean z) {
        this.j = z;
    }

    public void setReleaseListener(a aVar) {
        this.k = aVar;
    }
}
