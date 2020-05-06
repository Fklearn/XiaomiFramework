package b.b.c.j;

import android.text.TextUtils;
import java.io.File;

public class k {
    public static boolean a(String str) {
        return a(str, false);
    }

    public static boolean a(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        File file = new File(str);
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                return file.delete();
            }
            for (File path : listFiles) {
                a(path.getPath(), z);
            }
            if (!z) {
                return file.delete();
            }
        }
        return true;
    }
}
