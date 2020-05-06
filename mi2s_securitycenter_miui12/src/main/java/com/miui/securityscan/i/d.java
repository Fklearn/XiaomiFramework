package com.miui.securityscan.i;

import android.text.TextUtils;
import java.io.File;

public class d {
    public static void a(File file) {
        if (file != null) {
            try {
                if (file.exists() && file.isDirectory()) {
                    for (File file2 : file.listFiles()) {
                        String name = file2.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith("_temp.png")) {
                            file2.delete();
                        }
                    }
                }
            } catch (Exception unused) {
            }
        }
    }
}
