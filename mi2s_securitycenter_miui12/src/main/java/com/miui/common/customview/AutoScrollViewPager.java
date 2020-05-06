package com.miui.common.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.lang.reflect.Field;

public class AutoScrollViewPager extends ViewPager {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public long f3776a = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;

    /* renamed from: b  reason: collision with root package name */
    private int f3777b = 1;

    /* renamed from: c  reason: collision with root package name */
    private boolean f3778c = true;

    /* renamed from: d  reason: collision with root package name */
    private boolean f3779d = true;
    private int e = 0;
    private boolean f = true;
    /* access modifiers changed from: private */
    public double g = 1.0d;
    /* access modifiers changed from: private */
    public double h = 1.0d;
    private Handler i;
    private boolean j = false;
    private boolean k = false;
    private float l = 0.0f;
    private float m = 0.0f;
    /* access modifiers changed from: private */
    public d n = null;
    int o = -1;
    int p = -1;

    private class a extends Handler {
        private a() {
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0) {
                AutoScrollViewPager.this.n.a(AutoScrollViewPager.this.g);
                AutoScrollViewPager.this.a();
                AutoScrollViewPager.this.n.a(AutoScrollViewPager.this.h);
                AutoScrollViewPager autoScrollViewPager = AutoScrollViewPager.this;
                autoScrollViewPager.a(autoScrollViewPager.f3776a + ((long) AutoScrollViewPager.this.n.getDuration()));
            }
        }
    }

    public AutoScrollViewPager(Context context) {
        super(context);
        d();
    }

    public AutoScrollViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        d();
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        this.i.removeMessages(0);
        this.i.sendEmptyMessageDelayed(0, j2);
    }

    private void d() {
        this.i = new a();
        e();
    }

    private void e() {
        try {
            Field declaredField = ViewPager.class.getDeclaredField("mScroller");
            declaredField.setAccessible(true);
            Field declaredField2 = ViewPager.class.getDeclaredField("sInterpolator");
            declaredField2.setAccessible(true);
            this.n = new d(getContext(), (Interpolator) declaredField2.get((Object) null));
            declaredField.set(this, this.n);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void a() {
        int count;
        int i2;
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        if (adapter != null && (count = adapter.getCount()) > 1) {
            int i3 = this.f3777b == 0 ? currentItem - 1 : currentItem + 1;
            if (i3 < 0) {
                if (this.f3778c) {
                    i2 = count - 1;
                } else {
                    return;
                }
            } else if (i3 != count) {
                setCurrentItem(i3, true);
                return;
            } else if (this.f3778c) {
                i2 = 0;
            } else {
                return;
            }
            setCurrentItem(i2, this.f);
        }
    }

    public void a(int i2) {
        this.j = true;
        a((long) i2);
    }

    public void b() {
        this.j = true;
        a((long) (((double) this.f3776a) + ((((double) this.n.getDuration()) / this.g) * this.h)));
    }

    public void c() {
        this.j = false;
        this.i.removeMessages(0);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (this.f3779d) {
            if (actionMasked == 0 && this.j) {
                this.k = true;
                c();
            } else if ((actionMasked == 3 || actionMasked == 1) && this.k) {
                b();
            }
        }
        int i2 = this.e;
        if (i2 == 2 || i2 == 1) {
            this.l = motionEvent.getX();
            if (motionEvent.getAction() == 0) {
                this.m = this.l;
            }
            int currentItem = getCurrentItem();
            PagerAdapter adapter = getAdapter();
            int count = adapter == null ? 0 : adapter.getCount();
            if ((currentItem == 0 && this.m <= this.l) || (currentItem == count - 1 && this.m >= this.l)) {
                if (this.e == 2) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (count > 1) {
                        setCurrentItem((count - currentItem) - 1, this.f);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return AutoScrollViewPager.super.dispatchTouchEvent(motionEvent);
            }
        }
        return AutoScrollViewPager.super.dispatchTouchEvent(motionEvent);
    }

    public int getDirection() {
        return this.f3777b == 0 ? 0 : 1;
    }

    public long getInterval() {
        return this.f3776a;
    }

    public int getSlideBorderMode() {
        return this.e;
    }

    public void setAutoScrollDurationFactor(double d2) {
        this.g = d2;
    }

    public void setBorderAnimation(boolean z) {
        this.f = z;
    }

    public void setCycle(boolean z) {
        this.f3778c = z;
    }

    public void setDirection(int i2) {
        this.f3777b = i2;
    }

    public void setDuration(int i2) {
        d dVar = this.n;
        if (dVar != null) {
            dVar.a(i2);
        }
    }

    public void setInterval(long j2) {
        this.f3776a = j2;
    }

    public void setSlideBorderMode(int i2) {
        this.e = i2;
    }

    public void setStopScrollWhenTouch(boolean z) {
        this.f3779d = z;
    }

    public void setSwipeScrollDurationFactor(double d2) {
        this.h = d2;
    }
}
