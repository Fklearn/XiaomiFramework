package b.d.d.a;

import com.google.android.exoplayer2.C;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static Map<String, Integer> f2153a = new HashMap();

    private static void a(InputStream inputStream) {
        try {
            f2153a.clear();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, C.UTF8_NAME));
            while (bufferedReader.ready()) {
                String[] split = bufferedReader.readLine().split("\t");
                f2153a.put(split[0], Integer.valueOf(Integer.parseInt(split[1])));
            }
            bufferedReader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void a(String str) {
        try {
            a(b.d.d.d.a.a(str, "word2id.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
