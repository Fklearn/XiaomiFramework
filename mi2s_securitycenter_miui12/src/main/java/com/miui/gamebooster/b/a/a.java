package com.miui.gamebooster.b.a;

import android.content.Context;
import android.util.Log;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.securitycenter.Application;
import miui.slide.ISlideChangeListener;
import miui.slide.ISlideManagerService;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static ISlideChangeListener f4088a;

    /* renamed from: com.miui.gamebooster.b.a.a$a  reason: collision with other inner class name */
    public interface C0046a {
        void onSlideChanged(int i);
    }

    private static class b extends ISlideChangeListener.Stub {

        /* renamed from: a  reason: collision with root package name */
        private C0046a f4089a;

        public b(C0046a aVar) {
            this.f4089a = aVar;
        }

        public void onSlideChanged(int i) {
            C0046a aVar = this.f4089a;
            if (aVar != null) {
                aVar.onSlideChanged(i);
            }
        }
    }

    public static void a() {
        if (C0388t.o()) {
            try {
                ISlideManagerService.Stub.asInterface(b.b.o.a.b.a((String) e.a(Class.forName("miui.slide.SlideManagerService"), "SERVICE_NAME"))).unregisterSlideChangeListener(f4088a);
            } catch (Exception e) {
                Log.i("GameBoosterReflectUtils", e.toString());
            }
        }
    }

    public static void a(C0046a aVar, com.miui.gamebooster.d.b bVar) {
        if (aVar != null && b(bVar)) {
            f4088a = new b(aVar);
            try {
                ISlideManagerService.Stub.asInterface(b.b.o.a.b.a((String) e.a(Class.forName("miui.slide.SlideManagerService"), "SERVICE_NAME"))).registerSlideChangeListener("gamebooster", f4088a);
            } catch (Exception e) {
                Log.i("GameBoosterReflectUtils", e.toString());
            }
        }
    }

    private static boolean a(Context context, com.miui.gamebooster.d.b bVar) {
        return (C0384o.a(context.getContentResolver(), "gb_videobox", 1, -2) == 1) && com.miui.gamebooster.d.b.VIDEO_ALL.equals(bVar);
    }

    private static boolean a(com.miui.gamebooster.d.b bVar) {
        return com.miui.gamebooster.c.a.w(true) && com.miui.gamebooster.d.b.GAME.equals(bVar);
    }

    private static boolean b(com.miui.gamebooster.d.b bVar) {
        if (!C0388t.o()) {
            return false;
        }
        return a((Context) Application.d(), bVar) || a(bVar);
    }
}
