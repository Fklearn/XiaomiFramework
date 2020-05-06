package c.a.b.a;

import android.os.SystemClock;
import java.util.concurrent.TimeoutException;

public class a {

    /* renamed from: c.a.b.a.a$a  reason: collision with other inner class name */
    public interface C0034a<ValueType> {
        ValueType doAction(long j, long j2);
    }

    public static class b extends Exception {
    }

    public static <ValueType> ValueType a(C0034a<ValueType> aVar, long j, long j2) {
        if (aVar == null || j < 0 || j2 <= 0) {
            throw new IllegalArgumentException("null == action || timeoutMillis < 0 || retryIntervalMillis <= 0");
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long j3 = 0;
        while (true) {
            long j4 = 1 + j3;
            try {
                return aVar.doAction(uptimeMillis, j3);
            } catch (b unused) {
                long uptimeMillis2 = j - (SystemClock.uptimeMillis() - uptimeMillis);
                if (uptimeMillis2 > 0) {
                    Thread.sleep(Math.min(uptimeMillis2, j2));
                    j3 = j4;
                } else {
                    throw new TimeoutException();
                }
            }
        }
    }
}
