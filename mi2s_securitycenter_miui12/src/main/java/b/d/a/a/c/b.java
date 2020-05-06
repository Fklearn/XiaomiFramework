package b.d.a.a.c;

import b.d.a.a.a.a;
import b.d.a.a.a.c;
import com.miui.activityutil.o;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static Map<String, List<a>> f2105a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private static boolean f2106b = false;

    public static a a(String str, String str2) {
        if (!f2106b) {
            b();
        }
        List<a> list = f2105a.get(str);
        if (!(list == null || list.size() == 0)) {
            for (a aVar : list) {
                if (aVar.a(str2)) {
                    return aVar.f2104b;
                }
            }
        }
        List<a> list2 = f2105a.get(o.f2309a);
        if (!(list2 == null || list2.size() == 0)) {
            for (a aVar2 : list2) {
                if (aVar2.a(str2)) {
                    return aVar2.f2104b;
                }
            }
        }
        return a.UNKNOWN;
    }

    public static boolean a() {
        f2105a = new HashMap();
        f2106b = false;
        return true;
    }

    public static void b() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(c.f2090c + "template/address.txt")));
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(c.f2090c + "template/body_regex.txt")));
            BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(new FileInputStream(c.f2090c + "template/category.txt")));
            String readLine = bufferedReader.readLine();
            String readLine2 = bufferedReader2.readLine();
            while (true) {
                String readLine3 = bufferedReader3.readLine();
                if (readLine == null) {
                    break;
                }
                if (f2105a.containsKey(readLine)) {
                    f2105a.get(readLine).add(new a(Pattern.compile(readLine2), a.valueOf(readLine3)));
                } else {
                    LinkedList linkedList = new LinkedList();
                    linkedList.add(new a(Pattern.compile(readLine2), a.valueOf(readLine3)));
                    f2105a.put(readLine, linkedList);
                }
                readLine = bufferedReader.readLine();
                readLine2 = bufferedReader2.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        f2106b = true;
    }
}
