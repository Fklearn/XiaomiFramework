package de.greenrobot.event.util;

import android.util.Log;
import de.greenrobot.event.EventBus;
import java.util.HashMap;
import java.util.Map;

public class ExceptionToResourceMapping {
    public final Map<Class<? extends Throwable>, Integer> throwableToMsgIdMap = new HashMap();

    public Integer mapThrowable(Throwable throwable) {
        Throwable throwableToCheck = throwable;
        int depthToGo = 20;
        do {
            Integer resId = mapThrowableFlat(throwableToCheck);
            if (resId != null) {
                return resId;
            }
            throwableToCheck = throwableToCheck.getCause();
            depthToGo--;
            if (depthToGo <= 0 || throwableToCheck == throwable) {
                Log.d(EventBus.TAG, "No specific message ressource ID found for " + throwable);
            }
        } while (throwableToCheck != null);
        Log.d(EventBus.TAG, "No specific message ressource ID found for " + throwable);
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.Integer} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Integer mapThrowableFlat(java.lang.Throwable r9) {
        /*
            r8 = this;
            java.lang.Class r0 = r9.getClass()
            java.util.Map<java.lang.Class<? extends java.lang.Throwable>, java.lang.Integer> r1 = r8.throwableToMsgIdMap
            java.lang.Object r1 = r1.get(r0)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x0042
            r2 = 0
            java.util.Map<java.lang.Class<? extends java.lang.Throwable>, java.lang.Integer> r3 = r8.throwableToMsgIdMap
            java.util.Set r3 = r3.entrySet()
            java.util.Iterator r4 = r3.iterator()
        L_0x0019:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0042
            java.lang.Object r5 = r4.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            java.lang.Object r6 = r5.getKey()
            java.lang.Class r6 = (java.lang.Class) r6
            boolean r7 = r6.isAssignableFrom(r0)
            if (r7 == 0) goto L_0x0041
            if (r2 == 0) goto L_0x0039
            boolean r7 = r2.isAssignableFrom(r6)
            if (r7 == 0) goto L_0x0041
        L_0x0039:
            r2 = r6
            java.lang.Object r7 = r5.getValue()
            r1 = r7
            java.lang.Integer r1 = (java.lang.Integer) r1
        L_0x0041:
            goto L_0x0019
        L_0x0042:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.util.ExceptionToResourceMapping.mapThrowableFlat(java.lang.Throwable):java.lang.Integer");
    }

    public ExceptionToResourceMapping addMapping(Class<? extends Throwable> clazz, int msgId) {
        this.throwableToMsgIdMap.put(clazz, Integer.valueOf(msgId));
        return this;
    }
}
