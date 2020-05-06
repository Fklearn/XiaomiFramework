package a.d.a;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.f;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

@RestrictTo({RestrictTo.a.f224c})
class k {

    /* renamed from: a  reason: collision with root package name */
    private ConcurrentHashMap<Long, FontResourcesParserCompat.b> f116a = new ConcurrentHashMap<>();

    private interface a<T> {
        boolean a(T t);

        int b(T t);
    }

    k() {
    }

    private FontResourcesParserCompat.c a(FontResourcesParserCompat.b bVar, int i) {
        return (FontResourcesParserCompat.c) a(bVar.a(), i, new j(this));
    }

    private static <T> T a(T[] tArr, int i, a<T> aVar) {
        int i2 = (i & 1) == 0 ? 400 : 700;
        boolean z = (i & 2) != 0;
        int i3 = Integer.MAX_VALUE;
        T t = null;
        for (T t2 : tArr) {
            int abs = (Math.abs(aVar.b(t2) - i2) * 2) + (aVar.a(t2) == z ? 0 : 1);
            if (t == null || i3 > abs) {
                t = t2;
                i3 = abs;
            }
        }
        return t;
    }

    private void a(Typeface typeface, FontResourcesParserCompat.b bVar) {
        long b2 = b(typeface);
        if (b2 != 0) {
            this.f116a.put(Long.valueOf(b2), bVar);
        }
    }

    private static long b(@Nullable Typeface typeface) {
        if (typeface == null) {
            return 0;
        }
        try {
            Field declaredField = Typeface.class.getDeclaredField("native_instance");
            declaredField.setAccessible(true);
            return ((Number) declaredField.get(typeface)).longValue();
        } catch (NoSuchFieldException e) {
            Log.e("TypefaceCompatBaseImpl", "Could not retrieve font from family.", e);
            return 0;
        } catch (IllegalAccessException e2) {
            Log.e("TypefaceCompatBaseImpl", "Could not retrieve font from family.", e2);
            return 0;
        }
    }

    @Nullable
    public Typeface a(Context context, Resources resources, int i, String str, int i2) {
        File a2 = l.a(context);
        if (a2 == null) {
            return null;
        }
        try {
            if (!l.a(a2, resources, i)) {
                return null;
            }
            Typeface createFromFile = Typeface.createFromFile(a2.getPath());
            a2.delete();
            return createFromFile;
        } catch (RuntimeException unused) {
            return null;
        } finally {
            a2.delete();
        }
    }

    @Nullable
    public Typeface a(Context context, @Nullable CancellationSignal cancellationSignal, @NonNull f.b[] bVarArr, int i) {
        InputStream inputStream;
        InputStream inputStream2 = null;
        if (bVarArr.length < 1) {
            return null;
        }
        try {
            inputStream = context.getContentResolver().openInputStream(a(bVarArr, i).c());
            try {
                Typeface a2 = a(context, inputStream);
                l.a((Closeable) inputStream);
                return a2;
            } catch (IOException unused) {
                l.a((Closeable) inputStream);
                return null;
            } catch (Throwable th) {
                th = th;
                inputStream2 = inputStream;
                l.a((Closeable) inputStream2);
                throw th;
            }
        } catch (IOException unused2) {
            inputStream = null;
            l.a((Closeable) inputStream);
            return null;
        } catch (Throwable th2) {
            th = th2;
            l.a((Closeable) inputStream2);
            throw th;
        }
    }

    @Nullable
    public Typeface a(Context context, FontResourcesParserCompat.b bVar, Resources resources, int i) {
        FontResourcesParserCompat.c a2 = a(bVar, i);
        if (a2 == null) {
            return null;
        }
        Typeface a3 = c.a(context, resources, a2.b(), a2.a(), i);
        a(a3, bVar);
        return a3;
    }

    /* access modifiers changed from: protected */
    public Typeface a(Context context, InputStream inputStream) {
        File a2 = l.a(context);
        if (a2 == null) {
            return null;
        }
        try {
            if (!l.a(a2, inputStream)) {
                return null;
            }
            Typeface createFromFile = Typeface.createFromFile(a2.getPath());
            a2.delete();
            return createFromFile;
        } catch (RuntimeException unused) {
            return null;
        } finally {
            a2.delete();
        }
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public FontResourcesParserCompat.b a(Typeface typeface) {
        long b2 = b(typeface);
        if (b2 == 0) {
            return null;
        }
        return this.f116a.get(Long.valueOf(b2));
    }

    /* access modifiers changed from: protected */
    public f.b a(f.b[] bVarArr, int i) {
        return (f.b) a(bVarArr, i, new i(this));
    }
}
