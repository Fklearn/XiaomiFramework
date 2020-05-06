package miui.cloud.common;

public class XWrapper<T> {
    private T mObj;

    public T get() {
        return this.mObj;
    }

    public void set(T t) {
        this.mObj = t;
    }
}
