package com.miui.cleanmaster;

import android.app.NotificationManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.miui.gamebooster.m.C0381l;
import com.miui.securitycenter.h;
import miui.provider.ExtraSettings;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f3738a;

    private static class a extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private Context f3739a;

        public a(Context context) {
            super((Handler) null);
            this.f3739a = context;
        }

        private void a() {
            boolean g = h.g(this.f3739a);
            b.e(this.f3739a, "general_show_cnt");
            if (!g) {
                b.c(this.f3739a, "key_time_garbage_cleanup");
            } else {
                m.a(this.f3739a, false);
            }
        }

        public void onChange(boolean z) {
            a();
        }

        public void onChange(boolean z, Uri uri) {
            a();
        }
    }

    /* renamed from: com.miui.cleanmaster.b$b  reason: collision with other inner class name */
    private static class C0044b extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private Context f3740a;

        public C0044b(Context context) {
            super((Handler) null);
            this.f3740a = context;
        }

        private void a() {
            boolean l = h.l(this.f3740a);
            b.e(this.f3740a, "wechat_show_cnt");
            if (!l) {
                b.c(this.f3740a, "key_wechat_time_garbage_cleanup");
            } else {
                m.b(this.f3740a, false);
            }
        }

        public void onChange(boolean z) {
            a();
        }

        public void onChange(boolean z, Uri uri) {
            a();
        }
    }

    private static class c extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private Context f3741a;

        public c(Context context) {
            super((Handler) null);
            this.f3741a = context;
        }

        private void a() {
            boolean m = h.m(this.f3741a);
            b.e(this.f3741a, "whatsapp_show_cnt");
            if (!m) {
                b.c(this.f3741a, "key_whatsapp_time_garbage_cleanup");
            } else {
                m.c(this.f3741a, false);
            }
        }

        public void onChange(boolean z) {
            a();
        }

        public void onChange(boolean z, Uri uri) {
            a();
        }
    }

    private b() {
    }

    public static b a() {
        if (f3738a == null) {
            f3738a = new b();
        }
        return f3738a;
    }

    private void b(Context context) {
        context.getContentResolver().registerContentObserver(d(context, "key_notificaiton_general_clean_need"), true, new a(context));
    }

    private void c(Context context) {
        context.getContentResolver().registerContentObserver(d(context, "key_notification_wechat_size_need"), true, new C0044b(context));
    }

    /* access modifiers changed from: private */
    public static void c(Context context, String str) {
        ((NotificationManager) context.getSystemService("notification")).cancel(m.a(context).a(str));
    }

    private Uri d(Context context, String str) {
        return C0381l.a(context) ? ExtraSettings.Secure.getUriFor(str) : ExtraSettings.System.getUriFor(str);
    }

    private void d(Context context) {
        context.getContentResolver().registerContentObserver(d(context, "key_notification_whatsapp_clean_need"), true, new c(context));
    }

    /* access modifiers changed from: private */
    public static void e(Context context, String str) {
        d.a(context).a(str, 0);
    }

    public void a(Context context) {
        c(context);
        b(context);
        d(context);
    }
}
