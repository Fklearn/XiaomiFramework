package com.miui.maml.animation.interpolater;

public class InterpolatorFactory {
    public static final String LOG_TAG = "InterpolatorFactory";

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.view.animation.Interpolator create(java.lang.String r10, com.miui.maml.data.Expression[] r11) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r10)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            r0 = 0
            r2 = 1
            if (r11 == 0) goto L_0x0011
            int r3 = r11.length
            if (r3 <= 0) goto L_0x0011
            r3 = r2
            goto L_0x0012
        L_0x0011:
            r3 = r0
        L_0x0012:
            r4 = 40
            int r4 = r10.indexOf(r4)
            r5 = 41
            int r5 = r10.indexOf(r5)
            r6 = 0
            r7 = -1
            if (r4 == r7) goto L_0x0065
            if (r5 == r7) goto L_0x0065
            int r4 = r4 + r2
            java.lang.String r4 = r10.substring(r4, r5)
            java.lang.String r5 = ","
            int r5 = r4.indexOf(r5)
            if (r5 == r7) goto L_0x003d
            java.lang.String r0 = r4.substring(r0, r5)
            int r5 = r5 + r2
            java.lang.String r5 = r4.substring(r5)
            r7 = r5
            r5 = r2
            goto L_0x0042
        L_0x003d:
            java.lang.String r5 = ""
            r7 = r5
            r5 = r0
            r0 = r4
        L_0x0042:
            float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ NumberFormatException -> 0x004d }
            if (r5 == 0) goto L_0x0068
            float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ NumberFormatException -> 0x004e }
            goto L_0x0068
        L_0x004d:
            r0 = r6
        L_0x004e:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "parse error:"
            r7.append(r8)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            java.lang.String r7 = "InterpolatorFactory"
            android.util.Log.d(r7, r4)
            goto L_0x0068
        L_0x0065:
            r2 = r0
            r5 = r2
            r0 = r6
        L_0x0068:
            java.lang.String r4 = "BackEaseIn"
            boolean r7 = r4.equalsIgnoreCase(r10)
            if (r7 == 0) goto L_0x0076
            com.miui.maml.animation.interpolater.BackEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x0076:
            java.lang.String r7 = "BackEaseOut"
            boolean r8 = r7.equalsIgnoreCase(r10)
            if (r8 == 0) goto L_0x0084
            com.miui.maml.animation.interpolater.BackEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseOutInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x0084:
            java.lang.String r8 = "BackEaseInOut"
            boolean r9 = r8.equalsIgnoreCase(r10)
            if (r9 == 0) goto L_0x0092
            com.miui.maml.animation.interpolater.BackEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInOutInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x0092:
            boolean r8 = r10.startsWith(r8)
            if (r8 == 0) goto L_0x00a8
            if (r3 == 0) goto L_0x00a0
            com.miui.maml.animation.interpolater.BackEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInOutInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x00a0:
            if (r2 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.BackEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInOutInterpolater
            r10.<init>((float) r0)
            return r10
        L_0x00a8:
            boolean r4 = r10.startsWith(r4)
            if (r4 == 0) goto L_0x00be
            if (r3 == 0) goto L_0x00b6
            com.miui.maml.animation.interpolater.BackEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x00b6:
            if (r2 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.BackEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseInInterpolater
            r10.<init>((float) r0)
            return r10
        L_0x00be:
            boolean r4 = r10.startsWith(r7)
            if (r4 == 0) goto L_0x00d4
            if (r3 == 0) goto L_0x00cc
            com.miui.maml.animation.interpolater.BackEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseOutInterpolater
            r10.<init>((com.miui.maml.data.Expression[]) r11)
            return r10
        L_0x00cc:
            if (r2 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.BackEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.BackEaseOutInterpolater
            r10.<init>((float) r0)
            return r10
        L_0x00d4:
            java.lang.String r2 = "BounceEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x00e2
            com.miui.maml.animation.interpolater.BounceEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.BounceEaseInInterpolater
            r10.<init>()
            return r10
        L_0x00e2:
            java.lang.String r2 = "BounceEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x00f0
            com.miui.maml.animation.interpolater.BounceEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.BounceEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x00f0:
            java.lang.String r2 = "BounceEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x00fe
            com.miui.maml.animation.interpolater.BounceEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.BounceEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x00fe:
            java.lang.String r2 = "CircEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x010c
            com.miui.maml.animation.interpolater.CircEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.CircEaseInInterpolater
            r10.<init>()
            return r10
        L_0x010c:
            java.lang.String r2 = "CircEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x011a
            com.miui.maml.animation.interpolater.CircEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.CircEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x011a:
            java.lang.String r2 = "CircEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0128
            com.miui.maml.animation.interpolater.CircEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.CircEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x0128:
            java.lang.String r2 = "CubicEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0136
            com.miui.maml.animation.interpolater.CubicEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.CubicEaseInInterpolater
            r10.<init>()
            return r10
        L_0x0136:
            java.lang.String r2 = "CubicEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0144
            com.miui.maml.animation.interpolater.CubicEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.CubicEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x0144:
            java.lang.String r2 = "CubicEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0152
            com.miui.maml.animation.interpolater.CubicEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.CubicEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x0152:
            java.lang.String r2 = "ElasticEaseIn"
            boolean r4 = r2.equalsIgnoreCase(r10)
            if (r4 == 0) goto L_0x0160
            com.miui.maml.animation.interpolater.ElasticEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInInterpolater
            r10.<init>(r11)
            return r10
        L_0x0160:
            java.lang.String r4 = "ElasticEaseOut"
            boolean r7 = r4.equalsIgnoreCase(r10)
            if (r7 == 0) goto L_0x016e
            com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater
            r10.<init>(r11)
            return r10
        L_0x016e:
            java.lang.String r7 = "ElasticEaseInOut"
            boolean r8 = r7.equalsIgnoreCase(r10)
            if (r8 == 0) goto L_0x017c
            com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater
            r10.<init>(r11)
            return r10
        L_0x017c:
            boolean r7 = r10.startsWith(r7)
            if (r7 == 0) goto L_0x0192
            if (r3 == 0) goto L_0x018a
            com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater
            r10.<init>(r11)
            return r10
        L_0x018a:
            if (r5 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInOutInterpolater
            r10.<init>(r0, r6)
            return r10
        L_0x0192:
            boolean r2 = r10.startsWith(r2)
            if (r2 == 0) goto L_0x01a8
            if (r3 == 0) goto L_0x01a0
            com.miui.maml.animation.interpolater.ElasticEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInInterpolater
            r10.<init>(r11)
            return r10
        L_0x01a0:
            if (r5 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.ElasticEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseInInterpolater
            r10.<init>(r0, r6)
            return r10
        L_0x01a8:
            boolean r2 = r10.startsWith(r4)
            if (r2 == 0) goto L_0x01be
            if (r3 == 0) goto L_0x01b6
            com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater
            r10.<init>(r11)
            return r10
        L_0x01b6:
            if (r5 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.ElasticEaseOutInterpolater
            r10.<init>(r0, r6)
            return r10
        L_0x01be:
            java.lang.String r2 = "ExpoEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x01cc
            com.miui.maml.animation.interpolater.ExpoEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.ExpoEaseInInterpolater
            r10.<init>()
            return r10
        L_0x01cc:
            java.lang.String r2 = "ExpoEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x01da
            com.miui.maml.animation.interpolater.ExpoEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.ExpoEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x01da:
            java.lang.String r2 = "ExpoEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x01e8
            com.miui.maml.animation.interpolater.ExpoEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.ExpoEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x01e8:
            java.lang.String r2 = "QuadEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x01f6
            com.miui.maml.animation.interpolater.QuadEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.QuadEaseInInterpolater
            r10.<init>()
            return r10
        L_0x01f6:
            java.lang.String r2 = "QuadEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0204
            com.miui.maml.animation.interpolater.QuadEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuadEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x0204:
            java.lang.String r2 = "QuadEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0212
            com.miui.maml.animation.interpolater.QuadEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuadEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x0212:
            java.lang.String r2 = "QuartEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0220
            com.miui.maml.animation.interpolater.QuartEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.QuartEaseInInterpolater
            r10.<init>()
            return r10
        L_0x0220:
            java.lang.String r2 = "QuartEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x022e
            com.miui.maml.animation.interpolater.QuartEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuartEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x022e:
            java.lang.String r2 = "QuartEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x023c
            com.miui.maml.animation.interpolater.QuartEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuartEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x023c:
            java.lang.String r2 = "QuintEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x024a
            com.miui.maml.animation.interpolater.QuintEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.QuintEaseInInterpolater
            r10.<init>()
            return r10
        L_0x024a:
            java.lang.String r2 = "QuintEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0258
            com.miui.maml.animation.interpolater.QuintEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuintEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x0258:
            java.lang.String r2 = "QuintEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0266
            com.miui.maml.animation.interpolater.QuintEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.QuintEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x0266:
            java.lang.String r2 = "SineEaseIn"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0274
            com.miui.maml.animation.interpolater.SineEaseInInterpolater r10 = new com.miui.maml.animation.interpolater.SineEaseInInterpolater
            r10.<init>()
            return r10
        L_0x0274:
            java.lang.String r2 = "SineEaseOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0282
            com.miui.maml.animation.interpolater.SineEaseOutInterpolater r10 = new com.miui.maml.animation.interpolater.SineEaseOutInterpolater
            r10.<init>()
            return r10
        L_0x0282:
            java.lang.String r2 = "SineEaseInOut"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x0290
            com.miui.maml.animation.interpolater.SineEaseInOutInterpolater r10 = new com.miui.maml.animation.interpolater.SineEaseInOutInterpolater
            r10.<init>()
            return r10
        L_0x0290:
            java.lang.String r2 = "Linear"
            boolean r2 = r2.equalsIgnoreCase(r10)
            if (r2 == 0) goto L_0x029e
            com.miui.maml.animation.interpolater.LinearInterpolater r10 = new com.miui.maml.animation.interpolater.LinearInterpolater
            r10.<init>()
            return r10
        L_0x029e:
            java.lang.String r2 = "PhysicBased"
            boolean r4 = r2.equalsIgnoreCase(r10)
            if (r4 == 0) goto L_0x02ac
            com.miui.maml.animation.interpolater.PhysicBasedInterpolator r10 = new com.miui.maml.animation.interpolater.PhysicBasedInterpolator
            r10.<init>(r11)
            return r10
        L_0x02ac:
            boolean r10 = r10.startsWith(r2)
            if (r10 == 0) goto L_0x02c2
            if (r3 == 0) goto L_0x02ba
            com.miui.maml.animation.interpolater.PhysicBasedInterpolator r10 = new com.miui.maml.animation.interpolater.PhysicBasedInterpolator
            r10.<init>(r11)
            return r10
        L_0x02ba:
            if (r5 == 0) goto L_0x02c2
            com.miui.maml.animation.interpolater.PhysicBasedInterpolator r10 = new com.miui.maml.animation.interpolater.PhysicBasedInterpolator
            r10.<init>(r0, r6)
            return r10
        L_0x02c2:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.animation.interpolater.InterpolatorFactory.create(java.lang.String, com.miui.maml.data.Expression[]):android.view.animation.Interpolator");
    }
}
