package miui.io;

import android.system.Os;

public class FileStat {
    public static long getCreatedTime(String path) {
        try {
            return Os.lstat(path).st_ctime * 1000;
        } catch (Exception e) {
            return 0;
        }
    }
}
