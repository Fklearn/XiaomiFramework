package androidx.core.provider;

import a.c.g;
import a.c.i;
import a.d.a.l;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.annotation.GuardedBy;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.k;
import com.xiaomi.stat.MiStat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class f {

    /* renamed from: a  reason: collision with root package name */
    static final g<String, Typeface> f747a = new g<>(16);

    /* renamed from: b  reason: collision with root package name */
    private static final k f748b = new k("fonts", 10, 10000);

    /* renamed from: c  reason: collision with root package name */
    static final Object f749c = new Object();
    @GuardedBy("sLock")

    /* renamed from: d  reason: collision with root package name */
    static final i<String, ArrayList<k.a<c>>> f750d = new i<>();
    private static final Comparator<byte[]> e = new e();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private final int f751a;

        /* renamed from: b  reason: collision with root package name */
        private final b[] f752b;

        @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
        public a(int i, @Nullable b[] bVarArr) {
            this.f751a = i;
            this.f752b = bVarArr;
        }

        public b[] a() {
            return this.f752b;
        }

        public int b() {
            return this.f751a;
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        private final Uri f753a;

        /* renamed from: b  reason: collision with root package name */
        private final int f754b;

        /* renamed from: c  reason: collision with root package name */
        private final int f755c;

        /* renamed from: d  reason: collision with root package name */
        private final boolean f756d;
        private final int e;

        @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
        public b(@NonNull Uri uri, @IntRange(from = 0) int i, @IntRange(from = 1, to = 1000) int i2, boolean z, int i3) {
            a.d.e.f.a(uri);
            this.f753a = uri;
            this.f754b = i;
            this.f755c = i2;
            this.f756d = z;
            this.e = i3;
        }

        public int a() {
            return this.e;
        }

        @IntRange(from = 0)
        public int b() {
            return this.f754b;
        }

        @NonNull
        public Uri c() {
            return this.f753a;
        }

        @IntRange(from = 1, to = 1000)
        public int d() {
            return this.f755c;
        }

        public boolean e() {
            return this.f756d;
        }
    }

    private static final class c {

        /* renamed from: a  reason: collision with root package name */
        final Typeface f757a;

        /* renamed from: b  reason: collision with root package name */
        final int f758b;

        c(@Nullable Typeface typeface, int i) {
            this.f757a = typeface;
            this.f758b = i;
        }
    }

    @VisibleForTesting
    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public static ProviderInfo a(@NonNull PackageManager packageManager, @NonNull a aVar, @Nullable Resources resources) {
        String d2 = aVar.d();
        ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(d2, 0);
        if (resolveContentProvider == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + d2);
        } else if (resolveContentProvider.packageName.equals(aVar.e())) {
            List<byte[]> a2 = a(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort(a2, e);
            List<List<byte[]>> a3 = a(aVar, resources);
            for (int i = 0; i < a3.size(); i++) {
                ArrayList arrayList = new ArrayList(a3.get(i));
                Collections.sort(arrayList, e);
                if (a(a2, (List<byte[]>) arrayList)) {
                    return resolveContentProvider;
                }
            }
            return null;
        } else {
            throw new PackageManager.NameNotFoundException("Found content provider " + d2 + ", but package was not " + aVar.e());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0072, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0083, code lost:
        f748b.a(r1, new androidx.core.provider.d(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x008d, code lost:
        return null;
     */
    @androidx.annotation.RestrictTo({androidx.annotation.RestrictTo.a.LIBRARY_GROUP_PREFIX})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Typeface a(android.content.Context r2, androidx.core.provider.a r3, @androidx.annotation.Nullable androidx.core.content.res.g.a r4, @androidx.annotation.Nullable android.os.Handler r5, boolean r6, int r7, int r8) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r3.c()
            r0.append(r1)
            java.lang.String r1 = "-"
            r0.append(r1)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            a.c.g<java.lang.String, android.graphics.Typeface> r1 = f747a
            java.lang.Object r1 = r1.b(r0)
            android.graphics.Typeface r1 = (android.graphics.Typeface) r1
            if (r1 == 0) goto L_0x0028
            if (r4 == 0) goto L_0x0027
            r4.a((android.graphics.Typeface) r1)
        L_0x0027:
            return r1
        L_0x0028:
            if (r6 == 0) goto L_0x0043
            r1 = -1
            if (r7 != r1) goto L_0x0043
            androidx.core.provider.f$c r2 = a((android.content.Context) r2, (androidx.core.provider.a) r3, (int) r8)
            if (r4 == 0) goto L_0x0040
            int r3 = r2.f758b
            if (r3 != 0) goto L_0x003d
            android.graphics.Typeface r3 = r2.f757a
            r4.a((android.graphics.Typeface) r3, (android.os.Handler) r5)
            goto L_0x0040
        L_0x003d:
            r4.a((int) r3, (android.os.Handler) r5)
        L_0x0040:
            android.graphics.Typeface r2 = r2.f757a
            return r2
        L_0x0043:
            androidx.core.provider.b r1 = new androidx.core.provider.b
            r1.<init>(r2, r3, r8, r0)
            r2 = 0
            if (r6 == 0) goto L_0x0056
            androidx.core.provider.k r3 = f748b     // Catch:{ InterruptedException -> 0x0055 }
            java.lang.Object r3 = r3.a(r1, (int) r7)     // Catch:{ InterruptedException -> 0x0055 }
            androidx.core.provider.f$c r3 = (androidx.core.provider.f.c) r3     // Catch:{ InterruptedException -> 0x0055 }
            android.graphics.Typeface r2 = r3.f757a     // Catch:{ InterruptedException -> 0x0055 }
        L_0x0055:
            return r2
        L_0x0056:
            if (r4 != 0) goto L_0x005a
            r3 = r2
            goto L_0x005f
        L_0x005a:
            androidx.core.provider.c r3 = new androidx.core.provider.c
            r3.<init>(r4, r5)
        L_0x005f:
            java.lang.Object r4 = f749c
            monitor-enter(r4)
            a.c.i<java.lang.String, java.util.ArrayList<androidx.core.provider.k$a<androidx.core.provider.f$c>>> r5 = f750d     // Catch:{ all -> 0x008e }
            java.lang.Object r5 = r5.get(r0)     // Catch:{ all -> 0x008e }
            java.util.ArrayList r5 = (java.util.ArrayList) r5     // Catch:{ all -> 0x008e }
            if (r5 == 0) goto L_0x0073
            if (r3 == 0) goto L_0x0071
            r5.add(r3)     // Catch:{ all -> 0x008e }
        L_0x0071:
            monitor-exit(r4)     // Catch:{ all -> 0x008e }
            return r2
        L_0x0073:
            if (r3 == 0) goto L_0x0082
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x008e }
            r5.<init>()     // Catch:{ all -> 0x008e }
            r5.add(r3)     // Catch:{ all -> 0x008e }
            a.c.i<java.lang.String, java.util.ArrayList<androidx.core.provider.k$a<androidx.core.provider.f$c>>> r3 = f750d     // Catch:{ all -> 0x008e }
            r3.put(r0, r5)     // Catch:{ all -> 0x008e }
        L_0x0082:
            monitor-exit(r4)     // Catch:{ all -> 0x008e }
            androidx.core.provider.k r3 = f748b
            androidx.core.provider.d r4 = new androidx.core.provider.d
            r4.<init>(r0)
            r3.a(r1, r4)
            return r2
        L_0x008e:
            r2 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x008e }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.provider.f.a(android.content.Context, androidx.core.provider.a, androidx.core.content.res.g$a, android.os.Handler, boolean, int, int):android.graphics.Typeface");
    }

    @NonNull
    public static a a(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull a aVar) {
        ProviderInfo a2 = a(context.getPackageManager(), aVar, context.getResources());
        return a2 == null ? new a(1, (b[]) null) : new a(0, a(context, aVar, a2.authority, cancellationSignal));
    }

    @NonNull
    static c a(Context context, a aVar, int i) {
        try {
            a a2 = a(context, (CancellationSignal) null, aVar);
            int i2 = -3;
            if (a2.b() == 0) {
                Typeface a3 = a.d.a.c.a(context, (CancellationSignal) null, a2.a(), i);
                if (a3 != null) {
                    i2 = 0;
                }
                return new c(a3, i2);
            }
            if (a2.b() == 1) {
                i2 = -2;
            }
            return new c((Typeface) null, i2);
        } catch (PackageManager.NameNotFoundException unused) {
            return new c((Typeface) null, -1);
        }
    }

    private static List<List<byte[]>> a(a aVar, Resources resources) {
        return aVar.a() != null ? aVar.a() : FontResourcesParserCompat.a(resources, aVar.b());
    }

    private static List<byte[]> a(Signature[] signatureArr) {
        ArrayList arrayList = new ArrayList();
        for (Signature byteArray : signatureArr) {
            arrayList.add(byteArray.toByteArray());
        }
        return arrayList;
    }

    @RequiresApi(19)
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public static Map<Uri, ByteBuffer> a(Context context, b[] bVarArr, CancellationSignal cancellationSignal) {
        HashMap hashMap = new HashMap();
        for (b bVar : bVarArr) {
            if (bVar.a() == 0) {
                Uri c2 = bVar.c();
                if (!hashMap.containsKey(c2)) {
                    hashMap.put(c2, l.a(context, cancellationSignal, c2));
                }
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    private static boolean a(List<byte[]> list, List<byte[]> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!Arrays.equals(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    @NonNull
    static b[] a(Context context, a aVar, String str, CancellationSignal cancellationSignal) {
        String str2 = str;
        ArrayList arrayList = new ArrayList();
        Uri build = new Uri.Builder().scheme(MiStat.Param.CONTENT).authority(str2).build();
        Uri build2 = new Uri.Builder().scheme(MiStat.Param.CONTENT).authority(str2).appendPath("file").build();
        Cursor cursor = null;
        try {
            cursor = Build.VERSION.SDK_INT > 16 ? context.getContentResolver().query(build, new String[]{"_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code"}, "query = ?", new String[]{aVar.f()}, (String) null, cancellationSignal) : context.getContentResolver().query(build, new String[]{"_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code"}, "query = ?", new String[]{aVar.f()}, (String) null);
            if (cursor != null && cursor.getCount() > 0) {
                int columnIndex = cursor.getColumnIndex("result_code");
                ArrayList arrayList2 = new ArrayList();
                int columnIndex2 = cursor.getColumnIndex("_id");
                int columnIndex3 = cursor.getColumnIndex("file_id");
                int columnIndex4 = cursor.getColumnIndex("font_ttc_index");
                int columnIndex5 = cursor.getColumnIndex("font_weight");
                int columnIndex6 = cursor.getColumnIndex("font_italic");
                while (cursor.moveToNext()) {
                    int i = columnIndex != -1 ? cursor.getInt(columnIndex) : 0;
                    arrayList2.add(new b(columnIndex3 == -1 ? ContentUris.withAppendedId(build, cursor.getLong(columnIndex2)) : ContentUris.withAppendedId(build2, cursor.getLong(columnIndex3)), columnIndex4 != -1 ? cursor.getInt(columnIndex4) : 0, columnIndex5 != -1 ? cursor.getInt(columnIndex5) : 400, columnIndex6 != -1 && cursor.getInt(columnIndex6) == 1, i));
                }
                arrayList = arrayList2;
            }
            return (b[]) arrayList.toArray(new b[0]);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
