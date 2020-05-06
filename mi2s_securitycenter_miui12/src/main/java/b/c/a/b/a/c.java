package b.c.a.b.a;

import java.io.FilterInputStream;
import java.io.InputStream;

public class c extends FilterInputStream {
    public c(InputStream inputStream) {
        super(inputStream);
    }

    public long skip(long j) {
        long j2 = 0;
        while (j2 < j) {
            long skip = this.in.skip(j - j2);
            if (skip == 0) {
                if (read() < 0) {
                    break;
                }
                skip = 1;
            }
            j2 += skip;
        }
        return j2;
    }
}
