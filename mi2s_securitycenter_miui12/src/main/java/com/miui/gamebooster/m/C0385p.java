package com.miui.gamebooster.m;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.lang.reflect.Method;

/* renamed from: com.miui.gamebooster.m.p  reason: case insensitive filesystem */
public class C0385p {

    /* renamed from: a  reason: collision with root package name */
    private static Class<?> f4506a;

    /* renamed from: b  reason: collision with root package name */
    private static Method f4507b;

    /* renamed from: c  reason: collision with root package name */
    private static IBinder f4508c;

    public static int a() {
        String str;
        if (!b()) {
            str = "DEBUG SERVICE NOT START";
        } else {
            try {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                obtain.writeInterfaceToken("miui.whetstone.mcd");
                f4508c.transact(10, obtain, obtain2, 0);
                obtain2.readException();
                int readInt = obtain2.readInt();
                obtain.recycle();
                obtain2.recycle();
                return readInt;
            } catch (RemoteException e) {
                str = "local_dns_disable RemoteException:" + e;
            }
        }
        Log.e("GPUUtils", str);
        return -1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean b() {
        /*
            java.lang.String r0 = "GPUUtils"
            r1 = 1
            r2 = 0
            java.lang.Class<?> r3 = f4506a     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            if (r3 != 0) goto L_0x0010
            java.lang.String r3 = "android.os.ServiceManager"
            java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            f4506a = r3     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
        L_0x0010:
            java.lang.reflect.Method r3 = f4507b     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            if (r3 != 0) goto L_0x0028
            java.lang.Class<?> r3 = f4506a     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            if (r3 == 0) goto L_0x0028
            java.lang.Class<?> r3 = f4506a     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            java.lang.String r4 = "checkService"
            java.lang.Class[] r5 = new java.lang.Class[r1]     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r5[r2] = r6     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            java.lang.reflect.Method r3 = r3.getDeclaredMethod(r4, r5)     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            f4507b = r3     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
        L_0x0028:
            java.lang.reflect.Method r3 = f4507b     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            if (r3 == 0) goto L_0x006e
            java.lang.reflect.Method r3 = f4507b     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            java.lang.String r6 = "miui.whetstone.mcd"
            r5[r2] = r6     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            java.lang.Object r3 = r3.invoke(r4, r5)     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            android.os.IBinder r3 = (android.os.IBinder) r3     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            f4508c = r3     // Catch:{ ClassNotFoundException -> 0x0059, NoSuchMethodException -> 0x0050, InvocationTargetException -> 0x0047, IllegalAccessException -> 0x003e }
            goto L_0x006e
        L_0x003e:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "checkNativeService IllegalAccessException:"
            goto L_0x0061
        L_0x0047:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "checkNativeService InvocationTargetException:"
            goto L_0x0061
        L_0x0050:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "checkNativeService NoSuchMethodException:"
            goto L_0x0061
        L_0x0059:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "checkNativeService ClassNotFoundException:"
        L_0x0061:
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            android.util.Log.e(r0, r3)
        L_0x006e:
            android.os.IBinder r0 = f4508c
            if (r0 == 0) goto L_0x0073
            goto L_0x0074
        L_0x0073:
            r1 = r2
        L_0x0074:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.C0385p.b():boolean");
    }
}
