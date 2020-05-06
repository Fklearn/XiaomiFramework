package b.b.c.j;

import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class z {
    public static int a(long j) {
        return a(System.currentTimeMillis(), j, TimeZone.getDefault());
    }

    public static int a(long j, long j2, TimeZone timeZone) {
        long rawOffset = (long) (timeZone.getRawOffset() / 1000);
        return Time.getJulianDay(j, rawOffset) - Time.getJulianDay(j2, rawOffset);
    }

    public static String a(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }
}
