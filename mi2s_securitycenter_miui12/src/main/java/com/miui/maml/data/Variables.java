package com.miui.maml.data;

import android.util.Log;
import com.miui.maml.util.Utils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Variables {
    /* access modifiers changed from: private */
    public static boolean DBG = false;
    private static final String LOG_TAG = "Variables";
    public static final int MAX_ARRAY_SIZE = 10000;
    private DoubleBucket mDoubleBucket = new DoubleBucket();
    private VarBucket<Object> mObjectBucket = new VarBucket<>();

    private static abstract class BaseVarBucket {
        private HashMap<String, Integer> mIndices;
        private int mNextIndex;

        private BaseVarBucket() {
            this.mIndices = new HashMap<>();
            this.mNextIndex = 0;
        }

        public boolean exists(String str) {
            return this.mIndices.containsKey(str);
        }

        /* access modifiers changed from: protected */
        public abstract void onAddItem(int i);

        public synchronized int registerVariable(String str) {
            Integer num;
            num = this.mIndices.get(str);
            if (num == null) {
                num = Integer.valueOf(this.mNextIndex);
                this.mIndices.put(str, num);
                onAddItem(this.mNextIndex);
            }
            if (num.intValue() == this.mNextIndex) {
                this.mNextIndex++;
            }
            if (Variables.DBG) {
                Log.d(Variables.LOG_TAG, "registerVariable: " + str + "  index:" + num);
            }
            return num.intValue();
        }
    }

    private static class DoubleBucket extends BaseVarBucket {
        private ArrayList<DoubleInfo> mArray;

        private DoubleBucket() {
            super();
            this.mArray = new ArrayList<>();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0014, code lost:
            return false;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final synchronized boolean exists(int r3) {
            /*
                r2 = this;
                monitor-enter(r2)
                r0 = 0
                if (r3 >= 0) goto L_0x0005
                goto L_0x000e
            L_0x0005:
                java.util.ArrayList<com.miui.maml.data.Variables$DoubleInfo> r1 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0013, all -> 0x0010 }
                java.lang.Object r3 = r1.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0013, all -> 0x0010 }
                if (r3 == 0) goto L_0x000e
                r0 = 1
            L_0x000e:
                monitor-exit(r2)
                return r0
            L_0x0010:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0013:
                monitor-exit(r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.DoubleBucket.exists(int):boolean");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0016, code lost:
            return 0.0d;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized double get(int r4) {
            /*
                r3 = this;
                monitor-enter(r3)
                r0 = 0
                java.util.ArrayList<com.miui.maml.data.Variables$DoubleInfo> r2 = r3.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0015, all -> 0x0012 }
                java.lang.Object r4 = r2.get(r4)     // Catch:{ IndexOutOfBoundsException -> 0x0015, all -> 0x0012 }
                com.miui.maml.data.Variables$DoubleInfo r4 = (com.miui.maml.data.Variables.DoubleInfo) r4     // Catch:{ IndexOutOfBoundsException -> 0x0015, all -> 0x0012 }
                if (r4 != 0) goto L_0x000e
                goto L_0x0010
            L_0x000e:
                double r0 = r4.mValue     // Catch:{ IndexOutOfBoundsException -> 0x0015, all -> 0x0012 }
            L_0x0010:
                monitor-exit(r3)
                return r0
            L_0x0012:
                r4 = move-exception
                monitor-exit(r3)
                throw r4
            L_0x0015:
                monitor-exit(r3)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.DoubleBucket.get(int):double");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0015, code lost:
            return -1;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized int getVer(int r3) {
            /*
                r2 = this;
                monitor-enter(r2)
                r0 = -1
                java.util.ArrayList<com.miui.maml.data.Variables$DoubleInfo> r1 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                java.lang.Object r3 = r1.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                com.miui.maml.data.Variables$DoubleInfo r3 = (com.miui.maml.data.Variables.DoubleInfo) r3     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                if (r3 != 0) goto L_0x000d
                goto L_0x000f
            L_0x000d:
                int r0 = r3.mVersion     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
            L_0x000f:
                monitor-exit(r2)
                return r0
            L_0x0011:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0014:
                monitor-exit(r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.DoubleBucket.getVer(int):int");
        }

        /* access modifiers changed from: protected */
        public void onAddItem(int i) {
            while (this.mArray.size() <= i) {
                this.mArray.add((Object) null);
            }
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final synchronized void put(int r3, double r4) {
            /*
                r2 = this;
                monitor-enter(r2)
                if (r3 >= 0) goto L_0x0005
                monitor-exit(r2)
                return
            L_0x0005:
                java.util.ArrayList<com.miui.maml.data.Variables$DoubleInfo> r0 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                java.lang.Object r0 = r0.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                com.miui.maml.data.Variables$DoubleInfo r0 = (com.miui.maml.data.Variables.DoubleInfo) r0     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                if (r0 != 0) goto L_0x001b
                com.miui.maml.data.Variables$DoubleInfo r0 = new com.miui.maml.data.Variables$DoubleInfo     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                r1 = 0
                r0.<init>(r4, r1)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                java.util.ArrayList<com.miui.maml.data.Variables$DoubleInfo> r4 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                r4.set(r3, r0)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                goto L_0x0022
            L_0x001b:
                r0.setValue(r4)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                goto L_0x0022
            L_0x001f:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0022:
                monitor-exit(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.DoubleBucket.put(int, double):void");
        }

        public void reset() {
            int size = this.mArray.size();
            for (int i = 0; i < size; i++) {
                DoubleInfo doubleInfo = this.mArray.get(i);
                if (doubleInfo != null) {
                    doubleInfo.setValue(0.0d);
                }
            }
        }
    }

    private static class DoubleInfo {
        double mValue;
        int mVersion;

        public DoubleInfo(double d2, int i) {
            this.mValue = d2;
            this.mVersion = i;
        }

        public void setValue(double d2) {
            this.mValue = d2;
            this.mVersion++;
        }
    }

    private static class ValueInfo<T> {
        T mValue;
        int mVersion;

        public ValueInfo(T t, int i) {
            this.mValue = t;
            this.mVersion = i;
        }

        public void reset() {
            T t = this.mValue;
            int i = 0;
            if (t instanceof double[]) {
                double[] dArr = (double[]) t;
                while (i < dArr.length) {
                    dArr[i] = 0.0d;
                    i++;
                }
            } else if (t instanceof float[]) {
                float[] fArr = (float[]) t;
                while (i < fArr.length) {
                    fArr[i] = 0.0f;
                    i++;
                }
            } else if (t instanceof int[]) {
                int[] iArr = (int[]) t;
                for (int i2 = 0; i2 < iArr.length; i2++) {
                    iArr[i2] = 0;
                }
            } else if (t instanceof Object[]) {
                Object[] objArr = (Object[]) t;
                while (i < objArr.length) {
                    objArr[i] = null;
                    i++;
                }
            } else {
                setValue((Object) null);
            }
        }

        public void setValue(T t) {
            this.mValue = t;
            this.mVersion++;
        }
    }

    private static class VarBucket<T> extends BaseVarBucket {
        private ArrayList<ValueInfo<T>> mArray;

        private VarBucket() {
            super();
            this.mArray = new ArrayList<>();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0015, code lost:
            return null;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized T get(int r3) {
            /*
                r2 = this;
                monitor-enter(r2)
                r0 = 0
                java.util.ArrayList<com.miui.maml.data.Variables$ValueInfo<T>> r1 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                java.lang.Object r3 = r1.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                com.miui.maml.data.Variables$ValueInfo r3 = (com.miui.maml.data.Variables.ValueInfo) r3     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                if (r3 != 0) goto L_0x000d
                goto L_0x000f
            L_0x000d:
                T r0 = r3.mValue     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
            L_0x000f:
                monitor-exit(r2)
                return r0
            L_0x0011:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0014:
                monitor-exit(r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.VarBucket.get(int):java.lang.Object");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0015, code lost:
            return -1;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized int getVer(int r3) {
            /*
                r2 = this;
                monitor-enter(r2)
                r0 = -1
                java.util.ArrayList<com.miui.maml.data.Variables$ValueInfo<T>> r1 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                java.lang.Object r3 = r1.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                com.miui.maml.data.Variables$ValueInfo r3 = (com.miui.maml.data.Variables.ValueInfo) r3     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
                if (r3 != 0) goto L_0x000d
                goto L_0x000f
            L_0x000d:
                int r0 = r3.mVersion     // Catch:{ IndexOutOfBoundsException -> 0x0014, all -> 0x0011 }
            L_0x000f:
                monitor-exit(r2)
                return r0
            L_0x0011:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0014:
                monitor-exit(r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.VarBucket.getVer(int):int");
        }

        /* access modifiers changed from: protected */
        public void onAddItem(int i) {
            while (this.mArray.size() <= i) {
                this.mArray.add((Object) null);
            }
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final synchronized void put(int r3, T r4) {
            /*
                r2 = this;
                monitor-enter(r2)
                if (r3 >= 0) goto L_0x0005
                monitor-exit(r2)
                return
            L_0x0005:
                java.util.ArrayList<com.miui.maml.data.Variables$ValueInfo<T>> r0 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                java.lang.Object r0 = r0.get(r3)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                com.miui.maml.data.Variables$ValueInfo r0 = (com.miui.maml.data.Variables.ValueInfo) r0     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                if (r0 != 0) goto L_0x001b
                com.miui.maml.data.Variables$ValueInfo r0 = new com.miui.maml.data.Variables$ValueInfo     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                r1 = 0
                r0.<init>(r4, r1)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                java.util.ArrayList<com.miui.maml.data.Variables$ValueInfo<T>> r4 = r2.mArray     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                r4.set(r3, r0)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                goto L_0x0022
            L_0x001b:
                r0.setValue(r4)     // Catch:{ IndexOutOfBoundsException -> 0x0022, all -> 0x001f }
                goto L_0x0022
            L_0x001f:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            L_0x0022:
                monitor-exit(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Variables.VarBucket.put(int, java.lang.Object):void");
        }

        public void reset() {
            int size = this.mArray.size();
            for (int i = 0; i < size; i++) {
                ValueInfo valueInfo = this.mArray.get(i);
                if (valueInfo != null) {
                    valueInfo.reset();
                }
            }
        }
    }

    private static void dbglog(String str) {
        if (DBG) {
            Log.d(LOG_TAG, str);
        }
    }

    private <T> T getArrInner(int i, int i2) {
        String str;
        try {
            T[] tArr = (Object[]) get(i);
            if (tArr != null) {
                return tArr[i2];
            }
            dbglog("getArrInner: designated object is not an array. index:" + i);
            return null;
        } catch (ClassCastException unused) {
            str = "getArrInner: designated object type is not correct. index:" + i;
            dbglog(str);
            return null;
        } catch (IndexOutOfBoundsException unused2) {
            str = "getArrInner: designated index is invalid. index:" + i + " arrIndex:" + i2;
            dbglog(str);
            return null;
        }
    }

    public boolean createArray(String str, int i, Class<?> cls) {
        if (cls == null || i <= 0 || i > 10000) {
            Log.e(LOG_TAG, "createArray failed: name= " + str + "  size=" + i);
            return false;
        }
        int registerVariable = registerVariable(str);
        if (get(registerVariable) == null) {
            try {
                put(registerVariable, Array.newInstance(cls, i));
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public boolean existsArrItem(int i, int i2) {
        Object obj = get(i);
        if (obj == null || i2 < 0) {
            return false;
        }
        try {
            return i2 < Array.getLength(obj);
        } catch (RuntimeException unused) {
            return false;
        }
    }

    public boolean existsDouble(int i) {
        return this.mDoubleBucket.exists(i);
    }

    public boolean existsDouble(String str) {
        return this.mDoubleBucket.exists(str);
    }

    public boolean existsObj(String str) {
        return this.mObjectBucket.exists(str);
    }

    public Object get(int i) {
        return this.mObjectBucket.get(i);
    }

    public Object get(String str) {
        return get(registerVariable(str));
    }

    public Object getArr(int i, int i2) {
        return getArrInner(i, i2);
    }

    public double getArrDouble(int i, int i2) {
        try {
            Object obj = get(i);
            if (obj != null) {
                return obj instanceof boolean[] ? ((boolean[]) obj)[i] ? 1.0d : 0.0d : Array.getDouble(obj, i2);
            }
            dbglog("getArrDouble: designated array does not exist. index:" + i);
            return 0.0d;
        } catch (Exception unused) {
            dbglog("getArrDouble: designated index is invalid. index:" + i + " arrIndex:" + i2);
        }
    }

    public String getArrString(int i, int i2) {
        return (String) getArrInner(i, i2);
    }

    public double getDouble(int i) {
        return this.mDoubleBucket.get(i);
    }

    public double getDouble(String str) {
        return getDouble(registerDoubleVariable(str));
    }

    public String getString(int i) {
        try {
            return (String) get(i);
        } catch (ClassCastException unused) {
            return null;
        }
    }

    public String getString(String str) {
        return getString(registerVariable(str));
    }

    public int getVer(int i, boolean z) {
        return z ? this.mDoubleBucket.getVer(i) : this.mObjectBucket.getVer(i);
    }

    public final void put(int i, double d2) {
        this.mDoubleBucket.put(i, d2);
    }

    public final void put(int i, Object obj) {
        this.mObjectBucket.put(i, obj);
    }

    public final void put(String str, double d2) {
        put(registerDoubleVariable(str), d2);
    }

    public void put(String str, Object obj) {
        put(registerVariable(str), obj);
    }

    public boolean putArr(int i, int i2, double d2) {
        try {
            Object obj = get(i);
            if (obj == null) {
                dbglog("putArr: designated array does not exist. index:" + i);
                return false;
            }
            if (obj instanceof double[]) {
                ((double[]) obj)[i2] = d2;
            } else if (obj instanceof byte[]) {
                ((byte[]) obj)[i2] = (byte) ((int) ((long) d2));
            } else if (obj instanceof char[]) {
                ((char[]) obj)[i2] = (char) ((int) ((long) d2));
            } else if (obj instanceof float[]) {
                ((float[]) obj)[i2] = (float) d2;
            } else if (obj instanceof int[]) {
                ((int[]) obj)[i2] = (int) ((long) d2);
            } else if (obj instanceof long[]) {
                ((long[]) obj)[i2] = (long) d2;
            } else if (obj instanceof short[]) {
                ((short[]) obj)[i2] = (short) ((int) ((long) d2));
            } else if (obj instanceof boolean[]) {
                ((boolean[]) obj)[i2] = d2 > 0.0d;
            }
            put(i, obj);
            return true;
        } catch (Exception e) {
            dbglog("putArr: failed. index:" + i + " arrIndex:" + i2 + "\n" + e.toString());
            return false;
        }
    }

    public boolean putArr(int i, int i2, Object obj) {
        String str;
        try {
            Object[] objArr = (Object[]) get(i);
            if (objArr == null) {
                dbglog("putArr: designated array does not exist. index:" + i);
                return false;
            }
            objArr[i2] = obj;
            put(i, (Object) objArr);
            return true;
        } catch (ClassCastException unused) {
            str = "putArr: designated object is not an object array. index:" + i;
            dbglog(str);
            return false;
        } catch (IndexOutOfBoundsException unused2) {
            str = "putArr: designated array index is invalid. index:" + i + " arrIndex:" + i2;
            dbglog(str);
            return false;
        }
    }

    public boolean putArrDouble(int i, int i2, Object obj) {
        if (obj instanceof Number) {
            return putArr(i, i2, ((Number) obj).doubleValue());
        }
        if (!(obj instanceof String)) {
            return false;
        }
        try {
            return putArr(i, i2, Utils.parseDouble((String) obj));
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public final boolean putDouble(int i, Object obj) {
        if (obj instanceof Number) {
            put(i, ((Number) obj).doubleValue());
            return true;
        } else if (obj instanceof Boolean) {
            put(i, ((Boolean) obj).booleanValue() ? 1.0d : 0.0d);
            return true;
        } else if (!(obj instanceof String)) {
            return false;
        } else {
            try {
                put(i, Double.parseDouble((String) obj));
                return true;
            } catch (NumberFormatException unused) {
                return false;
            }
        }
    }

    @Deprecated
    public final void putNum(String str, double d2) {
        put(str, d2);
    }

    public int registerDoubleVariable(String str) {
        return this.mDoubleBucket.registerVariable(str);
    }

    public int registerVariable(String str) {
        return this.mObjectBucket.registerVariable(str);
    }

    public void reset() {
        this.mDoubleBucket.reset();
        this.mObjectBucket.reset();
    }
}
