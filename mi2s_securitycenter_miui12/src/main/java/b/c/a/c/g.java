package b.c.a.c;

import android.content.Context;
import android.os.Environment;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import java.io.File;
import java.io.IOException;

public final class g {
    public static File a(Context context) {
        return a(context, true);
    }

    public static File a(Context context, String str) {
        File a2 = a(context);
        File file = new File(a2, str);
        return (file.exists() || file.mkdir()) ? file : a2;
    }

    public static File a(Context context, boolean z) {
        String str = "";
        try {
            str = Environment.getExternalStorageState();
        } catch (IncompatibleClassChangeError | NullPointerException unused) {
        }
        File c2 = (!z || !"mounted".equals(str) || !d(context)) ? null : c(context);
        if (c2 == null) {
            c2 = context.getCacheDir();
        }
        if (c2 != null) {
            return c2;
        }
        String str2 = "/data/data/" + context.getPackageName() + "/cache/";
        d.d("Can't define system cache directory! '%s' will be used.", str2);
        return new File(str2);
    }

    public static File b(Context context) {
        return a(context, "uil-images");
    }

    private static File c(Context context) {
        File file = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), "Android"), DataSchemeDataSource.SCHEME_DATA), context.getPackageName()), "cache");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                d.d("Unable to create external cache directory", new Object[0]);
                return null;
            }
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException unused) {
                d.c("Can't create \".nomedia\" file in application external cache directory", new Object[0]);
            }
        }
        return file;
    }

    private static boolean d(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }
}
