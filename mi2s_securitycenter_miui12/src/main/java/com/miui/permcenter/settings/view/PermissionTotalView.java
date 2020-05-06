package com.miui.permcenter.settings.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import b.c.a.b.d.d;
import com.miui.permcenter.privacymanager.a.d;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PermissionTotalView extends View implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6582a = "PermissionTotalView";
    private PointF A;
    protected Paint B;
    protected float C;
    protected float D;
    protected float E;
    protected String F;
    protected String G;
    private float H;
    protected float I;
    protected float J;
    private boolean K;
    private float L;
    private float M;
    private float N;
    private float O;
    private boolean P;
    /* access modifiers changed from: private */
    public boolean Q;
    /* access modifiers changed from: private */
    public boolean R;
    private ValueAnimator S;
    private ValueAnimator T;
    /* access modifiers changed from: private */
    public int U;
    protected Context V;
    /* access modifiers changed from: private */
    public Handler W = new Handler();
    private ArrayList<d> aa = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    protected int f6583b;
    private long ba;

    /* renamed from: c  reason: collision with root package name */
    protected int f6584c;
    private boolean ca = false;

    /* renamed from: d  reason: collision with root package name */
    private int f6585d;
    private ValueAnimator da;
    protected float e;
    private int ea;
    protected float f;
    private int fa;
    private Paint g;
    private ValueAnimator ga;
    protected int h;
    /* access modifiers changed from: private */
    public int ha;
    protected float i;
    protected float j;
    protected float k;
    protected float l = 8.0f;
    protected List<RectF> m;
    protected RectF n;
    protected Paint o;
    protected Paint p;
    private float q;
    protected Paint r;
    private Paint s;
    private RectF t;
    private float u;
    protected List<RectF> v;
    private int w;
    private boolean x;
    protected RectF y;
    protected Paint z;

    public PermissionTotalView(Context context) {
        super(context);
        this.V = context;
        h();
    }

    public PermissionTotalView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.V = context;
        h();
    }

    public PermissionTotalView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.V = context;
        h();
    }

    public static float a(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top;
    }

    public static float a(Paint paint, float f2) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float f3 = fontMetrics.bottom;
        return (f2 + ((f3 - fontMetrics.top) / 2.0f)) - f3;
    }

    public static int a(Context context, float f2) {
        return (int) ((f2 * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private Bitmap a(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    private Bitmap a(String str, Object obj) {
        try {
            Drawable applicationIcon = this.V.getPackageManager().getApplicationIcon(d.a.PKG_ICON.a(str));
            if (applicationIcon instanceof BitmapDrawable) {
                return ((BitmapDrawable) applicationIcon).getBitmap();
            }
            if (applicationIcon != null) {
                return a(applicationIcon);
            }
            return null;
        } catch (PackageManager.NameNotFoundException e2) {
            throw new IOException(e2);
        }
    }

    private void a(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private void a(Canvas canvas) {
        int i2;
        this.m.clear();
        this.v.clear();
        float f2 = 50.0f;
        int i3 = 0;
        while (true) {
            i2 = this.h;
            if (i3 >= i2) {
                break;
            }
            boolean z2 = this.x;
            this.o.setColor(a(i3));
            float b2 = b(i3);
            int i4 = this.f6584c;
            float f3 = ((float) this.f6584c) + ((((b2 - ((float) i4)) * ((float) this.ha)) * 1.0f) / 100.0f);
            this.n = new RectF(f2, f3, this.e + f2, (float) (i4 + 100));
            canvas.save();
            canvas.clipRect(new RectF(f2, f3, this.e + f2, (float) this.f6584c));
            canvas.drawRoundRect(this.n, 12.0f, 12.0f, this.o);
            canvas.restore();
            float a2 = a(i3, f2);
            float f4 = this.q;
            float a3 = a(i3, f2);
            float f5 = this.u;
            this.t = new RectF(a2, f4, a3 + f5, this.q + f5);
            if (i(i3) != null) {
                canvas.drawBitmap(i(i3), (Rect) null, this.t, this.s);
            }
            this.v.add(this.t);
            this.m.add(this.n);
            f2 += this.k;
            i3++;
        }
        if (i2 == 0 && !this.ca) {
            c(canvas);
            b(canvas);
        }
    }

    private void a(RectF rectF, int i2) {
        Paint.Align align;
        Paint paint;
        if (this.A == null) {
            this.A = new PointF();
        }
        PointF pointF = this.A;
        float width = rectF.right - (rectF.width() / 2.0f);
        pointF.x = width;
        this.A.y = rectF.top;
        i();
        float f2 = this.I;
        float f3 = (f2 / 2.0f) + width;
        float f4 = width - (f2 / 2.0f);
        int i3 = this.f6583b;
        if (f3 > ((float) i3)) {
            f3 = (float) i3;
            f4 = f3 - f2;
        }
        float f5 = 0.0f;
        if (f4 < 0.0f) {
            f3 = this.I + 0.0f;
            f4 = 0.0f;
        }
        if (this.y == null) {
            this.y = new RectF(0.0f, 0.0f, 0.0f, this.J);
        }
        float b2 = (b(i2) - 50.0f) - this.J;
        if (b2 >= 0.0f) {
            f5 = b2;
        }
        RectF rectF2 = this.y;
        rectF2.left = f4;
        rectF2.right = f3;
        rectF2.top = f5;
        rectF2.bottom = f5 + this.J;
        if (a()) {
            this.H = f3 - d((int) R.dimen.view_dimen_30);
            paint = this.B;
            align = Paint.Align.RIGHT;
        } else {
            this.H = f4 + d((int) R.dimen.view_dimen_30);
            paint = this.B;
            align = Paint.Align.LEFT;
        }
        paint.setTextAlign(align);
    }

    private Bitmap b(String str, Object obj) {
        try {
            Drawable applicationIcon = this.V.getPackageManager().getApplicationIcon(d.a.PKG_ICON_XSPACE.a(str));
            Method declaredMethod = Class.forName("miui.securityspace.XSpaceUserHandle").getDeclaredMethod("getXSpaceIcon", new Class[]{Context.class, Drawable.class});
            declaredMethod.setAccessible(true);
            Drawable drawable = (Drawable) declaredMethod.invoke((Object) null, new Object[]{this.V, applicationIcon});
            if (applicationIcon instanceof BitmapDrawable) {
                if (drawable != null) {
                    return ((BitmapDrawable) drawable).getBitmap();
                }
            } else if (drawable != null) {
                return a(drawable);
            }
        } catch (Exception unused) {
            Log.e(f6582a, "LOG_BASE_IMAGE_DOWNLOADER");
        }
        return null;
    }

    private void b(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
        }
    }

    private void b(Canvas canvas) {
        float f2 = (float) (this.f6584c - 30);
        float f3 = (((((float) this.f6583b) - this.e) - this.l) - 100.0f) / 5.0f;
        float f4 = 50.0f;
        for (int i2 = 0; i2 < 6; i2++) {
            this.n = new RectF(f4, f2, this.e + f4, (float) (this.f6584c + 100));
            canvas.save();
            canvas.clipRect(new RectF(f4, f2, this.e + f4, (float) this.f6584c));
            canvas.drawRoundRect(this.n, 12.0f, 12.0f, this.p);
            canvas.restore();
            f4 += f3;
        }
    }

    private void c() {
        this.e = 50.0f;
        this.h = getDataSize();
        this.i = (float) (getMaxYValue() + 8);
        this.j = getMyBarMaxHeight();
        int i2 = this.h;
        if (i2 <= 1) {
            this.k = 0.0f;
        } else if (i2 == 2) {
            this.k = (((((float) this.f6583b) - this.e) - this.l) - 100.0f) / ((float) i2);
        } else {
            this.k = (((((float) this.f6583b) - this.e) - this.l) - 100.0f) / ((float) (i2 - 1));
        }
    }

    private void c(Canvas canvas) {
        String str;
        int i2;
        Resources resources;
        if (this.ca) {
            resources = this.V.getResources();
            i2 = R.string.loading;
        } else {
            Resources resources2 = this.V.getResources();
            i2 = R.string.privacy_empty_location;
            str = resources2.getString(R.string.privacy_empty_location);
            long j2 = this.ba;
            if (j2 == 32) {
                resources = this.V.getResources();
            } else if (j2 == 16) {
                resources = this.V.getResources();
                i2 = R.string.privacy_empty_call;
            } else if (j2 == 8) {
                resources = this.V.getResources();
                i2 = R.string.privacy_empty_contact;
            } else if (j2 == PermissionManager.PERM_ID_AUDIO_RECORDER) {
                resources = this.V.getResources();
                i2 = R.string.privacy_empty_record;
            } else {
                if (j2 == PermissionManager.PERM_ID_EXTERNAL_STORAGE) {
                    resources = this.V.getResources();
                    i2 = R.string.privacy_empty_storage;
                }
                canvas.drawText(str, ((float) (this.f6583b / 2)) - (a(str, this.r) / 2.0f), (float) ((this.f6584c / 2) - 40), this.r);
            }
        }
        str = resources.getString(i2);
        canvas.drawText(str, ((float) (this.f6583b / 2)) - (a(str, this.r) / 2.0f), (float) ((this.f6584c / 2) - 40), this.r);
    }

    private void d() {
        if (this.T == null) {
            this.T = ValueAnimator.ofInt(new int[]{254, 0});
            this.T.setInterpolator(new LinearInterpolator());
            this.T.setDuration(400);
            this.T.addListener(new h(this));
            this.T.addUpdateListener(new i(this));
        }
        this.T.start();
    }

    private void d(Canvas canvas) {
        float f2 = (float) this.f6584c;
        if (!this.ca) {
            canvas.drawLine(0.0f, f2, (float) this.f6583b, f2, this.g);
        }
        if (this.h != 0) {
            float f3 = ((float) this.f6584c) - (this.j / 2.0f);
            Canvas canvas2 = canvas;
            canvas2.drawLine(0.0f, f3, (float) this.f6583b, f3, this.g);
            float f4 = ((float) this.f6584c) - this.j;
            canvas2.drawLine(0.0f, f4, (float) this.f6583b, f4, this.g);
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        this.x = false;
        this.w = -1;
        this.I = getTipRectWidth();
        invalidate();
    }

    private void e(Canvas canvas) {
        RectF rectF = this.y;
        if (rectF != null) {
            canvas.drawRoundRect(rectF, 10.0f, 10.0f, this.z);
            this.B.setColor(c((int) R.color.pm_setting_chart_white_light));
            this.B.setTextSize(this.C);
            float b2 = (b(this.w) - 40.0f) - this.J;
            if (b2 < 0.0f) {
                b2 = 0.0f;
            }
            Paint paint = this.B;
            float a2 = a(paint, (a(paint) / 2.0f) + d((int) R.dimen.view_dimen_18) + b2);
            canvas.drawText(this.F, this.H, a2, this.B);
            this.B.setColor(c((int) R.color.white));
            this.B.setTextSize(this.D);
            canvas.drawText(this.G, this.H, a(this.B, a2 + ((float) a(this.V.getApplicationContext(), 1.09f)) + (a(this.B) / 2.0f)), this.B);
        }
    }

    private void f() {
        if (this.m != null) {
            int i2 = (int) ((this.L - (this.e / 2.0f)) / this.k);
            int i3 = this.h;
            if (i2 >= i3 - 1) {
                i2 = i3 - 1;
            }
            if (i2 <= 0) {
                i2 = 0;
            }
            try {
                RectF rectF = this.m.get(i2);
                if (rectF != null && this.L <= rectF.right + 50.0f && this.L >= rectF.left - 50.0f) {
                    this.V.startActivity(PrivacyDetailActivity.a(this.aa.get(i2).f6337b, this.aa.get(i2).f6336a, "statics"));
                }
            } catch (Exception e2) {
                Log.e(f6582a, "doAppClick: ", e2);
            }
        }
    }

    private void g() {
        List<RectF> list = this.m;
        if (list != null) {
            if (this.h != list.size()) {
                c();
                invalidate();
            }
            int i2 = (int) ((this.L - (this.e / 2.0f)) / this.k);
            int i3 = this.h;
            if (i2 >= i3 - 1) {
                i2 = i3 - 1;
            }
            if (i2 <= 0) {
                i2 = 0;
            }
            try {
                RectF rectF = this.m.get(i2);
                if (rectF != null && this.L <= rectF.right + 50.0f && this.L >= rectF.left - 50.0f && rectF.height() > 0.0f) {
                    this.w = i2;
                    this.x = true;
                    e(i2);
                    f(i2);
                    a(rectF, i2);
                    l();
                }
            } catch (Exception e2) {
                Log.e(f6582a, "doClickAction : ", e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void g(int i2) {
        this.r.setAlpha(i2);
        this.p.setAlpha((i2 * 255) / 80);
    }

    private void h() {
        this.K = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
        this.m = new ArrayList();
        this.v = new ArrayList();
        this.f = d((int) R.dimen.view_dimen_1);
        this.o = new Paint();
        this.o.setAntiAlias(true);
        this.p = new Paint();
        this.p.setAntiAlias(true);
        this.p.setColor(Color.parseColor("#EAEAEA"));
        this.s = new Paint(1);
        this.g = new Paint(1);
        this.g.setColor(c((int) R.color.pm_setting_chart_line));
        this.g.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0.0f));
        this.g.setStrokeWidth(this.f);
        this.z = new Paint(1);
        this.z.setStyle(Paint.Style.FILL);
        this.z.setColor(c((int) R.color.pm_setting_chart_blue));
        this.B = new Paint(1);
        this.B.setTextAlign(Paint.Align.LEFT);
        this.B.setTypeface(Typeface.DEFAULT_BOLD);
        this.C = d((int) R.dimen.text_font_size_34);
        this.D = d((int) R.dimen.text_font_size_40);
        this.E = d((int) R.dimen.text_font_size_38);
        this.I = d((int) R.dimen.view_dimen_313);
        this.J = d((int) R.dimen.view_dimen_152);
        this.u = d((int) R.dimen.view_dimen_79);
        this.r = new Paint(1);
        this.r.setTextAlign(Paint.Align.LEFT);
        this.r.setColor(c((int) R.color.color_gtb_svg_disable));
        this.r.setTextSize(this.E);
        this.ca = true;
    }

    /* access modifiers changed from: private */
    public void h(int i2) {
        this.z.setAlpha(i2);
        this.B.setAlpha(i2);
        invalidate();
    }

    private Bitmap i(int i2) {
        String str = this.aa.get(i2).f6337b;
        int i3 = this.aa.get(i2).f6336a;
        String concat = (i3 == 999 ? "pkg_icon_xspace://" : "pkg_icon://").concat(str);
        if (i3 != 999) {
            return a(concat, (Object) null);
        }
        try {
            return b(concat, (Object) null);
        } catch (Exception e2) {
            Log.d(f6582a, e2.toString());
            return null;
        }
    }

    private void i() {
        this.B.setTextSize(this.C);
        float a2 = a(this.F, this.B);
        this.B.setTextSize(this.D);
        float max = Math.max(a(this.G, this.B), a2);
        if (this.I - (d((int) R.dimen.view_dimen_30) * 2.0f) < max) {
            this.I = max + (d((int) R.dimen.view_dimen_30) * 2.0f);
        }
    }

    private void j() {
        if (this.ga == null) {
            this.ga = ValueAnimator.ofInt(new int[]{0, 100});
            this.ga.setDuration(400);
            this.ga.setInterpolator(new LinearInterpolator());
            this.ga.addUpdateListener(new e(this));
        }
        this.ga.start();
    }

    private void k() {
        if (this.da == null) {
            this.da = ValueAnimator.ofInt(new int[]{0, 80});
            this.da.setDuration(300);
            this.da.setInterpolator(new LinearInterpolator());
            this.da.addUpdateListener(new d(this));
        }
        this.da.start();
    }

    private void l() {
        if (this.S == null) {
            this.S = ValueAnimator.ofInt(new int[]{0, 255});
            this.S.setDuration(400);
            this.S.setInterpolator(new LinearInterpolator());
            this.S.addListener(new f(this));
            this.S.addUpdateListener(new g(this));
        }
        this.S.start();
        this.R = true;
    }

    /* access modifiers changed from: protected */
    public float a(int i2, float f2) {
        return (f2 + (this.e / 2.0f)) - (this.u / 2.0f);
    }

    /* access modifiers changed from: protected */
    public float a(String str, Paint paint) {
        return paint.measureText(str);
    }

    /* access modifiers changed from: protected */
    public int a(int i2) {
        return this.x ? i2 == this.w ? c((int) R.color.pm_setting_chart_blue) : Color.argb(255 - ((this.U * 205) / 255), 0, 153, 255) : c((int) R.color.pm_setting_chart_blue);
    }

    public void a(boolean z2) {
        this.ca = z2;
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        return this.K;
    }

    /* access modifiers changed from: protected */
    public float b(int i2) {
        float f2 = (float) this.aa.get(i2).f6338c;
        if (f2 == 0.0f) {
            return (float) (this.f6584c + 100);
        }
        int i3 = this.f6584c;
        float f3 = this.j;
        float f4 = (((float) i3) - f3) + (f3 * (1.0f - (f2 / this.i)));
        return ((float) i3) - f4 > 3.0f ? f4 : (float) (i3 - 3);
    }

    public void b() {
        b(this.T);
        b(this.S);
        this.W.removeCallbacksAndMessages((Object) null);
    }

    /* access modifiers changed from: protected */
    public int c(int i2) {
        return this.V.getResources().getColor(i2);
    }

    /* access modifiers changed from: protected */
    public float d(int i2) {
        return (float) this.V.getResources().getDimensionPixelSize(i2);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        ViewParent viewParent;
        int action = motionEvent.getAction();
        boolean z2 = true;
        if (action != 0) {
            if (action == 2) {
                Log.d(f6582a, "dispatchTouchEvent: move");
                int abs = Math.abs(((int) motionEvent.getX()) - this.ea);
                if (abs <= Math.abs(((int) motionEvent.getY()) - this.fa) || abs >= 25) {
                    viewParent = getParent();
                    z2 = false;
                    viewParent.requestDisallowInterceptTouchEvent(z2);
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        Log.d(f6582a, "dispatchTouchEvent: down");
        this.ea = (int) motionEvent.getX();
        this.fa = (int) motionEvent.getY();
        viewParent = getParent();
        viewParent.requestDisallowInterceptTouchEvent(z2);
        return super.dispatchTouchEvent(motionEvent);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        d(canvas);
        a(canvas);
        if (this.x) {
            e(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void e(int i2) {
        Resources resources;
        int i3;
        String string;
        long j2 = this.ba;
        if (j2 != 32) {
            if (j2 == 16) {
                resources = this.V.getResources();
                i3 = R.string.privacy_pop_call;
            } else if (j2 == 8) {
                resources = this.V.getResources();
                i3 = R.string.privacy_pop_contact;
            } else if (j2 == PermissionManager.PERM_ID_AUDIO_RECORDER) {
                resources = this.V.getResources();
                i3 = R.string.privacy_pop_record;
            } else if (j2 == PermissionManager.PERM_ID_EXTERNAL_STORAGE) {
                resources = this.V.getResources();
                i3 = R.string.privacy_pop_storage;
            }
            string = resources.getString(i3);
            this.F = string;
        }
        string = this.V.getResources().getString(R.string.privacy_pop_location);
        this.F = string;
    }

    /* access modifiers changed from: protected */
    public void f(int i2) {
        int i3 = this.aa.get(i2).f6338c;
        this.G = this.V.getResources().getQuantityString(R.plurals.privacy_pop_times, i3, new Object[]{String.valueOf(i3)});
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.aa.size();
    }

    /* access modifiers changed from: protected */
    public int getMaxYValue() {
        Iterator<com.miui.permcenter.privacymanager.a.d> it = this.aa.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            int i3 = it.next().f6338c;
            if (i2 < i3) {
                i2 = i3;
            }
        }
        return i2;
    }

    /* access modifiers changed from: protected */
    public float getMyBarMaxHeight() {
        return d((int) R.dimen.view_dimen_452);
    }

    /* access modifiers changed from: protected */
    public float getTipRectWidth() {
        return d((int) R.dimen.view_dimen_313);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i3);
        int size2 = View.MeasureSpec.getSize(i3);
        if (mode == 1073741824) {
            this.f6583b = size;
        } else {
            this.f6583b = getMeasuredWidth();
            size = 0;
        }
        if (mode2 == 1073741824) {
            this.f6584c = size2;
        } else {
            this.f6584c = this.f6585d;
            size2 = 0;
        }
        setMeasuredDimension(size, size2);
        int i4 = this.f6584c;
        float f2 = this.u;
        this.q = ((float) i4) - f2;
        this.f6584c = (int) (((float) i4) - (f2 + 30.0f));
        c();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.P = true;
            this.Q = false;
        } else if (action == 1) {
            this.W.removeCallbacks(this);
            e();
            a(this.S);
            a(this.T);
            float x2 = motionEvent.getX();
            this.L = x2;
            this.N = x2;
            float y2 = motionEvent.getY();
            this.M = y2;
            this.O = y2;
            float x3 = motionEvent.getX();
            float y3 = motionEvent.getY();
            if (Math.abs(x3 - this.L) <= 25.0f || Math.abs(y3 - this.M) <= 25.0f) {
                int i2 = this.f6584c;
                if (y3 > ((float) i2) || this.M > ((float) i2)) {
                    f();
                } else if (this.P) {
                    g();
                }
            }
        }
        return true;
    }

    public void run() {
        if (this.Q) {
            d();
        }
    }

    public void setPermissionType(long j2) {
        this.ba = j2;
    }

    public void setValues(ArrayList<com.miui.permcenter.privacymanager.a.d> arrayList) {
        this.aa = arrayList;
        if (this.aa == null) {
            this.aa = new ArrayList<>();
        }
        if (this.R) {
            d();
        }
        this.x = false;
        requestLayout();
        j();
        k();
    }
}
