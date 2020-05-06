package b.d.e.c;

import b.d.e.b;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class j extends f {

    /* renamed from: b  reason: collision with root package name */
    private static String f2209b = "((https?|ftp|file)://)?(?<![@|[A-Za-z0-9_]])([[A-Za-z0-9_]-_]+[.])+([a-zA-Z]+)(:[1-9]\\d*)?([/][[A-Za-z0-9_]+&#%?=.~_|!]*)*";

    /* renamed from: c  reason: collision with root package name */
    private static String f2210c = "(((http(s?)|ftp|file):)?//)?((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})(:[1-9]\\d*)?([/][[A-Za-z0-9_]+&#%?=.~_|!]*)*";

    /* renamed from: d  reason: collision with root package name */
    private Pattern f2211d = Pattern.compile(f2209b, 2);
    private Pattern e = Pattern.compile(f2210c, 2);
    private List<String> f = new ArrayList();

    /* access modifiers changed from: protected */
    public List<String> a(String str) {
        Matcher matcher = this.f2211d.matcher(str);
        ArrayList<String> arrayList = new ArrayList<>();
        int i = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > i) {
                arrayList.add(str.substring(i, start));
            }
            this.f.add(str.substring(start, end));
            i = end;
        }
        if (i < str.length() - 1) {
            arrayList.add(str.substring(i));
        }
        ArrayList arrayList2 = new ArrayList();
        for (String str2 : arrayList) {
            Matcher matcher2 = this.e.matcher(str2);
            int i2 = 0;
            while (matcher2.find()) {
                int start2 = matcher2.start();
                int end2 = matcher2.end();
                if (start2 > i2) {
                    arrayList2.add(str2.substring(i2, start2));
                }
                this.f.add(str2.substring(start2, end2));
                i2 = end2;
            }
            if (i2 < str2.length() - 1) {
                arrayList2.add(str2.substring(i2));
            }
        }
        return arrayList2;
    }

    public boolean a(b bVar, int[] iArr, int i) {
        if (this.f.size() <= 0) {
            return false;
        }
        iArr[i] = iArr[i] + 1;
        bVar.g = new ArrayList();
        bVar.g.addAll(this.f);
        return true;
    }

    /* access modifiers changed from: protected */
    public void b() {
        super.b();
        this.f.clear();
    }
}
