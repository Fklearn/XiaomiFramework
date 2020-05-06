package com.miui.gamebooster.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class IncomingCallFloatBall extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private WindowManager f5254a;

    /* renamed from: b  reason: collision with root package name */
    private float f5255b;

    /* renamed from: c  reason: collision with root package name */
    private float f5256c;

    /* renamed from: d  reason: collision with root package name */
    private int f5257d;
    private int e;
    private int f;
    private int g;
    private TextView h;
    private TextView i;
    private Button j;
    private a k;
    private float l;
    private boolean m;
    private int n;
    private int o;
    private boolean p;
    private Context q;

    public interface a {
        void a();
    }

    private class b implements TypeEvaluator<Point> {
        private b() {
        }

        /* synthetic */ b(IncomingCallFloatBall incomingCallFloatBall, t tVar) {
            this();
        }

        /* renamed from: a */
        public Point evaluate(float f, Point point, Point point2) {
            int i = point.x;
            int i2 = point.y;
            return new Point((int) (((float) i) + (((float) (point2.x - i)) * f)), (int) (((float) i2) + (f * ((float) (point2.y - i2)))));
        }
    }

    public IncomingCallFloatBall(Context context) {
        super(context);
        this.q = context;
    }

    public IncomingCallFloatBall(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.q = context;
    }

    public IncomingCallFloatBall(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.q = context;
    }

    public static IncomingCallFloatBall a(Context context) {
        return (IncomingCallFloatBall) LayoutInflater.from(context).inflate(R.layout.game_booster_call_float_ball, (ViewGroup) null);
    }

    private void a(int i2, int i3, boolean z) {
        if (z) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
            ValueAnimator ofObject = ValueAnimator.ofObject(new b(this, (t) null), new Object[]{new Point(layoutParams.x, layoutParams.y), new Point(i2, i3)});
            ofObject.setDuration(300);
            ofObject.addUpdateListener(new t(this));
            ofObject.setInterpolator(new u());
            ofObject.start();
            return;
        }
        a(i2, i3);
    }

    private void e() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
        int i2 = layoutParams.y;
        if (i2 != 0 && i2 != this.o - this.g) {
            int i3 = layoutParams.x;
            int i4 = this.n;
            int i5 = this.f;
            if (i3 < (i4 - i5) / 2) {
                a(0, i2, true);
            } else {
                a(i4 - i5, i2, true);
            }
        }
    }

    public void a() {
        this.f5254a.removeView(this);
        this.p = false;
    }

    public void a(int i2, int i3) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
        if (i2 < 0) {
            i2 = 0;
        } else {
            int i4 = this.n;
            int i5 = this.f;
            if (i2 > i4 - i5) {
                i2 = i4 - i5;
            }
        }
        if (i3 < 0) {
            i3 = 0;
        } else {
            int i6 = this.o;
            int i7 = this.g;
            if (i3 > i6 - i7) {
                i3 = i6 - i7;
            }
        }
        layoutParams.x = i2;
        layoutParams.y = i3;
        if (this.p) {
            this.f5254a.updateViewLayout(this, layoutParams);
        }
    }

    public void b() {
        this.f5254a = (WindowManager) this.q.getSystemService("window");
        this.h = (TextView) findViewById(R.id.call_name);
        this.i = (TextView) findViewById(R.id.call_duration);
        this.j = (Button) findViewById(R.id.hang_up);
        this.j.setOnClickListener(this);
        this.l = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void c() {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        this.n = rect.width();
        this.o = rect.height();
    }

    public void d() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(com.xiaomi.stat.c.b.n);
        layoutParams.format = -3;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.flags = 4136;
        layoutParams.gravity = 8388659;
        Display defaultDisplay = this.f5254a.getDefaultDisplay();
        int rotation = defaultDisplay.getRotation();
        if (rotation == 0 || rotation == 2) {
            Point point = new Point();
            defaultDisplay.getSize(point);
            measure(View.MeasureSpec.makeMeasureSpec(point.x, 0), View.MeasureSpec.makeMeasureSpec(point.y, Integer.MIN_VALUE));
            layoutParams.x = (point.x / 2) - (getMeasuredWidth() / 2);
        } else {
            layoutParams.x = 0;
        }
        layoutParams.y = 0;
        this.f5254a.addView(this, layoutParams);
        this.p = true;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        return super.onApplyWindowInsets(windowInsets);
    }

    public void onClick(View view) {
        a aVar = this.k;
        if (aVar != null) {
            aVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        c();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
        a(layoutParams.x, layoutParams.y);
        e();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        b();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.f5255b = rawX;
            this.f5256c = rawY;
        } else if (action != 1) {
            if (action == 2 && !this.m && Math.max(Math.abs(rawX - this.f5255b), Math.abs(rawY - this.f5256c)) > this.l) {
                this.m = true;
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
                this.f5255b = rawX;
                this.f5256c = rawY;
                this.f5257d = layoutParams.x;
                this.e = layoutParams.y;
            }
            return this.m;
        }
        this.m = false;
        return this.m;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
        if (this.n == 0) {
            c();
        }
        this.f = i4 - i2;
        this.g = i5 - i3;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int action = motionEvent.getAction();
        if (action == 0) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
            this.f5257d = layoutParams.x;
            this.e = layoutParams.y;
        } else if (action == 1) {
            e();
            this.m = false;
        } else if (action == 2) {
            a((int) (((float) this.f5257d) + (rawX - this.f5255b)), (int) (((float) this.e) + (rawY - this.f5256c)));
        }
        return true;
    }

    public void setCallDuration(String str) {
        this.i.setText(str);
    }

    public void setCallerName(String str) {
        this.h.setText(str);
    }

    public void setOnHangUpClickListener(a aVar) {
        this.k = aVar;
    }
}
