package b.d.a.a.d.a;

import b.d.a.a.a.c;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class h implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static h f2133a;

    /* renamed from: b  reason: collision with root package name */
    private final Map<String, Double> f2134b = new HashMap(0, 0.9f);

    /* renamed from: c  reason: collision with root package name */
    private b f2135c;

    private h() {
        c();
    }

    public static h a() {
        h hVar = f2133a;
        if (hVar != null) {
            return hVar;
        }
        f2133a = new h();
        return f2133a;
    }

    private String c(String str) {
        this.f2135c.a(str.toCharArray());
        return str;
    }

    private void c() {
        this.f2135c = new b(0);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(c.f2090c + "dict_word.txt"));
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(c.f2090c + "dict_freq.txt"));
            String readLine = bufferedReader.readLine();
            while (true) {
                String readLine2 = bufferedReader2.readLine();
                if (readLine != null) {
                    c(readLine);
                    this.f2134b.put(readLine, Double.valueOf(((double) Integer.parseInt(readLine2)) / 4.566588E7d));
                    readLine = bufferedReader.readLine();
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a(String str) {
        return this.f2134b.containsKey(str);
    }

    /* access modifiers changed from: package-private */
    public b b() {
        return this.f2135c;
    }

    /* access modifiers changed from: package-private */
    public Double b(String str) {
        return a(str) ? this.f2134b.get(str) : Double.valueOf(-16.943714788138596d);
    }
}
