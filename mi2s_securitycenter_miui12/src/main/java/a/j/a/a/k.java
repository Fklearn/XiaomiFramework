package a.j.a.a;

import a.d.a.b;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.miui.maml.folme.AnimatedProperty;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class k extends i {

    /* renamed from: b  reason: collision with root package name */
    static final PorterDuff.Mode f177b = PorterDuff.Mode.SRC_IN;

    /* renamed from: c  reason: collision with root package name */
    private g f178c;

    /* renamed from: d  reason: collision with root package name */
    private PorterDuffColorFilter f179d;
    private ColorFilter e;
    private boolean f;
    private boolean g;
    private Drawable.ConstantState h;
    private final float[] i;
    private final Matrix j;
    private final Rect k;

    private static class a extends e {
        a() {
        }

        a(a aVar) {
            super(aVar);
        }

        private void a(TypedArray typedArray, XmlPullParser xmlPullParser) {
            String string = typedArray.getString(0);
            if (string != null) {
                this.f185b = string;
            }
            String string2 = typedArray.getString(1);
            if (string2 != null) {
                this.f184a = a.d.a.b.a(string2);
            }
            this.f186c = androidx.core.content.res.h.b(typedArray, xmlPullParser, "fillType", 2, 0);
        }

        public void a(Resources resources, AttributeSet attributeSet, Resources.Theme theme, XmlPullParser xmlPullParser) {
            if (androidx.core.content.res.h.a(xmlPullParser, "pathData")) {
                TypedArray a2 = androidx.core.content.res.h.a(resources, theme, attributeSet, a.f162d);
                a(a2, xmlPullParser);
                a2.recycle();
            }
        }

        public boolean b() {
            return true;
        }
    }

    private static class b extends e {
        private int[] e;
        androidx.core.content.res.b f;
        float g = 0.0f;
        androidx.core.content.res.b h;
        float i = 1.0f;
        float j = 1.0f;
        float k = 0.0f;
        float l = 1.0f;
        float m = 0.0f;
        Paint.Cap n = Paint.Cap.BUTT;
        Paint.Join o = Paint.Join.MITER;
        float p = 4.0f;

        b() {
        }

        b(b bVar) {
            super(bVar);
            this.e = bVar.e;
            this.f = bVar.f;
            this.g = bVar.g;
            this.i = bVar.i;
            this.h = bVar.h;
            this.f186c = bVar.f186c;
            this.j = bVar.j;
            this.k = bVar.k;
            this.l = bVar.l;
            this.m = bVar.m;
            this.n = bVar.n;
            this.o = bVar.o;
            this.p = bVar.p;
        }

        private Paint.Cap a(int i2, Paint.Cap cap) {
            return i2 != 0 ? i2 != 1 ? i2 != 2 ? cap : Paint.Cap.SQUARE : Paint.Cap.ROUND : Paint.Cap.BUTT;
        }

        private Paint.Join a(int i2, Paint.Join join) {
            return i2 != 0 ? i2 != 1 ? i2 != 2 ? join : Paint.Join.BEVEL : Paint.Join.ROUND : Paint.Join.MITER;
        }

        private void a(TypedArray typedArray, XmlPullParser xmlPullParser, Resources.Theme theme) {
            this.e = null;
            if (androidx.core.content.res.h.a(xmlPullParser, "pathData")) {
                String string = typedArray.getString(0);
                if (string != null) {
                    this.f185b = string;
                }
                String string2 = typedArray.getString(2);
                if (string2 != null) {
                    this.f184a = a.d.a.b.a(string2);
                }
                Resources.Theme theme2 = theme;
                this.h = androidx.core.content.res.h.a(typedArray, xmlPullParser, theme2, "fillColor", 1, 0);
                this.j = androidx.core.content.res.h.a(typedArray, xmlPullParser, "fillAlpha", 12, this.j);
                this.n = a(androidx.core.content.res.h.b(typedArray, xmlPullParser, "strokeLineCap", 8, -1), this.n);
                this.o = a(androidx.core.content.res.h.b(typedArray, xmlPullParser, "strokeLineJoin", 9, -1), this.o);
                this.p = androidx.core.content.res.h.a(typedArray, xmlPullParser, "strokeMiterLimit", 10, this.p);
                this.f = androidx.core.content.res.h.a(typedArray, xmlPullParser, theme2, "strokeColor", 3, 0);
                this.i = androidx.core.content.res.h.a(typedArray, xmlPullParser, "strokeAlpha", 11, this.i);
                this.g = androidx.core.content.res.h.a(typedArray, xmlPullParser, "strokeWidth", 4, this.g);
                this.l = androidx.core.content.res.h.a(typedArray, xmlPullParser, "trimPathEnd", 6, this.l);
                this.m = androidx.core.content.res.h.a(typedArray, xmlPullParser, "trimPathOffset", 7, this.m);
                this.k = androidx.core.content.res.h.a(typedArray, xmlPullParser, "trimPathStart", 5, this.k);
                this.f186c = androidx.core.content.res.h.b(typedArray, xmlPullParser, "fillType", 13, this.f186c);
            }
        }

        public void a(Resources resources, AttributeSet attributeSet, Resources.Theme theme, XmlPullParser xmlPullParser) {
            TypedArray a2 = androidx.core.content.res.h.a(resources, theme, attributeSet, a.f161c);
            a(a2, xmlPullParser, theme);
            a2.recycle();
        }

        public boolean a() {
            return this.h.d() || this.f.d();
        }

        public boolean a(int[] iArr) {
            return this.f.a(iArr) | this.h.a(iArr);
        }

        /* access modifiers changed from: package-private */
        public float getFillAlpha() {
            return this.j;
        }

        /* access modifiers changed from: package-private */
        @ColorInt
        public int getFillColor() {
            return this.h.a();
        }

        /* access modifiers changed from: package-private */
        public float getStrokeAlpha() {
            return this.i;
        }

        /* access modifiers changed from: package-private */
        @ColorInt
        public int getStrokeColor() {
            return this.f.a();
        }

        /* access modifiers changed from: package-private */
        public float getStrokeWidth() {
            return this.g;
        }

        /* access modifiers changed from: package-private */
        public float getTrimPathEnd() {
            return this.l;
        }

        /* access modifiers changed from: package-private */
        public float getTrimPathOffset() {
            return this.m;
        }

        /* access modifiers changed from: package-private */
        public float getTrimPathStart() {
            return this.k;
        }

        /* access modifiers changed from: package-private */
        public void setFillAlpha(float f2) {
            this.j = f2;
        }

        /* access modifiers changed from: package-private */
        public void setFillColor(int i2) {
            this.h.b(i2);
        }

        /* access modifiers changed from: package-private */
        public void setStrokeAlpha(float f2) {
            this.i = f2;
        }

        /* access modifiers changed from: package-private */
        public void setStrokeColor(int i2) {
            this.f.b(i2);
        }

        /* access modifiers changed from: package-private */
        public void setStrokeWidth(float f2) {
            this.g = f2;
        }

        /* access modifiers changed from: package-private */
        public void setTrimPathEnd(float f2) {
            this.l = f2;
        }

        /* access modifiers changed from: package-private */
        public void setTrimPathOffset(float f2) {
            this.m = f2;
        }

        /* access modifiers changed from: package-private */
        public void setTrimPathStart(float f2) {
            this.k = f2;
        }
    }

    private static class c extends d {

        /* renamed from: a  reason: collision with root package name */
        final Matrix f180a = new Matrix();

        /* renamed from: b  reason: collision with root package name */
        final ArrayList<d> f181b = new ArrayList<>();

        /* renamed from: c  reason: collision with root package name */
        float f182c = 0.0f;

        /* renamed from: d  reason: collision with root package name */
        private float f183d = 0.0f;
        private float e = 0.0f;
        private float f = 1.0f;
        private float g = 1.0f;
        private float h = 0.0f;
        private float i = 0.0f;
        final Matrix j = new Matrix();
        int k;
        private int[] l;
        private String m = null;

        public c() {
            super();
        }

        public c(c cVar, a.c.b<String, Object> bVar) {
            super();
            e eVar;
            this.f182c = cVar.f182c;
            this.f183d = cVar.f183d;
            this.e = cVar.e;
            this.f = cVar.f;
            this.g = cVar.g;
            this.h = cVar.h;
            this.i = cVar.i;
            this.l = cVar.l;
            this.m = cVar.m;
            this.k = cVar.k;
            String str = this.m;
            if (str != null) {
                bVar.put(str, this);
            }
            this.j.set(cVar.j);
            ArrayList<d> arrayList = cVar.f181b;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                d dVar = arrayList.get(i2);
                if (dVar instanceof c) {
                    this.f181b.add(new c((c) dVar, bVar));
                } else {
                    if (dVar instanceof b) {
                        eVar = new b((b) dVar);
                    } else if (dVar instanceof a) {
                        eVar = new a((a) dVar);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    this.f181b.add(eVar);
                    String str2 = eVar.f185b;
                    if (str2 != null) {
                        bVar.put(str2, eVar);
                    }
                }
            }
        }

        private void a(TypedArray typedArray, XmlPullParser xmlPullParser) {
            this.l = null;
            this.f182c = androidx.core.content.res.h.a(typedArray, xmlPullParser, AnimatedProperty.PROPERTY_NAME_ROTATION, 5, this.f182c);
            this.f183d = typedArray.getFloat(1, this.f183d);
            this.e = typedArray.getFloat(2, this.e);
            this.f = androidx.core.content.res.h.a(typedArray, xmlPullParser, AnimatedProperty.PROPERTY_NAME_SCALE_X, 3, this.f);
            this.g = androidx.core.content.res.h.a(typedArray, xmlPullParser, AnimatedProperty.PROPERTY_NAME_SCALE_Y, 4, this.g);
            this.h = androidx.core.content.res.h.a(typedArray, xmlPullParser, "translateX", 6, this.h);
            this.i = androidx.core.content.res.h.a(typedArray, xmlPullParser, "translateY", 7, this.i);
            String string = typedArray.getString(0);
            if (string != null) {
                this.m = string;
            }
            b();
        }

        private void b() {
            this.j.reset();
            this.j.postTranslate(-this.f183d, -this.e);
            this.j.postScale(this.f, this.g);
            this.j.postRotate(this.f182c, 0.0f, 0.0f);
            this.j.postTranslate(this.h + this.f183d, this.i + this.e);
        }

        public void a(Resources resources, AttributeSet attributeSet, Resources.Theme theme, XmlPullParser xmlPullParser) {
            TypedArray a2 = androidx.core.content.res.h.a(resources, theme, attributeSet, a.f160b);
            a(a2, xmlPullParser);
            a2.recycle();
        }

        public boolean a() {
            for (int i2 = 0; i2 < this.f181b.size(); i2++) {
                if (this.f181b.get(i2).a()) {
                    return true;
                }
            }
            return false;
        }

        public boolean a(int[] iArr) {
            boolean z = false;
            for (int i2 = 0; i2 < this.f181b.size(); i2++) {
                z |= this.f181b.get(i2).a(iArr);
            }
            return z;
        }

        public String getGroupName() {
            return this.m;
        }

        public Matrix getLocalMatrix() {
            return this.j;
        }

        public float getPivotX() {
            return this.f183d;
        }

        public float getPivotY() {
            return this.e;
        }

        public float getRotation() {
            return this.f182c;
        }

        public float getScaleX() {
            return this.f;
        }

        public float getScaleY() {
            return this.g;
        }

        public float getTranslateX() {
            return this.h;
        }

        public float getTranslateY() {
            return this.i;
        }

        public void setPivotX(float f2) {
            if (f2 != this.f183d) {
                this.f183d = f2;
                b();
            }
        }

        public void setPivotY(float f2) {
            if (f2 != this.e) {
                this.e = f2;
                b();
            }
        }

        public void setRotation(float f2) {
            if (f2 != this.f182c) {
                this.f182c = f2;
                b();
            }
        }

        public void setScaleX(float f2) {
            if (f2 != this.f) {
                this.f = f2;
                b();
            }
        }

        public void setScaleY(float f2) {
            if (f2 != this.g) {
                this.g = f2;
                b();
            }
        }

        public void setTranslateX(float f2) {
            if (f2 != this.h) {
                this.h = f2;
                b();
            }
        }

        public void setTranslateY(float f2) {
            if (f2 != this.i) {
                this.i = f2;
                b();
            }
        }
    }

    private static abstract class d {
        private d() {
        }

        public boolean a() {
            return false;
        }

        public boolean a(int[] iArr) {
            return false;
        }
    }

    private static abstract class e extends d {

        /* renamed from: a  reason: collision with root package name */
        protected b.C0002b[] f184a = null;

        /* renamed from: b  reason: collision with root package name */
        String f185b;

        /* renamed from: c  reason: collision with root package name */
        int f186c = 0;

        /* renamed from: d  reason: collision with root package name */
        int f187d;

        public e() {
            super();
        }

        public e(e eVar) {
            super();
            this.f185b = eVar.f185b;
            this.f187d = eVar.f187d;
            this.f184a = a.d.a.b.a(eVar.f184a);
        }

        public void a(Path path) {
            path.reset();
            b.C0002b[] bVarArr = this.f184a;
            if (bVarArr != null) {
                b.C0002b.a(bVarArr, path);
            }
        }

        public boolean b() {
            return false;
        }

        public b.C0002b[] getPathData() {
            return this.f184a;
        }

        public String getPathName() {
            return this.f185b;
        }

        public void setPathData(b.C0002b[] bVarArr) {
            if (!a.d.a.b.a(this.f184a, bVarArr)) {
                this.f184a = a.d.a.b.a(bVarArr);
            } else {
                a.d.a.b.b(this.f184a, bVarArr);
            }
        }
    }

    private static class f {

        /* renamed from: a  reason: collision with root package name */
        private static final Matrix f188a = new Matrix();

        /* renamed from: b  reason: collision with root package name */
        private final Path f189b;

        /* renamed from: c  reason: collision with root package name */
        private final Path f190c;

        /* renamed from: d  reason: collision with root package name */
        private final Matrix f191d;
        Paint e;
        Paint f;
        private PathMeasure g;
        private int h;
        final c i;
        float j;
        float k;
        float l;
        float m;
        int n;
        String o;
        Boolean p;
        final a.c.b<String, Object> q;

        public f() {
            this.f191d = new Matrix();
            this.j = 0.0f;
            this.k = 0.0f;
            this.l = 0.0f;
            this.m = 0.0f;
            this.n = 255;
            this.o = null;
            this.p = null;
            this.q = new a.c.b<>();
            this.i = new c();
            this.f189b = new Path();
            this.f190c = new Path();
        }

        public f(f fVar) {
            this.f191d = new Matrix();
            this.j = 0.0f;
            this.k = 0.0f;
            this.l = 0.0f;
            this.m = 0.0f;
            this.n = 255;
            this.o = null;
            this.p = null;
            this.q = new a.c.b<>();
            this.i = new c(fVar.i, this.q);
            this.f189b = new Path(fVar.f189b);
            this.f190c = new Path(fVar.f190c);
            this.j = fVar.j;
            this.k = fVar.k;
            this.l = fVar.l;
            this.m = fVar.m;
            this.h = fVar.h;
            this.n = fVar.n;
            this.o = fVar.o;
            String str = fVar.o;
            if (str != null) {
                this.q.put(str, this);
            }
            this.p = fVar.p;
        }

        private static float a(float f2, float f3, float f4, float f5) {
            return (f2 * f5) - (f3 * f4);
        }

        private float a(Matrix matrix) {
            float[] fArr = {0.0f, 1.0f, 1.0f, 0.0f};
            matrix.mapVectors(fArr);
            float a2 = a(fArr[0], fArr[1], fArr[2], fArr[3]);
            float max = Math.max((float) Math.hypot((double) fArr[0], (double) fArr[1]), (float) Math.hypot((double) fArr[2], (double) fArr[3]));
            if (max > 0.0f) {
                return Math.abs(a2) / max;
            }
            return 0.0f;
        }

        private void a(c cVar, e eVar, Canvas canvas, int i2, int i3, ColorFilter colorFilter) {
            float f2 = ((float) i2) / this.l;
            float f3 = ((float) i3) / this.m;
            float min = Math.min(f2, f3);
            Matrix matrix = cVar.f180a;
            this.f191d.set(matrix);
            this.f191d.postScale(f2, f3);
            float a2 = a(matrix);
            if (a2 != 0.0f) {
                eVar.a(this.f189b);
                Path path = this.f189b;
                this.f190c.reset();
                if (eVar.b()) {
                    this.f190c.setFillType(eVar.f186c == 0 ? Path.FillType.WINDING : Path.FillType.EVEN_ODD);
                    this.f190c.addPath(path, this.f191d);
                    canvas.clipPath(this.f190c);
                    return;
                }
                b bVar = (b) eVar;
                if (!(bVar.k == 0.0f && bVar.l == 1.0f)) {
                    float f4 = bVar.k;
                    float f5 = bVar.m;
                    float f6 = (f4 + f5) % 1.0f;
                    float f7 = (bVar.l + f5) % 1.0f;
                    if (this.g == null) {
                        this.g = new PathMeasure();
                    }
                    this.g.setPath(this.f189b, false);
                    float length = this.g.getLength();
                    float f8 = f6 * length;
                    float f9 = f7 * length;
                    path.reset();
                    if (f8 > f9) {
                        this.g.getSegment(f8, length, path, true);
                        this.g.getSegment(0.0f, f9, path, true);
                    } else {
                        this.g.getSegment(f8, f9, path, true);
                    }
                    path.rLineTo(0.0f, 0.0f);
                }
                this.f190c.addPath(path, this.f191d);
                if (bVar.h.e()) {
                    androidx.core.content.res.b bVar2 = bVar.h;
                    if (this.f == null) {
                        this.f = new Paint(1);
                        this.f.setStyle(Paint.Style.FILL);
                    }
                    Paint paint = this.f;
                    if (bVar2.c()) {
                        Shader b2 = bVar2.b();
                        b2.setLocalMatrix(this.f191d);
                        paint.setShader(b2);
                        paint.setAlpha(Math.round(bVar.j * 255.0f));
                    } else {
                        paint.setShader((Shader) null);
                        paint.setAlpha(255);
                        paint.setColor(k.a(bVar2.a(), bVar.j));
                    }
                    paint.setColorFilter(colorFilter);
                    this.f190c.setFillType(bVar.f186c == 0 ? Path.FillType.WINDING : Path.FillType.EVEN_ODD);
                    canvas.drawPath(this.f190c, paint);
                }
                if (bVar.f.e()) {
                    androidx.core.content.res.b bVar3 = bVar.f;
                    if (this.e == null) {
                        this.e = new Paint(1);
                        this.e.setStyle(Paint.Style.STROKE);
                    }
                    Paint paint2 = this.e;
                    Paint.Join join = bVar.o;
                    if (join != null) {
                        paint2.setStrokeJoin(join);
                    }
                    Paint.Cap cap = bVar.n;
                    if (cap != null) {
                        paint2.setStrokeCap(cap);
                    }
                    paint2.setStrokeMiter(bVar.p);
                    if (bVar3.c()) {
                        Shader b3 = bVar3.b();
                        b3.setLocalMatrix(this.f191d);
                        paint2.setShader(b3);
                        paint2.setAlpha(Math.round(bVar.i * 255.0f));
                    } else {
                        paint2.setShader((Shader) null);
                        paint2.setAlpha(255);
                        paint2.setColor(k.a(bVar3.a(), bVar.i));
                    }
                    paint2.setColorFilter(colorFilter);
                    paint2.setStrokeWidth(bVar.g * min * a2);
                    canvas.drawPath(this.f190c, paint2);
                }
            }
        }

        private void a(c cVar, Matrix matrix, Canvas canvas, int i2, int i3, ColorFilter colorFilter) {
            cVar.f180a.set(matrix);
            cVar.f180a.preConcat(cVar.j);
            canvas.save();
            for (int i4 = 0; i4 < cVar.f181b.size(); i4++) {
                d dVar = cVar.f181b.get(i4);
                if (dVar instanceof c) {
                    a((c) dVar, cVar.f180a, canvas, i2, i3, colorFilter);
                } else if (dVar instanceof e) {
                    a(cVar, (e) dVar, canvas, i2, i3, colorFilter);
                }
            }
            canvas.restore();
        }

        public void a(Canvas canvas, int i2, int i3, ColorFilter colorFilter) {
            a(this.i, f188a, canvas, i2, i3, colorFilter);
        }

        public boolean a() {
            if (this.p == null) {
                this.p = Boolean.valueOf(this.i.a());
            }
            return this.p.booleanValue();
        }

        public boolean a(int[] iArr) {
            return this.i.a(iArr);
        }

        public float getAlpha() {
            return ((float) getRootAlpha()) / 255.0f;
        }

        public int getRootAlpha() {
            return this.n;
        }

        public void setAlpha(float f2) {
            setRootAlpha((int) (f2 * 255.0f));
        }

        public void setRootAlpha(int i2) {
            this.n = i2;
        }
    }

    private static class g extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        int f192a;

        /* renamed from: b  reason: collision with root package name */
        f f193b;

        /* renamed from: c  reason: collision with root package name */
        ColorStateList f194c;

        /* renamed from: d  reason: collision with root package name */
        PorterDuff.Mode f195d;
        boolean e;
        Bitmap f;
        ColorStateList g;
        PorterDuff.Mode h;
        int i;
        boolean j;
        boolean k;
        Paint l;

        public g() {
            this.f194c = null;
            this.f195d = k.f177b;
            this.f193b = new f();
        }

        public g(g gVar) {
            this.f194c = null;
            this.f195d = k.f177b;
            if (gVar != null) {
                this.f192a = gVar.f192a;
                this.f193b = new f(gVar.f193b);
                Paint paint = gVar.f193b.f;
                if (paint != null) {
                    this.f193b.f = new Paint(paint);
                }
                Paint paint2 = gVar.f193b.e;
                if (paint2 != null) {
                    this.f193b.e = new Paint(paint2);
                }
                this.f194c = gVar.f194c;
                this.f195d = gVar.f195d;
                this.e = gVar.e;
            }
        }

        public Paint a(ColorFilter colorFilter) {
            if (!b() && colorFilter == null) {
                return null;
            }
            if (this.l == null) {
                this.l = new Paint();
                this.l.setFilterBitmap(true);
            }
            this.l.setAlpha(this.f193b.getRootAlpha());
            this.l.setColorFilter(colorFilter);
            return this.l;
        }

        public void a(Canvas canvas, ColorFilter colorFilter, Rect rect) {
            canvas.drawBitmap(this.f, (Rect) null, rect, a(colorFilter));
        }

        public boolean a() {
            return !this.k && this.g == this.f194c && this.h == this.f195d && this.j == this.e && this.i == this.f193b.getRootAlpha();
        }

        public boolean a(int i2, int i3) {
            return i2 == this.f.getWidth() && i3 == this.f.getHeight();
        }

        public boolean a(int[] iArr) {
            boolean a2 = this.f193b.a(iArr);
            this.k |= a2;
            return a2;
        }

        public void b(int i2, int i3) {
            if (this.f == null || !a(i2, i3)) {
                this.f = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
                this.k = true;
            }
        }

        public boolean b() {
            return this.f193b.getRootAlpha() < 255;
        }

        public void c(int i2, int i3) {
            this.f.eraseColor(0);
            this.f193b.a(new Canvas(this.f), i2, i3, (ColorFilter) null);
        }

        public boolean c() {
            return this.f193b.a();
        }

        public void d() {
            this.g = this.f194c;
            this.h = this.f195d;
            this.i = this.f193b.getRootAlpha();
            this.j = this.e;
            this.k = false;
        }

        public int getChangingConfigurations() {
            return this.f192a;
        }

        @NonNull
        public Drawable newDrawable() {
            return new k(this);
        }

        @NonNull
        public Drawable newDrawable(Resources resources) {
            return new k(this);
        }
    }

    @RequiresApi(24)
    private static class h extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        private final Drawable.ConstantState f196a;

        public h(Drawable.ConstantState constantState) {
            this.f196a = constantState;
        }

        public boolean canApplyTheme() {
            return this.f196a.canApplyTheme();
        }

        public int getChangingConfigurations() {
            return this.f196a.getChangingConfigurations();
        }

        public Drawable newDrawable() {
            k kVar = new k();
            kVar.f176a = (VectorDrawable) this.f196a.newDrawable();
            return kVar;
        }

        public Drawable newDrawable(Resources resources) {
            k kVar = new k();
            kVar.f176a = (VectorDrawable) this.f196a.newDrawable(resources);
            return kVar;
        }

        public Drawable newDrawable(Resources resources, Resources.Theme theme) {
            k kVar = new k();
            kVar.f176a = (VectorDrawable) this.f196a.newDrawable(resources, theme);
            return kVar;
        }
    }

    k() {
        this.g = true;
        this.i = new float[9];
        this.j = new Matrix();
        this.k = new Rect();
        this.f178c = new g();
    }

    k(@NonNull g gVar) {
        this.g = true;
        this.i = new float[9];
        this.j = new Matrix();
        this.k = new Rect();
        this.f178c = gVar;
        this.f179d = a(this.f179d, gVar.f194c, gVar.f195d);
    }

    static int a(int i2, float f2) {
        return (i2 & 16777215) | (((int) (((float) Color.alpha(i2)) * f2)) << 24);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0038 A[Catch:{ IOException | XmlPullParserException -> 0x0045 }] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x003d A[Catch:{ IOException | XmlPullParserException -> 0x0045 }] */
    @androidx.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static a.j.a.a.k a(@androidx.annotation.NonNull android.content.res.Resources r6, @androidx.annotation.DrawableRes int r7, @androidx.annotation.Nullable android.content.res.Resources.Theme r8) {
        /*
            java.lang.String r0 = "parser error"
            java.lang.String r1 = "VectorDrawableCompat"
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 < r3) goto L_0x0023
            a.j.a.a.k r0 = new a.j.a.a.k
            r0.<init>()
            android.graphics.drawable.Drawable r6 = androidx.core.content.res.g.a(r6, r7, r8)
            r0.f176a = r6
            a.j.a.a.k$h r6 = new a.j.a.a.k$h
            android.graphics.drawable.Drawable r7 = r0.f176a
            android.graphics.drawable.Drawable$ConstantState r7 = r7.getConstantState()
            r6.<init>(r7)
            r0.h = r6
            return r0
        L_0x0023:
            android.content.res.XmlResourceParser r7 = r6.getXml(r7)     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
            android.util.AttributeSet r2 = android.util.Xml.asAttributeSet(r7)     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
        L_0x002b:
            int r3 = r7.next()     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
            r4 = 2
            if (r3 == r4) goto L_0x0036
            r5 = 1
            if (r3 == r5) goto L_0x0036
            goto L_0x002b
        L_0x0036:
            if (r3 != r4) goto L_0x003d
            a.j.a.a.k r6 = createFromXmlInner(r6, r7, r2, r8)     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
            return r6
        L_0x003d:
            org.xmlpull.v1.XmlPullParserException r6 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
            java.lang.String r7 = "No start tag found"
            r6.<init>(r7)     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
            throw r6     // Catch:{ XmlPullParserException -> 0x0047, IOException -> 0x0045 }
        L_0x0045:
            r6 = move-exception
            goto L_0x0048
        L_0x0047:
            r6 = move-exception
        L_0x0048:
            android.util.Log.e(r1, r0, r6)
            r6 = 0
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: a.j.a.a.k.a(android.content.res.Resources, int, android.content.res.Resources$Theme):a.j.a.a.k");
    }

    private static PorterDuff.Mode a(int i2, PorterDuff.Mode mode) {
        if (i2 == 3) {
            return PorterDuff.Mode.SRC_OVER;
        }
        if (i2 == 5) {
            return PorterDuff.Mode.SRC_IN;
        }
        if (i2 == 9) {
            return PorterDuff.Mode.SRC_ATOP;
        }
        switch (i2) {
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                return PorterDuff.Mode.ADD;
            default:
                return mode;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: a.j.a.a.k$a} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: a.j.a.a.k$b} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: a.j.a.a.k$a} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: a.j.a.a.k$a} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: a.j.a.a.k$a} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.res.Resources r11, org.xmlpull.v1.XmlPullParser r12, android.util.AttributeSet r13, android.content.res.Resources.Theme r14) {
        /*
            r10 = this;
            a.j.a.a.k$g r0 = r10.f178c
            a.j.a.a.k$f r1 = r0.f193b
            java.util.ArrayDeque r2 = new java.util.ArrayDeque
            r2.<init>()
            a.j.a.a.k$c r3 = r1.i
            r2.push(r3)
            int r3 = r12.getEventType()
            int r4 = r12.getDepth()
            r5 = 1
            int r4 = r4 + r5
            r6 = r5
        L_0x0019:
            if (r3 == r5) goto L_0x00c4
            int r7 = r12.getDepth()
            r8 = 3
            if (r7 >= r4) goto L_0x0024
            if (r3 == r8) goto L_0x00c4
        L_0x0024:
            r7 = 2
            java.lang.String r9 = "group"
            if (r3 != r7) goto L_0x00af
            java.lang.String r3 = r12.getName()
            java.lang.Object r7 = r2.peek()
            a.j.a.a.k$c r7 = (a.j.a.a.k.c) r7
            java.lang.String r8 = "path"
            boolean r8 = r8.equals(r3)
            if (r8 == 0) goto L_0x0059
            a.j.a.a.k$b r3 = new a.j.a.a.k$b
            r3.<init>()
            r3.a(r11, r13, r14, r12)
            java.util.ArrayList<a.j.a.a.k$d> r6 = r7.f181b
            r6.add(r3)
            java.lang.String r6 = r3.getPathName()
            if (r6 == 0) goto L_0x0057
            a.c.b<java.lang.String, java.lang.Object> r6 = r1.q
            java.lang.String r7 = r3.getPathName()
            r6.put(r7, r3)
        L_0x0057:
            r6 = 0
            goto L_0x007d
        L_0x0059:
            java.lang.String r8 = "clip-path"
            boolean r8 = r8.equals(r3)
            if (r8 == 0) goto L_0x0085
            a.j.a.a.k$a r3 = new a.j.a.a.k$a
            r3.<init>()
            r3.a(r11, r13, r14, r12)
            java.util.ArrayList<a.j.a.a.k$d> r7 = r7.f181b
            r7.add(r3)
            java.lang.String r7 = r3.getPathName()
            if (r7 == 0) goto L_0x007d
            a.c.b<java.lang.String, java.lang.Object> r7 = r1.q
            java.lang.String r8 = r3.getPathName()
            r7.put(r8, r3)
        L_0x007d:
            int r7 = r0.f192a
            int r3 = r3.f187d
        L_0x0081:
            r3 = r3 | r7
            r0.f192a = r3
            goto L_0x00be
        L_0x0085:
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L_0x00be
            a.j.a.a.k$c r3 = new a.j.a.a.k$c
            r3.<init>()
            r3.a(r11, r13, r14, r12)
            java.util.ArrayList<a.j.a.a.k$d> r7 = r7.f181b
            r7.add(r3)
            r2.push(r3)
            java.lang.String r7 = r3.getGroupName()
            if (r7 == 0) goto L_0x00aa
            a.c.b<java.lang.String, java.lang.Object> r7 = r1.q
            java.lang.String r8 = r3.getGroupName()
            r7.put(r8, r3)
        L_0x00aa:
            int r7 = r0.f192a
            int r3 = r3.k
            goto L_0x0081
        L_0x00af:
            if (r3 != r8) goto L_0x00be
            java.lang.String r3 = r12.getName()
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L_0x00be
            r2.pop()
        L_0x00be:
            int r3 = r12.next()
            goto L_0x0019
        L_0x00c4:
            if (r6 != 0) goto L_0x00c7
            return
        L_0x00c7:
            org.xmlpull.v1.XmlPullParserException r11 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "no path defined"
            r11.<init>(r12)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: a.j.a.a.k.a(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    private void a(TypedArray typedArray, XmlPullParser xmlPullParser, Resources.Theme theme) {
        g gVar = this.f178c;
        f fVar = gVar.f193b;
        gVar.f195d = a(androidx.core.content.res.h.b(typedArray, xmlPullParser, "tintMode", 6, -1), PorterDuff.Mode.SRC_IN);
        ColorStateList a2 = androidx.core.content.res.h.a(typedArray, xmlPullParser, theme, "tint", 1);
        if (a2 != null) {
            gVar.f194c = a2;
        }
        gVar.e = androidx.core.content.res.h.a(typedArray, xmlPullParser, "autoMirrored", 5, gVar.e);
        fVar.l = androidx.core.content.res.h.a(typedArray, xmlPullParser, "viewportWidth", 7, fVar.l);
        fVar.m = androidx.core.content.res.h.a(typedArray, xmlPullParser, "viewportHeight", 8, fVar.m);
        if (fVar.l <= 0.0f) {
            throw new XmlPullParserException(typedArray.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        } else if (fVar.m > 0.0f) {
            fVar.j = typedArray.getDimension(3, fVar.j);
            fVar.k = typedArray.getDimension(2, fVar.k);
            if (fVar.j <= 0.0f) {
                throw new XmlPullParserException(typedArray.getPositionDescription() + "<vector> tag requires width > 0");
            } else if (fVar.k > 0.0f) {
                fVar.setAlpha(androidx.core.content.res.h.a(typedArray, xmlPullParser, AnimatedProperty.PROPERTY_NAME_ALPHA, 4, fVar.getAlpha()));
                String string = typedArray.getString(0);
                if (string != null) {
                    fVar.o = string;
                    fVar.q.put(string, fVar);
                }
            } else {
                throw new XmlPullParserException(typedArray.getPositionDescription() + "<vector> tag requires height > 0");
            }
        } else {
            throw new XmlPullParserException(typedArray.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        }
    }

    private boolean a() {
        return Build.VERSION.SDK_INT >= 17 && isAutoMirrored() && androidx.core.graphics.drawable.a.d(this) == 1;
    }

    public static k createFromXmlInner(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        k kVar = new k();
        kVar.inflate(resources, xmlPullParser, attributeSet, theme);
        return kVar;
    }

    /* access modifiers changed from: package-private */
    public PorterDuffColorFilter a(PorterDuffColorFilter porterDuffColorFilter, ColorStateList colorStateList, PorterDuff.Mode mode) {
        if (colorStateList == null || mode == null) {
            return null;
        }
        return new PorterDuffColorFilter(colorStateList.getColorForState(getState(), 0), mode);
    }

    /* access modifiers changed from: package-private */
    public Object a(String str) {
        return this.f178c.f193b.q.get(str);
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        this.g = z;
    }

    public /* bridge */ /* synthetic */ void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
    }

    public boolean canApplyTheme() {
        Drawable drawable = this.f176a;
        if (drawable == null) {
            return false;
        }
        androidx.core.graphics.drawable.a.a(drawable);
        return false;
    }

    public /* bridge */ /* synthetic */ void clearColorFilter() {
        super.clearColorFilter();
    }

    public void draw(Canvas canvas) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.draw(canvas);
            return;
        }
        copyBounds(this.k);
        if (this.k.width() > 0 && this.k.height() > 0) {
            ColorFilter colorFilter = this.e;
            if (colorFilter == null) {
                colorFilter = this.f179d;
            }
            canvas.getMatrix(this.j);
            this.j.getValues(this.i);
            float abs = Math.abs(this.i[0]);
            float abs2 = Math.abs(this.i[4]);
            float abs3 = Math.abs(this.i[1]);
            float abs4 = Math.abs(this.i[3]);
            if (!(abs3 == 0.0f && abs4 == 0.0f)) {
                abs = 1.0f;
                abs2 = 1.0f;
            }
            int min = Math.min(2048, (int) (((float) this.k.width()) * abs));
            int min2 = Math.min(2048, (int) (((float) this.k.height()) * abs2));
            if (min > 0 && min2 > 0) {
                int save = canvas.save();
                Rect rect = this.k;
                canvas.translate((float) rect.left, (float) rect.top);
                if (a()) {
                    canvas.translate((float) this.k.width(), 0.0f);
                    canvas.scale(-1.0f, 1.0f);
                }
                this.k.offsetTo(0, 0);
                this.f178c.b(min, min2);
                if (!this.g) {
                    this.f178c.c(min, min2);
                } else if (!this.f178c.a()) {
                    this.f178c.c(min, min2);
                    this.f178c.d();
                }
                this.f178c.a(canvas, colorFilter, this.k);
                canvas.restoreToCount(save);
            }
        }
    }

    public int getAlpha() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.b(drawable) : this.f178c.f193b.getRootAlpha();
    }

    public int getChangingConfigurations() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getChangingConfigurations() : super.getChangingConfigurations() | this.f178c.getChangingConfigurations();
    }

    public ColorFilter getColorFilter() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.c(drawable) : this.e;
    }

    public Drawable.ConstantState getConstantState() {
        Drawable drawable = this.f176a;
        if (drawable != null && Build.VERSION.SDK_INT >= 24) {
            return new h(drawable.getConstantState());
        }
        this.f178c.f192a = getChangingConfigurations();
        return this.f178c;
    }

    public /* bridge */ /* synthetic */ Drawable getCurrent() {
        return super.getCurrent();
    }

    public int getIntrinsicHeight() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getIntrinsicHeight() : (int) this.f178c.f193b.k;
    }

    public int getIntrinsicWidth() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getIntrinsicWidth() : (int) this.f178c.f193b.j;
    }

    public /* bridge */ /* synthetic */ int getMinimumHeight() {
        return super.getMinimumHeight();
    }

    public /* bridge */ /* synthetic */ int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    public int getOpacity() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            return drawable.getOpacity();
        }
        return -3;
    }

    public /* bridge */ /* synthetic */ boolean getPadding(Rect rect) {
        return super.getPadding(rect);
    }

    public /* bridge */ /* synthetic */ int[] getState() {
        return super.getState();
    }

    public /* bridge */ /* synthetic */ Region getTransparentRegion() {
        return super.getTransparentRegion();
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.inflate(resources, xmlPullParser, attributeSet);
        } else {
            inflate(resources, xmlPullParser, attributeSet, (Resources.Theme) null);
        }
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, resources, xmlPullParser, attributeSet, theme);
            return;
        }
        g gVar = this.f178c;
        gVar.f193b = new f();
        TypedArray a2 = androidx.core.content.res.h.a(resources, theme, attributeSet, a.f159a);
        a(a2, xmlPullParser, theme);
        a2.recycle();
        gVar.f192a = getChangingConfigurations();
        gVar.k = true;
        a(resources, xmlPullParser, attributeSet, theme);
        this.f179d = a(this.f179d, gVar.f194c, gVar.f195d);
    }

    public void invalidateSelf() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.invalidateSelf();
        } else {
            super.invalidateSelf();
        }
    }

    public boolean isAutoMirrored() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.e(drawable) : this.f178c.e;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0019, code lost:
        r0 = r1.f178c.f194c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000f, code lost:
        r0 = r1.f178c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isStateful() {
        /*
            r1 = this;
            android.graphics.drawable.Drawable r0 = r1.f176a
            if (r0 == 0) goto L_0x0009
            boolean r0 = r0.isStateful()
            return r0
        L_0x0009:
            boolean r0 = super.isStateful()
            if (r0 != 0) goto L_0x0028
            a.j.a.a.k$g r0 = r1.f178c
            if (r0 == 0) goto L_0x0026
            boolean r0 = r0.c()
            if (r0 != 0) goto L_0x0028
            a.j.a.a.k$g r0 = r1.f178c
            android.content.res.ColorStateList r0 = r0.f194c
            if (r0 == 0) goto L_0x0026
            boolean r0 = r0.isStateful()
            if (r0 == 0) goto L_0x0026
            goto L_0x0028
        L_0x0026:
            r0 = 0
            goto L_0x0029
        L_0x0028:
            r0 = 1
        L_0x0029:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: a.j.a.a.k.isStateful():boolean");
    }

    public /* bridge */ /* synthetic */ void jumpToCurrentState() {
        super.jumpToCurrentState();
    }

    public Drawable mutate() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.mutate();
            return this;
        }
        if (!this.f && super.mutate() == this) {
            this.f178c = new g(this.f178c);
            this.f = true;
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        PorterDuff.Mode mode;
        Drawable drawable = this.f176a;
        if (drawable != null) {
            return drawable.setState(iArr);
        }
        boolean z = false;
        g gVar = this.f178c;
        ColorStateList colorStateList = gVar.f194c;
        if (!(colorStateList == null || (mode = gVar.f195d) == null)) {
            this.f179d = a(this.f179d, colorStateList, mode);
            invalidateSelf();
            z = true;
        }
        if (!gVar.c() || !gVar.a(iArr)) {
            return z;
        }
        invalidateSelf();
        return true;
    }

    public void scheduleSelf(Runnable runnable, long j2) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.scheduleSelf(runnable, j2);
        } else {
            super.scheduleSelf(runnable, j2);
        }
    }

    public void setAlpha(int i2) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setAlpha(i2);
        } else if (this.f178c.f193b.getRootAlpha() != i2) {
            this.f178c.f193b.setRootAlpha(i2);
            invalidateSelf();
        }
    }

    public void setAutoMirrored(boolean z) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, z);
        } else {
            this.f178c.e = z;
        }
    }

    public /* bridge */ /* synthetic */ void setChangingConfigurations(int i2) {
        super.setChangingConfigurations(i2);
    }

    public /* bridge */ /* synthetic */ void setColorFilter(int i2, PorterDuff.Mode mode) {
        super.setColorFilter(i2, mode);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
            return;
        }
        this.e = colorFilter;
        invalidateSelf();
    }

    public /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
        super.setFilterBitmap(z);
    }

    public /* bridge */ /* synthetic */ void setHotspot(float f2, float f3) {
        super.setHotspot(f2, f3);
    }

    public /* bridge */ /* synthetic */ void setHotspotBounds(int i2, int i3, int i4, int i5) {
        super.setHotspotBounds(i2, i3, i4, i5);
    }

    public /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
        return super.setState(iArr);
    }

    public void setTint(int i2) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.b(drawable, i2);
        } else {
            setTintList(ColorStateList.valueOf(i2));
        }
    }

    public void setTintList(ColorStateList colorStateList) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, colorStateList);
            return;
        }
        g gVar = this.f178c;
        if (gVar.f194c != colorStateList) {
            gVar.f194c = colorStateList;
            this.f179d = a(this.f179d, colorStateList, gVar.f195d);
            invalidateSelf();
        }
    }

    public void setTintMode(PorterDuff.Mode mode) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, mode);
            return;
        }
        g gVar = this.f178c;
        if (gVar.f195d != mode) {
            gVar.f195d = mode;
            this.f179d = a(this.f179d, gVar.f194c, mode);
            invalidateSelf();
        }
    }

    public boolean setVisible(boolean z, boolean z2) {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.setVisible(z, z2) : super.setVisible(z, z2);
    }

    public void unscheduleSelf(Runnable runnable) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.unscheduleSelf(runnable);
        } else {
            super.unscheduleSelf(runnable);
        }
    }
}
