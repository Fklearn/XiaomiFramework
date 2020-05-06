package com.miui.optimizecenter.widget.storage;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.i.b.g;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.optimizecenter.storage.v;
import com.miui.optimizecenter.widget.storage.StorageColumnView;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class StorageViewGroup extends LinearLayout implements StorageColumnView.b, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    public static final b[] f5837a = {b.STORAGE_SYSTEM, b.STORAGE_FILE, b.STORAGE_APK, b.STORAGE_VIDEO, b.STORAGE_VOICE, b.STORAGE_IMAGE, b.STORAGE_APP_DATA, b.STORAGE_OTHER};

    /* renamed from: b  reason: collision with root package name */
    private v f5838b;

    /* renamed from: c  reason: collision with root package name */
    private StorageColumnView f5839c;

    /* renamed from: d  reason: collision with root package name */
    private Path f5840d;
    private Path e;
    private Paint f;
    private Paint g;
    private HashMap<b, d> h;
    private LinearLayout i;
    private d j;
    private int k;
    private int l;
    private ValueAnimator m;
    private ValueAnimator n;
    private b o;
    private boolean p;

    class a extends d {
        TextView h;

        a() {
            super();
        }
    }

    static class b implements ValueAnimator.AnimatorUpdateListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<StorageViewGroup> f5841a;

        /* renamed from: b  reason: collision with root package name */
        private b f5842b;

        /* renamed from: c  reason: collision with root package name */
        private b f5843c;

        public b(StorageViewGroup storageViewGroup, b bVar, b bVar2) {
            this.f5841a = new WeakReference<>(storageViewGroup);
            this.f5842b = bVar;
            this.f5843c = bVar2;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            StorageViewGroup storageViewGroup;
            WeakReference<StorageViewGroup> weakReference = this.f5841a;
            if (weakReference != null && (storageViewGroup = (StorageViewGroup) weakReference.get()) != null) {
                storageViewGroup.a(Float.valueOf(valueAnimator.getAnimatedFraction()).floatValue(), this.f5842b, this.f5843c);
            }
        }
    }

    static class c implements ValueAnimator.AnimatorUpdateListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<StorageViewGroup> f5844a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f5845b;

        /* renamed from: c  reason: collision with root package name */
        private b f5846c;

        public c(StorageViewGroup storageViewGroup, boolean z, b bVar) {
            this.f5844a = new WeakReference<>(storageViewGroup);
            this.f5845b = z;
            this.f5846c = bVar;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            StorageViewGroup storageViewGroup;
            WeakReference<StorageViewGroup> weakReference = this.f5844a;
            if (weakReference != null && (storageViewGroup = (StorageViewGroup) weakReference.get()) != null) {
                storageViewGroup.a(this.f5846c, this.f5845b, Float.valueOf(valueAnimator.getAnimatedFraction()).floatValue());
            }
        }
    }

    class d {

        /* renamed from: a  reason: collision with root package name */
        b f5847a;

        /* renamed from: b  reason: collision with root package name */
        View f5848b;

        /* renamed from: c  reason: collision with root package name */
        View f5849c;

        /* renamed from: d  reason: collision with root package name */
        TextView f5850d;
        SizeTextView e;
        ImageView f;

        d() {
        }
    }

    public StorageViewGroup(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public StorageViewGroup(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StorageViewGroup(@NonNull Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5838b = v.DEFAULT;
        this.f5840d = new Path();
        this.e = new Path();
        this.h = new HashMap<>();
        this.k = 5;
        this.l = 70;
        this.f = new Paint(1);
        this.f.setStrokeWidth((float) this.k);
        this.f.setStyle(Paint.Style.STROKE);
        this.f.setDither(true);
        this.g = new Paint(1);
        this.g.setStrokeWidth((float) this.k);
        this.g.setStyle(Paint.Style.STROKE);
        this.g.setDither(true);
        setOrientation(0);
        this.l = context.getResources().getDimensionPixelSize(R.dimen.storage_line_offset);
    }

    private Point a(View view, View view2) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int[] iArr2 = new int[2];
        view2.getLocationInWindow(iArr2);
        return new Point((iArr[0] + (view.getWidth() / 2)) - iArr2[0], (iArr[1] + (view.getHeight() / 2)) - iArr2[1]);
    }

    private void a(b bVar, b bVar2) {
        ValueAnimator valueAnimator = this.n;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        for (d next : this.h.values()) {
            b bVar3 = next.f5847a;
            if (!(bVar3 == bVar || bVar3 == bVar2)) {
                next.f5848b.setAlpha(0.2f);
            }
        }
        this.n = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.n.setDuration(250);
        this.n.setInterpolator(new DecelerateInterpolator(2.0f));
        this.n.addUpdateListener(new b(this, bVar, bVar2));
        this.n.start();
    }

    private void a(boolean z, b bVar) {
        ValueAnimator valueAnimator = this.m;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.n;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        this.m = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.m.setDuration(250);
        this.m.setInterpolator(new DecelerateInterpolator(2.0f));
        this.m.addUpdateListener(new c(this, z, bVar));
        this.m.start();
    }

    private void b() {
        int length = f5837a.length;
        Context context = getContext();
        LayoutInflater from = LayoutInflater.from(context);
        for (int i2 = length - 1; i2 >= 0; i2--) {
            b bVar = f5837a[i2];
            View inflate = from.inflate(R.layout.storage_main_list_item_layout, this.i, false);
            d dVar = new d();
            dVar.f5848b = inflate;
            dVar.f5849c = inflate.findViewById(R.id.v_dot);
            dVar.f = (ImageView) inflate.findViewById(R.id.iv_arrow);
            dVar.e = (SizeTextView) inflate.findViewById(R.id.tv_size);
            dVar.f5850d = (TextView) inflate.findViewById(R.id.tv_title);
            dVar.f5847a = bVar;
            dVar.f5848b.setTag(dVar);
            dVar.f5848b.setOnClickListener(this);
            if (bVar == b.STORAGE_SYSTEM || bVar == b.STORAGE_OTHER) {
                dVar.f.setVisibility(8);
                dVar.f5848b.setBackground((Drawable) null);
                dVar.f5848b.setOnClickListener((View.OnClickListener) null);
            }
            int i3 = getResources().getBoolean(R.bool.dark_mode_val) ? -1 : RoundedDrawable.DEFAULT_BORDER_COLOR;
            Drawable drawable = getResources().getDrawable(R.drawable.storage_item_right_arrow);
            drawable.setAutoMirrored(true);
            drawable.setColorFilter(i3, PorterDuff.Mode.SRC_ATOP);
            dVar.f.setImageDrawable(drawable);
            if (b.b.i.b.c.a() >= 9) {
                dVar.e.setTypeface(Typeface.create("mipro-medium", 0));
            }
            dVar.f5850d.setText(bVar.a(context));
            dVar.e.setSize(0);
            this.i.addView(inflate);
            if (g.a()) {
                inflate.setBackgroundResource(R.drawable.shape_storage_list_item_pressed_bg_folme);
                b.b.i.b.a.a(inflate);
            } else {
                inflate.setBackgroundResource(R.drawable.selector_storage_item_bg);
            }
            ((GradientDrawable) dVar.f5849c.getBackground()).setColor(bVar.b(context));
            this.h.put(bVar, dVar);
            dVar.f5848b.setEnabled(false);
        }
        View inflate2 = from.inflate(R.layout.storage_main_item_cleaner, this.i, false);
        if (g.a()) {
            b.b.i.b.a.b(inflate2);
        }
        a aVar = new a();
        aVar.f5848b = inflate2;
        aVar.h = (TextView) inflate2.findViewById(R.id.title);
        this.i.addView(inflate2);
        inflate2.setTag(b.CLENER);
        inflate2.setOnClickListener(this);
        this.h.put(b.CLENER, aVar);
        if (!g.b()) {
            ((LinearLayout.LayoutParams) this.i.getLayoutParams()).topMargin = context.getResources().getDimensionPixelSize(R.dimen.storage_column_mt_small_title);
        }
        d();
    }

    private boolean c() {
        return getLayoutDirection() == 1;
    }

    private void d() {
        TextView textView;
        boolean z = this.f5838b == v.FILE_EXPLORE;
        for (b next : this.h.keySet()) {
            d dVar = this.h.get(next);
            if (dVar != null) {
                if (!a()) {
                    g.b(dVar.f, 8);
                    g.b(dVar.f5848b);
                } else {
                    if ((z || next == b.STORAGE_SYSTEM || next == b.STORAGE_OTHER || !next.c(getContext())) && next != b.CLENER) {
                        g.b(dVar.f, 8);
                        g.b(dVar.f5848b);
                        g.a(dVar.f5848b);
                    } else {
                        g.b(dVar.f, 0);
                        g.a(dVar.f5848b, (View.OnClickListener) this);
                    }
                    if (next == b.CLENER) {
                        next.a("miui.intent.action.GARBAGE_CLEANUP");
                        next.a((int) R.string.storage_item_clean_name);
                        if ((dVar instanceof a) && (textView = ((a) dVar).h) != null) {
                            textView.setText(next.a(getContext()));
                        }
                    }
                }
            }
        }
    }

    public void a(float f2, b bVar, b bVar2) {
        float f3 = 0.8f * f2;
        float f4 = 0.2f + f3;
        float f5 = 1.0f - f3;
        d dVar = this.h.get(bVar);
        d dVar2 = this.h.get(bVar2);
        if (dVar != null) {
            dVar.f5848b.setAlpha(f4);
        }
        if (dVar2 != null) {
            dVar2.f5848b.setAlpha(f5);
        }
        this.f.setAlpha((int) (f2 * 255.0f));
        this.g.setAlpha((int) ((1.0f - f2) * 255.0f));
        if (f2 == 1.0f) {
            this.e.reset();
        }
    }

    public void a(MotionEvent motionEvent, b bVar, int i2, int i3) {
        if (bVar != b.CLENER) {
            int actionMasked = motionEvent.getActionMasked();
            int i4 = -5;
            if (actionMasked == 0 && bVar != null) {
                this.f.setColor(bVar.b(getContext()));
                Point a2 = a(this.h.get(bVar).f5849c, (View) this);
                this.f5840d.reset();
                Path path = this.f5840d;
                if (!c()) {
                    i4 = 5;
                }
                float f2 = (float) i3;
                path.moveTo((float) (i2 - i4), f2);
                this.f5840d.lineTo((float) i2, f2);
                this.f5840d.lineTo((float) (a2.x + (c() ? this.l : -this.l)), (float) a2.y);
                this.f5840d.lineTo((float) a2.x, (float) a2.y);
                this.o = bVar;
                a(true, this.o);
            } else if (actionMasked != 2 || bVar == null) {
                a(false, this.o);
                this.o = null;
            } else {
                b bVar2 = this.o;
                if (bVar2 != bVar && bVar2 != null) {
                    this.g.setColor(bVar2.b(getContext()));
                    this.f.setColor(bVar.b(getContext()));
                    Point a3 = a(this.h.get(bVar).f5849c, (View) this);
                    this.e.reset();
                    this.e.addPath(this.f5840d);
                    this.f5840d.reset();
                    Path path2 = this.f5840d;
                    if (!c()) {
                        i4 = 5;
                    }
                    float f3 = (float) i3;
                    path2.moveTo((float) (i2 - i4), f3);
                    this.f5840d.lineTo((float) i2, f3);
                    this.f5840d.lineTo((float) (a3.x + (c() ? this.l : -this.l)), (float) a3.y);
                    this.f5840d.lineTo((float) a3.x, (float) a3.y);
                    a(bVar, this.o);
                    this.o = bVar;
                    postInvalidate();
                }
            }
        }
    }

    public void a(b bVar, int i2, int i3) {
        if (bVar == null) {
            a(false, this.o);
            this.o = null;
        } else if (bVar != b.CLENER) {
            this.f.setColor(bVar.b(getContext()));
            d dVar = this.h.get(bVar);
            Point a2 = a(dVar.f5849c, (View) this);
            this.e.reset();
            this.e.addPath(this.f5840d);
            this.f5840d.reset();
            float f2 = (float) i3;
            this.f5840d.moveTo((float) (i2 - (c() ? -5 : 5)), f2);
            this.f5840d.lineTo((float) i2, f2);
            this.f5840d.lineTo((float) (a2.x + (c() ? this.l : -this.l)), (float) a2.y);
            this.f5840d.lineTo((float) a2.x, (float) a2.y);
            b bVar2 = this.o;
            if (bVar2 == bVar) {
                this.e.reset();
                d dVar2 = this.h.get(this.o);
                if (dVar2 != null) {
                    dVar2.f5848b.setAlpha(0.2f);
                }
                dVar.f5848b.setAlpha(1.0f);
            } else if (bVar2 != null) {
                this.g.setColor(bVar2.b(getContext()));
                a(bVar, this.o);
            }
            this.o = bVar;
            postInvalidate();
        }
    }

    public void a(b bVar, boolean z, float f2) {
        float f3;
        float f4 = 0.2f;
        if (!z) {
            f3 = 1.0f;
        } else {
            f3 = 0.2f;
            f4 = 1.0f;
        }
        float f5 = f4 + ((f3 - f4) * f2);
        for (d next : this.h.values()) {
            next.f5848b.setAlpha(next.f5847a == bVar ? 1.0f : f5);
        }
        this.f.setAlpha((int) (z ? 255.0f * f2 : 255.0f * (1.0f - f2)));
        if (!z && f2 == 1.0f) {
            this.f.setAlpha(255);
            this.f5840d.reset();
        }
        invalidate();
    }

    public void a(d dVar) {
        if (dVar != null) {
            this.f5839c.a(dVar);
            for (b next : this.h.keySet()) {
                if (next != b.CLENER) {
                    d dVar2 = this.h.get(next);
                    dVar2.e.setSize(this.j.a(dVar2.f5847a));
                }
            }
            this.j.a(dVar);
        }
    }

    public boolean a() {
        return this.p;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawPath(this.f5840d, this.f);
    }

    public View getListContainer() {
        return this.i;
    }

    public void onClick(View view) {
        if (this.p) {
            b bVar = null;
            if (view.getTag() instanceof d) {
                bVar = ((d) view.getTag()).f5847a;
            } else if (view.getTag() instanceof b) {
                bVar = (b) view.getTag();
            }
            if (bVar != null) {
                bVar.d(view.getContext());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5839c = (StorageColumnView) findViewById(R.id.scv);
        this.f5839c.setOnItemEventListener(this);
        this.i = (LinearLayout) findViewById(R.id.ll_list);
        b();
    }

    public void setScanFinished(boolean z) {
        this.p = z;
        for (int length = f5837a.length - 1; length >= 0; length--) {
            this.h.get(f5837a[length]).f5848b.setEnabled(true);
        }
        this.f5839c.setScanFinished(z);
        d();
    }

    public void setStorageInfo(d dVar) {
        this.j = dVar;
    }

    public void setStorageStyle(v vVar) {
        if (vVar != null && vVar != this.f5838b) {
            this.f5838b = vVar;
            d();
        }
    }
}
