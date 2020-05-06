package com.miui.permcenter.privacymanager.b;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;

public class m extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private static int f6369a;

    /* renamed from: b  reason: collision with root package name */
    private static int f6370b;

    /* renamed from: c  reason: collision with root package name */
    private final Paint f6371c;

    /* renamed from: d  reason: collision with root package name */
    private final Paint f6372d;
    private final Paint e;
    private final Paint f;
    private final Xfermode g;
    private final Context h;
    /* access modifiers changed from: private */
    public final Rect i;
    /* access modifiers changed from: private */
    public View j;
    /* access modifiers changed from: private */
    public RectF k;
    private float l;
    /* access modifiers changed from: private */
    public float m;
    /* access modifiers changed from: private */
    public boolean n;
    private boolean o;
    /* access modifiers changed from: private */
    public int p;
    /* access modifiers changed from: private */
    public float q;
    private float r;
    /* access modifiers changed from: private */
    public float s;
    /* access modifiers changed from: private */
    public float t;
    private int u;
    /* access modifiers changed from: private */
    public boolean v;
    /* access modifiers changed from: private */
    public d w;
    /* access modifiers changed from: private */
    public c x;
    /* access modifiers changed from: private */
    public b y;
    private f z;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private View f6373a;

        /* renamed from: b  reason: collision with root package name */
        private String f6374b;

        /* renamed from: c  reason: collision with root package name */
        private c f6375c = c.AUTO;

        /* renamed from: d  reason: collision with root package name */
        private b f6376d;
        private Context e;
        private d f;

        public a(Context context) {
            this.e = context;
        }

        public a a(View view) {
            this.f6373a = view;
            return this;
        }

        public a a(b bVar) {
            this.f6376d = bVar;
            return this;
        }

        public a a(String str) {
            this.f6374b = str;
            return this;
        }

        public m a() {
            m mVar = new m(this.e, this.f6373a, (h) null);
            c cVar = this.f6375c;
            if (cVar == null) {
                cVar = c.AUTO;
            }
            c unused = mVar.x = cVar;
            b bVar = this.f6376d;
            if (bVar == null) {
                bVar = b.TARGET_VIEW;
            }
            b unused2 = mVar.y = bVar;
            float f2 = this.e.getResources().getDisplayMetrics().density;
            mVar.setMessage(this.f6374b);
            d dVar = this.f;
            if (dVar != null) {
                d unused3 = mVar.w = dVar;
            }
            return mVar;
        }
    }

    public enum b {
        OUTSIDE,
        ANYWHERE,
        TARGET_VIEW,
        SELF_VIEW
    }

    public enum c {
        AUTO,
        CENTER
    }

    public interface d {
        void a(View view);
    }

    private m(Context context, View view) {
        super(context);
        this.f6371c = new Paint();
        this.f6372d = new Paint();
        this.e = new Paint();
        this.f = new Paint(1);
        this.g = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        this.i = new Rect();
        this.p = 0;
        this.v = false;
        this.h = context;
        int color = getContext().getResources().getColor(R.color.sensitive_permission_intro_message_bg);
        f6369a = color;
        f6370b = color;
        setWillNotDraw(false);
        setLayerType(2, (Paint) null);
        this.j = view;
        this.l = context.getResources().getDisplayMetrics().density;
        c();
        int[] iArr = new int[2];
        this.j.getLocationOnScreen(iArr);
        this.k = new RectF((float) iArr[0], (float) iArr[1], (float) (iArr[0] + this.j.getWidth()), (float) (iArr[1] + this.j.getHeight()));
        this.z = new f(getContext());
        f fVar = this.z;
        int i2 = this.u;
        fVar.setPadding(i2, i2, i2, i2);
        this.z.a(color);
        addView(this.z, new FrameLayout.LayoutParams(-1, -2));
        setMessageLocation(e());
        getViewTreeObserver().addOnGlobalLayoutListener(new h(this));
    }

    /* synthetic */ m(Context context, View view, h hVar) {
        this(context, view);
    }

    private boolean a(View view, float f2, float f3) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        return f2 >= ((float) i2) && f2 <= ((float) (i2 + view.getWidth())) && f3 >= ((float) i3) && f3 <= ((float) (i3 + view.getHeight()));
    }

    private void c() {
        float f2 = this.l;
        this.r = 3.0f * f2;
        this.s = 8.0f * f2;
        this.t = 15.0f * f2;
        this.u = (int) (f2 * 13.0f);
    }

    private boolean d() {
        return getResources().getConfiguration().orientation != 1;
    }

    /* access modifiers changed from: private */
    public Point e() {
        float f2;
        int width = this.x == c.CENTER ? (int) ((this.k.left - ((float) (this.z.getWidth() / 2))) + ((float) (this.j.getWidth() / 2))) : ((int) this.k.right) - this.z.getWidth();
        if (d()) {
            width -= getNavigationBarSize();
        }
        if (this.z.getWidth() + width > getWidth()) {
            width = getWidth() - this.z.getWidth();
        }
        if (width < 0) {
            width = 0;
        }
        if (this.k.top + this.t > ((float) (getHeight() / 2))) {
            this.n = false;
            f2 = (this.k.top - ((float) this.z.getHeight())) - this.t;
        } else {
            this.n = true;
            f2 = this.k.top + ((float) this.j.getHeight()) + this.t;
        }
        this.p = (int) f2;
        if (this.p < 0) {
            this.p = 0;
        }
        return new Point(width, this.p);
    }

    /* access modifiers changed from: private */
    public void f() {
        if (!this.v) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.m, this.q});
            ofFloat.addUpdateListener(new i(this, ofFloat));
            ofFloat.setDuration(700);
            ofFloat.start();
            ofFloat.addListener(new j(this));
        }
    }

    private int getNavigationBarSize() {
        Resources resources = getContext().getResources();
        int identifier = resources.getIdentifier("navigation_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void setMessageLocation(Point point) {
        this.z.setX((float) point.x);
        this.z.setY((float) point.y);
        postInvalidate();
    }

    public void a() {
        animate().alpha(0.0f).setDuration(500).setListener(new k(this, this));
        this.o = false;
        d dVar = this.w;
        if (dVar != null) {
            dVar.a(this.j);
        }
    }

    public void b() {
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setClickable(false);
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        startAnimation(alphaAnimation);
        this.o = true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.j != null) {
            this.f6371c.setColor(-1728053248);
            this.f6371c.setStyle(Paint.Style.FILL);
            this.f6371c.setAntiAlias(true);
            canvas.drawRect(this.i, this.f6371c);
            this.f6372d.setStyle(Paint.Style.FILL);
            this.f6372d.setColor(f6370b);
            this.f6372d.setStrokeWidth(this.r);
            this.f6372d.setAntiAlias(true);
            this.e.setStyle(Paint.Style.FILL);
            this.e.setColor(f6369a);
            this.e.setAntiAlias(true);
            RectF rectF = this.k;
            float f2 = rectF.right;
            float f3 = rectF.left;
            float f4 = f3 + ((f2 - f3) / 4.0f);
            canvas.drawLine(f4, this.q, f4, this.m, this.f6372d);
            int i2 = 16;
            if (!this.n) {
                i2 = -16;
            }
            Path path = new Path();
            float f5 = (float) i2;
            path.moveTo(f4, this.q - f5);
            path.lineTo(f4 - f5, this.q + f5);
            path.lineTo(f4 + f5, this.q + f5);
            path.lineTo(f4, this.q - f5);
            path.close();
            canvas.drawPath(path, this.e);
            this.f.setXfermode(this.g);
            this.f.setAntiAlias(true);
            canvas.drawRoundRect(this.k, 15.0f, 15.0f, this.f);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
        if (a(r4.z, r0, r1) == false) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        if (a(r4.z, r0, r1) == false) goto L_0x003b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            float r0 = r5.getX()
            float r1 = r5.getY()
            int r5 = r5.getAction()
            if (r5 != 0) goto L_0x0049
            int[] r5 = com.miui.permcenter.privacymanager.b.l.f6368a
            com.miui.permcenter.privacymanager.b.m$b r2 = r4.y
            int r2 = r2.ordinal()
            r5 = r5[r2]
            r2 = 1
            if (r5 == r2) goto L_0x003f
            r3 = 2
            if (r5 == r3) goto L_0x003b
            r3 = 3
            if (r5 == r3) goto L_0x002e
            r3 = 4
            if (r5 == r3) goto L_0x0025
            goto L_0x0048
        L_0x0025:
            com.miui.permcenter.privacymanager.b.f r5 = r4.z
            boolean r5 = r4.a(r5, r0, r1)
            if (r5 == 0) goto L_0x0048
            goto L_0x003b
        L_0x002e:
            android.graphics.RectF r5 = r4.k
            boolean r5 = r5.contains(r0, r1)
            if (r5 == 0) goto L_0x0048
            android.view.View r5 = r4.j
            r5.performClick()
        L_0x003b:
            r4.a()
            goto L_0x0048
        L_0x003f:
            com.miui.permcenter.privacymanager.b.f r5 = r4.z
            boolean r5 = r4.a(r5, r0, r1)
            if (r5 != 0) goto L_0x0048
            goto L_0x003b
        L_0x0048:
            return r2
        L_0x0049:
            r5 = 0
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.b.m.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setMessage(String str) {
        this.z.a(str);
    }
}
