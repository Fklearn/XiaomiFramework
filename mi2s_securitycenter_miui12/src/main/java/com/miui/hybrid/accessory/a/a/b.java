package com.miui.hybrid.accessory.a.a;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static String f5477a;

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
        r2 = com.miui.hybrid.accessory.a.d.a.a(r2, "getMiuiDeviceId", new java.lang.Object[0]);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(android.content.Context r6) {
        /*
            java.lang.String r0 = "DeviceInfo"
            java.lang.String r1 = f5477a
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x000d
            java.lang.String r6 = f5477a
            return r6
        L_0x000d:
            r1 = 0
            java.lang.String r2 = "miui.telephony.TelephonyManager"
            java.lang.String r3 = "getDefault"
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0055 }
            java.lang.Object r2 = com.miui.hybrid.accessory.a.d.a.a((java.lang.String) r2, (java.lang.String) r3, (java.lang.Object[]) r5)     // Catch:{ Exception -> 0x0055 }
            if (r2 == 0) goto L_0x0032
            java.lang.String r3 = "getMiuiDeviceId"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0055 }
            java.lang.Object r2 = com.miui.hybrid.accessory.a.d.a.a((java.lang.Object) r2, (java.lang.String) r3, (java.lang.Object[]) r4)     // Catch:{ Exception -> 0x0055 }
            if (r2 == 0) goto L_0x0032
            boolean r3 = r2 instanceof java.lang.String     // Catch:{ Exception -> 0x0055 }
            if (r3 == 0) goto L_0x0032
            java.lang.Class<java.lang.String> r3 = java.lang.String.class
            java.lang.Object r2 = r3.cast(r2)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0055 }
            goto L_0x0033
        L_0x0032:
            r2 = r1
        L_0x0033:
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0055 }
            if (r3 == 0) goto L_0x004c
            java.lang.String r3 = "phone"
            java.lang.Object r6 = r6.getSystemService(r3)     // Catch:{ SecurityException -> 0x0046 }
            android.telephony.TelephonyManager r6 = (android.telephony.TelephonyManager) r6     // Catch:{ SecurityException -> 0x0046 }
            java.lang.String r2 = r6.getDeviceId()     // Catch:{ SecurityException -> 0x0046 }
            goto L_0x004c
        L_0x0046:
            r6 = move-exception
            java.lang.String r3 = "Fail to read device id"
            com.miui.hybrid.accessory.a.b.a.b(r0, r3, r6)     // Catch:{ Exception -> 0x0055 }
        L_0x004c:
            boolean r6 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0055 }
            if (r6 != 0) goto L_0x0054
            f5477a = r2     // Catch:{ Exception -> 0x0055 }
        L_0x0054:
            return r2
        L_0x0055:
            r6 = move-exception
            java.lang.String r2 = "Fail to get imei"
            com.miui.hybrid.accessory.a.b.a.b(r0, r2, r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.a.b.a(android.content.Context):java.lang.String");
    }
}
