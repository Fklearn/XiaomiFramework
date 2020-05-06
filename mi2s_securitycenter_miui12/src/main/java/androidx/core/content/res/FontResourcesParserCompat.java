package androidx.core.content.res;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Base64;
import android.util.TypedValue;
import android.util.Xml;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class FontResourcesParserCompat {

    @Retention(RetentionPolicy.SOURCE)
    public @interface FetchStrategy {
    }

    public interface a {
    }

    public static final class b implements a {
        @NonNull

        /* renamed from: a  reason: collision with root package name */
        private final c[] f703a;

        public b(@NonNull c[] cVarArr) {
            this.f703a = cVarArr;
        }

        @NonNull
        public c[] a() {
            return this.f703a;
        }
    }

    public static final class c {
        @NonNull

        /* renamed from: a  reason: collision with root package name */
        private final String f704a;

        /* renamed from: b  reason: collision with root package name */
        private int f705b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f706c;

        /* renamed from: d  reason: collision with root package name */
        private String f707d;
        private int e;
        private int f;

        public c(@NonNull String str, int i, boolean z, @Nullable String str2, int i2, int i3) {
            this.f704a = str;
            this.f705b = i;
            this.f706c = z;
            this.f707d = str2;
            this.e = i2;
            this.f = i3;
        }

        @NonNull
        public String a() {
            return this.f704a;
        }

        public int b() {
            return this.f;
        }

        public int c() {
            return this.e;
        }

        @Nullable
        public String d() {
            return this.f707d;
        }

        public int e() {
            return this.f705b;
        }

        public boolean f() {
            return this.f706c;
        }
    }

    public static final class d implements a {
        @NonNull

        /* renamed from: a  reason: collision with root package name */
        private final androidx.core.provider.a f708a;

        /* renamed from: b  reason: collision with root package name */
        private final int f709b;

        /* renamed from: c  reason: collision with root package name */
        private final int f710c;

        public d(@NonNull androidx.core.provider.a aVar, int i, int i2) {
            this.f708a = aVar;
            this.f710c = i;
            this.f709b = i2;
        }

        public int a() {
            return this.f710c;
        }

        @NonNull
        public androidx.core.provider.a b() {
            return this.f708a;
        }

        public int c() {
            return this.f709b;
        }
    }

    private static int a(TypedArray typedArray, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return typedArray.getType(i);
        }
        TypedValue typedValue = new TypedValue();
        typedArray.getValue(i, typedValue);
        return typedValue.type;
    }

    /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    @androidx.annotation.Nullable
    public static androidx.core.content.res.FontResourcesParserCompat.a a(org.xmlpull.v1.XmlPullParser r3, android.content.res.Resources r4) {
        /*
        L_0x0000:
            int r0 = r3.next()
            r1 = 2
            if (r0 == r1) goto L_0x000b
            r2 = 1
            if (r0 == r2) goto L_0x000b
            goto L_0x0000
        L_0x000b:
            if (r0 != r1) goto L_0x0012
            androidx.core.content.res.FontResourcesParserCompat$a r3 = b(r3, r4)
            return r3
        L_0x0012:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "No start tag found"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.content.res.FontResourcesParserCompat.a(org.xmlpull.v1.XmlPullParser, android.content.res.Resources):androidx.core.content.res.FontResourcesParserCompat$a");
    }

    public static List<List<byte[]>> a(Resources resources, @ArrayRes int i) {
        if (i == 0) {
            return Collections.emptyList();
        }
        TypedArray obtainTypedArray = resources.obtainTypedArray(i);
        try {
            if (obtainTypedArray.length() == 0) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            if (a(obtainTypedArray, 0) == 1) {
                for (int i2 = 0; i2 < obtainTypedArray.length(); i2++) {
                    int resourceId = obtainTypedArray.getResourceId(i2, 0);
                    if (resourceId != 0) {
                        arrayList.add(a(resources.getStringArray(resourceId)));
                    }
                }
            } else {
                arrayList.add(a(resources.getStringArray(i)));
            }
            obtainTypedArray.recycle();
            return arrayList;
        } finally {
            obtainTypedArray.recycle();
        }
    }

    private static List<byte[]> a(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        for (String decode : strArr) {
            arrayList.add(Base64.decode(decode, 0));
        }
        return arrayList;
    }

    private static void a(XmlPullParser xmlPullParser) {
        int i = 1;
        while (i > 0) {
            int next = xmlPullParser.next();
            if (next == 2) {
                i++;
            } else if (next == 3) {
                i--;
            }
        }
    }

    @Nullable
    private static a b(XmlPullParser xmlPullParser, Resources resources) {
        xmlPullParser.require(2, (String) null, "font-family");
        if (xmlPullParser.getName().equals("font-family")) {
            return c(xmlPullParser, resources);
        }
        a(xmlPullParser);
        return null;
    }

    @Nullable
    private static a c(XmlPullParser xmlPullParser, Resources resources) {
        TypedArray obtainAttributes = resources.obtainAttributes(Xml.asAttributeSet(xmlPullParser), a.d.c.FontFamily);
        String string = obtainAttributes.getString(a.d.c.FontFamily_fontProviderAuthority);
        String string2 = obtainAttributes.getString(a.d.c.FontFamily_fontProviderPackage);
        String string3 = obtainAttributes.getString(a.d.c.FontFamily_fontProviderQuery);
        int resourceId = obtainAttributes.getResourceId(a.d.c.FontFamily_fontProviderCerts, 0);
        int integer = obtainAttributes.getInteger(a.d.c.FontFamily_fontProviderFetchStrategy, 1);
        int integer2 = obtainAttributes.getInteger(a.d.c.FontFamily_fontProviderFetchTimeout, 500);
        obtainAttributes.recycle();
        if (string == null || string2 == null || string3 == null) {
            ArrayList arrayList = new ArrayList();
            while (xmlPullParser.next() != 3) {
                if (xmlPullParser.getEventType() == 2) {
                    if (xmlPullParser.getName().equals("font")) {
                        arrayList.add(d(xmlPullParser, resources));
                    } else {
                        a(xmlPullParser);
                    }
                }
            }
            if (arrayList.isEmpty()) {
                return null;
            }
            return new b((c[]) arrayList.toArray(new c[arrayList.size()]));
        }
        while (xmlPullParser.next() != 3) {
            a(xmlPullParser);
        }
        return new d(new androidx.core.provider.a(string, string2, string3, a(resources, resourceId)), integer, integer2);
    }

    private static c d(XmlPullParser xmlPullParser, Resources resources) {
        TypedArray obtainAttributes = resources.obtainAttributes(Xml.asAttributeSet(xmlPullParser), a.d.c.FontFamilyFont);
        int i = obtainAttributes.getInt(obtainAttributes.hasValue(a.d.c.FontFamilyFont_fontWeight) ? a.d.c.FontFamilyFont_fontWeight : a.d.c.FontFamilyFont_android_fontWeight, 400);
        boolean z = 1 == obtainAttributes.getInt(obtainAttributes.hasValue(a.d.c.FontFamilyFont_fontStyle) ? a.d.c.FontFamilyFont_fontStyle : a.d.c.FontFamilyFont_android_fontStyle, 0);
        int i2 = obtainAttributes.hasValue(a.d.c.FontFamilyFont_ttcIndex) ? a.d.c.FontFamilyFont_ttcIndex : a.d.c.FontFamilyFont_android_ttcIndex;
        String string = obtainAttributes.getString(obtainAttributes.hasValue(a.d.c.FontFamilyFont_fontVariationSettings) ? a.d.c.FontFamilyFont_fontVariationSettings : a.d.c.FontFamilyFont_android_fontVariationSettings);
        int i3 = obtainAttributes.getInt(i2, 0);
        int i4 = obtainAttributes.hasValue(a.d.c.FontFamilyFont_font) ? a.d.c.FontFamilyFont_font : a.d.c.FontFamilyFont_android_font;
        int resourceId = obtainAttributes.getResourceId(i4, 0);
        String string2 = obtainAttributes.getString(i4);
        obtainAttributes.recycle();
        while (xmlPullParser.next() != 3) {
            a(xmlPullParser);
        }
        return new c(string2, i, z, string, i3, resourceId);
    }
}
