package com.xiaomi.analytics.a.a;

import java.io.File;

public class f {
    public static void a(File file) {
        try {
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    if (file2.isDirectory()) {
                        b(file2.getAbsolutePath());
                    } else {
                        b(file2);
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    public static void a(String str) {
        a(new File(str));
    }

    public static void b(File file) {
        try {
            file.delete();
        } catch (Exception unused) {
        }
    }

    public static void b(String str) {
        try {
            File file = new File(str);
            a(str);
            file.delete();
        } catch (Exception unused) {
        }
    }
}
