package a.d.a;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.FontResourcesParserCompat;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
@RestrictTo({RestrictTo.a.f224c})
class d extends k {

    /* renamed from: b  reason: collision with root package name */
    private static Class<?> f108b = null;

    /* renamed from: c  reason: collision with root package name */
    private static Constructor<?> f109c = null;

    /* renamed from: d  reason: collision with root package name */
    private static Method f110d = null;
    private static Method e = null;
    private static boolean f = false;

    d() {
    }

    private static Typeface a(Object obj) {
        a();
        try {
            Object newInstance = Array.newInstance(f108b, 1);
            Array.set(newInstance, 0, obj);
            return (Typeface) e.invoke((Object) null, new Object[]{newInstance});
        } catch (IllegalAccessException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }

    private File a(@NonNull ParcelFileDescriptor parcelFileDescriptor) {
        try {
            String readlink = Os.readlink("/proc/self/fd/" + parcelFileDescriptor.getFd());
            if (OsConstants.S_ISREG(Os.stat(readlink).st_mode)) {
                return new File(readlink);
            }
        } catch (ErrnoException unused) {
        }
        return null;
    }

    private static void a() {
        Method method;
        Class<?> cls;
        Method method2;
        if (!f) {
            f = true;
            Constructor<?> constructor = null;
            try {
                cls = Class.forName("android.graphics.FontFamily");
                Constructor<?> constructor2 = cls.getConstructor(new Class[0]);
                method = cls.getMethod("addFontWeightStyle", new Class[]{String.class, Integer.TYPE, Boolean.TYPE});
                method2 = Typeface.class.getMethod("createFromFamiliesWithDefault", new Class[]{Array.newInstance(cls, 1).getClass()});
                constructor = constructor2;
            } catch (ClassNotFoundException | NoSuchMethodException e2) {
                Log.e("TypefaceCompatApi21Impl", e2.getClass().getName(), e2);
                method2 = null;
                cls = null;
                method = null;
            }
            f109c = constructor;
            f108b = cls;
            f110d = method;
            e = method2;
        }
    }

    private static boolean a(Object obj, String str, int i, boolean z) {
        a();
        try {
            return ((Boolean) f110d.invoke(obj, new Object[]{str, Integer.valueOf(i), Boolean.valueOf(z)})).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }

    private static Object b() {
        a();
        try {
            return f109c.newInstance(new Object[0]);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004d, code lost:
        r5 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x004e, code lost:
        r8 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0052, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0053, code lost:
        r3 = r8;
        r8 = r5;
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0065, code lost:
        r5 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0066, code lost:
        r7 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x006a, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x006b, code lost:
        r3 = r7;
        r7 = r5;
        r5 = r3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0065 A[ExcHandler: all (th java.lang.Throwable)] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Typeface a(android.content.Context r5, android.os.CancellationSignal r6, @androidx.annotation.NonNull androidx.core.provider.f.b[] r7, int r8) {
        /*
            r4 = this;
            int r0 = r7.length
            r1 = 0
            r2 = 1
            if (r0 >= r2) goto L_0x0006
            return r1
        L_0x0006:
            androidx.core.provider.f$b r7 = r4.a((androidx.core.provider.f.b[]) r7, (int) r8)
            android.content.ContentResolver r8 = r5.getContentResolver()
            android.net.Uri r7 = r7.c()     // Catch:{ IOException -> 0x007f }
            java.lang.String r0 = "r"
            android.os.ParcelFileDescriptor r6 = r8.openFileDescriptor(r7, r0, r6)     // Catch:{ IOException -> 0x007f }
            if (r6 != 0) goto L_0x0020
            if (r6 == 0) goto L_0x001f
            r6.close()     // Catch:{ IOException -> 0x007f }
        L_0x001f:
            return r1
        L_0x0020:
            java.io.File r7 = r4.a((android.os.ParcelFileDescriptor) r6)     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            if (r7 == 0) goto L_0x0037
            boolean r8 = r7.canRead()     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            if (r8 != 0) goto L_0x002d
            goto L_0x0037
        L_0x002d:
            android.graphics.Typeface r5 = android.graphics.Typeface.createFromFile(r7)     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            if (r6 == 0) goto L_0x0036
            r6.close()     // Catch:{ IOException -> 0x007f }
        L_0x0036:
            return r5
        L_0x0037:
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            java.io.FileDescriptor r8 = r6.getFileDescriptor()     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            r7.<init>(r8)     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            android.graphics.Typeface r5 = super.a((android.content.Context) r5, (java.io.InputStream) r7)     // Catch:{ Throwable -> 0x0050, all -> 0x004d }
            r7.close()     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            if (r6 == 0) goto L_0x004c
            r6.close()     // Catch:{ IOException -> 0x007f }
        L_0x004c:
            return r5
        L_0x004d:
            r5 = move-exception
            r8 = r1
            goto L_0x0056
        L_0x0050:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r8 = move-exception
            r3 = r8
            r8 = r5
            r5 = r3
        L_0x0056:
            if (r8 == 0) goto L_0x0061
            r7.close()     // Catch:{ Throwable -> 0x005c, all -> 0x0065 }
            goto L_0x0064
        L_0x005c:
            r7 = move-exception
            r8.addSuppressed(r7)     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
            goto L_0x0064
        L_0x0061:
            r7.close()     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
        L_0x0064:
            throw r5     // Catch:{ Throwable -> 0x0068, all -> 0x0065 }
        L_0x0065:
            r5 = move-exception
            r7 = r1
            goto L_0x006e
        L_0x0068:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x006a }
        L_0x006a:
            r7 = move-exception
            r3 = r7
            r7 = r5
            r5 = r3
        L_0x006e:
            if (r6 == 0) goto L_0x007e
            if (r7 == 0) goto L_0x007b
            r6.close()     // Catch:{ Throwable -> 0x0076 }
            goto L_0x007e
        L_0x0076:
            r6 = move-exception
            r7.addSuppressed(r6)     // Catch:{ IOException -> 0x007f }
            goto L_0x007e
        L_0x007b:
            r6.close()     // Catch:{ IOException -> 0x007f }
        L_0x007e:
            throw r5     // Catch:{ IOException -> 0x007f }
        L_0x007f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: a.d.a.d.a(android.content.Context, android.os.CancellationSignal, androidx.core.provider.f$b[], int):android.graphics.Typeface");
    }

    public Typeface a(Context context, FontResourcesParserCompat.b bVar, Resources resources, int i) {
        Object b2 = b();
        FontResourcesParserCompat.c[] a2 = bVar.a();
        int length = a2.length;
        int i2 = 0;
        while (i2 < length) {
            FontResourcesParserCompat.c cVar = a2[i2];
            File a3 = l.a(context);
            if (a3 == null) {
                return null;
            }
            try {
                if (!l.a(a3, resources, cVar.b())) {
                    a3.delete();
                    return null;
                } else if (!a(b2, a3.getPath(), cVar.e(), cVar.f())) {
                    return null;
                } else {
                    a3.delete();
                    i2++;
                }
            } catch (RuntimeException unused) {
                return null;
            } finally {
                a3.delete();
            }
        }
        return a(b2);
    }
}
