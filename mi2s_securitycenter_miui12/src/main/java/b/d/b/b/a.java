package b.d.b.b;

import java.util.concurrent.Callable;

class a implements Callable<V> {
    a() {
    }

    public V call() {
        throw new IllegalStateException("this should never be called");
    }
}
