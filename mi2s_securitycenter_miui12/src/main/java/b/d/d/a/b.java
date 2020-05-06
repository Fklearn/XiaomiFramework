package b.d.d.a;

import b.d.d.d.a;
import com.google.android.exoplayer2.C;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static Map<String, Double> f2154a = new HashMap();

    private static void a(InputStream inputStream) {
        try {
            f2154a.clear();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, C.UTF8_NAME));
            while (bufferedReader.ready()) {
                String[] split = bufferedReader.readLine().split("\t");
                f2154a.put(split[0], Double.valueOf(Double.parseDouble(split[1])));
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(String str) {
        try {
            a(a.a(str, "word2idf.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
