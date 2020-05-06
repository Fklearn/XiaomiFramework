package b.d.d.d;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class a {
    public static InputStream a(String str, String str2) {
        try {
            File file = new File(str + str2);
            return file.exists() ? new FileInputStream(file) : a.class.getClassLoader().getResourceAsStream(str2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
