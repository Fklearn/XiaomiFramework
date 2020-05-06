package b.d.d.c;

import b.d.d.d.a;
import b.d.d.e.c;
import com.google.android.exoplayer2.C;
import com.miui.activityutil.o;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class b {

    /* renamed from: a  reason: collision with root package name */
    double f2155a;

    /* renamed from: b  reason: collision with root package name */
    double[] f2156b;

    /* renamed from: c  reason: collision with root package name */
    c[] f2157c;

    /* renamed from: d  reason: collision with root package name */
    double f2158d;

    public b(String str) {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(a.a(str, "key_val.txt"), C.UTF8_NAME));
        this.f2158d = Double.parseDouble(properties.getProperty("intercept", o.f2309a));
        this.f2155a = Double.parseDouble(properties.getProperty("gamma", o.f2309a));
        int parseInt = Integer.parseInt(properties.getProperty("supportVectorSize", o.f2309a));
        this.f2156b = new double[parseInt];
        a(str);
        this.f2157c = new c[parseInt];
        b(str);
    }

    private void a(String str) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(a.a(str, "alpha.txt"), C.UTF8_NAME));
        int i = 0;
        while (bufferedReader.ready()) {
            this.f2156b[i] = Double.parseDouble(bufferedReader.readLine());
            i++;
        }
        bufferedReader.close();
    }

    private void b(String str) {
        int i;
        String str2;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(a.a(str, "weight.txt"), C.UTF8_NAME));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i2 = 1;
        while (true) {
            i = 0;
            if (!bufferedReader.ready()) {
                break;
            }
            String[] split = bufferedReader.readLine().split(" ");
            if (Integer.parseInt(split[0]) == i2) {
                arrayList.add(Integer.valueOf(Integer.parseInt(split[1]) - 1));
                str2 = split[2];
            } else {
                int[] iArr = new int[arrayList.size()];
                double[] dArr = new double[arrayList2.size()];
                while (i < arrayList.size()) {
                    iArr[i] = ((Integer) arrayList.get(i)).intValue();
                    dArr[i] = ((Double) arrayList2.get(i)).doubleValue();
                    i++;
                }
                this.f2157c[i2 - 1] = new b.d.d.e.b(5822, iArr, dArr);
                i2++;
                arrayList.clear();
                arrayList2.clear();
                arrayList.add(Integer.valueOf(Integer.parseInt(split[1]) - 1));
                str2 = split[2];
            }
            arrayList2.add(Double.valueOf(Double.parseDouble(str2)));
        }
        int[] iArr2 = new int[arrayList.size()];
        double[] dArr2 = new double[arrayList2.size()];
        while (i < arrayList.size()) {
            iArr2[i] = ((Integer) arrayList.get(i)).intValue();
            dArr2[i] = ((Double) arrayList2.get(i)).doubleValue();
            i++;
        }
        this.f2157c[i2 - 1] = new b.d.d.e.b(5822, iArr2, dArr2);
        bufferedReader.close();
    }

    public double a(c cVar) {
        double d2 = this.f2158d;
        int i = 0;
        while (true) {
            c[] cVarArr = this.f2157c;
            if (i >= cVarArr.length) {
                return d2;
            }
            d2 += this.f2156b[i] * a.a(this.f2155a, cVar, cVarArr[i]);
            i++;
        }
    }
}
