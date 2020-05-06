package de.greenrobot.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SubscriberMethodFinder {
    private static final int BRIDGE = 64;
    private static final int MODIFIERS_IGNORE = 5192;
    private static final String ON_EVENT_METHOD_NAME = "onEvent";
    private static final int SYNTHETIC = 4096;
    private static final Map<String, List<SubscriberMethod>> methodCache = new HashMap();
    private final Map<Class<?>, Class<?>> skipMethodVerificationForClasses = new ConcurrentHashMap();

    SubscriberMethodFinder(List<Class<?>> skipMethodVerificationForClassesList) {
        if (skipMethodVerificationForClassesList != null) {
            for (Class<?> clazz : skipMethodVerificationForClassesList) {
                this.skipMethodVerificationForClasses.put(clazz, clazz);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
        r3 = new java.util.ArrayList<>();
        r4 = new java.util.HashSet<>();
        r5 = new java.lang.StringBuilder();
        r6 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002a, code lost:
        if (r6 == null) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        r0 = r6.getName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0036, code lost:
        if (r0.startsWith("java.") != false) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        if (r0.startsWith("javax.") != false) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        if (r0.startsWith("android.") == false) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
        r7 = r6.getDeclaredMethods();
        r8 = r7;
        r9 = r8.length;
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0051, code lost:
        if (r10 >= r9) goto L_0x013a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0053, code lost:
        r11 = r8[r10];
        r12 = r11.getName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005f, code lost:
        if (r12.startsWith(ON_EVENT_METHOD_NAME) == false) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0061, code lost:
        r13 = r11.getModifiers();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0067, code lost:
        if ((r13 & 1) == 0) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006b, code lost:
        if ((r13 & MODIFIERS_IGNORE) != 0) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006d, code lost:
        r14 = r11.getParameterTypes();
        r16 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0075, code lost:
        if (r14.length != 1) goto L_0x00ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0077, code lost:
        r0 = r12.substring(ON_EVENT_METHOD_NAME.length());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0085, code lost:
        if (r0.length() != 0) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0087, code lost:
        r15 = de.greenrobot.event.ThreadMode.PostThread;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0090, code lost:
        if (r0.equals("MainThread") == false) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0092, code lost:
        r15 = de.greenrobot.event.ThreadMode.MainThread;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009b, code lost:
        if (r0.equals("BackgroundThread") == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x009d, code lost:
        r15 = de.greenrobot.event.ThreadMode.BackgroundThread;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a6, code lost:
        if (r0.equals("Async") == false) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a8, code lost:
        r15 = de.greenrobot.event.ThreadMode.Async;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00aa, code lost:
        r17 = r0;
        r18 = r7;
        r7 = r14[0];
        r5.setLength(0);
        r5.append(r12);
        r5.append('>');
        r5.append(r7.getName());
        r0 = r5.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00cb, code lost:
        if (r4.add(r0) == false) goto L_0x00d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cd, code lost:
        r19 = r0;
        r3.add(new de.greenrobot.event.SubscriberMethod(r11, r15, r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d8, code lost:
        r19 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00db, code lost:
        r17 = r0;
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e5, code lost:
        if (r1.skipMethodVerificationForClasses.containsKey(r6) == false) goto L_0x00e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00fe, code lost:
        throw new de.greenrobot.event.EventBusException("Illegal onEvent method, check for typos: " + r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ff, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0102, code lost:
        r16 = r0;
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x010c, code lost:
        if (r1.skipMethodVerificationForClasses.containsKey(r6) != false) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x010e, code lost:
        r0 = de.greenrobot.event.EventBus.TAG;
        android.util.Log.d(r0, "Skipping method (not public, static or abstract): " + r6 + "." + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x012e, code lost:
        r16 = r0;
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0132, code lost:
        r10 = r10 + 1;
        r0 = r16;
        r7 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x013a, code lost:
        r16 = r0;
        r18 = r7;
        r6 = r6.getSuperclass();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0144, code lost:
        r16 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x014a, code lost:
        if (r3.isEmpty() != false) goto L_0x0159;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x014c, code lost:
        r7 = methodCache;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x014e, code lost:
        monitor-enter(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        methodCache.put(r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0154, code lost:
        monitor-exit(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0155, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x017b, code lost:
        throw new de.greenrobot.event.EventBusException("Subscriber " + r21 + " has no public methods called " + ON_EVENT_METHOD_NAME);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0014, code lost:
        if (r4 == null) goto L_0x0017;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<de.greenrobot.event.SubscriberMethod> findSubscriberMethods(java.lang.Class<?> r21) {
        /*
            r20 = this;
            r1 = r20
            java.lang.String r2 = r21.getName()
            java.util.Map<java.lang.String, java.util.List<de.greenrobot.event.SubscriberMethod>> r3 = methodCache
            monitor-enter(r3)
            r4 = 0
            java.util.Map<java.lang.String, java.util.List<de.greenrobot.event.SubscriberMethod>> r0 = methodCache     // Catch:{ all -> 0x0180 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0180 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ all -> 0x0180 }
            r4 = r0
            monitor-exit(r3)     // Catch:{ all -> 0x017c }
            if (r4 == 0) goto L_0x0017
            return r4
        L_0x0017:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            r0 = r21
            java.util.HashSet r4 = new java.util.HashSet
            r4.<init>()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r6 = r0
        L_0x002a:
            if (r6 == 0) goto L_0x0146
            java.lang.String r0 = r6.getName()
            java.lang.String r7 = "java."
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0144
            java.lang.String r7 = "javax."
            boolean r7 = r0.startsWith(r7)
            if (r7 != 0) goto L_0x0144
            java.lang.String r7 = "android."
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x004a
            goto L_0x0146
        L_0x004a:
            java.lang.reflect.Method[] r7 = r6.getDeclaredMethods()
            r8 = r7
            int r9 = r8.length
            r10 = 0
        L_0x0051:
            if (r10 >= r9) goto L_0x013a
            r11 = r8[r10]
            java.lang.String r12 = r11.getName()
            java.lang.String r13 = "onEvent"
            boolean r13 = r12.startsWith(r13)
            if (r13 == 0) goto L_0x012e
            int r13 = r11.getModifiers()
            r14 = r13 & 1
            if (r14 == 0) goto L_0x0102
            r14 = r13 & 5192(0x1448, float:7.276E-42)
            if (r14 != 0) goto L_0x0102
            java.lang.Class[] r14 = r11.getParameterTypes()
            int r15 = r14.length
            r16 = r0
            r0 = 1
            if (r15 != r0) goto L_0x00ff
            java.lang.String r0 = "onEvent"
            int r0 = r0.length()
            java.lang.String r0 = r12.substring(r0)
            int r15 = r0.length()
            if (r15 != 0) goto L_0x008a
            de.greenrobot.event.ThreadMode r15 = de.greenrobot.event.ThreadMode.PostThread
            goto L_0x00aa
        L_0x008a:
            java.lang.String r15 = "MainThread"
            boolean r15 = r0.equals(r15)
            if (r15 == 0) goto L_0x0095
            de.greenrobot.event.ThreadMode r15 = de.greenrobot.event.ThreadMode.MainThread
            goto L_0x00aa
        L_0x0095:
            java.lang.String r15 = "BackgroundThread"
            boolean r15 = r0.equals(r15)
            if (r15 == 0) goto L_0x00a0
            de.greenrobot.event.ThreadMode r15 = de.greenrobot.event.ThreadMode.BackgroundThread
            goto L_0x00aa
        L_0x00a0:
            java.lang.String r15 = "Async"
            boolean r15 = r0.equals(r15)
            if (r15 == 0) goto L_0x00db
            de.greenrobot.event.ThreadMode r15 = de.greenrobot.event.ThreadMode.Async
        L_0x00aa:
            r17 = r0
            r0 = 0
            r18 = r7
            r7 = r14[r0]
            r5.setLength(r0)
            r5.append(r12)
            r0 = 62
            r5.append(r0)
            java.lang.String r0 = r7.getName()
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            boolean r19 = r4.add(r0)
            if (r19 == 0) goto L_0x00d8
            r19 = r0
            de.greenrobot.event.SubscriberMethod r0 = new de.greenrobot.event.SubscriberMethod
            r0.<init>(r11, r15, r7)
            r3.add(r0)
            goto L_0x012d
        L_0x00d8:
            r19 = r0
            goto L_0x012d
        L_0x00db:
            r17 = r0
            r18 = r7
            java.util.Map<java.lang.Class<?>, java.lang.Class<?>> r0 = r1.skipMethodVerificationForClasses
            boolean r0 = r0.containsKey(r6)
            if (r0 == 0) goto L_0x00e8
            goto L_0x0132
        L_0x00e8:
            de.greenrobot.event.EventBusException r0 = new de.greenrobot.event.EventBusException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r15 = "Illegal onEvent method, check for typos: "
            r7.append(r15)
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            r0.<init>((java.lang.String) r7)
            throw r0
        L_0x00ff:
            r18 = r7
            goto L_0x012d
        L_0x0102:
            r16 = r0
            r18 = r7
            java.util.Map<java.lang.Class<?>, java.lang.Class<?>> r0 = r1.skipMethodVerificationForClasses
            boolean r0 = r0.containsKey(r6)
            if (r0 != 0) goto L_0x012d
            java.lang.String r0 = de.greenrobot.event.EventBus.TAG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r14 = "Skipping method (not public, static or abstract): "
            r7.append(r14)
            r7.append(r6)
            java.lang.String r14 = "."
            r7.append(r14)
            r7.append(r12)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r0, r7)
            goto L_0x0132
        L_0x012d:
            goto L_0x0132
        L_0x012e:
            r16 = r0
            r18 = r7
        L_0x0132:
            int r10 = r10 + 1
            r0 = r16
            r7 = r18
            goto L_0x0051
        L_0x013a:
            r16 = r0
            r18 = r7
            java.lang.Class r6 = r6.getSuperclass()
            goto L_0x002a
        L_0x0144:
            r16 = r0
        L_0x0146:
            boolean r0 = r3.isEmpty()
            if (r0 != 0) goto L_0x0159
            java.util.Map<java.lang.String, java.util.List<de.greenrobot.event.SubscriberMethod>> r7 = methodCache
            monitor-enter(r7)
            java.util.Map<java.lang.String, java.util.List<de.greenrobot.event.SubscriberMethod>> r0 = methodCache     // Catch:{ all -> 0x0156 }
            r0.put(r2, r3)     // Catch:{ all -> 0x0156 }
            monitor-exit(r7)     // Catch:{ all -> 0x0156 }
            return r3
        L_0x0156:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0156 }
            throw r0
        L_0x0159:
            de.greenrobot.event.EventBusException r0 = new de.greenrobot.event.EventBusException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Subscriber "
            r7.append(r8)
            r8 = r21
            r7.append(r8)
            java.lang.String r9 = " has no public methods called "
            r7.append(r9)
            java.lang.String r9 = "onEvent"
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r0.<init>((java.lang.String) r7)
            throw r0
        L_0x017c:
            r0 = move-exception
            r8 = r21
            goto L_0x0183
        L_0x0180:
            r0 = move-exception
            r8 = r21
        L_0x0183:
            monitor-exit(r3)     // Catch:{ all -> 0x0185 }
            throw r0
        L_0x0185:
            r0 = move-exception
            goto L_0x0183
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.SubscriberMethodFinder.findSubscriberMethods(java.lang.Class):java.util.List");
    }

    static void clearCaches() {
        synchronized (methodCache) {
            methodCache.clear();
        }
    }
}
