package b.b.b.d;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import miui.util.IOUtils;
import miui.util.Log;

public class i {
    public static String a(String str) {
        String str2 = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            try {
                str2 = ExtraTextUtils.toHexReadable(DigestUtils.get(fileInputStream, "MD5"));
            } catch (IOException e) {
                Log.e("MD5", e.toString());
            } catch (Throwable th) {
                IOUtils.closeQuietly(fileInputStream);
                throw th;
            }
            IOUtils.closeQuietly(fileInputStream);
            return str2;
        } catch (FileNotFoundException e2) {
            Log.e("MD5", e2.toString());
            return null;
        }
    }
}
