package com.miui.networkassistant.utils;

import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class ReferenceCountUtil {
    private static final String TAG = "com.miui.networkassistant.utils.ReferenceCountUtil";
    private static ReferenceCountUtil sInstance;
    private Map<String, Integer> mRefCountMap;
    private Object mRefCountMapLocker = new Object();

    public static ReferenceCountUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ReferenceCountUtil();
        }
        return sInstance;
    }

    public int addReference(String str) {
        int intValue;
        if (!TextUtils.isEmpty(str)) {
            synchronized (this.mRefCountMapLocker) {
                if (this.mRefCountMap == null) {
                    this.mRefCountMap = new HashMap();
                }
                Integer num = this.mRefCountMap.get(str);
                if (num == null) {
                    num = 0;
                    this.mRefCountMap.put(str, num);
                }
                Integer valueOf = Integer.valueOf(num.intValue() + 1);
                this.mRefCountMap.put(str, valueOf);
                String str2 = TAG;
                Log.i(str2, "addReference: ref=" + valueOf);
                intValue = valueOf.intValue();
            }
            return intValue;
        }
        throw new NullPointerException("addReference");
    }

    public int getReference(String str) {
        if (!TextUtils.isEmpty(str)) {
            synchronized (this.mRefCountMapLocker) {
                if (this.mRefCountMap == null) {
                    return 0;
                }
                Integer num = this.mRefCountMap.get(str);
                if (num == null) {
                    return 0;
                }
                String str2 = TAG;
                Log.i(str2, "getReference: ref=" + num);
                int intValue = num.intValue();
                return intValue;
            }
        }
        throw new NullPointerException("getReference");
    }

    public int releaseReference(String str) {
        if (!TextUtils.isEmpty(str)) {
            synchronized (this.mRefCountMapLocker) {
                if (this.mRefCountMap == null) {
                    return 0;
                }
                Integer num = this.mRefCountMap.get(str);
                if (num == null) {
                    return 0;
                }
                Integer valueOf = Integer.valueOf(num.intValue() - 1);
                if (valueOf.intValue() <= 0) {
                    this.mRefCountMap.remove(str);
                } else {
                    this.mRefCountMap.put(str, valueOf);
                }
                String str2 = TAG;
                Log.i(str2, "releaseReference: ref=" + valueOf);
                int intValue = valueOf.intValue();
                return intValue;
            }
        }
        throw new NullPointerException("releaseReference");
    }
}
