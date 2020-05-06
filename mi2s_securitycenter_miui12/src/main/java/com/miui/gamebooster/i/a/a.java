package com.miui.gamebooster.i.a;

import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.globalgame.util.b;
import java.util.Map;

public class a {
    static void a(String str, String str2, Map<String, String> map) {
        if (b.a()) {
            StringBuilder sb = new StringBuilder(str);
            sb.append("[");
            sb.append(str2);
            sb.append("]");
            sb.append("{");
            for (Map.Entry next : map.entrySet()) {
                sb.append((String) next.getKey());
                sb.append(":");
                sb.append((String) next.getValue());
                sb.append(",");
            }
            sb.append("}");
            b.a((Object) sb);
        }
    }

    static void a(Map<String, String> map) {
        map.put("isFirst", String.valueOf(Utils.n() ? 1 : 0));
        map.put("isNew", String.valueOf(Utils.o() ? 1 : 0));
    }
}
