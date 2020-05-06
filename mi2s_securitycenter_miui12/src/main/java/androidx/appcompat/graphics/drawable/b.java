package androidx.appcompat.graphics.drawable;

import a.c.j;
import a.j.a.a.k;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.StateSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.graphics.drawable.d;
import androidx.appcompat.graphics.drawable.f;
import androidx.appcompat.widget.X;
import androidx.core.content.res.h;
import com.miui.permission.PermissionManager;
import miui.cloud.CloudPushConstants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@SuppressLint({"RestrictedAPI"})
public class b extends f implements androidx.core.graphics.drawable.b {
    private C0009b o;
    private f p;
    private int q;
    private int r;
    private boolean s;

    private static class a extends f {

        /* renamed from: a  reason: collision with root package name */
        private final Animatable f330a;

        a(Animatable animatable) {
            super();
            this.f330a = animatable;
        }

        public void c() {
            this.f330a.start();
        }

        public void d() {
            this.f330a.stop();
        }
    }

    /* renamed from: androidx.appcompat.graphics.drawable.b$b  reason: collision with other inner class name */
    static class C0009b extends f.a {
        a.c.f<Long> K;
        j<Integer> L;

        C0009b(@Nullable C0009b bVar, @NonNull b bVar2, @Nullable Resources resources) {
            super(bVar, bVar2, resources);
            j<Integer> jVar;
            if (bVar != null) {
                this.K = bVar.K;
                jVar = bVar.L;
            } else {
                this.K = new a.c.f<>();
                jVar = new j<>();
            }
            this.L = jVar;
        }

        private static long f(int i, int i2) {
            return ((long) i2) | (((long) i) << 32);
        }

        /* access modifiers changed from: package-private */
        public int a(int i, int i2, @NonNull Drawable drawable, boolean z) {
            int a2 = super.a(drawable);
            long f = f(i, i2);
            long j = z ? 8589934592L : 0;
            long j2 = (long) a2;
            this.K.a(f, Long.valueOf(j2 | j));
            if (z) {
                this.K.a(f(i2, i), Long.valueOf(PermissionManager.PERM_ID_ACCESS_XIAOMI_ACCOUNT | j2 | j));
            }
            return a2;
        }

        /* access modifiers changed from: package-private */
        public int a(@NonNull int[] iArr, @NonNull Drawable drawable, int i) {
            int a2 = super.a(iArr, drawable);
            this.L.c(a2, Integer.valueOf(i));
            return a2;
        }

        /* access modifiers changed from: package-private */
        public int b(@NonNull int[] iArr) {
            int a2 = super.a(iArr);
            return a2 >= 0 ? a2 : super.a(StateSet.WILD_CARD);
        }

        /* access modifiers changed from: package-private */
        public int c(int i, int i2) {
            return (int) this.K.b(f(i, i2), -1L).longValue();
        }

        /* access modifiers changed from: package-private */
        public int d(int i) {
            if (i < 0) {
                return 0;
            }
            return this.L.b(i, 0).intValue();
        }

        /* access modifiers changed from: package-private */
        public boolean d(int i, int i2) {
            return (this.K.b(f(i, i2), -1L).longValue() & PermissionManager.PERM_ID_ACCESS_XIAOMI_ACCOUNT) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean e(int i, int i2) {
            return (this.K.b(f(i, i2), -1L).longValue() & 8589934592L) != 0;
        }

        /* access modifiers changed from: package-private */
        public void m() {
            this.K = this.K.clone();
            this.L = this.L.clone();
        }

        @NonNull
        public Drawable newDrawable() {
            return new b(this, (Resources) null);
        }

        @NonNull
        public Drawable newDrawable(Resources resources) {
            return new b(this, resources);
        }
    }

    private static class c extends f {

        /* renamed from: a  reason: collision with root package name */
        private final a.j.a.a.d f331a;

        c(a.j.a.a.d dVar) {
            super();
            this.f331a = dVar;
        }

        public void c() {
            this.f331a.start();
        }

        public void d() {
            this.f331a.stop();
        }
    }

    private static class d extends f {

        /* renamed from: a  reason: collision with root package name */
        private final ObjectAnimator f332a;

        /* renamed from: b  reason: collision with root package name */
        private final boolean f333b;

        d(AnimationDrawable animationDrawable, boolean z, boolean z2) {
            super();
            int numberOfFrames = animationDrawable.getNumberOfFrames();
            int i = z ? numberOfFrames - 1 : 0;
            int i2 = z ? 0 : numberOfFrames - 1;
            e eVar = new e(animationDrawable, z);
            ObjectAnimator ofInt = ObjectAnimator.ofInt(animationDrawable, "currentIndex", new int[]{i, i2});
            if (Build.VERSION.SDK_INT >= 18) {
                ofInt.setAutoCancel(true);
            }
            ofInt.setDuration((long) eVar.a());
            ofInt.setInterpolator(eVar);
            this.f333b = z2;
            this.f332a = ofInt;
        }

        public boolean a() {
            return this.f333b;
        }

        public void b() {
            this.f332a.reverse();
        }

        public void c() {
            this.f332a.start();
        }

        public void d() {
            this.f332a.cancel();
        }
    }

    private static class e implements TimeInterpolator {

        /* renamed from: a  reason: collision with root package name */
        private int[] f334a;

        /* renamed from: b  reason: collision with root package name */
        private int f335b;

        /* renamed from: c  reason: collision with root package name */
        private int f336c;

        e(AnimationDrawable animationDrawable, boolean z) {
            a(animationDrawable, z);
        }

        /* access modifiers changed from: package-private */
        public int a() {
            return this.f336c;
        }

        /* access modifiers changed from: package-private */
        public int a(AnimationDrawable animationDrawable, boolean z) {
            int numberOfFrames = animationDrawable.getNumberOfFrames();
            this.f335b = numberOfFrames;
            int[] iArr = this.f334a;
            if (iArr == null || iArr.length < numberOfFrames) {
                this.f334a = new int[numberOfFrames];
            }
            int[] iArr2 = this.f334a;
            int i = 0;
            for (int i2 = 0; i2 < numberOfFrames; i2++) {
                int duration = animationDrawable.getDuration(z ? (numberOfFrames - i2) - 1 : i2);
                iArr2[i2] = duration;
                i += duration;
            }
            this.f336c = i;
            return i;
        }

        public float getInterpolation(float f) {
            int i = (int) ((f * ((float) this.f336c)) + 0.5f);
            int i2 = this.f335b;
            int[] iArr = this.f334a;
            int i3 = 0;
            while (i3 < i2 && i >= iArr[i3]) {
                i -= iArr[i3];
                i3++;
            }
            return (((float) i3) / ((float) i2)) + (i3 < i2 ? ((float) i) / ((float) this.f336c) : 0.0f);
        }
    }

    private static abstract class f {
        private f() {
        }

        public boolean a() {
            return false;
        }

        public void b() {
        }

        public abstract void c();

        public abstract void d();
    }

    public b() {
        this((C0009b) null, (Resources) null);
    }

    b(@Nullable C0009b bVar, @Nullable Resources resources) {
        super((f.a) null);
        this.q = -1;
        this.r = -1;
        a((d.b) new C0009b(bVar, this, resources));
        onStateChange(getState());
        jumpToCurrentState();
    }

    public static b a(@NonNull Context context, @NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) {
        String name = xmlPullParser.getName();
        if (name.equals("animated-selector")) {
            b bVar = new b();
            bVar.b(context, resources, xmlPullParser, attributeSet, theme);
            return bVar;
        }
        throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": invalid animated-selector tag " + name);
    }

    private void a(TypedArray typedArray) {
        C0009b bVar = this.o;
        if (Build.VERSION.SDK_INT >= 21) {
            bVar.f346d |= typedArray.getChangingConfigurations();
        }
        bVar.b(typedArray.getBoolean(a.a.b.b.AnimatedStateListDrawableCompat_android_variablePadding, bVar.i));
        bVar.a(typedArray.getBoolean(a.a.b.b.AnimatedStateListDrawableCompat_android_constantSize, bVar.l));
        bVar.b(typedArray.getInt(a.a.b.b.AnimatedStateListDrawableCompat_android_enterFadeDuration, bVar.A));
        bVar.c(typedArray.getInt(a.a.b.b.AnimatedStateListDrawableCompat_android_exitFadeDuration, bVar.B));
        setDither(typedArray.getBoolean(a.a.b.b.AnimatedStateListDrawableCompat_android_dither, bVar.x));
    }

    private boolean b(int i) {
        int i2;
        int c2;
        f fVar;
        f fVar2 = this.p;
        if (fVar2 == null) {
            i2 = b();
        } else if (i == this.q) {
            return true;
        } else {
            if (i != this.r || !fVar2.a()) {
                i2 = this.q;
                fVar2.d();
            } else {
                fVar2.b();
                this.q = this.r;
                this.r = i;
                return true;
            }
        }
        this.p = null;
        this.r = -1;
        this.q = -1;
        C0009b bVar = this.o;
        int d2 = bVar.d(i2);
        int d3 = bVar.d(i);
        if (d3 == 0 || d2 == 0 || (c2 = bVar.c(d2, d3)) < 0) {
            return false;
        }
        boolean e2 = bVar.e(d2, d3);
        a(c2);
        Drawable current = getCurrent();
        if (current instanceof AnimationDrawable) {
            fVar = new d((AnimationDrawable) current, bVar.d(d2, d3), e2);
        } else if (current instanceof a.j.a.a.d) {
            fVar = new c((a.j.a.a.d) current);
        } else {
            if (current instanceof Animatable) {
                fVar = new a((Animatable) current);
            }
            return false;
        }
        fVar.c();
        this.p = fVar;
        this.r = i2;
        this.q = i;
        return true;
    }

    private void c() {
        onStateChange(getState());
    }

    private void c(@NonNull Context context, @NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) {
        int depth = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next != 1) {
                int depth2 = xmlPullParser.getDepth();
                if (depth2 < depth && next == 3) {
                    return;
                }
                if (next == 2 && depth2 <= depth) {
                    if (xmlPullParser.getName().equals(CloudPushConstants.XML_ITEM)) {
                        d(context, resources, xmlPullParser, attributeSet, theme);
                    } else if (xmlPullParser.getName().equals("transition")) {
                        e(context, resources, xmlPullParser, attributeSet, theme);
                    }
                }
            } else {
                return;
            }
        }
    }

    private int d(@NonNull Context context, @NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) {
        int next;
        TypedArray a2 = h.a(resources, theme, attributeSet, a.a.b.b.AnimatedStateListDrawableItem);
        int resourceId = a2.getResourceId(a.a.b.b.AnimatedStateListDrawableItem_android_id, 0);
        int resourceId2 = a2.getResourceId(a.a.b.b.AnimatedStateListDrawableItem_android_drawable, -1);
        Drawable a3 = resourceId2 > 0 ? X.a().a(context, resourceId2) : null;
        a2.recycle();
        int[] a4 = a(attributeSet);
        if (a3 == null) {
            do {
                next = xmlPullParser.next();
            } while (next == 4);
            if (next == 2) {
                a3 = xmlPullParser.getName().equals("vector") ? k.createFromXmlInner(resources, xmlPullParser, attributeSet, theme) : Build.VERSION.SDK_INT >= 21 ? Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet, theme) : Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet);
            } else {
                throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
        if (a3 != null) {
            return this.o.a(a4, a3, resourceId);
        }
        throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
    }

    private int e(@NonNull Context context, @NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) {
        int next;
        TypedArray a2 = h.a(resources, theme, attributeSet, a.a.b.b.AnimatedStateListDrawableTransition);
        int resourceId = a2.getResourceId(a.a.b.b.AnimatedStateListDrawableTransition_android_fromId, -1);
        int resourceId2 = a2.getResourceId(a.a.b.b.AnimatedStateListDrawableTransition_android_toId, -1);
        int resourceId3 = a2.getResourceId(a.a.b.b.AnimatedStateListDrawableTransition_android_drawable, -1);
        Drawable a3 = resourceId3 > 0 ? X.a().a(context, resourceId3) : null;
        boolean z = a2.getBoolean(a.a.b.b.AnimatedStateListDrawableTransition_android_reversible, false);
        a2.recycle();
        if (a3 == null) {
            do {
                next = xmlPullParser.next();
            } while (next == 4);
            if (next == 2) {
                a3 = xmlPullParser.getName().equals("animated-vector") ? a.j.a.a.d.a(context, resources, xmlPullParser, attributeSet, theme) : Build.VERSION.SDK_INT >= 21 ? Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet, theme) : Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet);
            } else {
                throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <transition> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
        if (a3 == null) {
            throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <transition> tag requires a 'drawable' attribute or child tag defining a drawable");
        } else if (resourceId != -1 && resourceId2 != -1) {
            return this.o.a(resourceId, resourceId2, a3, z);
        } else {
            throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <transition> tag requires 'fromId' & 'toId' attributes");
        }
    }

    /* access modifiers changed from: package-private */
    public C0009b a() {
        return new C0009b(this.o, this, (Resources) null);
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull d.b bVar) {
        super.a(bVar);
        if (bVar instanceof C0009b) {
            this.o = (C0009b) bVar;
        }
    }

    @RequiresApi(21)
    public /* bridge */ /* synthetic */ void applyTheme(@NonNull Resources.Theme theme) {
        super.applyTheme(theme);
    }

    public void b(@NonNull Context context, @NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) {
        TypedArray a2 = h.a(resources, theme, attributeSet, a.a.b.b.AnimatedStateListDrawableCompat);
        setVisible(a2.getBoolean(a.a.b.b.AnimatedStateListDrawableCompat_android_visible, true), true);
        a(a2);
        a(resources);
        a2.recycle();
        c(context, resources, xmlPullParser, attributeSet, theme);
        c();
    }

    @RequiresApi(21)
    public /* bridge */ /* synthetic */ boolean canApplyTheme() {
        return super.canApplyTheme();
    }

    public /* bridge */ /* synthetic */ void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
    }

    public /* bridge */ /* synthetic */ int getAlpha() {
        return super.getAlpha();
    }

    public /* bridge */ /* synthetic */ int getChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    @NonNull
    public /* bridge */ /* synthetic */ Drawable getCurrent() {
        return super.getCurrent();
    }

    public /* bridge */ /* synthetic */ void getHotspotBounds(@NonNull Rect rect) {
        super.getHotspotBounds(rect);
    }

    public /* bridge */ /* synthetic */ int getIntrinsicHeight() {
        return super.getIntrinsicHeight();
    }

    public /* bridge */ /* synthetic */ int getIntrinsicWidth() {
        return super.getIntrinsicWidth();
    }

    public /* bridge */ /* synthetic */ int getMinimumHeight() {
        return super.getMinimumHeight();
    }

    public /* bridge */ /* synthetic */ int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    public /* bridge */ /* synthetic */ int getOpacity() {
        return super.getOpacity();
    }

    @RequiresApi(21)
    public /* bridge */ /* synthetic */ void getOutline(@NonNull Outline outline) {
        super.getOutline(outline);
    }

    public /* bridge */ /* synthetic */ boolean getPadding(@NonNull Rect rect) {
        return super.getPadding(rect);
    }

    public /* bridge */ /* synthetic */ void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
    }

    public /* bridge */ /* synthetic */ boolean isAutoMirrored() {
        return super.isAutoMirrored();
    }

    public boolean isStateful() {
        return true;
    }

    public void jumpToCurrentState() {
        super.jumpToCurrentState();
        f fVar = this.p;
        if (fVar != null) {
            fVar.d();
            this.p = null;
            a(this.q);
            this.q = -1;
            this.r = -1;
        }
    }

    public Drawable mutate() {
        if (!this.s) {
            super.mutate();
            if (this == this) {
                this.o.m();
                this.s = true;
            }
        }
        return this;
    }

    public /* bridge */ /* synthetic */ boolean onLayoutDirectionChanged(int i) {
        return super.onLayoutDirectionChanged(i);
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        int b2 = this.o.b(iArr);
        boolean z = b2 != b() && (b(b2) || a(b2));
        Drawable current = getCurrent();
        return current != null ? z | current.setState(iArr) : z;
    }

    public /* bridge */ /* synthetic */ void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long j) {
        super.scheduleDrawable(drawable, runnable, j);
    }

    public /* bridge */ /* synthetic */ void setAlpha(int i) {
        super.setAlpha(i);
    }

    public /* bridge */ /* synthetic */ void setAutoMirrored(boolean z) {
        super.setAutoMirrored(z);
    }

    public /* bridge */ /* synthetic */ void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
    }

    public /* bridge */ /* synthetic */ void setDither(boolean z) {
        super.setDither(z);
    }

    public /* bridge */ /* synthetic */ void setHotspot(float f2, float f3) {
        super.setHotspot(f2, f3);
    }

    public /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
        super.setHotspotBounds(i, i2, i3, i4);
    }

    public /* bridge */ /* synthetic */ void setTintList(ColorStateList colorStateList) {
        super.setTintList(colorStateList);
    }

    public /* bridge */ /* synthetic */ void setTintMode(@NonNull PorterDuff.Mode mode) {
        super.setTintMode(mode);
    }

    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        if (this.p != null && (visible || z2)) {
            if (z) {
                this.p.c();
            } else {
                jumpToCurrentState();
            }
        }
        return visible;
    }

    public /* bridge */ /* synthetic */ void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {
        super.unscheduleDrawable(drawable, runnable);
    }
}
