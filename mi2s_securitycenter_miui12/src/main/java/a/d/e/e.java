package a.d.e;

import androidx.annotation.NonNull;

public class e<T> implements d<T> {

    /* renamed from: a  reason: collision with root package name */
    private final Object[] f135a;

    /* renamed from: b  reason: collision with root package name */
    private int f136b;

    public e(int i) {
        if (i > 0) {
            this.f135a = new Object[i];
            return;
        }
        throw new IllegalArgumentException("The max pool size must be > 0");
    }

    private boolean a(@NonNull T t) {
        for (int i = 0; i < this.f136b; i++) {
            if (this.f135a[i] == t) {
                return true;
            }
        }
        return false;
    }

    public T acquire() {
        int i = this.f136b;
        if (i <= 0) {
            return null;
        }
        int i2 = i - 1;
        T[] tArr = this.f135a;
        T t = tArr[i2];
        tArr[i2] = null;
        this.f136b = i - 1;
        return t;
    }

    public boolean release(@NonNull T t) {
        if (!a(t)) {
            int i = this.f136b;
            Object[] objArr = this.f135a;
            if (i >= objArr.length) {
                return false;
            }
            objArr[i] = t;
            this.f136b = i + 1;
            return true;
        }
        throw new IllegalStateException("Already in the pool!");
    }
}
