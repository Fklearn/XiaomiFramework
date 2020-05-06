package miui.cloud.common;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;

public class IOUtil {
    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException unused) {
            XLogger.loge("Closing failed. ");
        }
    }

    public static void realFlush(Flushable flushable) {
        flushable.flush();
        if (flushable instanceof FileOutputStream) {
            ((FileOutputStream) flushable).getFD().sync();
        }
    }

    public static void realFlushQuietly(Flushable flushable) {
        try {
            realFlush(flushable);
        } catch (IOException unused) {
            XLogger.log("Flush failed. ");
        }
    }
}
