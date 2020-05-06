package b.b.c.g;

import android.util.Pair;

public class c extends Pair<String, String> {
    public c(String str, String str2) {
        super(str, str2);
        if (str == null) {
            throw new IllegalArgumentException("key may not be null");
        } else if (str2 == null) {
            throw new IllegalArgumentException("value may not be null");
        }
    }

    public String a() {
        return (String) this.first;
    }

    public String b() {
        return (String) this.second;
    }
}
