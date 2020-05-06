package com.miui.optimizemanage.settings;

import com.miui.activityutil.o;
import com.miui.powercenter.utils.s;
import java.io.PrintWriter;

public class d {
    public static void a(PrintWriter printWriter) {
        printWriter.println("=== OptimizeManage Settings ===");
        long b2 = c.b();
        StringBuilder sb = new StringBuilder();
        sb.append("Last clean time: ");
        int i = (b2 > 0 ? 1 : (b2 == 0 ? 0 : -1));
        String str = o.f2309a;
        sb.append(i == 0 ? str : s.b(b2));
        printWriter.println(sb.toString());
        long d2 = c.d();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Last show media scan timeout time: ");
        sb2.append(d2 == 0 ? str : s.b(d2));
        printWriter.println(sb2.toString());
        long e = c.e();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Last show memory occupy time: ");
        if (e != 0) {
            str = s.b(e);
        }
        sb3.append(str);
        printWriter.println(sb3.toString());
        boolean i2 = c.i();
        printWriter.println("Show cpu overload notify: " + i2);
        int g = c.g();
        printWriter.println("Memory occupy percent: " + g);
        boolean h = c.h();
        printWriter.println("Usage tips shown: " + h);
        int f = c.f();
        printWriter.println("Lock app count: " + f);
        printWriter.println(TtmlNode.END);
        printWriter.println();
    }
}
