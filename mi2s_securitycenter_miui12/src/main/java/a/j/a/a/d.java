package a.j.a.a;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.h;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

public class d extends i implements b {

    /* renamed from: b  reason: collision with root package name */
    private a f164b;

    /* renamed from: c  reason: collision with root package name */
    private Context f165c;

    /* renamed from: d  reason: collision with root package name */
    private ArgbEvaluator f166d;
    private Animator.AnimatorListener e;
    ArrayList<Object> f;
    final Drawable.Callback g;

    private static class a extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        int f167a;

        /* renamed from: b  reason: collision with root package name */
        k f168b;

        /* renamed from: c  reason: collision with root package name */
        AnimatorSet f169c;

        /* renamed from: d  reason: collision with root package name */
        ArrayList<Animator> f170d;
        a.c.b<Animator, String> e;

        public a(Context context, a aVar, Drawable.Callback callback, Resources resources) {
            if (aVar != null) {
                this.f167a = aVar.f167a;
                k kVar = aVar.f168b;
                if (kVar != null) {
                    Drawable.ConstantState constantState = kVar.getConstantState();
                    this.f168b = (k) (resources != null ? constantState.newDrawable(resources) : constantState.newDrawable());
                    k kVar2 = this.f168b;
                    kVar2.mutate();
                    this.f168b = kVar2;
                    this.f168b.setCallback(callback);
                    this.f168b.setBounds(aVar.f168b.getBounds());
                    this.f168b.a(false);
                }
                ArrayList<Animator> arrayList = aVar.f170d;
                if (arrayList != null) {
                    int size = arrayList.size();
                    this.f170d = new ArrayList<>(size);
                    this.e = new a.c.b<>(size);
                    for (int i = 0; i < size; i++) {
                        Animator animator = aVar.f170d.get(i);
                        Animator clone = animator.clone();
                        String str = aVar.e.get(animator);
                        clone.setTarget(this.f168b.a(str));
                        this.f170d.add(clone);
                        this.e.put(clone, str);
                    }
                    a();
                }
            }
        }

        public void a() {
            if (this.f169c == null) {
                this.f169c = new AnimatorSet();
            }
            this.f169c.playTogether(this.f170d);
        }

        public int getChangingConfigurations() {
            return this.f167a;
        }

        public Drawable newDrawable() {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }

        public Drawable newDrawable(Resources resources) {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }
    }

    @RequiresApi(24)
    private static class b extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        private final Drawable.ConstantState f171a;

        public b(Drawable.ConstantState constantState) {
            this.f171a = constantState;
        }

        public boolean canApplyTheme() {
            return this.f171a.canApplyTheme();
        }

        public int getChangingConfigurations() {
            return this.f171a.getChangingConfigurations();
        }

        public Drawable newDrawable() {
            d dVar = new d();
            dVar.f176a = this.f171a.newDrawable();
            dVar.f176a.setCallback(dVar.g);
            return dVar;
        }

        public Drawable newDrawable(Resources resources) {
            d dVar = new d();
            dVar.f176a = this.f171a.newDrawable(resources);
            dVar.f176a.setCallback(dVar.g);
            return dVar;
        }

        public Drawable newDrawable(Resources resources, Resources.Theme theme) {
            d dVar = new d();
            dVar.f176a = this.f171a.newDrawable(resources, theme);
            dVar.f176a.setCallback(dVar.g);
            return dVar;
        }
    }

    d() {
        this((Context) null, (a) null, (Resources) null);
    }

    private d(@Nullable Context context) {
        this(context, (a) null, (Resources) null);
    }

    private d(@Nullable Context context, @Nullable a aVar, @Nullable Resources resources) {
        this.f166d = null;
        this.e = null;
        this.f = null;
        this.g = new c(this);
        this.f165c = context;
        if (aVar != null) {
            this.f164b = aVar;
        } else {
            this.f164b = new a(context, aVar, this.g, resources);
        }
    }

    public static d a(Context context, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        d dVar = new d(context);
        dVar.inflate(resources, xmlPullParser, attributeSet, theme);
        return dVar;
    }

    private void a(Animator animator) {
        ArrayList<Animator> childAnimations;
        if ((animator instanceof AnimatorSet) && (childAnimations = ((AnimatorSet) animator).getChildAnimations()) != null) {
            for (int i = 0; i < childAnimations.size(); i++) {
                a(childAnimations.get(i));
            }
        }
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator objectAnimator = (ObjectAnimator) animator;
            String propertyName = objectAnimator.getPropertyName();
            if ("fillColor".equals(propertyName) || "strokeColor".equals(propertyName)) {
                if (this.f166d == null) {
                    this.f166d = new ArgbEvaluator();
                }
                objectAnimator.setEvaluator(this.f166d);
            }
        }
    }

    private void a(String str, Animator animator) {
        animator.setTarget(this.f164b.f168b.a(str));
        if (Build.VERSION.SDK_INT < 21) {
            a(animator);
        }
        a aVar = this.f164b;
        if (aVar.f170d == null) {
            aVar.f170d = new ArrayList<>();
            this.f164b.e = new a.c.b<>();
        }
        this.f164b.f170d.add(animator);
        this.f164b.e.put(animator, str);
    }

    public void applyTheme(Resources.Theme theme) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, theme);
        }
    }

    public boolean canApplyTheme() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            return androidx.core.graphics.drawable.a.a(drawable);
        }
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
        this.f164b.f168b.draw(canvas);
        if (this.f164b.f169c.isStarted()) {
            invalidateSelf();
        }
    }

    public int getAlpha() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.b(drawable) : this.f164b.f168b.getAlpha();
    }

    public int getChangingConfigurations() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getChangingConfigurations() : super.getChangingConfigurations() | this.f164b.f167a;
    }

    public ColorFilter getColorFilter() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.c(drawable) : this.f164b.f168b.getColorFilter();
    }

    public Drawable.ConstantState getConstantState() {
        Drawable drawable = this.f176a;
        if (drawable == null || Build.VERSION.SDK_INT < 24) {
            return null;
        }
        return new b(drawable.getConstantState());
    }

    public /* bridge */ /* synthetic */ Drawable getCurrent() {
        return super.getCurrent();
    }

    public int getIntrinsicHeight() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getIntrinsicHeight() : this.f164b.f168b.getIntrinsicHeight();
    }

    public int getIntrinsicWidth() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getIntrinsicWidth() : this.f164b.f168b.getIntrinsicWidth();
    }

    public /* bridge */ /* synthetic */ int getMinimumHeight() {
        return super.getMinimumHeight();
    }

    public /* bridge */ /* synthetic */ int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    public int getOpacity() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.getOpacity() : this.f164b.f168b.getOpacity();
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
        inflate(resources, xmlPullParser, attributeSet, (Resources.Theme) null);
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray obtainAttributes;
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, resources, xmlPullParser, attributeSet, theme);
            return;
        }
        int eventType = xmlPullParser.getEventType();
        int depth = xmlPullParser.getDepth() + 1;
        while (eventType != 1 && (xmlPullParser.getDepth() >= depth || eventType != 3)) {
            if (eventType == 2) {
                String name = xmlPullParser.getName();
                if ("animated-vector".equals(name)) {
                    obtainAttributes = h.a(resources, theme, attributeSet, a.e);
                    int resourceId = obtainAttributes.getResourceId(0, 0);
                    if (resourceId != 0) {
                        k a2 = k.a(resources, resourceId, theme);
                        a2.a(false);
                        a2.setCallback(this.g);
                        k kVar = this.f164b.f168b;
                        if (kVar != null) {
                            kVar.setCallback((Drawable.Callback) null);
                        }
                        this.f164b.f168b = a2;
                    }
                } else if ("target".equals(name)) {
                    obtainAttributes = resources.obtainAttributes(attributeSet, a.f);
                    String string = obtainAttributes.getString(0);
                    int resourceId2 = obtainAttributes.getResourceId(1, 0);
                    if (resourceId2 != 0) {
                        Context context = this.f165c;
                        if (context != null) {
                            a(string, f.a(context, resourceId2));
                        } else {
                            obtainAttributes.recycle();
                            throw new IllegalStateException("Context can't be null when inflating animators");
                        }
                    }
                } else {
                    continue;
                }
                obtainAttributes.recycle();
            }
            eventType = xmlPullParser.next();
        }
        this.f164b.a();
    }

    public boolean isAutoMirrored() {
        Drawable drawable = this.f176a;
        return drawable != null ? androidx.core.graphics.drawable.a.e(drawable) : this.f164b.f168b.isAutoMirrored();
    }

    public boolean isRunning() {
        Drawable drawable = this.f176a;
        return drawable != null ? ((AnimatedVectorDrawable) drawable).isRunning() : this.f164b.f169c.isRunning();
    }

    public boolean isStateful() {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.isStateful() : this.f164b.f168b.isStateful();
    }

    public /* bridge */ /* synthetic */ void jumpToCurrentState() {
        super.jumpToCurrentState();
    }

    public Drawable mutate() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.mutate();
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setBounds(rect);
        } else {
            this.f164b.f168b.setBounds(rect);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int i) {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.setLevel(i) : this.f164b.f168b.setLevel(i);
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        Drawable drawable = this.f176a;
        return drawable != null ? drawable.setState(iArr) : this.f164b.f168b.setState(iArr);
    }

    public void setAlpha(int i) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setAlpha(i);
        } else {
            this.f164b.f168b.setAlpha(i);
        }
    }

    public void setAutoMirrored(boolean z) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, z);
        } else {
            this.f164b.f168b.setAutoMirrored(z);
        }
    }

    public /* bridge */ /* synthetic */ void setChangingConfigurations(int i) {
        super.setChangingConfigurations(i);
    }

    public /* bridge */ /* synthetic */ void setColorFilter(int i, PorterDuff.Mode mode) {
        super.setColorFilter(i, mode);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        } else {
            this.f164b.f168b.setColorFilter(colorFilter);
        }
    }

    public /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
        super.setFilterBitmap(z);
    }

    public /* bridge */ /* synthetic */ void setHotspot(float f2, float f3) {
        super.setHotspot(f2, f3);
    }

    public /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
        super.setHotspotBounds(i, i2, i3, i4);
    }

    public /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
        return super.setState(iArr);
    }

    public void setTint(int i) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.b(drawable, i);
        } else {
            this.f164b.f168b.setTint(i);
        }
    }

    public void setTintList(ColorStateList colorStateList) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, colorStateList);
        } else {
            this.f164b.f168b.setTintList(colorStateList);
        }
    }

    public void setTintMode(PorterDuff.Mode mode) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            androidx.core.graphics.drawable.a.a(drawable, mode);
        } else {
            this.f164b.f168b.setTintMode(mode);
        }
    }

    public boolean setVisible(boolean z, boolean z2) {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            return drawable.setVisible(z, z2);
        }
        this.f164b.f168b.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    public void start() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).start();
        } else if (!this.f164b.f169c.isStarted()) {
            this.f164b.f169c.start();
            invalidateSelf();
        }
    }

    public void stop() {
        Drawable drawable = this.f176a;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).stop();
        } else {
            this.f164b.f169c.end();
        }
    }
}
