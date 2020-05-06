package com.miui.gamebooster.m;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.Application;
import java.util.HashMap;

public class X {

    /* renamed from: a  reason: collision with root package name */
    private static HashMap<String, Integer> f4464a = new HashMap<>();

    /* renamed from: b  reason: collision with root package name */
    private static HashMap<String, Integer> f4465b = new HashMap<>();

    static {
        f4464a.put("pine", 27);
        f4464a.put("ginkgo", 95);
        f4464a.put("raphael", 84);
        f4464a.put("raphaelin", 84);
        f4464a.put("tucana", 102);
        f4464a.put("phoenix", 94);
        f4464a.put("phoenixin", 94);
        f4464a.put("umi", 110);
        f4464a.put("cmi", 110);
        f4464a.put("vince", 52);
        f4464a.put("rosy", 22);
        f4464a.put("whyred", 42);
        f4464a.put("ysl", 18);
        f4464a.put("wayne", 34);
        f4464a.put("chiron", 50);
        f4464a.put("polaris", 50);
        f4464a.put("beryllium", 104);
        f4464a.put("sirius", 54);
        f4464a.put("dipper", 96);
        f4464a.put("ursa", 96);
        f4464a.put("equuleus", 96);
        f4464a.put("nitrogen", 43);
        f4464a.put("sakura", 102);
        f4464a.put("clover", 33);
        f4464a.put("cactus", 33);
        f4464a.put("cereus", 33);
        f4464a.put("tulip", 96);
        f4464a.put("platina", 93);
        f4464a.put("andromeda", 86);
        f4464a.put("lotus", 96);
        f4464a.put("lavender", 106);
        f4464a.put("violet", 106);
        f4464a.put("cepheus", 96);
        f4464a.put("grus", 108);
        f4464a.put("onc", 68);
        f4464a.put("davinci", 84);
        f4464a.put("davinciin", 84);
        f4464a.put("laurus", 74);
        f4464a.put("pyxis", 96);
        f4464a.put("ginkgo", 95);
        f4464a.put("begonia", 95);
        f4464a.put("begoniain", 95);
        f4464a.put("olive", 70);
        f4464a.put("olivelite", 70);
        f4464a.put("tucana", 102);
        f4464a.put("picasso", 94);
        f4464a.put("picassoin", 94);
        f4464a.put("lmi", 103);
        f4464a.put("lmiin", 103);
        f4464a.put("lmipro", 103);
        f4464a.put("merlin", 106);
        f4464a.put("merlinin", 106);
        f4465b.put("pine", 27);
        f4465b.put("ginkgo", 90);
        f4465b.put("raphael", 84);
        f4465b.put("raphaelin", 84);
        f4465b.put("tucana", 102);
        f4465b.put("phoenix", 90);
        f4465b.put("phoenixin", 90);
        f4465b.put("umi", 110);
        f4465b.put("cmi", 110);
        f4465b.put("vince", 52);
        f4465b.put("rosy", 22);
        f4465b.put("whyred", 42);
        f4465b.put("ysl", 18);
        f4465b.put("wayne", 34);
        f4465b.put("chiron", 50);
        f4465b.put("polaris", 50);
        f4465b.put("beryllium", 104);
        f4465b.put("sirius", 50);
        f4465b.put("dipper", 80);
        f4465b.put("ursa", 80);
        f4465b.put("equuleus", 80);
        f4465b.put("nitrogen", 43);
        f4465b.put("sakura", 73);
        f4465b.put("clover", 33);
        f4465b.put("cactus", 33);
        f4465b.put("cereus", 33);
        f4465b.put("tulip", 86);
        f4465b.put("platina", 66);
        f4465b.put("andromeda", 86);
        f4465b.put("lotus", 74);
        f4465b.put("lavender", 108);
        f4465b.put("violet", 108);
        f4465b.put("cepheus", 106);
        f4465b.put("grus", 124);
        f4465b.put("onc", 51);
        f4465b.put("davinci", 84);
        f4465b.put("davinciin", 84);
        f4465b.put("laurus", 62);
        f4465b.put("pyxis", 106);
        f4465b.put("ginkgo", 90);
        f4465b.put("begonia", 90);
        f4465b.put("begoniain", 90);
        f4465b.put("olive", 70);
        f4465b.put("olivelite", 70);
        f4465b.put("tucana", 102);
        f4465b.put("picasso", 90);
        f4465b.put("picassoin", 90);
        f4465b.put("lmi", 103);
        f4465b.put("lmiin", 103);
        f4465b.put("lmipro", 103);
        f4465b.put("merlin", 103);
        f4465b.put("merlinin", 103);
    }

    public static int a(Context context) {
        Integer num = f4465b.get(Build.DEVICE);
        if (num != null) {
            return num.intValue();
        }
        int identifier = context.getResources().getIdentifier("rounded_corner_radius_bottom", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return -1;
    }

    public static boolean a() {
        for (Display state : ((DisplayManager) Application.d().getSystemService("display")).getDisplays()) {
            if (state.getState() == 2) {
                return true;
            }
        }
        return false;
    }

    public static int b(Context context) {
        Integer num = f4464a.get(Build.DEVICE);
        if (num != null) {
            return num.intValue();
        }
        int identifier = context.getResources().getIdentifier("rounded_corner_radius_top", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return -1;
    }

    public static synchronized boolean c(Context context) {
        boolean isKeyguardLocked;
        synchronized (X.class) {
            isKeyguardLocked = ((KeyguardManager) context.getSystemService("keyguard")).isKeyguardLocked();
        }
        return isKeyguardLocked;
    }
}
