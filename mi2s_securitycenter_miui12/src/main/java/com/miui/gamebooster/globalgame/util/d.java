package com.miui.gamebooster.globalgame.util;

import com.google.gson.Gson;
import java.util.Map;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static Gson f4420a = new Gson();

    public static <T> T a(String str, Class<T> cls) {
        try {
            return f4420a.fromJson(str, cls);
        } catch (Exception e) {
            e.printStackTrace();
            b.b(e);
            return null;
        }
    }

    public static String a(Object obj) {
        try {
            return f4420a.toJson(obj);
        } catch (Exception e) {
            b.b(e);
            return null;
        }
    }

    public static Map<String, String> a(String str) {
        try {
            if (f4420a != null) {
                return (Map) f4420a.fromJson(str, new c().getType());
            }
            return null;
        } catch (Exception e) {
            b.b(e);
            return null;
        }
    }
}
