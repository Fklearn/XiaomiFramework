package b.d.e;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2186a = "b.d.e.c";

    /* renamed from: b  reason: collision with root package name */
    public static final String[] f2187b = {"dict", "model", "pattern", "phish"};

    /* renamed from: c  reason: collision with root package name */
    private static volatile boolean f2188c = false;

    public static InputStream a(Context context, String str) {
        try {
            String[] fileList = context.fileList();
            int length = fileList.length;
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                } else if (fileList[i].equals(str)) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            return (!z || f2188c) ? context.getAssets().open(str) : context.openFileInput(str);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(f2186a, "load Model file " + str + " failed.");
            return null;
        }
    }

    protected static String a(Context context) {
        try {
            InputStream a2 = a(context, "pattern");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(a2));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    bufferedReader.close();
                    a2.close();
                    return sb.toString();
                }
                sb.append(String.valueOf(readLine) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void a(boolean z) {
        f2188c = z;
    }
}
