package a.d.a;

import a.c.g;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.content.res.g;
import androidx.core.provider.f;

@SuppressLint({"NewApi"})
public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final k f106a;

    /* renamed from: b  reason: collision with root package name */
    private static final g<String, Typeface> f107b = new g<>(16);

    static {
        int i = Build.VERSION.SDK_INT;
        f106a = i >= 29 ? new h() : i >= 28 ? new g() : i >= 26 ? new f() : (i < 24 || !e.a()) ? Build.VERSION.SDK_INT >= 21 ? new d() : new k() : new e();
    }

    @Nullable
    @RestrictTo({RestrictTo.a.f224c})
    public static Typeface a(@NonNull Context context, @NonNull Resources resources, int i, String str, int i2) {
        Typeface a2 = f106a.a(context, resources, i, str, i2);
        if (a2 != null) {
            f107b.a(b(resources, i, i2), a2);
        }
        return a2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r2 = b(r2, r3, r4);
     */
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Typeface a(@androidx.annotation.NonNull android.content.Context r2, @androidx.annotation.Nullable android.graphics.Typeface r3, int r4) {
        /*
            if (r2 == 0) goto L_0x0014
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 >= r1) goto L_0x000f
            android.graphics.Typeface r2 = b((android.content.Context) r2, (android.graphics.Typeface) r3, (int) r4)
            if (r2 == 0) goto L_0x000f
            return r2
        L_0x000f:
            android.graphics.Typeface r2 = android.graphics.Typeface.create(r3, r4)
            return r2
        L_0x0014:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Context cannot be null"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: a.d.a.c.a(android.content.Context, android.graphics.Typeface, int):android.graphics.Typeface");
    }

    @Nullable
    @RestrictTo({RestrictTo.a.f224c})
    public static Typeface a(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull f.b[] bVarArr, int i) {
        return f106a.a(context, cancellationSignal, bVarArr, i);
    }

    @Nullable
    @RestrictTo({RestrictTo.a.f224c})
    public static Typeface a(@NonNull Context context, @NonNull FontResourcesParserCompat.a aVar, @NonNull Resources resources, int i, int i2, @Nullable g.a aVar2, @Nullable Handler handler, boolean z) {
        Typeface typeface;
        if (aVar instanceof FontResourcesParserCompat.d) {
            FontResourcesParserCompat.d dVar = (FontResourcesParserCompat.d) aVar;
            boolean z2 = false;
            if (!z ? aVar2 == null : dVar.a() == 0) {
                z2 = true;
            }
            typeface = f.a(context, dVar.b(), aVar2, handler, z2, z ? dVar.c() : -1, i2);
        } else {
            typeface = f106a.a(context, (FontResourcesParserCompat.b) aVar, resources, i2);
            if (aVar2 != null) {
                if (typeface != null) {
                    aVar2.a(typeface, handler);
                } else {
                    aVar2.a(-3, handler);
                }
            }
        }
        if (typeface != null) {
            f107b.a(b(resources, i, i2), typeface);
        }
        return typeface;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.f224c})
    public static Typeface a(@NonNull Resources resources, int i, int i2) {
        return f107b.b(b(resources, i, i2));
    }

    @Nullable
    private static Typeface b(Context context, Typeface typeface, int i) {
        FontResourcesParserCompat.b a2 = f106a.a(typeface);
        if (a2 == null) {
            return null;
        }
        return f106a.a(context, a2, context.getResources(), i);
    }

    private static String b(Resources resources, int i, int i2) {
        return resources.getResourcePackageName(i) + "-" + i + "-" + i2;
    }
}
