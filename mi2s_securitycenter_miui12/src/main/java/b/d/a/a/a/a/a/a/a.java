package b.d.a.a.a.a.a.a;

import b.d.a.a.a.c;
import b.d.a.a.d.a.d;
import com.google.android.exoplayer2.C;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class a implements b.d.a.a.a.a.a {

    /* renamed from: a  reason: collision with root package name */
    static Map<String, Integer> f2081a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    static d f2082b = new d();

    /* renamed from: c  reason: collision with root package name */
    static int f2083c;

    static {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(c.f2090c + "classify/word2id.txt"), C.UTF8_NAME));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    String[] split = readLine.split("\t");
                    if (split.length == 2) {
                        f2081a.put(split[0], Integer.valueOf(Integer.parseInt(split[1])));
                    } else {
                        PrintStream printStream = System.out;
                        printStream.println("ERROR:" + readLine);
                    }
                } else {
                    f2083c = f2081a.size() + 1;
                    bufferedReader.close();
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int a() {
        return f2083c;
    }
}
