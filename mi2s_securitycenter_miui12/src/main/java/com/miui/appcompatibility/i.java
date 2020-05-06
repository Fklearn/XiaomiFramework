package com.miui.appcompatibility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import b.b.c.j.x;
import java.util.ArrayList;
import java.util.List;

public class i {

    /* renamed from: a  reason: collision with root package name */
    public static List<String> f3083a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public final Context f3084b;

    /* renamed from: c  reason: collision with root package name */
    private final Resources f3085c;

    /* renamed from: d  reason: collision with root package name */
    private final int f3086d;
    private final String e;

    private class a implements d {
        private a() {
        }

        public void a(XmlResourceParser xmlResourceParser, List<String> list) {
            String a2 = i.c(xmlResourceParser, "packageName");
            if (!TextUtils.isEmpty(a2)) {
                list.add(a2);
            }
        }
    }

    private class b implements d {

        /* renamed from: a  reason: collision with root package name */
        private final ArrayMap<String, d> f3088a;

        b(i iVar) {
            this(iVar.b());
        }

        b(ArrayMap<String, d> arrayMap) {
            this.f3088a = arrayMap;
        }

        public void a(XmlResourceParser xmlResourceParser, List<String> list) {
            d dVar;
            int depth = xmlResourceParser.getDepth();
            while (true) {
                int next = xmlResourceParser.next();
                if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                    return;
                }
                if (next == 2 && (dVar = this.f3088a.get(xmlResourceParser.getName())) != null) {
                    dVar.a(xmlResourceParser, list);
                }
            }
        }
    }

    private class c implements d {
        private c() {
        }

        public void a(XmlResourceParser xmlResourceParser, List<String> list) {
            String a2 = i.c(xmlResourceParser, "packageName");
            if (!TextUtils.isEmpty(a2) && !x.h(i.this.f3084b, a2)) {
                list.add(a2);
            }
        }
    }

    interface d {
        void a(XmlResourceParser xmlResourceParser, List<String> list);
    }

    private i(Context context, Resources resources, int i, String str) {
        this.f3084b = context;
        this.e = str;
        this.f3085c = resources;
        this.f3086d = i;
    }

    static Pair<String, Resources> a(String str, PackageManager packageManager) {
        for (ResolveInfo next : packageManager.queryBroadcastReceivers(new Intent(str), 0)) {
            ActivityInfo activityInfo = next.activityInfo;
            if (!(activityInfo == null || (activityInfo.applicationInfo.flags & 1) == 0)) {
                String str2 = next.activityInfo.packageName;
                try {
                    return Pair.create(str2, packageManager.getResourcesForApplication(str2));
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }

    public static i a(Context context) {
        Pair<String, Resources> a2 = a("android.autoinstalls.config.action.PLAY_AUTO_INSTALL", context.getPackageManager());
        if (a2 != null) {
            return a(context, (String) a2.first, (Resources) a2.second);
        }
        Log.d("AutoInstalls", "not found pai config apk");
        return null;
    }

    private static i a(Context context, String str, Resources resources) {
        int identifier = resources.getIdentifier("default_layout", "xml", str);
        if (identifier != 0) {
            return new i(context, resources, identifier, "workspace");
        }
        Log.e("AutoInstalls", "Layout definition not found in package: " + str);
        return null;
    }

    private void a(XmlResourceParser xmlResourceParser, ArrayMap<String, d> arrayMap, List<String> list) {
        d dVar = arrayMap.get(xmlResourceParser.getName());
        if (dVar != null) {
            dVar.a(xmlResourceParser, list);
        }
    }

    /* access modifiers changed from: private */
    public ArrayMap<String, d> b() {
        ArrayMap<String, d> arrayMap = new ArrayMap<>();
        arrayMap.put("autoinstall", new a());
        return arrayMap;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:8|9|(2:13|(4:16|(2:18|27)(1:28)|19|14))|20|21|22|23) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0038 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized java.util.List<java.lang.String> b(android.content.Context r3) {
        /*
            java.lang.Class<com.miui.appcompatibility.i> r0 = com.miui.appcompatibility.i.class
            monitor-enter(r0)
            java.util.List<java.lang.String> r1 = f3083a     // Catch:{ all -> 0x003c }
            if (r1 == 0) goto L_0x000b
            java.util.List<java.lang.String> r3 = f3083a     // Catch:{ all -> 0x003c }
            monitor-exit(r0)
            return r3
        L_0x000b:
            com.miui.appcompatibility.i r3 = a((android.content.Context) r3)     // Catch:{ Exception -> 0x0038 }
            if (r3 == 0) goto L_0x0038
            java.util.List r3 = r3.a()     // Catch:{ Exception -> 0x0038 }
            if (r3 == 0) goto L_0x0038
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x0038 }
        L_0x001b:
            boolean r1 = r3.hasNext()     // Catch:{ Exception -> 0x0038 }
            if (r1 == 0) goto L_0x0038
            java.lang.Object r1 = r3.next()     // Catch:{ Exception -> 0x0038 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0038 }
            java.util.List<java.lang.String> r2 = f3083a     // Catch:{ Exception -> 0x0038 }
            if (r2 != 0) goto L_0x0032
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0038 }
            r2.<init>()     // Catch:{ Exception -> 0x0038 }
            f3083a = r2     // Catch:{ Exception -> 0x0038 }
        L_0x0032:
            java.util.List<java.lang.String> r2 = f3083a     // Catch:{ Exception -> 0x0038 }
            r2.add(r1)     // Catch:{ Exception -> 0x0038 }
            goto L_0x001b
        L_0x0038:
            java.util.List<java.lang.String> r3 = f3083a     // Catch:{ all -> 0x003c }
            monitor-exit(r0)
            return r3
        L_0x003c:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.i.b(android.content.Context):java.util.List");
    }

    /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    private static void b(android.content.res.XmlResourceParser r3, java.lang.String r4) {
        /*
        L_0x0000:
            int r0 = r3.next()
            r1 = 2
            if (r0 == r1) goto L_0x000b
            r2 = 1
            if (r0 == r2) goto L_0x000b
            goto L_0x0000
        L_0x000b:
            if (r0 != r1) goto L_0x003b
            java.lang.String r0 = r3.getName()
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0018
            return
        L_0x0018:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unexpected start tag: found "
            r1.append(r2)
            java.lang.String r3 = r3.getName()
            r1.append(r3)
            java.lang.String r3 = ", expected "
            r1.append(r3)
            r1.append(r4)
            java.lang.String r3 = r1.toString()
            r0.<init>(r3)
            throw r0
        L_0x003b:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "No start tag found"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.i.b(android.content.res.XmlResourceParser, java.lang.String):void");
    }

    private ArrayMap<String, d> c() {
        ArrayMap<String, d> arrayMap = new ArrayMap<>();
        arrayMap.put("autoinstall", new a());
        arrayMap.put("folder", new b(this));
        arrayMap.put("appwidget", new c());
        return arrayMap;
    }

    /* access modifiers changed from: private */
    public static String c(XmlResourceParser xmlResourceParser, String str) {
        return xmlResourceParser.getAttributeValue((String) null, str);
    }

    public List<String> a() {
        try {
            ArrayList arrayList = new ArrayList();
            XmlResourceParser xml = this.f3085c.getXml(this.f3086d);
            b(xml, this.e);
            int depth = xml.getDepth();
            ArrayMap<String, d> c2 = c();
            while (true) {
                int next = xml.next();
                if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                    if (next == 2) {
                        a(xml, c2, (List<String>) arrayList);
                    }
                }
            }
            return arrayList;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
