package b.b.j.d;

import android.text.TextUtils;
import com.miui.common.persistence.b;
import java.util.ArrayList;
import java.util.List;

public class a {
    public static ArrayList<String> a() {
        String a2 = b.a("pm_recent_use_actions", "");
        ArrayList<String> arrayList = new ArrayList<>();
        if (a2.contains(",")) {
            String[] split = a2.split(",");
            for (String add : split) {
                arrayList.add(add);
            }
        } else if (!TextUtils.isEmpty(a2)) {
            arrayList.add(a2);
        }
        return arrayList;
    }

    public static void a(String str) {
        ArrayList<String> a2 = a();
        if (a2.contains(str)) {
            a2.remove(a2.indexOf(str));
            a2.add(0, str);
        } else {
            a2.add(0, str);
            if (a2.size() > 4) {
                a2.remove(a2.size() - 1);
            }
        }
        a((List<String>) a2);
    }

    public static void a(List<String> list) {
        if (list.isEmpty()) {
            b.b("pm_recent_use_actions", "");
            return;
        }
        String str = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            str = str.concat(",").concat(list.get(i));
        }
        b.b("pm_recent_use_actions", str);
    }
}
