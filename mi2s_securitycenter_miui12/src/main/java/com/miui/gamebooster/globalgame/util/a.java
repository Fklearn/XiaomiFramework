package com.miui.gamebooster.globalgame.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static a f4417a = new a();

    /* renamed from: b  reason: collision with root package name */
    private static Application f4418b;

    /* renamed from: c  reason: collision with root package name */
    private static SharedPreferences f4419c;

    public static Context a() {
        return f4418b.getApplicationContext();
    }

    public static void a(Application application, SharedPreferences sharedPreferences) {
        f4418b = application;
        f4419c = sharedPreferences;
    }

    public static a b() {
        return f4417a;
    }

    public static SharedPreferences c() {
        return f4419c;
    }
}
