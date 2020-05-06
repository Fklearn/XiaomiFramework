package com.miui.maml.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

public final class IOUtils {
    public static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:1|2|3|4|6) */
    /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0005 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void closeQuietly(java.io.OutputStream r0) {
        /*
            if (r0 == 0) goto L_0x0008
            r0.flush()     // Catch:{ IOException -> 0x0005 }
        L_0x0005:
            r0.close()     // Catch:{ IOException -> 0x0008 }
        L_0x0008:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.net.IOUtils.closeQuietly(java.io.OutputStream):void");
    }

    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException unused) {
            }
        }
    }
}
