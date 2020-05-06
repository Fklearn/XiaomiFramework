package com.miui.optimizecenter.storage;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static o f5765a;

    public interface a {
        void a();

        void a(long j, long j2);

        void a(String str, long j, long j2);

        void b();
    }

    private o() {
    }

    private long a(List<String> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        LinkedList linkedList = new LinkedList();
        linkedList.addAll(list);
        long j = 0;
        while (!linkedList.isEmpty()) {
            String str = (String) linkedList.remove();
            if (!TextUtils.isEmpty(str) && !str.endsWith(".nomedia")) {
                File file = new File(str);
                if (!file.isFile() || file.length() <= 0) {
                    File[] listFiles = file.listFiles();
                    if (listFiles != null) {
                        for (File file2 : listFiles) {
                            if (!file2.isFile() || file2.length() <= 0) {
                                linkedList.add(file2.getAbsolutePath());
                            } else {
                                j += file2.length();
                            }
                        }
                    }
                } else {
                    j += file.length();
                }
            }
        }
        return j;
    }

    public static o a() {
        if (f5765a == null) {
            f5765a = new o();
        }
        return f5765a;
    }

    public HashMap<String, List<String>> a(Context context, List<String> list) {
        j b2 = j.b();
        b2.c();
        HashMap<String, List<String>> hashMap = new HashMap<>();
        for (String next : list) {
            List<String> a2 = b2.a(next);
            if (!a2.isEmpty()) {
                hashMap.put(next, a2);
            }
        }
        b2.a();
        return hashMap;
    }

    public void a(HashMap<String, List<String>> hashMap, a aVar) {
        a aVar2 = aVar;
        if (aVar2 != null) {
            aVar.a();
        }
        if ((hashMap == null || hashMap.isEmpty()) && aVar2 != null) {
            aVar.b();
            return;
        }
        long j = 0;
        long j2 = 0;
        for (Map.Entry next : hashMap.entrySet()) {
            String str = (String) next.getKey();
            long currentTimeMillis = System.currentTimeMillis();
            long a2 = a((List) next.getValue());
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            if (aVar2 != null) {
                aVar.a(str, a2, currentTimeMillis2);
            }
            j += a2;
            j2 += currentTimeMillis2;
        }
        if (aVar2 != null) {
            aVar2.a(j, j2);
            aVar.b();
        }
    }
}
