package b.b.c.i;

import android.util.Log;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1741a = "c";

    /* renamed from: b  reason: collision with root package name */
    private final Lock f1742b = new ReentrantLock();

    /* renamed from: c  reason: collision with root package name */
    private final Condition f1743c = this.f1742b.newCondition();

    /* renamed from: d  reason: collision with root package name */
    private int f1744d = 0;

    public void a() {
        this.f1742b.lock();
        this.f1744d++;
        this.f1742b.unlock();
    }

    public void b() {
        this.f1742b.lock();
        this.f1744d--;
        if (this.f1744d == 0) {
            this.f1743c.signalAll();
        }
        this.f1742b.unlock();
    }

    public void c() {
        this.f1742b.lock();
        while (this.f1744d > 0) {
            try {
                this.f1743c.await();
            } catch (InterruptedException unused) {
                Log.e(f1741a, "Interrupted while waiting on isChallenged");
            }
        }
        this.f1742b.unlock();
    }
}
