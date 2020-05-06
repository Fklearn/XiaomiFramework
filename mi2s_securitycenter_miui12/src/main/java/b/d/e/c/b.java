package b.d.e.c;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class b extends f {

    /* renamed from: b  reason: collision with root package name */
    private static String f2190b = "([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)";

    /* renamed from: c  reason: collision with root package name */
    private Pattern f2191c = Pattern.compile(f2190b);

    /* renamed from: d  reason: collision with root package name */
    private List<String> f2192d = new ArrayList();

    /* access modifiers changed from: protected */
    public List<String> a(String str) {
        Matcher matcher = this.f2191c.matcher(str);
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > i) {
                arrayList.add(str.substring(i, start));
            }
            this.f2192d.add(str.substring(start, end));
            i = end;
        }
        if (i < str.length() - 1) {
            arrayList.add(str.substring(i));
        }
        return arrayList;
    }

    public boolean a(b.d.e.b bVar, int[] iArr, int i) {
        if (this.f2192d.size() <= 0) {
            return false;
        }
        iArr[i] = iArr[i] + 1;
        return true;
    }

    /* access modifiers changed from: protected */
    public void b() {
        super.b();
        this.f2192d.clear();
    }
}
