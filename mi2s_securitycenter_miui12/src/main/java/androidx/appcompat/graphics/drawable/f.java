package androidx.appcompat.graphics.drawable;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.appcompat.graphics.drawable.d;

@SuppressLint({"RestrictedAPI"})
@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
class f extends d {
    private a m;
    private boolean n;

    static class a extends d.b {
        int[][] J;

        a(a aVar, f fVar, Resources resources) {
            super(aVar, fVar, resources);
            this.J = aVar != null ? aVar.J : new int[c()][];
        }

        /* access modifiers changed from: package-private */
        public int a(int[] iArr) {
            int[][] iArr2 = this.J;
            int d2 = d();
            for (int i = 0; i < d2; i++) {
                if (StateSet.stateSetMatches(iArr2[i], iArr)) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        public int a(int[] iArr, Drawable drawable) {
            int a2 = a(drawable);
            this.J[a2] = iArr;
            return a2;
        }

        public void a(int i, int i2) {
            super.a(i, i2);
            int[][] iArr = new int[i2][];
            System.arraycopy(this.J, 0, iArr, 0, i);
            this.J = iArr;
        }

        /* access modifiers changed from: package-private */
        public void m() {
            int[][] iArr = this.J;
            int[][] iArr2 = new int[iArr.length][];
            for (int length = iArr.length - 1; length >= 0; length--) {
                int[][] iArr3 = this.J;
                iArr2[length] = iArr3[length] != null ? (int[]) iArr3[length].clone() : null;
            }
            this.J = iArr2;
        }

        @NonNull
        public Drawable newDrawable() {
            return new f(this, (Resources) null);
        }

        @NonNull
        public Drawable newDrawable(Resources resources) {
            return new f(this, resources);
        }
    }

    f(@Nullable a aVar) {
        if (aVar != null) {
            a((d.b) aVar);
        }
    }

    f(a aVar, Resources resources) {
        a((d.b) new a(aVar, this, resources));
        onStateChange(getState());
    }

    /* access modifiers changed from: package-private */
    public a a() {
        return new a(this.m, this, (Resources) null);
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull d.b bVar) {
        super.a(bVar);
        if (bVar instanceof a) {
            this.m = (a) bVar;
        }
    }

    /* access modifiers changed from: package-private */
    public int[] a(AttributeSet attributeSet) {
        int attributeCount = attributeSet.getAttributeCount();
        int[] iArr = new int[attributeCount];
        int i = 0;
        for (int i2 = 0; i2 < attributeCount; i2++) {
            int attributeNameResource = attributeSet.getAttributeNameResource(i2);
            if (!(attributeNameResource == 0 || attributeNameResource == 16842960 || attributeNameResource == 16843161)) {
                int i3 = i + 1;
                if (!attributeSet.getAttributeBooleanValue(i2, false)) {
                    attributeNameResource = -attributeNameResource;
                }
                iArr[i] = attributeNameResource;
                i = i3;
            }
        }
        return StateSet.trimStateSet(iArr, i);
    }

    @RequiresApi(21)
    public void applyTheme(@NonNull Resources.Theme theme) {
        super.applyTheme(theme);
        onStateChange(getState());
    }

    public boolean isStateful() {
        return true;
    }

    @NonNull
    public Drawable mutate() {
        if (!this.n) {
            super.mutate();
            if (this == this) {
                this.m.m();
                this.n = true;
            }
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        boolean onStateChange = super.onStateChange(iArr);
        int a2 = this.m.a(iArr);
        if (a2 < 0) {
            a2 = this.m.a(StateSet.WILD_CARD);
        }
        return a(a2) || onStateChange;
    }
}
